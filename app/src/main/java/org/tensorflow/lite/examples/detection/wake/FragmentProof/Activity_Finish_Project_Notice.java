package org.tensorflow.lite.examples.detection.wake.FragmentProof;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.R;

import java.util.HashMap;
import java.util.Map;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_JOIN_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_CHECK;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_EXP;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_EXP_CATEGORY;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_MORE_REWARD;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_PEOPLE_COUNT;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_PROGRESS_PERCENT;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_YOUR_PROGRESS_PERCENT;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_CHELLANGE_DONE_RESULT_YOUR_REAWARD;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.GET_USER_REWARD;
import static org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Fragment_MyPage.itemMyJoinLists;

public class Activity_Finish_Project_Notice extends AppCompatActivity
{
    private String TAG = "Activity_Finish_Project_Notice";

    private TextView
            project_done_go_my_challenge_status   // 액티비티 닫기
            , project_done_total_people           // 총 참가 인원수
            , project_done_total_progress_percent // 참가자 전원 달성률
            , project_done_your_progress_percent  // 당신의
            , project_done_your_total_reward
            , project_done_your_exp
            , project_done_your_additional_reward
            , project_done_your_additional_status
            ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__finish__project__notice);

        // View Find
        project_done_go_my_challenge_status = findViewById(R.id.project_done_go_my_challenge_status);
        project_done_total_people = findViewById(R.id.project_done_total_people);
        project_done_total_progress_percent = findViewById(R.id.project_done_total_progress_percent);
        project_done_your_progress_percent = findViewById(R.id.project_done_your_progress_percent);
        project_done_your_total_reward = findViewById(R.id.project_done_your_total_reward);
        project_done_your_exp = findViewById(R.id.project_done_your_exp);
        project_done_your_additional_reward = findViewById(R.id.project_done_your_additional_reward);
        project_done_your_additional_status = findViewById(R.id.project_done_your_additional_status);


        // todo: 챌린지 결과 반환 (보상 새로 계산) or 지난 챌린지 결과 반환 (기존에 계산 되었던 보상 불러오기)
//        getChallangeResult();


        project_done_total_people.setText(GET_CHELLANGE_DONE_RESULT_PEOPLE_COUNT + "명");
        project_done_total_progress_percent.setText(GET_CHELLANGE_DONE_RESULT_PROGRESS_PERCENT + "%");
        project_done_your_progress_percent.setText(GET_CHELLANGE_DONE_RESULT_YOUR_PROGRESS_PERCENT + "%");
        project_done_your_total_reward.setText(GET_CHELLANGE_DONE_RESULT_YOUR_REAWARD + "원");
        project_done_your_exp.setText(GET_CHELLANGE_DONE_RESULT_EXP + "점 (" + GET_CHELLANGE_DONE_RESULT_EXP_CATEGORY + ")");

        if (Integer.parseInt(GET_CHELLANGE_DONE_RESULT_MORE_REWARD)== 0)
        {
            project_done_your_additional_reward.setText("없음");
        }

        else
        {
            project_done_your_additional_reward.setText(GET_CHELLANGE_DONE_RESULT_MORE_REWARD + "원");
        }


        // 아직 보상 안 받았으면 보상 받으라고 알려주기
        if (GET_CHELLANGE_DONE_RESULT_CHECK.equals("CheckNot"))
        {
            project_done_your_additional_status.setVisibility(View.VISIBLE);

            project_done_go_my_challenge_status.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    updateChallangeResultCheck();
                }
            });
        }

        // 아니면 그냥 확인버튼 누르고 닫기
        else
        {
            project_done_your_additional_status.setVisibility(View.GONE);
            // 액티비티 닫기
            project_done_go_my_challenge_status.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
        }
    }

    // todo: 챌린지 종료 확인 or 종료 확인 체크하기
    private void updateChallangeResultCheck()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/updateChallengeDoneResult.php",
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

                            if (success.equals("1"))
                            {
                                Toast.makeText(Activity_Finish_Project_Notice.this, "보상이 지급되었습니다", Toast.LENGTH_SHORT).show();

                                int index = Integer.parseInt(GET_CHELLANGE_DONE_JOIN_INDEX);

                                // 보상 다시 못 받게 Checked로 수정하기
                                itemMyJoinLists.get(index).setWake_DoneResult_Check("Checked");

                                Log.e(TAG, "onResponse: 추가 보상 지급 전: " + GET_USER_REWARD );

                                int userReward = Integer.parseInt(GET_USER_REWARD);
                                int moreReward = Integer.parseInt(GET_CHELLANGE_DONE_RESULT_MORE_REWARD);

                                GET_USER_REWARD = String.valueOf(userReward + moreReward);

                                Log.e(TAG, "onResponse: 추가 보상 지급 후: " + GET_USER_REWARD );

                                finish();
                            }

                            else
                            {
                                Toast.makeText(Activity_Finish_Project_Notice.this, "문제발생.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: response: " + response);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(Activity_Finish_Project_Notice.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString() );
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_Finish_Project_Notice.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: error: " + error );
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
//                params.put("Index", Index);
                params.put("GET_CHELLANGE_DONE_RESULT_INDEX", GET_CHELLANGE_DONE_RESULT_INDEX);
                params.put("GET_CHELLANGE_DONE_RESULT_CHECK", GET_CHELLANGE_DONE_RESULT_CHECK);
                params.put("GET_CHELLANGE_DONE_RESULT_MORE_REWARD", GET_CHELLANGE_DONE_RESULT_MORE_REWARD);
                params.put("getId", getId);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Activity_Finish_Project_Notice.this);
        requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        finish();
    }
}
