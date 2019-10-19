package org.tensorflow.lite.examples.detection.wake.FragmentHome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.ApiClient;
import org.tensorflow.lite.examples.detection.wake.ApiInterface;
import org.tensorflow.lite.examples.detection.wake.FragmentProof.Activity_Certifying_Shot_List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search.GET_PROJECT_INDEX;


/**
 * 프로젝트 상세보기 페이지
 */
public class Activity_Project_Detail_Page extends AppCompatActivity
{
    private String TAG = "Activity_Project_Detail_Page";

    private ImageView project_detail_thumb; // 썸네일 사진 주소

    private TextView
            project_detail_title        // 제목
            , project_detail_Introduce   // 소개, 인증방법
            , project_Exp                // 경험치 안내
            , project_detail_day         // 진행 기간
            , project_detail_join_price  // 참가 금액
            , project_detail_total_price // 총 누적 금액
            , project_detail_people      // 총 누적 참가 인원
            , project_detail_certi_term  // 인증 빈도
            , project_detail_certi_time  // 인증 시간
            , project_detail_way         // 인증 방법 (인증샷, 물체인식, 방송)
            , project_detail_album_use   // 앨범 사용 (가능, 불가능)
            , project_detail_album_use_
            , project_detail_join_button // 참가버튼 (결제 페이지로 이동한다)
            , project_detail_users_certi_more_button // 인증샷 더보기 버튼
            ;

    // 진행 여부
    private String Progress;

    public static String GET_PROJECT_PRICE          // 프로젝트 참가비
            ,            GET_PROJECT_CERTI_COUNT    // 인증이 필요한 횟수
            ,            GET_PROJECT_CERTI_USER     // 신고 당할 유저
            ,            GET_PROJECT_CERTI_INDEX    // 신고할 인증샷 인덱스
    ;


    // 리사이클러뷰
    private RecyclerView mRecyclerView;
    public static List<ItemCertifyingList> GET_CERTY_LIST;
    private AdapterCertifyingShot mAdapter;

    // 그리드 리사이클러뷰 / 레이아웃 매니저
    private GridLayoutManager mGridLayoutManager;



