package com.yunkyun.piececollector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.adapter.HistoryRecyclerAdapter;
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

public class HistoryFragment extends Fragment {
    @BindView(R.id.rv_history)
    RecyclerView recyclerView;

    public static final String TAG = "HistoryFragment";
    private HistoryRecyclerAdapter adapter;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    public HistoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        setRecyclerView();

        return view;
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new HistoryRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setContents(List<Record> recordList) {
        adapter.setRecordList(recordList);
        adapter.notifyDataSetChanged();
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
                setContents(recordList);
            }

            @Override
            public void onFailure(Call<List<Record>> call, Throwable t) {

            }
        });
    }
}
