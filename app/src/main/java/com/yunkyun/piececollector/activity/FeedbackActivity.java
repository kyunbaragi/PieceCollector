package com.yunkyun.piececollector.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.yunkyun.piececollector.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-08-17.
 */

public class FeedbackActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_close)
    void onButtionClick(View view) {
        onBackPressed();
    }
}
