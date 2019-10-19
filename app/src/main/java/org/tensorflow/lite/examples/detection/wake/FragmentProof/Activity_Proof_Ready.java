package org.tensorflow.lite.examples.detection.wake.FragmentProof;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.tensorflow.lite.examples.detection.DetectorActivity;
import org.tensorflow.lite.examples.detection.R;

import java.io.File;
import java.util.ArrayList;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_JOIN_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_JOIN_MEMBER_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_CATEGORY;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_CERTI_COUNT;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_DAY;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_INDEX;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_JOIN_PRICE;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_MY_CERTI_COUNT;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_SUBSCRIPT;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_THUMB;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_TIME;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_TITLE;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_TYPE;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_REWARD;

public class Activity_Proof_Ready extends AppCompatActivity
{
    private String TAG = "Activity_Proof_Ready";

    private TextView
            proof_ready_title                   // 제목
            , proof_ready_day                   // 진행기간
            , proof_ready_time                  // 시간
            , proof_ready_reword                // 리워드
            , proof_ready_proof_way             // 인증 방법
            , proof_ready_album_use_notification        // 앨범 사용여부 안내
            , proof_ready_cam_button                    // 버튼 카메라로 인증하기
            , proof_ready_cam_album_button              // 버튼 앨범 사진으로 인증하기
            , proof_ready_object_detect_button          // 버튼 물체인식
            , proof_ready_motion_detect_button             // 버튼 방송하기 / VOD 남기기
            , proof_ready_distance_measurement_button   // 거리측정 기능 버튼
            , view_page_text
            ;

    private ImageView proof_ready_thumb;            // 썸네일

    private LinearLayout proof_ready_cam_use_area;  // 카메라 버튼 수평 영역

    // 액티비티 종료 준비 (프로젝트 전송 완료되면 화면이 종료됨)
    public static Activity Activity_Proof_Ready;

    // 앨범에서 사진 가져오기
    private static final int PICK_FROM_ALBUM = 1;

    private File tempFile;

    public static Bitmap ORIGINAL_BM;

    public static String GET_DETECT;

    // 뷰페이저
    private ViewPager work_info_image_pager;

    // 다이얼로그에 띄울 튜토리얼 뷰 페이저 어댑터
    private ViewPagerAdapter pagerAdapter;

     // 뷰페이저에 담을 메시지
    public String[] TutorialTextArray =
                    {
                            /*detect_tutorial_1*/"사용자의 자세를 인식합니다\n\n\n(확인)버튼을 누르면 인증을 시작합니다\n\n\n\n\n\n\n\n\n다음 페이지로 스와이프 하면 사용 방법을 안내 받습니다",
                            /*detect_tutorial_3*/"step 1_\n\n휴대폰을 잡고 거울 앞에 서십시오.\n\n녹색 선을 발 아래 두고 아래 ②와 같은 안내 메시지를 기다립니다",
                            /*detect_tutorial_4*/"step 2_\n\n안내 메시지가 확인되면 스쿼트 자세를 취하십시오\n\n\n\n\n\n\n\n\n출처: DesRun YouTube",
                            /*detect_tutorial_5*/"step 3_\n\n자세를 취하면 자동 촬영을 시작합니다"/*주의: 자세를 유지하지 않으면 촬영이 취소됩니다*/
                    };

    // 뷰페이저에 담을 이미지
    private int[] imageIds =
                    {
                            R.drawable.detect_tutorial_1,
                            R.drawable.detect_tutorial_3,
                            R.drawable.detect_tutorial_4,
                            R.drawable.detect_tutorial_5
                    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proof_ready);

        // 액티비티 종료 준비 (프로젝트 전송 완료되면 화면이 종료됨)
        Activity_Proof_Ready = Activity_Proof_Ready.this;

        // View Find
        proof_ready_title = findViewById(R.id.proof_ready_title);
        proof_ready_day = findViewById(R.id.proof_ready_day);
        proof_ready_time = findViewById(R.id.proof_ready_time);
        proof_ready_thumb = findViewById(R.id.proof_ready_thumb);
        proof_ready_reword = findViewById(R.id.proof_ready_reword);
        proof_ready_proof_way = findViewById(R.id.proof_ready_proof_way);
        proof_ready_album_use_notification = findViewById(R.id.proof_ready_album_use_notification);
        proof_ready_distance_measurement_button = findViewById(R.id.proof_ready_distance_measurement_button);

