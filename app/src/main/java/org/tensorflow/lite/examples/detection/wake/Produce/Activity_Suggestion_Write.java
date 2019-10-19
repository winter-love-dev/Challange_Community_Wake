package org.tensorflow.lite.examples.detection.wake.Produce;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.getSuggestionList;


/**
 * 프로그램 주제 제안 글쓰기 화면
 * */
public class Activity_Suggestion_Write extends AppCompatActivity
{

    private String TAG = "Activity_Suggestion_Write";

    private EditText Suggestion_Write_Title     // 제안할 주제
            ,        Suggestion_Write_Opinion    // 인증방법 제안
            ;

    private TextView Suggestion_Write_Done      // 주제 제안 작성완료
            ;

    // 입력한 주제, 인증방법 담기
    private String getSuggestion_Write_Title
            ,      getSuggestion_Write_Opinion
            ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__suggestion__write);

        // todo: 화면 상단의 타이틀바
        Toolbar toolbar = findViewById(R.id.produce_Suggestion_Write_Toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰 색으로 지정하기
        setSupportActionBar(toolbar);
        // todo: 타이틀바 끝

        // ViewFind
        Suggestion_Write_Title = findViewById(R.id.Suggestion_Write_Title);
        Suggestion_Write_Opinion = findViewById(R.id.Suggestion_Write_Opinion);
        Suggestion_Write_Done = findViewById(R.id.Suggestion_Write_Done);

        // todo: 주제 제안 작성완료 알림 다이얼로그
        Suggestion_Write_Done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // todo: 입력한 내용 받기
                getSuggestion_Write_Title   = Suggestion_Write_Title.getText().toString();
                getSuggestion_Write_Opinion = Suggestion_Write_Opinion.getText().toString();

                Log.e(TAG, "onClick: 서버로 전송할 제안" );
                Log.e(TAG, "onClick: getSuggestion_Write_Title: " + getSuggestion_Write_Title );
                Log.e(TAG, "onClick: getSuggestion_Write_Opinion: " + getSuggestion_Write_Opinion );

                // 빈 칸 체크...!!
//                if (TextUtils.isEmpty(getSuggestion_Write_Title))
//                {
                Suggestion_Write_Done();
//                }


            }
        });
    }

    // todo: 주제 제안 작성완료 알림 다이얼로그
    private void Suggestion_Write_Done()
    {
        // 다이얼로그 세팅
        AlertDialog.Builder startBroadcastDialog = new AlertDialog.Builder(Activity_Suggestion_Write.this);
        startBroadcastDialog.setTitle("주제를 제안하시겠습니다?");
//        startBroadcastDialog.setMessage("방송을 시작합니다");
        startBroadcastDialog
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener()
                        {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // todo: 서버에 주제 제안 정보 저장하기
                                addSuggestion();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                            }
                        });

        final AlertDialog edit_dialog = startBroadcastDialog.create();

        edit_dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
        {
            @Override
            public void onShow(DialogInterface arg0)
            {
                edit_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        edit_dialog.show(); // 다이얼로그 실행
        // 다이얼로그 끝
    }

    // todo: 서버에 주제 제안 정보 저장하기
    private void addSuggestion()
    {
        // 입력한 정보를 php POST로 DB에 전송합니다.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/addSuggestion.php",
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
                                Toast.makeText(Activity_Suggestion_Write.this, "작성 완료", Toast.LENGTH_SHORT).show();

                                // 글 목록 새로고침
                                getSuggestionList();

                                // 작성 완료하면 액티비티 종료
                                finish();
                            }

                            else
                            {
                                Toast.makeText(Activity_Suggestion_Write.this, "문제발생.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: response: " + response);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(Activity_Suggestion_Write.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString() );
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_Suggestion_Write.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: error: " + error );
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("Suggestion_Write_Title", getSuggestion_Write_Title);
                params.put("Suggestion_Write_Opinion", getSuggestion_Write_Opinion);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
        finish();
    }
}
