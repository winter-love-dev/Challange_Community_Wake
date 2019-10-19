/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.detection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.customview.OverlayView.DrawCallback;
import org.tensorflow.lite.examples.detection.env.BorderedText;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Classifier;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Activity_Proof_Ready.GET_DETECT;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track objects
 * TensorFlowMultiBoxDetector 및 ObjectTracker를 사용하여 오브젝트를 감지 한 후 추적하는 활동
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener
{
    private static final Logger LOGGER = new Logger();

    String TAG = "DetectorActivity";

    // Configuration values for the prepackaged SSD model.
    // 사전 패키지 SSD 모델의 구성 값.
    private static final int TF_OD_API_INPUT_SIZE = 300;

    // 텐서플로우 오브젝트 디텍터 api 정량화(QUANTIZED)...?
    private static final boolean TF_OD_API_IS_QUANTIZED = true;

    // 학습된 파일
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";

    // 인식된 객체 라벨링 목록
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";

    // 탐지모드: 텐서플로우의 오브젝트 디텍트 api 사용
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;

    // Minimum detection confidence to track a detection.
    // 탐지를 추적하기위한 최소 탐지 신뢰도? 50%만 믿는다는 말인가?
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;

    // 유지 비율
    private static final boolean MAINTAIN_ASPECT = false;

    // 원하는 미리보기 크기 (카메라뷰 크기? 맞나?)
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

    // 카메라뷰를 비트맵으로 저장 할지에 대한 여부
    private static final boolean SAVE_PREVIEW_BITMAP = false;

    // 라벨링된 텍스트의 사이즈
    private static final float TEXT_SIZE_DIP = 1;

    // 오버레이(OverlayView): 위에 까는 것
    // 즉 인식된 사물 위에 라벨과 사각형을 그려주는 레이아웃 뷰
    OverlayView trackingOverlay;

    // 카메라 방향 (가로 or 세로)
    // 기기의 방향을 처음부터 설정하는게 아닌,
    // 기기의 방향이 바뀌면 getScreenOrientation() 메소드로
    // 기기의 방향을 감지해서 방향을 실시간으로 바꾸는 것으로 추정됨
    private Integer sensorOrientation;

    // 인식한 오브젝트를 분류 해주는 '분류기 클래스'
    private Classifier detector;

    // 마지막으로 인식된 시간 (몇 초전)을 담는 변수로 보임
    private long lastProcessingTimeMs;

    // 사물이 인식되면 카메라뷰 전체를 비트맵에 담는 것?
    private Bitmap rgbFrameBitmap = null;

    // 인식된 사물만 잘라서 비트맵에 담는 것?
    private Bitmap croppedBitmap = null;

    // 자른 비트맵 이미지를 복사해서 어디에 쓴다는거지?
    private Bitmap cropCopyBitmap = null;

    // 컴퓨터로 탐지하는게 아닌 휴대폰으로 탐지한다는 것?
    private boolean computingDetection = false;

    // 시간 기록.
    // 뭘 기록하는걸까? 인식된 시간?
    // 사물을 마지막으로 인식한 시간?
    private long timestamp = 0;

    // 자르기 변환 프레임??
    private Matrix frameToCropTransform;

    // 프레임 저장??
    private Matrix cropToFrameTransform;

    // 인식된 사물에 사각형을 그려주는 클래스
    private MultiBoxTracker tracker;

    // 경계 텍스트
    // 인식된 박스 안의 텍스트?
    private BorderedText borderedText;

    private TextView od_shot_notice;

    String dectection_object;

    Handler handler;

    CircleImageView od_serti_shot_button;

    public static String OD_SHOT_SIGNAL;

    FrameLayout container;

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation)
    {
//        Log.e(TAG, "onPreviewSizeChosen: 실행됨");

        // 디스플레이의 크기에 따라 글씨의 크기를 유동적으로 변환한다는 뜻인가?
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());

