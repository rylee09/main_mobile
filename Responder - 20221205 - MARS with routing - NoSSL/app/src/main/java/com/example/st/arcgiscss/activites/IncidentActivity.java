package com.example.st.arcgiscss.activites;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

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
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

public class IncidentActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{
    @D3View(click = "onClick")
    ImageView iv_addimage;
    @D3View(click = "onClick")
    LinearLayout ll_descript;
    @D3View(click = "onClick")
    Button btn_incident;
    @D3View
    LinearLayout ll_root;
    @D3View
    EditText et_descript, et_casualties, et_rank_name, et_unit, et_contact;
    @D3View
    MaterialSpinner sp_incident_type, sp_location, sp_location_name, sp_condition;
    @D3View
    TextView tv_act_location, tv_dtg, tv_location, tv_updatedDesc, showLocation;

    public Gson gson;

    private NewIncident incident;

    String location;

    //    private List<String> types = new ArrayList<>();
//    private List<String> locations = new ArrayList<>();
    private List<String> incidentType = new ArrayList<>();
    private List<String> incidentLocation = new ArrayList<>();
    private List<String> activationLocations = new ArrayList<>();
    private List<String> casualtyCondition = new ArrayList<>();


    private String iType;
    private LocationType locationType;
    private String str_incident_point_lat;
    private String str_incident_point_lon;

    LocationManager locationManager;
    LocationListener locationListener;

    FusedLocationProviderClient client; //to call service provider
    LocationRequest mLocationRequest; // to request for location
    LocationCallback mLocationCallback; //to help get last location


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_incident);

        Spinner spinner = findViewById(R.id.sp_incident_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.incident_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        client = LocationServices.getFusedLocationProviderClient(this); //to declare client to use locationservice;
        showLocation = findViewById(R.id.sp_location);
        mLocationCallback = new LocationCallback(){
            public void onLocationResult(LocationResult locationResult){
                if(locationResult!=null){
                    Location data = locationResult.getLastLocation();
                    String msg =  "Lat: " + data.getLatitude() + "Lng: " + data.getLongitude();
                    showLocation.setText(msg);
                    location = data.getLatitude() + "," + data.getLongitude();
                }else{

                }
            }
        };

        if(checkPermission()==true){
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000); //not sure
            mLocationRequest.setFastestInterval(5000); //time to catch the location
            mLocationRequest.setSmallestDisplacement(100); //placement

            client.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        }else{
            String msg = "Permission not granted to retrieve location info";
            Toast.makeText(IncidentActivity.this,msg,Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(IncidentActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }
//        showLocation = findViewById(R.id.sp_location);
//        Spinner spinnerResponder = findViewById(R.id.sp_condition);
//        ArrayAdapter<CharSequence> adapterResponder = ArrayAdapter.createFromResource(this, R.array.incident_responder, android.R.layout.simple_spinner_item);
//        adapterResponder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerResponder.setAdapter(adapterResponder);
//        spinnerResponder.setOnItemSelectedListener(this);


        TextView showDate = findViewById(R.id.tv_dtg);
        SimpleDateFormat sdf = new SimpleDateFormat("'Date:'dd-MM-yyyy'\nTime:'HH:mm:ss z");
        // on below line we are creating a variable
        // for current date and time and calling a simple date format in it.
        String currentDateAndTime = sdf.format(new Date());
        // on below line we are setting current
        // date and time to our text view.
        showDate.setText(currentDateAndTime);
//        TextView showLocation = findViewById(R.id.sp_location);
//        getLocation();
//        Button getLocationBtn = findViewById(R.id.getLocationBtn);


//        getLocationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getLocation();
//            }
//        });
//        showLocation.setText("hello");





//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
//        spinnerAdapter.addAll("Fire","Extrication","Medical-based", "Intrusion", "Trespassing", "Drone", "Public Order", "Road Traffic Accident", "Ammunition/Firearms/Explosives");
//
//        Spinner spinner = findViewById(R.id.sp_incident_type);
//        spinner.setAdapter(spinnerAdapter);
//        String[] arraySpinner = new String[]{
//                "Fire","Trespassing"
//        };
//        MaterialSpinner ms = findViewById(R.id.sp_incident_type);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner);
//        ms.setAdapter(adapter);
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.acti_incident, new String[]{"Item 1", "Item 2", "Item 3"});
//        Spinner spinner = findViewById(R.id.sp_incident_type);
//        spinner.setAdapter(spinnerAdapter);


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

//        initView();
        //initData();

        System.out.println("category selected");
    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                IncidentActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                IncidentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
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
//
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
//
//        Log.e("TEST",incident.getImages().toString()+"wwwwww");
    }

    public void onClick(View v){

        switch (v.getId()) {
            case R.id.btn_incident:

//               btn_incident.setEnabled(false);
               incident = new NewIncident();
//               incident.setType(sp_incident_type.getText().toString());
//               incident.setDescription(et_descript.getText().toString());
//               incident.setIncidentLocation(sp_location.getText().toString());
//               incident.setActivationLocation(sp_location_name.getText().toString());
//               incident.setCasualties(et_casualties.getText().toString());
//               incident.setIncidentCondition(sp_condition.getText().toString());
//               incident.setTimestamp(tv_dtg.getText().toString());
//               incident.setPOC_Name(et_rank_name.getText().toString());
//               incident.setPOC_Unit(et_unit.getText().toString());
//               incident.setPOC_Contact(et_contact.getText().toString());
//               incident.setStatus("");
//               incident.setLat(str_incident_point_lat);
//               incident.setLng(str_incident_point_lon);
//               incident.setRealTimeDescription(tv_updatedDesc.getText().toString());
//                createIncident();

                triggerFromMobile();
//               uploadIncidentData();
               break;

            case R.id.ll_descript:
                showSoftInputFromWindow(et_descript);
                break;
        }
    }


