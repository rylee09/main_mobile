package com.example.st.arcgiscss.activites;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TransportationNetworkDataset;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.DirectionManeuver;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.constant.Constants;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.dao.NewIncidentDBHelper;
import com.example.st.arcgiscss.model.IncidentLoc;
import com.example.st.arcgiscss.model.LocationType;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.model.NewUser;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.LogcatHelper;
import com.example.st.arcgiscss.util.MyAndroidUtil;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.example.st.arcgiscss.util.ToastUtil;
import com.example.st.arcgiscss.util.Tool;
import com.example.st.arcgiscss.util.TransInformation;
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private Context mContext;
    @D3View
    RelativeLayout rl_user;
    @D3View(click = "onClick")
    Button login;
    @D3View
    EditText vl_number,vl_pwd;
    @D3View
    MaterialSpinner ms_location;
    @D3View(click = "onClick")
    TextView select_ip, tv_version_number;
    @D3View
    CheckBox checkBox;
    private boolean isChecked = false;

    private List<String> locations;

    private String username;
    private String pwd;
    private String location;
    private String name;

    private LocationType locationType;

    //ZN - 20210420
    private static final String FILE_EXTENSION = ".mmpk";
    private static File extStorDir;
    private static String extSDCardDirName;
    private static String filename;
    private static String mmpkFilePath;
    private MobileMapPackage mapPackage;
    private TransportationNetworkDataset streetNetwork;
    private RouteTask routeTask;
    private static final int READ_PHONE_STATE = 100;

    //ZN - 20220619 logging to external file
    private boolean isPermissionGivenOnAppInstalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("EVENT", "[LoginActivity] onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        //ZN - 20210420 request for permission to read arcgis folders
        if (Build.VERSION.SDK_INT >= 23) {
            showContacts();
        }

        //ZN - 20210420 preload map in app startup
        //loadMap();

        isChecked = MyApplication.sharedPreferences.getBoolean(Constants.USER_CHECK, false);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setChecked(isChecked);
        name = MyApplication.sharedPreferences.getString(Constants.LOGIN_USER, null);
        pwd = MyApplication.sharedPreferences.getString(Constants.LOGIN_PWD, null);
        if(name!=null && pwd!=null) {
            location = "Activation Location A";
            Log.i("EVENT", "[LoginActivity] onCreate doLogin");
            doLogin(name,location,pwd);
        }


        //ZN - 20220709 standardise version number display
        String version_number = MyApplication.getInstance().getVersionNumber();
        tv_version_number.setText(version_number);

        //ZN - 20220208 delete apk upon first run of app
        File sdcard = Environment.getExternalStorageDirectory();
        //File file = new File(sdcard,"Download/Responder-v0.5.0.apk");
        File file = new File(sdcard,"Download/Responder-" + version_number + ".apk");
        if(file.exists())
            file.delete();

    }

    @Override
    protected void onResume() {
        Log.i("EVENT", "[LoginActivity] onResume");
        super.onResume();

//        //ZN - 20220208 check for valid IMEI
//        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("IS_IMEI_PASSED", false)) {
//            //if fail imei check, exit app immediately
//            Log.i("IMEI", "intent returned false");
//            finish();
//        } else {
//            Log.i("IMEI", "intent returned true");
//            if (CacheUtils.getIP(this) != null)
//            {
//                //initData();
//                init();
//            }
//        }

            if (CacheUtils.getIP(this) != null)
            {
                //initData();
                init();
            }
    }

    private void initData() {
        getIncidentLocationList();
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
                init();
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ToastUtil.showToast(LoginActivity.this,"Please check the registration plate number！");
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

    private void init(){
        Animation anim= AnimationUtils.loadAnimation(mContext, R.anim.login_anim);
        anim.setFillAfter(true);
        rl_user.startAnimation(anim);
        //Log.e("TEST",locations.size()+"~~~~~");
        //ms_location.setItems(locations);
        //ms_location.setDropdownMaxHeight(550);
        vl_number.setTransformationMethod(new TransInformation());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //ZN - 20220118 disable login button after click to prevent double NewMainActivity instance
                login.setEnabled(false);

                username = vl_number.getText().toString().toUpperCase();
                if (TextUtils.isEmpty(username)) {
                    Tool.initToast(LoginActivity.this, getString(R.string.register_name));
                    return;
                }
                pwd = vl_pwd.getText().toString();
                if (TextUtils.isEmpty(pwd)) {
                    Tool.initToast(LoginActivity.this, getString(R.string.register_pwd));
                    return;
                }
//                location = ms_location.getText().toString();
//                if (TextUtils.isEmpty(location)) {
//                    Tool.initToast(LoginActivity.this, getString(R.string.register_location));
//                    return;
//                }
                location = "Activation Location A";

                //ZN - 20220208 check for valid IMEI
                //checkIMEI();
                doLogin(username,location,pwd);
                break;
            case R.id.select_ip:
                Intent intent = new Intent(LoginActivity.this, SelectIPActivity.class);
                startActivity(intent);
                //finish();
                break;
        }
    }

    void doLogin(String username,String act_location,String pwd){
        Map<String,String> params = new HashMap<>();
        params.put("username",username);
        params.put("password",pwd);
        params.put("activationLocation",act_location);
        Call<JsonObject> call = RetrofitUtils.getInstance().driverLogin(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code")==-1){
                        showToast("Please check your account name and password.");
                        //ZN - 20220118 disable login button after click to prevent double NewMainActivity instance - enable back regardless of result to allow button click the next time
                        login.setEnabled(true);

                        //ZN - 20220619 logging to external file
                        Log.i("EVENT", "[doLogin] application log out to server failed");

                        return;
                    }

                    JSONObject resp_msg = jsonObject.getJSONObject("resp_msg");
                    Log.i("RESP", "[LoginActivity] msg: " + resp_msg);
                    User userType = new User(resp_msg);

                    CacheUtils.saveUsername(LoginActivity.this, username);
                    CacheUtils.saveUserId(LoginActivity.this,userType.getId()+"");
                    CacheUtils.saveActivationLocation(LoginActivity.this,act_location);
                    CacheUtils.saveUser(LoginActivity.this, userType);

                    //ZN - 20210606 token implementation
                    RetrofitUtils.TOKEN = userType.getToken();
                    Log.i("TOKEN", userType.getToken());

                    NewIncidentDBHelper.getInstance(getApplicationContext()).clear();
                    for (int i = 0; i < userType.getIncidentList().size(); i++) {
                        NewIncident newIncident = userType.getIncidentList().get(i);
                        NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(newIncident);
                    }

                    if(isChecked) {
                        MyAndroidUtil.editXmlByString(Constants.LOGIN_USER, username);
                        MyAndroidUtil.editXmlByString(Constants.LOGIN_PWD, pwd);
                    }
                    else {
                        MyAndroidUtil.removeXml(Constants.LOGIN_USER);
                        MyAndroidUtil.removeXml(Constants.LOGIN_PWD);
                    }

                    //ZN - 20220118 disable login button after click to prevent double NewMainActivity instance - enable back regardless of result to allow button click the next time
                    login.setEnabled(true);

                    Intent intent = new Intent(LoginActivity.this, NewMainActivity.class);
                    intent.putExtra("locations",locationType);
                 //   intent.putExtra("username", username);

                    startActivity(intent);

                    //getIncompleteIncidentStatus();

                    //ZN - 20210201 save username for display in main page
                    MyApplication.getInstance().setUsername(username);

                    //ZN - 20220619 logging to external file
                    Log.i("EVENT", "[doLogin] application log in to server success: " + username);

                    //finish();

                } catch (JSONException e) {
                    e.printStackTrace();

                    //ZN - 20220118 disable login button after click to prevent double NewMainActivity instance - enable back regardless of result to allow button click the next time
                    login.setEnabled(true);

                    //ZN - 20220619 logging to external file
                    Log.i("EVENT", "[doLogout] application log in to server failed");
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
                ToastUtil.showToast(LoginActivity.this,"Please check your account name and password.");

                //ZN - 20220118 disable login button after click to prevent double NewMainActivity instance - enable back regardless of result to allow button click the next time
                login.setEnabled(true);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        checkBox.setChecked(isChecked);
        this.isChecked = isChecked;
        MyAndroidUtil.editXml(Constants.USER_CHECK, isChecked);
        if (isChecked) {
            checkBox.setButtonDrawable(R.mipmap.login_checked);
        } else {
            checkBox.setButtonDrawable(R.mipmap.login_check);
        }
    }

    @Override
    protected void onPause() {
        Log.i("EVENT","[LoginActivity] onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.i("EVENT","[LoginActivity] onDestroy");
        super.onDestroy();
    }

    //ZN - 20210420 preload map in app startup
    private void loadMap() {
        //ZN - 20221123 ArcGIS Online services
//        ArcGISRuntimeEnvironment.setApiKey("AAPK23813770375044178267d461067ee23b9L4PMGZPsYaf3OIOxa0Xvb36yZI45f70t2WalUDGgEePBQ7_M7yFpY42WPaex-0F");
//        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_NAVIGATION_NIGHT);
//        MyApplication.getInstance().setMap(map);
//        routeTask = new RouteTask(LoginActivity.this, "https://route-api.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World");
//        MyApplication.getInstance().setRouteTask(routeTask);

//        Point startPoint = new Point(114.98232961649391, 4.980972501376365, SpatialReferences.getWgs84());
//        Point endPoint = new Point(114.93978404274576, 4.941093149612689, SpatialReferences.getWgs84());
//        List<Stop> stops = Arrays.asList(new Stop(startPoint), new Stop(endPoint));
//        final ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
//        routeParametersFuture.addDoneListener(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Log.i("ROUTE", "fixed route parameters created " + routeParametersFuture.isDone());
//
//                    // define the route parameters
//                    RouteParameters routeParameters = routeParametersFuture.get();
//                    routeParameters.setStops(stops);
//                    routeParameters.setReturnDirections(true);
//                    routeParameters.setReturnStops(true);
//                    routeParameters.setReturnRoutes(true);
//                    routeParameters.setOutputSpatialReference(SpatialReferences.getWgs84());
//
//                    Log.i("ROUTE", "ready to run solve fixed route task");
//                    ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
//                    routeResultFuture.addDoneListener(() -> {
//                        try {
//                            Log.i("ROUTE", "Fixed Route results completed");
//
//                            // get the route geometry from the route result
//                            RouteResult routeResult = routeResultFuture.get();
//                            //startNavigation(routeTask,routeParameters,routeResult);
//                            Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();
//
//                            Route retRoute = routeResult.getRoutes().get(0);
//                            List<DirectionManeuver> retDirections = retRoute.getDirectionManeuvers();
//                                for (DirectionManeuver directionManeuver : retDirections) {
//                                    Log.i("Directions", directionManeuver.getDirectionText());
//                                }
//
//
//                        } catch (ExecutionException | InterruptedException e) {
//                            String error = "Error solving fixed route: " + e.getMessage();
//                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
//                            Log.e("ROUTE", error);
//                        }
//                    });
//                } catch (InterruptedException | ExecutionException e) {
//                    String error = "Error creating the fixed route parameters " + e.getMessage();
//                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
//                    Log.e("ROUTE", error);
//                }
//            }
//        });

        extStorDir = Environment.getExternalStorageDirectory();
        extSDCardDirName = this.getResources().getString(R.string.config_data_sdcard_offline_dir);
        filename = this.getResources().getString(R.string.singapore_mmpk);
        mmpkFilePath = createMobileMapPackageFilePath();
        mapPackage = new MobileMapPackage(mmpkFilePath);
        mapPackage.loadAsync();
        mapPackage.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                    MyApplication.getInstance().setMobileMapPackage(mapPackage);
                    TransportationNetworkDataset transportationNetwork = mapPackage.getMaps().get(0).getTransportationNetworks().get(0);

                    //ZN- 20210918
                    List<TransportationNetworkDataset> lst_dataset = mapPackage.getMaps().get(0).getTransportationNetworks();
                    for (TransportationNetworkDataset t : lst_dataset) {
                        Log.i("DATASET", "Dataset name: " + t.getName());
                    }

                    routeTask = new RouteTask(LoginActivity.this,transportationNetwork);
                    routeTask.loadAsync();

                    //ZN - 20210415
                    routeTask.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("MAP", "[LoginActivity] routeTask done loading");
                            MyApplication.getInstance().setRouteTask(routeTask);
                        }
                    });


                }
        });
    }

    private static String createMobileMapPackageFilePath() {
        return extStorDir.getAbsolutePath() + File.separator + extSDCardDirName + File.separator + filename
                + FILE_EXTENSION;
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
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SYSTEM_ALERT_WINDOW
            }, READ_PHONE_STATE);
        } else {
            Log.e("PERMISSION", "PERMISSION GRANTED!!!!");

            //ZN - 20210819
            loadMap();

            //ZN - 20220619 logging to external file
            LogcatHelper.getInstance(getApplicationContext()).start();

        }
    }

    //ZN - 20210819
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == READ_PHONE_STATE) {
            if (!Arrays.asList(grantResults).contains(PackageManager.PERMISSION_DENIED) && !isPermissionGivenOnAppInstalled) {
                //all permissions have been granted
                loadMap();

                //ZN - 20220619 logging to external file
                LogcatHelper.getInstance(getApplicationContext()).start();
                isPermissionGivenOnAppInstalled = true;

                //ZN - 20220711 prompt to allow battery optimisation
//                Intent intent_pow = new Intent();
//                String packageName = getApplicationContext().getPackageName();
//                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//                intent_pow.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
//                Log.i("BATTERY_OP", "ACTION_REQUEST");
//                getApplicationContext().startActivity(intent_pow);

            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i("BACK", "[LoginActivity] back button pressed");
        //android.os.Process.killProcess(android.os.Process.myPid());
        finishAffinity();
    }

    //ZN - 20210922 method to check for incomplete incident
    public void getIncompleteIncidentStatus() {
        Map<String,String> params = new HashMap<>();
        params.put("user_id",CacheUtils.getUserId(this));
        //params.put("user_id","42");
        Call<JsonObject> call = RetrofitUtils.getInstance().getIncompleteIncidentRecordByUserId(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject != null) {
                        Log.i("INCOMPLETE", "jsonStr: " + jsonObject);
                        JSONObject resp_msg = jsonObject.getJSONObject("resp_msg");
                        int status = Integer.valueOf(resp_msg.getString("incompleteIncidentStatus"));
                        Log.i("INCOMPLETE", "status: " + status);

                        //ZN - 20210201 save username for display in main page
                        MyApplication.getInstance().setIncompleteIncidentStatus(status);
                        //finish();
                    }

                    Intent intent = new Intent(LoginActivity.this, NewMainActivity.class);
                    intent.putExtra("locations",locationType);
                    //   intent.putExtra("username", username);

                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    //ZN - 20220208 check for valid IMEI
    public void checkIMEI() {
        Map<String, String> params = new HashMap<>();
//        params.put("imei", str_imei);
        params.put("imei", MyApplication.getInstance().getImei());

        Call<JsonObject> call = RetrofitUtils.getInstance().getMatchingIMEI(params);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().toString());
                    if (jsonObject.getInt("resp_code") == 1) {
                        //IMEI matched, continue login
                        Log.i("IMEI", "imei matched!");
                        doLogin(username, location, pwd);
                    } else if (jsonObject.getInt("resp_code") == -1) {
                        //IMEI dont match, close app
                        Log.i("IMEI", "imei not matched!");
                        finishAndRemoveTask();
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

}
