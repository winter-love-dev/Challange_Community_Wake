package org.tensorflow.lite.examples.detection.wake.FragmentMyPage;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.ApiClient;
import org.tensorflow.lite.examples.detection.wake.ApiInterface;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Activity_Project_Detail_Page;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.OnBackPressedListener;
import org.tensorflow.lite.examples.detection.wake.FragmentProof.Activity_Finish_Project_Notice;
import org.tensorflow.lite.examples.detection.wake.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search.mContext_Fragment_Search;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_MyPage extends Fragment implements OnBackPressedListener
{
    private String TAG = "Fragment_MyPage";

    // 요청 전송할 서버 주소 iwinv
    private static String URL_READ = "http://115.68.231.84/read_wake_detail.php"; // 유저 정보 불러오기

    // View 선언
    private TextView fra_mypage_name            // 사용자 이름
            , fra_mypage_logout                 // 로그아웃
            , fra_mypage_reword                 // 리워드
            , fra_mypage_complain_manage_button // 신고접수 처리화면으로 이동하기 버튼 (관리자 권한)
            , fra_mypage_manager_tag            // 관리자 접수
            , fra_mypage_join_list_more_button  // 지난 참가기록 더보기
            , fra_mypage_chart_more_button      // 차트 더보기 버튼
            , fra_mypage_tensor_flow_test            // 텐서 플로우 테스트
            ;

    private CircleImageView fra_mypage_profile_image; // 프로필 이미지

    // Context 선언
    private View View_Fragment_MyPage; // 프래그먼트 뷰
    private Context Context_Fragment_MyPage; // 컨텍스트

    // 세션 선언
    public SessionManager sessionManager;

    // 리사이클러뷰
    public static RecyclerView mRecyclerView;
    public static List<Item_My_JoinList> itemMyJoinLists;
    private Item_My_JoinList item_my_joinList;
    private MyJoinList mAdapter;

    // 획득 습관점수
    private String
            My_Ability      // 역량
            , My_Health     // 건강
            , My_Life       // 생활
            , My_Hobby      // 취미
            , My_Relation   // 관계
            , My_Asset      // 자산
            ;

    /*
        My_Ability
        My_Health
        My_Life
        My_Hobby
        My_Relation
        My_Asset
    */

    /**
     * 발표 순서
     * 1. 마이페이지 실행하기
     * 2. 프로젝트 생성
     * 3. 프로젝트 참가
     * 4. 인증하기
     * 5. 피드에서 확인하기
     * 6. 마이페이지에서 차트 확인하기
     */

    // 차트 선언
    private RadarChart chart;

    public static String GET_JAVA_VARIABLES;

    public static String
            GET_CHELLANGE_DONE_RESULT_PEOPLE_COUNT              // 챌린지 총 참가 인원수
            , GET_CHELLANGE_DONE_RESULT_YOUR_REAWARD            // 챌린지에서 얻은 보상금
            , GET_CHELLANGE_DONE_RESULT_PROGRESS_PERCENT        // 챌린지 참가인원 총 달성률
            , GET_CHELLANGE_DONE_RESULT_YOUR_PROGRESS_PERCENT   // 나의 챌린지 달성률
            , GET_CHELLANGE_DONE_RESULT_CHECK                   // 종료된 챌린지 동료 체크하기 or 체크해서 추가보상 받기
            , GET_CHELLANGE_DONE_RESULT_PRICE                   //
            , GET_CHELLANGE_DONE_RESULT_EXP                     // 챌리지에서 획득한 습관점수
            , GET_CHELLANGE_DONE_RESULT_EXP_CATEGORY            // 습관점수 카테고리
            , GET_CHELLANGE_DONE_RESULT_MORE_REWARD             // 추가 보상금
            , GET_CHELLANGE_DONE_PROGRESS_INDEX                 // 종료된 챌린지 인덱스
            , GET_CHELLANGE_DONE_RESULT_INDEX                   // 챌린지 종료 결과 인덱스
            , GET_CHELLANGE_DONE_JOIN_INDEX                     // ??
            , GET_USER_REWARD               //
            , GET_EXP_INFO_MY_ABILITY  // 습관점수 _ 역량
            , GET_EXP_INFO_MY_HEALTH   // 습관점수 _ 건강
            , GET_EXP_INFO_MY_LIFE     // 습관점수 _ 생활
            , GET_EXP_INFO_MY_HOBBY    // 습관점수 _ 취미
            , GET_EXP_INFO_MY_RELATION // 습관점수 _ 관계
            , GET_EXP_INFO_MY_ASSET    // 습관점수 _ 자산
            ;

    /*
    GET_EXP_INFO_MY_ABILITY
    GET_EXP_INFO_MY_HEALTH
    GET_EXP_INFO_MY_LIFE
    GET_EXP_INFO_MY_HOBBY
    GET_EXP_INFO_MY_RELATION
    GET_EXP_INFO_MY_ASSET
    */

    /*
        GET_CHELLANGE_DONE_RESULT_PEOPLE_COUNT
        GET_CHELLANGE_DONE_RESULT_YOUR_REAWARD
        GET_CHELLANGE_DONE_RESULT_PROGRESS_PERCENT
        GET_CHELLANGE_DONE_RESULT_YOUR_PROGRESS_PERCENT
        GET_CHELLANGE_DONE_RESULT_CHECK
        GET_CHELLANGE_DONE_RESULT_PRICE
        GET_CHELLANGE_DONE_RESULT_EXP
        GET_CHELLANGE_DONE_RESULT_EXP_CATEGORY
        GET_CHELLANGE_DONE_RESULT_MORE_REWARD
    */

    public Fragment_MyPage()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Fragment_MyPage = inflater.inflate(R.layout.fragment_my_page, container, false);

        // Context 준비
        Context_Fragment_MyPage = getActivity().getApplicationContext();

        // View Find
        fra_mypage_logout = View_Fragment_MyPage.findViewById(R.id.fra_mypage_logout);
        fra_mypage_profile_image = View_Fragment_MyPage.findViewById(R.id.fra_mypage_profile_image);
        fra_mypage_name = View_Fragment_MyPage.findViewById(R.id.fra_mypage_name);
        fra_mypage_reword = View_Fragment_MyPage.findViewById(R.id.fra_mypage_reword);
        fra_mypage_complain_manage_button = View_Fragment_MyPage.findViewById(R.id.fra_mypage_complain_manage_button);
        fra_mypage_manager_tag = View_Fragment_MyPage.findViewById(R.id.fra_mypage_manager_tag);
        // fra_mypage_join_list_more_button = View_Fragment_MyPage.findViewById(R.id.fra_mypage_join_list_more_button);
        fra_mypage_chart_more_button = View_Fragment_MyPage.findViewById(R.id.fra_mypage_chart_more_button);



//        // todo: 텐서플로우 테스트
//        fra_mypage_tensor_flow_test = View_Fragment_MyPage.findViewById(R.id.fra_mypage_tensor_flow_test);
//        fra_mypage_tensor_flow_test.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(Context_Fragment_MyPage, org.tensorflow.lite.examples.posenet.PosenetCameraActivity.class);
//
//                intent.putExtra("kotlin_test", "값 가라!!");
//                GET_JAVA_VARIABLES = "받아라!";
//
//                Context_Fragment_MyPage.startActivity(intent);
//            }
//        });

        // todo: 관리자 인식
        if (Integer.parseInt(getId) == 1 || Integer.parseInt(getId) == 2)
        {
            // 관리자
            fra_mypage_complain_manage_button.setVisibility(View.VISIBLE);
            fra_mypage_manager_tag.setVisibility(View.VISIBLE);

            // 신고접수 처리 화면으로 이동하기
            fra_mypage_complain_manage_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext_Fragment_Search, Activity_Complain_List.class);
                    mContext_Fragment_Search.startActivity(intent);
                }
            });
        }

        // todo: 차트 불러오기
        getChatData();

        // 참여중인 프로젝트 리사이클러뷰
        itemMyJoinLists = new ArrayList<>();
        mRecyclerView = View_Fragment_MyPage.findViewById(R.id.fra_mypage_join_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Context_Fragment_MyPage));

        // todo: 참여중인 프로젝트 불러오기
        getMyJoinProject();

        // todo: 지난 프로젝트 참여기록 불러오기
