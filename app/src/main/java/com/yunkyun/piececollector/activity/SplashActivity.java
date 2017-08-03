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
import com.yunkyun.piececollector.dao.PlaceDAO;
import com.yunkyun.piececollector.network.NetworkService;
import com.yunkyun.piececollector.object.Place;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        syncDatabase();

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

    private void syncDatabase() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String clientVersion = sharedPreferences.getString("db_version", null);
        final NetworkService service = NetworkService.retrofit.create(NetworkService.class);

        if (clientVersion == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("db_version", getResources().getString(R.string.db_version));
            editor.commit();
            updateDatabase(service);
        } else {
            Call<String> call = service.getVersion("places");
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String serverVersion = response.body();
                    if (!clientVersion.equals(serverVersion)) {
                        updateDatabase(service);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "onFailure in syncDatabase()");
                }
            });

        }
    }

    private void updateDatabase(NetworkService service) {
        final PlaceDAO placeDAO = new PlaceDAO(this);
        Call<List<Place>> call = service.getPlaces();
        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                List<Place> placeList = response.body();
                placeDAO.insertPlaces(placeList);
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Log.e(TAG, "onFailure");
            }
        });
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
