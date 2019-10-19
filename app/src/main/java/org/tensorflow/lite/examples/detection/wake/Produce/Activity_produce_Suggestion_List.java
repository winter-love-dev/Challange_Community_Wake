package org.tensorflow.lite.examples.detection.wake.Produce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.ApiClient;
import org.tensorflow.lite.examples.detection.wake.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;

/**
 * Wake Up! 프로그램 주제 제안 목록
 **/

public class Activity_produce_Suggestion_List extends AppCompatActivity
{
    static String TAG = "Activity_produce_Suggestion_List";

    // 새로고침 메소드를 전역으로 사용하기
    public static Context SuggestionListContext;

    TextView Button_Suggestion_Write; // 주제 제안 작성화면으로 이동하기

    // 리사이클러뷰
    public static RecyclerView mRecyclerView;
    public static Suggestion_List Adapter_Suggestion_List;
    public static List<Item_Suggestion> List_Suggestion;


    // 조회할 글 번호
    public static String SuggestionDetailInfo
            ,               SuggestionDetailTitle
            ,               SuggestionLike
            ,               Suggestion_Detail_Vote_Level
            ,               LikeIncludeMe
            ,               SuggestionArrayListIndex
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produce__suggestion__list);

        // todo: 화면 상단의 타이틀바
        Toolbar toolbar = findViewById(R.id.produce_Suggestion_Toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰 색으로 지정하기
        setSupportActionBar(toolbar);
        // todo: 타이틀바 끝

        // context 부여하기
        SuggestionListContext = getApplicationContext();

        // View Find
        Button_Suggestion_Write = findViewById(R.id.Button_Suggestion_Write);

        // 주제 제안 작성 페이지로 이동하기
        Button_Suggestion_Write.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Activity_produce_Suggestion_List.this, Activity_Suggestion_Write.class);
                startActivity(intent);
            }
        });

        // 리사이클러뷰
        List_Suggestion = new ArrayList<>();

        // 리사이클러뷰
        mRecyclerView = findViewById(R.id.produce_Suggestion_List);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 서버에서 값 불러오고 리사이클러뷰에 세팅하기
        getSuggestionList();


        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_suggestion);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // cancle the Visual indication of a refresh
                        swipeRefreshLayout.setRefreshing(false);

                        // todo: 주제 제안 목록 불러오기
                        getSuggestionList();
                    }
                }, 1500); // 1.5초 딜레이 후 리스트 새로 불러옴
            }
        });
    }

    // todo: 주제 제안 목록 불러오기
    public static void getSuggestionList()
    {
        Log.e(TAG, "주제 제안 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface SuggestionList = retrofit.create(ApiInterface.class);

        // defining the call
        Call<List<Item_Suggestion>> SuggestionList_Call = SuggestionList.getSuggestionList(getId);

        SuggestionList_Call.enqueue(new Callback<List<Item_Suggestion>>()
        {
            @Override
            public void onResponse(Call<List<Item_Suggestion>> call, Response<List<Item_Suggestion>> response)
            {
                List_Suggestion = response.body();

                for (int i = 0; i < List_Suggestion.size(); i++)
                {
                    Log.e(TAG, "onResponse: getWake_AS_No: " + List_Suggestion.get(i).getWake_AS_No());
                    Log.e(TAG, "onResponse: getWake_AS_Suggestion: " + List_Suggestion.get(i).getWake_AS_Suggestion());
                    Log.e(TAG, "onResponse: getSuggestion_Like: " + List_Suggestion.get(i).getSuggestion_Like());
                }

                // 서버에서 응답받은 값 세팅
                Adapter_Suggestion_List = new Suggestion_List(SuggestionListContext, List_Suggestion);
                mRecyclerView.setAdapter(Adapter_Suggestion_List);
            }

            @Override
            public void onFailure(Call<List<Item_Suggestion>> call, Throwable t)
            {
                Toast.makeText(SuggestionListContext, "문제 발생", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: call: " + call.toString() );
                Log.e(TAG, "onFailure: Throwable t: " + t.toString() );
            }
        });
    }

    // 제안 목록 어댑터
    public static class Suggestion_List extends RecyclerView.Adapter<Suggestion_List.Suggestion_List_ViewHolder>
    {
        private Context mContext;

        public Suggestion_List(Context context, List<Item_Suggestion> item_suggestions)
        {
            mContext = context;
            List_Suggestion = item_suggestions;
        }

        @NonNull
        @Override
        public Suggestion_List_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_wake_up_suggestion, viewGroup, false);

            return new Suggestion_List_ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Suggestion_List_ViewHolder suggestion_list_viewHolder, final int i)
        {
            // 아이템 클래스에 담긴 값 꺼내기
            final Item_Suggestion currentItem = List_Suggestion.get(i);

            // 유저가 작성한 주제 세팅
            suggestion_list_viewHolder.item_Suggestion_title.setText(currentItem.getWake_AS_Suggestion());

            /**
             * 투표수 상위 3등 안에 포함된 항목 컬러를 각각 다르게 표시함
             * 투표수 3위 이하에 포함된 항목 컬러는 모두 노란 색으로 통일한다.
             * */
//            // 컬러 _ 메인 1등 _ 254, 96, 103
//            suggestion_list_viewHolder.item_Suggestion_vote_level.setBackgroundColor(Color.rgb(254, 96, 103));
//
//            // 컬러 _ 메인 2등 _ 250, 124, 130
//            suggestion_list_viewHolder.item_Suggestion_vote_level.setBackgroundColor(Color.rgb(254, 96, 103));
//
//            // 컬러 _ 메인 3등 _ 248, 156, 161
//            suggestion_list_viewHolder.item_Suggestion_vote_level.setBackgroundColor(Color.rgb(254, 96, 103));
//
//            // 컬러 _ 노랑 등외_ 240, 219, 71
//            suggestion_list_viewHolder.item_Suggestion_vote_level.setBackgroundColor(Color.rgb(254, 96, 103));

            // 좋아요 수 (투표수)
            suggestion_list_viewHolder.item_Suggestion_like_Count.setText(currentItem.getSuggestion_Like() + "명");

            // 값 확인하기
            Log.e(TAG, "onBindViewHolder: Index: " + currentItem.getWake_AS_No() );
            Log.e(TAG, "onBindViewHolder: Wake_AS_Suggestion(): " + currentItem.getWake_AS_Suggestion() );
            Log.e(TAG, "onBindViewHolder: Suggestion_Like(): " + currentItem.getSuggestion_Like() );
            Log.e(TAG, "onBindViewHolder: Like_Include_Me(): " + currentItem.getLike_InClude_Me() );

//            if (currentItem.getLike_InClude_Me().equals("true"))
//            {
//                suggestion_list_viewHolder.item_Suggestion_vote_icon.setImageResource(R.drawable.baseline_how_to_vote_white_48);
//            }
//            else
//            {
//                suggestion_list_viewHolder.item_Suggestion_vote_icon.setImageResource(R.drawable.outline_how_to_vote_white_48);
//            }

            // todo: 좋아요 여부 icon 체크
            if (currentItem.getLike_InClude_Me().equals("true"))
            {
                // 이미 좋아요 눌렀다면
                suggestion_list_viewHolder.item_Suggestion_vote_icon.setImageResource(R.drawable.baseline_how_to_vote_white_48);
            } else
            {
                // 아직 좋아요 누르지 않았다면
                suggestion_list_viewHolder.item_Suggestion_vote_icon.setImageResource(R.drawable.outline_how_to_vote_white_48);
            }

            // todo: 주제 제안에 투표하기 (좋아요)
            suggestion_list_viewHolder.item_Suggestion_vote_level.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (currentItem.getLike_InClude_Me().equals("false"))
                    {
                        Log.e(TAG, "onClick: 투표하기 " );
                        Log.e(TAG, "onClick: 투표하는 항목 번호: " + currentItem.getWake_AS_No() );
                        
                        // 투표 수 올리기
                        Log.e(TAG, "onClick: 투표 전 카운트 : " + currentItem.getSuggestion_Like() );
                        int Vote_Count = Integer.parseInt(currentItem.getSuggestion_Like() + 1);
                        Log.e(TAG, "onClick: 투표 후 카운트: " + Vote_Count );
                        suggestion_list_viewHolder.item_Suggestion_like_Count.setText(Vote_Count + "명");

                        // 리스트에 투표 수 올리기
                        List_Suggestion.get(i).setSuggestion_Like(String.valueOf(Vote_Count));

                        Log.e(TAG, "onClick: 나의 투표 여부 추가 전: " + currentItem.getLike_InClude_Me() );

                        // 투표한 항목에 '나의 투표여부'를 true로 바꾼다.
                        currentItem.setLike_InClude_Me("true");

                        Log.e(TAG, "onClick: 나의 투표 여부 추가 후: " + currentItem.getLike_InClude_Me() );

                        // 리스트에 '나의 투표여부'를 false로 업데이트
                        List_Suggestion.get(i).setLike_InClude_Me("true");

                        // 투표 취소 이미지로 바꾸기
                        suggestion_list_viewHolder.item_Suggestion_vote_icon.setImageResource(R.drawable.baseline_how_to_vote_white_48);

                        Toast.makeText(mContext, "투표했습니다.", Toast.LENGTH_SHORT).show();

                        // todo: 서버의 mysql로 내 투표정보 업데이트 하기
                        UpDateMyVote(currentItem.getWake_AS_No() ,getId);
                    }
                    else
                    {
                        Log.e(TAG, "onClick: 투표취소 " );

                        // 투표 취소하기
                        Log.e(TAG, "onClick: 투표 취소 전 카운트 : " + currentItem.getSuggestion_Like() );
                        int Vote_Count = Integer.parseInt(currentItem.getSuggestion_Like());

                        // 투표 수 한 칸 감소
                        Vote_Count--;

                        Log.e(TAG, "onClick: 투표 취소 후 카운트: " + Vote_Count );
                        suggestion_list_viewHolder.item_Suggestion_like_Count.setText(Vote_Count + "명");

                        Log.e(TAG, "onClick: 나의 투표 취소 전: " + currentItem.getLike_InClude_Me() );

                        // 리스트에 투표 수 내리기
                        List_Suggestion.get(i).setSuggestion_Like(String.valueOf(Vote_Count));

                        // 투표 취소한 항목에 '나의 투표여부'를 false로 바꾼다.
                        currentItem.setLike_InClude_Me("false");
                        Log.e(TAG, "onClick: 나의 투표 취소 후: " + currentItem.getLike_InClude_Me() );

                        // 리스트에 '나의 투표여부'를 false로 업데이트
                        List_Suggestion.get(i).setLike_InClude_Me("false");

                        // 투표 이미지로 바꾸기
                        suggestion_list_viewHolder.item_Suggestion_vote_icon.setImageResource(R.drawable.outline_how_to_vote_white_48);

                        Toast.makeText(mContext, "투표를 취소합니다.", Toast.LENGTH_SHORT).show();

                        UpDateMyVote(currentItem.getWake_AS_No() ,getId);
                    }
                }
            }); // todo: 주제 제안에 투표 끝 (좋아요)

            // todo: 상세 페이지로 이동하기
            suggestion_list_viewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mContext = v.getContext();
                    Intent intent = new Intent(mContext, Activity_Suggestion_Detail.class);

                    // 조회할 글 번호
                    SuggestionDetailInfo = currentItem.getWake_AS_No();

                    // 주제 제목
                    SuggestionDetailTitle = currentItem.getWake_AS_Suggestion();

                    // 좋아요 수
                    SuggestionLike = currentItem.getSuggestion_Like();

                    // 좋아요 레벨
