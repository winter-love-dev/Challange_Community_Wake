package org.tensorflow.lite.examples.detection.wake.FragmentProof;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.ApiClient;
import org.tensorflow.lite.examples.detection.wake.ApiInterface;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.OnBackPressedListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Proof extends Fragment implements OnBackPressedListener
{
    // 태그 선언
    private String TAG = "Fragment_Proof";

    // Context 선언
    private View View_Fragment_Proof; // 프래그먼트 뷰
    private Context mContext; // 컨텍스트

    // 리사이클러뷰
    private RecyclerView mRecyclerView;
    public static List<Item_Proof_List> mListProof;
    public static Proof_Adapter mAdapter;

    public static String
//            GET_PROOF_CONTENT // 인증할 프로젝트의 컨텐츠 (사진 경로, VOD 경로)
            GET_PROOF_INDEX     // 인증할 프로젝트의 인덱스
            , GET_PROOF_TYPE    // 인증할 프로젝트의 인증 방법
            , GET_PROOF_TITLE   // 인증할 프로젝트의 제목
            , GET_PROOF_THUMB   // 인증할 프로젝트의 썸네일
            , GET_PROOF_DAY     // 인증할 프로젝트의 인증 기간
            , GET_PROOF_TIME    // 인증할 프로젝트의 인증 시간
            , GET_PROOF_JOIN_PRICE  // 참가비
            , GET_PROOF_CERTI_COUNT // 인증 해야될 횟수
            , GET_PROOF_MY_CERTI_COUNT // 내가 인증한 횟수
            , GET_PROOF_CATEGORY // 프로젝트 카테고리
            , GET_REWARD         // 1회 인증시 얻게될 리워드
            , GET_JOIN_INDEX     // 프로젝트 상세정보 인덱스
            , GET_JOIN_MEMBER_INDEX // 참가자 명단 인덱스
            , GET_PROGRESS_INDEX // 프로젝트 진행정보
            , GET_PROOF_LIST_INDEX
            , GET_PROOF_SUBSCRIPT // 챌린지 설명
            ;

    public Fragment_Proof()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View_Fragment_Proof = inflater.inflate(R.layout.fragment__proof, container, false);

        mContext = getActivity().getApplicationContext();

        // 리사이클러뷰 세팅하기
        mRecyclerView = View_Fragment_Proof.findViewById(R.id.fra_proof_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // 리스트 초기화 해주기
        mListProof = new ArrayList<>();

        // todo: 인증 목록 불러오기
        getProofList();

        return View_Fragment_Proof;
    }

    // todo: 인증 목록 불러오기
    private void getProofList()
    {
        Log.e(TAG, "getProjectList(): 프로젝트 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiInterface proofListRequest = retrofit.create(ApiInterface.class);
        Call<List<Item_Proof_List>> listcall = proofListRequest.getProofList(getId);

        listcall.enqueue(new Callback<List<Item_Proof_List>>()
        {
            @Override
            public void onResponse(Call<List<Item_Proof_List>> call, Response<List<Item_Proof_List>> response)
            {
                mListProof = response.body();

                // 넘어온 값 확인하기
                for (int i = 0; i < mListProof.size(); i++)
                {
                    Log.e(TAG, "list call onResponse = " + response.body().get(i).getWake_Info_Title());
                }

                mAdapter = new Proof_Adapter(mContext, mListProof);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Item_Proof_List>> call, Throwable t)
            {
//                Toast.makeText(mContext, "리스트 로드 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed()
    {

    }

    public class Proof_Adapter extends RecyclerView.Adapter<Proof_Adapter.ViewHolder>
    {

        public Proof_Adapter(Context context, List<Item_Proof_List> item_proof_lists)
        {
            mContext = context;
            mListProof = item_proof_lists;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_proof_list, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i)
        {
            final Item_Proof_List currentItem = mListProof.get(i);

            Log.e(TAG, "onBindViewHolder: 인증 필요한 횟수: " + currentItem.getWake_Progress_Certi_Count());
            Log.e(TAG, "onBindViewHolder: 나의 인증 횟수: " + currentItem.getWake_JoinM_Your_CertiCount());

            // 서버에서 인증이 끝난 챌린지 항목을 확인하고 반환하지만
            // 혹시 인증이 끝난 항목이 클라이언트로 넘어올 경우,
            // 인증이 끝난 챌린지 항목은 인증목록에서 삭제하기
//            if (Integer.parseInt(currentItem.getWake_Progress_Certi_Count()) <= Integer.parseInt(currentItem.getWake_JoinM_Your_CertiCount()))
//            {
//                mListProof.remove(i);
//                Log.e(TAG, "onBindViewHolder: remove 됨 : "  + i + "번 인덱스");
//            }

            // 썸네일
            Picasso.get().load(currentItem.getWake_Info_ThumbImages()).
                    memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.logo_2).
                    networkPolicy(NetworkPolicy.NO_CACHE).
                    into(viewHolder.proof_list_thumb);

            // 프로젝트 제목
            viewHolder.proof_list_title.setText(currentItem.getWake_Info_Title());

            // 프로젝트 진행기간
            viewHolder.proof_list_day.setText(currentItem.getWake_Progress_Start_Date() + "~" + currentItem.getWake_Progress_End_Date());

            // 인증 가능한 시간
            viewHolder.proof_list_time.setText(currentItem.getWake_Progress_Certi_Start_Time() + "~" + currentItem.getWake_Progress_Certi_End_Time());

            /** todo: 1. 1회 인증시 얻게될 보상금 = 참가비 / 인증 해야될 횟수 (소수점 제외)
             * */

            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Progress_Certi_Count(): " + currentItem.getWake_Progress_Certi_Count() );
            int joinPrice = Integer.parseInt(currentItem.getWake_Info_Price() + "0000");
            int Reword = joinPrice / Integer.parseInt(currentItem.getWake_Progress_Certi_Count());
            GET_REWARD = String.valueOf(Reword);

            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Info_Price(): " + currentItem.getWake_Info_Price());
            Log.e(TAG, "onBindViewHolder: GET_REWARD: " + GET_REWARD);

            // 이번 인증으로 획득할 리워드
            viewHolder.proof_list_reword.setText(GET_REWARD + "원");

            Log.e(TAG, "onBindViewHolder: currentItem.getWake_Info_Certi_Way(): " + currentItem.getWake_Info_Certi_Way());

            Log.e(TAG, "onBindViewHolder: GET_PROOF_JOIN_PRICE: " + currentItem.getWake_Info_Price());
            Log.e(TAG, "onBindViewHolder: GET_PROOF_CATEGORY: " + currentItem.getWake_info_category());
            Log.e(TAG, "onBindViewHolder: GET_JOIN_INDEX: " + currentItem.getWake_JoinM_WakeInfo());

            Log.e(TAG, "onBindViewHolder: Wake_Dispose_Result: " + currentItem.getWake_Dispose_Result());

            /**
             * 인증 권한 없음: Found_Your_Penalty_Disposition
             *              (누군가 해당 유저가 참가중인 챌린지의 인증샷을 부정 신고했고,
             *              관리자는 부정 행위가 맞다고 판단. 챌린지 참가 불가)
             *
             * 인증 권한 있음: Not_Found_Your_Penalty_Disposition
             * */

            // 인증 권한 없는 목록 클릭 막기
            if (currentItem.getWake_Dispose_Result().equals("Found_Your_Penalty_Disposition"))
            {
                viewHolder.proof_list_proof_permission.setVisibility(View.VISIBLE);
            }

            //
            else
            {
                viewHolder.proof_list_proof_permission.setVisibility(View.GONE);
                viewHolder.view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(mContext, Activity_Proof_Ready.class);

                        // 제목
                        GET_PROOF_TITLE = currentItem.getWake_Info_Title();

                        // 진행기간
                        GET_PROOF_DAY = currentItem.getWake_Progress_Start_Date() + "~" + currentItem.getWake_Progress_End_Date();

                        // 인증시간
                        GET_PROOF_TIME = currentItem.getWake_Progress_Certi_Start_Time() + "~" + currentItem.getWake_Progress_Certi_End_Time();

                        // 썸네일
                        GET_PROOF_THUMB = currentItem.getWake_Info_ThumbImages();

                        // 인증 방법
                        GET_PROOF_TYPE = currentItem.getWake_Info_Certi_Way();

                        // 인덱스
                        GET_PROOF_INDEX = currentItem.getWake_Progress_No();

                        // 참가비
                        GET_PROOF_JOIN_PRICE = currentItem.getWake_Info_Price();

                        // 프로젝트 카테고리
                        GET_PROOF_CATEGORY = currentItem.getWake_info_category();

                        // 프로젝트 상세정보 인덱스
                        GET_JOIN_INDEX = currentItem.getWake_JoinM_WakeInfo();

                        // 참가자 명단 인덱스
                        GET_JOIN_MEMBER_INDEX = currentItem.getWake_JoinM_No();

                        // 인증 해야될 횟수
                        GET_PROOF_CERTI_COUNT = currentItem.getWake_Progress_Certi_Count();

                        // 나의 인증 횟수
                        GET_PROOF_MY_CERTI_COUNT = currentItem.getWake_JoinM_Your_CertiCount();

                        // 챌린지 종료시 삭제할 리스트의 인덱스
                        GET_PROOF_LIST_INDEX = String.valueOf(i);

                        GET_PROOF_SUBSCRIPT = currentItem.getWake_Info_Subscript();

//                    // 프로젝트 진행정보
//                    GET_PROGRESS_INDEX = currentItem.getWake_Progress_No();

                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount()
        {
            return mListProof.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public View view;

            public TextView
                    proof_list_title, proof_list_day, proof_list_time, proof_list_reword, proof_list_proof_permission;

            public ImageView
                    proof_list_thumb;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                view = itemView;

                proof_list_title = itemView.findViewById(R.id.proof_list_title);
                proof_list_day = itemView.findViewById(R.id.proof_list_day);
                proof_list_time = itemView.findViewById(R.id.proof_list_time);
                proof_list_reword = itemView.findViewById(R.id.proof_list_reword);
                proof_list_thumb = itemView.findViewById(R.id.proof_list_thumb);
                proof_list_proof_permission = itemView.findViewById(R.id.proof_list_proof_permission);
            }
        }
    }
}
