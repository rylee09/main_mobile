package com.example.st.arcgiscss.activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.constant.Constants;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.fragments.HomeFragment;
import com.example.st.arcgiscss.fragments.MoreFragment;
import com.example.st.arcgiscss.fragments.PeerReadinessFragment;
import com.example.st.arcgiscss.fragments.SelectSendOutEvacFragment;
import com.example.st.arcgiscss.fragments.SystemReadinessFragment;
import com.example.st.arcgiscss.fragments.TaskAssignFragment;
import com.example.st.arcgiscss.fragments.TaskRequestFragment;
import com.example.st.arcgiscss.model.FuelTopUp;
import com.example.st.arcgiscss.model.IncidentLoc;
import com.example.st.arcgiscss.model.LocationType;
import com.example.st.arcgiscss.model.MyPoint;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.mqtt.GetNewInfoService;
import com.example.st.arcgiscss.service.LocationService;
import com.example.st.arcgiscss.service.UploadLocationService2;
import com.example.st.arcgiscss.upload.UserCoordinate;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.LocationUtil;
import com.example.st.arcgiscss.util.LogcatHelper;
import com.example.st.arcgiscss.util.MyAndroidUtil;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.example.st.arcgiscss.util.ToastUtil;
import com.example.st.arcgiscss.views.IosAlertDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMainActivity extends BaseActivity {

    @D3View(click = "onClick")
    ImageView iv_task_assigment, iv_fuel, iv_logout;
    @D3View
    TextView tv_fuel, tv_fuel_status;

    @D3View
    TextView tv_home,tv_task_request,tv_task_assign,tv_more;

    @D3View(click = "onClick")
    Button btn_home,btn_task_request,btn_task_assign,btn_more;

    private User user;

    private NewMQTTMsgReceiver2 newMReceiver;

    private static final int READ_PHONE_STATE = 100;

    private boolean isIncidentReceived = false;

    private LocationType locationType;

    private List<String> locations;

    private String n_incident_LUP = "";
    private String n_incident_type = "";
    private String n_incident_location = "";

    NotificationManagerCompat notificationManagerCompat;

    //ZN - 20210417
    private NewFuelTopUpReceiver newFuelTopUpReceiver;

    //ZN - 20210630
    private NewLinkStatusReceiver newLinkStatusReceiver;

    //ZN - 20210719
    private NewBackupEASMsgReceiver newBackupEASMsgReceiver;

    //ZN - 20211201 cancel task assignment
    private NewCancelledIncidentReceiver newCancelledIncidentReceiver;

    //ZN - 20220125 consolidate peer readiness method call
    private NewPeerReadinessMsgReceiver newPeerReadinessMsgReceiver;

    //ZN - 20210505
    private LocationManager lm;
    private MyPoint current;

    //ZN - 20220224 new send-out feature - added new send-out evac fragment

    @D3View
    FrameLayout page1, page2, page3, page4, page5, page6, page7;
    private HomeFragment homeFragment;
    private SystemReadinessFragment systemReadinessFragment;
    private TaskAssignFragment taskAssignFragment;
    private TaskRequestFragment taskRequestFragment;
    private PeerReadinessFragment peerReadinessFragment;

    //ZN - 20220709 added new More fragment
    private MoreFragment moreFragment;

    //ZN - 20220224 new send-out feature - added new send-out evac fragment
    private SelectSendOutEvacFragment selectSendOutEvacFragment;

    private List<FrameLayout> pages = new ArrayList<FrameLayout>();

    //ZN - 20210630
    public static final String SYSTEM_READY = "Y";
    public static final String SYSTEM_NOT_READY = "N";
    public static final String SYSTEM_PENDING = "P";
    private String systemReadyStatus = "";

    //ZN - 20211201 cancel task assignment
    public static final String SYSTEM_CANCELLED = "CANCELLED";

    //ZN - 20210706
    private boolean isAppBackground = false;
    private int link_status_counter = 0;
    public static final int FRAG_HOME = 1;
    public static final int FRAG_SYSTEM_READINESS = 2;
    public static final int FRAG_TASK_REQUEST = 3;
    public static final int FRAG_TASK_ASSIGN = 4;
    public static final int FRAG_MORE = 5;
    public static final int FRAG_PEER_READINESS = 6;

    //ZN - 20220224 new send-out feature - select send-out evac fragment
    public static final int FRAG_SEND_OUT_EVAC = 7;

    //ZN - 20210819
    private NewIncident incident;
    public static final String MAIN_NOT_ACTIVATED = "1";
    public static final String MAIN_ACTIVATED = "2";

    //ZN - 20220720 restore activation
    public static final String MAIN_RESTORE = "3";

    private HashMap<String, String> EASUser_status = new HashMap<String, String>();
    private String activation_flag = NewMainActivity.MAIN_NOT_ACTIVATED;

    //ZN - 20211115 reduce link status loss alert
    long lng_alert_timestamp, lng_current_timestamp, lng_time_diff;
    private String str_previous_link_status = "";

    private boolean isCancelIncidentListenerRegistered;

    private Button openChat;


    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle arg0) {
        Log.i("EVENT", "[NewMainActivity] onCreate");
        super.onCreate(arg0);
        setContentView(R.layout.acti_newhome);

        String username = CacheUtils.getUsername(getApplication());
        String ip = CacheUtils.getIP(getApplication());
        String port = CacheUtils.getPort(getApplication());
        String http = CacheUtils.getProtocol(getApplication());

        System.out.println("Current Username is: " + username);
        System.out.println("Current Ip is: " + ip);
        System.out.println("Current Port is: " + port);
        System.out.println("Current Protocol is: " + http);

        String packageName = "com.heyletscode.chattutorial";

        findViewById(R.id.chatAppSwitch)
                .setOnClickListener(v->{
                    PackageManager manager = getPackageManager();
                    Intent intent = manager.getLaunchIntentForPackage(packageName);
//                    intent.setClassName(packageName,"com.heyletscode.chattutorial.ChatActivity");
                    intent.putExtra("username", username);
                    intent.putExtra("ip", ip);
                    intent.putExtra("port", port);
                    intent.putExtra("protocol", http);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);                });


