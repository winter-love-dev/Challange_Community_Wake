package org.tensorflow.lite.examples.detection.wake;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TabHost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;


import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.FragmentFeed.Fragment_Feed;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.OnBackPressedListener;
import org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage;
import org.tensorflow.lite.examples.detection.wake.FragmentProoduce.Fragment_Produce;
import org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof;

import java.util.HashMap;
import java.util.List;

public class Activity_Home extends AppCompatActivity
{
    String TAG = "Activity_Home";

    public static FragmentTabHost host;

    int TabPsotion;

    // 쉐어드에서 내 정보 불러오기
    private SessionManager sessionManager;

    public static String getId;
    public static String getName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 세션생성
        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);        // 유저 아이디
        getName = user.get(sessionManager.NAME);    // 유저 이름

        Log.e(TAG, "onCreate: getId = " + getId);

        // 탭 호스트 연결
        host = (FragmentTabHost) findViewById(android.R.id.tabhost);
        host.setup(this, getSupportFragmentManager(), R.id.content);

        // 탭 이미지 연결
        ImageView tab01 = new ImageView(this);
        tab01.setImageResource(R.drawable.tab_01);

        // todo: 탐색
        TabHost.TabSpec tabSpec1 = host.newTabSpec("탐색"); // 구분자
        tabSpec1.setIndicator(tab01);
        host.addTab(tabSpec1, Fragment_Search.class, null);

        /*.setContent(new Intent(this, Fragment_Search.class))*/

        // 탭 이미지 연결
        ImageView tab02 = new ImageView(this);
        tab02.setImageResource(R.drawable.tab_02);

        // todo: 맞춤
        TabHost.TabSpec tabSpec2 = host.newTabSpec("맞춤");
        tabSpec2.setIndicator(tab02);
        host.addTab(tabSpec2, Fragment_Produce.class, null);

        // 탭 이미지 연결
        ImageView tab03 = new ImageView(this);
        tab03.setImageResource(R.drawable.tab_03);

        // todo: 인증
        TabHost.TabSpec tabSpec3 = host.newTabSpec("인증");
        tabSpec3.setIndicator(tab03);
        host.addTab(tabSpec3, Fragment_Proof.class, null);

        // 탭 이미지 연결
        ImageView tab04 = new ImageView(this);
        tab04.setImageResource(R.drawable.tab_04);

        // todo: 피드
        TabHost.TabSpec tabSpec4 = host.newTabSpec("피드");
        tabSpec4.setIndicator(tab04);
        host.addTab(tabSpec4, Fragment_Feed.class, null);

        // 탭 이미지 연결
        ImageView tab05 = new ImageView(this);
        tab05.setImageResource(R.drawable.tab_05);

        // todo: 마이페이지
        TabHost.TabSpec tabSpec5 = host.newTabSpec("마이페이지");
        tabSpec5.setIndicator(tab05);
        host.addTab(tabSpec5, Fragment_MyPage.class, null);

        TabPsotion = host.getCurrentTab();
        Log.e(TAG, "onTabChanged: TabPsotion: " + TabPsotion);

        // 이미지 간격 줄이기
        for (int i = 0; i < host.getTabWidget().getChildCount(); i++)
        {
            host.getTabWidget().getChildAt(i).setPadding(20, 20, 20, 20);
        }

        // 탭 배경색
        host.getTabWidget().getChildAt(0)
                .setBackgroundColor(Color.parseColor("#FFFFFF"));
        host.getTabWidget().getChildAt(1)
                .setBackgroundColor(Color.parseColor("#FFFFFF"));
        host.getTabWidget().getChildAt(2)
                .setBackgroundColor(Color.parseColor("#FFFFFF"));
        host.getTabWidget().getChildAt(3)
                .setBackgroundColor(Color.parseColor("#FFFFFF"));
        host.getTabWidget().getChildAt(4)
                .setBackgroundColor(Color.parseColor("#FFFFFF"));

        // 기본 탭 위치는 '탐색'창으로 지정한다
        host.setCurrentTab(0);

//        TextView temp = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
//        temp.setTextColor(Color.parseColor("#FE6067"));

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                // 탭 변경시 리스너
                TabPsotion = host.getCurrentTab();
                Log.e(TAG, "onTabChanged: TabPsotion: " + TabPsotion);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null)
        {
            //TODO: Perform your logic to pass back press here
            for (Fragment fragment : fragmentList)
            {
                if (fragment instanceof OnBackPressedListener)
                {
                    ((OnBackPressedListener) fragment).onBackPressed();

                    // 탭 포지션이 '탐색창'이 아니면 탐색창으로 이동한다.
                    // 탭 포지션이 '탐색창'이라면 화면을 종료한다.
                    if (TabPsotion != 0)
                    {
                        Log.e(TAG, "onBackPressed: 0번 포지션으로 이동합니다");
                        host.setCurrentTab(0);
                    } else if (TabPsotion == 0)
                    {
                        Log.e(TAG, "onBackPressed: 앱을 종료합니다");
                        finish();
                    }
                }
            }
        }

    }
}