    // 액티비티 종료 준비 (프로젝트 참가가 완료되면 화면이 종료됨)
    public static Activity Activity_Project_Detail_Page;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__project__detail__page);

        Activity_Project_Detail_Page = Activity_Project_Detail_Page.this;

        // todo: 화면 상단의 타이틀바
        Toolbar toolbar = findViewById(R.id.project_detail_page_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰 색으로 지정하기
        setSupportActionBar(toolbar);
        // 타이틀바 끝

        // todo: 서버에 값 요청 후 뷰에 값을 세팅한다. (제목, 진행 기간 등의 프로젝트 정보)
        getProjectInfo();

        // 리사이클러뷰 세팅하기
        mRecyclerView = findViewById(R.id.project_detail_users_certi_list);
        mRecyclerView.setHasFixedSize(true);

        // 그리드 리사이클러뷰 세팅
        mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // 리스트 초기화 해주기
        GET_CERTY_LIST = new ArrayList<>();
    }

    // todo: 서버에 값 요청 후 뷰에 값을 세팅한다. (제목, 진행 기간 등의 프로젝트 정보)
    private void getProjectInfo()
    {
        // View Find
        project_detail_thumb        = findViewById(R.id.project_detail_thumb);
        project_detail_title        = findViewById(R.id.project_detail_title);
        project_detail_Introduce    = findViewById(R.id.project_detail_Introduce);
        project_Exp                 = findViewById(R.id.project_Exp);
        project_detail_day          = findViewById(R.id.project_detail_day);
        project_detail_join_price   = findViewById(R.id.project_detail_join_price);
        project_detail_total_price  = findViewById(R.id.project_detail_total_price);
        project_detail_people       = findViewById(R.id.project_detail_people);
        project_detail_certi_term   = findViewById(R.id.project_detail_certi_term);
        project_detail_certi_time   = findViewById(R.id.project_detail_certi_time);
        project_detail_way          = findViewById(R.id.project_detail_way);
        project_detail_album_use    = findViewById(R.id.project_detail_album_use);
        project_detail_album_use_   = findViewById(R.id.project_detail_album_use_);
        project_detail_join_button  = findViewById(R.id.project_detail_join_button);
        project_detail_users_certi_more_button = findViewById(R.id.project_detail_users_certi_more_button);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/getProjectInfo.php",
                new Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e(TAG, "onResponse: response: " + response);

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(TAG, "onResponse: jsonObject: " + jsonObject);

                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1"))
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Log.e(TAG, "onResponse: Object: " + object);

                                    String MyJoinCheck = object.getString("Wake_JoinM_UserId");

                                    Picasso.get().load(object.getString("Wake_Info_ThumbImages")).
                                            memoryPolicy(MemoryPolicy.NO_CACHE).
                                            placeholder(R.drawable.logo_2).
                                            networkPolicy(NetworkPolicy.NO_CACHE).
                                            into(project_detail_thumb);

                                    // 제목
                                    project_detail_title.setText("제목: " + object.getString("Wake_Info_Title"));

                                    // 진행기간
                                    project_detail_day.setText("진행기간: " + object.getString("Wake_Progress_Start_Date") + " ~ " + object.getString("Wake_Progress_End_Date"));

                                    // 참가비용
                                    GET_PROJECT_PRICE =  object.getString("Wake_Info_Price");
                                    project_detail_join_price.setText("참가 금액: " + GET_PROJECT_PRICE + "만 원");

                                    // 총 누적 참가비용
                                    project_detail_total_price.setText("누적 참가금액: " + object.getString("Wake_Info_Total_Price") + "만 원");

                                    // 총 누적 참가 인원수
                                    project_detail_people.setText("참가인원: " + object.getString("Wake_Info_Total_People") + "명");

                                    // 프로젝트 소개, 인증 방법
                                    project_detail_Introduce.setText(object.getString("Wake_Info_Subscript"));

                                    // 경험치 안내 (카테고리)
                                    project_Exp.setText("이 챌린지를 완료하면 " + object.getString("Wake_Info_Category") + "'점수를 총 "+ GET_PROJECT_PRICE +"0 점 획득할 수 있습니다.");

                                    // 인증 빈도
                                    /**
                                     * 1. 7day
                                     * 2. 5day
                                     * 3. 2day
                                     * */
                                    String CertiDay = null;
                                    if (object.getString("Wake_Progress_Certi_Day").equals("7day"))
                                    {
                                        CertiDay = "월 ~ 일 (주 7일)";
                                    } else if (object.getString("Wake_Progress_Certi_Day").equals("5day"))
                                    {
                                        CertiDay = "월 ~ 금 (주 5일)";
                                    } else if (object.getString("Wake_Progress_Certi_Day").equals("2day"))
                                    {
                                        CertiDay = "토 ~ 일 (주 2일)";
                                    }

                                    // term : 인증 빈도
                                    project_detail_certi_term.setText(CertiDay);

                                    // time : 인증 시간
                                    project_detail_certi_time.setText(object.getString("Wake_Progress_Certi_Start_Time") + " ~ " + object.getString("Wake_Progress_Certi_End_Time"));

                                    // 인증 방법 (사진, 물체인식, 방송, 앨범 사용 가능여부)
                                    /**
                                     * 1. broadCast (방송)
                                     * 2. normal_photo_camera_only (일반사진_카메라만)
                                     * 3. normal_photo_use_album (일반사진_앨범허용)
                                     * 4. detect_photo_type_1_camera_only (사물인식_인식할 사물1_카메라만) (예: 인식할 사물1 = 컵 , 인식할 사물2 = 키보드)
                                     * 5. detect_photo_type_1_use_album (사물인식_인식할 사물1_앨범허용)
                                     * 6. distance_measurement 이동 거리 측정
                                     */

                                    String
                                            CertiWay = null      // 인증 방법
                                            , AlbumUse = null;   // 앨범 사용

//                                    if (object.getString("Wake_Info_Certi_Way").equals("broadCast"))
//                                    {
//                                        CertiWay = "방송";
//
//                                        // 방송일 경우 앨범 사용여부 숨기기
//                                        project_detail_album_use.setVisibility(View.GONE);
//                                        project_detail_album_use_.setVisibility(View.GONE);
//                                    }

                                    Log.e(TAG, "onResponse: object.getString(\"Wake_Info_Certi_Way\"): " + object.getString("Wake_Info_Certi_Way") );

                                    if (object.getString("Wake_Info_Certi_Way").equals("distance_measurement"))
                                    {
                                        CertiWay = "이동거리 측정기능 사용";

                                        // 방송일 경우 앨범 사용여부 숨기기
                                        project_detail_album_use.setVisibility(View.GONE);
                                        project_detail_album_use_.setVisibility(View.GONE);
                                    }
                                    else if (object.getString("Wake_Info_Certi_Way").equals("normal_photo_camera_only"))
                                    {
                                        CertiWay = "인증샷";
                                        AlbumUse = "불가능";
                                    } else if (object.getString("Wake_Info_Certi_Way").equals("normal_photo_use_album"))
                                    {
                                        CertiWay = "인증샷";
                                        AlbumUse = "가능";
                                    } else if (object.getString("Wake_Info_Certi_Way").equals("detect_photo_type_1_camera_only"))
                                    {
                                        CertiWay = "물체인식";
                                        AlbumUse = "불가능";
                                    } else if (object.getString("Wake_Info_Certi_Way").equals("detect_photo_type_1_use_album"))
                                    {
                                        CertiWay = "물체인식";
                                        AlbumUse = "가능";
                                    }
                                    // 앨범 사용 (가능, 불가능)
                                    project_detail_album_use.setText(AlbumUse);

                                    project_detail_way.setText(CertiWay);

                                    // 진행 여부
                                    Progress = object.getString("Wake_Progress");
                                    Log.e(TAG, "onResponse: Progress: " + Progress);

                                    GET_PROJECT_CERTI_COUNT = object.getString("Wake_Progress_Certi_Count");
                                    Log.e(TAG, "onResponse: 인증 해야될 횟수 GET_PROJECT_CERTI_COUNT: " + GET_PROJECT_CERTI_COUNT );

                                    /** Wake_Progress
                                     *
                                     * 1. Before_The_Start = 모집중 (이 경우에만 참여 가능)
                                     * 2. Started          = 시작됨
                                     * 3. Ended            = 종료됨
                                     * */

                                    // todo: 프로젝트 진행 여부에 따라 버튼 활성, 비활성
                                    if (Progress.equals("Before_The_Start"))
                                    {
                                        // todo: 참가하기 (결제 페이지로 이동)
                                        project_detail_join_button.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                // 결제 페이지로 이동
                                                Intent intent = new Intent(Activity_Project_Detail_Page.this, Activity_Project_Join.class);
                                                startActivity(intent);
                                            }
                                        });

                                    } else if (Progress.equals("Started"))
                                    {
                                        // todo: 프로젝트 진행 여부: 이미 시작 되었을 때
                                        project_detail_join_button.setText("챌린지가 시작 되었습니다");
                                        project_detail_join_button.setEnabled(false);
                                    } else if (Progress.equals("Ended"))
                                    {
                                        // todo: 프로젝트 진행 여부: 이미 종료 되었을 때
                                        project_detail_join_button.setText("챌린지가 종료 되었습니다");
                                        project_detail_join_button.setEnabled(false);
                                    }

                                    Log.e(TAG, "onResponse: MyJoinCheck: " + MyJoinCheck );
                                    if (MyJoinCheck.equals(getId))
                                    {
                                        // todo: 프로젝트 진행 여부: 이미 참여했을 때
                                        project_detail_join_button.setText("이미 참여했습니다");
                                        project_detail_join_button.setEnabled(false);
                                    }

                                    String index = object.getString("Wake_Progress_Info_No");
                                    Log.e(TAG, "onResponse: index: " + index );

                                    // todo: 유저들의 인증샷 불러오기
                                    getFeedList(index);
                                }
                                // 값 세팅 끝
                            }

                        } catch (JSONException e)
                        {
                            Toast.makeText(Activity_Project_Detail_Page.this, "JSONException. 로그확인", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e);
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
                        Toast.makeText(Activity_Project_Detail_Page.this, "VolleyError. 로그확인", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: VolleyError error: " + error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                params.put("GET_PROJECT_INDEX", GET_PROJECT_INDEX);
                params.put("getId", getId);
                return params;
            }
        };

        // 설정한 주소로 POST 요청하기
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getFeedList(String index)
    {
        Log.e(TAG, "getProjectList(): 인증샷 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiInterface Request = retrofit.create(ApiInterface.class);
        Call<List<ItemCertifyingList>> listCall = Request.getUserSertifyingShot(index);

        listCall.enqueue(new Callback<List<ItemCertifyingList>>()
        {
            @Override
            public void onResponse(Call<List<ItemCertifyingList>> call, retrofit2.Response<List<ItemCertifyingList>> response)
            {
                GET_CERTY_LIST = response.body();

                Log.e(TAG, "onResponse: GET_CERTY_LIST.size(): " + GET_CERTY_LIST.size() );

                if (GET_CERTY_LIST.size() == 0)
                {
                    Log.e(TAG, "onResponse: 표시할 리사이클러뷰 없음" );
                    project_detail_users_certi_more_button.setText("인증샷 없음");
                    project_detail_users_certi_more_button.setEnabled(false);
                }

                else
                {
//                    project_detail_users_certi_more_button.setVisibility(View.VISIBLE);

                    // 넘어온 값 확인하기
                    for (int i = 0; i < GET_CERTY_LIST.size(); i++)
                    {
                        Log.e(TAG, "list call onResponse = Info_Title: " + response.body().get(i).getWake_Info_Title());
                    }

                    // 리사이클러뷰 세팅
                    mAdapter = new AdapterCertifyingShot(Activity_Project_Detail_Page.this, GET_CERTY_LIST);
                    mRecyclerView.setAdapter(mAdapter);

                    // 더보기 페이지로 이동
                    project_detail_users_certi_more_button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(Activity_Project_Detail_Page.this, Activity_Certifying_Shot_List.class);

                            // 인증샷 상세정보로 가져갈 정보


                            startActivity(intent);
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<List<ItemCertifyingList>> call, Throwable t)
            {
                Toast.makeText(Activity_Project_Detail_Page.this, "리스트 로드 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
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
        Log.e(TAG, "onBackPressed: finish()");
        finish();
    }
}
