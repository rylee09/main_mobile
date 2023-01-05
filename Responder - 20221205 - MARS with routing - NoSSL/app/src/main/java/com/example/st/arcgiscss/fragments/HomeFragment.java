package com.example.st.arcgiscss.fragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.activites.MainActivity;
import com.example.st.arcgiscss.activites.NewMainActivity;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3Fragment;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.LogcatHelper;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.example.st.arcgiscss.views.IosAlertDialog;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends D3Fragment {

//    @D3View(click = "onClick")
//    RelativeLayout rl_system_read,rl_status, rl_taskAssignment;
//
//    @D3View(click = "onClick")
//    ImageView iv_backscan,iv_logout;
//    @D3View
//    TextView tv_nowtime,tv_link_status,tv_status,tv_status_time, tv_user_name, tv_system_ready, tv_backupEAS, tv_activationStatus;

    @D3View(click = "onClick")
    RelativeLayout rl_acknowledge;

    @D3View(click = "onClick")
    LinearLayout ll_tips,ll_activation_bg,ll_status_bg;

    @D3View(click = "onClick")
    ImageView iv_activation,iv_status,iv_system_readiness,iv_peer_readiness, iv_logout;

    @D3View
    TextView tv_activation,tv_status,tv_time,tv_update_time, tv_user_name, tv_timestamp;

    @D3View(click = "onClick")
    TextView tv_acknowledge;

    private User user;

    private final String READY = "Ready";
    private final String NOT_READY = "Not Ready";

    private final String ACTIVATED = "Activated";
    private final String STANDBY = "Standby";
    private final String OFFLINE = "Offline";

    //ZN - 20210805
    private String status;

    //ZN - 20210819
    private NewIncident incident;
    private String temp_timestamp;
    private Button openChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("EVENT", "[HomeFragment] onCreateView");



        View view = setContentView(inflater, R.layout.frag_status);
        initData();
        initView();


        //ZN - 20210630
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myLinkStatusReceiver,
                new IntentFilter("NewLinkStatusReceiver"));

        //ZN - 20210719
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBackupEASStatusReceiver,
                new IntentFilter("NewBackupEASMsgReceiver"));

        //ZN - 20210726
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myActivationStatusReceiver,
                new IntentFilter("NewActivationStatusMsgReceiver"));

        //ZN - 20211201 cancel task assignment
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myCancelledIncidentReceiver,
                new IntentFilter("NewCancelledIncidentReceiver"));

        //ZN - 20220125 consolidate peer readiness method call
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myPeerReadinessMsgReceiver,
                new IntentFilter("NewPeerReadinessMsgReceiver"));

        return view;
    }

    public void initData() {
        if (CacheUtils.getUser(getContext()) != null){
            user = CacheUtils.getUser(getContext());
        }
    }

    public void initView() {
        if (user!=null)
            updateLinkStatus(user.getStatus());

        tv_user_name.setText(user.getUsername());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_logout:

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "Log out clicked");

                ((NewMainActivity)getActivity()).showExitView();
                break;
