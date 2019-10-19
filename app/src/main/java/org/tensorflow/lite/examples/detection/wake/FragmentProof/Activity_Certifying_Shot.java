package org.tensorflow.lite.examples.detection.wake.FragmentProof;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.tensorflow.lite.examples.detection.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Certifying_Shot extends AppCompatActivity implements SurfaceHolder.Callback
{
    private String TAG = "Activity_Certifying_Shot";

    @SuppressWarnings("deprecation")
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    CircleImageView Serti_shot_button;
    String str;

    @SuppressWarnings("deprecation")
    Camera.PictureCallback jpegCallback;

    static final int PERMISSIONS_REQUEST_CODE = 1000;
    //    String[] PERMISSIONS = {"android.permission.CAMERA"};
    String[] PERMISSIONS = {"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static Activity Activity_Certifying_Shot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certifying_shot);

        Activity_Certifying_Shot = Activity_Certifying_Shot.this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) //
            {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        // 오토 포커스, 초점 새로고침
        FrameLayout surfaceArea = findViewById(R.id.surfaceArea);
        surfaceArea.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                camera.autoFocus(new Camera.AutoFocusCallback()
                {
                    public void onAutoFocus(boolean success, Camera camera)
                    {
                        if (success)
                        {
//                            Toast.makeText(Activity_Certifying_Shot.this, "초점 새로고침", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onAutoFocus: 초점 새로고침" );
//                    mCamera.takePicture(null, null, null);
                        }
                    }
                });

            }
        });


        Serti_shot_button = findViewById(R.id.Serti_shot_button);
        Serti_shot_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Serti_shot_button.setEnabled(false);
                camera.takePicture(null, null, jpegCallback);
            }
        });

        getWindow().setFormat(PixelFormat.UNKNOWN);

//        getWindow().setFlags(WindowManager.Lay    outParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback()
        {
            @Override
            public void onPictureTaken(byte[] data, Camera camera)
            {
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

                Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_LONG).show();
                refreshCamera();

                // todo: 촬영 및 저장 완료되면 다음 액티비티로 이동하기 (인증 완료 액티비티)
                Intent intent = new Intent(Activity_Certifying_Shot.this, Activity_Done_Certify.class);
                intent.putExtra("strParamName", str);
                intent.putExtra("certiFrom", "camera");
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // 사진 다시 찍으러 오면 촬영 버튼 활성화
        Serti_shot_button.setEnabled(true);
    }

    public void refreshCamera()
    {
        if (surfaceHolder.getSurface() == null)
        {
            return;
        }

        try
        {
            camera.stopPreview();
        } catch (Exception e)
        {
        }

        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e)
        {
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    int sw = 1;

    @SuppressWarnings("deprecation")
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // 카메라를 open()할 수 있다.
        camera.stopPreview();

        int rotation = Activity_Certifying_Shot.getWindowManager().getDefaultDisplay().getRotation();
        int degress = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
                degress = 0;
                break;
            case Surface.ROTATION_90:
                degress = 90;
                break;
            case Surface.ROTATION_180:
                degress = 180;
                break;
            case Surface.ROTATION_270:
                degress = 270;
                break;
        }

        int result = (90 - degress + 360) % 360;
        camera.setDisplayOrientation(result);

        Camera.Parameters param = camera.getParameters();
        param.setRotation(90);
        camera.setParameters(param);

        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (Exception e)
        {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    //여기서부턴 퍼미션 관련 메소드
    private boolean hasPermissions(String[] permissions)
    {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions)
        {

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED)
            {
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

//                    if (!cameraPermissionAccepted)
//                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

                    boolean writePermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted || !writePermissionAccepted)
                    {
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                        return;
                    } else
                    {
//                        read_cascade_file();
                    }
                }
                break;
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                finish();
            }
        });
        builder.create().show();
    }
}
