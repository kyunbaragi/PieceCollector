package com.yunkyun.piececollector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.object.Record;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-07-29.
 */

public class RecordActivity extends BaseActivity {
    @BindView(R.id.iv_photo)
    ImageView photoUI;
    @BindView(R.id.tv_detail_title)
    TextView titleUI;
    @BindView(R.id.tv_detail_date)
    TextView dateUI;
    @BindView(R.id.tv_detail_memo)
    TextView memoUI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            Record record = intent.getParcelableExtra("record");
            Glide.with(this).load(record.getImagePath()).into(photoUI);
            titleUI.setText(record.getTitle());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(record.getCreated());

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String date = String.format("%d. %d. %d.", year, month, day);

            dateUI.setText(date);
            String memo = record.getMemo();
            if(memo == null){
                memo = "메모를 남겨주세요.";
            }
            memoUI.setText(memo);
        }


    }

    @OnClick({R.id.btn_edit, R.id.btn_pick_photo, R.id.btn_share, R.id.btn_back})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_edit:
                break;
            case R.id.btn_pick_photo:
                showGallery();
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void showGallery() {

    }
}
