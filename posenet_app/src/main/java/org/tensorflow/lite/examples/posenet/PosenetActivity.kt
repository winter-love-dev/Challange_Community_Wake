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

package org.tensorflow.lite.examples.posenet

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.net.Uri
import android.os.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_posenet.*
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_JOIN_INDEX
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_JOIN_MEMBER_INDEX
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_PROOF_CATEGORY
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_PROOF_CERTI_COUNT
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_PROOF_INDEX
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_PROOF_JOIN_PRICE
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_PROOF_MY_CERTI_COUNT
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_PROOF_TITLE
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_PROOF_TYPE
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.GET_REWARD
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.getID
import org.tensorflow.lite.examples.posenet.PosenetCameraActivity.Companion.receiveHandler
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.pow
import org.tensorflow.lite.examples.posenet.lib.BodyPart
import org.tensorflow.lite.examples.posenet.lib.Person
import org.tensorflow.lite.examples.posenet.lib.Posenet
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
//import kotlinx.android.synthetic.main.activity_posenet.motion_detect_foot_icon_left as motion_detect_foot_icon_left1

class PosenetActivity :
        Fragment(),
        ActivityCompat.OnRequestPermissionsResultCallback
{

    // Log.e(TAG, "")
    val TAG = "PosenetActivity"

    /**
    List of body joints that should be connected.
    연결해야하는 신체 관절 목록.
     * */
    private val bodyJoints = listOf(
            Pair(BodyPart.LEFT_WRIST, BodyPart.LEFT_ELBOW),     // 손목과 팔꿈치 연결 (좌)
            Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_SHOULDER),  // 팔꿈치와 어깨 연결 (좌)
            Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER), // (좌) 어깨와 (우) 어깨 연결
            Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW), // 어깨와 팔꿈치 연결 (우)
            Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),    // 팔꿈치와 손목 연결 (우)
            Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),     // 어깨와 엉덩이 연결 (좌)
            Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),         // (좌) 엉덩이와 (우) 엉덩이 연결
            Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_SHOULDER),   // (우) 엉덩이와 (우) 어깨 연결
            Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),         // 엉덩이와 무릎 연결 (좌)
            Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),       // 무릎과 발목 연결 (좌)
            Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),       // 엉덩이와 무릎 연결 (우)
            Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)      // 엉덩이와 발목 연결 (우)
                                   )

//    private var LeftHipX = 0
//    private var RightHipY = 0
//    private var LeftKneeX = 0
//    private var RightKneeY = 0
//    private var LeftAnkleX = 0
//    private var RightAnkleY = 0
//
//    var needScore: Float = 0.81F
//    var mScore: String? = null

    /*
        LeftHipX
        RightHipY
        LeftKneeX
        RightKneeY
        LeftAnkleX
        RightAnkleY
    **/

    /** Threshold for confidence score.
     *  신뢰 점수에 대한 임계 값
     * */
    private val minConfidence = 0.5
//    private val minConfidence = 0.1
    // 최소한의 자신감, 신뢰점수

    /** Radius of circle used to draw keypoints.
     * 키포인트를 그리는 데 사용되는 원의 반경
     * (관절 사이에 표시되는 원의 크기)
     * */
//    private val circleRadius = 8.0f
    private val circleRadius = 4.0f

    /** Paint class holds the style and color information to draw geometries,text and bitmaps.
     * Paint 클래스는 형상, 텍스트 및 비트 맵을 그리기위한 스타일 및 색상 정보를 보유합니다.
     * */
    private var paint = Paint()

    /** A shape for extracting frame data.
     * 프레임 데이터를 추출하기위한 모양입니다.
     * */
//    private val PREVIEW_WIDTH = 640
//    private val PREVIEW_HEIGHT = 480

    private val PREVIEW_WIDTH = 320
    private val PREVIEW_HEIGHT = 240

