package org.tensorflow.lite.examples.posenet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Activity_Posenet_Done_Certify extends AppCompatActivity
{
    private String TAG = "Activity_Posenet_Done_Certify";
    private Bitmap adjustedBitmap;
    private Bitmap bmp;
    private TextView
            certi_done_title            // 프로젝트 제목
            , certi_done_button         // 프로젝트 인증 버튼
            , certi_done_reward         // 보상받을 리워드
            , certi_done_exp            // 경험치
            , certi_done_progress_percent // 달성 증가율
            ;
    private String ResultProgressPercent //
            , ResultExp     //
            ;

    String getCertiContent;

    private String
            getID,                      //
            GET_JOIN_INDEX,             //
            GET_JOIN_MEMBER_INDEX,      //
            GET_PROOF_CATEGORY,         //
            GET_PROOF_CERTI_COUNT,      //
            GET_PROOF_INDEX,            //
            GET_PROOF_JOIN_PRICE,       //
            GET_PROOF_MY_CERTI_COUNT,   //
            GET_PROOF_TITLE,            //
            GET_PROOF_TYPE,             //
            GET_REWARD;                 //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__done__certify);

        // ViewFind
        certi_done_title = findViewById(R.id.certi_done_title);
        certi_done_button = findViewById(R.id.certi_done_button);
        ImageView img = (ImageView) findViewById(R.id.imageView1);

        certi_done_reward = findViewById(R.id.certi_done_reward);
        certi_done_exp = findViewById(R.id.certi_done_exp);
        certi_done_progress_percent = findViewById(R.id.certi_done_progress_percent);

        // 이전 액티비티에서 촬영한 사진이 저장된 경로 받기
        Intent intent = getIntent();

        String certiFrom = intent.getStringExtra("certiFrom");

        getID = intent.getStringExtra("getID");
        GET_JOIN_INDEX = intent.getStringExtra("GET_JOIN_INDEX");
        GET_JOIN_MEMBER_INDEX = intent.getStringExtra("GET_JOIN_MEMBER_INDEX");
        GET_PROOF_CATEGORY = intent.getStringExtra("GET_PROOF_CATEGORY");
        GET_PROOF_CERTI_COUNT = intent.getStringExtra("GET_PROOF_CERTI_COUNT");

        GET_PROOF_INDEX = intent.getStringExtra("GET_PROOF_INDEX");
        GET_PROOF_JOIN_PRICE = intent.getStringExtra("GET_PROOF_JOIN_PRICE");
        GET_PROOF_MY_CERTI_COUNT = intent.getStringExtra("GET_PROOF_MY_CERTI_COUNT");
        GET_PROOF_TITLE = intent.getStringExtra("GET_PROOF_TITLE");
        GET_PROOF_TYPE = intent.getStringExtra("GET_PROOF_TYPE");

        GET_REWARD = intent.getStringExtra("GET_REWARD");

        Log.e(TAG, "코틀린에서 값 전달받음 getId: " + getID);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_JOIN_INDEX: " + GET_JOIN_INDEX);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_JOIN_MEMBER_INDEX: " + GET_JOIN_MEMBER_INDEX);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_PROOF_CATEGORY: " + GET_PROOF_CATEGORY);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_PROOF_CERTI_COUNT: " + GET_PROOF_CERTI_COUNT);

        Log.e(TAG, "코틀린에서 값 전달받음 GET_PROOF_INDEX: " + GET_PROOF_INDEX);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_PROOF_JOIN_PRICE: " + GET_PROOF_JOIN_PRICE);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_PROOF_MY_CERTI_COUNT: " + GET_PROOF_MY_CERTI_COUNT);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_PROOF_TITLE: " + GET_PROOF_TITLE);
        Log.e(TAG, "코틀린에서 값 전달받음 GET_PROOF_TYPE: " + GET_PROOF_TYPE);

        Log.e(TAG, "코틀린에서 값 전달받음 GET_REWARD: " + GET_REWARD);


        // 프로젝트 제목
        certi_done_title.setText(GET_PROOF_TITLE);

        /** todo: 1. 1회 인증시 얻게될 보상금 = 참가비 / 인증 해야될 횟수 (소수점 제외)
         * */
        int joinPrice = Integer.parseInt(GET_PROOF_JOIN_PRICE + "0000");
        int Reword = joinPrice / Integer.parseInt(GET_PROOF_CERTI_COUNT);
        String ResultReword = String.valueOf(Reword);

        // 보상받을 금액 세팅
        certi_done_reward.setText(GET_REWARD + "원");

        /** todo: 2. 1회 인증시 얻을 경험치
         *
         * 참가비 만 원 당 얻을 수 있는 최대 경험치는 20점
         *
         * 만약_
         * 참가비: 만 원
         * 인증 해야될 횟수: 7회
         *
         * 1회 인증시 얻을 경험치 = 참가비 / 인증 해야될 횟수 (소수점 제외)
         * */
        int exp = Integer.parseInt(GET_PROOF_JOIN_PRICE + "0") / Integer.parseInt(GET_PROOF_CERTI_COUNT);
        ResultExp = String.valueOf(exp);
        certi_done_exp.setText(ResultExp + "점 (" + GET_PROOF_CATEGORY +")");

        /** todo: 3. 1회 인증시 프로젝트 달성 증가율 계산
         *
         * 만약_
         * 5회 참여 해야되는 프로젝트라면?
         * 1회 인증당 증가할 달성률은?
         * (1 / 5) * 100 = 20 %
         *
         * 달성 증가율 % = (1 / 인증 해야될 횟수) * 100
         * */

        float ProgressPercent = (float) 1 / (float)Integer.parseInt(GET_PROOF_CERTI_COUNT) * (float)100;
        Log.e(TAG, "onCreate: ProgressPercent: " + ProgressPercent );

        // 소수점 둘 째 자리까지만 표시하기 (ex_"%.3f" = 소수점 셋 째 자리까지 표시)

        ResultProgressPercent = String.format("%.2f", ProgressPercent);
        certi_done_progress_percent.setText(ResultProgressPercent + "%");


        if (certiFrom.equals("camera"))
        {
            String photoPath = intent.getStringExtra("strParamName");
            Log.e(TAG, "onCreate: photoPath: " + photoPath);

            // 사진이 저장된 경로 탐색 후 비트맵에 담기 (사진 압축하기)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            bmp = BitmapFactory.decodeFile(photoPath, options);

            Matrix matrix = new Matrix();
            matrix.preRotate(90);
            adjustedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

            // todo: 비트맵에 담긴 촬영 결과 세팅하기
            img.setImageBitmap(adjustedBitmap);

            getCertiContent = getStringImage(adjustedBitmap);
        } else if (certiFrom.equals("album"))
        {
            String photoPath = intent.getStringExtra("strParamName");
            Log.e(TAG, "onCreate: photoPath: " + photoPath);

            // 사진이 저장된 경로 탐색 후 비트맵에 담기 (사진 압축하기)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            bmp = BitmapFactory.decodeFile(photoPath, options);

            // todo: 비트맵에 담긴 촬영 결과 세팅하기
            img.setImageBitmap(bmp);

            getCertiContent = getStringImage(bmp);
        } else if (certiFrom.equals("Distance_Measurement"))
        {
            String photoPath = intent.getStringExtra("strParamName");
            Log.e(TAG, "onCreate: photoPath: " + photoPath);

            // 사진이 저장된 경로 탐색 후 비트맵에 담기 (사진 압축하기)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bmp = BitmapFactory.decodeFile(photoPath, options);

            // todo: 비트맵에 담긴 촬영 결과 세팅하기
            img.setImageBitmap(bmp);

            getCertiContent = getStringImage(bmp);
        }

        // 보상과 달성률 확인하기
        Log.e(TAG, "getParams: ResultProgressPercent: " + ResultProgressPercent);
        Log.e(TAG, "getParams: ResultExp: " + ResultExp);
        Log.e(TAG, "getParams: GET_REWARD: " + GET_REWARD );

