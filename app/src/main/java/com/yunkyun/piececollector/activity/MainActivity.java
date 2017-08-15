package com.yunkyun.piececollector.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.dao.PlaceDAO;
import com.yunkyun.piececollector.fragment.CollectionFragment;
import com.yunkyun.piececollector.fragment.HistoryFragment;
import com.yunkyun.piececollector.fragment.MainFragment;
import com.yunkyun.piececollector.fragment.ProfileFragment;
import com.yunkyun.piececollector.object.Place;
import com.yunkyun.piececollector.util.AppPreferenceKey;
import com.yunkyun.piececollector.util.PermissionManager;
import com.yunkyun.piececollector.util.PreferenceKey;
import com.yunkyun.piececollector.util.SharedPreferencesService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    @BindView(R.id.iv_home)
    ImageView btnHomeImage;
    @BindView(R.id.iv_history)
    ImageView btnHistoryImage;
    @BindView(R.id.iv_collection)
    ImageView btnCollectionImage;
    @BindView(R.id.iv_profile)
    ImageView btnProfileImage;
    @BindView(R.id.tv_home)
    TextView btnHomeText;
    @BindView(R.id.tv_history)
    TextView btnHistoryText;
    @BindView(R.id.tv_collection)
    TextView btnCollectionText;
    @BindView(R.id.tv_profile)
    TextView btnProfileText;
    @BindView(R.id.fab_map)
    FloatingActionButton mapButton;

    public static final String TAG = "MainActivity";
    private static final int FINISH_MAIN_ACTIVITY = 0;
    private FragmentManager fragmentManager;
    private boolean finishFlag;
    private Toast finishToast;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        syncDatabase();

        setFinishToast();
        setFragmentManager();

        PermissionManager.checkPermission(this,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void syncDatabase() {
        final String clientVersion = SharedPreferencesService.getInstance().getPrefStringData(AppPreferenceKey.PREF_DB_VERSION_KEY);
        final NetworkService service = NetworkService.retrofit.create(NetworkService.class);

        if (clientVersion.isEmpty()) {
            SharedPreferencesService.getInstance().setPrefData(PreferenceKey.DB_VERSION, "1.0");
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

    @OnClick({R.id.btn_nav_home, R.id.btn_nav_history, R.id.btn_nav_collection, R.id.btn_nav_profile})
    void onNavigationButtonClick(View view) {
        Fragment nextFragment = null;
        String nextTag = "";

        switch (view.getId()) {
            case R.id.btn_nav_home:
                nextFragment = MainFragment.newInstance();
                nextTag = MainFragment.TAG;
                mapButton.show();
                break;
            case R.id.btn_nav_history:
                nextFragment = HistoryFragment.newInstance();
                nextTag = HistoryFragment.TAG;
                mapButton.hide();
                break;
            case R.id.btn_nav_collection:
                nextFragment = CollectionFragment.newInstance();
                nextTag = CollectionFragment.TAG;
                mapButton.hide();
                break;
            case R.id.btn_nav_profile:
                nextFragment = ProfileFragment.newInstance();
                nextTag = ProfileFragment.TAG;
                mapButton.hide();
                break;
        }
        changeButtonResource(view.getId());
        changeFragment(nextFragment, nextTag);
    }

    private void changeButtonResource(int id) {
        switch (id) {
            case R.id.btn_nav_home:
                btnHomeImage.setImageResource(R.drawable.ic_home_clicked);
                btnHistoryImage.setImageResource(R.drawable.ic_history);
                btnCollectionImage.setImageResource(R.drawable.ic_collection);
                btnProfileImage.setImageResource(R.drawable.ic_profile);
                btnHomeText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                btnHistoryText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnCollectionText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnProfileText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                break;
            case R.id.btn_nav_history:
                btnHomeImage.setImageResource(R.drawable.ic_home);
                btnHistoryImage.setImageResource(R.drawable.ic_history_clicked);
                btnCollectionImage.setImageResource(R.drawable.ic_collection);
                btnProfileImage.setImageResource(R.drawable.ic_profile);
                btnHomeText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnHistoryText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                btnCollectionText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnProfileText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                break;
            case R.id.btn_nav_collection:
                btnHomeImage.setImageResource(R.drawable.ic_home);
                btnHistoryImage.setImageResource(R.drawable.ic_history);
                btnCollectionImage.setImageResource(R.drawable.ic_collection_clicked);
                btnProfileImage.setImageResource(R.drawable.ic_profile);
                btnHomeText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnHistoryText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnCollectionText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                btnProfileText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                break;
            case R.id.btn_nav_profile:
                btnHomeImage.setImageResource(R.drawable.ic_home);
                btnHistoryImage.setImageResource(R.drawable.ic_history);
                btnCollectionImage.setImageResource(R.drawable.ic_collection);
                btnProfileImage.setImageResource(R.drawable.ic_profile_clicked);
                btnHomeText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnHistoryText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnCollectionText.setTextColor(ContextCompat.getColor(this, R.color.HintTextBlack));
                btnProfileText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                break;
        }
    }

    @OnClick(R.id.fab_map)
    void onFloatingActionButtionClick(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private void setFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, MainFragment.newInstance(), MainFragment.TAG);
        transaction.commit();
    }

    private void changeFragment(Fragment nextFragment, String nextTag) {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (nextTag.equals(currentFragment.getTag())) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.fragment_container, nextFragment, nextTag);
        transaction.commit();
    }

    private void setFinishToast() {
        finishToast = Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == FINISH_MAIN_ACTIVITY) {
                    finishFlag = false;
                }
            }
        };
    }

    @Override
    public void finish() {
        if (finishFlag == false) {
            finishToast.show();
            handler.sendEmptyMessageDelayed(FINISH_MAIN_ACTIVITY, 2000);
            finishFlag = true;
        } else {
            finishToast.cancel();
            super.finish();
        }
    }
}
