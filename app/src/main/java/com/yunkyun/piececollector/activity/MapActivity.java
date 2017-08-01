package com.yunkyun.piececollector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yunkyun.piececollector.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class MapActivity extends BaseActivity implements OnMapReadyCallback {
    @BindView(R.id.toolbar_map)
    Toolbar toolbar;
    @BindView(R.id.fab_camera)
    FloatingActionButton fab;
    @BindView(R.id.tv_map_address)
    TextView addressView;
    private SupportMapFragment mapFragment;
    private GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        setMapFragment();
    }

    private void setMapFragment() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMinZoomPreference(8.0f);
        map.setMaxZoomPreference(18.0f);

        // TODO: 유저의 현재위치를 받아와 적당한 줌으로 카메라 이동
        /*LatLng seoul = new LatLng(37.551161, 126.988194);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12));*/


        /*LatLngBounds AUSTRALIA = new LatLngBounds(
                new LatLng(-44, 113), new LatLng(-10, 154));*/

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(37.5801859611, 126.9767235747);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng sydney2 = new LatLng(37.5796170, 126.9770410);
        map.addMarker(new MarkerOptions().position(sydney2).title("Marker in Sydney2"));
    }

    @OnClick({R.id.fab_camera, R.id.btn_back})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent);
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }
}
