package org.tensorflow.lite.examples.detection.wake.Produce_create;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Calendar;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getName;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_1.GET_PRODUCE_TITLE;

public class Activity_Product_Create_4 extends AppCompatActivity
{
    private String TAG = "Activity_Product_Create_4";

    private Button
            create_button_4_done                // 다음 페이지로
            , create_button_4_cancel              // 액티비티 닫기
            , produce_create_page_4_auth_start_time // 인증 시작 시간 (인증시간 범위 설정)
            , produce_create_page_4_auth_end_time // 인증 종료 시간 (인증시간 범위 설정)
            ;

    private ArrayAdapter arrayAdapter;
    private Spinner produce_create_page_4_select_detect; // 인식할 사물 선택버튼

    private TextView produce_create_intro_4;

    public static String GET_AUTH_TYPE   //
            , GET_POROJECT_JOIN_PRICE    //
            , GET_AUTH_START_TIME        //
            , GET_AUTH_END_TIME          //
            ;

    private String
            photoType       //
            , detectType     //
            ;

    private RadioGroup radioGroup1 // 인증 방법 선택(사진, 방송)
            , radioGroup2 // 사진 유형 (일반 사진, 사물 인식)
            , radioGroup3 // 카메라만, 앨범 허용
            ;

    private RadioButton
            radio_button_1_1 // 인증 방법 _ 사진
            , radio_button_1_2 // 인증 방법 _ 방송
            , radio_button_2_1 // 사진 유형 _ 일반 사진
            , radio_button_2_2 // 사진 유형 _ 사물 인식
            , radio_button_3_1 // 사진 선택 방법 _ 카메라만
            , radio_button_3_2 // 사진 선택 방법 _ 앨범 허용
            ;

    private EditText produce_create_page_4_input_price // 참가 비용 입력하기
            ;

    private Calendar cal;


    //todo: GET_AUTH_TYPE
    /**
     * GET_AUTH_TYPE 결과 (인증방식 선택 결과)
     * <p>
     * 1. broadCast (방송)
     * <p>
     * 2. normal_photo_camera_only (일반사진_카메라만)
     * 3. normal_photo_use_album (일반사진_앨범허용)
     * <p>
     * 4. distance_measurement 이동 거리 측정
     * <p>
     * 5. detect_object_laptop 노트북
     * 6. detect_object_watch  시계
     * 7. detect_object_person 사람
     * 8. detect_motion_squat  스쿼트
     */

    /*
        랩탑 (물체인식)
        시계 (물체인식)
        인물 인식
        스쿼트 (동작인식)

        detect_object_laptop
        detect_object_watch
        detect_object_person
        detect_motion_squat
    */


    // 액티비티 종료 준비 (챌린지 전송 완료되면 화면이 종료됨)
    public static Activity Activity_Product_Create_4;

