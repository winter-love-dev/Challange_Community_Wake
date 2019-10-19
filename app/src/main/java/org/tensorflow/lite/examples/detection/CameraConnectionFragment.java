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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.tensorflow.lite.examples.detection.customview.AutoFitTextureView;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.wake.FragmentProof.Activity_Done_Certify;

@SuppressLint("ValidFragment")
public class CameraConnectionFragment extends Fragment
{
    private static final Logger LOGGER = new Logger();
    String TAG = "CameraConnectionFragment";

    static Context Context_CameraConnectionFragment;

    /**
     * The camera preview size will be chosen to be the smallest frame by pixel size capable of
     * containing a DESIRED_SIZE x DESIRED_SIZE square.
     */
    private static final int MINIMUM_PREVIEW_SIZE = 320;

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    private static final String FRAGMENT_DIALOG = "dialog";

    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private final Semaphore cameraOpenCloseLock = new Semaphore(1);
    /**
     * A {@link OnImageAvailableListener} to receive frames as they are available.
     */
    private final OnImageAvailableListener imageListener;
    /**
     * The input size in pixels desired by TensorFlow (width and height of a square bitmap).
     */
    private final Size inputSize;
    /**
     * The layout identifier to inflate for this Fragment.
     */
    private final int layout;

    private final ConnectionCallback cameraConnectionCallback;
    private final CameraCaptureSession.CaptureCallback captureCallback =
            new CameraCaptureSession.CaptureCallback()
            {
                @Override
                public void onCaptureProgressed(
                        final CameraCaptureSession session,
                        final CaptureRequest request,
                        final CaptureResult partialResult)
                {
//                    Log.e(TAG, "onCaptureProgressed: 실행됨" );
                }

                @Override
                public void onCaptureCompleted(
                        final CameraCaptureSession session,
                        final CaptureRequest request,
                        final TotalCaptureResult result)
                {
//                    Log.e(TAG, "onCaptureCompleted: 실행됨" );
                }
            };
    /**
     * ID of the current {@link CameraDevice}.
     */
    private static String cameraId;
    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private static AutoFitTextureView textureView;
    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession captureSession;
    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private static CameraDevice cameraDevice;
    /**
     * The rotation in degrees of the camera sensor from the display.
     */
    private Integer sensorOrientation;
    /**
     * The {@link Size} of camera preview.
     */
    private Size previewSize;
    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread backgroundThread;
    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler backgroundHandler;
    /**
     * An {@link ImageReader} that handles preview frame capture.
     */
    private ImageReader previewReader;
    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder previewRequestBuilder;
    /**
     * {@link CaptureRequest} generated by {@link #previewRequestBuilder}
     */
    private CaptureRequest previewRequest;
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback stateCallback =
            new CameraDevice.StateCallback()
            {
                @Override
                public void onOpened(final CameraDevice cd)
                {
                    // This method is called when the camera is opened.  We start camera preview here.
                    // 이 메소드는 카메라가 열릴 때 호출됩니다. 카메라 미리보기를 시작합니다
//                    Log.e(TAG, "onOpened: 실행됨" );
                    cameraOpenCloseLock.release();
                    cameraDevice = cd;
                    createCameraPreviewSession();
                }

                @Override
                public void onDisconnected(final CameraDevice cd)
                {
//                    Log.e(TAG, "onDisconnected: 실행됨" );
                    cameraOpenCloseLock.release();
                    cd.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(final CameraDevice cd, final int error)
                {
//                    Log.e(TAG, "onError: 실행됨" );
                    cameraOpenCloseLock.release();
                    cd.close();
                    cameraDevice = null;
                    final Activity activity = getActivity();
                    if (null != activity)
                    {
                        activity.finish();
                    }
                }
            };
    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a {@link
     * TextureView}.
     */
    private final TextureView.SurfaceTextureListener surfaceTextureListener =
            new TextureView.SurfaceTextureListener()
            {
                @Override
                public void onSurfaceTextureAvailable(
                        final SurfaceTexture texture, final int width, final int height)
                {
//                    Log.e(TAG, "onSurfaceTextureAvailable: 실행됨" );
                    openCamera(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(
                        final SurfaceTexture texture, final int width, final int height)
                {
//                    Log.e(TAG, "onSurfaceTextureSizeChanged: 실행됨" );
                    configureTransform(width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(final SurfaceTexture texture)
                {
//                    Log.e(TAG, "onSurfaceTextureDestroyed: 실행됨" );
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(final SurfaceTexture texture)
                {
//                    Log.e(TAG, "onSurfaceTextureUpdated: 실행됨" );
                }
            };

    private CameraConnectionFragment(
            final ConnectionCallback connectionCallback,
            final OnImageAvailableListener imageListener,
            final int layout,
            final Size inputSize)
    {
//        Log.e(TAG, "CameraConnectionFragment: 실행됨" );
        this.cameraConnectionCallback = connectionCallback;
        this.imageListener = imageListener;
        this.layout = layout;
        this.inputSize = inputSize;
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the minimum of both, or an exact match if possible.
     *
     * @param choices The list of sizes that the camera supports for the intended output class
     * @param width   The minimum desired width
     * @param height  The minimum desired height
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    protected static Size chooseOptimalSize(final Size[] choices, final int width, final int height)
    {
        String TAG = "chooseOptimalSize";
//        Log.e(TAG, "chooseOptimalSize: 실행됨" );
        final int minSize = Math.max(Math.min(width, height), MINIMUM_PREVIEW_SIZE);
        final Size desiredSize = new Size(width, height);

        // Collect the supported resolutions that are at least as big as the preview Surface
        // 최소한 미리보기 표면보다 큰 지원 해상도를 수집하십시오.
        boolean exactSizeFound = false;
        final List<Size> bigEnough = new ArrayList<Size>();
        final List<Size> tooSmall = new ArrayList<Size>();
        for (final Size option : choices)
        {
            if (option.equals(desiredSize))
            {
                // Set the size but don't return yet so that remaining sizes will still be logged.
                exactSizeFound = true;
            }

            if (option.getHeight() >= minSize && option.getWidth() >= minSize)
            {
                bigEnough.add(option);
            } else
            {
                tooSmall.add(option);
            }
        }

        LOGGER.i("Desired size: " + desiredSize + ", min size: " + minSize + "x" + minSize);
        LOGGER.i("Valid preview sizes: [" + TextUtils.join(", ", bigEnough) + "]");
        LOGGER.i("Rejected preview sizes: [" + TextUtils.join(", ", tooSmall) + "]");

        LOGGER.e("Desired size: " + desiredSize + ", min size: " + minSize + "x" + minSize);
        LOGGER.e("Valid preview sizes: [" + TextUtils.join(", ", bigEnough) + "]");
        LOGGER.e("Rejected preview sizes: [" + TextUtils.join(", ", tooSmall) + "]");

        if (exactSizeFound)
        {
            LOGGER.i("Exact size match found.");
            LOGGER.e("Exact size match found.");
            return desiredSize;
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0)
        {
            final Size chosenSize = Collections.min(bigEnough, new CompareSizesByArea());
            LOGGER.i("Chosen size: " + chosenSize.getWidth() + "x" + chosenSize.getHeight());
            LOGGER.e("Chosen size: " + chosenSize.getWidth() + "x" + chosenSize.getHeight());
            return chosenSize;
        } else
        {
            LOGGER.e("Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @SuppressLint("LongLogTag")
    public static CameraConnectionFragment newInstance(
            final ConnectionCallback callback,
            final OnImageAvailableListener imageListener,
            final int layout,
            final Size inputSize)
    {
        String TAG = "CameraConnectionFragment";
//        Log.e(TAG, "newInstance: 실행됨" );
        return new CameraConnectionFragment(callback, imageListener, layout, inputSize);
    }

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text)
    {
//        Log.e(TAG, "showToast: 실행됨" );
        final Activity activity = getActivity();
        if (activity != null)
        {
            activity.runOnUiThread(
                    new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
//        Log.e(TAG, "onCreateView: 실행됨" );
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
//        Log.e(TAG, "onViewCreated: 실행됨" );
        textureView = (AutoFitTextureView) view.findViewById(R.id.texture);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState)
    {
//        Log.e(TAG, "onActivityCreated: 실행됨" );
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        startBackgroundThread();

//        Log.e(TAG, "onResume: 실행됨" );

        // When the screen is turned off and turned back on, the SurfaceTexture is already available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open a camera and start preview from here (otherwise, we wait until the surface is ready in the SurfaceTextureListener).
        // 화면을 껐다가 다시 켜면 SurfaceTexture를 이미 사용할 수 있으며
        // "onSurfaceTextureAvailable"이 호출되지 않습니다. 이 경우 카메라를 열고
        // 여기에서 미리보기를 시작할 수 있습니다
        // (그렇지 않으면 SurfaceTextureListener에서 표면이 준비 될 때까지 기다립니다)
        if (textureView.isAvailable())
        {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else
        {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onPause()
    {
//        Log.e(TAG, "onPause: 실행됨" );
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    public void setCamera(String cameraId)
    {
//        Log.e(TAG, "setCamera: 실행됨" );
        this.cameraId = cameraId;
    }

    /**
     * Sets up member variables related to camera.
     * 카메라 관련 멤버 변수를 설정합니다
     */
    private void setUpCameraOutputs()
    {
//        Log.e(TAG, "setUpCameraOutputs: 실행됨" );
        final Activity activity = getActivity();
        final CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try
        {
            final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

            final StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

            // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
            // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
            // garbage capture data.
            previewSize =
                    chooseOptimalSize(
                            map.getOutputSizes(SurfaceTexture.class),
                            inputSize.getWidth(),
                            inputSize.getHeight());

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            final int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            } else
            {
                textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }
        } catch (final CameraAccessException e)
        {
            Log.e(TAG, "setUpCameraOutputs: CameraAccessException: Exception!" );
            LOGGER.e(e, "Exception!");
        } catch (final NullPointerException e)
        {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            // TODO(andrewharp): abstract ErrorDialog/RuntimeException handling out into new method and
            // reuse throughout app.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            throw new RuntimeException(getString(R.string.camera_error));
        }

        cameraConnectionCallback.onPreviewSizeChosen(previewSize, sensorOrientation);
    }

    /**
     * Opens the camera specified by {@link CameraConnectionFragment#cameraId}.
     * {@link CameraConnectionFragment # cameraId}로 지정된 카메라를 엽니 다.
     */
    private void openCamera(final int width, final int height)
    {
        Context_CameraConnectionFragment = getActivity();
//        Log.e(TAG, "openCamera: 실행됨" );
        setUpCameraOutputs();
        configureTransform(width, height);
        final Activity activity = getActivity();
        final CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try
        {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS))
            {
                Log.e(TAG, "openCamera: Time out waiting to lock camera opening");
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler);
        } catch (final CameraAccessException e)
        {
            LOGGER.e(e, "Exception!");
        } catch (final InterruptedException e)
        {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    @SuppressLint("LongLogTag")
    public static void takePicture()
    {
        if (null == cameraDevice)
        {
            Log.e("CameraConnectionFragment", "mCameraDevice is null, return");
            return;
        }

        try
        {
            Size[] jpegSizes = null;
            CameraManager cameraManager = (CameraManager) Context_CameraConnectionFragment.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            if (map != null)
            {
                jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
//                Log.d("TEST", "map != null " + jpegSizes.length);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length)
            {
//                for (int i = 0 ; i < jpegSizes.length; i++) {
//                    Log.d("TEST", "getHeight = " + jpegSizes[i].getHeight() + ", getWidth = " + jpegSizes[i].getWidth());
//                }
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
//            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);

            // Orientation
            int rotation = ((Activity) Context_CameraConnectionFragment).getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

            final File file = new File(Environment.getExternalStorageDirectory() + "/DCIM", "pic_" + dateFormat.format(date) + ".jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener()
            {
                @Override
                public void onImageAvailable(ImageReader reader)
                {
                    Image image = null;
                    try
                    {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (image != null)
                        {
                            image.close();
                            reader.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException
                {
                    OutputStream output = null;
                    try
                    {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally
                    {
                        if (null != output)
                        {
                            output.close();
                        }
                    }
                }
            };

            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroudHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroudHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback()
            {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request, TotalCaptureResult result)
                {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(Context_CameraConnectionFragment, "Saved:" + file, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(file));
                    Context_CameraConnectionFragment.sendBroadcast(intent);

                    String str = String.valueOf(file);

                    Intent intent1 = new Intent(Context_CameraConnectionFragment, Activity_Done_Certify.class);

                    intent1.putExtra("strParamName", str);
                    intent1.putExtra("certiFrom", "camera");

                    Context_CameraConnectionFragment.startActivity(intent1);

                    Log.e("CameraConnectionFragment", "onCaptureCompleted: file: " + file );
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback()
            {
                @Override
                public void onConfigured(CameraCaptureSession session)
                {
                    try
                    {
                        session.capture(captureBuilder.build(), captureListener, backgroudHandler);
                    } catch (CameraAccessException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session)
                {

                }
            }, backgroudHandler);

        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera()
    {
//        Log.e(TAG, "closeCamera: 실행됨" );
        try
        {
            cameraOpenCloseLock.acquire();
            if (null != captureSession)
            {
                captureSession.close();
                captureSession = null;
            }
            if (null != cameraDevice)
            {
                cameraDevice.close();
                cameraDevice = null;
            }
            if (null != previewReader)
            {
                previewReader.close();
                previewReader = null;
            }
        } catch (final InterruptedException e)
        {
            Log.e(TAG, "closeCamera: \"Interrupted while trying to lock camera closing.\", e: " + e.toString() );
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally
        {
            cameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread()
    {
//        Log.e(TAG, "startBackgroundThread: 실행됨" );
        backgroundThread = new HandlerThread("ImageListener");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
//        Log.e(TAG, "stopBackgroundThread: 실행됨" );
        backgroundThread.quitSafely();
        try
        {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (final InterruptedException e)
        {
            LOGGER.e(e, "Exception!");
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession()
    {
//        Log.e(TAG, "createCameraPreviewSession: 실행됨" );
        try
        {
            final SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            // 기본 버퍼 크기를 원하는 카메라 미리보기 크기로 구성합니다.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            // This is the output Surface we need to start preview.
            // 이것이 미리보기를 시작하는 데 필요한 출력 표면입니다
            final Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            // 출력 Surface로 CaptureRequest.Builder를 설정했습니다.
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);

            LOGGER.i("Opening camera preview: " + previewSize.getWidth() + "x" + previewSize.getHeight());

            // Create the reader for the preview frames.
            // 미리보기 프레임 용 리더 만들기
            previewReader =
                    ImageReader.newInstance(
                            previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);

            previewReader.setOnImageAvailableListener(imageListener, backgroundHandler);
            previewRequestBuilder.addTarget(previewReader.getSurface());

            // Here, we create a CameraCaptureSession for camera preview.
            // 카메라 미리보기를위한 CameraCaptureSession을 만듭니다.
            cameraDevice.createCaptureSession(Arrays.asList(surface, previewReader.getSurface()),
                    new CameraCaptureSession.StateCallback()
                    {
                        @Override
                        public void onConfigured(final CameraCaptureSession cameraCaptureSession)
                        {
                            // The camera is already closed
                            // 카메라가 이미 닫혀 있습니다
                            if (null == cameraDevice)
                            {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            // 세션이 준비되면 미리보기가 표시됩니다.
                            captureSession = cameraCaptureSession;
                            try
                            {
                                // Auto focus should be continuous for camera preview.
                                // 카메라 미리보기를 위해 자동 초점이 연속적이어야합니다.

                                previewRequestBuilder.set(
                                        CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                // 필요할 때 플래시가 자동으로 활성화됩니다
                                previewRequestBuilder.set(
                                        CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                // Finally, we start displaying the camera preview.
                                // 마지막으로 카메라 미리보기를 표시하기 시작합니다
                                previewRequest = previewRequestBuilder.build();
                                captureSession.setRepeatingRequest(
                                        previewRequest, captureCallback, backgroundHandler);

                                Log.e(TAG, "onConfigured: 카메라 실행됨" );

                            } catch (final CameraAccessException e)
                            {
                                LOGGER.e(e, "Exception!");
                            }
                        }

                        @Override
                        public void onConfigureFailed(final CameraCaptureSession cameraCaptureSession)
                        {
                            showToast("Failed");
                        }
                    },
                    null);
        } catch (final CameraAccessException e)
        {
            LOGGER.e(e, "Exception!");
        }
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`. This method should be
     * called after the camera preview size is determined in setUpCameraOutputs and also the size of
     * `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(final int viewWidth, final int viewHeight)
    {
//        Log.e(TAG, "configureTransform: 실행됨" );
        final Activity activity = getActivity();
        if (null == textureView || null == previewSize || null == activity)
        {
            return;
        }

        final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        final Matrix matrix = new Matrix();
        final RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        final RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        final float centerX = viewRect.centerX();
        final float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            final float scale =
                    Math.max(
                            (float) viewHeight / previewSize.getHeight(),
                            (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation)
        {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    /**
     * Callback for Activities to use to initialize their data once the selected preview size is
     * known.
     */
    public interface ConnectionCallback
    {
        void onPreviewSizeChosen(Size size, int cameraRotation);
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(final Size lhs, final Size rhs)
        {
            // We cast here to ensure the multiplications won't overflow
            // 우리는 곱셈이 넘치지 않도록 여기에 캐스트했습니다.

            String TAG = "CompareSizesByArea";
//            Log.e(TAG, "compare: 실행됨" );
            return Long.signum(
                    (long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    /**
     * Shows an error message dialog.
     * 오류 메시지 대화 상자를 표시합니다.
     */
    public static class ErrorDialog extends DialogFragment
    {
        private static final String ARG_MESSAGE = "message";


        public static ErrorDialog newInstance(final String message)
        {
            String TAG = "ErrorDialog";
//            Log.e(TAG, "newInstance: 실행됨" );

            final ErrorDialog dialog = new ErrorDialog();
            final Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState)
        {
            String TAG = "ErrorDialog";
//            Log.e(TAG, "onCreateDialog: 실행됨" );
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(
                            android.R.string.ok,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, final int i)
                                {
                                    activity.finish();
                                }
                            })
                    .create();
        }
    }
}
