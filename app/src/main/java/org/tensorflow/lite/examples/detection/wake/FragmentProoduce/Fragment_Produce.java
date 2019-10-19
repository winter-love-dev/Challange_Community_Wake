package org.tensorflow.lite.examples.detection.wake.FragmentProoduce;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import org.tensorflow.lite.examples.detection.R;
import org.tensorflow.lite.examples.detection.wake.FragmentHome.OnBackPressedListener;
import org.tensorflow.lite.examples.detection.wake.Produce.Activity_produce_Suggestion_List;
import org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_1;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Produce extends Fragment implements OnBackPressedListener
{
    // 태그 선언
    private String TAG = "Fragment_Produce";

    // Context 선언
    private View View_Fragment_Produce; // 프래그먼트 뷰
    private Context Context_Fragment_Produce; // 컨텍스트

    // 버튼 선언
    private LinearLayout fra_produce_Suggestion, fra_produce_Create;

    public Fragment_Produce()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Fragment_Produce = inflater.inflate(R.layout.fragment_produce, container, false);

        // Context 선언
        Context_Fragment_Produce = getActivity().getApplicationContext();

        // View Find
        fra_produce_Suggestion = View_Fragment_Produce.findViewById(R.id.fra_produce_Suggestion);
        fra_produce_Create = View_Fragment_Produce.findViewById(R.id.fra_produce_Create);


        fra_produce_Suggestion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Fragment_Produce, Activity_produce_Suggestion_List.class);
                startActivity(intent);
            }
        });

        fra_produce_Create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Fragment_Produce, Activity_Product_Create_1.class);
                startActivity(intent);
            }
        });

        return View_Fragment_Produce;
    }

    @Override
    public void onBackPressed()
    {

    }
}