//    void getLocation(){
//        try{
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//
//        }catch (SecurityException e){
//            e.printStackTrace();
//            System.out.println("error when showing location");
//        }
//    }
//    private void uploadIncidentData() {
//        Log.i("INCIDENT", "uploadIncidentData");
//        Map<String,String> params = new HashMap<>();
//        params.put("unit", MyApplication.getInstance().getUsername());
//        params.put("type",incident.getType());
//        params.put("description",incident.getDescription());
//        params.put("incidentCondition",incident.getIncidentCondition());
//        params.put("casualties",incident.getCasualties());
//        params.put("lat",str_incident_point_lat);
//        params.put("lon",str_incident_point_lon);
//        params.put("locationName",incident.getActivationLocation());
//        params.put("LUPCode",incident.getActivationLocation());
//        params.put("timestamp",incident.getTimestamp());
//        params.put("dtg",incident.getTimestamp());
//        params.put("LUPLat",str_incident_point_lat);
//        params.put("LUPLon",str_incident_point_lon);
//        params.put("userId", CacheUtils.getUserId(this));
//        params.put("locationType", "5");
//        params.put("rankName", incident.getPOC_Name());
//        params.put("unit", incident.getPOC_Unit());
//        params.put("contactNumber", incident.getPOC_Contact());
//        params.put("POCName", incident.getPOC_Name());
//        params.put("POCContact", incident.getPOC_Unit());
//        params.put("POCUnit", incident.getPOC_Contact());
//
//        Log.i("INCIDENT", params.toString());
//        Call<ResponseBody> call = RetrofitUtils.getInstance().createExportIncident(params);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                incident.setId(Long.toString(timestamp.getTime()));
//                NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(incident);
//                finish();
//
//                //                JSONObject json = null;
////                try {
////                    Log.i("HELP", "HELP: " + response.body().toString());
////                    json = new JSONObject(response.body().toString());
////                    int resp_code = json.getInt("resp_code");
////                    if (resp_code == 1) {
////                        Log.i("INCIDENT", "[uploadIncidentData] create incident success");
////                        //show incident has been updated
////                        showToast("Incident sent");
////                        String resp_msg = json.getString("resp_msg");
////
////                        //Log.i("HELP", "JSON string: " + incident_id);
////                        //incident.setId(incident_id);
////                        incident.setId("1");
////                        NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(incident);
//////                        btn_incident.setEnabled(true);
////                        finish();
////                    } else {
////                        showToast("Failure to send Incident data");
////                        Log.i("INCIDENT", "[uploadIncidentData] create incident failed");
//////                        btn_incident.setEnabled(true);
////                    }
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
//                showToast("Failure to send Incident data");
////                btn_incident.setClickable(true);
//            }
//        });
//
//    }

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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        iType = text;
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//        private void createIncident() {
//        Map<String,String> params = new HashMap<>();
////            params.put("type",incident.getType());
////            params.put("description",incident.getDescription());
////            params.put("incidentCondition",incident.getIncidentCondition());
////            params.put("casualties",incident.getCasualties());
////            params.put("LUPLat",str_incident_point_lat);
////            params.put("LUPLon",str_incident_point_lon);
////            params.put("locationName",incident.getActivationLocation());
//
////        params.put("Incident_id", incident.getId());
////        params.put("Responder_id",  CacheUtils.getUserId(this));
////        params.put("Location", tv_act_incidentview_camplocation.getText().toString());
////        params.put("currentTime", str_selectedcondtion);
////        params.put("desc", tv_act_incidentview_incidentdescription.getText().toString());
////        params.put("Description", et_descript.getText().toString());
////        params.put("Location", incident.getLatLon());
////        params.put("Casualty_Condition", "");
////        params.put("Remarks", et_act_incidentview_responderremarks.getText().toString());
//        //Log.i("TEST", "[UpdateIncidentActivity] msg: " + incident.getId() + " " + CacheUtils.getUserId(this) + " " + str_selectedcondtion + " " + tv_act_incidentview_incidentdescription.getText().toString()+ " " + tv_act_incidentview_camplocation.getText().toString() + " " + et_act_incidentview_responderremarks.getText().toString());
//
//        Call<JsonObject> call = RetrofitUtils.getInstance().createIncident(params);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
////                JSONObject jsonObject = null;
////
////                try {
////                    jsonObject = new JSONObject(response.body().toString());
////                    System.out.println("details" + jsonObject);
////                    if (jsonObject.getInt("resp_code") == -1) {
////                        //do nth
////                        System.out.println("WHATT?");
////                    }else{
////                        System.out.println("sini!!!");
////                        JSONObject resp_msg= new JSONObject(response.body().toString());
//////                            JSONObject desc= new JSONObject(resp_msg.toString());
////                        System.out.println(resp_msg);
////                        //resp_msg.toString();
//////                            JSONObject resp_msg = new JSONObject().put("response.body().toString()");
////                        JSONArray desc= resp_msg.getJSONArray("resp_msg");
////                        JSONObject desc1= desc.getJSONObject(0);
////                        System.out.println(desc1);
////
////
////                        return;
////                    }
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//                System.out.println("incident created");
//                showToast("Incident Created");
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
//                showToast("Failure to create Incident ");
//                finish();
//            }
//        });
//    }

    private void triggerFromMobile() {
        Map<String,String> params = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());
        params.put("Date",currentDateAndTime);
