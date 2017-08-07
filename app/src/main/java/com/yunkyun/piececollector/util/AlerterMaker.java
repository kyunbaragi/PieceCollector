package com.yunkyun.piececollector.util;

import android.app.Activity;

import com.tapadoo.alerter.Alerter;

/**
 * Created by YunKyun on 2017-08-07.
 */

public class AlerterMaker {
    private AlerterMaker() {

    }

    public static void makeLongAlerter(Activity activity, String title, String text, int color) {
        if (activity != null) {
            if (title != null && text != null) {
                Alerter.create(activity)
                        .setDuration(3 * 1000)
                        .enableSwipeToDismiss()
                        .setTitle(title)
                        .setText(text)
                        .setBackgroundColorRes(color)
                        .show();
            }
        }
    }

    public static void makeShortAlerter(Activity activity, String title, String text, int color) {
        if (activity != null) {
            if (title != null && text != null) {
                Alerter.create(activity)
                        .setDuration(1 * 1000)
                        .enableSwipeToDismiss()
                        .setTitle(title)
                        .setText(text)
                        .setBackgroundColorRes(color)
                        .show();
            }
        }
    }
}