//        TextView testbutton = findViewById(R.id.testbutton1);
//        testbutton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(DetectorActivity.this, org.tensorflow.lite.examples.posenet.PosenetCameraActivity.class);
//                startActivity(intent);
//            }
//        });

        od_shot_notice = findViewById(R.id.od_shot_notice);
        od_serti_shot_button = findViewById(R.id.od_serti_shot_button);
        container = findViewById(R.id.container);

        handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                Log.e(TAG, "onPreviewSizeChosen: dectection_object: " + dectection_object);

                if (dectection_object == null || dectection_object.length() == 0 || dectection_object.equals(null))
                {
//                    Log.e(TAG, "processImage: " + GET_DETECT + " 탐색중");
                    od_serti_shot_button.setEnabled(false);
                    od_serti_shot_button.setBorderWidth(0);
                    od_shot_notice.setText("탐색중");
                } else if (dectection_object.equals(GET_DETECT))
                {
//                    Log.e(TAG, "processImage: " + GET_DETECT +" 활성화");
                    od_serti_shot_button.setEnabled(true);
                    od_shot_notice.setText("촬영 가능합니다");
                    od_serti_shot_button.setBorderWidth(4);
                    od_serti_shot_button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Log.e(TAG, "onClick: 촬영버튼 누름" );
                            CameraConnectionFragment.takePicture();
                        }
                    });
                } else
                {
//                    Log.e(TAG, "processImage: " + GET_DETECT + " 비 활성화");
                    od_serti_shot_button.setEnabled(false);
                    od_serti_shot_button.setBorderWidth(0);
                    od_shot_notice.setText("탐색중");
                }
            }
        };

//        Log.e(TAG, "onPreviewSizeChosen: textSizePx: " + textSizePx);

        // 보드 텍스트 사이즈는 디스플레이의 크기에 따라 유동적으로 변환한다
        borderedText = new BorderedText(textSizePx);
