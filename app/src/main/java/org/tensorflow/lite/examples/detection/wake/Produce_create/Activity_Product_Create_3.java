package org.tensorflow.lite.examples.detection.wake.Produce_create;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import org.tensorflow.lite.examples.detection.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getName;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_1.GET_PRODUCE_INTRO_AND_CERTI;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_2.GET_SERTI_COUNT;


public class Activity_Product_Create_3 extends AppCompatActivity
{
    private String TAG = "Activity_Product_Create_3";

    private Button create_button_3_done                        // 다음 페이지로
            , create_button_3_cancel                      // 페이지 닫기
            , produce_create_page_3_select_start_date     // 챌린지 시작일
//            , produce_create_page_3_select_end_date       // 챌린지 종료일
            ;

    private Spinner produce_create_page_3_select_end_date; // 챌린지 종료일

    private TextView produce_create_intro_3              // 상단 화면에 유저 이름과 챌린지 제목 표시
            ,        produce_create_page_3_end_date_info // 종료일 알려주기
            ;

    // 데이트 피커
    private DatePickerDialog dialog;

    //Calendar를 이용하여 년, 월, 일, 시간, 분을 PICKER에 넣어준다.
    //Calendar를 이용하여 년, 월, 일, 시간, 분을 PICKER에 넣어준다.
    private Calendar cal = Calendar.getInstance();

    private String mYear,
            mMonth,
            mDate;

    public static String    GET_START_DATE  // 챌린지 시작 날짜
            ,               GET_END_DATE    // 챌린지 종료 날짜
            ,               GET_CERTI_DAY    // 챌린지 인증 해야될 횟수
            ;

    // 액티비티 종료 준비 (챌린지 전송 완료되면 화면이 종료됨)
    public static Activity Activity_Product_Create_3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create_3);

        // 액티비티 종료 준비 (챌린지 전송 완료되면 화면이 종료됨)
        Activity_Product_Create_3 = Activity_Product_Create_3.this;

        create_button_3_done = findViewById(R.id.create_button_3_done);
        produce_create_intro_3 = findViewById(R.id.produce_create_intro_3);
        create_button_3_cancel = findViewById(R.id.create_button_3_cancel);
        produce_create_page_3_select_start_date = findViewById(R.id.produce_create_page_3_select_start_date);
        produce_create_page_3_select_end_date = findViewById(R.id.produce_create_page_3_select_end_date);
        produce_create_page_3_end_date_info = findViewById(R.id.produce_create_page_3_end_date_info);

        // 화면 상단에 이름과 챌린지 제목 표시
        produce_create_intro_3.setText(getName + "님의 챌린지 \n" + GET_PRODUCE_INTRO_AND_CERTI);