//        Log.e(TAG, "onClick: getStringImage(Bitmap bitmap): " + getStringImage(adjustedBitmap) );

        // todo: 인증 완료버튼
        certi_done_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // todo: 서버로 인증결과 전송하기
                DoneCertify();
            }
        });
    }

    // todo: 서버로 인증결과 전송하기
    private void DoneCertify()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/addWakeCertify.php",
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
                                Toast.makeText(Activity_Posenet_Done_Certify.this, "인증 완료", Toast.LENGTH_SHORT).show();

                                finish();

                            } else
                            {
                                Toast.makeText(Activity_Posenet_Done_Certify.this, "인증: 에러발생. 로그 확인", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e)
                        {
                            Toast.makeText(Activity_Posenet_Done_Certify.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Activity_Posenet_Done_Certify.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: JSONException VolleyError error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("getId", getID);
                params.put("GET_PROOF_INDEX", GET_PROOF_INDEX);
                params.put("GET_PROOF_File", getCertiContent);
                params.put("GET_PROOF_TYPE", GET_PROOF_TYPE);
                params.put("ResultProgressPercent", ResultProgressPercent);
                params.put("ResultExp", ResultExp);
                params.put("GET_REWARD", GET_REWARD);
                params.put("GET_PROOF_CATEGORY", GET_PROOF_CATEGORY);
                params.put("GET_JOIN_INDEX", GET_JOIN_INDEX);
                params.put("GET_JOIN_MEMBER_INDEX", GET_JOIN_MEMBER_INDEX);
                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // 비트맵을 문자열로 변환하는 메소드
    public String getStringImage(Bitmap bitmap)
    {
        // 바이트 배열 사용.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 비트맵을 변환한다. 원래 100%였던 것을 50%의 품질로. 그리고 바이트 배열화 시킨다.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();

        // 베이스 64? 뭔 소린지 모르겠다.
        // 위키를 참고했다. (https://ko.wikipedia.org/wiki/베이스64)
        // 64진법이라고 한다. 64진법으로 인코딩 하는건가?
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        // 인코딩 결과를 반환한다.
        return encodedImage;
    }
}

