package org.tensorflow.lite.examples.detection.wake.FragmentFeed;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.ApiClient;
import org.tensorflow.lite.examples.detection.wake.ApiInterface;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Activity_Project_Detail_Page;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.OnBackPressedListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Feed extends Fragment implements OnBackPressedListener
{

    // 태그 선언
    private String TAG = "Fragment_Feed";

    // Context 선언
    private View View_Fragment_Feed; // 프래그먼트 뷰
    private Context mContext; // 컨텍스트

    // 리사이클러뷰
    private RecyclerView mRecyclerView;
    private List<Item_Feed_List> mListFeed;
    private Feed_Adapter mAdapter;

    public Fragment_Feed()
    {
        // Required empty public constructor
    }

    public static String
            GET_COMPLAIN_USER               // 신고 당할 유저
            , GET_COMPLAIN_CERTI_INDEX      // 신고할 인증샷 인덱스
            ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Fragment_Feed = inflater.inflate(R.layout.fragment_feed, container, false);
        // Inflate the layout for this fragment

        mContext = getActivity().getApplicationContext();

        // 리사이클러뷰 세팅하기
        mRecyclerView = View_Fragment_Feed.findViewById(R.id.fra_feed_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // 리스트 초기화 해주기
        mListFeed = new ArrayList<>();

        // todo: 유저들의 인증샷 불러오기
        getFeedList();

        return View_Fragment_Feed;
    }

    // todo: 유저들의 인증샷 불러오기
    private void getFeedList()
    {
        Log.e(TAG, "getProjectList(): 인증샷 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface feedListRequest = retrofit.create(ApiInterface.class);
        Call<List<Item_Feed_List>> listCall = feedListRequest.getFeedList(getId);

        listCall.enqueue(new Callback<List<Item_Feed_List>>()
        {
            @Override
            public void onResponse(Call<List<Item_Feed_List>> call, Response<List<Item_Feed_List>> response)
            {
                mListFeed = response.body();

                // 넘어온 값 확인하기
                for (int i = 0; i < mListFeed.size(); i++)
                {
                    Log.e(TAG, "list call onResponse = " + response.body().get(i).getWake_Info_Title());
                }

                mAdapter = new Feed_Adapter(mContext, mListFeed);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Item_Feed_List>> call, Throwable t)
            {
                Toast.makeText(mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    public class Feed_Adapter extends RecyclerView.Adapter<Feed_Adapter.ViewHolder>
    {

        public Feed_Adapter(Context context, List<Item_Feed_List> item_feed_lists)
        {
            mContext = context;
            mListFeed = item_feed_lists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_feed, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
        {
            final Item_Feed_List currentItem = mListFeed.get(i);

            Log.e(TAG, "onBindViewHolder: Wake_Evidence_File_Type: " + currentItem.getWake_Evidence_File_Type() );
            Log.e(TAG, "onBindViewHolder: Wake_Evidence_No: " + currentItem.getWake_Evidence_No() );

            // 유저 프로필 사진
            Picasso.get().load(currentItem.getPhoto()).
                    placeholder(R.drawable.logo_2).
                    into(viewHolder.feed_user_photo);

            viewHolder.feed_user_name.setText(currentItem.getName());
            viewHolder.feed_title.setText(currentItem.getWake_Info_Title());
            viewHolder.feed_certi_type.setText(currentItem.getWake_Evidence_File_Type());

//            viewHolder.feed_like_count.setText();
//            viewHolder.feed_declaration_count.setText();

//            viewHolder.feed_user_info_button
//            viewHolder.feed_project_info_button

            // 인증샷
            Picasso.get().load(currentItem.getWake_Evidence_File()).
            placeholder(R.drawable.logo_2).
            into(viewHolder.feed_project_thumb);

            Log.e(TAG, "onBindViewHolder: 인증샷: " + currentItem.getWake_Evidence_File());

            if (currentItem.getWake_Evidence_File_Type().equals("distance_measurement"))
            {
                viewHolder.feed_project_certi_date.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.feed_project_certi_date.setVisibility(View.VISIBLE);
                viewHolder.feed_project_certi_date.setText(currentItem.getWake_Evidence_Date());
            }


            Log.e(TAG, "onBindViewHolder: getId: " + getId );
            Log.e(TAG, "onBindViewHolder: currentItem.getId(): " + currentItem.getId() );

            // 챌린지 상세정보로 이동하기
            viewHolder.feed_challenge_info_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, Activity_Project_Detail_Page.class);
                    Fragment_Search.GET_PROJECT_INDEX = currentItem.getWake_Evidence_JoinProject_index();
                    startActivity(intent);
                }
            });

            // 내 아이디는 신고버튼 숨기기
            if (Integer.parseInt(getId) == Integer.parseInt(currentItem.getId()))
            {
                Log.e(TAG, "onBindViewHolder: GONE" );
//                viewHolder.feed_complain_button.setEnabled(false);
                viewHolder.feed_complain_button.setVisibility(View.GONE);
                viewHolder.feed_like_count.setVisibility(View.INVISIBLE);
            }

            // 나의 신고 권한이 없을 경우 신고 권한 없음 알림 띄우기
            // (부정 신고로 인해 관리자가 신고권한 박탈한 상태임)
            else if (currentItem.getDisposition_of_false_reports().equals("Your_false_report_was_retrieved"))
            {
                viewHolder.feed_complain_button.setVisibility(View.GONE);
                viewHolder.feed_like_count.setVisibility(View.VISIBLE);
            }

            else
            {
                viewHolder.feed_like_count.setVisibility(View.INVISIBLE);
                viewHolder.feed_complain_button.setVisibility(View.VISIBLE);
                Log.e(TAG, "onBindViewHolder: VISIBLE" );
//                viewHolder.feed_complain_button.setEnabled(true);

                // todo: 신고 버튼
                viewHolder.feed_complain_button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // 데이터를 다이얼로그로 보내는 코드
                        Bundle args = new Bundle();
                        args.putString("key", "신고합니다");

                        // --------------------------------------- //

                        // 신고 당할 유저
                        GET_COMPLAIN_USER = currentItem.getId();

                        // 신고할 인증샷 인덱스
                        GET_COMPLAIN_CERTI_INDEX = currentItem.getWake_Evidence_No();

                        // 커스텀 프래그먼트 클래스 선언
                        FragmentDialogComplain dialog = new FragmentDialogComplain();

                        // 커스텀 프래그먼트로 데이터 전달
                        dialog.setArguments(args);

                        // 다이얼로그 실행하기 (tag: 다이얼로그로 보낼 값의 태그)
                        dialog.show(getActivity().getSupportFragmentManager(),"key");
                    }
                });
            }
        }

        @Override
        public int getItemCount()
        {
            return mListFeed.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView
                    feed_user_name          // 유저 이름
                    , feed_title             // 프로젝트 타이틀
                    , feed_certi_type        // 인증 유형
                    , feed_like_count        // 좋아요 누적수
                    , feed_project_certi_date    // 인증한 날짜, 시간
                    , feed_declaration_count // 신고 누적수
                    ;

            public LinearLayout feed_challenge_info_button; // 유저 정보 자세히 보기 버튼
            public FrameLayout feed_project_info_button; // 프로젝트 정보 자세히 보기 버튼

            public ImageView
                    feed_project_thumb          // 프로젝트 썸네일
                    , feed_like_button           // 좋아요 버튼
                    ;

            public CircleImageView
                    feed_complain_button // 신고 버튼
                    , feed_user_photo    // 유저 프로필사진
                    ;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                feed_user_name = itemView.findViewById(R.id.feed_user_name);
                feed_title = itemView.findViewById(R.id.feed_title);
                feed_certi_type = itemView.findViewById(R.id.feed_certi_type);
                feed_like_count = itemView.findViewById(R.id.feed_like_count);
                feed_declaration_count = itemView.findViewById(R.id.feed_declaration_count);

//                feed_user_info_button = itemView.findViewById(R.id.feed_user_info_button);
                feed_challenge_info_button = itemView.findViewById(R.id.feed_challenge_info_button);

                feed_project_thumb = itemView.findViewById(R.id.feed_project_thumb);
                feed_project_certi_date = itemView.findViewById(R.id.feed_project_certi_date);
//                feed_like_button = itemView.findViewById(R.id.feed_like_button);

                feed_complain_button = itemView.findViewById(R.id.feed_complain_button);
                feed_user_photo = itemView.findViewById(R.id.feed_user_photo);
            }
        }
    }

    @Override
    public void onBackPressed()
    {

    }
}
