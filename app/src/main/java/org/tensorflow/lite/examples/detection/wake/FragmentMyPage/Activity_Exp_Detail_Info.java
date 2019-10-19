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
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Activity_Project_Detail_Page;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.Fragment_Search;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_EXP_INFO_MY_ABILITY;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_EXP_INFO_MY_ASSET;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_EXP_INFO_MY_HEALTH;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_EXP_INFO_MY_HOBBY;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_EXP_INFO_MY_LIFE;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_EXP_INFO_MY_RELATION;

public class Activity_Exp_Detail_Info extends AppCompatActivity
{
    private TextView
            exp_detail_ability          // 역량
            , exp_detail_health         // 건강
            , exp_detail_relation       // 관계
            , exp_detail_hobby // 취미
            , exp_detail_life  // 생활
            , exp_detail_asset // 자산
    ;

    private RecyclerView exp_detail_recycler_view;
    private List<Item_getExpRecordList> mList;
    private getExpRecordData getExpRecordData;

    private String TAG = "Activity_Exp_Detail_Info";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_detail_info);

        // View Find
        exp_detail_ability = findViewById(R.id.exp_detail_ability);
        exp_detail_health = findViewById(R.id.exp_detail_health);
        exp_detail_relation = findViewById(R.id.exp_detail_relation);
        exp_detail_hobby = findViewById(R.id.exp_detail_hobby);
        exp_detail_life = findViewById(R.id.exp_detail_life);
        exp_detail_asset = findViewById(R.id.exp_detail_asset);
        exp_detail_recycler_view = findViewById(R.id.exp_detail_recycler_view);

        // Value Set
        exp_detail_ability.setText(GET_EXP_INFO_MY_ABILITY);
        exp_detail_health.setText(GET_EXP_INFO_MY_HEALTH);
        exp_detail_relation.setText(GET_EXP_INFO_MY_RELATION);
        exp_detail_hobby.setText(GET_EXP_INFO_MY_HOBBY);
        exp_detail_life.setText(GET_EXP_INFO_MY_LIFE);
        exp_detail_asset.setText(GET_EXP_INFO_MY_ASSET);

        // Recycler View
        mList = new ArrayList<>();

        exp_detail_recycler_view.setHasFixedSize(true);
        exp_detail_recycler_view.setLayoutManager(new LinearLayoutManager(Activity_Exp_Detail_Info.this));

        // todo 경험치 획득 기록 불러오기
        requestExpRecord();
    }

    // todo 경험치 획득 기록 불러오기
    private void requestExpRecord()
    {
        Log.e(TAG, "requestExpRecord: 경험치 획득 기록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface MygetExpRecordList = retrofit.create(ApiInterface.class);

        Call<List<Item_getExpRecordList>> listCall = MygetExpRecordList.getExpRecordList(getId);

        listCall.enqueue(new Callback<List<Item_getExpRecordList>>()
        {
            @Override
            public void onResponse(Call<List<Item_getExpRecordList>> call, Response<List<Item_getExpRecordList>> response)
            {
                mList = response.body();

                for (int i = 0; i < mList.size(); i++)
                {
                    Log.e(TAG, "onResponse: " + mList.get(i).getWake_Info_Title() );

                }

                getExpRecordData = new getExpRecordData(Activity_Exp_Detail_Info.this, mList);
                exp_detail_recycler_view.setAdapter(getExpRecordData);
            }

            @Override
            public void onFailure(Call<List<Item_getExpRecordList>> call, Throwable t)
            {
                Toast.makeText(Activity_Exp_Detail_Info.this, "문제발생" + t.toString(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: 문제 발생: " + t.toString() );
            }
        });
    }

    public class getExpRecordData extends RecyclerView.Adapter<getExpRecordData.ExpViewHolder>
    {
        private Context mContext;

        public getExpRecordData(Context context, List<Item_getExpRecordList> item_getExpRecordLists)
        {
            mContext = context;
            mList = item_getExpRecordLists;
        }

        @NonNull
        @Override
        public ExpViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_exp_record, viewGroup, false);

            return new ExpViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpViewHolder expViewHolder, int i)
        {
            final Item_getExpRecordList currentItem = mList.get(i);

            // 썸네일
            Picasso.get().load(currentItem.getWake_Info_ThumbImages()).
            // memoryPolicy(MemoryPolicy.NO_CACHE).
            placeholder(R.drawable.logo_2).
            // networkPolicy(NetworkPolicy.NO_CACHE).
            into(expViewHolder.exp_record_challenge_thumb);

            expViewHolder.exp_record_challenge_title.setText(currentItem.getWake_Info_Title());

            expViewHolder.exp_record_get_exp.setText("(" + currentItem.getWake_Evidence_getExpType() + ") " + currentItem.getWake_Evidence_getExp() + "점");

            expViewHolder.exp_record_get_exp_date.setText(currentItem.getWake_Evidence_Date());

            expViewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Fragment_Search.GET_PROJECT_INDEX = currentItem.getWake_Evidence_JoinProject_index();

                    Intent intent = new Intent(mContext, Activity_Project_Detail_Page.class);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mList.size();
        }

        public class ExpViewHolder extends RecyclerView.ViewHolder
        {
            public ImageView exp_record_challenge_thumb;
            public TextView
                    exp_record_challenge_title
                    , exp_record_get_exp
                    , exp_record_get_exp_date
            ;

            public View view;

            public ExpViewHolder(@NonNull View itemView)
            {
                super(itemView);

                exp_record_challenge_thumb = itemView.findViewById(R.id.exp_record_challenge_thumb);
                exp_record_challenge_title = itemView.findViewById(R.id.exp_record_challenge_title);
                exp_record_get_exp = itemView.findViewById(R.id.exp_record_get_exp);
                exp_record_get_exp_date = itemView.findViewById(R.id.exp_record_get_exp_date);
                view = itemView;
            }
        }
    }
}