//        openChat.findViewById(R.id.chatAppSwitch);
//        openChat.setOnClickListener(v->{
//            Intent i = new Intent(Intent.ACTION_MAIN);
//            PackageManager manager = getPackageManager();
//            i = manager.getLaunchIntentForPackage("com.heyletscode.chattutorial");
//            i.addCategory(Intent.CATEGORY_LAUNCHER);
//            startActivity(i);
//
//        });

        if (CacheUtils.getUser(this)!=null){
            user = CacheUtils.getUser(this);
        }

        pages.add(page1);
        pages.add(page2);
        pages.add(page3);
        pages.add(page4);
        pages.add(page5);
        pages.add(page6);

        //ZN - 20220224 new send-out feature - added new send-out evac fragment
        pages.add(page7);

        switchFragment(FRAG_HOME);

        notificationManagerCompat = NotificationManagerCompat.from(this);
        //ZN - 20210706
        createLinkStatusNotificationChannel();
        //ZN - 20210819 create notification channel for incident notification
        createActivationNotificationChannel();

        //ZN - 20211201 cancel task assignment - cancel notification
        createCancellationNotificationChannel();

        if (Build.VERSION.SDK_INT >= 23) {
            showContacts();
            //setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        user = CacheUtils.getUser(this);
        //initView(user);

        //ZN - 20210417 send timestamp only when log in
        doTimestamp(user.getId());

        Intent intent1 = new Intent(this, GetNewInfoService.class);
        startService(intent1);

        newMReceiver = new NewMQTTMsgReceiver2();
        newFuelTopUpReceiver = new NewFuelTopUpReceiver();
        newLinkStatusReceiver = new NewLinkStatusReceiver();
        newBackupEASMsgReceiver = new NewBackupEASMsgReceiver();

        //ZN - 20211201 cancel task assignment
        newCancelledIncidentReceiver = new NewCancelledIncidentReceiver();

        //ZN - 20220125 consolidate peer readiness method call
        newPeerReadinessMsgReceiver = new NewPeerReadinessMsgReceiver();

        Log.i("BROADCAST", "[NewMainActivity] register receiver: NewLinkStatusReceiver");
        registerReceiver(newLinkStatusReceiver, new IntentFilter("NewLinkStatusReceiver"));

        //ZN - 20210719
        Log.i("BROADCAST", "[NewMainActivity] register receiver: NewBackupEASMsgReceiver");
        registerReceiver(newBackupEASMsgReceiver, new IntentFilter("NewBackupEASMsgReceiver"));


        Log.i("BROADCAST", "[NewMainActivity] register receiver: NewPeerReadinessMsgReceiver");
        registerReceiver(newPeerReadinessMsgReceiver, new IntentFilter("NewPeerReadinessMsgReceiver"));


        //ZN - 20210505 start location services once logged in
        startLocationServices();

        //ZN - 20210708
        getSupportFragmentManager().addOnBackStackChangedListener(getListener());

        //ZN - 20210726 system ready by default
        updateSystemReadyStatus(SYSTEM_READY);

        //ZN - 20210113 generate fixed route in event no route solve - warm up and start routing engine
        //startRoutingEngine();

        //ZN - 20220322 smart search feature for medical centre / hospital
        getEvacLocationList();

        //ZN - 20220720 restore activation
        checkAndRestoreActivation();
    }

    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, READ_PHONE_STATE);
        } else {
            Log.e("PERMISSION", "PERMISSION GRANTED!!!!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
/*        switch (requestCode) {
            case READ_PHONE_STATE:
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    startLocation();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }*/
    }

//    private void initView(User user) {
//        if (tv_user_name != null)
//
////        Glide.with(NewMainActivity.this).load(R.mipmap.topupfuel).into(iv_fuel);
////        tv_fuel.setText("Top Up Fuel");
//    }

    public void onClick(View v) {
        Log.i("TEST", "onClick: " + v.getId());
        switch (v.getId()) {
            case R.id.btn_home:
                switchFragment(FRAG_HOME);
                break;
            case R.id.btn_task_request:
                switchFragment(FRAG_TASK_REQUEST);
                break;
            case R.id.btn_more:
                switchFragment(FRAG_MORE);
                break;
            case R.id.btn_task_assign:
                gotoMainActivty(MAIN_NOT_ACTIVATED);
                break;
//            case R.id.iv_fuel:
//                //ZN - 20210416 for fuel topup approval
//                if (tv_fuel_status.getText().equals("")){
//                    //start state
//                    Glide.with(NewMainActivity.this).load(R.mipmap.topupfuel_amber).into(iv_fuel);
//                    tv_fuel.setText("");
//                    tv_fuel_status.setText("Pending");
//                    //disable button press
//                    iv_fuel.setEnabled(false);
//                    callServerTopupfuel("P");
//                    //register for fuel top up status listener
//                    registerReceiver(newFuelTopUpReceiver, new IntentFilter("NewFuelTopUpMsgReceiver"));
//                } else if (tv_fuel_status.getText().equals("Approved")) {
//                    Glide.with(NewMainActivity.this).load(R.mipmap.topupfuel).into(iv_fuel);
//                    tv_fuel.setText("Top Up Fuel");
//                    tv_fuel_status.setText("");
//                    //change back to N once user have completed approved top up
//                    callServerTopupfuel("N");
//                    //unregister for fuel top up status listener
//                    unregisterReceiver(newFuelTopUpReceiver);
//                } else if (tv_fuel_status.getText().equals("Rejected")) {
//                    Glide.with(NewMainActivity.this).load(R.mipmap.topupfuel).into(iv_fuel);
//                    tv_fuel.setText("Top Up Fuel");
//                    tv_fuel_status.setText("");
//                    //callServerTopupfuel("N");
//                    //unregister for fuel top up status listener
//                    unregisterReceiver(newFuelTopUpReceiver);
//                } else {
//                    //do nothing
//                }
//                break;
//            case R.id.iv_logout:
//                doLogout();
//                break;
        }

    }

    @Override
    protected void onPause() {
        Log.i("EVENT","[NewMainActivity] onPause");
        isAppBackground = true;

        //Log.i("BROADCAST", "[NewMainActivity] unregister receiver: NewMQTTMsgReceiver");
        //unregisterReceiver(newMReceiver);

        super.onPause();
    }
/*
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("INCIDENT","VIEW RESTART ON STACK AGAIN!!!!");

        if(isIncidentReceived == true) {
            Log.e("INCIDENT","RESTART VIEW AGAIN!!!!");

            Intent intent = new Intent(NewMainActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }*/

    @Override
    protected void onResume() {
        Log.i("EVENT","[NewMainActivity] onResume");
        isAppBackground = true;
        super.onResume();

        //ZN - 20210420
        Log.i("BROADCAST", "[NewMainActivity] register receiver: NewMQTTMsgReceiver");
        registerReceiver(newMReceiver, new IntentFilter("NewMQTTMsgReceiver"));

        //unregisterReceiver(newMReceiver);

//        if(isIncidentReceived == true) {
//            gotoMainActivty();
//        }

//        //ZN - 20210819 broadcast listener for activation status from MainActivity
//        if(getIntent().getExtras() != null) {
//
//                Log.i("INTENT", "[NewMainActivity] onResume hasExtras");
//                String temp_activation_flag = getIntent().getExtras().getString("flag");
//                Log.i("ACTIVATION_FLAG", "Temp flag: " + temp_activation_flag + " Real flag: " + activation_flag);
//
//                //only change status if incident completed
//                if (temp_activation_flag != null && !temp_activation_flag.equalsIgnoreCase(activation_flag)) {
////                    Intent fragIntent = new Intent("NewActivationStatusMsgReceiver");
////                    fragIntent.putExtra("activation_status", new Boolean(false));
//                    activation_flag = MAIN_NOT_ACTIVATED;
//                    updateSystemReadyStatus(activation_flag);
//                }
//
//        }

        //ZN - 20210819 enable bottom menu buttons (except HOME) only if currently no incident
        if(MyApplication.getInstance().getCurrentIncidentID() == null) {
            btn_task_request.setEnabled(true);
            btn_more.setEnabled(true);
            //btn_task_assign.setEnabled(true);

            unregisterCancelledIncidentListener();
        }

//        //we should always enable this button to true when incident has been ack or incident is complete
        if (MyApplication.getInstance().isIncidentAck() || MyApplication.getInstance().getCurrentIncidentID() == null)
            btn_task_assign.setEnabled(true);

    }

    private void getIncidentLocationList() {

        Call<JsonObject> call = RetrofitUtils.getInstance().getIncidentLocationList();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                locationType = new LocationType(jsonObject);
                locations = new ArrayList<>();
                for (int i = 0; i < locationType.getRespMsg().size(); i++) {
                    IncidentLoc incidentLoc = locationType.getRespMsg().get(i);
                    locations.add(incidentLoc.getActivationLocation());
                }
                locations = getRemoveList(locations);
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ToastUtil.showToast(NewMainActivity.this,"Please check the registration plate number！");
            }
        });

    }

    private static List<String> getRemoveList(List<String> list){
        Set set = new HashSet();
        List<String> newList = new ArrayList<>();
        for (Iterator iter = list.iterator(); iter.hasNext();){
            String object = (String) iter.next();
            if(set.add(object))
                newList.add(object);
        }
        return newList;
    }

    public void gotoMainActivty(String flag) {
   //     CacheUtils.saveUserId(NewMainActivity.this,"1");
   //     CacheUtils.saveActivationLocation(NewMainActivity.this,"Locaiton1");
   //     NewIncidentDBHelper.getInstance(getApplicationContext()).clear();
//        for (int i = 0; i < userType.getIncidentList().size(); i++) {
//            NewIncident newIncident = userType.getIncidentList().get(i);
//            NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(newIncident);
//        }
      //  LocationType locationType = new LocationType();
//        Log.i("NOTIFICATION", "(gotoMainActivity) inside");
//        Log.i("BROADCAST", "[NewMainActivity] unregister receiver: NewMQTTMsgReceiver");
//        unregisterReceiver(newMReceiver);

        Log.i("INCIDENT", "[gotoMainActivty] go to MainActivity");

        getIncidentLocationList();

        Intent intent = new Intent(NewMainActivity.this, MainActivity.class);
        intent.putExtra("locations",locationType);

        //ZN - 20210819 added flag Extra to differentiate NORMAL and INCIDENT for MainActivity
        intent.putExtra("flag", flag);
        if (flag.equalsIgnoreCase(NewMainActivity.MAIN_ACTIVATED) || flag.equalsIgnoreCase(NewMainActivity.MAIN_RESTORE)) {
            intent.putExtra("incident", incident);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    public void callServerTopupfuel(String status) {
        Map<String,String> maps = new HashMap<>();
        maps.put("status", status);
        //maps.put("userId", CacheUtils.getUserId(this));
        maps.put("userId", CacheUtils.getUser(this).getId());
        maps.put("username",CacheUtils.getUser(this).getUsername());
        maps.put("fullusername", CacheUtils.getUser(this).getFullusername());

        Call<JsonObject> call = RetrofitUtils.getInstance().processFuelTopUpRequest(maps);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code") == 0) {
                        String resp_msg = jsonObject.getString("resp_msg");
                        showToast(resp_msg);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.e("TEST","error---------topupfuel");
            }
        });
    }

    //ZN - 20210627 
    public void callServerSystemReady(String status, String remarks) {
        Map<String,String> maps = new HashMap<>();
        maps.put("status", status);
        maps.put("remarks", remarks);
        //maps.put("userId", CacheUtils.getUserId(this));
        maps.put("userId", CacheUtils.getUser(this).getId());
        maps.put("username",CacheUtils.getUser(this).getUsername());
        maps.put("fullusername", CacheUtils.getUser(this).getFullusername());

        Call<JsonObject> call = RetrofitUtils.getInstance().processSystemReady(maps);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code") == 0) {
                        String resp_msg = jsonObject.getString("resp_msg");
                        showToast(resp_msg);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.e("TEST","error---------systemReady");
            }
        });

    }

    PendingIntent pendingActivationIntent, pendingOwnIntent;
    NotificationCompat.Builder builder;

    //ZN - 20210420
    Intent intent_mainActivity;

