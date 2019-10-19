package org.tensorflow.lite.examples.detection.wake.FragmentProof;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import org.tensorflow.lite.examples.detection.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.tensorflow.lite.examples.detection.wake.FragmentProof.CaptureUtil.strFilePath;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_THUMB;
import static org.tensorflow.lite.examples.detection.wake.FragmentProof.Fragment_Proof.GET_PROOF_TITLE;

public class Activity_Distance_Measurement extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener
{
    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    // private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int UPDATE_INTERVAL_MS = 5000;  // 5초
    // private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 5000; // 5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;

    LatLng currentPosition = null;
    LatLng previousPosition = null;
    Marker addedMarker = null;
    int tracking = 0;
//    int tracking = 1;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    private String TAG = "Activity_Distance_Measurement";

    private TextView distance_measurement_title     // 챌린지 제목
            , distance_measurement_distance         // 남은 거리
            , distance_measurement_distance_select  // 선택한 이동 거리
            ;

    private ImageView distance_measurement_thumb;

    private String Distance_Select = null;
    private String Arrive;

    public static Activity Activity_Distance_Measurement;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_measurement);

        Activity_Distance_Measurement = Activity_Distance_Measurement.this;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.e(TAG, "onCreate");

        // View Find
        distance_measurement_title = findViewById(R.id.distance_measurement_title);
        distance_measurement_distance = findViewById(R.id.distance_measurement_distance);
        distance_measurement_distance_select = findViewById(R.id.distance_measurement_distance_select);
        distance_measurement_thumb = findViewById(R.id.distance_measurement_thumb);

        distance_measurement_title.setText(GET_PROOF_TITLE);
        distance_measurement_distance.setText("선택 안 함");
        distance_measurement_distance_select.setText("선택 안 함");

        Picasso.get().load(GET_PROOF_THUMB).
                placeholder(R.drawable.logo_1).
                into(distance_measurement_thumb);

        mActivity = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // getMapAsync() 메소드가 메인 쓰레드에서 호출되어야 메인스레드에서 onMapReady 콜백이 실행된다
        mapFragment.getMapAsync(this);


        Log.e(TAG, "onCreate: GET_PROOF_TITLE: " + GET_PROOF_TITLE);
        Log.e(TAG, "onCreate: GET_PROOF_THUMB: " + GET_PROOF_THUMB);

        // todo: 시작 알림 다이얼로그 활성화
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Distance_Measurement.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_distance_measurment_notice_start, null);
        builder.setView(view);

        // 시작 다이얼로그
        ImageView dialog_distance_measurement_notice_background = view.findViewById(R.id.dialog_distance_measurement_notice_background);
        TextView dialog_distance_measurement_notice_title = view.findViewById(R.id.dialog_distance_measurement_notice_title);
        TextView dialog_distance_measurement_notice_check = view.findViewById(R.id.dialog_distance_measurement_notice_check);

        final AlertDialog dialog = builder.create();

        dialog_distance_measurement_notice_title.setText(GET_PROOF_TITLE);

        Picasso.get().load(GET_PROOF_THUMB).
                placeholder(R.drawable.logo_1).
                into(dialog_distance_measurement_notice_background);

        // 위치 재설정 / 마커 지우기
        dialog_distance_measurement_notice_check.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(mActivity, "목적지를 선택하세요", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
        // 알림 다이얼로그 활성화 끝

        // 거리 측정 시작
//        final Button button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                tracking = 1 - tracking;
//
//                Log.e(TAG, "onClick: tracking: " + tracking);
//
//                if (tracking == 1)
//                {
//                    button.setText("Stop");
//                } else button.setText("Start");
//            }
//        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (mRequestingLocationUpdates == false)
        {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED)
                {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } //

                else
                {

                    Log.e(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.e(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            } //

            else
            {

                Log.e(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.e(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.e(TAG, "onConnectionFailed");
        setDefaultLocation();
    }

    @Override
    public void onResume()
    {

        super.onResume();

        if (mGoogleApiClient.isConnected())
        {

            Log.e(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain)
        {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }

    private void startLocationUpdates()
    {
        if (!checkLocationServicesStatus())
        {

            Log.e(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else
        {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {

                Log.e(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.e(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }
    }

    private void stopLocationUpdates()
    {

        Log.e(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.e(TAG, "onMapReady :");

        mGoogleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        // CameraUpdateFactory.zoomTo 메소드를 사용하여 지정한 단계로 카메라 줌을 조정한다
        // 1 단계로 지정하면 세계지도 수준으로 보이고 숫자가 커질수록 상세지도가 보인다
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()
        {

            @Override
            public boolean onMyLocationButtonClick()
            {

                Log.e(TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {

            @Override
            public void onMapClick(LatLng latLng)
            {

                Log.e(TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener()
        {

            @Override
            public void onCameraMoveStarted(int i)
            {

                if (mMoveMapByUser == true && mRequestingLocationUpdates)
                {

                    Log.e(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener()
        {

            @Override
            public void onCameraMove()
            {


            }
        });

        // 원하는 위치를 사용자가 클릭했을 경우 마커를 추가하기
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(final LatLng latLng)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Distance_Measurement.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_distance_measurment_notice_run, null);
                builder.setView(view);

                final TextView button_submit = (TextView) view.findViewById(R.id.button_dialog_placeInfo);
                final TextView dialog_distance_measurement_start_notice = view.findViewById(R.id.dialog_distance_measurement_start_notice);
                final TextView dialog_distance_measurement_start_notice_title = view.findViewById(R.id.dialog_distance_measurement_start_notice_title);
                final ImageView dialog_distance_measurement_start_notice_background = view.findViewById(R.id.dialog_distance_measurement_start_notice_background);

                Picasso.get().load(GET_PROOF_THUMB).
                        placeholder(R.drawable.logo_1).
                        into(dialog_distance_measurement_start_notice_background);

                Log.e(TAG, "onClick: tracking: " + tracking );

                if (tracking == 1)
                {
                    button_submit.setText("위치 재설정");

                    dialog_distance_measurement_start_notice_title.setText("목적지 선택 취소");

                    dialog_distance_measurement_start_notice.setText("'위치 재설정'버튼을 눌러\n선택한 목적지를 취소합니다");
                }
                else
                {
                    button_submit.setText("시작하기");

                    dialog_distance_measurement_start_notice.setText("목적지까지 이동하세요");
                }

                final AlertDialog dialog = builder.create();
                button_submit.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
//                        String string_placeTitle = editText_placeTitle.getText().toString();
//                        String string_placeDesc = editText_placeDesc.getText().toString();

                        String string_placeTitle = "목적지";

                        tracking = 1 - tracking;
                        Log.e(TAG, "onClick: tracking: " + tracking );

                        Log.e(TAG, "onClick: tracking: " + tracking);

                        if (tracking == 1)
                        {
                            //맵을 클릭시 현재 위치에 마커 추가
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(string_placeTitle); // 피커 제목
//                        markerOptions.snippet(""); // 목적지에 대한 설명
                            markerOptions.draggable(true);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                            if (addedMarker != null) mGoogleMap.clear();
                            addedMarker = mGoogleMap.addMarker(markerOptions);

                            dialog.dismiss();
                        } //

                        else
                        {
                            Toast.makeText(mActivity, "위치를 재설정 하세요", Toast.LENGTH_SHORT).show();
                            distance_measurement_distance_select.setText("선택 안 함"); // 설정한 이동거리
                            distance_measurement_distance.setText("선택 안 함");        // 남은 거리
                            mGoogleMap.clear();

//                            mGoogleApiClient.disconnect();

                            dialog.dismiss();

//                            mGoogleApiClient.connect();
//                            startLocationUpdates();

                        }
//                        Toast.makeText(Activity_Distance_Measurement.this, string_placeTitle, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();

            }
        });
    }

    @Override
    public void onLocationChanged(Location location)
    {

        currentPosition
                = new LatLng(location.getLatitude(), location.getLongitude());


        Log.e(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        Log.e(TAG, "onLocationChanged: 위도: " + String.valueOf(location.getLatitude()));
        Log.e(TAG, "onLocationChanged: 경도: " + String.valueOf(location.getLongitude()));

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocatiion = location;

        previousPosition = currentPosition;

        currentPosition
                = new LatLng(location.getLatitude(), location.getLongitude());

        if (previousPosition == null) previousPosition = currentPosition;

        Log.e(TAG, "onLocationChanged: tracking: " + tracking);

        if ((addedMarker != null) && tracking == 1)
        {
            Log.e(TAG, "onLocationChanged: if (tracking: " + tracking);

            double radius = 10; // 500m distance. 입력한 거리 내에 들면 도착으로 간주함 (현재 10미터)

            double distance = SphericalUtil.computeDistanceBetween(currentPosition, addedMarker.getPosition());

            Log.e(TAG, "onLocationChanged: distance: " + (int) distance);
            Log.e(TAG, "onLocationChanged: previousPosition: " + previousPosition);
            Log.e(TAG, "onLocationChanged: currentPosition: " + currentPosition);

            if (TextUtils.isEmpty(Distance_Select))
            {
                Distance_Select = String.valueOf((int) distance + "m");
                Log.e(TAG, "onLocationChanged: Distance_Select: " + Distance_Select );
            }

            // 뷰에 선택한 거리, 남은 거리 체크
            distance_measurement_distance_select.setText(Distance_Select);               // 설정한 이동거리
            distance_measurement_distance.setText(String.valueOf((int) distance) + "m"); // 남은 거리

            Log.e(TAG, "onLocationChanged: " + addedMarker.getTitle() + "까지" + (int) distance + "m 남음");

            if ((distance < radius) /*&& (!previousPosition.equals(currentPosition))*/)
            {
                if (TextUtils.isEmpty(Arrive))
                {
                    Arrive = "Arrive";
                    Log.e(TAG, "onLocationChanged: distance < radius: 도달함 (Arrive: " + Arrive + ")");
                    stopLocationUpdates();
                }

                else if (!TextUtils.isEmpty(Arrive))
                {
                    Log.e(TAG, "onLocationChanged: 도착함" );
                }

//                Toast.makeText(this, addedMarker.getTitle() + "까지" + (int) distance + "m 남음", Toast.LENGTH_LONG).show();
                Log.e(TAG, "onLocationChanged: " + addedMarker.getTitle() + "까지" + (int) distance + "m 남음");
            }

            if (TextUtils.isEmpty(Arrive))
            {
                Log.e(TAG, "onLocationChanged: 선택 안 됨");
            }

            // Arrive에 값이 채워지면 다이얼로그 한 번 만 실행하기
            else if (!TextUtils.isEmpty(Arrive) && Arrive.equals("Arrive"))
            {
                // todo: 종료 알림 다이얼로그 활성화
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Distance_Measurement.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_distance_measurment_notice_end, null);
                builder.setView(view);

                final ImageView dialog_distance_measurement_notice_end_background = view.findViewById(R.id.dialog_distance_measurement_notice_end_background);
                TextView dialog_distance_measurement_notice_end_title = view.findViewById(R.id.dialog_distance_measurement_notice_end_title);
                final TextView dialog_distance_measurement_notice_end_check = view.findViewById(R.id.dialog_distance_measurement_notice_end_check);
                final FrameLayout dialog_distance_measurement_notice_end_frame = view.findViewById(R.id.dialog_distance_measurement_notice_end_frame);

                // 이동한 거리
                TextView dialog_distance_measurement_notice_end_measurement = view.findViewById(R.id.dialog_distance_measurement_notice_end_measurement);

                // 종료한 날짜
                TextView dialog_distance_measurement_notice_end_date = view.findViewById(R.id.dialog_distance_measurement_notice_end_date);

                final AlertDialog dialog = builder.create();

                dialog_distance_measurement_notice_end_title.setText(GET_PROOF_TITLE);

                dialog_distance_measurement_notice_end_measurement.setText(Distance_Select);

                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss", Locale.KOREA );
                Date currentTime = new Date ();
                String mTime = mSimpleDateFormat.format ( currentTime );

                dialog_distance_measurement_notice_end_date.setText(mTime);

                Picasso.get().load(GET_PROOF_THUMB).
                        placeholder(R.drawable.logo_1).
                        into(dialog_distance_measurement_notice_end_background);

                dialog_distance_measurement_notice_end_check.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog_distance_measurement_notice_end_check.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });

                dialog.show();
                // 알림 다이얼로그 활성화 끝


                // 다이얼로그 종료 감지
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        Log.e(TAG, "onLocationChanged: 다음 페이지로 이동" );
                        Toast.makeText(mActivity, "다음 페이지로 이동", Toast.LENGTH_SHORT).show();

                        // 뷰 캡쳐
                        CaptureUtil.captureView(dialog_distance_measurement_notice_end_frame);

                        Intent intent = new Intent(Activity_Distance_Measurement.this, Activity_Done_Certify.class);

                        intent.putExtra("strParamName", strFilePath);
                        intent.putExtra("certiFrom", "Distance_Measurement");

                        startActivity(intent);

                        finish();
                    }
                });
            }
        }
    }

    @Override
    protected void onStart()
    {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false)
        {
            Log.e(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop()
    {

        if (mRequestingLocationUpdates)
        {

            Log.e(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if (mGoogleApiClient.isConnected())
        {

            Log.e(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    public String getCurrentAddress(LatLng latlng)
    {
        Log.e(TAG, "getCurrentAddress: ");

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try
        {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException)
        {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException)
        {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        Log.e(TAG, "getCurrentAddress: addresses: " + addresses);

        if (addresses == null || addresses.size() == 0)
        {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            Log.e(TAG, "getCurrentAddress: " + "주소 미발견");
            return "주소 미발견";

        } //

        else //
        {
            Address address = addresses.get(0);
            Log.e(TAG, "getCurrentAddress: address: " + address.toString());
            Log.e(TAG, "getCurrentAddress: address.getAddressLine: " + address.getAddressLine(0).toString());
            return address.getAddressLine(0).toString();
        }
    }


    public boolean checkLocationServicesStatus()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet)
    {

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mGoogleMap.addMarker(markerOptions);


        if (mMoveMapByAPI)
        {

            Log.e(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude());
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    public void setDefaultLocation()
    {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions()
    {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale)
        {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED)
        {


            Log.e(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if (mGoogleApiClient.isConnected() == false)
            {

                Log.e(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0)
        {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted)
            {


                if (mGoogleApiClient.isConnected() == false)
                {

                    Log.e(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }


            } else
            {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Distance_Measurement.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Distance_Measurement.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Distance_Measurement.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus())
                {
                    if (checkLocationServicesStatus())
                    {

                        Log.e(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if (mGoogleApiClient.isConnected() == false)
                        {

                            Log.e(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }

}
