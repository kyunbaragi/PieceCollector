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
import com.yunkyun.piececollector.adapter.CollectionRecyclerAdapter;
import com.yunkyun.piececollector.object.Collection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class CollectionFragment extends Fragment {
    @BindView(R.id.rv_collection)
    RecyclerView recyclerView;

    public static final String TAG = "CollectionFragment";
    private CollectionRecyclerAdapter adapter;

    public static CollectionFragment newInstance() {
        CollectionFragment fragment = new CollectionFragment();
        return fragment;
    }

    public CollectionFragment() {
        // Required empty public constructor.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this, view);

        setRecyclerView();
        loadCollectionList();

        return view;
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CollectionRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void loadCollectionList() {
        List<Collection> collectionList = new ArrayList<>();
        collectionList.add(new Collection());

        setCollectionList(collectionList);
    }

    private void setCollectionList(List<Collection> collectionList) {
        adapter.setCollectionList(collectionList);
        adapter.notifyDataSetChanged();
    }
}
