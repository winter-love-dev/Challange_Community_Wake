package org.tensorflow.lite.examples.detection.wake.FragmentFeed;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.R;

import java.util.HashMap;
import java.util.Map;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentFeed.Fragment_Feed.GET_COMPLAIN_CERTI_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentFeed.Fragment_Feed.GET_COMPLAIN_USER;


public class FragmentDialogComplain extends DialogFragment
{
    Context mContext;

    String TAG = "FragmentDialogComplain";

    private RadioGroup complain_dialog_radio_group; // 신고 선택 라디오 그룹

    private RadioButton
            complain_dialog_radio_button_1 // 라디오 신고 선택 1
            , complain_dialog_radio_button_2// 라디오 신고 선택 2
            ;

    private TextView complain_done_button; // 신고버튼

    private String Complain_Content; // 해당 인증샷만 신고 or 해당 인증샷을 찍은 유저의 다른 인증샷도 봐주세요             ;

    private Fragment fragment;

    private DialogFragment dialogFragment;

    public FragmentDialogComplain()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_complain, container, false);

        Log.e(TAG, "onCreateView: 신고 다이얼로그 실행됨");

        mContext = getActivity().getApplicationContext();

        // View Find
        complain_dialog_radio_group = view.findViewById(R.id.complain_dialog_radio_group);
        complain_dialog_radio_button_1 = view.findViewById(R.id.complain_dialog_radio_button_1);
        complain_dialog_radio_button_2 = view.findViewById(R.id.complain_dialog_radio_button_2);
        complain_done_button = view.findViewById(R.id.complain_done_button);

        // 피드에서 신고할 인증샷 정보 불러오기
        Bundle args = getArguments();

        // 받을 값
        String value = args.getString("key");

        Log.e(TAG, "onCreateView: value: " + value);

        // 값 안 받고 그냥 static으로 불러와야지
        // 최소한 프래그먼트에서 보낸 값의 태그(key)는 받아야됨.
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("key");
        dialogFragment = (DialogFragment) fragment;

        Log.e(TAG, "onCreateView: dialogFragment: " + dialogFragment);
        Log.e(TAG, "onCreateView: fragment: " + fragment);

        // todo: 신고 유형 선택
        SelectComplainType();

        return view;
    }

    // todo: 신고 유형 선택
    private void SelectComplainType()
    {
        Log.e(TAG, "SelectComplainType: 신고 유형 선택");

        // 프래그먼트에서 key로 전달한 값을 받을 때만 아래 신고절차 진행하기
        if (fragment != null)
        {
            RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    // 해당 인증샷만 신고
                    if (checkedId == R.id.complain_dialog_radio_button_1)
                    {
                        Complain_Content = "Complain_This_Shot";

                        // 라디오 선택 결과 확인
                        Log.e(TAG, "onCheckedChanged: Complain_Content: " + Complain_Content);
                    }

                    // 해당 인증샷을 찍은 유저의 다른 인증샷도 봐주세요
                    else if (checkedId == R.id.complain_dialog_radio_button_2)
                    {
                        Complain_Content = "Complain_Other_Shot";

                        // 라디오 선택 결과 확인
                        Log.e(TAG, "onCheckedChanged: Complain_Content: " + Complain_Content);
                    }
                }
            };

            // 라디오 그룹 활성화
            complain_dialog_radio_group.setOnCheckedChangeListener(onCheckedChangeListener);

            // todo: 신고 접수버튼
            complain_done_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.e(TAG, "onClick: 신고 접수버튼 클릭");
                    Done_Complain();
                }
            });
        }
    }

    // todo: 신고 접수버튼
    private void Done_Complain()
    {
        // 입력한 정보를 php POST로 DB에 전송합니다.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/addWakeComplain.php",
                new Response.Listener<String>()
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
                                Toast.makeText(mContext, "신고 접수 완료", Toast.LENGTH_SHORT).show();

                                // 신고 접수되면 다이얼로그 닫기
                                dialogFragment.dismiss();
                            } else if (success.equals("3"))
                            {
                                Toast.makeText(mContext, "이미 신고했습니다", Toast.LENGTH_SHORT).show();
                            } else
                            {
                                Toast.makeText(mContext, "\"message\":\"error\" 문제발생.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(mContext, "JSONException 문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(mContext, "VolleyError 문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: error: " + error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                params.put("GET_COMPLAIN_USER", GET_COMPLAIN_USER);
                params.put("GET_COMPLAIN_CERTI_INDEX", GET_COMPLAIN_CERTI_INDEX);
                params.put("getId", getId);
                params.put("Complain_Content", Complain_Content);

                Log.e(TAG, "getParams: 신고 테이블로 전송하는 값");
                Log.e(TAG, "getParams: GET_COMPLAIN_USER: " + GET_COMPLAIN_USER);
                Log.e(TAG, "getParams: GET_COMPLAIN_CERTI_INDEX: " + GET_COMPLAIN_CERTI_INDEX);
                Log.e(TAG, "getParams: getId: " + getId);
                Log.e(TAG, "getParams: Complain_Content: " + Complain_Content);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
    }
}