//        fra_mypage_join_list_more_button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//
//            }
//        });


        // todo: 로그아웃 버튼
        sessionManager = new SessionManager(Context_Fragment_MyPage);
        fra_mypage_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 세션에 담긴 값을 초기화 한다.
                // 초기화 후 로그인 페이지로 이동한다.
                sessionManager.logout();
            }
        });

        return View_Fragment_MyPage;
    }


    // todo: 차트 불러오기
    private void getChatData()
    {
        // 로그인 한 유저의 정보 불러오기.
        getUserDetail();

        // View Find
        chart = View_Fragment_MyPage.findViewById(R.id.chart1);
        chart.setBackgroundColor(Color.rgb(255, 255, 255));

        // 차트 회전 막기
        chart.setTouchEnabled(false);

        // 차트 설명문 비활성화, 숨기기
        chart.getDescription().setEnabled(false);

        // 거미줄 모양 설정
        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // 사용자 정의 MarkerView (확장 MarkerView)를 작성하고 레이아웃을 지정하십시오.
        // to use for it
        // 그것을 위해 사용
        // todo: 차트의 선을 표현할 뷰를 불러옵니다 (커스텀 뷰)

        // 차트 선 표현하는 레이아웃 (선: 마커)
        MarkerView mv = new RadarMarkerView(Context_Fragment_MyPage, R.layout.radar_markerview);

        // 차트 선을 레이더 차트뷰에 세팅
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

//        // 차트에 데이터 넣기
//        setData();

        // 차트를 애니메이션 설정
        chart.animateXY(1400, 1400, Easing.EaseInOutQuad);


        // 차트 테두리 설정
        XAxis xAxis = chart.getXAxis();

//        xAxis.setTypeface(tfLight);
        xAxis.setTextSize(12f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);

        // 차트 테두리에 표시할 카테고리 설정
        xAxis.setValueFormatter(new ValueFormatter()
        {

            private final String[] mActivities = new String[]{"역량", "건강", "관계", "취미", "생활", "자산"};

            @Override
            public String getFormattedValue(float value)
            {
                return mActivities[(int) value % mActivities.length];
            }
        });

        // 차트 텍스트 색상
        xAxis.setTextColor(Color.BLACK);

        // 차트 내부 설정
        YAxis yAxis = chart.getYAxis();
//        yAxis.setTypeface(tfLight);

        // 차트 내부 칸 수 설정
        yAxis.setLabelCount(5, false);

        // 차트 테두리의 텍스트 크기
        yAxis.setTextSize(9f);

        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        // 차트 제목 설정
        Legend l = chart.getLegend();

        // 차트 위치 설정 ( 높이_ 위, 아래, 중앙 )
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        // 차트 수평 위치 설정 ( 중앙, 왼 쪽, 오른 쪽)
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);

        // 제목과 차트를 어떻게 정렬할 지 설정하기
        // 수평: 제목과 차트를 수평으로 정렬 <- 이게 차트 크기를 더 크게 표현함
        // 수직: 제목과 차트를 수직으로 정렬
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        // 내부에 뭘 그린다는거지?
        l.setDrawInside(false);

        // 글씨 굵기
