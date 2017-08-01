package com.yunkyun.piececollector.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yunkyun.piececollector.PermissionManager;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.fragment.BadgeFragment;
import com.yunkyun.piececollector.fragment.HistoryFragment;
import com.yunkyun.piececollector.fragment.MainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MainActivity";
    private static final int FINISH_MAIN_ACTIVITY = -1;
    private static final int MAX_FRAGMENT_STACK_SIZE = 2;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.fab_map)
    FloatingActionButton mapButton;

    private FragmentManager fragmentManager;

    private boolean finishFlag;
    private Toast finishToast;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setToolbar();
        setDrawer();
        setFinishToast();
        setFragmentManager();

        PermissionManager.checkPermission(this,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @OnClick({R.id.fab_map, R.id.btn_menu})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_menu:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.fab_map:
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                /*UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Log.d(TAG, "onCompleteLogout");
                    }
                });*/
                break;
            default:
                break;
        }
    }

    private void setFragmentManager() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new MainFragment(), MainFragment.TAG);
        fragmentTransaction.commit();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() == 0) {
                    mapButton.show();
                } else {
                    mapButton.hide();
                }
            }
        });
    }

    private void setDrawer() {
        View header = navigationView.getHeaderView(0);
        CircleImageView profileImage = (CircleImageView) header.findViewById(R.id.iv_profile_image);
        TextView profileNickname = (TextView) header.findViewById(R.id.tv_profile_nickname);
        TextView profileEmail = (TextView) header.findViewById(R.id.tv_profile_email);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String nickname = sharedPreferences.getString("user_nickname", null);
        String email = sharedPreferences.getString("user_email", null);
        String profileImagePath = sharedPreferences.getString("user_profile_image_path", null);

        if(profileImagePath != null) {
            Glide.with(getApplicationContext()).load(profileImagePath).into(profileImage);
        }
        profileNickname.setText(nickname);
        profileEmail.setText(email);

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void changeFragment(int id, String nextTag) {
        String currentTag = fragmentManager.findFragmentById(R.id.fragment_container).getTag();
        if (nextTag.equals(currentTag)) {
            return;
        }

        Fragment nextFragment = null;
        if (id == R.id.nav_history) {
            nextFragment = new HistoryFragment();
        } else if (id == R.id.nav_badge) {
            nextFragment = new BadgeFragment();
        }

        if (fragmentManager.getBackStackEntryCount() < MAX_FRAGMENT_STACK_SIZE) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, nextFragment, nextTag)
                    .addToBackStack(null)
                    .commit();
        } else {
            fragmentManager.popBackStack();
        }
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case R.id.nav_history:
                changeFragment(id, HistoryFragment.TAG);
                break;
            case R.id.nav_badge:
                changeFragment(id, BadgeFragment.TAG);
                break;
            case R.id.nav_setting:
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return false;
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
