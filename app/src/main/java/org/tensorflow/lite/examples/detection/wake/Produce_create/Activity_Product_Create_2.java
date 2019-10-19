package org.tensorflow.lite.examples.detection.wake.Produce_create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.R;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getName;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_1.GET_PRODUCE_TITLE;


public class Activity_Product_Create_2 extends AppCompatActivity
{

    private String TAG = "Activity_Product_Create_2";

    // 뷰 선언
    private Button  create_button_2_done    // 다음 페이지로
            ,       create_button_2_cancel  // 액티비티 닫기
            ;
    private TextView produce_create_intro_2 // 화면 상단에 이름과 챌린지 제목 표시
            ;

    // 인증 빈도수 (라디오 그룹)
    private RadioGroup produce_page_2_radio_group;
    private RadioButton produce_page_2_radio_button_1 // 월 ~ 일 (매일)
            ,           produce_page_2_radio_button_2 // 월 ~ 금 (주 5일)
            ,           produce_page_2_radio_button_3 // 토 ~ 일 (주 2일)
            ;

    // 인증 빈도
    public static String GET_SERTI_COUNT;

    // 액티비티 종료 준비 (챌린지 전송 완료되면 화면이 종료됨)
    public static Activity Activity_Product_Create_2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create_2);

        // 액티비티 종료 준비 (챌린지 전송 완료되면 화면이 종료됨)
        Activity_Product_Create_2 = Activity_Product_Create_2.this;

        // View Find
        create_button_2_done = findViewById(R.id.create_button_2_done);
        create_button_2_cancel = findViewById(R.id.create_button_2_cancel);
        produce_create_intro_2 = findViewById(R.id.produce_create_intro_2);
        produce_page_2_radio_group = findViewById(R.id.produce_page_2_radio_group);
        produce_page_2_radio_button_1  = findViewById(R.id.produce_page_2_radio_button_1);
        produce_page_2_radio_button_2  = findViewById(R.id.produce_page_2_radio_button_2);
        produce_page_2_radio_button_3  = findViewById(R.id.produce_page_2_radio_button_3);

        // todo: 인증 빈도수 선택하기 (라디오그룹에서 값 받기)
        RadioGroupCertiCount();

        // 화면 상단에 이름과 챌린지 제목 표시
        produce_create_intro_2.setText(getName + "님의 챌린지 \n" + GET_PRODUCE_TITLE);

        // todo: 다음 페이지로 이동
        create_button_2_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Log.e(TAG, "onCheckedChanged: " + GET_SERTI_COUNT);

                Intent intent = new Intent(Activity_Product_Create_2.this, Activity_Product_Create_3.class);
                startActivity(intent);
            }
        });

        // todo: 화면 종료하기. 이전 페이지로
        create_button_2_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }


    // todo: 인증 빈도수 선택하기 (라디오그룹에서 값 받기)
    private void RadioGroupCertiCount()
    {
        RadioGroup.OnCheckedChangeListener RadioGroup_Certi_Count = new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // 월 ~ 일 (매일)
                if (checkedId == R.id.produce_page_2_radio_button_1)
                {
                    GET_SERTI_COUNT = "7day";
                    Log.e(TAG, "onCheckedChanged: " + GET_SERTI_COUNT);
                }

                // 월 ~ 금 (주 5일)
                else if (checkedId == R.id.produce_page_2_radio_button_2)
                {
                    GET_SERTI_COUNT = "5day";
                    Log.e(TAG, "onCheckedChanged: " + GET_SERTI_COUNT);
                }

                // 토 ~ 일 (주 2일)
                else if (checkedId == R.id.produce_page_2_radio_button_3)
                {
                    GET_SERTI_COUNT = "2day";
                    Log.e(TAG, "onCheckedChanged: " + GET_SERTI_COUNT);
                }
            }
        };

        // 라디오 그룹 활성화!
        produce_page_2_radio_group.setOnCheckedChangeListener(RadioGroup_Certi_Count);
    }
}