    ArrayList selectDetectType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create_4);

        // 액티비티 종료 준비 (챌린지 전송 완료되면 화면이 종료됨)
        Activity_Product_Create_4 = Activity_Product_Create_4.this;

        // View Find
        create_button_4_done = findViewById(R.id.create_button_4_done);
        create_button_4_cancel = findViewById(R.id.create_button_4_cancel);
        produce_create_intro_4 = findViewById(R.id.produce_create_intro_4);
        produce_create_page_4_select_detect = findViewById(R.id.produce_create_page_4_select_detect);
        produce_create_page_4_input_price = findViewById(R.id.produce_create_page_4_input_price);
        produce_create_page_4_auth_start_time = findViewById(R.id.produce_create_page_4_auth_start_time);
        produce_create_page_4_auth_end_time = findViewById(R.id.produce_create_page_4_auth_end_time);

        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioGroup3 = findViewById(R.id.radioGroup3);

        // 화면 상단에 이름과 챌린지 제목 표시
        produce_create_intro_4.setText(getName + "님의 챌린지 \n" + GET_PRODUCE_TITLE);


        selectDetectType = new ArrayList<>();
        selectDetectType.add("랩탑 (물체인식)");
        selectDetectType.add("시계 (물체인식)");
        selectDetectType.add("인물 인식");
        selectDetectType.add("스쿼트 (동작인식)");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                selectDetectType);

        produce_create_page_4_select_detect.setAdapter(arrayAdapter);

        // todo: 라디오그룹 제어하기
        RadioGroupControl_1();

        // todo: 인증시간 범위 설정
        SelectAuthTimeRange();

        create_button_4_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                // todo: 참가비용 입력
                GET_POROJECT_JOIN_PRICE = produce_create_page_4_input_price.getText().toString();

                Log.e(TAG, "onClick: GET_POROJECT_JOIN_PRICE: " + GET_POROJECT_JOIN_PRICE);
                Intent intent = new Intent(Activity_Product_Create_4.this, Activity_Product_Create_5.class);
                startActivity(intent);
            }
        });
    }

    // todo: 인증시간 범위 설정
    private void SelectAuthTimeRange()
    {
        //Calendar를 이용하여 년, 월, 일, 시간, 분을 PICKER에 넣어준다.
        final Calendar cal = Calendar.getInstance();

        // 인증 시작시간
        findViewById(R.id.produce_create_page_4_auth_start_time).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                TimePickerDialog dialog = new TimePickerDialog(Activity_Product_Create_4.this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min)
                    {

                        // 숫자가 10 이하일 때 '1'을 '01'로 출력하기
                        // '시간'이 10 이하일 경우
                        if (hour < 10)
                        {
                            // '분'도 10 이하일 경우
                            if (min < 10)
                            {
                                GET_AUTH_START_TIME = String.format("0%d:0%d", hour, min);
                                Log.e(TAG, "onTimeSet: time select: 1");
                            }

                            // '시간'만 10 이하일 경우
                            else
                            {
                                GET_AUTH_START_TIME = String.format("0%d:%d", hour, min);
                                Log.e(TAG, "onTimeSet: time select: 2");
                            }
                        }

                        // '분이'이 10 이하일 경우
                        else if (min < 10)
                        {
                            // '시간'도 10 이하일 경우
                            if (hour < 10)
                            {
                                GET_AUTH_START_TIME = String.format("0%d:%0d", hour, min);
                                Log.e(TAG, "onTimeSet: time select: 3");
                            }

                            // '분'만 10 이하일 경우
                            else
                            {
//                                GET_AUTH_START_TIME = String.format("%d:0%d", hour, min);
                                GET_AUTH_START_TIME = String.format("%d:%d", hour, min);
                                Log.e(TAG, "onTimeSet: time select: 4");
                            }
                        }

                        // 10 이하가 없을 경우
                        else
                        {
                            GET_AUTH_START_TIME = String.format("%d:%d", hour, min);
                            Log.e(TAG, "onTimeSet: time select: 5");
                        }

                        produce_create_page_4_auth_start_time.setText(GET_AUTH_START_TIME);

                        Log.e(TAG, "onTimeSet: GET_AUTH_START_TIME: " + GET_AUTH_START_TIME);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

                dialog.show();
            }
        });

        // 인증 종료시간
        findViewById(R.id.produce_create_page_4_auth_end_time).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                TimePickerDialog dialog = new TimePickerDialog(Activity_Product_Create_4.this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min)
                    {
                        String
                                hour2 = null, min2 = null;

                        // 숫자가 10 이하일 때 '1'을 '01'로 출력하기
                        // '시간'이 10 이하일 경우
                        if (hour < 10)
                        {

                            // '분'도 10 이하일 경우
                            if (min < 10)
                            {
                                GET_AUTH_END_TIME = String.format("0%d:0%d", hour, min);
                            }

                            // '시간'만 10 이하일 경우
                            else
                            {
                                GET_AUTH_END_TIME = String.format("0%d:%d", hour, min);
                            }

                        }

                        // '분이'이 10 이하일 경우
                        else if (min < 10)
                        {
                            // '시간'도 10 이하일 경우
                            if (hour < 10)
                            {
                                GET_AUTH_END_TIME = String.format("0%d:%d", hour, min);
                            }

                            // '분'만 10 이하일 경우
                            else
                            {
                                GET_AUTH_END_TIME = String.format("0%d:0%d", hour, min);
                            }

                        }

                        // 10 이하가 없을 경우
                        else
                        {
                            GET_AUTH_END_TIME = String.format("%d:%d", hour, min);
                        }

//                        GET_AUTH_END_TIME = String.format("%d:%d", hour2, min2);

                        produce_create_page_4_auth_end_time.setText(GET_AUTH_END_TIME);

                        Log.e(TAG, "onTimeSet: GET_AUTH_END_TIME: " + GET_AUTH_END_TIME);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

                dialog.show();
            }
        });
    }

    private void RadioGroupControl_1()
    {
        RadioGroup.OnCheckedChangeListener onCheckedChangeListener =
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        // 사진
                        if (checkedId == R.id.radio_button_1_1)
                        {
                            Log.e(TAG, "인증 방식: 사진");
                            RadioGroupControl_2();
                        }

                        // 거리 측정
                        else if (checkedId == R.id.radio_button_1_2)
                        {
//                            GET_AUTH_TYPE = "broadCast";
                            GET_AUTH_TYPE = "distance_measurement";
                            Log.e(TAG, "인증 방식: 방송 (변경 정)");
                            Log.e(TAG, "인증 방식: 거리측정 (현재)");
                            Log.e(TAG, "onCheckedChanged: GET_AUTH_TYPE: " + GET_AUTH_TYPE);

                            radioGroup2.clearCheck();
                            radioGroup3.clearCheck();

                            radioGroup2.setVisibility(View.GONE);
                            radioGroup3.setVisibility(View.GONE);
                            produce_create_page_4_select_detect.setVisibility(View.GONE);
                        }
                    }
                };

        radioGroup1.setOnCheckedChangeListener(onCheckedChangeListener);

    }

    private void RadioGroupControl_2()
    {
        radioGroup2.setVisibility(View.VISIBLE);

        RadioGroup.OnCheckedChangeListener onCheckedChangeListener =
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        // 일반 사진
                        if (checkedId == R.id.radio_button_2_1)
                        {
                            // 일반 사진
                            photoType = "normal_photo_";
                            Log.e(TAG, "사진 유형: 일반 사진");
                            RadioGroupControl_3();

                            radioGroup3.clearCheck();
                            produce_create_page_4_select_detect.setVisibility(View.GONE);
                        }

                        // 물체, 동작 감지
                        else if (checkedId == R.id.radio_button_2_2)
                        {
                            radioGroup3.clearCheck();
                            radioGroup3.setVisibility(View.GONE);

                            Log.e(TAG, "사진 유형: 물체 인식");
//                            RadioGroupControl_2();

                            // 물체인식
                            photoType = "detect_photo_";

                            produce_create_page_4_select_detect.setVisibility(View.VISIBLE);

                            detectType = "type_1_";

                            photoType = photoType + detectType;

                            Log.e(TAG, "onClick: GET_AUTH_TYPE: " + GET_AUTH_TYPE);

                            produce_create_page_4_select_detect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    Log.e(TAG, "onItemSelected: selectDetectType.get(position): " + selectDetectType.get(position));


                                            /*
                                                랩탑 (물체인식)
                                                시계 (물체인식)
                                                인물 인식
                                                스쿼트 (동작인식)
                                            */

                                    if ("랩탑 (물체인식)".equals(selectDetectType.get(position)))
                                    {
                                        GET_AUTH_TYPE = "detect_object_laptop";

                                        Log.e(TAG, "onItemSelected: GET_AUTH_TYPE: " + GET_AUTH_TYPE  );

                                    } else if ("시계 (물체인식)".equals(selectDetectType.get(position)))
                                    {
                                        GET_AUTH_TYPE = "detect_object_watch";

                                        Log.e(TAG, "onItemSelected: GET_AUTH_TYPE: " + GET_AUTH_TYPE  );
                                    } else if ("인물 인식".equals(selectDetectType.get(position)))
                                    {
                                        GET_AUTH_TYPE = "detect_object_person";
                                        Log.e(TAG, "onItemSelected: GET_AUTH_TYPE: " + GET_AUTH_TYPE  );
                                    } else if ("스쿼트 (동작인식)".equals(selectDetectType.get(position)))
                                    {
                                        GET_AUTH_TYPE = "detect_motion_squat";
                                        Log.e(TAG, "onItemSelected: GET_AUTH_TYPE: " + GET_AUTH_TYPE  );
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent)
                                {

                                }
                            });

                            // 인식할 물체를 선택하고 다음 라디오 그룹으로 넘어간다.
//                                    RadioGroupControl_3();
                        }
                    }
                };

        radioGroup2.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void RadioGroupControl_3()
    {
        radioGroup3.setVisibility(View.VISIBLE);

        RadioGroup.OnCheckedChangeListener onCheckedChangeListener =
                new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        // 카메라만
                        if (checkedId == R.id.radio_button_3_1)
                        {
                            GET_AUTH_TYPE = photoType + "camera_only";
                            Log.e(TAG, "카메라만 사용");
                            Log.e(TAG, "onClick: GET_AUTH_TYPE: " + GET_AUTH_TYPE);
                        }

                        // 앨범 허용
                        else if (checkedId == R.id.radio_button_3_2)
                        {
                            GET_AUTH_TYPE = photoType + "use_album";
                            Log.e(TAG, "앨범 허용");
                            Log.e(TAG, "onClick: GET_AUTH_TYPE: " + GET_AUTH_TYPE);
                        }
                    }
                };

        radioGroup3.setOnCheckedChangeListener(onCheckedChangeListener);
    }
}