//    //ZN - 20210420 for incident notification
//    private void createNotificationAndIntent() {
//        Log.i("NOTIFICATION", "(createNotificationAndIntent) inside");
//        intent_mainActivity = new Intent(NewMainActivity.this, MainActivity.class);
//        intent_mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent_mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
//    }

    //ZN - 20210420 for link status notification
    private void createLinkStatusNotificationAndIntent() {
        Log.i("NOTIFICATION", "(createOwnNotificationAndIntent) inside");
        Intent intent_newMainActivity = new Intent(NewMainActivity.this, NewMainActivity.class);
//        intent_newMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent_newMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        pendingOwnIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent_newMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        //ZN - 20210706
        builder  = new NotificationCompat.Builder(this, "LinkStatus")
                .setSmallIcon(R.mipmap.red_cross)
                .setContentTitle("Link Status Down")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Link Status Down"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingOwnIntent, true)
                .setAutoCancel(true);

        //ZN - 20211115 reduce link status loss alert
        //refresh timestamp of alert
        lng_alert_timestamp = new Date().getTime();
    }

    //ZN - 20210706
    private void createLinkStatusNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sample";
            String description = "Testing Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("LinkStatus", name, importance);
            channel.setDescription(description);

            //ZN - 20210707 set notification settings in channel instead
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.accomplished);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setDescription("Link Status Down");
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(soundUri, attributes);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //ZN - 20210819 for incident activation notification
    private void createActivationNotificationAndIntent() {
        n_incident_location = "Location: " + incident.getLatLon();
        n_incident_type = "Type: " + incident.getType();
        n_incident_LUP = "Collection Point: " + incident.getActivationLocation();

        Log.i("NOTIFICATION", "[createActivationNotificationAndIntent] inside");
        Intent intent_newMainActivity = new Intent(NewMainActivity.this, NewMainActivity.class);
        intent_newMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //intent_newMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingActivationIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent_newMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        builder  = new NotificationCompat.Builder(this, "Incident")
                .setSmallIcon(R.mipmap.red_cross)
                .setContentTitle("New Incident")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(n_incident_LUP + "\n" + n_incident_type + "\n" + n_incident_location))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingActivationIntent, true)
                .setAutoCancel(true);
    }

    //ZN - 20210819 notification channel for incident activation
    private void createActivationNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sample";
            String description = "Testing Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel activation_channel = new NotificationChannel("Incident", name, importance);
            activation_channel.setDescription(description);

            //ZN - 20210707 set notification settings in channel instead
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.siren);
//            String str_env = Environment.getExternalStorageDirectory().getAbsolutePath();
//            String str_arcgis = this.getResources().getString(R.string.config_data_sdcard_offline_dir);
//            String dir = str_env+File.separator+str_arcgis;
//            Uri soundUri = Uri.fromFile(new File(dir,"siren.mp3"));
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            activation_channel.setDescription("Incident Received");
            activation_channel.enableLights(true);
            activation_channel.enableVibration(true);
            activation_channel.setSound(soundUri, attributes);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(activation_channel);
        }
    }

    //ZN - 20211201 cancel task assignment - create notification
    private void createCancellationNotificationAndIntent() {
        Log.i("NOTIFICATION", "[createCancellationNotificationAndIntent] inside");
        Intent intent_newMainActivity = new Intent(NewMainActivity.this, NewMainActivity.class);
        intent_newMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //intent_newMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingActivationIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent_newMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        builder  = new NotificationCompat.Builder(this, "Cancellation")
                .setSmallIcon(R.mipmap.red_cross)
                .setContentTitle("Incident Cancelled")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Incident Cancelled"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(pendingActivationIntent, true)
                .setAutoCancel(true);
    }

    //ZN - 20211201 cancel task assignment - create notification
    private void createCancellationNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sample";
            String description = "Testing Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel cancel_channel = new NotificationChannel("Cancellation", name, importance);
            cancel_channel.setDescription(description);

            //ZN - 20210707 set notification settings in channel instead
