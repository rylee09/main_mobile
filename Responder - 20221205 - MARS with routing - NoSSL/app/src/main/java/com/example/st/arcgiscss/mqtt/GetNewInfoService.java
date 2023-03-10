package com.example.st.arcgiscss.mqtt;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.model.FuelTopUp;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.model.POCUpdate;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class GetNewInfoService extends Service {
    private String TAG = getClass().getSimpleName();
    private final IBinder mBinder = new GetNewInfoService.LocalBinder();
    private Thread statusCheckThread;
    private Thread timestampThread;

    private GetNewInfoService.StatusCheckRunnable statusCheckRunnable = new GetNewInfoService.StatusCheckRunnable(this);
    private GetNewInfoService.TimestampRunnable timestampRunnable = new GetNewInfoService.TimestampRunnable(this);
//    private boolean first = true;
    private MyApplication application;

    private static final int PUSH_NOTIFICATION_ID = (0x001);
    private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
    private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";

    //ZN - 20210630 add User object for link status update
    private User user;

    private ConnectivityManager manager;

    //ZN - 20210711
    private boolean isCheckPOCRecord = false;

    //ZN - 20211201 cancel task assignment
    private boolean isCheckIncidentCancellation;


    @Override
    public void onCreate(){
        Log.d("SERVICE", "GetNewInfoService create");

        application = MyApplication.getInstance();
        statusCheckThread = new Thread(statusCheckRunnable);
        statusCheckThread.start();

        //ZN - 20210721 for timestamp thread
        timestampThread = new Thread(timestampRunnable);
        timestampThread.start();

        //ZN - 20210630 init User object
        user = CacheUtils.getUser(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public class LocalBinder extends Binder
    {
        public GetNewInfoService getService()
        {
            Log.d(TAG, "LocalBinder getService");
            return GetNewInfoService.this;
        }
    }

    public class StatusCheckRunnable implements Runnable {
        private String TAG = getClass().getSimpleName();
        private boolean isRunning = true;
        private GetNewInfoService mService;
        public StatusCheckRunnable(GetNewInfoService service) {
            mService = service;
        }
        @Override
        public void run() {
            Log.d("SERVICE", "StatusCheckRunnable running");
            while (isRunning) {

                getIncidentRecord();

                //ZN - 20210504 for maintenance approval check
                getMaintenanceApproval();

                //ZN - 20210427 for update of link status frequently
                //doTimestamp(CacheUtils.getUser(GetNewInfoService.this).getId());

                //ZN - 20210719 check for backup EAS activated
                //getBackupEASActivated();

                //getEASUserStatusFromServer();

                //ZN - 20220125 consolidate peer readiness method call
                getPeerReadinessFromServer();

                //ZN - 20210711 only check POC if there is incident activated
                if (application.isCheckUpdatePOC())
                    getPOCRecord();

                //ZN - 20211201 cancel task assignment
                getCancelledIncident();

                //ZN - 20220606 store and forward
                //updateActivationTiming();

//                if (first == false){
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                }
//                first = false;
            }
        }
        public void cancel() {
            isRunning = false;
        }
    }

    public class TimestampRunnable implements Runnable {
        private String TAG = getClass().getSimpleName();
        private boolean isRunning = true;
        private GetNewInfoService mService;
        public TimestampRunnable(GetNewInfoService service) {
            mService = service;
        }
        @Override
        public void run() {
            Log.d(TAG, "TimestampRunnable running");
            while (isRunning) {

                //ZN - 20210427 for update of link status frequently
                doTimestamp(CacheUtils.getUser(GetNewInfoService.this).getId());

                try {
                    sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void cancel() {
            isRunning = false;
        }
    }

    public void getIncidentRecord(){
        Map<String,String> params = new HashMap<>();
//        Log.e("TEST","-----"+CacheUtils.getUserId(this));
        params.put("userId",CacheUtils.getUserId(this));
//        Log.i("getIncidentRecord", CacheUtils.getUserId(this));

        Call<JsonObject> call = RetrofitUtils.getInstance().getIncidentRecord(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i("POLLING","[GetNewInfoService] getIncidentRecord");
                Intent intent = new Intent("NewMQTTMsgReceiver");

                try {
                        Log.i("POLLING","[GetNewInfoService] getIncidentRecord");
                        JSONObject json = new JSONObject(response.body().toString());
                        int resp_code = json.getInt("resp_code");
                        if (resp_code == 0){

                            //ZN - 20211201 cancel task assignment
                            isCheckIncidentCancellation = false;

                            return;
                        }
                        String resp_msg = json.getString("resp_msg");
                        if (resp_msg == null||resp_msg.equals("")){
                            return;
                        }
                        if (resp_msg.length()>0){
                            JSONObject jsonObject = new JSONObject(resp_msg);
                            NewIncident incident = new NewIncident(jsonObject);
                            intent.putExtra("incident",incident);
                            Log.i("BROADCAST", "[GetNewInfoService] sending broadcast: NewMQTTMsgReceiver");
                            sendBroadcast(intent);

                            //ZN - 20211201 cancel task assignment
                            isCheckIncidentCancellation = false;
                        }
                    } catch(JSONException e){ e.printStackTrace(); }

                }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    //ZN - 20210117 new polling method for updated POC
    public void getPOCRecord(){
        Map<String,String> params = new HashMap<>();

        params.put("incidentId",application.getCurrentIncidentID());

        Call<JsonObject> call = RetrofitUtils.getInstance().checkUpdatePOC(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Intent intent = new Intent("NewPOCUpdateReceiver");
                try {
                    Log.i("POLLING","[GetNewInfoService] getPOCRecord");
                    JSONObject json = new JSONObject(response.body().toString());
                    int resp_code = json.getInt("resp_code");
                    if (resp_code == 0){
                        return;
                    }
                    String resp_msg = json.getString("resp_msg");
                    if (resp_msg == null||resp_msg.equals("")){
                        return;
                    }
                    if (resp_msg.length()>0){
                        JSONObject jsonObject = new JSONObject(resp_msg);
                        POCUpdate update = new POCUpdate(jsonObject);
                        intent.putExtra("update",update);
                        Log.i("BROADCAST", "[GetNewInfoService] sending broadcast: NewPOCUpdateReceiver");
                        sendBroadcast(intent);

                        //send back ack to server
                        ackUpdatePOC();
                    }
                } catch(JSONException e){ e.printStackTrace(); }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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

        Intent intent = new Intent("NewLinkStatusReceiver");

        Call<JsonObject> call = RetrofitUtils.getInstance().setTimestamp(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code")==-1){
                        //showToast("Send Timestamp failed");
                        Log.i("TIMESTAMP", "Send Timestamp failed");
                    } else {
                        //showToast("Send Timestamp ok");
                        Log.i("TIMESTAMP", "Send Timestamp ok");

                        //ZN - 20210630 set user status to "ONLINE" and broadcast
                        user.setStatus("Online");
                        intent.putExtra("link_status", "Online");

                        //ZN - 20210819 include timestammp
                        intent.putExtra("timestamp", now);

                        Log.i("BROADCAST", "[GetNewInfoService] sending broadcast: NewLinkStatusReceiver");
                        sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("SYSTEM", "[doTimestamp] FAILED: " + t.getMessage().toString());
                user.setStatus("Offline");
                intent.putExtra("link_status", "Offline");
                sendBroadcast(intent);
            }
        });
    }

    //ZN - 20210417 method to retrieve maintenance approval
    private void getMaintenanceApproval() {
        Map<String,String> params = new HashMap<>();
        params.put("userId",CacheUtils.getUser(this).getId());

        Call<JsonObject> call = RetrofitUtils.getInstance().getMaintenanceApproval(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i("POLLING","[GetNewInfoService] getMaintenanceApproval");
                Intent intent = new Intent("NewFuelTopUpMsgReceiver");
                try {
                    Log.i("Main","getMaintenanceApproval(): "+response.body().toString());
                    JSONObject json = new JSONObject(response.body().toString());
                    int resp_code = json.getInt("resp_code");
                    if (resp_code == 0){
                        return;
                    }
                    String resp_msg = json.getString("resp_msg");
                    if (resp_msg == null||resp_msg.equals("")){
                        return;
                    }
                    if (resp_msg.length()>0){
                        JSONObject jsonObject = new JSONObject(resp_msg);
                        FuelTopUp status = new FuelTopUp(jsonObject);
                        intent.putExtra("fuel_top_up_status",status);
                    }
                } catch(JSONException e){ e.printStackTrace(); }

                Log.i("BROADCAST", "sending broadcast: NewFuelTopUpMsgReceiver");
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("Main","[getMaintenanceApproval] FAILED: " + t.getMessage().toString());
            }
        });
    }

    private boolean isPOCUpdated(NewIncident incident) {
        return (!incident.getPOC_Contact().equalsIgnoreCase(application.getPOC_Contact()) || !incident.getPOC_Name().equalsIgnoreCase(application.getPOC_Name())
            || !incident.getPOC_Unit().equalsIgnoreCase(application.getPOC_Unit()));
    }

    //ZN - 20210711
    private void ackUpdatePOC() {
        Map<String,String> params = new HashMap<>();
        params.put("incidentId", application.getCurrentIncidentID());
        params.put("userId", CacheUtils.getUser(this).getId());

        Call<JsonObject> call = RetrofitUtils.getInstance().resetUpdatePOC(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    Log.i("SERVER","[GetNewInfoService] ackUpdatePOC");
                    JSONObject json = new JSONObject(response.body().toString());
                    int resp_code = json.getInt("resp_code");
                    if (resp_code == 1){
                        Log.i("SERVER","[GetNewInfoService] ackUpdatePOC reset updatePOC flag");
                        return;
                    }
                } catch(JSONException e){ e.printStackTrace(); }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    //ZN - 20210719 method to check if backup EAS activated
    private void getBackupEASActivated() {
        Map<String,String> params = new HashMap<>();
        params.put("activation_location", CacheUtils.getUser(this).getActivationLocation());
        Log.i("POLLING", "getBackupEASActivated: " + CacheUtils.getUser(this).getActivationLocation());

        Call<JsonObject> call = RetrofitUtils.getInstance().getBackupEASActivated(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i("POLLING","[GetNewInfoService] getBackupEASActivated");
                Intent intent = new Intent("NewBackupEASMsgReceiver");
                Boolean resp_msg = new Boolean(false);
                try {
                    Log.i("Main","getBackupEASActivated(): "+ response.body().toString());
                    JSONObject json = new JSONObject(response.body().toString());
                    int resp_code = json.getInt("resp_code");
                    if (resp_code == 0){
                        return;
                    }

                    resp_msg = json.getBoolean("resp_msg");
                    intent.putExtra("backupEAS_status", resp_msg);

                } catch(JSONException e){ e.printStackTrace(); }

                Log.i("BROADCAST", "sending broadcast: NewBackupEASMsgReceiver");
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("Main","[getBackupEASActivated] FAILED: " + t.getMessage().toString());
            }
        });
    }

    //ZN - 20210819 method to retrieve EAS user status (standby / activated / offline)
    public void getEASUserStatusFromServer() {
        //ZN - 20211124 get camp responder peers
        Map<String,String> params = new HashMap<>();
        params.put("type", user.getType());
        params.put("username", user.getUsername());

        Call<JsonObject> call = RetrofitUtils.getInstance().getEASUserStatusByType(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject, jsonObjUserStatusList;
                HashMap<String, String> temp_userEASStatus = new HashMap<String, String>();

                try {
                    jsonObject = new JSONObject(response.body().toString());
                    String resp_msg = jsonObject.getString("resp_msg");
                    Log.i("POLLING", "[getEASUserStatusFromServer] string" + resp_msg);

                    JSONArray ja = new JSONArray(resp_msg);
                    for (int i=0; i<ja.length(); i++) {
                        Log.i("POLLING", "[getEASUserStatusFromServer] " + ja.getJSONObject(i).getString("username") + " " + ja.getJSONObject(i).getString("status"));
                        temp_userEASStatus.put(ja.getJSONObject(i).getString("username"), ja.getJSONObject(i).getString("status"));
                    }

                    MyApplication.getInstance().setUserEASStatus(temp_userEASStatus);
                } catch (JSONException e) {
                    Log.i("POLLING", "[getEASUserStatusFromServer] exception: " + e);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("Main","[getEASUserStatusFromServer] FAILED: " + t.getMessage().toString());
            }
        });

    }

    //ZN - 20220125 consolidate peer readiness method call
    public void getPeerReadinessFromServer() {
        //ZN - 20211124 get camp responder peers
        Map<String,String> params = new HashMap<>();
        params.put("type", user.getType());
        params.put("username", user.getUsername());
        params.put("activation_location", CacheUtils.getUser(this).getActivationLocation());

        Call<JsonObject> call = RetrofitUtils.getInstance().getPeerReadinessStatus(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Intent intent = new Intent("NewPeerReadinessMsgReceiver");
                JSONObject jsonObject, jsonObjUserStatusList;
                HashMap<String, String> temp_userEASStatus = new HashMap<String, String>();

                try {
                    jsonObject = new JSONObject(response.body().toString());
                    String resp_msg = jsonObject.getString("resp_msg");
                    Log.i("POLLING", "[getPeerReadinessFromServer] string" + resp_msg);

                    JSONArray ja = new JSONArray(resp_msg);

                    for (int i=0; i<ja.length(); i++) {
                        Log.i("POLLING", "[getPeerReadinessFromServer] " + ja.getJSONObject(i).getString("username") + " " + ja.getJSONObject(i).getString("status" ) + " " + ja.getJSONObject(i).getString("peer"));
                        temp_userEASStatus.put(ja.getJSONObject(i).getString("username"), ja.getJSONObject(i).getString("status") + ":" + ja.getJSONObject(i).getString("peer"));
                    }

                    //MyApplication.getInstance().setUserEASStatus(temp_userEASStatus);

                    intent.putExtra("data", temp_userEASStatus);
                } catch (JSONException e) {
                    Log.i("POLLING", "[getPeerReadinessFromServer] exception: " + e);
                }

                Log.i("BROADCAST", "sending broadcast: NewPeerReadinessMsgReceiver");
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("Main","[getPeerReadinessFromServer] FAILED: " + t.getMessage().toString());
            }
        });

    }

    //ZN - 20211201 cancel task assignment - method to poll for CANCELLED status
    private void getCancelledIncident() {
        Map<String,String> params = new HashMap<>();
        params.put("incident_id", application.getCurrentIncidentID());
        Call<JsonObject> call = RetrofitUtils.getInstance().getCancelledIncident(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Intent intent = new Intent("NewCancelledIncidentReceiver");
                String resp_msg;
                JSONObject jsonObject;

                try {
                    Log.i("POLLING","[GetNewInfoService] getCancelledIncident(): "+ response.body().toString());
                    jsonObject = new JSONObject(response.body().toString());
                    int resp_code = jsonObject.getInt("resp_code");
                    if (resp_code == 0){
                        return;
                    }

                    resp_msg = jsonObject.getString("resp_msg");
                    if (resp_msg.equalsIgnoreCase("CANCELLED"))
                        intent.putExtra("cancelled_incident", resp_msg);

                } catch(JSONException e){ e.printStackTrace(); }

                Log.i("BROADCAST", "sending broadcast: NewCancelledIncidentReceiver");
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("Main","[getCancelledIncident] FAILED: " + t.getMessage().toString());
            }
        });
    }

    //ZN - 20220606 store and forward
    private void updateActivationTiming() {
        Map<String,String> params = application.getActivationTimingsMap();
        if (params.size() == 0) {
            return;
        }

        Log.i("??CTIVATION", params.toString());
        //check if have completed activation timings
        //might replace with checking of number of timings captured and do "if not-empty" then loop through the hashmap and update db individually at server end
        //also clear getActivationTimingsMap upon rcv new incident

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
                        Log.i("SERVER", "[GetNewInfoService] updateActivationTiming updated");
                        if (params.containsKey("completed")) {
                            params.clear();


                        }
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

//        if (!params.containsKey("completed"))
//            return;
//        else {
//            Call<JsonObject> call = RetrofitUtils.getInstance().updateActivationTiming(params);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//                    try {
//                        Log.i("SERVER", "[GetNewInfoService] updateActivationTiming");
//                        JSONObject json = new JSONObject(response.body().toString());
//                        int resp_code = json.getInt("resp_code");
//                        if (resp_code == 1) {
//                            //clear all when server has rcv and recorded into db the activation timings
//                            Log.i("SERVER", "[GetNewInfoService] updateActivationTiming updated");
//                            params.clear();
//                            return;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                }
//            });
//        }

    }

//    //ZN - 20210922 method to check for incomplete incident
//    public void getIncompleteIncidentStatus() {
//        Map<String,String> params = new HashMap<>();
//        params.put("user_id","42");
//
//        Call<JsonObject> call = RetrofitUtils.getInstance().getIncompleteIncidentRecordByUserId(params);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(response.body().toString());
//                    JSONObject resp_msg = jsonObject.getJSONObject("resp_msg");
//                    int status = Integer.valueOf(resp_msg.getString("incompleteIncidentStatus"));
//                    Log.i("INCOMPLETE", "status: " + status);
//                    //ZN - 20210201 save username for display in main page
//                    MyApplication.getInstance().setIncompleteIncidentStatus(status);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//        });
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        statusCheckRunnable.cancel();
        timestampRunnable.cancel();

        Log.i("SERVICE", "[GetNewInfoService] onDestroy");
    }

}

