package com.yunkyun.piececollector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.yunkyun.piececollector.R;
import com.yunkyun.piececollector.call.NetworkService;
import com.yunkyun.piececollector.util.PreferenceKey;
import com.yunkyun.piececollector.util.SharedPreferencesService;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private SessionCallback callback;

    @BindView(R.id.iv_login_logo)
    ImageView appLogo;
    @BindView(R.id.btn_login_kakao)
    ImageView iconKakao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Glide.with(this).load(R.drawable.logo_app).into(appLogo);
        Glide.with(this).load(R.drawable.ic_kakao).into(iconKakao);
    }

    @OnClick({R.id.btn_login_kakao})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_login_kakao:
                openLoginSession();
                break;
        }
    }

    private void openLoginSession() {
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            Session.getCurrentSession().open(AuthType.KAKAO_TALK, LoginActivity.this);
        }
    }

    private void saveUserData() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                // TODO: Handle Request Error.
                Log.d(TAG, "onFailure");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                // TODO: Handle Request Error.
                Log.d(TAG, "onSessionClosed");
            }

            @Override
            public void onNotSignedUp() {
                // TODO: Handle Not Signup state.
                Log.d(TAG, "onNotSignedUp");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                SharedPreferencesService.getInstance().setPrefData(PreferenceKey.USER_ID, userProfile.getId());
                SharedPreferencesService.getInstance().setPrefData(PreferenceKey.USER_EMAIL, userProfile.getEmail());
                SharedPreferencesService.getInstance().setPrefData(PreferenceKey.USER_NICKNAME, userProfile.getNickname());
                SharedPreferencesService.getInstance().setPrefData(PreferenceKey.USER_PROFILE_IMAGE_PATH, userProfile.getProfileImagePath());

                saveUserOnServer(userProfile);
            }
        });
    }

    private void saveUserOnServer(UserProfile userProfile) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("id", String.valueOf(userProfile.getId()));
        parameters.put("email", userProfile.getEmail());

        NetworkService service = NetworkService.retrofit.create(NetworkService.class);
        Call<ResponseBody> call = service.createUser(parameters);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    // TODO: 서버접속 실패 시 처리
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    // 최초 카카오톡 가입화면 후 호출, 오버라이드 하지않으면 로그인 성공 후 다음 화면으로 넘어가지 않음
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            saveUserData();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