//            case R.id.rl_taskAssignment:
//                ((NewMainActivity)getActivity()).gotoMainActivty();
//                break;
            case R.id.iv_system_readiness:
                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "System readiness clicked");

                if (status.equalsIgnoreCase(NewMainActivity.SYSTEM_NOT_READY))
                    showSystemReadyConfirmView();
                else
                    ((NewMainActivity)getActivity()).switchFragment(NewMainActivity.FRAG_SYSTEM_READINESS);
                break;
            case R.id.iv_peer_readiness:
                ((NewMainActivity)getActivity()).switchFragment(NewMainActivity.FRAG_PEER_READINESS);
                break;
            case R.id.tv_acknowledge:
                //ZN - 20210819
                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "Acknowledge clicked");
                rl_acknowledge.setVisibility(View.INVISIBLE);

                //ZN - 20210819 cancel notification
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(100);

                //ZN - 20210819 copy incident ack workflow to home page
                setIncidentAck();
                Toast.makeText(getContext(), "Incident Acknowledged", Toast.LENGTH_LONG).show();
                MyApplication.getInstance().setCheckUpdatePOC(true);

                //ZN - 20220720 restore activation - write iniital state of incident into activation log
                incident.setCurrentStatus("acknowledged");
                if (incident.getLocationType().equalsIgnoreCase("4")) {
                    //training area; route to CCP
                    incident.setDestinationPointLon(incident.getCCP_lon());
                    incident.setDestinationPointLat(incident.getCCP_lat());
                } else {
                    //camp; route to incident directly
                    incident.setDestinationPointLon(incident.getLng());
                    incident.setDestinationPointLat(incident.getLat());
                }
                LogcatHelper.addActivationLog(incident.toString(), false);

                ((NewMainActivity)getActivity()).gotoMainActivty(NewMainActivity.MAIN_ACTIVATED);
                break;
        }
    }

    @Override
    public void onDetach() {
        Log.i("EVENT", "[HomeFragment] onDetach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Log.i("EVENT", "[HomeFragment] onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("EVENT", "[HomeFragment] onResume");
        super.onResume();

//        status = ((NewMainActivity)getActivity()).getSystemReadyStatus();
//        Log.i("SYSTEM STATUS", "System Status: " + status);
//        updateSystemReadyTV(status);
        Log.i("INCIDENT OBJECT", "[HomeFragment] onResume incident: " + incident);

        //ZN - 20210726 check if got incident and set Activation status accordingly
        //ZN - 20210922 added check if there is incomplete incident
        Log.i("INCOMPLETE" , "MyApplication status: " + MyApplication.getInstance().getIncompleteIncidentStatus());
        if (MyApplication.getInstance().isRcvIncidentNotification() || MyApplication.getInstance().getIncompleteIncidentStatus() > 0) {
            //if current in incident
            Log.i("ACTIVATION", "current in incident: " + incident);
            updateSystemReady(NewMainActivity.MAIN_ACTIVATED);
        } else {
            status = ((NewMainActivity)getActivity()).getSystemReadyStatus();
            Log.i("SYSTEM STATUS", "System Status: " + status);
            updateSystemReady(status);
        }

    }

    @Override
    public void onDestroyView() {
        Log.i("EVENT", "[HomeFragment] onDestroyView");
        super.onDestroyView();
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myLinkStatusReceiver);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myBackupEASStatusReceiver);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myActivationStatusReceiver);

    }

    @Override
    public void onDestroy() {
        Log.i("EVENT", "[HomeFragment] onDestroy");
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myLinkStatusReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myBackupEASStatusReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myActivationStatusReceiver);

        //ZN - 20211201 cancel task assignment
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myCancelledIncidentReceiver);

        //ZN - 20220125 consolidate peer readiness method call
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myPeerReadinessMsgReceiver);
    }

    //ZN - 20210630 link status receiver
    private final BroadcastReceiver myLinkStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myLinkStatusReceiver] link status onReceive");
            String status = (String) intent.getExtras().getSerializable("link_status");

            //ZN - 20210819 update timestamp on display
            String timestamp = (String) intent.getExtras().getSerializable("timestamp");
            tv_timestamp.setText(timestamp);

            if (timestamp != null)
                temp_timestamp = timestamp;

            updateLinkStatus(status);
        }
    };

    //ZN - 20210630 backupEAS status receiver
    private final BroadcastReceiver myBackupEASStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myBackupEASStatusReceiver] status onReceive");
            Boolean status = (Boolean) intent.getExtras().getSerializable("backupEAS_status");
            updateBackupEASStatus(status);
        }
    };

    //ZN - 20210726 activation status receiver
    private final BroadcastReceiver myActivationStatusReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myActivationStatusReceiver] status onReceive");
            Boolean status = (Boolean) intent.getExtras().getSerializable("activation_status");
            //updateActivationStatus(status.booleanValue());
            updateSystemReady(NewMainActivity.MAIN_ACTIVATED);
            //rl_acknowledge.setVisibility(View.VISIBLE);

            //ZN - 20210819 extract incident object if exists
            if (intent.hasExtra("incident")) {
                incident = (NewIncident) intent.getExtras().getSerializable("incident");
            }

        }
    };

    //ZN - 20211201 cancel task assignment
    private final BroadcastReceiver myCancelledIncidentReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myCancelledIncidentReceiver] status onReceive");
            Log.i("INCIDENT", "[myCancelledIncidentReceiver] incident cancelled");
            String status = (String) intent.getExtras().getSerializable("cancelled_incident");
            updateSystemReady(NewMainActivity.SYSTEM_CANCELLED);
        }
    };

    //ZN - 20220125 consolidate peer readiness method call
    private final BroadcastReceiver myPeerReadinessMsgReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            Log.i("onReceive", "[myPeerReadinessMsgReceiver] status onReceive");
            HashMap<String, String> data = (HashMap<String, String>) intent.getExtras().getSerializable("data");
            updatePeerReadinessStatus(data);
        }
    };