//        l.setTypeface(tfLight);

        // x, y 축 입력공간 간격
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);

        // 제목 색상
        l.setTextColor(Color.BLACK);

    }

    // 차트에 데이터 넣기
    private void setData()
    {

        // 생활점수
        float score[] = {
                Float.parseFloat(My_Ability),  // 역량
                Float.parseFloat(My_Health),            // 건강
/*                Float.parseFloat(My_Relation),        // 관계*/
                20,
                /*Float.parseFloat(My_Hobby)*/10,             // 취미
                /*Float.parseFloat(My_Life)*/30,        // 생활
//                Float.parseFloat(My_Asset)            // 자산
                40
        };

        ArrayList<RadarEntry> entries1 = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of the chart.
        // 참고 : 항목 배열에 추가 될 때 항목의 순서에 따라 차트 중앙 주위의 위치가 결정됩니다.

        // todo: 차트에 생활점수 담기
        for (int i = 0; i < score.length; i++)
        {
            entries1.add(new RadarEntry(score[i]));
        }

        // 레이더 차트 클래스에 데이터 넣기 (제목 입력: 획득 습관)
        RadarDataSet set1 = new RadarDataSet(entries1, null);

        // 차트 선 테두리 색상
        set1.setColor(Color.rgb(254, 96, 103));

        // 차트 선 내부 색상
        set1.setFillColor(Color.rgb(241, 159, 162));

        // 차트 선 내부 색상 채우기. false로 하면 윤곽성만 보임
        set1.setDrawFilled(true);

        set1.setFillAlpha(180);
        set1.setLineWidth(2f);

        // 하이라이트 원 그리기 활성화
        set1.setDrawHighlightCircleEnabled(true);

        // 하이라이트 표시기 그리기
        set1.setDrawHighlightIndicators(false);

//        RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
//        set2.setColor(Color.rgb(121, 162, 175));
//        set2.setFillColor(Color.rgb(121, 162, 175));
//        set2.setDrawFilled(true);
//        set2.setFillAlpha(180);
//        set2.setLineWidth(2f);
//        set2.setDrawHighlightCircleEnabled(true);
//        set2.setDrawHighlightIndicators(false);

        // 위 설정을 리스트 sets변수에 담기
        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
//        sets.add(set2); // 두 가지 데이터를 표현 가능

        // sets 변수에 담긴 설정을 불러와서 차트에 적용하기
        RadarData data = new RadarData(sets);

//        data.setValueTypeface(tfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.invalidate();

        // 차트 더보기
        fra_mypage_chart_more_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Fragment_MyPage, Activity_Exp_Detail_Info.class);
                Context_Fragment_MyPage.startActivity(intent);
            }
        });
    }

    private void getMyJoinProject()
    {
        Log.e(TAG, "참여중인 프로젝트 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface MyJoinList = retrofit.create(ApiInterface.class);

        // defining the call
        Call<List<Item_My_JoinList>> SuggestionList_Call = MyJoinList.getMyJoinList(getId);

        SuggestionList_Call.enqueue(new Callback<List<Item_My_JoinList>>()
        {
            @Override
            public void onResponse(Call<List<Item_My_JoinList>> call, retrofit2.Response<List<Item_My_JoinList>> response)
            {
                itemMyJoinLists = response.body();

                mAdapter = new MyJoinList(Context_Fragment_MyPage, itemMyJoinLists);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Item_My_JoinList>> call, Throwable t)
            {
                Toast.makeText(Context_Fragment_MyPage, "문제 발생", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: call: " + call.toString());
                Log.e(TAG, "onFailure: Throwable t: " + t.toString());
            }
        });
    }

    // 유저 상세정보 불러오기 (이름, 이메일)
    private void getUserDetail()
    {

        // Volley로 서버 요청을 보내기 위해 데이터 세팅.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>()
                {
                    // JSON 형태로 결과 요청을 받는다.
                    // 요청받은 결과가 response에 저장된다.
                    @Override
                    public void onResponse(String response)
                    {
                        // 요청 응답 받음.
                        // 다이얼로그 비활성.
                        Log.i(TAG, response.toString());

                        try
                        {
                            // JSON 형식으로 응답받음.
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "getUserDetail: response = " + response);

                            // 전달받은 json에서 success를 받는다.
                            // {"success":"??"}
                            String success = jsonObject.getString("success");

                            // 키값이 read인 json에 담긴 value들을 배열에 담는다.
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            // success의 값이 아래와 같으면 아래 반복문을 진행한다
                            // {"success":"??"} = ??에 담긴 값이 1일 때 아래 반복문을 진행
                            if (success.equals("1"))
                            {
                                // 키값이 read인 배열에 담긴 값들을 모두 불러온다.
                                // 불러올 값 = 이름, 이메일, 프로필 이미지
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    Log.e(TAG, "onResponse: JSONObject object = " + object.length());

                                    String strName = object.getString("name").trim();
                                    // strEmail = object.getString("email").trim();
                                    String strType = object.getString("type").trim();
                                    Log.e(TAG, "onResponse: strType = " + strType);
                                    // 프로필 페이지에서 이름과 이메일 영역에
                                    // 로그인 중인 유저의 이름, 이메일, 프로필 사진을 출력한다.
                                    fra_mypage_name.setText(strName);

                                    String strImage = object.getString("image").trim();

                                    // 서버 URL로 불러온 이미지를 세팅한다.
                                    Picasso.get().load(strImage).
                                            // memoryPolicy(MemoryPolicy.NO_CACHE).
                                                    placeholder(R.drawable.logo_4).
                                            // networkPolicy(NetworkPolicy.NO_CACHE).
                                                    into(fra_mypage_profile_image);

                                    if (strImage != null)
                                    {
                                        // 프로필 사진 불러오면 테두리 두께좀 줄여
                                        fra_mypage_profile_image.setBorderWidth(1);
                                    } else
                                    {
                                        // 프로필 사진 비어있으면 테두리 두께를 늘려
                                        fra_mypage_profile_image.setBorderWidth(5);
                                    }

                                    GET_USER_REWARD = object.getString("Wake_Member_possession_reward").trim();

//                                    if (Reward == null || Reward.length() == 0 || TextUtils.isEmpty(Reward))
//                                    {

                                    // 보유 상금
                                    fra_mypage_reword.setText(GET_USER_REWARD + "원");
//                                    }
//                                    else
//                                    {
//                                        // 보유 상금
//                                        fra_mypage_reword.setText("0원");
//                                    }


                                    My_Ability = object.getString("Wake_Member_Exp_Ability").trim();
                                    My_Health = object.getString("Wake_Member_Exp_Health").trim();
                                    My_Life = object.getString("Wake_Member_Exp_Life").trim();
                                    My_Hobby = object.getString("Wake_Member_Exp_Hobby").trim();
                                    My_Relation = object.getString("Wake_Member_Exp_Relation").trim();
                                    My_Asset = object.getString("Wake_Member_Exp_Asset").trim();


                                    GET_EXP_INFO_MY_ABILITY = My_Ability;
                                    GET_EXP_INFO_MY_HEALTH = My_Health;
                                    GET_EXP_INFO_MY_LIFE = My_Life;
                                    GET_EXP_INFO_MY_HOBBY = My_Hobby;
                                    GET_EXP_INFO_MY_RELATION = My_Relation;
                                    GET_EXP_INFO_MY_ASSET = My_Asset;

                                    Log.e(TAG, "onResponse: My_Ability: " + My_Ability);
                                    Log.e(TAG, "onResponse: My_Health: " + My_Health);
                                    Log.e(TAG, "onResponse: My_Life: " + My_Life);

                                    Log.e(TAG, "onResponse: My_Hobby: " + My_Hobby);
                                    Log.e(TAG, "onResponse: My_Relation: " + My_Relation);
                                    Log.e(TAG, "onResponse: My_Asset: " + My_Asset);

//                                    if (TextUtils.isEmpty(My_Ability) &&
//                                            TextUtils.isEmpty(My_Health) &&
//                                            TextUtils.isEmpty(My_Life) &&
//                                            TextUtils.isEmpty(My_Hobby) &&
//                                            TextUtils.isEmpty(My_Relation) &&
//                                            TextUtils.isEmpty(My_Asset)
//                                    )
//                                    {
//                                        // 차트에 데이터 넣기
//                                        setData();
//
//                                        Log.e(TAG, "onResponse: 1" );
//                                    }
//                                    else
//                                    {
//                                        My_Ability  = "0";
//                                        My_Health   = "0";
//                                        My_Life     = "0";
//                                        My_Hobby    = "0";
//                                        My_Relation = "0";
//                                        My_Asset    = "0";
                                    setData();
//                                    }
                                }
                            }
                        } // try에 포함된 로직 중 틀린 코드가 있으면 예외상황으로 간주함.
                        catch (JSONException e) // 에러 알림
                        {
                            e.printStackTrace();
                            Toast.makeText(Context_Fragment_MyPage, "에러발생!" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: getUserDetail JSONException e = " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() // 응답 실패할 시 에러 알림
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
//                        Toast.makeText(Activity_MyPage.this, "에러발생!" + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: getUserDetail VolleyError = " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // 로그인 한 유저의 '유저 번호'를 해쉬맵에 담는다.
                params.put("id", getId);

                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(Context_Fragment_MyPage);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed()
    {
    }

    public class MyJoinList extends RecyclerView.Adapter<MyJoinList.ViewHolder>
    {

        public MyJoinList(Context context, List<Item_My_JoinList> item_my_joinLists)
        {
            Context_Fragment_MyPage = context;
            itemMyJoinLists = item_my_joinLists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(Context_Fragment_MyPage).inflate(R.layout.item_my_join_list, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i)
        {
            // 아이템 클래스에 담긴 값 꺼내기
            final Item_My_JoinList currentItem = itemMyJoinLists.get(i);

            viewHolder.fra_mypage_my_join_p_title.setText(currentItem.getWake_Info_Title());
            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Info_Title(): " + currentItem.getWake_Info_Title());
            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Progress_No(): " + currentItem.getWake_Progress_No());

            viewHolder.fra_mypage_my_join_p_date.setText(currentItem.getWake_Progress_Start_Date() + " ~ " + currentItem.getWake_Progress_End_Date());
            viewHolder.fra_mypage_my_join_p_category.setText(currentItem.getWake_Info_Category());
            viewHolder.fra_mypage_my_join_p_category_score.setText(currentItem.getWake_JoinM_Experience() + "점");
            viewHolder.fra_mypage_my_join_p_reward.setText(currentItem.getWake_JoinM_Reward() + "원");
            viewHolder.fra_mypage_my_join_p_progress_percent.setText(currentItem.getWake_JoinM_Progress_Percent() + "%");


            Log.e(TAG, "onBindViewHolder: currentItem.getWake_JoinM_Check_Exit_Result(): " + currentItem.getWake_JoinM_Check_Exit_Result());
            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Progress(): " + currentItem.getWake_Progress());

            /** todo: (프로젝트 진행 여부 참고)
             *
             * 1. Before_The_Start = 모집중 (이 경우에만 참여 가능)    =
             * 2. Started          = 시작됨 (진행중)                  = 진행중일 경우엔 챌린지 진행정보 페이지로 이동
             * 3. Ended            = 종료됨                           = 종료 되었을 경우 보상 확인 페이지로 이동
             * */

            /**
             * todo: 챌린지 종료 체크
             *
             * CheckNot : 챌린지 진행중 or 종료된 챌린지 확인 안 함.
             * Checked : 확인하면 종료 확인 체크됨
             *
             * 종료 확인 체크된 항목은 마이페이지에서 삭제하고 지난 챌린지 참가 기록으로 이동하기
             * */


            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Progress(): " + currentItem.getWake_Progress() );
            Log.e(TAG, "onBindViewHolder: currentItem.getWake_JoinM_Check_Exit_Result(): " + currentItem.getWake_JoinM_Check_Exit_Result() );

            // 진행중인 프로젝트 표시하기
            if (currentItem.getWake_Progress().equals("Before_The_Start") || currentItem.getWake_Progress().equals("Started"))
            {
                viewHolder.fra_mypage_my_join_p_more_button.setText("진행 정보 더보기");

                viewHolder.fra_mypage_my_join_p_more_button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Context_Fragment_MyPage, Activity_Project_Detail_Page.class);
                        Fragment_Search.GET_PROJECT_INDEX = currentItem.getWake_Progress_No();
                        Context_Fragment_MyPage.startActivity(intent);
                    }
                });
            }

            // 끝난 프로젝트, 그리고 종료 결과 확인 안 한 항목한 표시하기
            else if (currentItem.getWake_Progress().equals("Ended"))
            {
                if (currentItem.getWake_JoinM_Check_Exit_Result().equals("CheckNot"))
                {
                    viewHolder.fra_mypage_my_join_p_more_button.setText("챌린지 종료결과 더보기");

                    viewHolder.fra_mypage_my_join_p_more_button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 챌린지 종료 화면으로 이동
                            Intent intent = new Intent(Context_Fragment_MyPage, Activity_Finish_Project_Notice.class);

                            GET_CHELLANGE_DONE_PROGRESS_INDEX = currentItem.getWake_Progress_No();
                            GET_CHELLANGE_DONE_RESULT_PEOPLE_COUNT = currentItem.getWake_Progress_People_Count();
                            GET_CHELLANGE_DONE_RESULT_YOUR_REAWARD = currentItem.getWake_JoinM_Reward();
                            GET_CHELLANGE_DONE_RESULT_PROGRESS_PERCENT = currentItem.getWake_DoneResult_Progress_Percent();
                            GET_CHELLANGE_DONE_RESULT_YOUR_PROGRESS_PERCENT = currentItem.getWake_JoinM_Progress_Percent();
                            GET_CHELLANGE_DONE_RESULT_CHECK = currentItem.getWake_DoneResult_Check();
                            GET_CHELLANGE_DONE_RESULT_EXP = currentItem.getWake_JoinM_Experience();
                            GET_CHELLANGE_DONE_RESULT_EXP_CATEGORY = currentItem.getWake_Info_Category();
                            GET_CHELLANGE_DONE_RESULT_MORE_REWARD = currentItem.getWake_DoneResult_Price();
                            GET_CHELLANGE_DONE_RESULT_INDEX = currentItem.getWake_DoneResult_Index();
                            GET_CHELLANGE_DONE_JOIN_INDEX = String.valueOf(i);

                            startActivity(intent);
                        }
                    });

                    // 챌린지 상세정보 페이지로 이동
                    viewHolder.view.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(Context_Fragment_MyPage, Activity_Project_Detail_Page.class);
                            Fragment_Search.GET_PROJECT_INDEX = currentItem.getWake_Progress_No();
                            startActivity(intent);
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount()
        {
            return itemMyJoinLists.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView fra_mypage_my_join_p_title;
            public TextView fra_mypage_my_join_p_more_button;

            public TextView
                    fra_mypage_my_join_p_date               // 진행기간
                    , fra_mypage_my_join_p_category          // 습관
                    , fra_mypage_my_join_p_category_score    // 습관 점수
                    , fra_mypage_my_join_p_reward            // 상금
                    , fra_mypage_my_join_p_progress_percent  // 달성률
                    ;

            public View view;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                fra_mypage_my_join_p_title = itemView.findViewById(R.id.fra_mypage_my_join_p_title);
                fra_mypage_my_join_p_more_button = itemView.findViewById(R.id.fra_mypage_my_join_p_more_button);
                fra_mypage_my_join_p_date = itemView.findViewById(R.id.fra_mypage_my_join_p_date);
                fra_mypage_my_join_p_category = itemView.findViewById(R.id.fra_mypage_my_join_p_category);
                fra_mypage_my_join_p_category_score = itemView.findViewById(R.id.fra_mypage_my_join_p_category_score);
                fra_mypage_my_join_p_reward = itemView.findViewById(R.id.fra_mypage_my_join_p_reward);
                fra_mypage_my_join_p_progress_percent = itemView.findViewById(R.id.fra_mypage_my_join_p_progress_percent);
                view = itemView;
            }
        }
    }
}