//        // 중료일 비활성화
//        produce_create_page_3_select_end_date.setEnabled(false);

        // todo: 시작일 선택하기 (달력 다이얼로그, 데이트 피커)
        setStartDatePicer();

        create_button_3_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Log.e(TAG, "onClick: GET_START_DATE: "  + GET_START_DATE );
                Log.e(TAG, "onClick: GET_END_DATE: "  + GET_END_DATE );

                Intent intent = new Intent(Activity_Product_Create_3.this, Activity_Product_Create_4.class);
                startActivity(intent);
            }
        });
    }

    int date;

    private void setEndDateSpinner()
    {
        final ArrayList endDatePickList = new ArrayList<>();
        endDatePickList.add("1");
        endDatePickList.add("2");
        endDatePickList.add("3");

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                endDatePickList);

        produce_create_page_3_select_end_date.setAdapter(arrayAdapter);

        // 종료일 선택하기 (1주, 2주, 3주만 선택할 수 있게)
        produce_create_page_3_select_end_date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.e(TAG, "onItemSelected: endDatePickList.get(position): " + endDatePickList.get(position) );

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);

                if ("1".equals(endDatePickList.get(position)))
                {
//                    date = cal.get(Calendar.DATE) + 7;
//                    Log.e(TAG, "onItemSelected: date + : " + date );

                    cal.add(Calendar.DAY_OF_YEAR, 7); // 날짜 더하기 / 빼기

//                    cal.set(year, month, date);

                    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                    GET_END_DATE = format.format(cal.getTime());

                    Log.e(TAG, "onItemSelected: GET_END_DATE: " + GET_END_DATE );

                    // todo: 인증 횟수 구하기
                    // 주 7일 챌린지를 1주일 간 진행했을 때
                    if (GET_SERTI_COUNT.equals("7day"))
                    {
                        GET_CERTI_DAY = "7";
                    }

                    // 주 5일 챌린지를 1주일 간 진행했을 때
                    else if (GET_SERTI_COUNT.equals("5day"))
                    {
                        GET_CERTI_DAY = "5";
                    }

                    // 주 2일 챌린지를 1주일 간 진행했을 때
                    else if (GET_SERTI_COUNT.equals("2day"))
                    {
                        GET_CERTI_DAY = "2";
                    }

                    Log.e(TAG, "인증할 횟수: " + GET_CERTI_DAY );
                    Log.e(TAG, "onItemSelected: " + GET_END_DATE );

//                    date = cal.get(Calendar.DATE) - 7;
                    cal.add(Calendar.DAY_OF_YEAR, -7); // 날짜 더하기 / 빼기

//                    cal.set(year, month, date);
                }
                else if ("2".equals(endDatePickList.get(position)))
                {

//                    date = cal.get(Calendar.DATE) + 14;

                    cal.add(Calendar.DAY_OF_YEAR, 14); // 날짜 더하기 / 빼기

//                    cal.set(year, month, date);

                    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                    GET_END_DATE = format.format(cal.getTime());

                    // todo: 인증 횟수 구하기
                    // 주 7일 챌린지를 2주일 간 진행했을 때
                    if (GET_SERTI_COUNT.equals("7day"))
                    {
                        GET_CERTI_DAY = "14";
                    }

                    // 주 5일 챌린지를 2주일 간 진행했을 때
                    else if (GET_SERTI_COUNT.equals("5day"))
                    {
                        GET_CERTI_DAY = "10";
                    }

                    // 주 2일 챌린지를 2주일 간 진행했을 때
                    else if (GET_SERTI_COUNT.equals("2day"))
                    {
                        GET_CERTI_DAY = "4";
                    }

                    Log.e(TAG, "인증할 횟수: " + GET_CERTI_DAY );
                    Log.e(TAG, "onItemSelected: " + GET_END_DATE );

                    cal.add(Calendar.DAY_OF_YEAR, -14); // 날짜 더하기 / 빼기
//                    date = cal.get(Calendar.DATE) - 14;
//                    cal.set(year, month, date);
                }
                else if ("3".equals(endDatePickList.get(position)))
                {
//                    date = cal.get(Calendar.DATE) + 21;
                    cal.add(Calendar.DAY_OF_YEAR, 21); // 날짜 더하기 / 빼기

//                    cal.set(year, month, date);

                    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                    GET_END_DATE = format.format(cal.getTime());


                    // todo: 인증 횟수 구하기
                    // 주 7일 챌린지를 3주일 간 진행했을 때
                    if (GET_SERTI_COUNT.equals("7day"))
                    {
                        GET_CERTI_DAY = "21";
                    }

                    // 주 5일 챌린지를 3주일 간 진행했을 때
                    else if (GET_SERTI_COUNT.equals("5day"))
                    {
                        GET_CERTI_DAY = "15";
                    }

                    // 주 2일 챌린지를 3주일 간 진행했을 때
                    else if (GET_SERTI_COUNT.equals("2day"))
                    {
                        GET_CERTI_DAY = "6";
                    }

                    Log.e(TAG, "인증할 횟수: " + GET_CERTI_DAY );
                    Log.e(TAG, "onItemSelected: " + GET_END_DATE );

//                    date = cal.get(Calendar.DATE) - 21;
                    cal.add(Calendar.DAY_OF_YEAR, -21); // 날짜 더하기 / 빼기
//                    cal.set(year, month, date);
                }


                produce_create_page_3_end_date_info.setText(GET_END_DATE + "일에 종료됩니다.");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        /*


        // 종료일 선택하기 (1주, 2주, 3주만 선택할 수 있게)
        produce_create_page_3_select_end_date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                if ("1".equals(endDatePickList.get(position)))
                {
                }
                else if ("2".equals(endDatePickList.get(position)))
                {
                }
                else if ("3".equals(endDatePickList.get(position)))
                {
                }


                produce_create_page_3_end_date_info.setText(GET_END_DATE + "일에 종료됩니다.");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        */
    }

    // todo: 시작일 선택하기
    private void setStartDatePicer()
    {
        findViewById(R.id.produce_create_page_3_select_start_date).setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v)
            {
                dialog = new DatePickerDialog(Activity_Product_Create_3.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int date/*dayOfMonth*/)
                            {
                                year = dialog.getDatePicker().getYear();
                                month = dialog.getDatePicker().getMonth();
                                date = dialog.getDatePicker().getDayOfMonth();

                                cal = cal.getInstance();
                                cal.set(year, month, date);

                                SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
                                GET_START_DATE = format.format(cal.getTime());

                                Log.e(TAG, "onDateSet: datePicResult: " + GET_START_DATE);

                                // 선택한 날짜 세팅하기
                                produce_create_page_3_select_start_date.setText(GET_START_DATE);

                                // todo: 종료일 선택하기 (스피너)
                                setEndDateSpinner();
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)
                );

                // 날짜는 오늘 이후로만 선택하기
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());

                // 최대 7일 이후까지만 예약 가능
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
                dialog.show();
            }
        });
    }
}
