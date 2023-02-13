package com.example.st.arcgiscss.activites;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.dao.NewIncidentDBHelper;
import com.example.st.arcgiscss.model.IncidentLoc;
import com.example.st.arcgiscss.model.LocationType;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.CornerUtil;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncidentActivity extends BaseActivity {
    @D3View(click="onClick")
    ImageView iv_addimage;
    @D3View(click="onClick")
    LinearLayout ll_descript;
    @D3View(click="onClick")
    Button btn_incident;
    @D3View
    LinearLayout ll_root;
    @D3View
    EditText et_descript,et_casualties, et_rank_name, et_unit, et_contact;
    @D3View
    MaterialSpinner sp_incident_type, sp_location, sp_location_name, sp_condition;
    @D3View
    TextView tv_act_location, tv_dtg, tv_location;

    public Gson gson;

    private NewIncident incident;

//    private List<String> types = new ArrayList<>();
//    private List<String> locations = new ArrayList<>();
    private List<String> incidentType = new ArrayList<>();
    private List<String> incidentLocation = new ArrayList<>();
    private List<String> activationLocations = new ArrayList<>();
    private List<String> casualtyCondition = new ArrayList<>();


    private LocationType locationType;
    private String str_incident_point_lat;
    private String str_incident_point_lon;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acti_incident);

        incidentType.add("Intrusion");
        incidentType.add("Trespassing");
        incidentType.add("Public Order");

        incidentLocation.add("Location 1");
        incidentLocation.add("Location 2");
        incidentLocation.add("Location 3");

        activationLocations.add("Activated Location 1");
        activationLocations.add("Activated Location 2");
        activationLocations.add("Activated Location 3");

        casualtyCondition.add("Alert");
        casualtyCondition.add("Verbally Responsive");
        casualtyCondition.add("Unresponsive");

        str_incident_point_lat= (String) getIntent().getSerializableExtra("lat");
        str_incident_point_lon= (String) getIntent().getSerializableExtra("lon");

        initView();
        //initData();
    }

    private void initView() {
        tv_dtg.setText(getTime());

        sp_incident_type.setItems(incidentType);
        sp_incident_type.setPadding(40, 0, 0, 0);
        sp_incident_type.setDropdownMaxHeight(550);
        CornerUtil.clipViewCornerByDp(sp_incident_type,10);

        sp_location.setItems(incidentLocation);
        sp_location.setPadding(40, 0, 0, 0);
        sp_location.setDropdownMaxHeight(550);
        CornerUtil.clipViewCornerByDp(sp_location,10);

        sp_location_name.setItems(activationLocations);
        sp_location_name.setPadding(40, 0, 0, 0);
        sp_location_name.setDropdownMaxHeight(550);
        CornerUtil.clipViewCornerByDp(sp_location_name,10);

        sp_condition.setItems(casualtyCondition);
        sp_condition.setPadding(40, 0, 0, 0);
        sp_condition.setDropdownMaxHeight(550);
        CornerUtil.clipViewCornerByDp(sp_condition,10);

        btn_incident.setEnabled(true);

    }

    private void initData() {
        for (int i = 0; i < locationType.getRespMsg().size(); i++) {
            IncidentLoc loc = locationType.getRespMsg().get(i);
            if (loc.getIncidentLocation().equals(sp_location.getText().toString())){
                tv_act_location.setText(loc.getActivationLocation());
                return;
            }
        }

//        types = new ArrayList<>();
//        gson = new Gson();
//        locations = new ArrayList<>();
//        et_calendar.setText(incident.getCreate_time());
//        if (incident != null) {
//            et_coordinate.setText(incident.getLat() + "," + incident.getLon());
//        }
//        titleView.setText(incident.getTitle());
//        tv_report_time.setText(incident.getIncidentTime()==null?incident.getCreate_time():incident.getIncidentTime());
//        if (incident.getIncidentType() != null){
//            types.remove(incident.getIncidentType());
//            types.add(0,incident.getIncidentType());
//        }
//        if (incident.getLevel() != null){
//            levels.remove(incident.getLevel());
//            levels.add(0,incident.getLevel());
//        }

//        if (incident.getAddress()!=null){
//            et_address.setText(incident.getAddress());
//        }
//        if (incident.getIncidentName()!=null){
//            et_incident_name.setText(incident.getIncidentName());
//        }
//
//        if (incident.getDescription()!=null){
//            et_descript.setText(incident.getDescription());
//        }

//        Log.e("TEST",incident.getImages().toString()+"wwwwww");
    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_incident:
//               btn_incident.setEnabled(false);
               incident = new NewIncident();
               incident.setType(sp_incident_type.getText().toString());
               incident.setDescription(et_descript.getText().toString());
               incident.setIncidentLocation(sp_location.getText().toString());
               incident.setActivationLocation(sp_location_name.getText().toString());
               incident.setCasualties(et_casualties.getText().toString());
               incident.setIncidentCondition(sp_condition.getText().toString());
               incident.setTimestamp(tv_dtg.getText().toString());
               incident.setPOC_Name(et_rank_name.getText().toString());
               incident.setPOC_Unit(et_unit.getText().toString());
               incident.setPOC_Contact(et_contact.getText().toString());
               incident.setStatus("");
               incident.setLat(str_incident_point_lat);
               incident.setLng(str_incident_point_lon);

               uploadIncidentData();
               break;

            case R.id.ll_descript:
                showSoftInputFromWindow(et_descript);
                break;
        }
    }

    private void uploadIncidentData() {
        Log.i("INCIDENT", "uploadIncidentData");
        Map<String,String> params = new HashMap<>();
        params.put("unit", MyApplication.getInstance().getUsername());
        params.put("type",incident.getType());
        params.put("description",incident.getDescription());
        params.put("incidentCondition",incident.getIncidentCondition());
        params.put("casualties",incident.getCasualties());
        params.put("lat",str_incident_point_lat);
        params.put("lon",str_incident_point_lon);
        params.put("locationName",incident.getActivationLocation());
        params.put("LUPCode",incident.getActivationLocation());
        params.put("timestamp",incident.getTimestamp());
        params.put("dtg",incident.getTimestamp());
        params.put("LUPLat",str_incident_point_lat);
        params.put("LUPLon",str_incident_point_lon);
        params.put("userId", CacheUtils.getUserId(this));
        params.put("locationType", "5");
        params.put("rankName", incident.getPOC_Name());
        params.put("unit", incident.getPOC_Unit());
        params.put("contactNumber", incident.getPOC_Contact());
        params.put("POCName", incident.getPOC_Name());
        params.put("POCContact", incident.getPOC_Unit());
        params.put("POCUnit", incident.getPOC_Contact());

        Log.i("INCIDENT", params.toString());
        Call<ResponseBody> call = RetrofitUtils.getInstance().createExportIncident(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                incident.setId(Long.toString(timestamp.getTime()));
                NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(incident);
                finish();

                //                JSONObject json = null;
//                try {
//                    Log.i("HELP", "HELP: " + response.body().toString());
//                    json = new JSONObject(response.body().toString());
//                    int resp_code = json.getInt("resp_code");
//                    if (resp_code == 1) {
//                        Log.i("INCIDENT", "[uploadIncidentData] create incident success");
//                        //show incident has been updated
//                        showToast("Incident sent");
//                        String resp_msg = json.getString("resp_msg");
//
//                        //Log.i("HELP", "JSON string: " + incident_id);
//                        //incident.setId(incident_id);
//                        incident.setId("1");
//                        NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(incident);
////                        btn_incident.setEnabled(true);
//                        finish();
//                    } else {
//                        showToast("Failure to send Incident data");
//                        Log.i("INCIDENT", "[uploadIncidentData] create incident failed");
////                        btn_incident.setEnabled(true);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TEST",t.getMessage().toString());
                showToast("Failure to send Incident data");
//                btn_incident.setClickable(true);
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


    public void showSoftInputFromWindow(EditText editText){
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }


    public static boolean isReachable(String ip, int port, int timeout) {
        boolean isReachable = false;
        Socket socket = null;
        InetAddress inet = null;
        try {
            inet = InetAddress.getByName(ip);
            socket = new Socket();
            // Creates a socket address from an IP address and a port number
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(inet, port);
            socket.connect(endpointSocketAddr, timeout);
            Log.e("TEST", "SUCCESS - remote: " + inet.getHostAddress() + " port " + port);
            isReachable = true;
        } catch (IOException e) {
            Log.e("TEST", "FAILRE - remote: " + inet.getHostAddress() + " port " + port);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("TEST", "Error occurred while closing socket..");
                }
            }
        }
        return isReachable;
    }


}