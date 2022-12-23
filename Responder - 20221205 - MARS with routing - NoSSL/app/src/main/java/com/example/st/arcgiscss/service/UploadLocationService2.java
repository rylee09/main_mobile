package com.example.st.arcgiscss.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.model.MyPoint;
import com.example.st.arcgiscss.model.UserPosition;
import com.example.st.arcgiscss.receiver.AlarmReceiver;
import com.example.st.arcgiscss.upload.UserCoordinate;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadLocationService2 extends Service {

    private long time = 5*1000;

    private MyApplication application;
    private Gson gson;
    private DateFormat sdf = null;

    //ZN - 20210505
    private AlarmManager am;
    private PendingIntent pi;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(2,new Notification());
        application=MyApplication.getInstance();
        gson = new Gson();
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Service", "[UploadLocationService2] service started");
        System.out.println("Start timing upload");
        String sltime = CacheUtils.getSendLocationTime(MyApplication.getInstance().getApplicationContext())==null?"5":CacheUtils.getSendLocationTime(MyApplication.getInstance().getApplicationContext())+"";
        time = Integer.valueOf(sltime)*1000;

        //ZN - 20201214
//        UserPosition coord = application.getUserPosition();
        UserCoordinate coord = application.getUserCoordinates();
        UserPosition position = new UserPosition();

        //ZN - 20201214 need to figure out purpose of checking for null since there is no reference to the coord object
        if (coord != null){
            MyPoint current =MyApplication.getInstance().getCurrentPoint();
            position.setUserId(CacheUtils.getUserId(getApplicationContext()));

            //ZN - 20201214
            position.setLng(current.getLng());
            position.setLat(current.getLat());
            position.setTimestamp(System.currentTimeMillis()+"");
            position.setActivationLocation(CacheUtils.getActivationLocation(getApplicationContext()));
//            coord.setLongitude(current.getLng());
//            coord.setLatitude(current.getLat());
//            coord.setWrite_time(System.currentTimeMillis()+"");
//            //coord.setActivationLocation("");

            Log.i("Testing", "Current lat lon to upload server: " + current.getLat() + " " + current.getLng());

                Map<String,String> maps = new HashMap<>();
                maps.put("json",gson.toJson(position));
                Call<ResponseBody> call = RetrofitUtils.getInstance().createPositionRecord(maps);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("TEST","success---------update_position");
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        Log.e("TEST","error---------update_position");
                    }
                });
            }

        if(application!=null){

            long triggerTime= SystemClock.elapsedRealtime()+time;
            Intent i=new Intent(this, AlarmReceiver.class);
            am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            pi = PendingIntent.getBroadcast(this, 0, i, 0);
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("Service", "[UploadLocationService2] service stopped");
        am.cancel(pi);
        super.onDestroy();
    }


}

