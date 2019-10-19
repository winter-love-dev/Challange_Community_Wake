package org.tensorflow.lite.examples.detection.wake.FragmentMyPage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.tensorflow.lite.examples.detection.R;

public class Dialog_Item_Dispose extends Dialog
{
    /** todo: 관리자의 신고 처분 방법 선택창 (다이얼로그)
     *
     * 처분 방법 1
     * 신고한 유저에게 신고 권한 박탈하기 (신고 권한 박탈)
     *
     * 처분 방법 2
     * 신고 접수된 유저에게 챌린지 참가 권한 박탈하기 (신고 정상 처분)
     * */

    /** TODO: 처분 방법 참고
     *
     * 1. penalty_complain_user: 신고한 유저에게 신고 권한 박탈하기 (신고 권한 박탈)
     * 2. penalty_dispose_user: 신고 접수된 유저에게 챌린지 참가 권한 박탈하기 (신고 정상 처분)
     * */

    private String TAG = "Dialog_Item_Dispose";

    // 처분 방법
    public static String GET_DISPOSE_SELECT;

    // 신고 완료 클릭버튼
    private TextView dialog_dispose_done_button;

    // 신고 처분 라디오 그룹
    private RadioGroup complain_dispose_radio_group;

    // 신고 처분 라디오 버튼
    private RadioButton
            complain_dispose_radio_button_1     // 부정 신고처리 (신고한 유저에게 신고 권한 박탈)
            , complain_dispose_radio_button_2   // 정상 신고처리 (신고 접수된 유저의 해당 챌린지 참가권한 박탈)
            ;

    // todo: 신고 완료 클릭 감지하기
    private View.OnClickListener done_button_Listener;
    public Dialog_Item_Dispose(Context context, View.OnClickListener done_button_Listener)
    {
        super(context);
        this.done_button_Listener = done_button_Listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        // Layout Find
        setContentView(R.layout.dialog_complain_dispose);

        // View Find
        dialog_dispose_done_button = findViewById(R.id.dialog_dispose_done_button);
        complain_dispose_radio_group = findViewById(R.id.complain_dispose_radio_group);

        // todo: 신고 처분 방법 선택하기
        RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // 해당 인증샷만 신고
                if (checkedId == R.id.complain_dispose_radio_button_1)
                {
                    GET_DISPOSE_SELECT = "penalty_complain_user";

                    // 라디오 선택 결과 확인
                    Log.e(TAG, "onCheckedChanged: GET_DISPOSE_SELECT: " + GET_DISPOSE_SELECT);
                }

                // 해당 인증샷을 찍은 유저의 다른 인증샷도 봐주세요
                else if (checkedId == R.id.complain_dispose_radio_button_2)
                {
                    GET_DISPOSE_SELECT = "penalty_dispose_user";

                    // 라디오 선택 결과 확인
                    Log.e(TAG, "onCheckedChanged: GET_DISPOSE_SELECT: " + GET_DISPOSE_SELECT);
                }

                // 문제 없음
                else if (checkedId == R.id.complain_dispose_radio_button_3)
                {
                    GET_DISPOSE_SELECT = "penalty_no_problem";

                    // 라디오 선택 결과 확인
                    Log.e(TAG, "onCheckedChanged: GET_DISPOSE_SELECT: " + GET_DISPOSE_SELECT);
                }
            }
        };

        // 라디오 그룹 활성화
        complain_dispose_radio_group.setOnCheckedChangeListener(onCheckedChangeListener);

        // todo: 신고 완료 클릭 감지하기
        dialog_dispose_done_button.setOnClickListener(done_button_Listener);
    }
}



// 처분 방법 선택하지 않으면 버튼 클릭 불가능함
//        if (GET_DISPOSE_SELECT == null || GET_DISPOSE_SELECT.equals(null) || GET_DISPOSE_SELECT.length() == 0)
//        {
//            Log.e(TAG, "onCreate: 처분 방법을 선택하세요" );
//            Toast.makeText(getContext(), "처분 방법을 선택하세요", Toast.LENGTH_SHORT).show();

//            dialog_dispose_done_button.setEnabled(false);
//        }

//
//        else if (GET_DISPOSE_SELECT.equals("Complain_This_Shot") || GET_DISPOSE_SELECT.equals("Complain_Other_Shot"))
//        {
//            dialog_dispose_done_button.setEnabled(true);

//             신고 완료 클릭 감지하기
//            dialog_dispose_done_button.setOnClickListener(done_button_Listener);
//        }