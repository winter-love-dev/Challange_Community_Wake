package org.tensorflow.lite.examples.detection.wake.FragmentHome;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.FragmentFeed.FragmentDialogComplain;
import org.tensorflow.lite.examples.detection.wake.FragmentFeed.Fragment_Feed;
import org.tensorflow.lite.examples.detection.wake.FragmentMyPage.Activity_Complain_List;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Activity_Project_Detail_Page.GET_PROJECT_CERTI_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.Activity_Project_Detail_Page.GET_PROJECT_CERTI_USER;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot.GET_CERTIFYING_SHOT_DATE;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot.GET_CERTIFYING_SHOT_FILE;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot.GET_CERTIFYING_SHOT_TITLE;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot.GET_CERTIFYING_SHOT_TYPE;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot.GET_CERTIFYING_SHOT_USER_NAME;
import static org.tensorflow.lite.examples.detection.wake.FragmentHome.AdapterCertifyingShot.GET_CERTIFYING_SHOT_USER_PHOTO;


public class Activity_Project_Detail_in_Certi_Shot_Detail extends AppCompatActivity
{
    private String TAG = "Activity_Project_Detail_in_Certi_Shot_Detail";

    private TextView
            feed_user_name          // 유저 이름
            ,feed_title             // 프로젝트 타이틀
            ,feed_certi_type        // 인증 유형
            ,feed_like_count        // 좋아요 누적수
            ,feed_project_certi_date    // 인증한 날짜, 시간
            ,feed_declaration_count // 신고 누적수
            ;

    private LinearLayout feed_user_info_button; // 유저 정보 자세히 보기 버튼
    private FrameLayout feed_project_info_button; // 프로젝트 정보 자세히 보기 버튼

    private ImageView
            feed_project_thumb          // 프로젝트 썸네일
            ,feed_like_button           // 좋아요 버튼
            ;

    private CircleImageView
            feed_declaration_button // 신고 버튼
            ,feed_user_photo    // 유저 프로필사진
            ;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__project__detail_in__certi__shot__detail);

        // ViewFind
        feed_user_name = findViewById(R.id.feed_user_name);
        feed_title = findViewById(R.id.feed_title);
        feed_certi_type = findViewById(R.id.feed_certi_type);
        feed_like_count = findViewById(R.id.feed_like_count);
        feed_declaration_count = findViewById(R.id.feed_declaration_count);

        feed_user_info_button = findViewById(R.id.feed_user_info_button);
        feed_project_info_button = findViewById(R.id.feed_project_info_button);

        feed_project_thumb = findViewById(R.id.feed_project_thumb);
        feed_project_certi_date = findViewById(R.id.feed_project_certi_date);
//        feed_like_button = findViewById(R.id.feed_like_button);

        feed_declaration_button = findViewById(R.id.feed_complain_button);
        feed_user_photo = findViewById(R.id.feed_user_photo);

//        GET_CERTIFYING_SHOT_USER_PHOTO;
//        GET_CERTIFYING_SHOT_USER_NAME;
//        GET_CERTIFYING_SHOT_FILE;
//        GET_CERTIFYING_SHOT_DATE;
//        GET_CERTIFYING_SHOT_TITLE;
//        GET_CERTIFYING_SHOT_TYPE;

        // 유저 프로필 사진
        Picasso.get().load(GET_CERTIFYING_SHOT_USER_PHOTO).
        placeholder(R.drawable.logo_2).
        into(feed_user_photo);

        feed_user_name.setText(GET_CERTIFYING_SHOT_USER_NAME);
        feed_title.setText(GET_CERTIFYING_SHOT_TITLE);
        feed_certi_type.setText(GET_CERTIFYING_SHOT_TYPE);

//         viewHolder.feed_like_count.setText();
//         viewHolder.feed_declaration_count.setText();

//         feed_user_info_button
//         feed_project_info_button

        // 인증샷
        Picasso.get().load(GET_CERTIFYING_SHOT_FILE).
        placeholder(R.drawable.logo_2).
        into(feed_project_thumb);

//        Log.e(TAG, "onBindViewHolder: 인증샷: " +  );

        // 날짜 / 시간
        feed_project_certi_date.setText(GET_CERTIFYING_SHOT_DATE);
//            viewHolder.feed_like_button
//            viewHolder.feed_declaration_button


        feed_declaration_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 내 아이디는 신고버튼 숨기기
                if (Integer.parseInt(getId) == Integer.parseInt(GET_PROJECT_CERTI_USER))
                {
                    Log.e(TAG, "onBindViewHolder: GONE" );
//                viewHolder.feed_complain_button.setEnabled(false);
                    feed_declaration_button.setVisibility(View.GONE);
                }

                else
                {
                    feed_declaration_button.setVisibility(View.VISIBLE);
                    Log.e(TAG, "onBindViewHolder: VISIBLE" );
//                viewHolder.feed_complain_button.setEnabled(true);

                    // todo: 신고 버튼
                    feed_declaration_button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 데이터를 다이얼로그로 보내는 코드
                            Bundle args = new Bundle();
                            args.putString("key", "신고합니다");

                            // --------------------------------------- //

                            // 신고 당할 유저
                            Fragment_Feed.GET_COMPLAIN_USER = GET_PROJECT_CERTI_USER;

                            // 신고할 인증샷 인덱스
                            Fragment_Feed.GET_COMPLAIN_CERTI_INDEX = GET_PROJECT_CERTI_INDEX;

                            // 커스텀 프래그먼트 클래스 선언
                            FragmentDialogComplain dialog = new FragmentDialogComplain();

                            // 커스텀 프래그먼트로 데이터 전달
                            dialog.setArguments(args);

//                             다이얼로그 실행하기 (tag: 다이얼로그로 보낼 값의 태그)
//                            dialog.show(context.getActivity().getSupportFragmentManager(),"key");
                        }
                    });

                }
            }
        });

    }
}
