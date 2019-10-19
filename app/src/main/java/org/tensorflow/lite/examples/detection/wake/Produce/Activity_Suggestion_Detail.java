package org.tensorflow.lite.examples.detection.wake.Produce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.Adapter_Suggestion_List;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.LikeIncludeMe;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.List_Suggestion;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.SuggestionArrayListIndex;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.SuggestionDetailInfo;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.SuggestionDetailTitle;
import static org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List.SuggestionLike;


/**
 * 주제 제안 상세페이지
 */
public class Activity_Suggestion_Detail extends AppCompatActivity
{
    String TAG = "Activity_Suggestion_Detail";

    private TextView Suggestion_Detail_Title        // 주제 제목
            , Suggestion_Detail_Like_Count          // 좋아요 수
            , Suggestion_Opinion_Write              // 인증방법 제안 글 쓰기 버튼
            ;

    private ImageView Suggestion_Detail_Vote_Icon;  // 좋아요 아이콘

    private LinearLayout Suggestion_Detail_Vote_Level;  // 좋아요 레벨

    // 인증방법 제안
    private RecyclerView mRecyclerView;
    private Opinion_Adapter opinion_adapter;

    // 1. 인증방법 작성 페이지에서 작성한 내용 받아온다.
    // 2. 현재 페이지의 댓글 목록에 추가한다.
    public static List<Item_Opinion> List_Opinion;
    public static Item_Opinion item_opinion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_detail);

        // todo: 화면 상단의 타이틀바
        Toolbar toolbar = findViewById(R.id.Suggestion_Detail_Title_Bar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰 색으로 지정하기
        setSupportActionBar(toolbar);
        // todo: 타이틀바 끝

        // View Find
        Suggestion_Detail_Title = findViewById(R.id.Suggestion_Detail_Title);
        Suggestion_Detail_Like_Count = findViewById(R.id.Suggestion_Detail_Like_Count);
        Suggestion_Detail_Vote_Level = findViewById(R.id.Suggestion_Detail_Vote_Level);
        Suggestion_Detail_Vote_Icon = findViewById(R.id.Suggestion_Detail_Vote_Icon);
        Suggestion_Opinion_Write = findViewById(R.id.Suggestion_Opinion_Write);

        // todo: 이전 페이지 리사이클러뷰에서 가져온 값 세팅
        Suggestion_Detail_Title.setText(SuggestionDetailTitle);
        Suggestion_Detail_Like_Count.setText(SuggestionLike + "명");

        // todo: 좋아요 여부 icon 체크
        if (LikeIncludeMe.equals("true"))
        {
            // 이미 좋아요 눌렀다면
            Suggestion_Detail_Vote_Icon.setImageResource(R.drawable.baseline_how_to_vote_white_48);
        } else
        {
            // 아직 좋아요 누르지 않았다면
            Suggestion_Detail_Vote_Icon.setImageResource(R.drawable.outline_how_to_vote_white_48);
        }

        Suggestion_Detail_Vote_Level.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getLike_InClude_Me().equals("false"))
                {
                    Log.e(TAG, "onClick: 투표하기 " );
                    Log.e(TAG, "onClick: 투표하는 항목 번호: " + List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getWake_AS_No() );

                    // 투표 수 올리기
                    Log.e(TAG, "onClick: 투표 전 카운트 : " + List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getSuggestion_Like() );
                    int Vote_Count = Integer.parseInt(List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getSuggestion_Like() + 1);
                    Log.e(TAG, "onClick: 투표 후 카운트: " + Vote_Count );
                    Suggestion_Detail_Like_Count.setText(Vote_Count + "명");

                    // 리스트에 투표 수 올리기
                    List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).setSuggestion_Like(String.valueOf(Vote_Count));

                    Log.e(TAG, "onClick: 나의 투표 여부 추가 전: " + List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getLike_InClude_Me() );

                    // 투표한 항목에 '나의 투표여부'를 true로 바꾼다.
                    List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).setLike_InClude_Me("true");

                    Log.e(TAG, "onClick: 나의 투표 여부 추가 후: " + List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getLike_InClude_Me() );

                    // 리스트에 '나의 투표여부'를 false로 업데이트
                    List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).setLike_InClude_Me("true");

                    // 투표 취소 이미지로 바꾸기
                    Suggestion_Detail_Vote_Icon.setImageResource(R.drawable.baseline_how_to_vote_white_48);

                    Toast.makeText(Activity_Suggestion_Detail.this, "투표했습니다.", Toast.LENGTH_SHORT).show();

                    // todo: 서버에 투표여부 업로드
                    UpDateMyVote(SuggestionDetailInfo, getId);
                }
                else
                {
                    Log.e(TAG, "onClick: 투표취소 " );

                    // 투표 취소하기
                    Log.e(TAG, "onClick: 투표 취소 전 카운트 : " + List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getSuggestion_Like() );
                    int Vote_Count = Integer.parseInt(List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getSuggestion_Like());

                    // 투표 수 한 칸 감소
                    Vote_Count--;

                    Log.e(TAG, "onClick: 투표 취소 후 카운트: " + Vote_Count );
                    Suggestion_Detail_Like_Count.setText(Vote_Count + "명");

                    Log.e(TAG, "onClick: 나의 투표 취소 전: " + List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getLike_InClude_Me() );

                    // 리스트에 투표 수 내리기
                    List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).setSuggestion_Like(String.valueOf(Vote_Count));

                    // 투표 취소한 항목에 '나의 투표여부'를 false로 바꾼다.
                    List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).setLike_InClude_Me("false");
                    Log.e(TAG, "onClick: 나의 투표 취소 후: " + List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).getLike_InClude_Me() );

                    // 리스트에 '나의 투표여부'를 false로 업데이트
                    List_Suggestion.get(Integer.parseInt(SuggestionArrayListIndex)).setLike_InClude_Me("false");

                    // 투표 이미지로 바꾸기
                    Suggestion_Detail_Vote_Icon.setImageResource(R.drawable.outline_how_to_vote_white_48);

                    Toast.makeText(Activity_Suggestion_Detail.this, "투표를 취소합니다.", Toast.LENGTH_SHORT).show();

                    // todo: 서버에 투표여부 업로드
                    UpDateMyVote(SuggestionDetailInfo, getId);
                }
            }
        });

        // todo: 리사이클러뷰
        List_Opinion = new ArrayList<>();

        // 리사이클러뷰
        mRecyclerView = findViewById(R.id.Suggestion_Opinion_RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // todo: 인증방법 제안 불러오기
        getOpinionList();

        // todo: 인증방법 제안 글쓰기 버튼
        Suggestion_Opinion_Write.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Activity_Suggestion_Detail.this, Activity_Suggestion_Opinion_Write.class);
                startActivity(intent);
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
                                Toast.makeText(Activity_Suggestion_Detail.this, "투표함", Toast.LENGTH_SHORT).show();
                            }

                            else if (success.equals("deleteVote"))
                            {
                                Toast.makeText(Activity_Suggestion_Detail.this, "투표 취소됨", Toast.LENGTH_SHORT).show();
                            }

