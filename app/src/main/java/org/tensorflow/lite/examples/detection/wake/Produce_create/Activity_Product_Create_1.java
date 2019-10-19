package org.tensorflow.lite.examples.detection.wake.Produce_create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import org.tensorflow.lite.examples.detection.R;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getName;


/**
 * 챌린지 생성화면 1페이지 (총 5페이지)
 * */
public class Activity_Product_Create_1 extends AppCompatActivity
{

    // 뷰 변수 선언
    private Button      create_button_1_done // 다음 페이지로
            ;
    private TextView    produce_create_intro // 화면 상단에 사용자 이름 표시
            ;
    private EditText    produce_create_title // 제목 입력받기
            ,           produce_create_input_introduce_and_certi // 챌린지 소개 및 인증방법 입력받기
            ;

    public static String    GET_PRODUCE_TITLE
            ,               GET_PRODUCE_INTRO_AND_CERTI
            ;

    public static Activity Activity_Product_Create_1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create_1);

        // 액티비티 일괄 종료를 하기위한 준비
        Activity_Product_Create_1 = Activity_Product_Create_1.this;

        // View Find
        create_button_1_done = findViewById(R.id.create_button_1_done);
        produce_create_intro = findViewById(R.id.produce_create_intro);
        produce_create_title = findViewById(R.id.produce_create_title);
        produce_create_input_introduce_and_certi = findViewById(R.id.produce_create_input_introduce_and_certi);

        // 화면 상단에 사용자 이름 표시
        produce_create_intro.setText(getName + "님의\n챌린지를 개설하세요");

        // 제목과 내용 변수가 비어있지 않다면 값 세팅하기
        // TODO: 제목
//        if (GET_PRODUCE_TITLE == null)
//        {
//
//        }
//        else if (GET_PRODUCE_TITLE.length() == 0)
//        {
//
//        }
//        else
//        {
//            produce_create_title.setText(GET_PRODUCE_TITLE);
//        }
//
//        // TODO: 내용
//        if (produce_create_input_introduce_and_certi == null)
//        {
//
//        }
//        else if (produce_create_input_introduce_and_certi.length() == 0)
//        {
//
//        }
//        else
//        {
//            produce_create_input_introduce_and_certi.setText(GET_PRODUCE_INTRO_AND_CERTI);
//        }

        // 다음 페이지로 이동
        create_button_1_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 제목 입력받기
                GET_PRODUCE_TITLE = produce_create_title.getText().toString();

                // 내용 입력받기
                GET_PRODUCE_INTRO_AND_CERTI = produce_create_input_introduce_and_certi.getText().toString();

                Intent intent = new Intent(Activity_Product_Create_1.this, Activity_Product_Create_2.class);
                startActivity(intent);
            }
        });
    }
}