//        params.put("Incident_id", incident.getId());
//        params.put("Responder_id",  CacheUtils.getUserId(this));
        params.put("Location", location);
        params.put("Incident Category",iType);
//        params.put("currentTime", str_selectedcondtion);
//        params.put("desc", tv_act_incidentview_incidentdescription.getText().toString());
        params.put("Description", et_descript.getText().toString());
//        params.put("Location", incident.getLatLon());
//        params.put("Casualty_Condition", "");
//        params.put("Remarks", et_act_incidentview_responderremarks.getText().toString());

//            params.put("incident",incident.toString());


        //Log.i("TEST", "[UpdateIncidentActivity] msg: " + incident.getId() + " " + CacheUtils.getUserId(this) + " " + str_selectedcondtion + " " + tv_act_incidentview_incidentdescription.getText().toString()+ " " + tv_act_incidentview_camplocation.getText().toString() + " " + et_act_incidentview_responderremarks.getText().toString());

        Call<JsonObject> call = RetrofitUtils.getInstance().triggerFromMobile(params);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("helllllllllllllllo");

//                JSONObject jsonObject = null;
//
//                try {
//                    jsonObject = new JSONObject(response.body().toString());
//                    System.out.println("details" + jsonObject);
//                    if (jsonObject.getInt("resp_code") == -1) {
//                        //do nth
//                        System.out.println("WHATT?");
//                    }else{
//                        System.out.println("sini!!!");
//                        JSONObject resp_msg= new JSONObject(response.body().toString());
////                            JSONObject desc= new JSONObject(resp_msg.toString());
//                        System.out.println(resp_msg);
//                        //resp_msg.toString();
////                            JSONObject resp_msg = new JSONObject().put("response.body().toString()");
//                        JSONArray desc= resp_msg.getJSONArray("resp_msg");
//                        JSONObject desc1= desc.getJSONObject(0);
//                        System.out.println(desc1);
//
//
//                        return;
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                System.out.println("incident sent to operator");
                showToast("Incident Sent");
                finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("TEST",t.getMessage().toString());
                System.out.println("incident failed sent to operator");
                showToast("Failure to send Incident ");
                finish();
            }
        });
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        showLocation = findViewById(R.id.sp_location);
//        showLocation.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
//    }
//
//    @Override
//    public void onStatusChanged(String s, int i, Bundle bundle) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String s) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String s) {
//        Toast.makeText(IncidentActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
//    }
}