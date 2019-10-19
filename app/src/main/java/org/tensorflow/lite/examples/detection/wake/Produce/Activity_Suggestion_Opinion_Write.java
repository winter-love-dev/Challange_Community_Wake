package org.tensorflow.lite.examples.detection.wake.Produce;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.util.HashMap;
import java.util.Map;

import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_Suggestion_Detail.List_Opinion;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_Suggestion_Detail.item_opinion;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.SuggestionDetailInfo;

public class Activity_Suggestion_Opinion_Write extends AppCompatActivity
{
    private String TAG = "Activity_Suggestion_Opinion_Write";

    // View 선언
    private EditText opinion_Write;      // 인증방법 제안 글쓰기
    private TextView opinion_Write_Done; // 인증방법 제안 글쓰기 완료

    private String Opinion_Write;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_opinion_write);

        // todo: 화면 상단의 타이틀바
        Toolbar toolbar = findViewById(R.id.produce_Suggestion_Opinion_Write_Titlebar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰 색으로 지정하기
        setSupportActionBar(toolbar);
        // todo: 타이틀바 끝

        // View Find
        opinion_Write = findViewById(R.id.opinion_Write);
        opinion_Write_Done = findViewById(R.id.opinion_Write_Done);

        // 글 작성 완료 버튼 클릭
        opinion_Write_Done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 1. 작성한 글 내용 받아서 서버로 업로드 하기.
                // 2. 작성한 글 내용 받아서 이전 페이지의 댓글창에 추가하기
                Opinion_Write = opinion_Write.getText().toString();
                Log.e(TAG, "onClick: Opinion_Write: " + Opinion_Write);

                if (Opinion_Write.equals(null))
                {
                    Toast.makeText(Activity_Suggestion_Opinion_Write.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                else if (Opinion_Write == null)
                {
                    Toast.makeText(Activity_Suggestion_Opinion_Write.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                else if (Opinion_Write.length() == 0)
                {
                    Toast.makeText(Activity_Suggestion_Opinion_Write.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    // 이전 페이지 목록(리사이클러뷰)에 추가하기
                    item_opinion = new Item_Opinion("null", "null", Opinion_Write);
                    List_Opinion.add(item_opinion);

                    // 서버로 전송하기
                    addOpinion();
                }
            }
        });
    }

    private void addOpinion()
    {
        // 입력한 정보를 php POST로 DB에 전송합니다.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/addOpinion.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e(TAG, "onResponse: response = " + response);

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            String success = jsonObject.getString("success");

                            if (success.equals("1"))
                            {
                                Toast.makeText(Activity_Suggestion_Opinion_Write.this, "작성 완료", Toast.LENGTH_SHORT).show();

                                // 작성 완료하면 액티비티 종료
                                finish();
                            } else
                            {
                                Toast.makeText(Activity_Suggestion_Opinion_Write.this, "문제발생.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: response: " + response);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(Activity_Suggestion_Opinion_Write.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_Suggestion_Opinion_Write.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: error: " + error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                params.put("Wake_SO_AS_No", SuggestionDetailInfo);
                params.put("Opinion_Write", Opinion_Write);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
    }

    // 맨 위 툴바 뒤로가기 눌렀을 때 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
            {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed: finish()");
        finish();
    }
}
