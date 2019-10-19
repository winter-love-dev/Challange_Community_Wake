package org.tensorflow.lite.examples.detection.wake;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

public class Activity_Register extends AppCompatActivity
{
    // 회원가입 정보입력
    private EditText name, email, password, password_confirm;

    // 가입버튼
    private Button button_regist;

    // 로딩 프로그레스바
    private ProgressBar loading;

    // 회원가입 정보를 mysql에 전송하기 위해 주소를 담는다.
//    private static String URL_REGIST = "http://34.73.32.3/register.php"; // gcp


    private static String URL_REGIST = "http://115.68.231.84/register.php"; //iwinv



    private String TAG = "Activity_Register";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 화면 세로고정

        loading = findViewById(R.id.reg_loading);
        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_pass);
        password_confirm = findViewById(R.id.reg_pass_confirm);
        button_regist = findViewById(R.id.reg_confirm_button);

        // 비밀번호, 비밀번호 확인 입력값이 일치하는지 확인하기
//        passMatch();

        // 회원가입 정보 입력하기 로직
        // 패스워드 일치여부 확인하기 로직
        // addTextChangedListener = 입력되는 텍스트에 변화가 있을 때마다 리스너 이벤트가 작동된다.
        password_confirm.addTextChangedListener(new TextWatcher()
        {
            // EditText 객체에 addTextViewChangedListener를 달아주고 TextWatcher라는 인터페이스를 연결해주면 된다. 그리고 onTextChanged 메소드 내부를 원하는대로 구현해주면 된다.
            // 주로 사용되는 예시는 회원가입시 아이디 중복여부를 빠르게 판단할 때, 리스트에서 특정 문자열이 들어간 것을 빠르게 검색할 때 등에 사용한다.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) // 입력되는 텍스트의 변화가 있을 때 감지하는 메소드
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) // 텍스트 입력이 끝났을 때 동작을 감지하는 메소드
            {
                String emailColorWhite = email.getText().toString();
                String pass = password.getText().toString();
                String passCon = password_confirm.getText().toString();

                if (pass.equals(passCon)) // password와 confirm의 문자열 일치여부 비교하기
                {
                    password.setTextColor(Color.rgb(255, 255, 255)); // 흰색
                    password_confirm.setTextColor(Color.rgb(255, 255, 255));
                }
                else// 입력값이 같지 않으면 경고
                {
                    password.setTextColor(Color.rgb(247, 202, 201)); // 분홍색
                    password_confirm.setTextColor(Color.rgb(247, 202, 201));
                }

            }

            @Override
            public void afterTextChanged(Editable s) // 입력하기 전에 감지하는 메소드
            {

            }
        });

        email.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) // 입력되는 텍스트의 변화가 있을 때 감지하는 메소드
            {
                email.setTextColor(Color.rgb(255, 255, 255)); // 흰색
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        // 가입버튼
        button_regist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Regist(); // 회원가입 메소드 실행하기
            }
        });
    }

    // 회원가입 메소드
    private void Regist()
    {
        final String nameResponse = this.name.getText().toString().trim();
        final String emailResponse = this.email.getText().toString().trim();
        final String passwordResponse = this.password.getText().toString().trim();
        final String passwordConResponse = this.password_confirm.getText().toString().trim();

        Log.e(TAG, "onResponse: response = " + nameResponse + ", " + emailResponse + ", " + passwordResponse);

        // 빈 칸을 모두 채우면 가입 요청하기
        if (!TextUtils.isEmpty(nameResponse) && // 이름 빈칸체크
                !TextUtils.isEmpty(emailResponse) && // 이메일 빈칸체크
                !TextUtils.isEmpty(passwordResponse) && // 비밀번호 빈칸체크
                !TextUtils.isEmpty(passwordConResponse) && // 비밀번호 확인 빈칸체크
                passwordResponse.equals(passwordConResponse)) // 비밀번호와 비밀번호 확인 입력값이 일치하지 않으면 진행 정지하기
        {
            // '회원가입'버튼 비활성화 후 로딩 프로그레스바 활성화하기
            loading.setVisibility(View.VISIBLE);
            // '회원가입'버튼 누르면 이 버튼 비활성화.
            button_regist.setVisibility(View.GONE);

            // 입력한 정보를 php POST로 DB에 전송합니다.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            Log.e(TAG, "onResponse: response = " + response);

                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);

                                // 가입이 완료되면 서버에서 success를 반환한다.
                                String success = jsonObject.getString("success");

                                // 계정이 중복되면 서버에서 3을 반환한다
                                // 중복결과 알리고 가입 중지
                                if (success.equals("3"))
                                {
                                    email.setTextColor(Color.rgb(247, 202, 201)); // 분홍색
                                    Toast.makeText(Activity_Register.this, "이미 사용중인 이메일입니다. ", Toast.LENGTH_SHORT).show();
                                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                                    email.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
                                    email.requestFocus(); // 해당 EditText로 커서를 포커스 한다.

                                    loading.setVisibility(View.GONE);
                                    button_regist.setVisibility(View.VISIBLE);
                                } else if (success.equals("1"))
                                {
                                    Toast.makeText(Activity_Register.this, "가입완료", Toast.LENGTH_SHORT).show();
                                    loading.setVisibility(View.GONE);

                                    Intent intent = new Intent(Activity_Register.this, Activity_Login.class);
                                    startActivity(intent);
                                    finish(); // 액티비티 종료하기
                                }
//                                else // success.equals("0") 이면
//                                {
//                                    Toast.makeText(Activity_Register.this, "문제발생." , Toast.LENGTH_SHORT).show();
//                                    loading.setVisibility(View.GONE);
//                                    button_regist.setVisibility(View.VISIBLE);
//                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                                Toast.makeText(Activity_Register.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                button_regist.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast.makeText(Activity_Register.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("회원가입 ", "문제: " + error);
                            loading.setVisibility(View.GONE);
                            button_regist.setVisibility(View.VISIBLE);
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", nameResponse);
                    params.put("email", emailResponse);
                    params.put("password", passwordResponse);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
        }
        else if (!passwordResponse.equals(passwordConResponse)) // 패스워드 입력값 일치여부
        {
            android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
            button_regist.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

            Toast.makeText(Activity_Register.this, "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show(); // 토스트 메시지를 띄우고
            password_confirm.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
            return; // return 안하면 제기능 못 함
        }
        else
        {
            // 이름 빈 칸 체크
            if (TextUtils.isEmpty(nameResponse)) // 이메일 입력칸이 비어있으면 경고하기
            { // 로그인 실패
                android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                button_regist.startAnimation(anim); // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(Activity_Register.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show(); // 토스트 메시지를 띄우고
                name.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                return; // 포커스 하러 돌아가! return 안하면 제기능 못 함
            }

            // 이메일 빈 칸 체크
            if (TextUtils.isEmpty(emailResponse)) // 패스워드 입력칸이 비어있으면 경고하기
            { // 로그인 실패
                android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                button_regist.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(Activity_Register.this, "이메일을 입력하세요!", Toast.LENGTH_SHORT).show(); // 토스트 메시지를 띄우고
                email.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                return; // return 안하면 제기능 못 함
            }

            // 비밀번호 빈 칸 체크
            if (TextUtils.isEmpty(passwordResponse)) // 패스워드 입력칸이 비어있으면 경고하기
            {
                android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                button_regist.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(Activity_Register.this, "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show(); // 토스트 메시지를 띄우고
                password.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                return; // return 안하면 제기능 못 함
            }

            // 비밀번호 확인 빈 칸 체크
            if (TextUtils.isEmpty(passwordConResponse)) // 패스워드 입력칸이 비어있으면 경고하기
            {
                android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                button_regist.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(Activity_Register.this, "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show(); // 토스트 메시지를 띄우고
                password_confirm.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                return; // return 안하면 제기능 못 함
            }
        }
    }

    // 비밀번호, 비밀번호 확인 입력값이 일치하는지 확인하기
    private void passMatch()
    {

    }

}
