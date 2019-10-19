package org.tensorflow.lite.examples.detection.wake;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.R;

public class Activity_Intro extends AppCompatActivity
{

    // 세션 선언
    // 로그인 체크
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 화면 세로고정

        // 아래 스레드를 이용해 다음 프로세스를 실행한다.
        // 1. try에서 설정한 시간동안 해당 액티비티에 머무른다.
        // 2. 문제가 발생하지 않으면 finally로 넘어가서 로그인 액티비티로 이동하는 인텐트를 실행한다.
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try // 아래 설정한 시간만큼 멈춘다
                {
                    sleep(1000); // 3000 = 3초, 500 = 0.5초
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    // 세션생성
                    sessionManager = new SessionManager(Activity_Intro.this);

                    // 세션 매니저에서 로그인 여부 체크.
                    // 1. 로그인 하지 않았으면 로그인 화면으로 이동한다.
                    // 2. 로그인 했다면 홈 화면으로 이동한다.
                    sessionManager.checkLogin();
                }
            }
        };
        thread.start();
    }

    @Override // 해당 액티비티가 정지되면
    protected void onPause()
    {
        super.onPause();

        finish(); // 액티비티를 아예 종료한다.
    }
}