//    private val PREVIEW_WIDTH = 160
//    private val PREVIEW_HEIGHT = 120

    /** An object for the Posenet library.
     * Posenet 라이브러리의 객체입니다.
     * */
    private lateinit var posenet: Posenet

    /** ID of the current [CameraDevice].
     * 현재 [CameraDevice]의 ID입니다.
     * */
    private var cameraId: String? = null

    /** A [SurfaceView] for camera preview.
     * 카메라 미리보기를위한 [SurfaceView].
     * */
    private var surfaceView: SurfaceView? = null
    private var posenet_camera_frame: FrameLayout? = null


    /** A [CameraCaptureSession] for camera preview.
     * 카메라 미리보기를위한 [CameraCaptureSession].
     * */
    private var captureSession: CameraCaptureSession? = null

    /** A reference to the opened [CameraDevice].
     * 열린 [CameraDevice]에 대한 참조입니다.
     * */
    private var cameraDevice: CameraDevice? = null

    /** The [android.util.Size] of camera preview.
     * 카메라 미리보기의 [android.util.Size]
     * */
    private var previewSize: Size? = null

    /** The [android.util.Size.getWidth] of camera preview.
     * 카메라 미리보기의 [android.util.Size.getWidth]
     * */
    private var previewWidth = 0

    /** The [android.util.Size.getHeight] of camera preview.
     * 카메라 미리보기의 [android.util.Size.getHeight]
     * */
    private var previewHeight = 0

    /** A counter to keep count of total frames.
     * 총 프레임 수를 유지하는 카운터입니다.
     * */
    private var frameCounter = 0

    /** An IntArray to save image data in ARGB8888 format
     *  이미지 데이터를 ARGB8888 형식으로 저장하기위한 IntArray
     *  */
    private lateinit var rgbBytes: IntArray

    /** A ByteArray to save image data in YUV format
     * YUV 형식으로 이미지 데이터를 저장하는 ByteArray
     * */
    private var yuvBytes = arrayOfNulls<ByteArray>(3)

    /** An additional thread for running tasks that shouldn't block the UI.
     * UI를 차단해서는 안되는 작업을 실행하기위한 추가 스레드입니다.
     * */
    private var backgroundThread: HandlerThread? = null

    /** A [Handler] for running tasks in the background.
     * 백그라운드에서 작업을 실행하기위한 [Handler]입니다.
     * */
    private var backgroundHandler: Handler? = null

    /** An [ImageReader] that handles preview frame capture.
     * 미리보기 프레임 캡처를 처리하는 [ImageReader]입니다.
     * */
    private var imageReader: ImageReader? = null

    /** [CaptureRequest.Builder] for the camera preview
     * 카메라 미리보기 용 [CaptureRequest.Builder]
     * */
    private var previewRequestBuilder: CaptureRequest.Builder? = null

    /** [CaptureRequest] generated by [previewRequestBuilder]
     * [previewRequestBuilder]에 의해 생성 된 [CaptureRequest]
     * */
    private var previewRequest: CaptureRequest? = null

    /** A [Semaphore] to prevent the app from exiting before closing the camera.
     * 카메라를 닫기 전에 앱이 종료되지 않도록하는 [Semaphore]. 세마포어
     * */
    private val cameraOpenCloseLock = Semaphore(1)

    /** Whether the current camera device supports Flash or not.
     * 현재 카메라 장치가 플래시를 지원하는지 여부
     * */
    private var flashSupported = false

    /** Orientation of the camera sensor.
     * 카메라 센서의 방향
     *  */
    private var sensorOrientation: Int? = null

    /** Abstract interface to someone holding a display surface.
     * 디스플레이 표면을 잡고 누군가에게 추상 인터페이스
     * */
    private var surfaceHolder: SurfaceHolder? = null

    /** [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     * [CameraDevice]가 상태를 변경하면 [CameraDevice.StateCallback]이 호출됩니다.
     * */
    private val stateCallback = object : CameraDevice.StateCallback()
    {
        override fun onOpened(cameraDevice: CameraDevice)
        {
            Log.e(TAG, "onOpened() 실행됨")
            cameraOpenCloseLock.release()
            this@PosenetActivity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice)
        {
            Log.e(TAG, "onDisconnected() 실행됨")
            cameraOpenCloseLock.release()
            cameraDevice.close()
            this@PosenetActivity.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int)
        {
            Log.e(TAG, "onError() 실행됨")
            onDisconnected(cameraDevice)
            this@PosenetActivity.activity?.finish()
        }
    }

    /**
     * A [CameraCaptureSession.CaptureCallback] that handles events related to JPEG capture.
     * JPEG 캡처와 관련된 이벤트를 처리하는 [CameraCaptureSession.CaptureCallback]입니다.
     */
    private val captureCallback = object : CameraCaptureSession.CaptureCallback()
    {
        override fun onCaptureProgressed(
                session: CameraCaptureSession,
                request: CaptureRequest,
                partialResult: CaptureResult
                                        )
        {
            Log.e(TAG, "onCaptureProgressed() 실행됨")
        }

        override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
                                       )
        {
//            Log.e(TAG, "onCaptureCompleted() 실행됨")
        }
    }

    /**
     * Shows a [Toast] on the UI thread.
     * UI 스레드에 [Toast]를 표시합니다.
     *
     * @param text The message to show _ 텍스트 표시 할 메시지
     *
     */
    private fun showToast(text: String)
    {
        val activity = activity
        activity?.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
                             ): View? = inflater.inflate(R.layout.activity_posenet, container, false)

    var mContext: Context? = null

    private var motion_detect_foot_icon_left: ImageView? = null
    private var motion_detect_foot_icon_right: ImageView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        Log.e(TAG, "onViewCreated() 실행됨")

        // View Find
        surfaceView = view.findViewById(R.id.posenet_surfaceView)
        surfaceHolder = surfaceView!!.holder
        posenet_camera_frame = view.findViewById(R.id.posenet_camera_frame)
