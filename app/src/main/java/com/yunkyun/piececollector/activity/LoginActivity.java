package com.yunkyun.piececollector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.yunkyun.piececollector.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by YunKyun on 2017-07-27.
 */

public class LoginActivity extends BaseActivity {
    public static final String TAG = "LoginActivity";
    private SessionCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_login_kakao, R.id.btn_login_facebook, R.id.btn_login_naver})
    void onButtonClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_login_kakao:
                openLoginSession();
                break;
            case R.id.btn_login_facebook:
                Toast.makeText(this, "Login with Facebook", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_login_naver:
                Toast.makeText(this, "Login with Naver", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openLoginSession(){
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().open(AuthType.KAKAO_TALK, LoginActivity.this);
    }


    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            requestUserInfo();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.d(TAG, "onSessionOpenFailed");
            }
        }
    }

    private void requestUserInfo() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    // TODO: Redirect current activity.
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                openMainActivity(userProfile);
            }
        });
    }

    private void openMainActivity(UserProfile userProfile) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(TAG, userProfile);
        startActivity(intent);
        finish();
    }

    // 카카오톡 자동 로그인시 호출, 오버라이드 하지않으면 자동 로그인시 로그인 성공 화면으로 넘어가지 않음
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
}
