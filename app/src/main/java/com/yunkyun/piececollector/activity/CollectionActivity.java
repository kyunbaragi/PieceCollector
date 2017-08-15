package com.yunkyun.piececollector.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.adapter.PlaceRecyclerAdapter;
import com.yunkyun.piececollector.object.Place;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-08-14.
 */

public class CollectionActivity extends BaseActivity {
    @BindView(R.id.rv_collection_place)
    RecyclerView recyclerView;
    @BindView(R.id.tv_collection_percent)
    TextView percentView;
    @BindView(R.id.percent_circle_view)
    DecoView arcView;

    private static final String TAG = "CollectionActivity";
    private PlaceRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        setRecyclerView();
        setPercentCircleView();

        loadPlaceList();
    }

    @OnClick({R.id.btn_back})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void setPercentCircleView() {
        // Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(36f)
                .build());

        //Create data series track
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#E81751"))
                .setRange(0, 100, 0)
                .setLineWidth(36f)
                .build();

        int seriesIndex = arcView.addSeries(seriesItem);

        arcView.addEvent(new DecoEvent.Builder(85).setIndex(seriesIndex).build());

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                String format = "%.0f%%";
                if (format.contains("%%")) {
                    float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                    percentView.setText(String.format(format, percentFilled * 100f));
                } else {
                    percentView.setText(String.format(format, currentPosition));
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PlaceRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void loadPlaceList() {
        List<Place> placeList = new ArrayList<>();
        placeList.add(new Place());
        placeList.add(new Place());
        placeList.add(new Place());
        placeList.add(new Place());
        placeList.add(new Place());

        setPlaceList(placeList);
    }

    private void setPlaceList(List<Place> placeList) {
        adapter.setCollectionList(placeList);
        adapter.notifyDataSetChanged();
    }
}