//        Log.e(TAG, "onPreviewSizeChosen: borderedText: " + borderedText.toString());

        // 글씨체
        borderedText.setTypeface(Typeface.MONOSPACE);

        // 인식된 사물에 사각형을 그려주는 클래스
        tracker = new MultiBoxTracker(this);

        // 잘라내기 할 사이즈? TF_OD_API_INPUT_SIZE: 입력된 사이즈는 300
        int cropSize = TF_OD_API_INPUT_SIZE;

        try
        {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);

            // TFLiteObjectDetectionAPIModel: 사물인식을 하기위해 학습된 클래스
            // Classifier 인식된 사물을 분류해주는 리스트 클래스
            // 사물을 인식하고 분류한 결과를 Classifier 클래스에 담는다 (TFLiteObjectDetectionAPIModel을 Classifier로)

            // 자를 사이즈는 300
            cropSize = TF_OD_API_INPUT_SIZE;

        } //

        catch (final IOException e)
        {
            e.printStackTrace();

            LOGGER.e(e, "Exception initializing classifier!");
            Log.e(TAG, "onPreviewSizeChosen: 분류기를 초기화 할 수 없습니다.");
            Log.e(TAG, "onPreviewSizeChosen: Classifier could not be initialized");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        // 카메라뷰? 미리보기? 의 너비, 높이
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);

        // RGB 프레임 비트 맵
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        // 자르기 변환 프레임
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        //
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        // 인식된 사물 위에 그릴 사각형 뷰
        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new DrawCallback()
                {
                    @Override
                    public void drawCallback(final Canvas canvas)
                    {
                        // 인식된 사물 위에 사각형 그리기
                        tracker.draw(canvas);
//                        Log.e(TAG, "drawCallback: tracker.draw(canvas)");
                        if (isDebug())
                        {
                            // 이건 뭘까?
                            tracker.drawDebug(canvas);
                            Log.e(TAG, "drawCallback 디버깅 모드: tracker.drawDebug(canvas)");
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
        Log.e(TAG, "onPreviewSizeChosen: tracker.setFrameConfiguration( " + previewWidth + ", " + previewHeight + ", " + sensorOrientation + ") ");
    }



    @Override
    protected void processImage()
    {
//        Log.e(TAG, "processImage: 실행됨");

        ++timestamp;
        final long currTimestamp = timestamp;

        // 인식된 사물위에 그린 사각형 지우기? (post Invalidate: 무효화 게시)
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant
        // 이 방법은 재진입 할 수 없으므로 뮤텍스가 필요하지 않습니다
        if (computingDetection)
        {
            // 다음 이미지 준비?
            readyForNextImage();
            return;
        }

        computingDetection = true;
//        Log.e(TAG, "run: computingDetection: " + computingDetection);
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
//        Log.e(TAG, "processImage: rgbFrameBitmap.setPixels(" + getRgbBytes() + ", 0, " + previewWidth + ", 0, 0, " + previewWidth + "," + previewHeight + ")");

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        // For examining the actual TF input.
        // 실제 TF 입력 검사
        if (SAVE_PREVIEW_BITMAP)
        {
            // 비트맵 저장?
            ImageUtils.saveBitmap(croppedBitmap);
//            Log.e(TAG, "processImage: ImageUtils.saveBitmap(" + croppedBitmap + ")");
        }

        runInBackground(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        LOGGER.i("Running detection on image " + currTimestamp);
//                        Log.e(TAG, "Running detection on image " + currTimestamp);

                        final long startTime = SystemClock.uptimeMillis();
                        final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
//                        Log.e(TAG, "run: results: getId:      " + results.get(0).getId() );         // 사물 아이디인가? 인덱스?
//                        Log.e(TAG, "run: results: Title:      " + results.get(0).getTitle() );      // 인식된 사물
//                        Log.e(TAG, "run: results: Confidence: " + results.get(0).getConfidence() ); // 자신?
//                        Log.e(TAG, "run: results: Location:   " + results.get(0).getLocation() );   // 좌표
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

//                        Log.e(TAG, "run: lastProcessingTimeMs: " + lastProcessingTimeMs);

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE)
                        {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                Log.e(TAG, "run: case TF_OD_API");
                                break;
                        }

                        final List<Classifier.Recognition> mappedRecognitions =
                                new LinkedList<Classifier.Recognition>();

                        // todo: 인식된 사물을 출력하는 곳.
                        // 리스트 변수 result 0번 인덱스에 인식된 사물이 출력됨
                        for (final Classifier.Recognition result : results)
                        {
//                            Log.e(TAG, "run: Classifier.Recognition results: getId:     0 " + results.get(0).getId());
//                            Log.e(TAG, "run: Classifier.Recognition results: Title:     0 " + results.get(0).getTitle());
//                            Log.e(TAG, "run: Classifier.Recognition results: Confidence:0 " + results.get(0).getConfidence());
//                            Log.e(TAG, "run: Classifier.Recognition results: Location:  0 " + results.get(0).getLocation());

                            Timer timer = new Timer(true);
                            TimerTask timerTask = new TimerTask()
                            {
                                @Override
                                public void run()
                                {
//                                    Log.e(TAG, "timer run");
                                    dectection_object = results.get(0).getTitle();

                                    // todo: 출력되는 오브젝트를 로그에서 확인하기
//                                    Log.e(TAG, "run: dectection_object: " + dectection_object );
                                    Message msg = handler.obtainMessage();

                                    handler.sendMessage(msg);
                                }
                                @Override
                                public boolean cancel()
                                {
                                    Log.e(TAG, "timer cancel");
                                    return super.cancel();
                                }
                            };

                            // 탐색 주기: 2초
                            timer.schedule(timerTask, 3000);


                            final RectF location = result.getLocation();
                            if (location != null && result.getConfidence() >= minimumConfidence)
                            {
                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);

                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                        }

                        for (int i = 0; i < results.size(); i++)
                        {
//                            Log.e(TAG, "run: Classifier.Recognition results: getId:     i " + results.get(i).getId());
//                            Log.e(TAG, "run: Classifier.Recognition results: Title:    " + i + " " + results.get(i).getTitle());
//                            Log.e(TAG, "run: Classifier.Recognition results: Confidence:i " + results.get(i).getConfidence());
//                            Log.e(TAG, "run: Classifier.Recognition results: Location:  i " + results.get(i).getLocation());
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
//                        Log.e(TAG, "run: tracker.trackResults(" + mappedRecognitions + "," + currTimestamp + ")");

                        trackingOverlay.postInvalidate();

                        computingDetection = false;
//                        Log.e(TAG, "run: computingDetection: " + computingDetection);

                        runOnUiThread(
                                new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        showFrameInfo(previewWidth + "x" + previewHeight);
                                        showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                                        showInference(lastProcessingTimeMs + "ms");
                                    }
                                });
                    }
                });
    }


    @Override
    protected int getLayoutId()
    {
//        Log.e(TAG, "getLayoutId: 실행됨" );
//        Log.e(TAG, "getLayoutId: 인식된 사물 위에 그릴 사각형 레이아웃 불러오기");
        return R.layout.camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize()
    {
//        Log.e(TAG, "getDesiredPreviewFrameSize: 설정된 미리보기 크기: Width: " + DESIRED_PREVIEW_SIZE.getWidth() + ", Height: " + DESIRED_PREVIEW_SIZE.getHeight());
        return DESIRED_PREVIEW_SIZE;
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen checkpoints.
    // 사용할 감지 모델 : 기본적으로 Tensorflow Object Detection API 고정 체크 포인트를 사용합니다.
    private enum DetectorMode
    {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked)
    {
//        Log.e(TAG, "setUseNNAPI: 실행됨, setUseNNAPI(" + isChecked + ")");
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }

    @Override
    protected void setNumThreads(final int numThreads)
    {
//        Log.e(TAG, "setNumThreads: 실행됨, numThreads: (" + numThreads + ")");
        runInBackground(() -> detector.setNumThreads(numThreads));
    }

    @Override
    public synchronized void onStop()
    {
        super.onStop();
        finish();
    }
}
