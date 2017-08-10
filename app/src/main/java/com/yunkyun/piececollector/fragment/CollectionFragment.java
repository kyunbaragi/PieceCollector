package com.yunkyun.piececollector.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunkyun.piececollector.R;

import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class CollectionFragment extends Fragment {
    public static final String TAG = "CollectionFragment";

    public static CollectionFragment newInstance() {
        CollectionFragment fragment = new CollectionFragment();
        return fragment;
    }

    public CollectionFragment() {
        // Required empty public constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
