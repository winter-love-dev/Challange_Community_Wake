package org.tensorflow.lite.examples.detection.wake.FragmentHome;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.tensorflow.lite.examples.detection.R;

import java.util.List;

public class AdapterCertifyingShot extends RecyclerView.Adapter<AdapterCertifyingShot.ViewHolder>
{
    // 태그 선언
    private String TAG = "AdapterCertifyingShot";

    private Context mContext; // 컨텍스트

    public static List<ItemCertifyingList> itemCertifyingLists;

    public static String GET_CERTIFYING_SHOT_USER_PHOTO, GET_CERTIFYING_SHOT_USER_NAME, GET_CERTIFYING_SHOT_TITLE, GET_CERTIFYING_SHOT_TYPE, GET_CERTIFYING_SHOT_FILE, GET_CERTIFYING_SHOT_DATE;


    // 리사이클러뷰 표시제한
    private final int limit = 9;

    public AdapterCertifyingShot(Context Context, List<ItemCertifyingList> list)
    {
        mContext = Context;
        itemCertifyingLists = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_certifying_shot_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        final ItemCertifyingList currentItem = itemCertifyingLists.get(i);

        // 유저 프로필 사진
        Picasso.get().load(currentItem.getWake_Evidence_File()).
                placeholder(R.drawable.logo_2).
                into(viewHolder.certifying_shot);

        // 이미지 둥글게 만들기
        GradientDrawable drawable =
                (GradientDrawable) mContext.getDrawable(R.drawable.item_image_corner);
        viewHolder.certifying_shot.setBackground(drawable);
        viewHolder.certifying_shot.setClipToOutline(true);

        // 인증샷 크게보기
        viewHolder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /**
                 * 1. 프로필 사진
                 * 2. 유저 이름
                 * 3. 챌린지 제목
                 * 4. 인증샷
                 * 5. 날짜, 시간
                 * 6. 인증 유형, 타입
                 * 7. 신고 수
                 * */

                GET_CERTIFYING_SHOT_USER_PHOTO = currentItem.getPhoto();
                GET_CERTIFYING_SHOT_USER_NAME = currentItem.getName();
                GET_CERTIFYING_SHOT_TITLE = currentItem.getWake_Info_Title();
                GET_CERTIFYING_SHOT_FILE = currentItem.getWake_Evidence_File();
                GET_CERTIFYING_SHOT_DATE = currentItem.getWake_Evidence_Date();
                GET_CERTIFYING_SHOT_TYPE = currentItem.getWake_Evidence_File_Type();

                Activity_Project_Detail_Page.GET_PROJECT_CERTI_USER = currentItem.getId();               // 신고 당할 유저
                Activity_Project_Detail_Page.GET_PROJECT_CERTI_INDEX = currentItem.getWake_Evidence_No();// 신고할 인증샷 인덱스

                Intent intent = new Intent(mContext, Activity_Project_Detail_in_Certi_Shot_Detail.class);
                mContext.startActivity(intent);
            }
        });

        Log.e(TAG, "onBindViewHolder: index: " + currentItem.getWake_Evidence_No());
    }

    @Override
    public int getItemCount()
    {
        // 이미지 표시제한
//        if (itemCertifyingLists.size() > limit)
//        {
//            Log.e(TAG, "getItemCount: ?" );
//            return limit;
//        }
//
//        else
//        {
//            Log.e(TAG, "getItemCount: ??" );
        Log.e(TAG, "getItemCount: itemCertifyingLists.size(): " + itemCertifyingLists.size());
        return itemCertifyingLists.size();
//        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView certifying_shot;
        public View view;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            view = itemView;
            certifying_shot = itemView.findViewById(R.id.certifying_shot);
        }
    }
}
