package com.yunkyun.piececollector.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunkyun.piececollector.R;

import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-08-21.
 */

public class HelpFragment2 extends Fragment {
    public HelpFragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