//            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.cancelled);
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/cancelled");
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            cancel_channel.setDescription("Incident Cancelled");
            cancel_channel.enableLights(true);
            cancel_channel.enableVibration(true);
            cancel_channel.setSound(soundUri, attributes);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(cancel_channel);

        }
    }

    public class NewMQTTMsgReceiver2 extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "[NewMQTTMsgReceiver2] onReceive");
            if (intent.getAction().equals("NewMQTTMsgReceiver") && !MyApplication.getInstance().isRcvIncidentNotification()) {
                Log.i("ERS_EVENT", "[NewMQTTMsgReceiver2] incident notification received");

                //ZN - 20210819 set flag to indicate incident notification rcv
                MyApplication.getInstance().setRcvIncidentNotification(true);

                //ZN - 20210819 include incident object

                incident = (NewIncident) intent.getExtras().getSerializable("incident");
                //ZN - 20210819 set incident id once rcv incident
                MyApplication.getInstance().setCurrentIncidentID(incident.getId());
                Log.i("INCIDENT", "incident id: " + incident.getId());

                //ZN - 20210726
                Intent fragIntent = new Intent("NewActivationStatusMsgReceiver");
                fragIntent.putExtra("activation_status", new Boolean(true));
                fragIntent.putExtra("incident", incident);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);
                //gotoMainActivty();
                //activation_flag = MAIN_ACTIVATED;

                //display notification
                createActivationNotificationAndIntent();
                Notification n = builder.build();
                n.flags |= Notification.FLAG_INSISTENT;
                notificationManagerCompat.notify(100, n);

                //disable bottom menu buttons except HOME
                btn_task_request.setEnabled(false);
                btn_more.setEnabled(false);
                btn_task_assign.setEnabled(false);

                //ZN - 20211201 cancel task assignment - register listener for cancellation
                registerCancelledIncidentListener();

                //ZN - 20220606 store and forward
                //clear hashmap before put in new incident details
                MyApplication.getInstance().getActivationTimingsMap().clear();
                MyApplication.getInstance().getActivationTimingsMap().put("incident_id", incident.getId());
                MyApplication.getInstance().getActivationTimingsMap().put("user_id", user.getId());

                //set system ready status
                //updateSystemReadyStatus(NewMainActivity.MAIN_ACTIVATED);

                //switch to HOME fragment
                //switchFragment(FRAG_HOME);
            }
        }
    }

    //ZN - 20210417
    public class NewFuelTopUpReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "[NewFuelTopUpReceiver] onReceive");
            if (intent.getAction().equals("NewFuelTopUpMsgReceiver")) {
                if (intent.getExtras() != null) {

                    FuelTopUp status = (FuelTopUp) intent.getExtras().getSerializable("fuel_top_up_status");
                    //updateFuelTopUpApproval(status);
                    //ZN - 20210707
                    Intent fragIntent = new Intent("NewFuelTopUpMsgReceiver");
                    fragIntent.putExtra("top_up_fuel_status", status);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);

                    //ZN - 20220619 logging to external file
                    Log.i("ERS_EVENT", "[NewFuelTopUpReceiver] fuel top up notifcation received: " + status);
                }
            }
        }
    }

    //ZN - 20210630
    public class NewLinkStatusReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "[NewLinkStatusReceiver] onReceive");
            if (intent.getAction().equals("NewLinkStatusReceiver")) {
                if (intent.getExtras() != null) {
                    String status = (String) intent.getExtras().getSerializable("link_status");
                    Intent fragIntent = new Intent("NewLinkStatusReceiver");
                    fragIntent.putExtra("link_status", status);

                    //ZN - 20210819 update timestamp on display
                    String timestamp = (String) intent.getExtras().getSerializable("timestamp");
                    fragIntent.putExtra("timestamp", timestamp);

                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);

                    Log.i("LINK STATUS", "Status: " + status);

                    if (status.equalsIgnoreCase("Offline")) {
                        //offline

//                        //ZN - 20210706 check if app is in background , then bring app to foreground
//                        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.CREATED) {
//                            Log.i("onReceive", "[NewLinkStatusReceiver] bring app to foreground");
//                            Intent intentMain = new Intent(context, NewMainActivity.class);
////                            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            intentMain.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                            startActivity(intentMain);
//                        }

                        //ZN - 20211115 reduce link status loss alert
                        //only trigger notification after certain time has passed

//                        //ZN - 20210706 notification
//                        createLinkStatusNotificationAndIntent();
//                        Notification n = builder.build();
//                        n.flags |= Notification.FLAG_INSISTENT;
//                        notificationManagerCompat.notify(101, n);

                        lng_current_timestamp = new Date().getTime();
                        lng_time_diff = lng_current_timestamp - lng_alert_timestamp;
                        if (lng_time_diff > 60000*10) {
                            //ZN - 20210706 notification after 5mins
                            createLinkStatusNotificationAndIntent();
                            Notification n = builder.build();
                            n.flags |= Notification.FLAG_INSISTENT;
                            notificationManagerCompat.notify(101, n);
                        } else {
                               //ignore and wait for next notification
                        }

                        //ZN - 20220619 logging to external file
                        //only log if there is change to link status status
                        if (str_previous_link_status.equalsIgnoreCase("Online")) {
                            Log.i("ERS_EVENT", "[NewLinkStatusReceiver] Link status offline");
                        }

                        str_previous_link_status = "Offline";
                    } else {
                        //online
                        notificationManagerCompat.cancel(101);

                        //ZN - 20220619 logging to external file
                        //only log if there is change to link status status
                        if (str_previous_link_status.equalsIgnoreCase("Offline")) {
                            Log.i("ERS_EVENT", "[NewLinkStatusReceiver] Link status online");

                            //on link status resume, update activation timings during this period if have timing records in hashmap while link status was down
                            //first two entries is user id and incident id
                            if (MyApplication.getInstance().getActivationTimingsMap().size() > 2) {
                                updateActivationTiming();
                                Log.i("INCIDENT", "[NewLinkStatusReceiver] updated cached activation timings");
                            }
                        }

//                        //ZN - 20220606 store and forward
//                        //on link status resume, update activation timings during this period if have timing records in hashmap while link status was down
//                        //first two entries is user id and incident id
//                        if (MyApplication.getInstance().getActivationTimingsMap().size() > 2 && str_previous_link_status.equalsIgnoreCase("Offline")) {
//                            updateActivationTiming();
//                        }
                        
                        str_previous_link_status = "Online";

                    }
                }
            }
        }
    }

    //ZN - 20210719
    public class NewBackupEASMsgReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "[NewBackupEASMsgReceiver] onReceive");
            if (intent.getAction().equals("NewBackupEASMsgReceiver")) {
                if (intent.getExtras() != null) {
                    //ZN - 20220619 logging to external file
                    Log.i("ERS_EVENT", "[NewBackupEASMsgReceiver] backup EAS notifcation received");

                    Boolean status = (Boolean) intent.getExtras().getSerializable("backupEAS_status");
                    Intent fragIntent = new Intent("NewBackupEASMsgReceiver");
                    fragIntent.putExtra("backupEAS_status", status);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);

                }
            }
        }
    }

    //ZN - 20220125 consolidate peer readiness method call
    public class NewPeerReadinessMsgReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "[NewPeerReadinessMsgReceiver] onReceive");
            if (intent.getAction().equals("NewPeerReadinessMsgReceiver")) {
                if (intent.getExtras() != null) {
                    //ZN - 20220619 logging to external file
                    //Log.i("ERS_EVENT", "[NewPeerReadinessMsgReceiver] peer readiness notifcation received");

                    HashMap<String, String> data = (HashMap<String, String>) intent.getExtras().getSerializable("data");
                    Intent fragIntent = new Intent("NewPeerReadinessMsgReceiver");
                    fragIntent.putExtra("data", data);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);

                }
            }
        }
    }

    //ZN - 20211201 cancel task assignment
    public class NewCancelledIncidentReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "[NewCancelledIncidentReceiver] onReceive");
            if (intent.getAction().equals("NewCancelledIncidentReceiver")) {
                if (intent.getExtras() != null) {

                    //ZN - 20220619 logging to external file
                    Log.i("ERS_EVENT", "[NewCancelledIncidentReceiver] cancel incident notification received");

                    String status = (String) intent.getExtras().getSerializable("cancelled_incident");
                    Intent fragIntent = new Intent("NewCancelledIncidentReceiver");
                    fragIntent.putExtra("cancelled_incident", status);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);

                    //ZN - 20211201 cancel task assignment
                    MyApplication.getInstance().setRcvIncidentNotification(false);
                    unregisterCancelledIncidentListener();

                    //ZN - 20211201 cancel task assignment - cancel notification
                    createCancellationNotificationAndIntent();
                    Notification n = builder.build();
                    n.flags |= Notification.FLAG_INSISTENT;
                    notificationManagerCompat.notify(102, n);

                    //ZN - 20220703 bug fix to enable buttons after incident cancelled
                    btn_task_request.setEnabled(true);
                    btn_more.setEnabled(true);
                    btn_task_assign.setEnabled(true);

                    //ZN - 20220720 restore activation - clear activation log
                    LogcatHelper.clearActivationLog();
                }
            }
        }
    }

