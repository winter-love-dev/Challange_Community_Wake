package org.tensorflow.lite.examples.detection.wake.FragmentMyPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.ApiClient;
import org.tensorflow.lite.examples.detection.wake.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Complain_List extends AppCompatActivity
{
    static String TAG = "Activity_Complain_List";

    private static RecyclerView mRecyclerView;
    public static List<Item_Complain_List> mList;
    private static AdapterClameList mAdapter;

    public static Context contextComplain_List;

    public static String
                    GET_COMPLAIN_TITLE          // 챌린지 제목
            ,       GET_COMPLAIN_SUBSCRIPT      // 챌린지 인증방법
            ,       GET_COMPLAIN_THUMB          // 챌린지 썸네일
            ,       GET_COMPLAIN_CONTENT        // 신고 내용
            ,       GET_COMPLAIN_DATE           // 신고한 날짜
            ,       GET_COMPLAIN_USER_ID        // 신고한 유저 아이디
            ,       GET_COMPLAIN_USER_NAME      // 신고한 유저 이름
            ,       GET_COMPLAIN_USER_PHOTO     // 신고한 유저 사진
            ,       GET_COMPLAIN_DISPOSE_USER   // 신고 접수된 유저
            ,       GET_COMPLAIN_DISPOSE_NAME   // 신고 접수된 유저 이름
            ,       GET_COMPLAIN_DISPOSE_PHOTO  // 신고 접수된 유저 사진
            ,       GET_COMPLAIN_TABLE_INDEX    // 신고 테이블 인덱스
            ,       GET_COMPLAIN_JOIN_INDEX     // 신고 접수된 유저가 참여중인 챌린지 인덱스
            ,       GET_COMPLAIN_CERTI__INDEX   // 인증샷 테이블 인덱스
            ,       GET_COMPLAIN_DETAIL_INFO_INDEX // 챌린지 상세정보 인덱스
            ;

//                    * 1. 신고 테이블 인덱스
//                    * 2. 챌린지 진행정보 인덱스
//                    * 3. 신고한 유저 인덱스
//                    * 4. 신고 접수된 유저 인덱스

            /*
                GET_COMPLAIN_TITLE
                GET_COMPLAIN_SUBSCRIPT
                GET_COMPLAIN_THUMB
                GET_COMPLAIN_CONTENT
                GET_COMPLAIN_DATE
                GET_COMPLAIN_USER_ID
                GET_COMPLAIN_USER_NAME
                GET_COMPLAIN_USER_PHOTO
                GET_COMPLAIN_DISPOSE_USER
                GET_COMPLAIN_DISPOSE_NAME
                GET_COMPLAIN_DISPOSE_PHOTO
            */


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_list);

        contextComplain_List = getApplicationContext();

        // 리사이클러뷰 세팅하기/
        mRecyclerView = findViewById(R.id.complain_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 리스트 초기화 해주기
        mList = new ArrayList<>();

        // todo: 접수된 신고목록 불러오기
        getFeedList();
    }

    // todo: 접수된 신고목록 불러오기
    public static void getFeedList()
    {
        Log.e(TAG, "getProjectList(): 인증샷 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface ComplainListRequest = retrofit.create(ApiInterface.class);
        Call<List<Item_Complain_List>> listCall = ComplainListRequest.getComplainList("id");

        listCall.enqueue(new Callback<List<Item_Complain_List>>()
        {
            @Override
            public void onResponse(Call<List<Item_Complain_List>> call, Response<List<Item_Complain_List>> response)
            {
                mList = response.body();

//                넘어온 값 확인하기
//                for (int i = 0; i < mList.size(); i++)
//                {
//                    // Log.e(TAG, "list call onResponse = " + response.body().get(i).getWake_Info_Title());
//                }

                mAdapter = new AdapterClameList(contextComplain_List, mList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Item_Complain_List>> call, Throwable t)
            {
                Toast.makeText(contextComplain_List, "리스트 로드 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    public static class AdapterClameList extends RecyclerView.Adapter<AdapterClameList.ViewHolder>
    {
        Context mContext;

        public AdapterClameList(Context context, List<Item_Complain_List> List)
        {
            mContext = context;
            mList = List;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_complain_list, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
        {
            final Item_Complain_List currentItem = mList.get(i);

            viewHolder.complain_challenge_title.setText(currentItem.getWake_Info_Title());
            viewHolder.complain_date.setText(currentItem.getWake_Complain_Date());

            // 신고내용 간략하게 전달
            if (currentItem.getWake_Complain_Content().equals("Complain_This_Shot"))
            {
                viewHolder.complain_challenge_content.setText("부정행위 검토");

                GET_COMPLAIN_CONTENT = "부정행위 검토";
            }
            else
            {
                viewHolder.complain_challenge_content.setText("부정행위 모두 검토");

                GET_COMPLAIN_CONTENT = "부정행위 모두 검토";
            }

            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Complain_Content(): " + currentItem.getWake_Complain_Content() );

            // 인증샷
            Picasso.get().load(currentItem.getWake_Info_ThumbImages()).
                    placeholder(R.drawable.logo_2).
                    into(viewHolder.complain_thumb);

            viewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(contextComplain_List, Activity_Complain_detail.class);

                    GET_COMPLAIN_TITLE = currentItem.getWake_Info_Title();
                    GET_COMPLAIN_SUBSCRIPT = currentItem.getWake_Info_Subscript();
                    GET_COMPLAIN_THUMB = currentItem.getWake_Info_ThumbImages();

                    // 신고내용 간략하게 전달
                    if (currentItem.getWake_Complain_Content().equals("Complain_This_Shot"))
                    {
                        GET_COMPLAIN_CONTENT = "부정행위 검토";
                    }
                    else
                    {
                        GET_COMPLAIN_CONTENT = "부정행위 모두 검토";
                    }

                    GET_COMPLAIN_DATE = currentItem.getWake_Complain_Date();

                    GET_COMPLAIN_USER_ID = currentItem.getWake_Complain_User();
                    GET_COMPLAIN_USER_NAME = currentItem.getWake_Complain_User_name();
                    GET_COMPLAIN_USER_PHOTO = currentItem.getWake_Complain_User_photo();

                    GET_COMPLAIN_DISPOSE_USER = currentItem.getWake_Complain_Dispose_User();
                    GET_COMPLAIN_DISPOSE_NAME = currentItem.getWake_Complain_Dispose_User_Name();
                    GET_COMPLAIN_DISPOSE_PHOTO = currentItem.getWake_Complain_Dispose_User_Photo();

                    GET_COMPLAIN_TABLE_INDEX  = currentItem.getWake_Complain_Index();           // 신고 테이블 인덱스
                    GET_COMPLAIN_JOIN_INDEX = currentItem.getWake_Evidence_JoinProject_index(); // 신고 접수된 유저가 참여중인 챌린지 인덱스

                    GET_COMPLAIN_CERTI__INDEX = currentItem.getWake_Complain_Certi_Index();      // 인증샷 인덱스
                    GET_COMPLAIN_DETAIL_INFO_INDEX =  currentItem.getWake_Info_No();            // 챌린지 상세정보 인덱스

                    // 로그로 값 검사
                    Log.e(TAG, "onClick: GET_COMPLAIN_TITLE: " + GET_COMPLAIN_TITLE );
                    Log.e(TAG, "onClick: GET_COMPLAIN_SUBSCRIPT: " + GET_COMPLAIN_SUBSCRIPT );
                    Log.e(TAG, "onClick: GET_COMPLAIN_THUMB: " + GET_COMPLAIN_THUMB );
                    Log.e(TAG, "onClick: GET_COMPLAIN_CONTENT: " + GET_COMPLAIN_CONTENT );
                    Log.e(TAG, "onClick: GET_COMPLAIN_DATE: " + GET_COMPLAIN_DATE );
                    Log.e(TAG, "onClick: GET_COMPLAIN_USER_ID: " + GET_COMPLAIN_USER_ID );
                    Log.e(TAG, "onClick: GET_COMPLAIN_USER_NAME: " + GET_COMPLAIN_USER_NAME );
                    Log.e(TAG, "onClick: GET_COMPLAIN_USER_PHOTO: " + GET_COMPLAIN_USER_PHOTO );
                    Log.e(TAG, "onClick: GET_COMPLAIN_DISPOSE_USER: " + GET_COMPLAIN_DISPOSE_USER );
                    Log.e(TAG, "onClick: GET_COMPLAIN_DISPOSE_NAME: " + GET_COMPLAIN_DISPOSE_NAME );
                    Log.e(TAG, "onClick: GET_COMPLAIN_DISPOSE_PHOTO: " + GET_COMPLAIN_DISPOSE_PHOTO );

                    contextComplain_List.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView complain_challenge_title
                    ,       complain_challenge_content
                    ,       complain_date
                    ;

            public ImageView complain_thumb;

            public View view;


            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                complain_challenge_title = itemView.findViewById(R.id.complain_challenge_title);
                complain_challenge_content = itemView.findViewById(R.id.complain_challenge_content);
                complain_date = itemView.findViewById(R.id.complain_date);
                complain_thumb = itemView.findViewById(R.id.complain_thumb);

                view = itemView;
            }
        }
    }
}
