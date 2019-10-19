package org.tensorflow.lite.examples.detection.wake.FragmentProof;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot;

import static org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot.itemCertifyingLists;


public class Activity_Certifying_Shot_List extends AppCompatActivity
{

    private String TAG = "Activity_Certifying_Shot_List";

    private RecyclerView mRecyclerView;
    private AdapterCertifyingShot mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__certifying__shot__list);

        // todo: 화면 상단의 타이틀바
        Toolbar toolbar = findViewById(R.id.certifying_shot_list_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰 색으로 지정하기
        setSupportActionBar(toolbar);
        // 타이틀바 끝

        // 리사이클러뷰 세팅하기
        mRecyclerView = findViewById(R.id.project_certy_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(Activity_Certifying_Shot_List.this, 3));

        // 넘어온 값 확인하기
        for (int i = 0; i < itemCertifyingLists.size(); i++)
        {
            Log.e(TAG, "onCreate: itemCertifyingLists: " + itemCertifyingLists.get(i).getPhoto() );
        }

        mAdapter = new AdapterCertifyingShot(Activity_Certifying_Shot_List.this, itemCertifyingLists);
        mRecyclerView.setAdapter(mAdapter);
    }
}