//    public class NewSystemReadyStatusReceiver extends BroadcastReceiver {
//        @RequiresApi(api = Build.VERSION_CODES.M)
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.i("onReceive", "[NewSystemReadyStatusReceiver] onReceive");
//            if (intent.getAction().equals("NewSystemReadyStatusReceiver")) {
//                if (intent.getExtras() != null) {
//                    Intent fragIntent = new Intent("NewSystemReadyStatusReceiver");
//                    fragIntent.putExtra("system_ready_status", status);
//                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);
//                }
//            }
//        }
//    }

    //ZN - 20210707
    public void registerTopUpFuelListener() {
        registerReceiver(newFuelTopUpReceiver, new IntentFilter("NewFuelTopUpMsgReceiver"));
    }

    //ZN - 20210707
    public void unregisterTopUpFuelListener() {
        unregisterReceiver(newFuelTopUpReceiver);
    }

    //ZN - 20211201 cancel task assignment
    public void registerCancelledIncidentListener() {
        registerReceiver(newCancelledIncidentReceiver, new IntentFilter("NewCancelledIncidentReceiver"));
        isCancelIncidentListenerRegistered = true;
    }

    //ZN - 20211201 cancel task assignment
    public void unregisterCancelledIncidentListener() {
        if (isCancelIncidentListenerRegistered)
            unregisterReceiver(newCancelledIncidentReceiver);

        isCancelIncidentListenerRegistered = false;
    }

    public void doLogout() {
        MyAndroidUtil.editXml(Constants.USER_CHECK, false);
        MyAndroidUtil.removeXml(Constants.LOGIN_USER);
        MyAndroidUtil.removeXml(Constants.LOGIN_PWD);

        //ZN - 20210503 send logout request to server to set timestamp to null
        Map<String,String> maps = new HashMap<>();
        maps.put("userId", CacheUtils.getUser(this).getId());

        Call<JsonObject> call = RetrofitUtils.getInstance().driverLogout(maps);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code") == 1) {
                        showToast("Logged out successfully");

                        //ZN - 20220619 logging to external file
                        Log.i("EVENT", "[doLogout] application log out from server success");

                        return;
                    } else {
                        //ZN - 20220619 logging to external file
                        Log.i("EVENT", "[doLogout] application log out from server failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    //ZN - 20220619 logging to external file
                    Log.i("EVENT", "[doLogout] application log out failed");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.e("TEST","error---------driverLogout");
            }
        });

        //ZN - 20210505 stop location services , unregister incident listener once logged out
        stopLocationServices();
        stopService(new Intent(this, GetNewInfoService.class));

        Intent intent = new Intent(NewMainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        //finish();

        ActivityCompat.finishAffinity(this);
    }

    //ZN - 20201213
    //method to send timestamp to server
    private void doTimestamp(String user_id){
        Map<String,String> params = new HashMap<>();
        params.put("user_id",user_id);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String now = sdf.format(new Date());
        params.put("timestamp",now);

        Call<JsonObject> call = RetrofitUtils.getInstance().setTimestamp(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code")==-1){
                        showToast("Send Timestamp failed");
                    } else {
                        showToast("Send Timestamp ok");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
            }
        });
    }

