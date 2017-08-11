package com.yunkyun.piececollector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.adapter.MainRecyclerAdapter;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.object.Record;
import com.yunkyun.piececollector.util.AppPreferenceKey;
import com.yunkyun.piececollector.util.SharedPreferencesService;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class MainFragment extends Fragment implements RecyclerRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_main)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    RecyclerRefreshLayout refreshLayout;

    public static final String TAG = "MainFragment";
    private MainRecyclerAdapter adapter;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Load User data from server.
        loadRecordList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        setRecyclerView();
        setRefreshLayout();

        return view;
    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new MainRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setRefreshLayout() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setAnimateToRefreshDuration(1 * 1000);
        refreshLayout.setRefreshStyle(RecyclerRefreshLayout.RefreshStyle.NORMAL);
    }

    private void setContents(List<Record> recordList) {
        adapter.setRecordList(recordList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        loadRecordList();
        refreshLayout.setRefreshing(false);
    }

    private void loadRecordList() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", String.valueOf(SharedPreferencesService.getInstance().getPrefLongData(AppPreferenceKey.PREF_USER_ID_KEY)));
        NetworkService service = NetworkService.retrofit.create(NetworkService.class);

        Call<List<Record>> call = service.getRecords(parameters);
        call.enqueue(new Callback<List<Record>>() {
            @Override
            public void onResponse(Call<List<Record>> call, Response<List<Record>> response) {
                List<Record> recordList = response.body();
                /*for(Record record : recordList){
                    Log.e(TAG, record.toString());
                }*/
                setContents(recordList);
            }

            @Override
            public void onFailure(Call<List<Record>> call, Throwable t) {

            }
        });
    }
}
