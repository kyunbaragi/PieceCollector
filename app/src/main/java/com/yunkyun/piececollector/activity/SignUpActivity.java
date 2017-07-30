package com.yunkyun.piececollector.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

/**
 * Created by YunKyun on 2017-07-30.
 */

public class SignUpActivity extends Activity {
    /**
     * MainActivity로의 전환을 결정하는 Activity
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestUserInfo() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    openLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                openLoginActivity();
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                openMainActivity();     // 로그인 성공시 MainActivity로
            }
        });
    }

    private void openMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    protected void openLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
