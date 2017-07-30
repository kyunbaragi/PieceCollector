package com.yunkyun.piececollector;

import android.Manifest;
import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

/**
 * Created by YunKyun on 2017-07-30.
 */

public class PermissionManager {
    public static void checkPermission(Context context){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };

        new TedPermission(context)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}
