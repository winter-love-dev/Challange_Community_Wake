package org.tensorflow.lite.examples.detection.wake.FragmentMyPage;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

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
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_CERTI__INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_CONTENT;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_DATE;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_DETAIL_INFO_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_DISPOSE_NAME;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_DISPOSE_PHOTO;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_DISPOSE_USER;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_JOIN_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_SUBSCRIPT;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_TABLE_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_TITLE;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_USER_ID;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_USER_NAME;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.GET_COMPLAIN_USER_PHOTO;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List.getFeedList;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Dialog_Item_Dispose.GET_DISPOSE_SELECT;

public class Activity_Complain_detail extends AppCompatActivity
{
    private TextView
            complain_detail_title, complain_detail_certi_subscript, complain_detail_content, complain_detail_user_name, complain_detail_date, complain_detail_dispose_name, complain_detail_dispose_done_button;

    private ImageView
            complain_detail_user_photo, complain_detail_dispose_photo;

    private Dialog_Item_Dispose customDialog;

    private String TAG = "Activity_Complain_detail";

    private RecyclerView mRecyclerView_Complain_detail;
    private List<Item_Complain_Certi_Shot_List> Complain_detail_List;
    private AdpaterComplainDetail mAdapter_Complain_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_detail);

        // View Find
        complain_detail_title = findViewById(R.id.complain_detail_title);
        complain_detail_certi_subscript = findViewById(R.id.complain_detail_certi_subscript);
        complain_detail_content = findViewById(R.id.complain_detail_content);
        complain_detail_user_name = findViewById(R.id.complain_detail_user_name);

        complain_detail_date = findViewById(R.id.complain_detail_date);
        complain_detail_dispose_name = findViewById(R.id.complain_detail_dispose_name);
        complain_detail_dispose_done_button = findViewById(R.id.complain_detail_dispose_done_button);

        complain_detail_user_photo = findViewById(R.id.complain_detail_user_photo);
        complain_detail_dispose_photo = findViewById(R.id.complain_detail_dispose_photo);

        // setView
        complain_detail_title.setText(GET_COMPLAIN_TITLE);
        complain_detail_certi_subscript.setText(GET_COMPLAIN_SUBSCRIPT);
        complain_detail_content.setText(GET_COMPLAIN_CONTENT);
        complain_detail_user_name.setText(GET_COMPLAIN_USER_NAME);
        complain_detail_dispose_name.setText(GET_COMPLAIN_DISPOSE_NAME);
        complain_detail_date.setText(GET_COMPLAIN_DATE);

        // 신고자 사진
        Picasso.get().load(GET_COMPLAIN_USER_PHOTO).
                placeholder(R.drawable.logo_2).
                into(complain_detail_user_photo);

        // 신고 접수된 유저 사진 (당할사람)
        Picasso.get().load(GET_COMPLAIN_DISPOSE_PHOTO).
                placeholder(R.drawable.logo_2).
                into(complain_detail_dispose_photo);

        // 인증샷 리스트 초기화
        Complain_detail_List = new ArrayList<>();

        // 리사이클러뷰 세팅하기/
        mRecyclerView_Complain_detail = findViewById(R.id.complain_detail_recycler_view);
        mRecyclerView_Complain_detail.setHasFixedSize(true);
        mRecyclerView_Complain_detail.setLayoutManager(new LinearLayoutManager(this));

        // todo: 인증샷 불러오기
        getCertiShotList();


        // todo: 신고 처분 방법 선택하기 (다이얼로그)
        /**
         * 1. 허위 신고 처리시 신고자에게 신고권한 박탈
         * 2. 정상 신고 처리시 신고 접수된 유저에게 신고 권한 박탈
         * */
        complain_detail_dispose_done_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                customDialog = new Dialog_Item_Dispose(Activity_Complain_detail.this, positiveListener);
                customDialog.show();
            }
        });
    }

    // todo: 인증샷 불러오기
    private void getCertiShotList()
    {
        Log.e(TAG, "getProjectList(): 인증샷 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface CertishotListRequest = retrofit.create(ApiInterface.class);
        Call<List<Item_Complain_Certi_Shot_List>> listCall
                // 이 유저가 참여한 챌린지의, 인증샷 인덱스, 인증샷 한 장만 볼 지 or 여러장 볼 지 판단하기
                = CertishotListRequest.getComplainCertiList(GET_COMPLAIN_DISPOSE_USER, GET_COMPLAIN_JOIN_INDEX, GET_COMPLAIN_CONTENT, GET_COMPLAIN_CERTI__INDEX);

        listCall.enqueue(new Callback<List<Item_Complain_Certi_Shot_List>>()
        {
            @Override
            public void onResponse(Call<List<Item_Complain_Certi_Shot_List>> call, Response<List<Item_Complain_Certi_Shot_List>> response)
            {
                Complain_detail_List = response.body();

//                넘어온 값 확인하기
//                for (int i = 0; i < mList.size(); i++)
//                {
//                    // Log.e(TAG, "list call onResponse = " + response.body().get(i).getWake_Info_Title());
//                }

                mAdapter_Complain_detail = new AdpaterComplainDetail(Activity_Complain_detail.this, Complain_detail_List);
                mRecyclerView_Complain_detail.setAdapter(mAdapter_Complain_detail);
            }

            @Override
            public void onFailure(Call<List<Item_Complain_Certi_Shot_List>> call, Throwable t)
            {
                Toast.makeText(Activity_Complain_detail.this, "인증샷 로드 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    private View.OnClickListener positiveListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            // todo: 신고 처분하기 (서버로 신고 처분 결과, 신고 처분한 운영자 아이디 전송)
            Done_Dispose();
        }
    };

    // todo: 신고 처분하기 (서버로 신고 처분 결과, 신고 처분한 운영자 아이디 전송)
    private void Done_Dispose()
    {
        /** 전송할 값 정리
         * 1. 신고 테이블 인덱스
         * 2. 챌린지 진행정보 인덱스
         * 3. 신고한 유저 인덱스
         * 4. 신고 접수된 유저 인덱스
         * 5. 신고 처분 결과 (부정 신고 처분, 정상 신고 처분)
         * 6. 처분한 운영자 아이디
         *
         *
         * 부정 신고 처분방법
         *  1. 해당 테이블 인덱스만 '처분완료' 표시하기
         *
         *
         * 정상 신고 처분방법
         *  1. 신고 처분할 유저
         *  2. 챌린지 진행정보 인덱스
         *
         *          위 두 가지 값이 일치한 신고들을 일괄 처리하기 (누적된 동일 신고 건수 일괄 처리)
         *
         *
         * 테이블에 저장될 신고 처분 결과
         *  1. 처리중 : Complaint_Received
         *  2. 패널티 처분 완료 : Penalty_Disposition
         *  3. 허위신고 처분함  : Disposition_of_false_reports
         *
         * */

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/addComplain_dispose.php",
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

                            if (success.equals("Disposition_of_false_reports"))
                            {
                                Toast.makeText(getApplicationContext(), "허위신고 처분 완료.", Toast.LENGTH_SHORT).show();
                                customDialog.dismiss();

                                // 처분 방법 초기화
                                Log.e(TAG, "onClick: GET_DISPOSE_SELECT: " + GET_DISPOSE_SELECT);
                                GET_DISPOSE_SELECT = null;

                                getFeedList();

                                finish();
                            } else if (success.equals("Penalty_Disposition"))
                            {
                                Toast.makeText(getApplicationContext(), "정상 처분 완료.", Toast.LENGTH_SHORT).show();
                                customDialog.dismiss();

                                // 처분 방법 초기화
                                Log.e(TAG, "onClick: GET_DISPOSE_SELECT: " + GET_DISPOSE_SELECT);
                                GET_DISPOSE_SELECT = null;

                                getFeedList();

                                finish();
                            } else if (success.equals("penalty_no_problem"))
                            {
                                Toast.makeText(getApplicationContext(), "'문제없음' 처분 완료.", Toast.LENGTH_SHORT).show();
                                customDialog.dismiss();

                                // 처분 방법 초기화
                                Log.e(TAG, "onClick: GET_DISPOSE_SELECT: " + GET_DISPOSE_SELECT);
                                GET_DISPOSE_SELECT = null;

                                getFeedList();

                                finish();
                            } else
                            {
                                Toast.makeText(Activity_Complain_detail.this, "신고 처분: 에러발생. 로그 확인", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e)
                        {
                            Toast.makeText(Activity_Complain_detail.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Activity_Complain_detail.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: JSONException VolleyError error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                params.put("GET_COMPLAIN_TABLE_INDEX", GET_COMPLAIN_TABLE_INDEX);   // 신고 테이블 인덱스
                params.put("GET_COMPLAIN_JOIN_INDEX", GET_COMPLAIN_JOIN_INDEX);     // 챌린지 진행정보 인덱스
                params.put("GET_COMPLAIN_CERTI__INDEX", GET_COMPLAIN_CERTI__INDEX); // 인증샷 인덱스
                params.put("GET_COMPLAIN_DETAIL_INFO_INDEX", GET_COMPLAIN_DETAIL_INFO_INDEX); // 챌린지 상세정보 인덱스

                params.put("GET_COMPLAIN_USER_ID", GET_COMPLAIN_USER_ID);           // 신고한 유저
                params.put("GET_COMPLAIN_DISPOSE_USER", GET_COMPLAIN_DISPOSE_USER); // 신고 접수된 유저

                params.put("GET_DISPOSE_SELECT", GET_DISPOSE_SELECT); // 신고 처분 결과
                params.put("getId", getId); // 처분한 운영자 아이디

                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(Activity_Complain_detail.this);
        requestQueue.add(stringRequest);
    }

    public class AdpaterComplainDetail extends RecyclerView.Adapter<AdpaterComplainDetail.ViewHolderComplainDetail>
    {

        Context mContext;

        public AdpaterComplainDetail(Context context, List<Item_Complain_Certi_Shot_List> list)
        {
            mContext = context;
            Complain_detail_List = list;
        }

        @NonNull
        @Override
        public ViewHolderComplainDetail onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_certi_shot, viewGroup, false);

            return new ViewHolderComplainDetail(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderComplainDetail viewHolderComplainDetail, int i)
        {
            Item_Complain_Certi_Shot_List currentItem = Complain_detail_List.get(i);

            // 인증샷
            Picasso.get().load(currentItem.getWake_Evidence_File()).
                    placeholder(R.drawable.logo_2).
                    into(viewHolderComplainDetail.complain_certi_shot);

            // 날짜
            viewHolderComplainDetail.complain_certi_shot_date.setText(currentItem.getWake_Evidence_Date());
        }

        @Override
        public int getItemCount()
        {
            return Complain_detail_List.size();
        }

        public class ViewHolderComplainDetail extends RecyclerView.ViewHolder
        {

            ImageView complain_certi_shot;

            TextView complain_certi_shot_date;

            public ViewHolderComplainDetail(@NonNull View itemView)
            {
                super(itemView);

                complain_certi_shot = itemView.findViewById(R.id.complain_certi_shot);
                complain_certi_shot_date = itemView.findViewById(R.id.complain_certi_shot_date);

            }
        }
    }
}
