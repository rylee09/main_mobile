package com.example.st.arcgiscss.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class ForceExitReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        final Activity activity = AppManager.getInstance().getTopActivity();
        Log.d("activity", activity + "");
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Warning")
                .setMessage("You have been forced to log out. Please log in again")
                .setCancelable(false)
                .setPositiveButton("Determine", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        AppManager.finishAll();
//                        Intent intent = new Intent(activity, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        activity.startActivity(intent);
                    }
                });
        dialog.create().show();
    }

}
