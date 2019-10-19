package org.tensorflow.lite.examples.detection.wake.FragmentHome;


import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Search extends Fragment implements OnBackPressedListener
{
    // 태그 선언
    public static String TAG = "Fragment_Search";

    // Context 선언
    private View View_Fragment_Search; // 프래그먼트 뷰
    public static Context mContext_Fragment_Search; // 컨텍스트

    // 리사이클러뷰
    private RecyclerView mRecyclerView;
    private List<Item_Project_List> mProjectList;
    private Project_List_Adapter mProjectListAdapter;

    public static TextView
            fra_home_price      // 누적 참가인원
            ,fra_home_people    // 누적 인원
            , testbutton
            ;

    // 상세 페이지에서 조회할 정보의 인덱스
    public static String GET_PROJECT_INDEX;

    public Fragment_Search()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View_Fragment_Search = inflater.inflate(R.layout.fragment_search, container, false);
        mContext_Fragment_Search = getActivity().getApplicationContext();

        // 리사이클러뷰 세팅하기
        mRecyclerView = View_Fragment_Search.findViewById(R.id.fra_home_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext_Fragment_Search));

        fra_home_price  = View_Fragment_Search.findViewById(R.id.fra_home_price);
        fra_home_people = View_Fragment_Search.findViewById(R.id.fra_home_people);
        testbutton = View_Fragment_Search.findViewById(R.id.testbutton);

        // 리스트 초기화 해주기
        mProjectList = new ArrayList<>();

        // todo: 챌린지 목록 불러오기
        getProjectList();

        // todo: 누적 참가 횟수, 금액 불러오기
        getTotalSum();

        return View_Fragment_Search;
    }

    // todo: 누적 참가 횟수, 금액 불러오기
    public static void getTotalSum()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/getWakeTotalSum.php",
                new com.android.volley.Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "onResponse: onResponse(String response): " + response);

                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1"))
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Log.e(TAG, "onResponse: Object: " + object);

                                    fra_home_people.setText(object.getString("Wake_Sum_People") + "명");
                                    fra_home_price.setText(object.getString("Wake_Sum_Price") + "만 원");
                                }
                            }

                            if (success.equals("1"))
                            {

                            } else
                            {
                                Toast.makeText(mContext_Fragment_Search, "예약: 에러발생. 로그 확인", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e)
                        {
                            Toast.makeText(mContext_Fragment_Search, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(mContext_Fragment_Search, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: JSONException VolleyError error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                params.put("getId", getId);
                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(mContext_Fragment_Search);
        requestQueue.add(stringRequest);
    }

    // todo: 챌린지 목록 불러오기
    private void getProjectList()
    {
        Log.e(TAG, "getProjectList(): 챌린지 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiInterface projectListRequest = retrofit.create(ApiInterface.class);

        //defining the call
        Call<List<Item_Project_List>> listCall = projectListRequest.getProjectList("id");

        listCall.enqueue(new Callback<List<Item_Project_List>>()
        {
            @Override
            public void onResponse(Call<List<Item_Project_List>> call, Response<List<Item_Project_List>> response)
            {
                Log.e(TAG, "list call onResponse = " + response.body());
                Log.e(TAG, "list call onResponse = " + response.body().toString());

                mProjectList = response.body();

                // 넘어온 값 확인하기
                for (int i = 0; i < mProjectList.size(); i++)
                {
                    Log.e(TAG, "onResponse: BroadCastTitle: " + mProjectList.get(i).Wake_Info_Title);
                }

                mProjectListAdapter = new Project_List_Adapter(mContext_Fragment_Search, mProjectList);
                mRecyclerView.setAdapter(mProjectListAdapter);

            }

            @Override
            public void onFailure(Call<List<Item_Project_List>> call, Throwable t)
            {
                Toast.makeText(mContext_Fragment_Search, "리스트 로드 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed()
    {

    }

    public class Project_List_Adapter extends RecyclerView.Adapter<Project_List_Adapter.ViewHolder>
    {

        public Project_List_Adapter(Context context, List<Item_Project_List> list)
        {
            mContext_Fragment_Search = context;
            mProjectList = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext_Fragment_Search).inflate(R.layout.item_home_project_list, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
        {
            final Item_Project_List currentItem = mProjectList.get(i);

            // 썸네일
            Picasso.get().load(currentItem.getWake_Info_ThumbImages()).
//                    memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.logo_2).
//                    networkPolicy(NetworkPolicy.NO_CACHE).
                    into(viewHolder.item_home_p_thumb);
            Log.e(TAG, "onBindViewHolder: 썸네일: " + currentItem.getWake_Info_ThumbImages());

            if (currentItem.getWake_Progress_Certi_Day().equals("5day"))
            {
                // 제목
                viewHolder.item_home_p_title_and_certi_day.setText(currentItem.getWake_Info_Title() + "│평일");
            } else if (currentItem.getWake_Progress_Certi_Day().equals("2day"))
            {
                // 제목
                viewHolder.item_home_p_title_and_certi_day.setText(currentItem.getWake_Info_Title() + "│주말");
            } else if (currentItem.getWake_Progress_Certi_Day().equals("7day"))
            {
                // 제목
                viewHolder.item_home_p_title_and_certi_day.setText(currentItem.getWake_Info_Title() + "│주 7일");
            }

            // 카테고리
            viewHolder.item_home_p_category.setText(currentItem.getWake_Info_Category());
            Log.e(TAG, "onBindViewHolder: 카테고리: " + currentItem.getWake_Info_Category());

            // 총 누적금액
            viewHolder.item_home_p_total_price.setText("누적 금액: " + currentItem.getWake_Info_Total_Price() + "만 원");
            Log.e(TAG, "onBindViewHolder: 총 누적금액: " + currentItem.getWake_Info_Total_Price() + "만 원");

            // 진행기간
            viewHolder.item_home_p_start_and_end_date.setText(currentItem.getWake_Progress_Start_Date() + " ~ " + currentItem.getWake_Progress_End_Date());
            Log.e(TAG, "onBindViewHolder: 진행기간: " + currentItem.getWake_Progress_Start_Date() + " ~ " + currentItem.getWake_Progress_End_Date());

            // 챌린지 상세 페이지로 이동하기
            viewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // 조회할 인덱스 담기
                    GET_PROJECT_INDEX = currentItem.getWake_Progress_Info_No();
                    Log.e(TAG, "onClick: 조회할 인덱스 정보: " + currentItem.getWake_Progress_Info_No());

                    // 상세 페이지로 이동
                    Intent intent = new Intent(mContext_Fragment_Search, Activity_Project_Detail_Page.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mProjectList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public View view;

            public ImageView
                    item_home_p_thumb // 썸네일
                    ;

            public TextView
                    item_home_p_title_and_certi_day     // 제목, 인증 빈도수
                    , item_home_p_category              // 카테고리
                    , item_home_p_total_price           // 총 누적 금액
                    , item_home_p_start_and_end_date    // 챌린지 시작일 & 종료일
                    ;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                view = itemView;
                item_home_p_thumb = itemView.findViewById(R.id.item_home_p_thumb);
                item_home_p_title_and_certi_day = itemView.findViewById(R.id.item_home_p_title_and_certi_day);
                item_home_p_category = itemView.findViewById(R.id.item_home_p_category);
                item_home_p_total_price = itemView.findViewById(R.id.item_home_p_total_price);
                item_home_p_start_and_end_date = itemView.findViewById(R.id.item_home_p_start_and_end_date);

            }
        }
    }
}
