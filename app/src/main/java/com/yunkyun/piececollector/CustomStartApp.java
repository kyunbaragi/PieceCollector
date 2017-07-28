package com.yunkyun.piececollector;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by YunKyun on 2017-07-28.
 */

public class CustomStartApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumBarunGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumBarunGothicBold.ttf"));
    }
}
