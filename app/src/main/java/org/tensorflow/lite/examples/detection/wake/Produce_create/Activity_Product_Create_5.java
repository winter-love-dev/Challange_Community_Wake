package org.tensorflow.lite.examples.detection.wake.Produce_create;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.examples.detection.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.tensorflow.lite.examples.detection.wake.Activity_Home.getId;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_1.GET_PRODUCE_INTRO_AND_CERTI;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_1.GET_PRODUCE_TITLE;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_2.GET_SERTI_COUNT;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_3.GET_CERTI_DAY;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_3.GET_END_DATE;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_3.GET_START_DATE;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_4.GET_AUTH_END_TIME;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_4.GET_AUTH_START_TIME;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_4.GET_AUTH_TYPE;
import static org.tensorflow.lite.examples.detection.wake.Produce_create.Activity_Product_Create_4.GET_POROJECT_JOIN_PRICE;

/**
 * 프로젝트 생성하기 5페이지
 * 1. 썸네일 사진을 선택한다
 * 2. 진행할 프로젝트의 카테고리를 선택한다.
 * 3. 이전 페이지에서 static 변수에 담은 값들을 로그에서 확인한다
 * 4. 5페이지 까지 입력된 값들을 서버로 전송한다.
 */
public class Activity_Product_Create_5 extends AppCompatActivity
{
    private String TAG = "Activity_Product_Create_5";

    private Button
            produce_create_page_5_cancel  // 페이지 닫기
            , produce_create_page_5_done    // 작업 완료. 서버로 값 전송하기
            ;

    private Spinner produce_create_page_5_category  // 주제 카테고리 선택하기
            ;

    private ImageView
            produce_create_page_5_photo_select  // 썸네일 선택버튼
            , produce_create_page_5_photo_area  // 선택한 썸네일을 표시할 영역
            ;

    private TextView
            produce_create_page_5_title       // 썸네일 영역에 프로젝트 제목 띄우기
            , produce_create_page_5_duration  // 썸네일 영역에 프로젝트 진행기간 띄우기
            ;

    private FrameLayout thumb_area;

    private LinearLayout
            category_area //
            , done_area//
            ;

    // 프로필 사진을 저장할 비트맵
    private Bitmap bitmap;

    private String Category;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create_5);

        // ViewFind
        produce_create_page_5_category = findViewById(R.id.produce_create_page_5_category);
        produce_create_page_5_cancel = findViewById(R.id.produce_create_page_5_cancel);
        produce_create_page_5_done = findViewById(R.id.produce_create_page_5_done);
        produce_create_page_5_photo_select = findViewById(R.id.produce_create_page_5_photo_select);
        produce_create_page_5_photo_area = findViewById(R.id.produce_create_page_5_photo_area);
        produce_create_page_5_title = findViewById(R.id.produce_create_page_5_title);
        produce_create_page_5_duration = findViewById(R.id.produce_create_page_5_duration);
        category_area = findViewById(R.id.category_area);
        thumb_area = findViewById(R.id.thumb_area);
        done_area = findViewById(R.id.done_area);

        // 1페이지부터 넘어온 값들 확인하기
        Log.e(TAG, "onCreate: GET_PRODUCE_TITLE             : " + GET_PRODUCE_TITLE);
        Log.e(TAG, "onCreate: GET_PRODUCE_INTRO_AND_CERTI   : " + GET_PRODUCE_INTRO_AND_CERTI);
        Log.e(TAG, "onCreate: GET_SERTI_COUNT               : " + GET_SERTI_COUNT);
        Log.e(TAG, "onCreate: GET_START_DATE                : " + GET_START_DATE);
        Log.e(TAG, "onCreate: GET_END_DATE                  : " + GET_END_DATE);
        Log.e(TAG, "onCreate: GET_AUTH_TYPE                 : " + GET_AUTH_TYPE);
        Log.e(TAG, "onCreate: GET_POROJECT_JOIN_PRICE       : " + GET_POROJECT_JOIN_PRICE);
        Log.e(TAG, "onCreate: GET_AUTH_START_TIME           : " + GET_AUTH_START_TIME);
        Log.e(TAG, "onCreate: GET_AUTH_END_TIME             : " + GET_AUTH_END_TIME);

        // 썸네일 영역에 프로젝트 제목 띄우기
        produce_create_page_5_title.setText(GET_PRODUCE_TITLE);

        // 썸네일 영역에 프로젝트 진행기간 띄우기
        produce_create_page_5_duration.setText(/*"진행 기간: " + */GET_START_DATE + " ~ " + GET_END_DATE);

        // todo: 썸네일 이미지 선택하기
        ThubChoose();

        // 화면 닫기
        produce_create_page_5_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    // todo: 카테고리 설정하기
    private void CategoryChooes()
    {
        category_area.setVisibility(View.VISIBLE);

        final ArrayList CategoryPickList = new ArrayList<>();
        CategoryPickList.add("역량"); // ability
        CategoryPickList.add("건강"); // health
        CategoryPickList.add("관계"); // relation
        CategoryPickList.add("자산"); // Assets
        CategoryPickList.add("생활"); // life
        CategoryPickList.add("취미"); // hobby

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                CategoryPickList);

        produce_create_page_5_category.setAdapter(arrayAdapter);

        produce_create_page_5_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.e(TAG, "onItemSelected: CategoryPickList.get(position): " + CategoryPickList.get(position));

                if ("역량".equals(CategoryPickList.get(position)))
                {
                    Category = "역량";
                    Log.e(TAG, "onItemSelected: Category: " + Category);
                } else if ("건강".equals(CategoryPickList.get(position)))
                {
                    Category = "건강";
                    Log.e(TAG, "onItemSelected: Category: " + Category);
                } else if ("관계".equals(CategoryPickList.get(position)))
                {
                    Category = "관계";
                    Log.e(TAG, "onItemSelected: Category: " + Category);
                } else if ("자산".equals(CategoryPickList.get(position)))
                {
                    Category = "자산";
                    Log.e(TAG, "onItemSelected: Category: " + Category);
                } else if ("생활".equals(CategoryPickList.get(position)))
                {
                    Category = "생활";
                    Log.e(TAG, "onItemSelected: Category: " + Category);
                } else if ("취미".equals(CategoryPickList.get(position)))
                {
                    Category = "취미";
                    Log.e(TAG, "onItemSelected: Category: " + Category);
                }