//                            else
//                            {
//                                Toast.makeText(Activity_Suggestion_Detail.this, "문제발생.", Toast.LENGTH_SHORT).show();
//                                Log.e(TAG, "onResponse: response: " + response);
//                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(Activity_Suggestion_Detail.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString() );
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_Suggestion_Detail.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Activity_Suggestion_Detail.this);
        requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
    }

    // todo: 인증방법 제안 불러오기
    private void getOpinionList()
    {
        Log.e(TAG, "인증방법 제안 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface OpinionList = retrofit.create(ApiInterface.class);

        // defining the call todo: 글 번호에 포함된 인증방법 제안 글 목록 불러오기
        Call<List<Item_Opinion>> Opinion_Call = OpinionList.getOpinionList(SuggestionDetailInfo);
        Log.e(TAG, "getOpinionList: 현재 주제제안 글 번호: " + SuggestionDetailInfo );

        // 인증방법 제안 http 요청하기
        Opinion_Call.enqueue(new Callback<List<Item_Opinion>>()
        {
            @Override
            public void onResponse(Call<List<Item_Opinion>> call, Response<List<Item_Opinion>> response)
            {
                List_Opinion = response.body();

                // 값 확인하기
                for (int i = 0; i < List_Opinion.size(); i++)
                {
                    Log.e(TAG, "onResponse: 댓글 인덱스: " + List_Opinion.get(i).getWake_SO_No());
                    Log.e(TAG, "onResponse: 이 인증방법 제안 글이 소속된 글: " + List_Opinion.get(i).getWake_SO_AS_No());
                    Log.e(TAG, "onResponse: 인증방법 제안 글 내용: " + List_Opinion.get(i).getWake_SO_Opinion());
                }

                // 서버에서 응답받은 값 세팅
                opinion_adapter = new Opinion_Adapter(Activity_Suggestion_Detail.this, List_Opinion);
                mRecyclerView.setAdapter(opinion_adapter);
            }

            @Override
            public void onFailure(Call<List<Item_Opinion>> call, Throwable t)
            {
                Toast.makeText(Activity_Suggestion_Detail.this, "문제발생", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: 문제발생: " + t.toString());
            }
        });
    }

    // todo: 인증방법 제안 (댓글) 어댑터
    private class Opinion_Adapter extends RecyclerView.Adapter<Opinion_Adapter.Opinion_Adapter_ViewHolder>
    {
        private Context mContext;

        public Opinion_Adapter(Context context, List<Item_Opinion> item_opinions)
        {
            mContext = context;
            List_Opinion = item_opinions;
        }

        @NonNull
        @Override
        public Opinion_Adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_suggestion_user_opinion, viewGroup, false);

            return new Opinion_Adapter_ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Opinion_Adapter_ViewHolder opinion_adapter_viewHolder, int i)
        {
//            // 아이템 클래스에 담긴 값 꺼내기
//            final Item_Suggestion currentItem = List_Suggestion.get(i);
            final Item_Opinion currentItem = List_Opinion.get(i);

            // 댓글 번호
            currentItem.getWake_SO_No();

            // 주제 번호
            currentItem.getWake_SO_AS_No();

            // todo: 인증방법 제안 내용
            currentItem.getWake_SO_Opinion();

            // 값 확인
            Log.e(TAG, "onBindViewHolder: 인증방법 제안 내용" );
            Log.e(TAG, "onBindViewHolder: getWake_SO_No: " + currentItem.getWake_SO_No() );
            Log.e(TAG, "onBindViewHolder: getWake_SO_AS_No: " + currentItem.getWake_SO_AS_No() );
            Log.e(TAG, "onBindViewHolder: getWake_SO_Opinion: " + currentItem.getWake_SO_Opinion() );

            // 인증방법 제안내용 세팅
            opinion_adapter_viewHolder.item_Opinion_Content.setText(currentItem.getWake_SO_Opinion());
        }

        @Override
        public int getItemCount()
        {
            return List_Opinion.size();
        }

        public class Opinion_Adapter_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view;
            public TextView item_Opinion_Content;

            public Opinion_Adapter_ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                view = itemView;
                item_Opinion_Content = itemView.findViewById(R.id.item_Opinion_Content);
            }
        }
    } // todo: 인증방법 제안 (댓글) 어댑터 끝

    // 맨 위 툴바 뒤로가기 눌렀을 때 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
            {
//                Intent intent = new Intent(Activity_Suggestion_Detail.this, Activity_produce_Suggestion_List.class);
//                startActivity(intent);
                Adapter_Suggestion_List.notifyDataSetChanged();

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

        Adapter_Suggestion_List.notifyDataSetChanged();
//        Intent intent = new Intent(Activity_Suggestion_Detail.this, Activity_produce_Suggestion_List.class);
//        startActivity(intent);

        finish();
    }
}