//    //ZN - 20210417
//    private void updateFuelTopUpApproval(FuelTopUp status) {
//        if (status.getApprovalStatus().equals(SYSTEM_PENDING)) {
//            //pending HQ response, do nothing
//            return;
//        } else if (status.getApprovalStatus().equals("Y")) {
//            //approved
//            Glide.with(NewMainActivity.this).load(R.mipmap.topupfuel_green).into(iv_fuel);
//            tv_fuel.setText("Tap When Refuelling Done");
//            tv_fuel_status.setText("Approved");
//            //enable button press
//            iv_fuel.setEnabled(true);
//        } else {
//            //rejected
//            Glide.with(NewMainActivity.this).load(R.mipmap.topupfuel_red).into(iv_fuel);
//            tv_fuel.setText("Tap To Go Back");
//            tv_fuel_status.setText("Rejected");
//            //enable button press
//            iv_fuel.setEnabled(true);
//        }
//    }

    public void updateSystemReadyStatus(String status) {
        systemReadyStatus = status;
        user.setSystemReady(status);
    }

    public String getSystemReadyStatus() {
        return systemReadyStatus;
    }

    //ZN - 20210505
    private void startLocationServices() {
        Log.i("Loc","[NewMainActivity] Location services started");
        UserCoordinate userCoordinate = new UserCoordinate();
        userCoordinate.setUserId(CacheUtils.getUserId(this));
        MyApplication.getInstance().setUserCoordinates(userCoordinate);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //default lat lon
        double latitude = 1.3434836900969456;
        double longitude = 103.85739813145422;

        startService(new Intent(this, LocationService.class));

        if (current == null) {
            if (LocationUtil.getBestLocation(lm, this) != null) {
                Location location = LocationUtil.getBestLocation(lm, this);
                Point point = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
                current = new MyPoint(point.getY() + "", point.getX() + "");
            } else {
                current = new MyPoint(latitude + "", longitude + "");
            }
        }

        MyApplication.getInstance().setCurrentPoint(current);
        MyApplication.getInstance().getUserCoordinates().setLatitude(current.getLat());
        MyApplication.getInstance().getUserCoordinates().setLongitude(current.getLng());

        startService(new Intent(this, UploadLocationService2.class));
    }

    //ZN - 20210505
    private void stopLocationServices() {
        Log.i("Loc","[NewMainActivity] Location services stopped");
        stopService(new Intent(this, LocationService.class));
        stopService(new Intent(this, UploadLocationService2.class));
    }

    public void switchFragment(int tabIndex) {
        page1.setVisibility(View.GONE);
        page2.setVisibility(View.GONE);
        page3.setVisibility(View.GONE);
        page4.setVisibility(View.GONE);
        page5.setVisibility(View.GONE);
        page6.setVisibility(View.GONE);

        //ZN - 20220224 new send-out feature - added new send-out evac fragment
        page7.setVisibility(View.GONE);

        Drawable drawable1 = getResources().getDrawable(R.mipmap.home);
        btn_home.setBackground(drawable1);
        tv_home.setTextColor(Color.parseColor("#80ffff"));

        Drawable drawable2 = getResources().getDrawable(R.mipmap.requst);
        btn_task_request.setBackground(drawable2);
        tv_task_request.setTextColor(Color.parseColor("#80ffff"));

        Drawable drawable3 = getResources().getDrawable(R.mipmap.task_assign_1);
        btn_task_assign.setBackground(drawable3);
        tv_task_assign.setTextColor(Color.parseColor("#80ffff"));

        Drawable drawable4 = getResources().getDrawable(R.mipmap.more);
        btn_more.setBackground(drawable4);
        tv_more.setTextColor(Color.parseColor("#80ffff"));

        FragmentTransaction ft;

        switch (tabIndex) {
            case FRAG_HOME:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(pages.get(tabIndex-1).getId(), homeFragment).commit();
//                    ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(pages.get(tabIndex-1).getId(), homeFragment);
//                    ft.addToBackStack(null);
//                    ft.commit();
                }

                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(pages.get(tabIndex-1).getId(), homeFragment);
                ft.addToBackStack(null);
                ft.commit();

//                page1.setVisibility(View.VISIBLE);
//                Drawable drawable5 = getResources().getDrawable(R.mipmap.home_select);
//                btn_home.setBackground(drawable5);
//                tv_home.setTextColor(Color.parseColor("#7cb756"));
//                break;

                page1.setVisibility(View.VISIBLE);
                Drawable drawable5 = getResources().getDrawable(R.mipmap.home);
                btn_home.setBackground(drawable5);
                tv_home.setTextColor(Color.parseColor("#80ffff"));
                break;
            case FRAG_SYSTEM_READINESS:
                if (systemReadinessFragment == null) {
                    systemReadinessFragment = new SystemReadinessFragment();
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(pages.get(tabIndex-1).getId(), systemReadinessFragment).commit();
//                    ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(pages.get(tabIndex-1).getId(), systemReadinessFragment);
//                    ft.addToBackStack(null);
//                    ft.commit();
                }

                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(pages.get(tabIndex-1).getId(), systemReadinessFragment);
                ft.addToBackStack(null);
                ft.commit();

                page2.setVisibility(View.VISIBLE);

//                page1.setVisibility(View.GONE);
//                page3.setVisibility(View.GONE);

                break;
            case FRAG_TASK_REQUEST:
                if (taskRequestFragment == null) {
                    taskRequestFragment = new TaskRequestFragment();
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(pages.get(tabIndex-1).getId(), homeFragment).commit();
//                    ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(pages.get(tabIndex-1).getId(), taskRequestFragment);
//                    ft.addToBackStack(null);
//                    ft.commit();
                }

                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(pages.get(tabIndex-1).getId(), taskRequestFragment);
                ft.addToBackStack(null);
                ft.commit();

                page3.setVisibility(View.VISIBLE);
//                Drawable drawable6 = getResources().getDrawable(R.mipmap.taskrequest_select);
//                btn_task_request.setBackground(drawable6);
//                tv_task_request.setTextColor(Color.parseColor("#7cb756"));
//                break;
                Drawable drawable6 = getResources().getDrawable(R.mipmap.requst);
                btn_task_request.setBackground(drawable6);
                tv_task_request.setTextColor(Color.parseColor("#80ffff"));
                break;
            case FRAG_TASK_ASSIGN:
//                if (taskAssignFragment == null) {
//                    taskAssignFragment = new TaskAssignFragment();
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(pages.get(tabIndex-1).getId(), taskAssignFragment).commit();
//                }
//                page4.setVisibility(View.VISIBLE);
//                Drawable drawable7 = getResources().getDrawable(R.mipmap.taskassign_select);
//                btn_task_assign.setBackground(drawable7);
//                tv_task_assign.setTextColor(Color.parseColor("#7cb756"));
//                break;

//                Drawable drawable7 = getResources().getDrawable(R.mipmap.task_assign);
//                btn_task_assign.setBackground(drawable7);
//                tv_task_assign.setTextColor(Color.parseColor("#80ffff"));
//                break;

            case FRAG_MORE:
                //ZN - 20220709 added new More fragment
                if (moreFragment == null) {
                    moreFragment = new MoreFragment();
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(pages.get(tabIndex-1).getId(), homeFragment).commit();
//                    ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(pages.get(tabIndex-1).getId(), taskRequestFragment);
//                    ft.addToBackStack(null);
//                    ft.commit();
                }

                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(pages.get(tabIndex-1).getId(), moreFragment);
                ft.addToBackStack(null);
                ft.commit();

                page5.setVisibility(View.VISIBLE);
                Drawable drawable8 = getResources().getDrawable(R.mipmap.more);
                btn_more.setBackground(drawable8);
                tv_more.setTextColor(Color.parseColor("#80ffff"));
                break;

            case FRAG_PEER_READINESS:
                if (peerReadinessFragment == null) {
                    peerReadinessFragment = new PeerReadinessFragment();
                }

                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(pages.get(tabIndex-1).getId(), peerReadinessFragment);
                ft.addToBackStack(null);
                ft.commit();

                page6.setVisibility(View.VISIBLE);

                //ZN - 20211124 get camp responder peers - to trigger update method whenever fragment is active
                peerReadinessFragment.updatePeerStatus();
                break;

            //ZN - 20220224 new send-out feature - added new send-out evac fragment
            case FRAG_SEND_OUT_EVAC:
                if (selectSendOutEvacFragment == null) {
                    selectSendOutEvacFragment = new SelectSendOutEvacFragment();
                }

                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(pages.get(tabIndex-1).getId(), selectSendOutEvacFragment);
                ft.addToBackStack(null);
                ft.commit();

                page7.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    //ZN - 20210708
    private FragmentManager.OnBackStackChangedListener getListener()
    {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
        {
            @SuppressLint("RestrictedApi")
            public void onBackStackChanged()
            {
                Log.i("Event", "[NewMainActivity] onBackStackChanged");
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.page1);
                if (currentFragment instanceof HomeFragment && currentFragment.isVisible()) {
                    Log.i("EVENT", "is currentFragment");
                    currentFragment.onResume();
                }
            }
        };

        return result;
    }

    public void showExitView(){

        new IosAlertDialog(this).builder().setTitle("Logout").setMsg("Are you sure you want to logout ?").setCancelable(false).setNegativeButton("No", null).setPositiveButton("YES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyApplication.getInstance().loginAgain("");
                doLogout();
            }
        }).show();
    }

    //ZN - 20210819 method to retrieve EAS user status (standby / activated / offline)