//        motion_detect_foot_icon_left = view.findViewById(R.id.motion_detect_foot_icon_left)
//        motion_detect_foot_icon_right = view.findViewById(R.id.motion_detect_foot_icon_right)

        mContext = context?.applicationContext

        // PosenetActivity에서 카운트 완료 신호 받기
        receiveHandler = @SuppressLint("HandlerLeak")
        object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                super.handleMessage(msg)

                if (msg.what === PosenetCameraActivity.START_CODE)
                {
                    Log.e(TAG, "메시지 받음 START_CODE")
                }

                else if (msg.what === PosenetCameraActivity.PROGRESS_CODE)
                {
                    Log.e(TAG, "메시지 받음 PROGRESS_CODE: " + msg.what)
                    Log.e(TAG, "메시지 받음 PROGRESS_CODE: " + msg.arg1)

//                    motion_detect_foot_icon_left.visibility = View.GONE
//                    motion_detect_foot_icon_right.visibility = View.GONE

                    // 카운트 완료 신호 받으면 촬영하기
                    takePicture()
                }
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        startBackgroundThread()
    }

    override fun onStart()
    {
        super.onStart()
        openCamera()
        posenet = Posenet(this.context!!)
    }

    override fun onPause()
    {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun requestCameraPermission()
    {
        Log.e(TAG, "requestCameraPermission() 실행됨")
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
        {
            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
        }
        else
        {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
                                           )
    {
        Log.e(TAG, "onRequestPermissionsResult() 실행됨")
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Sets up member variables related to camera.
     * 카메라 관련 멤버 변수를 설정합니다.
     */
    private fun setUpCameraOutputs()
    {
        Log.e(TAG, "setUpCameraOutputs() 실행됨")
        val activity = activity
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try
        {
            for (cameraId in manager.cameraIdList)
            {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                // 이 샘플에서는 전면 카메라를 사용하지 않습니다.
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraDirection != null &&
                    cameraDirection == CameraCharacteristics.LENS_FACING_FRONT
                )
                {
                    continue
                }

                previewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)

                imageReader = ImageReader.newInstance(
                        PREVIEW_WIDTH, PREVIEW_HEIGHT,
                        ImageFormat.YUV_420_888, /*maxImages*/ 2
                                                     )

                sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

                previewHeight = previewSize!!.height
                previewWidth = previewSize!!.width

                // Initialize the storage bitmaps once when the resolution is known.
                // 해상도가 알려진 경우 스토리지 비트 맵을 한 번 초기화하십시오.
                rgbBytes = IntArray(previewWidth * previewHeight)

                // Check if the flash is supported.
                // 플래시가 지원되는지 확인하십시오.
                flashSupported =
                        characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true

                this.cameraId = cameraId

                // We've found a viable camera and finished setting up member variables,
                // so we don't need to iterate through other available cameras.

                // 실행 가능한 카메라를 찾았고 멤버 변수 설정을 마쳤으므로
                // 사용 가능한 다른 카메라를 반복 할 필요가 없습니다.

                return
            }
        }
        catch (e: CameraAccessException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: NullPointerException)
        {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the device this code runs.
            // 현재 Camera2API를 사용하지만이 코드가 실행되는 장치에서 지원되지 않으면 NPE가 발생합니다. (NPE 널 포인트 익셉션)
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(childFragmentManager, FRAGMENT_DIALOG)
        }
    }

    /**
     * Opens the camera specified by [PosenetActivity.cameraId].
     * [PosenetActivity.cameraId]로 지정된 카메라를 엽니 다.
     */
    private fun openCamera()
    {
        Log.e(TAG, "openCamera() 실행됨")
        val permissionCamera = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        if (permissionCamera != PackageManager.PERMISSION_GRANTED)
        {
            requestCameraPermission()
        }
        setUpCameraOutputs()
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try
        {
            // Wait for camera to open - 2.5 seconds is sufficient
            // 카메라가 열리기를 기다립니다-2.5 초면 충분합니다 (try Acquire = 획득 시도)
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS))
            {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraId!!, stateCallback, backgroundHandler)
        }
        catch (e: CameraAccessException)
        {
            Log.e(TAG, e.toString())
        }
        catch (e: InterruptedException)
        {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    /**
     * Closes the current [CameraDevice].
     * 현재 [CameraDevice]를 닫습니다.
     */
    private fun closeCamera()
    {
        Log.e(TAG, "closeCamera() 실행됨")
        if (captureSession == null)
        {
            return
        }

        try
        {
            cameraOpenCloseLock.acquire()
            captureSession!!.close()
            captureSession = null
            cameraDevice!!.close()
            cameraDevice = null
            imageReader!!.close()
            imageReader = null
        }
        catch (e: InterruptedException)
        {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        }
        finally
        {
            cameraOpenCloseLock.release()
        }
    }

    /**
     * Starts a background thread and its [Handler].
     * 백그라운드 스레드와 해당 [Handler]를 시작합니다.
     */
    private fun startBackgroundThread()
    {
        Log.e(TAG, "startBackgroundThread() 실행됨")
        backgroundThread = HandlerThread("imageAvailableListener").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    /**
     * Stops the background thread and its [Handler].
     * 백그라운드 스레드와 해당 [Handler]를 중지합니다.
     */
    private fun stopBackgroundThread()
    {
        Log.e(TAG, "stopBackgroundThread() 실행됨")
        backgroundThread?.quitSafely()
        try
        {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        }
        catch (e: InterruptedException)
        {
            Log.e(TAG, e.toString())
        }
    }

    /**
    Fill the yuvBytes with data from image planes.
    이미지 평면의 데이터로 yuvBytes를 채 웁니다.
     */
    private fun fillBytes(planes: Array<Image.Plane>, yuvBytes: Array<ByteArray?>)
    {
        // Log.e(TAG, "fillBytes() 실행됨")
        /**

        Row stride is the total number of bytes occupied in memory by a row of an image.
        Because of the variable row stride it's not possible to know in advance the actual necessary dimensions of the yuv planes.

        행 보폭은 이미지 행이 메모리에서 차지하는 총 바이트 수입니다.
        가변 행 보폭으로 인해 yuv 평면의 실제 필요한 치수를 미리 알 수 없습니다.

         */

        for (i in planes.indices)
        {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null)
            {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer.get(yuvBytes[i]!!)
        }
    }

    /**
     * This is the output file for our picture.
     * 이것은 우리 사진의 출력 파일입니다.
     */
    private lateinit var file: File

    /**
     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a still image is ready to be saved.
     *  [ImageReader]에 대한 콜백 객체입니다. 스틸 이미지를 저장할 준비가되면 "onImageAvailable"이 호출됩니다.
     */
    private val onImageAvailableListener2 = ImageReader.OnImageAvailableListener {
        backgroundHandler?.post(ImageSaver(it.acquireNextImage(), file))
    }

    /** A [OnImageAvailableListener] to receive frames as they are available.
     * 사용 가능한 프레임을 수신하는 [OnImageAvailableListener]
     * */
    private var imageAvailableListener = object : OnImageAvailableListener
    {
        override fun onImageAvailable(imageReader: ImageReader)
        {
//            Log.e(TAG, "onImageAvailable() 실행됨")
            // We need wait until we have some size from onPreviewSizeChosen
            // onPreviewSizeChosen에서 크기가 나올 때까지 기다려야합니다.
            if (previewWidth == 0 || previewHeight == 0)
            {
                return
            }

            val image = imageReader.acquireLatestImage() ?: return
            fillBytes(image.planes, yuvBytes)

            ImageUtils.convertYUV420ToARGB8888(
                    yuvBytes[0]!!,
                    yuvBytes[1]!!,
                    yuvBytes[2]!!,
                    previewWidth,
                    previewHeight,
                    /*yRowStride=*/ image.planes[0].rowStride, /*y 행 보폭*/
                    /*uvRowStride=*/ image.planes[1].rowStride, /*u v 행 보폭*/
                    /*uvPixelStride=*/ image.planes[1].pixelStride, /*자외선 픽셀 보폭*/
                    rgbBytes
                                              )

            // Create bitmap from int array
            // int 배열에서 비트 맵 만들기
            val imageBitmap = Bitmap.createBitmap(
                    rgbBytes, previewWidth, previewHeight,
                    Bitmap.Config.ARGB_8888
                                                 )

            // Create rotated version for portrait display
            // 세로로 표시 할 회전 버전 만들기
            val rotateMatrix = Matrix()
            rotateMatrix.postRotate(90.0f)

            val rotatedBitmap = Bitmap.createBitmap(
                    imageBitmap, 0, 0, previewWidth, previewHeight,
                    rotateMatrix, true
                                                   )
            image.close()

            // Process an image for analysis in every 3 frames.
            // 3 프레임마다 분석 할 이미지를 처리하십시오.
//            frameCounter = (frameCounter + 1) % 3
            frameCounter = (frameCounter + 1) % 1
            if (frameCounter == 0)
            {
                processImage(rotatedBitmap)
            }
        }
    }

    /** Crop Bitmap to maintain aspect ratio of model input.
     * 비트 맵을 자르면 모델 입력의 종횡비가 유지됩니다.
     * */
    private fun cropBitmap(bitmap: Bitmap): Bitmap
    {
//        Log.e(TAG, "cropBitmap() 실행됨")

        // Rotated bitmap has previewWidth as its height and previewHeight as width.
        // 회전 된 비트 맵의 높이는 previewWidth, 너비는 previewHeight입니다.
        val previewRatio = previewWidth.toFloat() / previewHeight
        val modelInputRatio = MODEL_HEIGHT.toFloat() / MODEL_WIDTH
        var croppedBitmap = bitmap

        // Acceptable difference between the modelInputRatio and previewRatio to skip cropping.
        // 자르기를 건너뛰기 위해 modelInputRatio와 previewRatio간에 허용되는 차이입니다.
        val maxDifference = 1.0f.pow(-5)

        // Checks if the previewing bitmap has similar aspect ratio as the required model input.
        // 미리보기 비트 맵이 필요한 모델 입력과 유사한 종횡비를 갖는지 확인합니다.
        when
        {
            abs(modelInputRatio - previewRatio) < maxDifference -> return croppedBitmap
            modelInputRatio > previewRatio                      ->
            {
                // New image is taller so we are height constrained.
                // 새로운 이미지는 키가 커서 키가 제한됩니다.
                val cropHeight = previewHeight - (previewWidth.toFloat() / modelInputRatio)
                croppedBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        (cropHeight / 2).toInt(),
                        previewHeight,
                        (previewWidth - (cropHeight / 2)).toInt()
                                                   )
            }
            else                                                ->
            {
                val cropWidth = previewWidth - (previewHeight.toFloat() * modelInputRatio)
                croppedBitmap = Bitmap.createBitmap(
                        bitmap,
                        (cropWidth / 2).toInt(),
                        0,
                        (previewHeight - (cropWidth / 2)).toInt(),
                        previewWidth
                                                   )
            }
        }
        return croppedBitmap
    }

    /** Set the paint color and size.
     * 페인트 색상 및 크기 설정
     * */
    private fun setPaint()
    {
//        Log.e(TAG, "setPaint() 실행됨")

        // 선 굵기와 글자 색상
        // 글씨 크기

        paint.color = Color.GREEN
//        paint.textSize = 80.0f
//        paint.strokeWidth = 8.0f
        paint.textSize = 40.0f
        paint.strokeWidth = 4.0f
    }

    /** Draw bitmap on Canvas.
     * 캔버스에 비트 맵 그리기
     * */
    private fun draw(canvas: Canvas, person: Person, bitmap: Bitmap)
    {
//        Log.e(TAG, "draw() 실행됨")
        val screenWidth: Int = canvas.width
        val screenHeight: Int = canvas.height
        setPaint()
        canvas.drawBitmap(
                bitmap,
                Rect(0, 0, previewHeight, previewWidth),
                Rect(0, 0, screenWidth, screenHeight),
                paint
                         )

        val widthRatio = screenWidth.toFloat() / MODEL_WIDTH
        val heightRatio = screenHeight.toFloat() / MODEL_HEIGHT

        // Draw key points over the image.
        // 이미지 위에 키 포인트를 그립니다.
//        for (keyPoint in person.keyPoints)
//        {
//            if (keyPoint.score > minConfidence)
//            {
//                val position = keyPoint.position
//                val adjustedX: Float = position.x.toFloat() * widthRatio
//                val adjustedY: Float = position.y.toFloat() * heightRatio
//                canvas.drawCircle(adjustedX, adjustedY, circleRadius, paint)
//            }
//        }

        // 선 그리기
//        for (line in bodyJoints)
//        {
//            if (
//                (person.keyPoints[line.first.ordinal].score > minConfidence) and
//                (person.keyPoints[line.second.ordinal].score > minConfidence)
//            )
//            {
//                canvas.drawLine(
//                        person.keyPoints[line.first.ordinal].position.x.toFloat() * widthRatio,
//                        person.keyPoints[line.first.ordinal].position.y.toFloat() * heightRatio,
//                        person.keyPoints[line.second.ordinal].position.x.toFloat() * widthRatio,
//                        person.keyPoints[line.second.ordinal].position.y.toFloat() * heightRatio,
//                        paint
//                               )
//            }
//        }

        // Draw confidence score of a person.
        // 화면 좌측 하단에 점수 그리기
        val score = "%.2f".format(person.score)
        val scoreMessage = "자세 평가: " + score

//        if(0.81 <= score.toFloat())
//        if(6 == LeftAnkleX || 7 == LeftAnkleX && 6 == RightAnkleY || 7 == RightAnkleY)
//        {
//            canvas.drawText(
//                    "인증을 시작합니다",
//                    (15.0f * widthRatio),
//                    (255.0f * heightRatio),
//                    paint
//                           )
//        }
//
//        else
//        {
//            canvas.drawText(
//                    "두 발을 점선 위에 올려주세요",
//                    (15.0f * widthRatio),
//                    (255.0f * heightRatio),
//                    paint
//                           )
//        }

//        canvas.drawText(
//                scoreMessage,
//                (15.0f * widthRatio),
//                (243.0f * heightRatio),
//                paint
//                       )

//        Log.e(TAG, "draw: scoreMessage: " + scoreMessage)

        // Draw! 그림!
        surfaceHolder!!.unlockCanvasAndPost(canvas)

        // x y 좌표
//        Log.e(TAG, person.keyPoints.get(11).bodyPart.toString() + ": " + person.keyPoints.get(11).position.x + ", " + person.keyPoints.get(11).position.y)
//        Log.e(TAG, person.keyPoints.get(12).bodyPart.toString() + ": " + person.keyPoints.get(12).position.x + ", " + person.keyPoints.get(12).position.y)
//
//        Log.e(TAG, person.keyPoints.get(13).bodyPart.toString() + ": " + person.keyPoints.get(13).position.x + ", " + person.keyPoints.get(13).position.y)
//        Log.e(TAG, person.keyPoints.get(14).bodyPart.toString() + ": " + person.keyPoints.get(14).position.x + ", " + person.keyPoints.get(14).position.y)
//
//        Log.e(TAG, person.keyPoints.get(15).bodyPart.toString() + ": " + person.keyPoints.get(15).position.x + ", " + person.keyPoints.get(15).position.y)
//        Log.e(TAG, person.keyPoints.get(16).bodyPart.toString() + ": " + person.keyPoints.get(16).position.x + ", " + person.keyPoints.get(16).position.y)

        // hitmap 좌표
//        Log.e(TAG, person.keyPoints.get(11).bodyPart.toString() + ": " + person.keyPointPosition.get(11).row + ", " + person.keyPointPosition.get(11).col)
//        Log.e(TAG, person.keyPoints.get(12).bodyPart.toString() + ": " + person.keyPointPosition.get(12).row + ", " + person.keyPointPosition.get(12).col)
//
//        Log.e(TAG, person.keyPoints.get(13).bodyPart.toString() + ": " + person.keyPointPosition.get(13).row + ", " + person.keyPointPosition.get(13).col)
//        Log.e(TAG, person.keyPoints.get(14).bodyPart.toString() + ": " + person.keyPointPosition.get(14).row + ", " + person.keyPointPosition.get(14).col)
//
//        Log.e(TAG, person.keyPoints.get(15).bodyPart.toString() + ": " + person.keyPointPosition.get(15).row + ", " + person.keyPointPosition.get(15).col)
//        Log.e(TAG, person.keyPoints.get(16).bodyPart.toString() + ": " + person.keyPointPosition.get(16).row + ", " + person.keyPointPosition.get(16).col)

        /*
            LeftHipX
            RightHipY
            LeftKneeX
            RightKneeY
            LeftAnkleX
            RightAnkleY
        **/

        // PosenetCameraActivity로 값 전달 (5 ~ 10)

        // 5
        LeftShoulderX = person.keyPoints.get(5).position.x
        LeftShoulderY = person.keyPoints.get(5).position.y

        // 6
        RightShoulderX = person.keyPoints.get(6).position.x
        RightShoulderY = person.keyPoints.get(6).position.y

        // 7
        LeftElbowX = person.keyPoints.get(7).position.x
        LeftElbowY = person.keyPoints.get(7).position.y

        // 8
        RightElbowX = person.keyPoints.get(8).position.x
        RightElbowY = person.keyPoints.get(8).position.y

        // 9
        LeftWristX = person.keyPoints.get(9).position.x
        LeftWristY = person.keyPoints.get(9).position.y

        // 10
        RightWristX = person.keyPoints.get(10).position.x
        RightWristY = person.keyPoints.get(10).position.y

        // PosenetCameraActivity로 값 전달 (11 ~ 16)
        LeftHipX = person.keyPoints.get(11).position.x
        LeftHipY = person.keyPoints.get(11).position.y

        RightHipY = person.keyPoints.get(12).position.x
        RightHipX = person.keyPoints.get(12).position.y

        LeftKneeX = person.keyPoints.get(13).position.x
        LeftKneeY = person.keyPoints.get(13).position.y

        RightKneeY = person.keyPoints.get(14).position.x
        RightKneeX = person.keyPoints.get(14).position.y

        LeftAnkleX = person.keyPoints.get(15).position.x
        LeftAnkleY = person.keyPoints.get(15).position.y

        RightAnkleY = person.keyPoints.get(16).position.x
        RightAnkleX = person.keyPoints.get(16).position.y

        mScore = score

//        /*
//            od_pose_shot_notice
//            od_pose_shot_icon
//        */
//
//        // ??초에 한 번 씩 검사
//        Handler().postDelayed({
//            if (needScore.toInt() < score.toInt())
//            {
//                Log.e(TAG, "80점 이상임")
//
//                od_pose_shot_notice?.setText("현재 자세를 유지하세요 (3초)")
//                od_pose_shot_icon?.setImageDrawable(Drawable.createFromPath(R.drawable.eye_white_1.toString()))
//                od_pose_shot_icon?.borderWidth = 0
//            }
//
//            else
//            {
//                Log.e(TAG, "자세 탐색중");
//
//                od_pose_shot_notice?.setText("자세 탐색중")
//                od_pose_shot_icon?.setImageDrawable(Drawable.createFromPath(R.drawable.eye_white_2.toString()))
//                od_pose_shot_icon?.borderWidth = 1
//            }
//        }, 2500)


//        bodyJoints
//        Log.e(TAG, "person.keyPoints: " + person.score)

//        Log.e(TAG, "person.keyPoints: " + person.score)
//        Log.e(TAG, "person.keyPoints.get(11).score.toString(): " + person.keyPoints.get(11).score.toString())
    }

    /** Process image using Posenet library.
     * Posenet 라이브러리를 사용하여 이미지를 처리하십시오.
     * */
    private fun processImage(bitmap: Bitmap)
    {
//        Log.e(TAG, "processImage() 실행됨")
        // Crop bitmap.
        // 비트맵 자르기
        val croppedBitmap = cropBitmap(bitmap)

        // Created scaled version of bitmap for model input.
        // 모델 입력을위한 스케일 된 버전의 비트 맵을 만들었습니다.
        val scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, MODEL_WIDTH, MODEL_HEIGHT, true)

        // Perform inference
        // 추론 수행
        val person = posenet.estimateSinglePose(scaledBitmap)
        val canvas: Canvas = surfaceHolder!!.lockCanvas()
        draw(canvas, person, bitmap)

//        val position = posenet.estimateSinglePose(scaledBitmap).keyPoints[11].position
    }

    /**
     * Creates a new [CameraCaptureSession] for camera preview.
     * 카메라 미리보기를위한 새로운 [CameraCaptureSession]을 만듭니다.
     */
    private fun createCameraPreviewSession()
    {
        Log.e(TAG, "createCameraPreviewSession() 실행됨")
        try
        {

            // We capture images from preview in YUV format.
            // YUV 형식으로 미리보기에서 이미지를 캡처합니다.
            imageReader = ImageReader.newInstance(
                    previewSize!!.width, previewSize!!.height, ImageFormat.YUV_420_888, 2
                                                 )
            imageReader!!.setOnImageAvailableListener(imageAvailableListener, backgroundHandler)

            // This is the surface we need to record images for processing.
            // 이것은 처리를 위해 이미지를 기록해야하는 표면입니다.
            val recordingSurface = imageReader!!.surface

            // We set up a CaptureRequest.Builder with the output Surface.
            // 출력 Surface로 CaptureRequest.Builder를 설정했습니다.
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder!!.addTarget(recordingSurface)

            // Here, we create a CameraCaptureSession for camera preview.
            // 여기에서는 카메라 미리보기를위한 CameraCaptureSession을 만듭니다.
            cameraDevice!!.createCaptureSession(
                    listOf(recordingSurface),
                    object : CameraCaptureSession.StateCallback()
                    {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession)
                        {
                            // The camera is already closed
                            // 카메라가 이미 닫혀 있습니다
                            if (cameraDevice == null) return

                            // When the session is ready, we start displaying the preview.
                            // 세션이 준비되면 미리보기가 표시됩니다.
                            captureSession = cameraCaptureSession
                            try
                            {
                                // Auto focus should be continuous for camera preview.
                                // 카메라 미리보기를 위해 자동 초점이 연속적이어야합니다.
                                previewRequestBuilder!!.set(
                                        CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                                                           )
                                // Flash is automatically enabled when necessary.
                                // 필요한 경우 플래시가 자동으로 활성화됩니다.
                                setAutoFlash(previewRequestBuilder!!)

                                // Finally, we start displaying the camera preview.
                                // 마지막으로 카메라 미리보기를 표시합니다.
                                previewRequest = previewRequestBuilder!!.build()
                                captureSession!!.setRepeatingRequest(
                                        previewRequest!!,
                                        captureCallback, backgroundHandler
                                                                    )
                            }
                            catch (e: CameraAccessException)
                            {
                                Log.e(TAG, e.toString())
                            }
                        }

                        override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession)
                        {
                            showToast("Failed")
                        }
                    },
                    null
                                               )
        }
        catch (e: CameraAccessException)
        {
            Log.e(TAG, e.toString())
        }
    }

    private fun setAutoFlash(requestBuilder: CaptureRequest.Builder)
    {
        Log.e(TAG, "setAutoFlash() 실행됨")
        if (flashSupported)
        {
            requestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                              )
        }
    }

    fun takePicture()
    {
        if (null == cameraDevice)
        {
            Log.e(TAG, "mCameraDevice is null, return")
            return
        }

        try
        {
            var jpegSizes: Array<Size>? = null
            val cameraManager = mContext?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            if (map != null)
            {
                jpegSizes = map.getOutputSizes(ImageFormat.JPEG)
                //                Log.d("TEST", "map != null " + jpegSizes.length);
            }
            var width = 640
            var height = 480
            if (jpegSizes != null && 0 < jpegSizes.size)
            {
                //                for (int i = 0 ; i < jpegSizes.length; i++) {
                //                    Log.d("TEST", "getHeight = " + jpegSizes[i].getHeight() + ", getWidth = " + jpegSizes[i].getWidth());
                //                }
                width = jpegSizes[0].width
                height = jpegSizes[0].height
            }

            val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)
            val outputSurfaces = ArrayList<Surface>(2)
            outputSurfaces.add(reader.surface)
//            outputSurfaces.add(Surface(surfaceView.getSurfaceTexture()))

            val captureBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder?.addTarget(reader.surface)
            // captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            captureBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)

            mContext = context?.applicationContext

            // Orientation
//            val rotation = (mContext as Activity).windowManager.defaultDisplay.rotation
//            captureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))

            val date = Date()
            val dateFormat = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")

            val file = File(Environment.getExternalStorageDirectory().toString() + "/DCIM", "pic_" + dateFormat.format(date) + ".jpg")

            val readerListener = object : ImageReader.OnImageAvailableListener
            {
                override fun onImageAvailable(reader: ImageReader)
                {
                    var image: Image? = null
                    try
                    {
                        image = reader.acquireLatestImage()
                        val buffer = image!!.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        buffer.get(bytes)
                        save(bytes)
                    }
                    catch (e: FileNotFoundException)
                    {
                        e.printStackTrace()
                    }
                    catch (e: IOException)
                    {
                        e.printStackTrace()
                    }
                    finally
                    {
                        if (image != null)
                        {
                            image.close()
                            reader.close()
                        }
                    }
                }

                @Throws(IOException::class)
                private fun save(bytes: ByteArray)
                {
                    var output: OutputStream? = null
                    try
                    {
                        output = FileOutputStream(file)
                        output!!.write(bytes)
                    }
                    finally
                    {
                        output?.close()
                    }
                }
            }

            val thread = HandlerThread("CameraPicture")
            thread.start()
            val backgroudHandler = Handler(thread.looper)
            reader.setOnImageAvailableListener(readerListener, backgroudHandler)

            val captureListener = object : CameraCaptureSession.CaptureCallback()
            {
                override fun onCaptureCompleted(session: CameraCaptureSession,
                        request: CaptureRequest, result: TotalCaptureResult)
                {
                    super.onCaptureCompleted(session, request, result)
//                    Toast.makeText(mContext, "Saved:$file", Toast.LENGTH_SHORT).show()
                    showToast("Saved:$file")

                    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    intent.data = Uri.fromFile(file)
                    mContext?.sendBroadcast(intent)

                    posenetPicturUriPath = file.toString()

                    // 촬영 완료하면 인증 종료 화면으로 필요한 값 보내기
                    val intent1 = Intent(mContext, Activity_Posenet_Done_Certify::class.java)

                    intent1.putExtra("strParamName", posenetPicturUriPath)
                    intent1.putExtra("certiFrom", "camera")

                    intent1.putExtra("getID", getID)
                    intent1.putExtra("GET_JOIN_INDEX", GET_JOIN_INDEX)
                    intent1.putExtra("GET_JOIN_MEMBER_INDEX", GET_JOIN_MEMBER_INDEX)
                    intent1.putExtra("GET_PROOF_CATEGORY", GET_PROOF_CATEGORY)
                    intent1.putExtra("GET_PROOF_CERTI_COUNT", GET_PROOF_CERTI_COUNT)

                    intent1.putExtra("GET_PROOF_INDEX", GET_PROOF_INDEX)
                    intent1.putExtra("GET_PROOF_JOIN_PRICE", GET_PROOF_JOIN_PRICE)
                    intent1.putExtra("GET_PROOF_MY_CERTI_COUNT", GET_PROOF_MY_CERTI_COUNT)
                    intent1.putExtra("GET_PROOF_TITLE", GET_PROOF_TITLE)
                    intent1.putExtra("GET_PROOF_TYPE", GET_PROOF_TYPE)

                    intent1.putExtra("GET_REWARD", GET_REWARD)

                    mContext?.startActivity(intent1)

                    Log.e(TAG, "onCaptureCompleted: file: $file")

                    // 촬영 후 액티비티 종료
                    activity?.finish()
                }
            }

            cameraDevice?.createCaptureSession(outputSurfaces, object : CameraCaptureSession.StateCallback()
            {
                override fun onConfigured(session: CameraCaptureSession)
                {
                    try
                    {
                        session.capture(captureBuilder?.build(), captureListener, backgroudHandler)
                    }
                    catch (e: CameraAccessException)
                    {
                        e.printStackTrace()
                    }

                }

                override fun onConfigureFailed(session: CameraCaptureSession)
                {

                }
            }, backgroudHandler)

        }
        catch (e: CameraAccessException)
        {
            e.printStackTrace()
        }
    }

    /**
     * Shows an error message dialog.
     * 오류 메시지 대화 상자를 표시합니다.
     */
    class ErrorDialog : DialogFragment()
    {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
                AlertDialog.Builder(activity)
                        .setMessage(arguments!!.getString(ARG_MESSAGE))
                        .setPositiveButton(android.R.string.ok) { _, _ -> activity!!.finish() }
                        .create()

        companion object
        {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }

    companion object
    {
        /**
         * Conversion from screen rotation to JPEG orientation.
         * 화면 회전에서 JPEG 방향으로 변환
         */
        private val ORIENTATIONS = SparseIntArray()
        private val FRAGMENT_DIALOG = "dialog"

        init
        {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        // 5 ~ 10
        var LeftShoulderX: Int = 0
        var LeftShoulderY: Int = 0
        var RightShoulderX: Int = 0
        var RightShoulderY: Int = 0

        var LeftElbowX: Int = 0
        var LeftElbowY: Int = 0
        var RightElbowX: Int = 0
        var RightElbowY: Int = 0

        var LeftWristX: Int = 0
        var LeftWristY: Int = 0
        var RightWristX: Int = 0
        var RightWristY: Int = 0

        /*
        LeftShoulderX
        LeftShoulderY
        LeftElbowX
        LeftElbowY
        LeftWristX
        LeftWristY

        RightShoulderX
        RightShoulderY
        RightElbowX
        RightElbowY
        RightWristX
        RightWristY
        */

        // 11 ~ 16
        var LeftHipX: Int = 0
        var LeftHipY: Int = 0
        var RightHipX: Int = 0
        var RightHipY: Int = 0

        var LeftKneeX: Int = 0
        var LeftKneeY: Int = 0
        var RightKneeX: Int = 0
        var RightKneeY: Int = 0

        var LeftAnkleX: Int = 0
        var LeftAnkleY: Int = 0
        var RightAnkleX: Int = 0
        var RightAnkleY: Int = 0

        /*
                LeftHipY
                RightHipX
                LeftKneeY
                RightKneeX
                LeftAnkleY
                RightAnkleX
        */

        var needScore: Float = 0.75F
        var mScore: String? = null


//        var GET_LEFTHIP = LeftHipX
//        var GET_RIGHTHIP = RightHipY
//        var GET_LEFTKNEE = LeftKneeX
//        var GET_RIGHTKNEE = RightKneeY
//        var GET_LEFTANKLE = LeftAnkleX
//        var GET_RIGHTANKLE = RightAnkleY

//        var GET_NEEDSCORE = needScore
//        var GET_MSCORE = mScore

        var posenetPicturUriPath: String? = null

        /*
            LeftHipX
            RightHipY
            LeftKneeX
            RightKneeY
            LeftAnkleX
            RightAnkleY
        **/

        /**
         * Tag for the [Log].
         * [로그]에 대한 태그입니다.
         */
        private const val TAG = "PosenetActivity"
    }
}
