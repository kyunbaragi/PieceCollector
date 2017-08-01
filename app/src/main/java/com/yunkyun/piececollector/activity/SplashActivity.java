package com.yunkyun.piececollector.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.yunkyun.piececollector.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by YunKyun on 2017-07-26.
 */

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_TIME = 1000;
    @BindView(R.id.iv_splash_logo)
    ImageView appLogo;

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.logo_vertical).into(appLogo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                long userID = sharedPreferences.getLong("user_id", Long.MAX_VALUE);

                if (userID == Long.MAX_VALUE) {
                    overridePendingTransition(0, android.R.anim.fade_out);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    callback = new SessionCallback();
                    Session.getCurrentSession().addCallback(callback);
                    Session.getCurrentSession().checkAndImplicitOpen();
                }
            }
        }, SPLASH_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            startActivity(intent);
            finish();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Log.d(TAG, "onSessionOpenFailed");
            }
        }
    }
}
