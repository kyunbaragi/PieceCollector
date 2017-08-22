package com.yunkyun.piececollector.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.help.HelpFragment1;
import com.yunkyun.piececollector.help.HelpFragment2;
import com.yunkyun.piececollector.help.HelpFragment3;
import com.yunkyun.piececollector.help.HelpFragment4;
import com.yunkyun.piececollector.help.HelpFragment5;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-08-21.
 */

public class HelpActivity extends BaseActivity {
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.btn_next)
    Button nextButton;

    private static final String TAG = "HelpActivity";
    private static final int MAX_HELP_FRAGMENT_COUNT = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MAX_HELP_FRAGMENT_COUNT);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        nextButton.setText("다음");
                        break;
                    case 4:
                        nextButton.setText("확인");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int next = getNextItem();
                if(next == MAX_HELP_FRAGMENT_COUNT) {
                    finish();
                } else {
                    viewPager.setCurrentItem(next);
                }
            }
        });
    }

    private int getNextItem() {
        return viewPager.getCurrentItem() + 1;
    }

    class PagerAdapter extends FragmentStatePagerAdapter {
        private int fragmentCount;

        public PagerAdapter(FragmentManager fragmentManager, int fragmentCount) {
            super(fragmentManager);
            this.fragmentCount = fragmentCount;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new HelpFragment1();
                    break;
                case 1:
                    fragment = new HelpFragment2();
                    break;
                case 2:
                    fragment = new HelpFragment3();
                    break;
                case 3:
                    fragment = new HelpFragment4();
                    break;
                case 4:
                    fragment = new HelpFragment5();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return fragmentCount;
        }
    }
}
