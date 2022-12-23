package com.example.st.arcgiscss.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

/**
 * Created by jt on 2017/6/22.
 */

public class IMEIUtil {
    public static volatile String IMEI;
    private static int permission = PackageManager.PERMISSION_DENIED;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static String getIMEI(Activity activity)
    {
        if (IMEI != null && IMEI.length() != 0) return IMEI;
        verifyStoragePermissions(activity);
        if(permission != PackageManager.PERMISSION_GRANTED)
        {
            return null;
        }
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(activity.getApplicationContext().TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId();
        Log.e("TEST", "imei is : " + IMEI);
        return IMEI;
    }
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context activity)
    {
        if (IMEI != null && IMEI.length() != 0) return IMEI;
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId();
        Log.e("TEST", "imei is : " + IMEI);
        return IMEI;
    }
    public static void verifyStoragePermissions(Activity activity) {
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Check if we have write permission
        permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
