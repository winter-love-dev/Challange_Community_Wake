package org.tensorflow.lite.examples.detection.wake.FragmentHome;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.Import.KakaoWebViewClient;

import java.util.HashMap;
import java.util.Map;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Activity_Project_Detail_Page.GET_PROJECT_CERTI_COUNT;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Activity_Project_Detail_Page.GET_PROJECT_PRICE;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search.GET_PROJECT_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search.getTotalSum;

public class Activity_Project_Join extends AppCompatActivity
{
    private String TAG = "Activity_Project_Join";

    // 카카오 웹뷰
    private WebView join_webview;

    private TextView
            confirm_message, join_confirm_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_join);

        // View Find
        confirm_message = findViewById(R.id.confirm_message);
//        join_confirm_button = findViewById(R.id.join_confirm_button);
        join_webview = findViewById(R.id.join_webview);

        // 마이페이지로 이동헤서 이용현황 확인하기
//        join_confirm_button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Activity_Home.host.setCurrentTab(4);
//            }
//        });

        join_webview.setWebViewClient(new KakaoWebViewClient(Activity_Project_Join.this)
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                Log.e(TAG, "onPageStarted");
                Toast.makeText(Activity_Project_Join.this, "결제 화면 불러오는 중", Toast.LENGTH_SHORT).show();
                super.onPageStarted(view, url, favicon);
            }

            // 웹뷰가 종료되면(결제 완료되면) 서버로 값 전송
            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.e(TAG, "onPageFinished");

                Toast.makeText(Activity_Project_Join.this, "카카오페이 로드 완료", Toast.LENGTH_SHORT).show();

                // 서버로 값 전송
                doJoin();

                super.onPageFinished(view, url);
            }
        });

        WebSettings settings = join_webview.getSettings();
        settings.setJavaScriptEnabled(true);

        // 카카오페이 실행하기
        join_webview.loadUrl("http://115.68.231.84/addJoin_kakao.php?amount=" + GET_PROJECT_PRICE +"0000"); // 카카오페이로 입력값 보내기

        Log.e(TAG, "onCreate: GET_PROJECT_PRICE: " + GET_PROJECT_PRICE );
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // 액티비티가 멈출 때 결제완료 메시지, 완료버튼 활성화
        confirm_message.setVisibility(View.VISIBLE);
//        join_confirm_button.setVisibility(View.VISIBLE);

        // 액티비티 종료하기
        Activity_Project_Detail_Page activity_project_detail_page = (Activity_Project_Detail_Page)Activity_Project_Detail_Page.Activity_Project_Detail_Page;
        activity_project_detail_page.finish();

        Log.e(TAG, "onStop: 이전 액티비티 종료 완료" );
    }

    // 아임포트로 값 전달
    private void doJoin()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/addJoinInfo.php",
                new Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "onResponse: onResponse(String response): " + response);

                            // 전달받은 json에서 success를 받는다.
                            // {"success":"??"}
                            String success = jsonObject.getString("success");

                            if (success.equals("1"))
                            {
//                                Toast.makeText(Activity_Reservation_Web_View.this, "예약완료", Toast.LENGTH_SHORT).show();

                                // todo: 홈 화면의 통계 갱신하기 (누적 인원, 누적 금액)
                                getTotalSum();

                            } else
                            {
                                Toast.makeText(Activity_Project_Join.this, "예약: 에러발생. 로그 확인", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e)
                        {
                            Toast.makeText(Activity_Project_Join.this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_Project_Join.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: JSONException VolleyError error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                params.put("GET_PROJECT_INDEX", GET_PROJECT_INDEX);
                params.put("getId", getId);
                params.put("GET_PROJECT_PRICE", GET_PROJECT_PRICE);
                params.put("GET_PROJECT_CERTI_COUNT", GET_PROJECT_CERTI_COUNT);

                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