//                if ("역량".equals(CategoryPickList.get(position)))
//                {
//                    Category = "ability";
//                    Log.e(TAG, "onItemSelected: Category: " + Category);
//                } else if ("건강".equals(CategoryPickList.get(position)))
//                {
//                    Category = "health";
//                    Log.e(TAG, "onItemSelected: Category: " + Category);
//                } else if ("관계".equals(CategoryPickList.get(position)))
//                {
//                    Category = "relation";
//                    Log.e(TAG, "onItemSelected: Category: " + Category);
//                } else if ("자산".equals(CategoryPickList.get(position)))
//                {
//                    Category = "Assets";
//                    Log.e(TAG, "onItemSelected: Category: " + Category);
//                } else if ("생활".equals(CategoryPickList.get(position)))
//                {
//                    Category = "life";
//                    Log.e(TAG, "onItemSelected: Category: " + Category);
//                } else if ("취미".equals(CategoryPickList.get(position)))
//                {
//                    Category = "hobby";
//                    Log.e(TAG, "onItemSelected: Category: " + Category);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // todo: 프로젝트 등록하기
        CreateProject();
    }

    // todo: 프로젝트 등록하기
    private void CreateProject()
    {
        produce_create_page_5_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 입력한 정보를 php POST로 DB에 전송합니다.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/addWakeProject.php",
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
                                        Toast.makeText(Activity_Product_Create_5.this, "작성 완료", Toast.LENGTH_SHORT).show();

                                        // todo: 전송 성공하면 현재 액티비티와 이전 액티비티를 모두 종료한다.
                                        Activity_Product_Create_1 activity_product_create_1 = (Activity_Product_Create_1) Activity_Product_Create_1.Activity_Product_Create_1;
                                        activity_product_create_1.finish();

                                        Activity_Product_Create_2 activity_product_create_2 = (Activity_Product_Create_2) Activity_Product_Create_2.Activity_Product_Create_2;
                                        activity_product_create_2.finish();

                                        Activity_Product_Create_3 activity_product_create_3 = (Activity_Product_Create_3) Activity_Product_Create_3.Activity_Product_Create_3;
                                        activity_product_create_3.finish();

                                        Activity_Product_Create_4 activity_product_create_4 = (Activity_Product_Create_4) Activity_Product_Create_4.Activity_Product_Create_4;
                                        activity_product_create_4.finish();

                                        // 작성 완료하면 액티비티 종료
                                        finish();
                                    } else
                                    {
                                        Toast.makeText(Activity_Product_Create_5.this, "\"message\":\"error\" 문제발생.", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    Toast.makeText(Activity_Product_Create_5.this, "JSONException 문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(Activity_Product_Create_5.this, "VolleyError 문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onErrorResponse: error: " + error);
                            }
                        })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<>();

                        params.put("GET_PRODUCE_TITLE", GET_PRODUCE_TITLE);
                        params.put("GET_PRODUCE_INTRO_AND_CERTI", GET_PRODUCE_INTRO_AND_CERTI);

                        params.put("GET_SERTI_COUNT", GET_SERTI_COUNT);

                        params.put("GET_START_DATE", GET_START_DATE);
                        params.put("GET_END_DATE", GET_END_DATE);

                        params.put("GET_AUTH_TYPE", GET_AUTH_TYPE);

                        params.put("GET_POROJECT_JOIN_PRICE", GET_POROJECT_JOIN_PRICE);

