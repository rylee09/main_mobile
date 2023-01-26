package com.example.st.arcgiscss.activites;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.google.gson.Gson;
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

public class WatchIncidentActivity extends BaseActivity {

    @D3View(click="onClick")
    LinearLayout ll_descript;

    @D3View(click="onClick")
    Button btn_incident;

    @D3View
    LinearLayout ll_root;

    @D3View
    EditText et_descript;

    @D3View
    TextView tv_act_location,tv_incident_type,tv_location,tv_casualties,tv_coc, tv_act_POCName,tv_act_POCContact ;

    //ZN - 20210410
    //ZN - 20210615 added activation time
    @D3View
    TextView tv_camp_location, tv_activation_time;


    public Gson gson;

    private NewIncident incident;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        //ZN - 20210410 incident location type will determine layout to use
        incident = (NewIncident) getIntent().getSerializableExtra("incident");
        if (incident.getLocationType().equalsIgnoreCase("4")) {
            //training area
            setContentView(R.layout.acti_w_incident);
        } else {
            //camp
            setContentView(R.layout.acti_w_incident_camp);
        }
        initView();
        initData();

        //ZN - 20210420 set if UI is ack mode or summary mode
        setMode();
    }

    private void initView() {

    }

    private void initData() {

        if (incident.getActivationLocation()!=null){
            tv_act_location.setText(incident.getActivationLocation());
        }
        if (incident.getLatLon()!=null){
            tv_location.setText(incident.getLatLon());
        }
        if (incident.getCasualties()!=null){
            tv_casualties.setText(incident.getCasualties());
        }
        if (incident.getType()!=null){
            tv_incident_type.setText(incident.getType());
        }

        if (incident.getIncidentCondition()!=null){
            tv_coc.setText(incident.getIncidentCondition());
        }

        if (incident.getDescription()!=null){
            et_descript.setText(incident.getDescription());
        }

        //ZN - 20210117
        if (incident.getPOC_Name() != null) {
            //ZN - 20210713 to show updated POC if have
            if (MyApplication.getInstance().getPOC_Name().equalsIgnoreCase(""))
                tv_act_POCName.setText(incident.getPOC_Name());
            else
                tv_act_POCName.setText(MyApplication.getInstance().getPOC_Name());
        }

        if (incident.getPOC_Contact() != null) {
            //ZN - 20210713 to show updated POC if have
            if (MyApplication.getInstance().getPOC_Contact().equalsIgnoreCase(""))
                tv_act_POCContact.setText(incident.getPOC_Contact());
            else
                tv_act_POCContact.setText(MyApplication.getInstance().getPOC_Contact());
        }

        //ZN - 20210615 for activation timing
        if (incident.getTimestamp() != null) {
            tv_activation_time.setText(incident.getTimestamp());
        }

        Log.i("TEST", "actual address: " + String.valueOf(incident.getActual_incident_address().isEmpty()));
        Log.i("TEST", "actual address: " + String.valueOf(incident.getActual_incident_address() == ""));
        Log.i("TEST", "actual address: " + String.valueOf(incident.getActual_incident_address().length() != 0));
        Log.i("TEST", "actual address: " + String.valueOf(incident.getActual_incident_address().equals("null")));

        //ZN - 20210410 extra field to populate inside camp location
        if (incident.getActual_incident_address() != "" && !incident.getActual_incident_address().equals("null")) {
            Log.i("TEST", "actual address: " + incident.getActual_incident_address());
            tv_camp_location.setText(incident.getActual_incident_address());
        }

    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_incident:
                //ZN - 20210420 differentiate between OK and Acknowledge
                if (btn_incident.getText().equals("Acknowledge")) {
                    setIncidentAck();
                    Toast.makeText(WatchIncidentActivity.this, "Incident Acknowledged", Toast.LENGTH_LONG).show();

                    //ZN - 20210711
                    MyApplication.getInstance().setCheckUpdatePOC(true);
                }

                //ZN - 20210503 cancel notification
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(100);

                finish();
                break;
        }
    }

    private void setIncidentAck() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String now = sdf.format(new Date());
        Map<String,String> params = new HashMap<>();
        String userId = CacheUtils.getUserId(getApplicationContext());
        params.put("incidentId",incident.getId());
        params.put("userId", userId);
        params.put("ack_time", now);
        Call<JsonObject> call = RetrofitUtils.getInstance().setIncidentAck(params);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        //ZN - 20210420 set incident ack true
        MyApplication.getInstance().setIncidentAck(true);
    }

    //ZN - 20210420 method to set either ack mode or summary mode
    private void setMode() {
        if (MyApplication.getInstance().isIncidentAck()) {
            btn_incident.setText("OK");
        } else {
            btn_incident.setText("Acknowledge");
        }
    }

}