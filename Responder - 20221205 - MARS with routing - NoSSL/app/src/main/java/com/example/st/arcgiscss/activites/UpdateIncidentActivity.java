package com.example.st.arcgiscss.activites;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.model.LocationType;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateIncidentActivity extends BaseActivity {
    @D3View(click="onClick")
    Button btn_close_update;

    @D3View
    TextView tv_act_incidentview_incident_type, tv_act_incidentview_activation_time, tv_act_incidentview_incidentdescription;

//    @D3View
//    MaterialSpinner sp_casualtycondition;

    @D3View
    EditText et_act_incidentview_responderremarks;

    public Gson gson;

    private NewIncident incident;

    private LocationType locationType;

    //ZN - 20210616
    private List<String> lst_casualty_condition = new ArrayList<String>();
    private boolean isUpdate = false;
    private String str_selectedcondtion;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acti_update_incident);
        incident = (NewIncident) getIntent().getSerializableExtra("incident");
        initView();
    }

    private void initView() {

        if (incident.getActivationLocation()!=null){
//            tv_act_incidentview_location.setText(incident.getActivationLocation());
        }

//        if (incident.getIncidentLocation()!=null){
////            tv_act_incidentview_LUP.setText(incident.getIncidentLocation());
//        }

        if (incident.getCasualties()!=null){
//            tv_act_incidentview_noofcasualty.setText(incident.getCasualties());
        }

        if (incident.getType()!=null){
            tv_act_incidentview_incident_type.setText(incident.getType());
        }

        if (incident.getIncidentCondition()!=null){
//            tv_act_incidentview_casualtycondition.setText(incident.getIncidentCondition());
        }

        if (incident.getDescription()!=null){
            tv_act_incidentview_incidentdescription.setText(incident.getDescription());
        }

        if(incident.getLatLon()!=null){
//            tv_act_incidentview_camplocation.setText((incident.getLatLon()));
        }

        //ZN - 20210117
//        if (incident.getPOC_Name() != null) {
//            ZN - 20210713 to show updated POC if have
//            if (MyApplication.getInstance().getPOC_Name().equalsIgnoreCase(""))
//                tv_act_incidentview_POCName.setText(incident.getPOC_Name());
//            else
//                tv_act_incidentview_POCName.setText(MyApplication.getInstance().getPOC_Name());

//            tv_act_incidentview_POCName.setText(incident.getPOC_Name());
//        }

        if (incident.getPOC_Contact() != null) {
            //ZN - 20210713 to show updated POC if have
//            if (MyApplication.getInstance().getPOC_Contact().equalsIgnoreCase(""))
//                tv_act_incidentview_POCContact.setText(incident.getPOC_Contact());
//            else
//                tv_act_incidentview_POCContact.setText(MyApplication.getInstance().getPOC_Contact());

            //tv_act_incidentview_POCContact.setText(incident.getPOC_Contact());
        }

        //ZN - 20210615 for activation timing
        if (incident.getTimestamp() != null) {
            tv_act_incidentview_activation_time.setText(incident.getTimestamp());
        }

        //ZN - 20210118 fixed missing camp location field
        if (incident.getActual_incident_address() != null) {
//            tv_act_incidentview_camplocation.setText(incident.getActual_incident_address());
        }

        //ZN - 20210616 for casualty condition update
//        lst_casualty_condition.add("");
//        lst_casualty_condition.add("A - ALERT");
//        lst_casualty_condition.add("V - VERBAL RESPONSIVE");
//        lst_casualty_condition.add("P - RESPONSIVE TO PAIN");
//        lst_casualty_condition.add("U - UNRESPONSIVE");
//        sp_casualtycondition.setItems(lst_casualty_condition);
//        sp_casualtycondition.setPadding(40, 0, 0, 0);
//        sp_casualtycondition.setDropdownMaxHeight(550);
//        CornerUtil.clipViewCornerByDp(sp_casualtycondition,10);

//        sp_casualtycondition.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//
//                str_selectedcondtion = (String)item;
//                if (!str_selectedcondtion.equalsIgnoreCase("")) {
//                    isUpdate = true;
//                    btn_close_update.setText("Update");
//                } else {
//                    isUpdate = false;
//                    btn_close_update.setText("Close");
//                }
//            }
//        });
        et_act_incidentview_responderremarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isUpdate = false;
                btn_close_update.setText("Close");

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(et_act_incidentview_responderremarks.getText().toString().trim().length()>0){
                    isUpdate = true;
                    btn_close_update.setText("Update");

                }else{
                    isUpdate = false;
                    btn_close_update.setText("Close");
                }

            }
        });

    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_close_update:
                if (isUpdate) {
                    updateResponderIncidentInfo();
                } else {
                    //close activity
                    finish();
                }

                break;
        }
    }

    private void updateResponderIncidentInfo() {
        Map<String,String> params = new HashMap<>();
        params.put("Incident_id", incident.getId());
        params.put("Responder_id",  CacheUtils.getUserId(this));
//        params.put("Location", tv_act_incidentview_camplocation.getText().toString());
        //params.put("currentTime", str_selectedcondtion);
        //params.put("desc", tv_act_incidentview_incidentdescription.getText().toString());
        params.put("Location", incident.getLatLon());
        params.put("Casualty_Condition", "");
        params.put("Remarks", et_act_incidentview_responderremarks.getText().toString());




        //Log.i("TEST", "[UpdateIncidentActivity] msg: " + incident.getId() + " " + CacheUtils.getUserId(this) + " " + str_selectedcondtion + " " + tv_act_incidentview_incidentdescription.getText().toString()+ " " + tv_act_incidentview_camplocation.getText().toString() + " " + et_act_incidentview_responderremarks.getText().toString());

        Call<JsonObject> call = RetrofitUtils.getInstance().updateResponderIncidentInfo(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                showToast("Update Incident Success");
                finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("TEST",t.getMessage().toString());
                showToast("Failure to update Incident ");
                finish();
            }
        });
    }




    public static String date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getTime(){
//        long time = System.currentTimeMillis()/1000;
//        String  str=String.valueOf(time);

        //ZN - 20201213
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String now = sdf.format(new Date());

        return now;
    }

}