//                        Log.e(TAG, "getParams: GET_POROJECT_JOIN_PRICE: " + GET_POROJECT_JOIN_PRICE);

                        params.put("GET_AUTH_START_TIME", GET_AUTH_START_TIME);
                        params.put("GET_AUTH_END_TIME", GET_AUTH_END_TIME);

                        params.put("ThumbPhoto", getStringImage(bitmap));
//                        Log.e(TAG, "getParams: bitmap: " + getStringImage(bitmap) );

                        params.put("Category", Category);

                        params.put("getId", getId);
                        params.put("GET_CERTI_DAY", GET_CERTI_DAY);


                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(Activity_Product_Create_5.this);
                requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
            }
        });
    }

    // todo: 썸네일 이미지 선택하기
    private void ThubChoose()
    {
        // 이미지 둥글게 만들기
        GradientDrawable drawable =
                (GradientDrawable) getDrawable(R.drawable.item_image_corner);
        produce_create_page_5_photo_area.setBackground(drawable);
        produce_create_page_5_photo_area.setClipToOutline(true);

        produce_create_page_5_photo_select.setBackground(drawable);
        produce_create_page_5_photo_select.setClipToOutline(true);

        thumb_area.setBackground(drawable);
        thumb_area.setClipToOutline(true);

        // 썸네일 선택해서 썸네일 영역에 띄우기 (사진 선택 버튼)
        produce_create_page_5_photo_select.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CropImage.activity() // 크롭하기 위한 이미지를 가져온다.
                        .setGuidelines(CropImageView.Guidelines.ON) // 이미지를 크롭하기 위한 도구 ,Guidelines를
                        .setAspectRatio(1, 1) // 수직, 수평 비율 설정 (1:1 비율로)
                        .start(Activity_Product_Create_5.this); // 실행한다.
            }
        });

//        // 썸네일 선택해서 썸네일 영역에 띄우기 (사진 영역 클릭)
//        produce_create_page_5_photo_area.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                CropImage.activity() // 크롭하기 위한 이미지를 가져온다.
//                        .setGuidelines(CropImageView.Guidelines.ON) // 이미지를 크롭하기 위한 도구 ,Guidelines를
//                        .setAspectRatio(1, 1) // 수직, 수평 비율 설정 (1:1 비율로)
//                        .start(Activity_Product_Create_5.this); // 실행한다.
//            }
//        });

        // 선택한 썸네일을 아래 이미지뷰에 띄우기 (produce_create_page_5_photo_area)
        //produce_create_page_5_photo_area
    }

    // todo: 선택한 이미지 받아와서 이미지뷰에 세팅하기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // 응답코드 입력 완료 && 응답결과 성공!
        //                      && 인텐트에 담긴 data = 사진 선택 여부가 null이면 안 됨.
        //                      && data에 사진 선택이 확인되면 getData()로 사진의 경로를 받아온다.
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK && result != null) // requestCode == 1 && /*&& data.getData() != null*/
            {
                // 사진의 경로를 담는다
                Uri filePath = result.getUri();
                try
                {
                    // 바로 위 변수인 filePath에 담은 경로로 이미지를 가져온다.
                    // bitmap 변수에 담는다.

//                    File file = new File(filePath.getPath());
//                    bitmap = (Bitmap) new Compressor(this).setQuality(20).compressToBitmap(file);
//                    Log.e(TAG, "onActivityResult: bitmap1: " + bitmap );

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    Log.e(TAG, "onActivityResult: bitmap2: " + bitmap );

                    // 선택버튼 숨기기
                    produce_create_page_5_photo_select.setVisibility(View.GONE);

                    // 이미지 세션
                    // 프로필 이미지 영역에 비트맵에 담은 이미지를 실행한다.
                    produce_create_page_5_photo_area.setImageBitmap(bitmap);

                    // todo: 카테고리 설정하기
                    CategoryChooes();

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    // 비트맵을 문자열로 변환하는 메소드
    public String getStringImage(Bitmap bitmap)
    {
        // 바이트 배열 사용.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 비트맵을 변환한다. 원래 100%였던 것을 50%의 품질로. 그리고 바이트 배열화 시킨다.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();

        // 베이스 64? 뭔 소린지 모르겠다.
        // 위키를 참고했다. (https://ko.wikipedia.org/wiki/베이스64)
        // 64진법이라고 한다. 64진법으로 인코딩 하는건가?
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        // 인코딩 결과를 반환한다.
        return encodedImage;
    }
}
