package com.yunkyun.piececollector.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.adapter.PlaceRecyclerAdapter;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.object.Collection;
import com.yunkyun.piececollector.object.Place;
import com.yunkyun.piececollector.object.Record;
import com.yunkyun.piececollector.util.AppPreferenceKey;
import com.yunkyun.piececollector.util.SharedPreferencesService;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @BindView(R.id.iv_photo)
    ImageView collectionImage;
    @BindView(R.id.tv_collection_title)
    TextView collectionTitle;
    @BindView(R.id.tv_collection_description)
    TextView collectionDesc;

    private static final String TAG = "CollectionActivity";
    private PlaceRecyclerAdapter adapter;
    private Collection collection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            collection = intent.getParcelableExtra("collection");
            Glide.with(this).load(collection.getImagePath()).into(collectionImage);
            collectionTitle.setText(collection.getTitle());
            collectionDesc.setText(collection.getDescription());
        }

        setRecyclerView();
        setPercentCircleBackground();

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

    private void setPercentCircleBackground() {
        // Create background track
        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(36f)
                .build());
    }

    private void drawPercentCircleTrack(float endPosition) {
        //Create data series track
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#E81751"))
                .setRange(0, 100, 0)
                .setLineWidth(36f)
                .build();

        int seriesIndex = arcView.addSeries(seriesItem);

        arcView.addEvent(new DecoEvent.Builder(endPosition).setDelay(500).setIndex(seriesIndex).build());

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
        final NetworkService service = NetworkService.retrofit.create(NetworkService.class);
        Call<List<Place>> placeCall = service.getPlacesByCollection(String.valueOf(collection.getId()));

        placeCall.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                final List<Place> placeList = response.body();

                String userID = String.valueOf(SharedPreferencesService.getInstance().getPrefLongData(AppPreferenceKey.PREF_USER_ID_KEY));
                Call<List<Record>> recordCall = service.getRecords(userID);

                recordCall.enqueue(new Callback<List<Record>>() {
                    @Override
                    public void onResponse(Call<List<Record>> call, Response<List<Record>> response) {
                        List<Record> recordList = response.body();

                        HashMap<String, Record> recordHashMap = new HashMap<String, Record>();
                        for (Record record : recordList) {
                            recordHashMap.put(record.getTitle(), record);
                        }

                        float achieved = 0.0f;
                        for (Place place : placeList) {
                            if (recordHashMap.containsKey(place.getTitle())) {
                                place.setVisited(1);
                                achieved++;
                            } else {
                                place.setVisited(0);
                            }
                        }

                        float endPosition = achieved / placeList.size() * 100;
                        drawPercentCircleTrack(endPosition);

                        setPlaceList(placeList);
                    }

                    @Override
                    public void onFailure(Call<List<Record>> call, Throwable t) {
                        Log.e(TAG, "onFailure");
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Log.e(TAG, "onFailure");
            }
        });
    }

    private void setPlaceList(List<Place> placeList) {
        adapter.setPlaceList(placeList);
        adapter.notifyDataSetChanged();
    }
}
