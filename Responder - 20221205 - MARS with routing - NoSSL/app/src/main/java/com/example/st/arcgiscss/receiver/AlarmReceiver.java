package com.example.st.arcgiscss.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.st.arcgiscss.service.UploadLocationService2;


public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			Intent i=new Intent(context, UploadLocationService2.class);
			context.stopService(i);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(i);
		} else {
			Log.i("ALARM", "[AlarmReceiver] starting service");
			context.startService(i);
		}

	}
}
