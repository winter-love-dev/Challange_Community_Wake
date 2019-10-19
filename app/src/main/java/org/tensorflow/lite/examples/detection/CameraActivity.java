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

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.wake.FragmentProof.Activity_Certifying_Shot;
import org.tensorflow.lite.examples.detection.wake.FragmentProof.Activity_Done_Certify;

import static org.tensorflow.lite.examples.detection.DetectorActivity.OD_SHOT_SIGNAL;

public abstract class CameraActivity extends AppCompatActivity
        implements OnImageAvailableListener,
        Camera.PreviewCallback,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener
{
    private static final Logger LOGGER = new Logger();

    String TAG = "PosenetCameraActivity";

    private static final int PERMISSIONS_REQUEST = 1;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    private boolean debug = false;
    private Handler handler;
    private HandlerThread handlerThread;
    private boolean useCamera2API;
    private boolean isProcessingFrame = false;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;
    private Runnable postInferenceCallback;
    private Runnable imageConverter;

    private LinearLayout bottomSheetLayout;
    private LinearLayout gestureLayout;
    private BottomSheetBehavior sheetBehavior;

    protected TextView frameValueTextView, cropValueTextView, inferenceTimeTextView;
    protected ImageView bottomSheetArrowImageView;
    private ImageView plusImageView, minusImageView;
    private SwitchCompat apiSwitchCompat;
    private TextView threadsTextView;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        LOGGER.d("onCreate " + this);
//        Log.e(TAG, "onCreate: 실행됨");
        super.onCreate(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (hasPermission())
        {
            setFragment();
        } else
        {
            requestPermission();
        }

//        threadsTextView = findViewById(R.id.threads);
//        plusImageView = findViewById(R.id.plus);
//        minusImageView = findViewById(R.id.minus);
//        apiSwitchCompat = findViewById(R.id.api_info_switch);
//        gestureLayout = findViewById(R.id.gesture_layout);
//        bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
//        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
//        bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);

//        ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener()
//                {
//                    @Override
//                    public void onGlobalLayout()
//                    {
//                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
//                        {
//                            gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                        } else
//                        {
//                            gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        }
//                        //                int width = bottomSheetLayout.getMeasuredWidth();
////                        int height = gestureLayout.getMeasuredHeight();
////                        sheetBehavior.setPeekHeight(height);
//                    }
//                });
//        sheetBehavior.setHideable(false);

//        sheetBehavior.setBottomSheetCallback(
//                new BottomSheetBehavior.BottomSheetCallback()
//                {
//                    @Override
//                    public void onStateChanged(@NonNull View bottomSheet, int newState)
//                    {
//                        switch (newState)
//                        {
//                            case BottomSheetBehavior.STATE_HIDDEN:
//                                break;
//                            case BottomSheetBehavior.STATE_EXPANDED:
//                            {
//                                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down);
//                            }
//                            break;
//                            case BottomSheetBehavior.STATE_COLLAPSED:
//                            {
//                                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
//                            }
//                            break;
//                            case BottomSheetBehavior.STATE_DRAGGING:
//                                break;
//                            case BottomSheetBehavior.STATE_SETTLING:
//                                bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up);
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onSlide(@NonNull View bottomSheet, float slideOffset)
//                    {
//                    }
//                });

//        frameValueTextView = findViewById(R.id.frame_info);
//        cropValueTextView = findViewById(R.id.crop_info);
//        inferenceTimeTextView = findViewById(R.id.inference_info);

//        apiSwitchCompat.setOnCheckedChangeListener(this);
//        plusImageView.setOnClickListener(this);
//        minusImageView.setOnClickListener(this);
    }

    protected int[] getRgbBytes()
    {
//        Log.e(TAG, "getRgbBytes: 실행됨");
        imageConverter.run();
        return rgbBytes;
    }

    protected int getLuminanceStride()
    {
        return yRowStride;
    }

    protected byte[] getLuminance()
    {
        return yuvBytes[0];
    }

    Camera.PictureCallback jpegCallback;
    String str;

    /**
     * Callback for android.hardware.Camera API
     */
    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera)
    {
/*
        Log.e(TAG, "onPreviewFrame: 실행됨");

        if (OD_SHOT_SIGNAL == null || OD_SHOT_SIGNAL.length() == 0 || OD_SHOT_SIGNAL.equals(null))
        {
            Log.e(TAG, "onPreviewFrame: 촬영 대기중");
        } else if (OD_SHOT_SIGNAL.equals("OD_SHOT_SIGNAL"))
        {
            Log.e(TAG, "onPreviewFrame: 촬영 신호 받음");

            camera.takePicture(null, null, jpegCallback);

            camera.autoFocus(new Camera.AutoFocusCallback()
            {
                public void onAutoFocus(boolean success, Camera camera)
                {
                    if (success)
                    {
                        Log.e(TAG, "onAutoFocus: 초점 새로고침");
                    }
                }
            });

            jpegCallback = new Camera.PictureCallback()
            {
                @Override
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    Log.e(TAG, "onPictureTaken: 도달함");
                    FileOutputStream outStream = null;
                    try
                    {
                        str = String.format("/sdcard/%d.jpg",
                                System.currentTimeMillis());
                        outStream = new FileOutputStream(str);

                        outStream.write(data);
                        outStream.close();
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                    }

                    Toast.makeText(getApplicationContext(),
                            "Picture Saved", Toast.LENGTH_LONG).show();

                    // todo: 촬영 및 저장 완료되면 다음 액티비티로 이동하기 (인증 완료 액티비티)
                    Intent intent = new Intent(PosenetCameraActivity.this, Activity_Posenet_Done_Certify.class);
                    intent.putExtra("strParamName", str);
                    intent.putExtra("certiFrom", "camera");
                    startActivity(intent);
                }
            };
        }
*/

        if (isProcessingFrame)
        {
//            Log.e(TAG, "onPreviewFrame: 실행됨");
            LOGGER.w("Dropping frame!");
            return;
        }

        try
        {
            // Initialize the storage bitmaps once when the resolution is known.
            // 해상도가 알려진 경우 스토리지 비트 맵을 한 번 초기화
            if (rgbBytes == null)
            {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                previewHeight = previewSize.height;
                previewWidth = previewSize.width;
                rgbBytes = new int[previewWidth * previewHeight];
                onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
            }
        } catch (final Exception e)
        {
            LOGGER.e(e, "Exception!");
            return;
        }

        isProcessingFrame = true;
        yuvBytes[0] = bytes;
        yRowStride = previewWidth;

        imageConverter =
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
                    }
                };

        postInferenceCallback =
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        camera.addCallbackBuffer(bytes);
                        isProcessingFrame = false;
                    }
                };

        processImage();
    }

    /**
     * Callback for Camera2 API
     */
    @Override
    public void onImageAvailable(final ImageReader reader)
    {
//        Log.e(TAG, "onImageAvailable: 실행됨" );

        // We need wait until we have some size from onPreviewSizeChosen
        // onPreviewSizeChosen에서 크기가 나올 때까지 기다려야합니다.

        if (previewWidth == 0 || previewHeight == 0)
        {
            return;
        }
        if (rgbBytes == null)
        {
            rgbBytes = new int[previewWidth * previewHeight];
        }
        try
        {
            final Image image = reader.acquireLatestImage();

            if (image == null)
            {
                return;
            }

            if (isProcessingFrame)
            {
                image.close();
                return;
            }
            isProcessingFrame = true;
            Trace.beginSection("imageAvailable");
            final Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter =
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ImageUtils.convertYUV420ToARGB8888(
                                    yuvBytes[0],
                                    yuvBytes[1],
                                    yuvBytes[2],
                                    previewWidth,
                                    previewHeight,
                                    yRowStride,
                                    uvRowStride,
                                    uvPixelStride,
                                    rgbBytes);
                        }
                    };

            postInferenceCallback =
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            image.close();
                            isProcessingFrame = false;
                        }
                    };

            processImage();
        } catch (final Exception e)
        {
            LOGGER.e(e, "Exception!");
            Trace.endSection();
            return;
        }
        Trace.endSection();
    }

    @Override
    public synchronized void onStart()
    {
        LOGGER.d("onStart " + this);
        super.onStart();
    }

    @Override
    public synchronized void onResume()
    {
        LOGGER.d("onResume " + this);
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onPause()
    {
        LOGGER.d("onPause " + this);

        handlerThread.quitSafely();
        try
        {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e)
        {
            LOGGER.e(e, "Exception!");
        }

        super.onPause();
    }

    @Override
    public synchronized void onStop()
    {
        LOGGER.d("onStop " + this);
        super.onStop();
    }

    @Override
    public synchronized void onDestroy()
    {
        LOGGER.d("onDestroy " + this);
        super.onDestroy();
    }

    protected synchronized void runInBackground(final Runnable r)
    {
        if (handler != null)
        {
//            Log.e(TAG, "runInBackground: 실행됨");
            handler.post(r);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults)
    {
//        Log.e(TAG, "onRequestPermissionsResult: 실행됨" );
        if (requestCode == PERMISSIONS_REQUEST)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                setFragment();
            } else
            {
                requestPermission();
            }
        }
    }

    private boolean hasPermission()
    {
//        Log.e(TAG, "hasPermission: 실행됨" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else
        {
            return true;
        }
    }

    private void requestPermission()
    {
//        Log.e(TAG, "requestPermission: 실행됨" );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA))
            {
                Toast.makeText(
                        CameraActivity.this,
                        "Camera permission is required for this demo",
                        Toast.LENGTH_LONG)
                        .show();
            }
            requestPermissions(new String[]{PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
        }
    }

    // Returns true if the device supports the required hardware level, or better.
    // 장치가 필요한 하드웨어 수준 이상을 지원하면 true를 반환합니다.
    private boolean isHardwareLevelSupported(
            CameraCharacteristics characteristics, int requiredLevel)
    {
//        Log.e(TAG, "isHardwareLevelSupported: 실행됨" );
        int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
        {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    private String chooseCamera()
    {
//        Log.e(TAG, "chooseCamera: 실행됨" );

        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try
        {
            for (final String cameraId : manager.getCameraIdList())
            {
                final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                // 이 샘플에서는 전면 카메라를 사용하지 않습니다
                final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT)
                {
                    continue;
                }

                final StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                if (map == null)
                {
                    continue;
                }

                // Fallback to camera1 API for internal cameras that don't have full support. This should help with legacy situations where using the camera2 API causes distorted or otherwise broken previews.
                // 완벽하게 지원되지 않는 내부 카메라의 경우 camera1 API로 대체됩니다.
                // 이는 camera2 API를 사용하면 미리보기가 왜곡되거나 깨지는 레거시 상황에 도움이됩니다.
                useCamera2API =
                        (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                                || isHardwareLevelSupported(
                                characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
                LOGGER.i("Camera API lv2?: %s", useCamera2API);
                return cameraId;
            }
        } catch (CameraAccessException e)
        {
            LOGGER.e(e, "Not allowed to access camera");
        }

        return null;
    }

    protected void setFragment()
    {
//        Log.e(TAG, "setFragment: 실행됨" );
        String cameraId = chooseCamera();

//        Log.e(TAG, "setFragment: cameraId: " + cameraId );

        Fragment fragment;
        if (useCamera2API)
        {
            CameraConnectionFragment camera2Fragment =
                    CameraConnectionFragment.newInstance(
                            new CameraConnectionFragment.ConnectionCallback()
                            {
                                @Override
                                public void onPreviewSizeChosen(final Size size, final int rotation)
                                {
                                    previewHeight = size.getHeight();
                                    previewWidth = size.getWidth();
                                    CameraActivity.this.onPreviewSizeChosen(size, rotation);
                                }
                            },
                            this,
                            getLayoutId(),
                            getDesiredPreviewFrameSize());

            camera2Fragment.setCamera(cameraId);
            fragment = camera2Fragment;
        } else
        {
            fragment =
                    new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
        }

        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//        Log.e(TAG, "setFragment: fragment: " + fragment );
    }

    protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes)
    {
        // Because of the variable row stride it's not possible to know in advance the actual necessary dimensions of the yuv planes.
        // 가변 행 보폭으로 인해 yuv 평면의 실제 필요한 치수를 미리 알 수 없습니다.

//        Log.e(TAG, "fillBytes: 실행됨" );

        for (int i = 0; i < planes.length; ++i)
        {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null)
            {
                LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    public boolean isDebug()
    {
//        Log.e(TAG, "isDebug: 실행됨" );
        return debug;
    }

    protected void readyForNextImage()
    {
        if (postInferenceCallback != null)
        {
            // 이미지 인식 스레드?
            postInferenceCallback.run();
//            Log.e(TAG, "readyForNextImage: 실행됨");
        }
    }

    protected int getScreenOrientation()
    {
//        Log.e(TAG, "getScreenOrientation: 실행됨");
        switch (getWindowManager().getDefaultDisplay().getRotation())
        {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
//        Log.e(TAG, "onCheckedChanged: 실행됨 " );
        setUseNNAPI(isChecked);
//        if (isChecked) apiSwitchCompat.setText("NNAPI");
//        else apiSwitchCompat.setText("TFLITE");
    }

    @Override
    public void onClick(View v)
    {
//        Log.e(TAG, "onClick: 실행됨. 뷰 클릭 감지" );
//        if (v.getId() == R.id.plus)
//        {
//            String threads = threadsTextView.getText().toString().trim();
//            int numThreads = Integer.parseInt(threads);
//            if (numThreads >= 9) return;
//            numThreads++;
//            threadsTextView.setText(String.valueOf(numThreads));
//            setNumThreads(numThreads);
//        } else if (v.getId() == R.id.minus)
//        {
//            String threads = threadsTextView.getText().toString().trim();
//            int numThreads = Integer.parseInt(threads);
//            if (numThreads == 1)
//            {
//                return;
//            }
//            numThreads--;
//            threadsTextView.setText(String.valueOf(numThreads));
//            setNumThreads(numThreads);
//        }
    }

    protected void showFrameInfo(String frameInfo)
    {
//        Log.e(TAG, "showFrameInfo: 실행됨" );
//        frameValueTextView.setText(frameInfo);
    }

    protected void showCropInfo(String cropInfo)
    {
//        Log.e(TAG, "showCropInfo: 실행됨" );
//        cropValueTextView.setText(cropInfo);
    }

    protected void showInference(String inferenceTime)
    {
//        Log.e(TAG, "showInference: 실행됨" );
//        inferenceTimeTextView.setText(inferenceTime);
    }

    protected abstract void processImage();

    protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

    protected abstract int getLayoutId();

    protected abstract Size getDesiredPreviewFrameSize();

    protected abstract void setNumThreads(int numThreads);

    protected abstract void setUseNNAPI(boolean isChecked);
}