        proof_ready_cam_use_area = findViewById(R.id.proof_ready_cam_use_area);
        proof_ready_cam_button = findViewById(R.id.proof_ready_cam_button);
        proof_ready_cam_album_button = findViewById(R.id.proof_ready_cam_album_button);

        proof_ready_object_detect_button = findViewById(R.id.proof_ready_object_detect_button);
        proof_ready_motion_detect_button = findViewById(R.id.proof_ready_motion_detect_button);

        Log.e(TAG, "onCreate: GET_PROOF_THUMB: " + GET_PROOF_THUMB);
        Log.e(TAG, "onCreate: GET_JOIN_MEMBER_INDEX: " + GET_JOIN_MEMBER_INDEX);

        // 썸네일
        Picasso.get().load(GET_PROOF_THUMB).
                memoryPolicy(MemoryPolicy.NO_CACHE).
                placeholder(R.drawable.logo_2).
                networkPolicy(NetworkPolicy.NO_CACHE).
                into(proof_ready_thumb);

        // GET_PROOF_INDEX
        // 인증할 프로젝트의 인덱스

        proof_ready_title.setText(GET_PROOF_TITLE);
        proof_ready_day.setText(GET_PROOF_DAY);
        proof_ready_time.setText(GET_PROOF_TIME);
        proof_ready_reword.setText(GET_REWARD + "원");

        Log.e(TAG, "onCreate: GET_PROOF_TITLE: " + GET_PROOF_TITLE);
        Log.e(TAG, "onCreate: GET_PROOF_THUMB: " + GET_PROOF_THUMB);

        //todo: 인증 방법 GET_AUTH_TYPE
        /**
         * GET_AUTH_TYPE 결과 (인증방식 선택 결과)
         * <p>
         * 1. broadCast (방송)
         * <p>
         * 2. normal_photo_camera_only (일반사진_카메라만)
         * 3. normal_photo_use_album (일반사진_앨범허용)
         * <p>
         * 4. distance_measurement 이동 거리 측정
         * <p>
         * 5. detect_object_laptop 노트북
         * 6. detect_object_watch  시계
         * 7. detect_object_person 사람
         * 8. detect_motion_squat  스쿼트
         */

        /*
            랩탑 (물체인식)
            시계 (물체인식)
            인물 인식
            스쿼트 (동작인식)

            detect_object_laptop
            detect_object_watch
            detect_object_person
            detect_motion_squat
        */

        String ProofWay = null               //
                , AlbumUseNotification = null   //
                ;

        // 1
        if (GET_PROOF_TYPE.equals("normal_photo_camera_only"))
        {
            ProofWay = "사진";
            AlbumUseNotification = "불가능";

            proof_ready_cam_use_area.setVisibility(View.VISIBLE);
        }

        // 2
        else if (GET_PROOF_TYPE.equals("normal_photo_use_album"))
        {
            ProofWay = "사진";
            AlbumUseNotification = "가능";

            proof_ready_cam_use_area.setVisibility(View.VISIBLE);
            proof_ready_cam_album_button.setVisibility(View.VISIBLE);
        }

        // 3
        else if (GET_PROOF_TYPE.equals("distance_measurement"))
        {
            ProofWay = "이동거리 측정";
            AlbumUseNotification = "불가능";

            proof_ready_cam_use_area.setVisibility(View.VISIBLE);
            proof_ready_cam_button.setVisibility(View.GONE);
            proof_ready_distance_measurement_button.setVisibility(View.VISIBLE);
        }