//    public void getEASUserStatusFromServer() {
//
//        Call<ArrayList<UserStatus>> call = RetrofitUtils.getInstance().getEASUserStatus();
//        call.enqueue(new Callback<ArrayList<UserStatus>>() {
//            @Override
//            public void onResponse(Call<ArrayList<UserStatus>> call, Response<ArrayList<UserStatus>> response) {
//                JSONObject jsonObject = null;
//                UserStatus userStatus = null;
//                ArrayList<UserStatus> jsonArray = null;
//
//                jsonArray = response.body();
//                for (int i=0; i<jsonArray.size(); i++) {
//                    userStatus = jsonArray.get(i);
//                    EASUser_status.put(userStatus.getUsername(), userStatus.getStatus());
//                }
//            }
//            @Override
//            public void onFailure(Call<ArrayList<UserStatus>> call, Throwable t) {
//                ToastUtil.showToast(NewMainActivity.this,"EAS user status not available");
//            }
//        });
//
//    }

    //ZN - 20210819 for peer readiness fragment
    public HashMap<String, String> getEASUserStatus() {
        return EASUser_status;
    }

    @Override
    public void onBackPressed() {
        Log.i("BACK", "back button pressed");
        showExitView();
    }

    @Override
    protected void onDestroy() {
        Log.i("EVENT","[NewMainActivity] onDestroy");

        Log.i("BROADCAST", "[NewMainActivity] unregister receiver: NewMQTTMsgReceiver");
        unregisterReceiver(newMReceiver);

        //ZN - 20211115 unregister link status and backup EAS status listener
        unregisterReceiver(newBackupEASMsgReceiver);
        unregisterReceiver(newLinkStatusReceiver);

        //ZN - 20220125 consolidate peer readiness method call
        unregisterReceiver(newPeerReadinessMsgReceiver);

        System.out.println("Cleared cache info");
        CacheUtils.clearCacheInfo(getApplication());
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("EVENT", "[NewMainActivity] onNewIntent");
        if (intent.hasExtra("flag")) {
            String temp_activation_flag = intent.getExtras().getString("flag");
            Log.i("ACTIVATION_FLAG", "flag: " + temp_activation_flag);
        }
    }

    //ZN - 20210922 method to check for incomplete incident
    public void getIncompleteIncidentStatus() {
        Map<String,String> params = new HashMap<>();
//        params.put("user_id",CacheUtils.getUserId(this));
        params.put("user_id","42");
        Call<JsonObject> call = RetrofitUtils.getInstance().getIncompleteIncidentRecordByUserId(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    JSONObject resp_msg = jsonObject.getJSONObject("resp_msg");
                    int status = Integer.valueOf(resp_msg.getString("incompleteIncidentStatus"));
                    Log.i("INCOMPLETE", "status: " + status);

                    //ZN - 20210201 save username for display in main page
                    MyApplication.getInstance().setIncompleteIncidentStatus(status);
                    //finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    //ZN - 20210113 generate fixed route in event no route solve - warm up and start routing engine
    private void startRoutingEngine() {
        Point a = new Point(103.80656000000899, 1.4121799997031987,SpatialReference.create(4326));
        Point b = new Point(103.80656000000899, 1.4121799997031987,SpatialReference.create(4326));
        List<Stop> stops = Arrays.asList(new Stop(a), new Stop(b));
        RouteTask routeTask = MyApplication.getInstance().getRouteTask();

        final ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("ROUTE", "[NewMainActivity]startRoutingEngine parameters created " + routeParametersFuture.isDone());

                    // define the route parameters
                    RouteParameters routeParameters = routeParametersFuture.get();
                    routeParameters.setStops(stops);
                    routeParameters.setOutputSpatialReference(SpatialReferences.getWgs84());

                    Log.i("ROUTE", "[NewMainActivity]startRoutingEngine ready to start");
                    ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
                    routeResultFuture.addDoneListener(() -> {
                        try {
                            Log.i("ROUTE", "[NewMainActivity]startRoutingEngine route results completed");

                            // get the route geometry from the route result
                            RouteResult routeResult = routeResultFuture.get();
                            //startNavigation(routeTask,routeParameters,routeResult);
//                            Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();

//                            Route retRoute = routeResult.getRoutes().get(0);
//                            List<DirectionManeuver> retDirections = retRoute.getDirectionManeuvers();
////                                for (DirectionManeuver directionManeuver : retDirections) {
////                                    Log.i("Directions", directionManeuver.getDirectionText());
////                                }
//
//                            // create a graphic for the route geometry
//                            routeGraphic = new Graphic(routeGeometry,
//                                    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5f));
//
//                            //ZN - 20200624 create graphic for CCP point
//                            PictureMarkerSymbol CCP_pt_pms = new PictureMarkerSymbol(CCP_pt);
//                            CCP_pt_pms.setHeight(32);
//                            CCP_pt_pms.setWidth(32);
//                            CCP_pt_pms.loadAsync();
//                            CCP_pt_grap = new Graphic(endPoint, CCP_pt_pms);
//
//                            // add it to the graphics overlay
//                            //mMapView.getGraphicsOverlays().get(0).getGraphics().add(routeGraphic);
//                            mRouteOverlay.getGraphics().add(routeGraphic);
//
//                            //ZN - 20200624
//                            //mRouteOverlay.getGraphics().add(CCP_pt_grap);
//
//                            // set the map view view point to show the whole route
//                            mMapView.setViewpointAsync(new Viewpoint(routeGeometry.getExtent()));
//
//                            //ZN - 20200123
//                            //prepare route info to send back to server
//                            //Transform spatial reference of lat lon points and add to return points
//                            StringBuilder retString = new StringBuilder();
//                            Iterable<Point> i = routeGeometry.getParts().getPartsAsPoints();
//                            for (Point p : i) {
//                                Point temp = (Point) GeometryEngine.project(p, SpatialReference.create(4326));
//                                retString.append(temp.getY() + ";" + temp.getX() + "|");
//                            }
//                            double dist = Math.round(routeResult.getRoutes().get(0).getTotalLength());
//                            double time = Math.round(routeResult.getRoutes().get(0).getTravelTime());
//                            //send to server
//                            sendResponderRoute(retString, dist, time, purpose);
//
//                            //ZN - 20200123
//                            //create callout to display route info
//                            Envelope envelope = routeGraphic.getGeometry().getExtent();
//                            Callout co = mMapView.getCallout();
//                            TextView calloutContent = new TextView(getApplicationContext());
//                            calloutContent.setTextColor(Color.BLACK);
//                            calloutContent.setSingleLine(false);
//                            calloutContent.setLines(2);
//                            calloutContent.append("ETA: " + Math.round(time) + "mins\n");
//                            calloutContent.append("Distance: " + Math.round(dist / 1000) + "km\n");
//                            co.setContent(calloutContent);
//                            //co.setLocation(new Point(103.80656000000899, 1.4121799997031987,SpatialReference.create(4326)));
//                            co.setLocation(envelope.getCenter());
//                            co.show();
//
//                            //ZN - 20210113 generate fixed route in event no route solve - return route found
//                            retValue = 1;

                        } catch (ExecutionException | InterruptedException e) {
                            String error = "[NewMainActivity]startRoutingEngine error creating default route parameters: " + e.getMessage();
                            Log.e("ROUTE", error);
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    String error = "[NewMainActivity]startRoutingEngine error creating default route parameters: " + e.getMessage();
                    Log.e("ROUTE", error);

                }
            }
        });

    }

    //ZN - 20220322 smart search feature for medical centre / hospital - retrieve from server
    private void getEvacLocationList() {
        Call<JsonObject> call = RetrofitUtils.getInstance().getEvacLocationList();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    String resp_msg = jsonObject.getString("resp_msg");
                    JSONArray jsonArray = new JSONArray(resp_msg);
                    HashMap<String, String> tempEvacMCHash = new HashMap<String, String>();
                    HashMap<String, String> tempEvacHospitalHash = new HashMap<String, String>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Log.i("EVAC", "[getEvacLocationList] " +" name: " +jsonArray.getJSONObject(i).getString("name") + " location: " + jsonArray.getJSONObject(i).getString("location"));

                        //ZN - 20220322 smart search feature for medical centre / hospital - put both types of evac into one hash
                        tempEvacHospitalHash.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("location"));
//                        if (jsonArray.getJSONObject(i).getString("type").equalsIgnoreCase("Hospital"))
//                            tempEvacHospitalHash.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("location"));
//                        else if (jsonArray.getJSONObject(i).getString("type").equalsIgnoreCase("MC"))
//                            tempEvacMCHash.put(jsonArray.getJSONObject(i).getString("name"), jsonArray.getJSONObject(i).getString("location"));
                    }

                    //ZN - 20220322 smart search feature for medical centre / hospital - put both types of evac into one hash
                    //MyApplication.getInstance().setEvacMCLocations(tempEvacMCHash);
                    MyApplication.getInstance().setEvacHospitalLocations(tempEvacHospitalHash);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("TEST",t.getMessage().toString());
            }
        });

    }

    //ZN - 20220224 new send-out feature - add to incident record
    public void createSendOutIncident(String remarks) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String now = sdf.format(new Date());

        String latitude = MyApplication.getInstance().getUserCoordinates().getLatitude();
        String longitude = MyApplication.getInstance().getUserCoordinates().getLongitude();

        Map<String,String> params = new HashMap<>();
        params.put("remarks", remarks);
        params.put("status", "A-Alert");
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("incident_location", user.getActivationLocation());
        params.put("timestamp", now);
        params.put("user_id", user.getId());

        Call<JsonObject> call = RetrofitUtils.getInstance().createSendOutIncident(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code")==0){
                        showToast("Create send out record failed");

                        //ZN - 20220619 logging to external file
                        Log.i("INCIDENT", "[createSendOutIncident]  create send out at server failed ");
                    } else {
                        showToast("Create send out record success");
                        String resp_msg = jsonObject.getString("resp_msg");
                        Log.i("SendOut", "[createSendOutIncident]Send Out incident id: " + resp_msg);
                        MyApplication.getInstance().setSendOutIncidentID(resp_msg);

                        //ZN - 20220619 logging to external file
                        Log.i("INCIDENT", "[createSendOutIncident]  create send out at server success ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
            }
        });

    }

    //ZN - 20220224 new send-out feature - add to incident record
    public void completeSendOutIncident(String incident_id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String now = sdf.format(new Date());

        Map<String,String> params = new HashMap<>();
        params.put("completed_time", now);
        params.put("incidentId", incident_id);
        params.put("userId", user.getId());

        Call<JsonObject> call = RetrofitUtils.getInstance().completeSendOutIncident(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code")==0){
                        showToast("Complete send out failed");
                    } else {
                        showToast("Complete send out record success");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
            }
        });

    }

