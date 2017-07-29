package com.yunkyun.piececollector.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yunkyun.piececollector.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class MapActivity extends BaseActivity {
    @BindView(R.id.toolbar_map) Toolbar toolbar;
    @BindView(R.id.fab_camera) FloatingActionButton fab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.fab_camera, R.id.btn_back})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.fab_camera:
                break;
            default:
                break;
        }
    }
}