        // 4
        else if (GET_PROOF_TYPE.equals("detect_object_laptop"))
        {
            ProofWay = "물체인식 (laptop)";
            AlbumUseNotification = "불가능";

            GET_DETECT = "laptop";

            proof_ready_cam_button.setVisibility(View.GONE);
            proof_ready_object_detect_button.setVisibility(View.VISIBLE);
            proof_ready_cam_use_area.setVisibility(View.VISIBLE);
        } else if (GET_PROOF_TYPE.equals("detect_object_person"))
        {
            ProofWay = "인물인식 (person)";
            AlbumUseNotification = "불가능";

            GET_DETECT = "person";

            proof_ready_cam_button.setVisibility(View.GONE);
            proof_ready_object_detect_button.setVisibility(View.VISIBLE);
            proof_ready_cam_use_area.setVisibility(View.VISIBLE);
        } else if (GET_PROOF_TYPE.equals("detect_motion_squat"))
        {
            ProofWay = "동작인식 (squat)";
            AlbumUseNotification = "불가능";

            GET_DETECT = "squat";

            proof_ready_cam_button.setVisibility(View.GONE);
            proof_ready_object_detect_button.setVisibility(View.GONE);
            proof_ready_motion_detect_button.setVisibility(View.VISIBLE);
            proof_ready_cam_use_area.setVisibility(View.VISIBLE);
        }

        proof_ready_proof_way.setText(ProofWay + "\n\n" + GET_PROOF_SUBSCRIPT);
        proof_ready_album_use_notification.setText(AlbumUseNotification);

