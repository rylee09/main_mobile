package com.example.st.arcgiscss.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.st.arcgiscss.R;

public class TipHelper {

    private static Ringtone mRingtone;
    private static MediaPlayer mp;

//    public static void Vibrate(final Activity activity, long milliseconds) {
//        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
//        vib.vibrate(milliseconds);
//    }

    private static Vibrator vib;
    public synchronized static void Vibrate(final Context activity, long milliseconds) {
        if (vib == null) {
            vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vib.vibrate(500);
        }
//        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        if (!vib.hasVibrator()) {
            vib.vibrate(milliseconds);
        }

    }


    public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

//    public static void playSound(final Activity activity) {
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(activity, notification);
//        r.play();
//    }

    public synchronized static void playSound(Context context) {
        if (mRingtone == null) {
//            Uri no = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            String uri = "android.resource://" + context.getPackageName() + "/" + R.raw.accomplished;
            Uri no = Uri.parse(uri);

            mRingtone = RingtoneManager.getRingtone(context.getApplicationContext(), no);
        }

        if (!mRingtone.isPlaying()) {
            mRingtone.play();
        }
    }




}
