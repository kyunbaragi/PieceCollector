package com.yunkyun.piececollector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.adapter.RecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class MainFragment extends Fragment {
    public static final String TAG = "MainFragment";
    @BindView(R.id.rv_main)
    RecyclerView recyclerView;
    private RecyclerAdapter adapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        setRecyclerView();
        setContents();

        return view;
    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new RecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void setContents() {

    }


}