//    //ZN - 20210709 system ready status receiver
//    private final BroadcastReceiver mySystemReadyStatusReceiver = new BroadcastReceiver()
//    {
//        @Override
//        public void onReceive(Context context, android.content.Intent intent) {
//            Log.i("onReceive", "[mySystemReadyStatusReceiver] system ready status onReceive");
//            String status = (String) intent.getExtras().getSerializable("system_ready_status");
//            updateSystemReadyTV(status);
//        }
//    };

    //ZN - 20210630
    private void updateLinkStatus(String status) {
        if (status.equals("Offline")){
//            Drawable drawable1 = getResources().getDrawable(R.mipmap.offline_bg);
//            rl_status.setBackground(drawable1);
//            tv_link_status.setTextColor(Color.WHITE);
//            tv_status.setText("Offline");
//            tv_status.setTextColor(Color.WHITE);
//            tv_status_time.setTextColor(Color.WHITE);

            Drawable drawable = getResources().getDrawable(R.mipmap.offlinestatus);
            ll_status_bg.setBackground(drawable);
            Drawable drawable2 = getResources().getDrawable(R.mipmap.offline_status);
            iv_status.setBackground(drawable2);
            Drawable drawable3 = getResources().getDrawable(R.drawable.offline_bg);
            tv_status.setBackground(drawable3);
            tv_status.setText("Offline");

            tv_timestamp.setText(temp_timestamp);
            tv_timestamp.setTextColor(Color.RED);
            //tv_time.setTextColor(getResources().getColor(R.color.main_red));
        } else {
//            Drawable drawable2 = getResources().getDrawable(R.mipmap.new_main_btn);
//            rl_status.setBackground(drawable2);
//            tv_link_status.setTextColor(Color.parseColor("#767676"));
//            tv_status.setText("Online");
//            tv_status.setTextColor(Color.parseColor("#010101"));
//            tv_status_time.setTextColor(Color.parseColor("#b7b7b7"));

            Drawable drawable = getResources().getDrawable(R.mipmap.onlinestatus_bg);
            ll_status_bg.setBackground(drawable);
            Drawable drawable2 = getResources().getDrawable(R.mipmap.online_status);
            iv_status.setBackground(drawable2);
            Drawable drawable3 = getResources().getDrawable(R.drawable.online_bg);
            tv_status.setBackground(drawable3);
            tv_status.setText("Online");
            //tv_time.setTextColor(getResources().getColor(R.color.new_main_bottom_text));

            tv_timestamp.setTextColor(Color.parseColor("#80ffff"));
        }
    }

    //ZN - 20210630 update System Ready status
    private void updateSystemReady(String status) {
        if (status.equalsIgnoreCase(NewMainActivity.SYSTEM_NOT_READY)) {
            Log.i("DISPLAY", "[updateSystemReady] SYSTEM_NOT_READY");
            iv_system_readiness.setImageResource(R.mipmap.system_offline);

            //ZN - 20210819 update activation status
            Drawable drawable = getResources().getDrawable(R.mipmap.activation_offline_bg);
            ll_activation_bg.setBackground(drawable);
            Drawable activationicon = getResources().getDrawable(R.mipmap.offline);
            iv_activation.setBackground(activationicon);
            tv_activation.setText(OFFLINE);
        } else if (status.equalsIgnoreCase(NewMainActivity.MAIN_ACTIVATED)) {
            Log.i("DISPLAY", "[updateSystemReady] MAIN_ACTIVATED");
            Drawable drawable = getResources().getDrawable(R.mipmap.activation_activated_bg);
            ll_activation_bg.setBackground(drawable);
            Drawable activationicon = getResources().getDrawable(R.mipmap.activated);
            iv_activation.setBackground(activationicon);
            tv_activation.setText(ACTIVATED);

//            iv_system_readiness.setImageResource(R.mipmap.system_grayout);
            iv_system_readiness.setEnabled(false);
//            iv_peer_readiness.setImageResource(R.mipmap.peer_r_grayout);
            iv_peer_readiness.setEnabled(false);
            iv_logout.setEnabled(false);

            if(!MyApplication.getInstance().isIncidentAck() && MyApplication.getInstance().getIncompleteIncidentStatus() == 0) {
                Log.i("DISPLAY", "incident not ack");
                rl_acknowledge.setVisibility(View.VISIBLE);
            }

        } else {
            //SYSTEM_READY or SYSTEM_CANCELLED
            iv_system_readiness.setImageResource(R.mipmap.system_online);
            Log.i("DISPLAY", "[updateSystemReady] SYSTEM_ONLINE");


            //ZN - 20210819 update activation status
            Drawable drawable = getResources().getDrawable(R.mipmap.activation_standby_bg);
            ll_activation_bg.setBackground(drawable);
            Drawable activationicon = getResources().getDrawable(R.mipmap.standby);
            iv_activation.setBackground(activationicon);
            tv_activation.setText(STANDBY);

            //            iv_system_readiness.setImageResource(R.mipmap.system_online);
            iv_system_readiness.setEnabled(true);
//            iv_peer_readiness.setImageResource(R.mipmap.peer_r_online);
            iv_peer_readiness.setEnabled(true);
            iv_logout.setEnabled(true);

            //ZN - 20211201 cancel task assignment
            if (status.equalsIgnoreCase(NewMainActivity.SYSTEM_CANCELLED)) {
                rl_acknowledge.setVisibility(View.INVISIBLE);
                Log.i("DISPLAY", "[updateSystemReady] SYSTEM_CANCELLED");
            }
        }
    }

    //ZN - 20210630 update EAS backup text
    private void updateBackupEASStatus(Boolean status) {
        if (status.booleanValue())
            iv_peer_readiness.setImageResource(R.mipmap.peer_r_activated);
        else
            iv_peer_readiness.setImageResource(R.mipmap.peer_r_online);
    }

    //ZN - 20220125 consolidate peer readiness method call
    private void updateBackupEASStatus(boolean status) {
        if (status)
            iv_peer_readiness.setImageResource(R.mipmap.peer_r_activated);
        else
            iv_peer_readiness.setImageResource(R.mipmap.peer_r_online);
    }

    //ZN - 20220125 consolidate peer readiness method call
    private void updatePeerReadinessStatus(HashMap<String, String> data) {
        Log.i("ACTION", "in updatePeerReadinessStatus");
        //status string e.g. "OFFLINE:Y"

        User user = CacheUtils.getUser(getContext());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String username = entry.getKey();
            String status = entry.getValue();
            String[] temp = status.split(":");
//            if (!temp[0].equalsIgnoreCase("STANDBY") && temp[1].equalsIgnoreCase("Y")) {
//                //for EAS to check peer
//                Log.i("PEER", "[updatePeerReadinessStatus] print temp[0]: " + temp[0] + " " + "temp[1]: " + temp[1]);
//                updateBackupEASStatus(true);
//                break;
//            } else if (temp[1].equalsIgnoreCase("Y")) {
//
//
//            } else if (!temp[0].equalsIgnoreCase("STANDBY")) {
//                //for non-EAS to check peer
//                Log.i("PEER", "[updatePeerReadinessStatus] print temp[0]: " + temp[0] + " " + "temp[1]: " + temp[1] );
//                updateBackupEASStatus(true);
//                break;
//            }

            if (user.getType().equalsIgnoreCase("EAS")) {
                if (temp[1].equalsIgnoreCase("Y") && !temp[0].equalsIgnoreCase("STANDBY")) {
                    //for EAS
                    updateBackupEASStatus(true);
                    Log.i("EAS", "have peer and not standby");
                    break;
                } else if (temp[1].equalsIgnoreCase("Y") && temp[0].equalsIgnoreCase("STANDBY")) {
                    //for EAS
                    updateBackupEASStatus(false);
                    Log.i("EAS", "have peer and in standby");
                } else {
                    //do nothing
                }
            } else {
                //for non-EAS
                if (!temp[0].equalsIgnoreCase("STANDBY")) {
                    updateBackupEASStatus(true);
                    break;
                } else
                    updateBackupEASStatus(false);
            }
        }

    }

    //ZN - 20210726 update Activation status text
    private void updateActivationStatus(boolean status) {
        if (status) {
//            tv_activation.setText(ACTIVATED);
            Log.i("ACTIVATE", "[HomeFragment] updateActivationStatus");
            Drawable drawable = getResources().getDrawable(R.mipmap.activation_activated_bg);
            ll_activation_bg.setBackground(drawable);
            Drawable activationicon = getResources().getDrawable(R.mipmap.activated);
            iv_activation.setBackground(activationicon);
            tv_activation.setText(ACTIVATED);

//            iv_system_readiness.setImageResource(R.mipmap.system_grayout);
            iv_system_readiness.setEnabled(false);
//            iv_peer_readiness.setImageResource(R.mipmap.peer_r_grayout);
            iv_peer_readiness.setEnabled(false);

        } else {
//            tv_activation.setText(NOT_ACTIVATED);
            Drawable drawable = getResources().getDrawable(R.mipmap.activation_standby_bg);
            ll_activation_bg.setBackground(drawable);
            Drawable activationicon = getResources().getDrawable(R.mipmap.standby);
            iv_activation.setBackground(activationicon);
            tv_activation.setText(STANDBY);

//            iv_system_readiness.setImageResource(R.mipmap.system_online);
            iv_system_readiness.setEnabled(true);
//            iv_peer_readiness.setImageResource(R.mipmap.peer_r_online);
            iv_peer_readiness.setEnabled(true);
        }
    }

    //ZN - 20210630
    private void showSystemReadyConfirmView(){

        new IosAlertDialog(getContext()).builder().setTitle("System Ready").setMsg("Confirm Report System Ready ?").setCancelable(false).setNegativeButton("No", null).setPositiveButton("YES", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user.setSystemReady(NewMainActivity.SYSTEM_READY);
                ((NewMainActivity)getActivity()).updateSystemReadyStatus(NewMainActivity.SYSTEM_READY);
                ((NewMainActivity)getActivity()).callServerSystemReady(NewMainActivity.SYSTEM_READY, "");
                //iv_system_readiness.setImageResource(R.mipmap.system_online);
                updateSystemReady(NewMainActivity.SYSTEM_READY);
                status = ((NewMainActivity)getActivity()).getSystemReadyStatus();

                if(MyApplication.getInstance().getSendOutIncidentID() != "") {
                    ((NewMainActivity)getActivity()).completeSendOutIncident(MyApplication.getInstance().getSendOutIncidentID());
                    MyApplication.getInstance().setSendOutIncidentID("");
                }

                //ZN - 20220619 logging to external file
                Log.i("ON_CLICK", "System readiness: Ready");
            }
        }).show();
    }

    private void setIncidentAck() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String now = sdf.format(new Date());
        Map<String,String> params = new HashMap<>();
        String userId = CacheUtils.getUserId(getContext());
        params.put("incidentId",incident.getId());
        params.put("userId", userId);
        params.put("ack_time", now);

        //ZN - 20220606 store and forward
        MyApplication.getInstance().getActivationTimingsMap().put("priAckTime", now);

        Call<JsonObject> call = RetrofitUtils.getInstance().setIncidentAck(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //ZN - 20220619 logging to external file
                Log.i("INCIDENT", "[setIncidentAck] Incident ack and server responded");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //ZN - 20220619 logging to external file
                Log.i("INCIDENT", "[setIncidentAck] Incident ack and server failed to respond");
            }
        });

        //ZN - 20210420 set incident ack true
        MyApplication.getInstance().setIncidentAck(true);
    }

}