//                    Suggestion_Detail_Vote_Level = currentItem.getWake_AS_No();

                    // 나의 좋아요 여부
                    LikeIncludeMe = currentItem.getLike_InClude_Me();

                    // 상세 페이지에서 수정할 인덱스
                    SuggestionArrayListIndex = String.valueOf(i);

                    Log.e(TAG, "onClick: 조회할 글 번호: " + SuggestionDetailInfo );
                    Log.e(TAG, "onClick: 조회할 글 제목: " + SuggestionDetailTitle);
                    Log.e(TAG, "onClick: 나의 좋아요 여부: " + LikeIncludeMe);
                    Log.e(TAG, "onClick: ArrayListIndex: " + SuggestionArrayListIndex);
                    
                    mContext.startActivity(intent);
                }
            });
        }

        // todo: 서버의 mysql로 내 투표정보 업데이트 하기
        private void UpDateMyVote(final String Index, final String VoteUserID)
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/getVoteSignal.php",
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            Log.e(TAG, "onResponse: response = " + response);

                            try
                            {
                                JSONObject jsonObject = new JSONObject(response);

                                String success = jsonObject.getString("success");

                                if (success.equals("addVote"))
                                {
                                    Toast.makeText(SuggestionListContext, "투표함", Toast.LENGTH_SHORT).show();
                                }

                                else if (success.equals("deleteVote"))
                                {
                                    Toast.makeText(SuggestionListContext, "투표 취소됨", Toast.LENGTH_SHORT).show();
                                }

//                                else
//                                {
//                                    Toast.makeText(Activity_produce_Suggestion_List.this, "문제발생.", Toast.LENGTH_SHORT).show();
//                                    Log.e(TAG, "onResponse: response: " + response);
//                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                                Toast.makeText(SuggestionListContext, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: JSONException e: " + e.toString() );
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast.makeText(SuggestionListContext, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onErrorResponse: error: " + error );
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<>();
                    params.put("Index", Index);
                    params.put("VoteUserID", VoteUserID);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(SuggestionListContext);
            requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
        }


        @Override
        public int getItemCount()
        {
            return List_Suggestion.size();
        }

        public class Suggestion_List_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view;
            public TextView item_Suggestion_title
                    ,       item_Suggestion_like_Count
                    ;
            public LinearLayout item_Suggestion_vote_level;

            public ImageView item_Suggestion_vote_icon;

            public Suggestion_List_ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                view = itemView;
                item_Suggestion_title = itemView.findViewById(R.id.item_Suggestion_title);
                item_Suggestion_like_Count = itemView.findViewById(R.id.item_Suggestion_like_Count);
                item_Suggestion_vote_level = itemView.findViewById(R.id.item_Suggestion_vote_level);
                item_Suggestion_vote_icon = itemView.findViewById(R.id.item_Suggestion_vote_icon);
            }
        }
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
