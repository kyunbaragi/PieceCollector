package com.yunkyun.piececollector.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.dao.PlaceDAO;
import com.yunkyun.piececollector.object.Place;
import com.yunkyun.piececollector.util.PermissionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class MapActivity extends BaseActivity implements OnMapReadyCallback {
    @BindView(R.id.iv_my_location)
    ImageView myLocation;
    @BindView(R.id.iv_search_by_location)
    ImageView searchByLocation;
    @BindView(R.id.tv_current_location)
    TextView currentLocation;
    @BindView(R.id.toolbar_map)
    Toolbar toolbar;

    private static final String TAG = "MapActivity";
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final float MIN_ZOOM = 14.0f;
    private static final float MAX_ZOOM = 18.0f;
    private static final double DEFAULT_LATITUDE = 37.5759880;
    private static final double DEFAULT_LONGITUDE = 126.9769230;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    private ClusterManager<Place> clusterManager;
    private double currentLat;
    private double currentLng;
    private boolean flag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.ic_tracker).into(myLocation);
        Glide.with(this).load(R.drawable.ic_loading).into(searchByLocation);

        // ACCESS_FINE_LOCATION 권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionManager.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        connectGoogleApi();
        requestGPS();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(MIN_ZOOM);
        map.setMaxZoomPreference(MAX_ZOOM);

        LatLng defaultPosition = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        map.moveCamera(CameraUpdateFactory.newLatLng(defaultPosition));
    }

    private void moveCamera(double lat, double lng) {
        LatLng currentPosition = new LatLng(lat, lng);
        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(currentPosition);
        map.animateCamera(cameraPosition);
    }

    private void setClusterManager() {
        clusterManager = new ClusterManager<>(this, map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        addPlaces();
    }

    private void addPlaces() {
        PlaceDAO placeDAO = new PlaceDAO(this);
        List<Place> placeList = placeDAO.selectPlaces();
        clusterManager.addItems(placeList);
    }

    @OnClick({R.id.btn_my_location, R.id.btn_search_by_location, R.id.btn_back, R.id.fab_camera})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_my_location:
                moveCamera(currentLat, currentLng);
                break;
            case R.id.btn_search_by_location:
                break;
            case R.id.fab_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    private void requestGPS() {
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
                            Log.d(TAG, "LocationSettingsStatusCodes.SUCCESS");
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

    private void startLocationUpdate() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    if (flag) {
                        LatLng currentPosition = new LatLng(currentLat, currentLng);
                        map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                        flag = false;
                    }
                    String userLocation = String.format("위도: %f / 경도: %f", currentLat, currentLng);
                    currentLocation.setText(userLocation);
                }
            }
        };
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        } catch (SecurityException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void connectGoogleApi() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.e(TAG, "onConnected() in connectGoogleApi");
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.e(TAG, "onConnectionSuspended() in connectGoogleApi");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e(TAG, "onConnectionFailed() in connectGoogleApi");
                        }
                    })
                    .build();
            googleApiClient.connect();
        } else {
            api.getErrorDialog(this, code, 0).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == Activity.RESULT_OK) && (requestCode == REQUEST_CHECK_SETTINGS)) {
            startLocationUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationListener != null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
            } catch (SecurityException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationListener != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }
}
