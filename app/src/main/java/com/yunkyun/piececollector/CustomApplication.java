package com.yunkyun.piececollector;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;
import com.tsengvn.typekit.Typekit;
import com.yunkyun.piececollector.adapter.KakaoSDKAdapter;
import com.yunkyun.piececollector.util.SharedPreferencesService;

/**
 * Created by YunKyun on 2017-07-28.
 */

public class CustomApplication extends Application {
    private static volatile CustomApplication instance;
    private static volatile Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());

        // Typekit Custom Font settings.
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumSquareR.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumSquareB.ttf"))
                .addItalic(Typekit.createFromAsset(this, "fonts/NanumBarunpenR.ttf"));

        SharedPreferencesService.getInstance().load(getApplicationContext());
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        CustomApplication.currentActivity = currentActivity;
    }

    /**
     * singleton 애플리케이션 객체를 얻는다.
     * @return singleton 애플리케이션 객체
     */
    public static CustomApplication getCustomApplicationContext() {
        if(instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
}