//    //ZN - 20220606 store and forward
    private void updateActivationTiming() {
        Map<String,String> params = MyApplication.getInstance().getActivationTimingsMap();
        Log.i("ÄCTIVATION", params.toString());
        //check if have completed activation timings
        //might replace with checking of number of timings captured and do "if not-empty" then loop through the hashmap and update db individually at server end
        //also clear getActivationTimingsMap upon rcv new incident or if incident completed and rcv by server

        //ZN-20220619 revised to send timings upon every link status restore
        Call<JsonObject> call = RetrofitUtils.getInstance().updateActivationTiming(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    Log.i("SERVER", "[GetNewInfoService] updateActivationTiming");
                    JSONObject json = new JSONObject(response.body().toString());
                    int resp_code = json.getInt("resp_code");
                    if (resp_code == 1) {
                        //clear all when server has rcv and recorded into db the activation timings
                        Log.i("SERVER", "[NewMainActivity] updateActivationTiming updated");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    //ZN - 20220629 sending of logs to server
    public void uploadLog() {
        Map<String, RequestBody> map = new HashMap<>();
        String logDir = MyApplication.getInstance().getLogDirectory();
        String logFilename = MyApplication.getInstance().getLogFilename();
        File logFile = new File(logDir, logFilename);
        Log.i("TEST_LOG", "log file exist: " + logFile.exists() + " details: " + logFile.getPath() + " " + logFile.getName());
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), logFile);

        map.put(logFile.getName(), requestBody);

        Call<JsonObject> call = RetrofitUtils.getInstance().uploadLog(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    Log.i("SERVER", "[NewMainActivity] uploadLog");
                    JSONObject json = new JSONObject(response.body().toString());
                    int resp_code = json.getInt("resp_code");
                    if (resp_code == 1) {
                        Log.i("ON_CLICK", "[NewMainActivity] submit Log success");
                        ToastUtil.showToast(NewMainActivity.this,"Logs submitted");
                        return;
                    } else {
                        Log.i("ON_CLICK", "[NewMainActivity] submit Log failed");
                    }
                } catch (JSONException e) {
                    ToastUtil.showToast(NewMainActivity.this,"Error in submitting logs");
                    Log.i("ON_CLICK", "[NewMainActivity] submit Log failed: " + e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    //ZN - 20220720 restore activation
    public void clearActivationLog() {
        LogcatHelper.clearActivationLog();
    }

    //ZN - 20220720 restore activation
    public void addActivationLog(String value, boolean append) {
        LogcatHelper.addActivationLog(value, append);
    }

    //ZN - 20220720 restore activation
    public void checkAndRestoreActivation() {
        String str_activationLog = LogcatHelper.readActivationLog();
        Log.i("ACTIVATION_LOG", "Content of activation log: " + str_activationLog);
        //if activation log entry is present then proceed to restore incident, else ignore
        if (!str_activationLog.equalsIgnoreCase("-1")) {
            //String[] str_array = str_activationLog.split(",");
            //[0] - user id, [1] - incident id, [2] - next status, [3] - lat:lon
            //create new method to get incomplete incident from server to restore
            //or get json string from activationLog
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(str_activationLog);
                if (jsonObject != null) {
                    incident = new NewIncident(jsonObject);
                    MyApplication.getInstance().setCurrentIncidentID(incident.getId());
                    Log.i("INCIDENT", "restored incident id: " + incident.getId());

                    //set flag to indicate incident notification rcv and incident has been previously ack
                    MyApplication.getInstance().setRcvIncidentNotification(true);
                    MyApplication.getInstance().setIncidentAck(true);

                    //broadcast to HomeFragment to change screen to Activated mode
                    Intent fragIntent = new Intent("NewActivationStatusMsgReceiver");
                    fragIntent.putExtra("activation_status", new Boolean(true));
                    fragIntent.putExtra("incident", incident);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(fragIntent);

                    //disable bottom menu buttons except HOME
                    btn_task_request.setEnabled(false);
                    btn_more.setEnabled(false);
                    btn_task_assign.setEnabled(false);

                    //ZN - 20211201 cancel task assignment - register listener for cancellation
                    registerCancelledIncidentListener();

                    //ZN - 20220606 store and forward
                    //clear hashmap before put in new incident details
                    MyApplication.getInstance().getActivationTimingsMap().clear();
                    MyApplication.getInstance().getActivationTimingsMap().put("incident_id", incident.getId());
                    MyApplication.getInstance().getActivationTimingsMap().put("user_id", user.getId());

                    gotoMainActivty(NewMainActivity.MAIN_RESTORE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}


