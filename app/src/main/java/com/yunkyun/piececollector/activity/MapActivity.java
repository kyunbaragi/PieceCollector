package com.yunkyun.piececollector.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.dao.PlaceDAO;
import com.yunkyun.piececollector.object.Place;
import com.yunkyun.piececollector.util.AlerterMaker;
import com.yunkyun.piececollector.util.AppPreferenceKey;
import com.yunkyun.piececollector.util.MyMath;
import com.yunkyun.piececollector.util.PermissionManager;
import com.yunkyun.piececollector.util.SharedPreferencesService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class MapActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener {
    @BindView(R.id.iv_my_location)
    ImageView myLocation;
    @BindView(R.id.iv_search_by_location)
    ImageView searchByLocation;
    @BindView(R.id.tv_current_location)
    TextView currentLocation;
    @BindView(R.id.toolbar_map)
    Toolbar toolbar;
    View markerView;
    View userPositionView;

    private static final String TAG = "MapActivity";
    private static final int REQUEST_CHECK_SETTINGS = 0;
    private static final int TAKE_PHOTO_FROM_CAMERA = 1;
    private static final float MIN_ZOOM = 14.0f;
    private static final float MAX_ZOOM = 18.0f;
    private static final double DEFAULT_LATITUDE = 37.5759880;
    private static final double DEFAULT_LONGITUDE = 126.9769230;
    private static final double VALID_LATITUDE_RADIUS = 0.00045;
    private static final double VALID_LONGITUDE_RADIUS = 0.00055;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private Marker userPosition;
    private double currentLat = DEFAULT_LATITUDE;
    private double currentLng = DEFAULT_LONGITUDE;
    private boolean flag = true;

    private File imageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        // 아이콘 이미지 출력
        Glide.with(this).load(R.drawable.ic_tracker).into(myLocation);
        Glide.with(this).load(R.drawable.ic_loading).into(searchByLocation);

        // 여행지, 유저위치 마커 레이아웃 인플레이션
        markerView = LayoutInflater.from(this).inflate(R.layout.marker, null);
        userPositionView = LayoutInflater.from(this).inflate(R.layout.user_position, null);

        // GPS 권한 체크 및 요청
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionManager.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        connectGoogleApi();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setTrafficEnabled(false);
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        map.setMinZoomPreference(MIN_ZOOM);
        map.setMaxZoomPreference(MAX_ZOOM);

        if (!isGpsPossible()) {
            LatLng defaultPosition = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
            map.moveCamera(CameraUpdateFactory.newLatLng(defaultPosition));
        }
    }

    private void addMarkersToMap() {
        PlaceDAO placeDAO = new PlaceDAO(this);
        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        List<Place> placeList = placeDAO.selectPlacesByLatLng(visibleRegion.farRight, visibleRegion.nearLeft);

        if (placeList.size() == 0) {
            AlerterMaker.makeShortAlerter(this, "검색된 여행조각이 없습니다.", "", R.color.alarmDefault);
        } else {
            map.clear();
            for (Place place : placeList) {
                Log.e(TAG, place.toString());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, markerView)));
                markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                markerOptions.title(place.getTitle());
                map.addMarker(markerOptions).setTag(place);
            }
            userPosition = null;
            drawUserPosition(getUserPosition());
        }
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void moveToUserPosition() {
        if (isGpsPossible()) {
            LatLng currentPosition = getUserPosition();
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(currentPosition);
            map.animateCamera(cameraPosition);
            drawUserPosition(currentPosition);
        } else {
            requestGps();
        }
    }

    private LatLng getUserPosition() {
        return new LatLng(currentLat, currentLng);
    }

    private void drawUserPosition(LatLng currentPosition) {
        if (userPosition != null) {
            userPosition.setPosition(currentPosition);
        } else {
            userPosition = map.addMarker(new MarkerOptions()
                    .position(currentPosition)
                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, userPositionView))));
        }
    }

    @OnClick({R.id.btn_my_location, R.id.btn_search_by_location, R.id.btn_back, R.id.fab_camera})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_my_location:
                moveToUserPosition();
                break;
            case R.id.btn_search_by_location:
                addMarkersToMap();
                break;
            case R.id.fab_camera:
                showCamera();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    private void showCamera() {
        Uri uri = getFileUri();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, TAKE_PHOTO_FROM_CAMERA);
    }

    private Uri getFileUri() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES, "PieceCollector");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String imageFileName = new SimpleDateFormat("yyyyMMddkkmmss").format(new Date()) + ".jpg";
        imageFile = new File(dir, imageFileName);
        return FileProvider.getUriForFile(this, "com.yunkyun.piececollector.provider", imageFile);
    }

    private void requestGps() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        if (result != null) {
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            setLocationListener();
                            startLocationUpdate();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // TODO: Ignore the error.
                                Log.e(TAG, e.toString());
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
    }

    private boolean isGpsPossible() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void setLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    if (flag) {
                        LatLng currentPosition = getUserPosition();
                        map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                        addMarkersToMap();
                        drawUserPosition(currentPosition);
                        flag = false;
                    }
                    String userLocation = String.format("위도: %.5f / 경도: %.5f", currentLat, currentLng);
                    currentLocation.setText(userLocation);
                }
            }
        };
    }

    private void startLocationUpdate() {
        if (locationListener != null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
            } catch (SecurityException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void stopLocationUpdate() {
        if (locationListener != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
        }
    }

    private void connectGoogleApi() {
        if (isGoogleApiAvailable()) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
            googleApiClient.connect();
        }
    }

    private boolean isGoogleApiAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            return true;
        } else {
            api.getErrorDialog(this, code, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestGps();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended() in connectGoogleApi");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    setLocationListener();
                    startLocationUpdate();
                    break;
                case TAKE_PHOTO_FROM_CAMERA:
                    checkMarkersOnMap();
                    break;
            }
        }
    }

    private void checkMarkersOnMap() {
        // 현재위치를 중심으로, 한 변의 길이가 100m인 정사각형 이내의 여행지를 검색한다.
        double maxLatitude = currentLat + VALID_LATITUDE_RADIUS;
        double maxLongitude = currentLng + VALID_LONGITUDE_RADIUS;
        double minLatitude = currentLat - VALID_LATITUDE_RADIUS;
        double minLongitude = currentLng - VALID_LONGITUDE_RADIUS;

        PlaceDAO placeDAO = new PlaceDAO(this);
        List<Place> placeList = placeDAO.selectPlacesByLatLng(new LatLng(maxLatitude, maxLongitude), new LatLng(minLatitude, minLongitude));
        if (placeList.size() == 0) {
            AlerterMaker.makeLongAlerter(this, "여행조각 획득에 실패하였습니다.", "1. 여행지에 조금 더 가까이 가주세요.\n2. 현재 위치를 다시 한 번 확인해주세요.", R.color.alarmFailure);
        } else {
            Place minPlace = null;
            double minDistance = Double.MAX_VALUE;
            for (Place place : placeList) {
                double distance = MyMath.calDistance(place.getLatitude(), place.getLongitude(), currentLat, currentLng);
                if (distance < minDistance) {
                    minDistance = distance;
                    minPlace = place;
                }
            }

            // TODO: 중복방문 체크
            if (minPlace.getVisited() == 1) {
                AlerterMaker.makeLongAlerter(this, "여행조각 획득에 실패하였습니다.", "1. 이미 획득한 여행조각입니다.", R.color.alarmFailure);
            } else {
                AlerterMaker.makeLongAlerter(this, "여행조각을 획득하였습니다.", minPlace.getTitle(), R.color.alarmSuccess);

                // TODO: 로컬데이터 저장
                placeDAO.updatePlaceById(minPlace.getId());

                // TODO: 데이터 서버 전송, 사진
                uploadImage(minPlace);
            }
        }
    }

    private void uploadImage(Place place) {
        NetworkService service = NetworkService.retrofit.create(NetworkService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        HashMap<String, String> parameters = new HashMap<>();

        Long userID = SharedPreferencesService.getInstance().getPrefLongData(AppPreferenceKey.PREF_USER_ID_KEY);

        parameters.put("user_id", String.valueOf(userID));
        parameters.put("place_id", String.valueOf(place.getId()));

        Call<okhttp3.ResponseBody> call = service.postImage(parameters, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG, "onResponse in uploadImage");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure in uploadImage");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;

        public CustomInfoWindowAdapter(Context context) {
            mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(Marker marker, View view) {
            TextView titleUi = (TextView) view.findViewById(R.id.tv_place_title);
            TextView locationUi = (TextView) view.findViewById(R.id.tv_place_position);
            TextView distanceUi = (TextView) view.findViewById(R.id.tv_place_distance);

            Place place = (Place) marker.getTag();

            if(place != null) {
                titleUi.setText(place.getTitle());
                String placeLocation = String.format("위도: %.5f / 경도: %.5f", place.getLatitude(), place.getLongitude());
                locationUi.setText(placeLocation);
                double distance = MyMath.calDistance(place.getLatitude(), place.getLongitude(), currentLat, currentLng);
                String placeDistance = String.format("%.2f km", distance);
                distanceUi.setText(placeDistance);
            }
        }
    }
}