        // 카메라 실행 버튼
        proof_ready_cam_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Activity_Proof_Ready.this, Activity_Certifying_Shot.class);
                startActivity(intent);
            }
        });

        // 앨범 실행버튼
        proof_ready_cam_album_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        // 자세인식 버튼
        proof_ready_motion_detect_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*
                    // 시작 다이얼로그
                    ImageView dialog_distance_measurement_notice_background = view.findViewById(R.id.dialog_distance_measurement_notice_background);
                    TextView dialog_distance_measurement_notice_title = view.findViewById(R.id.dialog_distance_measurement_notice_title);
                    TextView dialog_distance_measurement_notice_check = view.findViewById(R.id.dialog_distance_measurement_notice_check);

                    final AlertDialog dialog = builder.create();

                    dialog_distance_measurement_notice_title.setText(GET_PROOF_TITLE);

                    Picasso.get().load(GET_PROOF_THUMB).
                    placeholder(R.drawable.logo_1).
                    into(dialog_distance_measurement_notice_background);

                    dialog.dismiss();
                */


                // todo: 시작 알림 다이얼로그 활성화
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Proof_Ready.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_motion_detect_tutorial, null);
                builder.setView(view);

                // 확인 버튼
                TextView dialog_motion_detect_tutorial_done = view.findViewById(R.id.dialog_motion_detect_tutorial_done);

                // 뷰페이저 세팅
                work_info_image_pager = view.findViewById(R.id.work_info_image); //뷰페이저
                view_page_text = view.findViewById(R.id.view_page_text);

                // 다이얼로그 실행
                final AlertDialog dialog = builder.create();
                dialog.show();

                // 다이얼로그 세팅
                pagerAdapter = new ViewPagerAdapter(getApplicationContext());
                work_info_image_pager.setAdapter(pagerAdapter);

                int pageTotal = imageIds.length;

                work_info_image_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                    {
                        // 이 부분을 선언하지 않으면 액티비티에 접근할 때
                        // 현재 페이지 수가 뜨지 않음.
                        int pageCul = position + 1;
                        view_page_text.setText(pageCul + " / " + pageTotal);
                    }

                    @Override
                    public void onPageSelected(int position)
                    {
                        // 현재 몇 페이지인지 감지하는 부분
                        int pageCul = position + 1;
                        view_page_text.setText(pageCul + " / " + pageTotal);
                        Log.e(TAG, "instantiateItem: pageCul: 현재 페이지: onPageSelected: " + pageCul + " / " + pageTotal);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state)
                    {

                    }
                });

                // 확인 버튼 클릭
                dialog_motion_detect_tutorial_done.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();

                        Intent intent = new Intent(Activity_Proof_Ready.this, org.tensorflow.lite.examples.posenet.PosenetCameraActivity.class);

                        intent.putExtra("getId", getId);
                        intent.putExtra("GET_JOIN_INDEX", GET_JOIN_INDEX);
                        intent.putExtra("GET_JOIN_MEMBER_INDEX", GET_JOIN_MEMBER_INDEX);
                        intent.putExtra("GET_PROOF_CATEGORY", GET_PROOF_CATEGORY);
                        intent.putExtra("GET_PROOF_CERTI_COUNT", GET_PROOF_CERTI_COUNT);

                        intent.putExtra("GET_PROOF_INDEX", GET_PROOF_INDEX);
                        intent.putExtra("GET_PROOF_JOIN_PRICE", GET_PROOF_JOIN_PRICE);
                        intent.putExtra("GET_PROOF_MY_CERTI_COUNT", GET_PROOF_MY_CERTI_COUNT);
                        intent.putExtra("GET_PROOF_TITLE", GET_PROOF_TITLE);
                        intent.putExtra("GET_PROOF_TYPE", GET_PROOF_TYPE);

                        intent.putExtra("GET_REWARD", GET_REWARD);

                        startActivity(intent);

                        finish();
                    }
                });

                // 다이얼로그 종료 감지
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        Log.e(TAG, "onDismiss: 종료 감지됨");
                    }
                });
            }
        });

        // 물체인식 버튼
        proof_ready_object_detect_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Activity_Proof_Ready.this, DetectorActivity.class);
                startActivity(intent);
            }
        });

        // 거리측정 버튼
        proof_ready_distance_measurement_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Activity_Proof_Ready.this, Activity_Distance_Measurement.class);
                startActivity(intent);
            }
        });
    }

    private void tedPermission()
    {

        PermissionListener permissionListener = new PermissionListener()
        {
            @Override
            public void onPermissionGranted()
            {
                // 권한 요청 성공
                goToAlbum();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions)
            {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    private void goToAlbum()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // 사진을 선택하지 않았을 경우
        if (resultCode != Activity.RESULT_OK)
        {

            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

//            if(tempFile != null) {
//                if (tempFile.exists()) {
//                    if (tempFile.delete()) {
//                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
//                        tempFile = null;
//                    }
//                }
//            }

            return;
        }


        if (requestCode == PICK_FROM_ALBUM)
        {
            Uri photoUri = data.getData();
            Cursor cursor = null;

            try
            {
                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = {MediaStore.Images.Media.DATA};


                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                Log.e(TAG, "onActivityResult: photoUri: " + photoUri);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                Log.e(TAG, "onActivityResult: cursor: " + cursor.getString(column_index));

                tempFile = new File(cursor.getString(column_index));

            } finally
            {
                if (cursor != null)
                {
                    cursor.close();
                }
            }

            // todo: 앨범에서 가져온 이미지 세팅하기
            setImage();
        }
    }

    // todo: 앨범에서 가져온 이미지 세팅하기
    private void setImage()
    {

//        ImageView imageView = findViewById(R.id.imageView);

        BitmapFactory.Options options = new BitmapFactory.Options();
        ORIGINAL_BM = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
//        imageView.setImageBitmap(originalBm);

        Log.e(TAG, "setImage: ORIGINAL_BM: " + ORIGINAL_BM);
        Log.e(TAG, "setImage: tempFile.getAbsolutePath(): " + tempFile.getAbsolutePath());

        Intent intent = new Intent(Activity_Proof_Ready.this, Activity_Done_Certify.class);

        intent.putExtra("strParamName", tempFile.getAbsolutePath());
        intent.putExtra("certiFrom", "album");

        startActivity(intent);
    }

    // todo: 뷰페이저 어댑터
    public class ViewPagerAdapter extends PagerAdapter
    {

        // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
        private Context mContext = null;

        public ViewPagerAdapter()
        {

        }

        // Context를 전달받아 mContext에 저장하는 생성자 추가.
        public ViewPagerAdapter(Context context)
        {
            mContext = context;
        }

        @SuppressLint("LongLogTag")
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View view = null;

            if (mContext != null)
            {
                // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.view_pager_detect_motion_tutorial, container, false);

                ImageView imageView = view.findViewById(R.id.work_image_pager);
                TextView textView = view.findViewById(R.id.tutorial_text);

                // 이미지 배열에 drawable 경로 담기
//                Log.e(TAG, "instantiateItem: TutorialTextArray["+ position +"]" + TutorialTextArray[position]);
//                String pagerPath = "R.drawable." + TutorialTextArray[position];
//                Picasso.get().load(pagerPath).into(imageView);

                imageView.setImageResource(imageIds[position]);

                textView.setText(TutorialTextArray[position]);
            }

            // 뷰페이저에 추가.
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            // 뷰페이저에서 삭제.
            container.removeView((View) object);
        }

        @Override
        public int getCount()
        {
            return imageIds.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
        {
            return (view == (View) object);
        }
    }
}