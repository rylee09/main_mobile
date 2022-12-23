package com.example.st.arcgiscss.activites;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TransportationNetworkDataset;
import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.navigation.DestinationStatus;
import com.esri.arcgisruntime.navigation.RouteTracker;
import com.esri.arcgisruntime.navigation.TrackingStatus;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.adapter.SearchAdapter;
import com.example.st.arcgiscss.constant.Constants;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.dao.DBOpenHelper;
import com.example.st.arcgiscss.model.LocationPointList;
import com.example.st.arcgiscss.model.MyPoint;
import com.example.st.arcgiscss.model.NewUser;
import com.example.st.arcgiscss.model.RouteWay;
import com.example.st.arcgiscss.model.TaskInfo;
import com.example.st.arcgiscss.model.TaskInfoMessage;
import com.example.st.arcgiscss.service.LocationService;
import com.example.st.arcgiscss.service.UploadLocationService2;
import com.example.st.arcgiscss.upload.UserCoordinate;
import com.example.st.arcgiscss.util.AnimationUtil;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.LocationUtil;
import com.example.st.arcgiscss.util.PointChange;
import com.example.st.arcgiscss.views.SelectLocationPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

//import com.example.st.patrollerpjt.dao.IncidentDBHelper;

//import com.example.st.patrollerpjt.util.OfflineLocationDataSource;

//import com.example.st.patrollerpjt.service.GetFriendsService;

//import com.mapbox.api.directions.v5.models.DirectionsRoute;
//import com.mapbox.geojson.Point;
//import com.mapbox.services.android.navigation.v5.utils.LocaleUtils;


public class TaskActivity extends BaseActivity {

    @D3View(click = "onClick")
    LinearLayout ll_menu_select,ll_search_btn,ll_offline,ll_self;
    @D3View
    LinearLayout ll_menue_main,ll_search,ll_select_task;
    @D3View
    ImageView iv_arrow;
    @D3View(click = "onClick")
    Button btn_head_left,btn_cancel_n;

    @D3View(click = "onClick")
    ImageView iv_arrive_edit, iv_return_to_base,
            iv_arrive_lup, iv_arrive_location,iv_self,

    iv_left_base, iv_left_lup, iv_left_location, iv_search_text;

    @D3View
    RelativeLayout rl_main;
    @D3View
    EditText et_search;

    @D3View
    ListView lv_search;



    @D3View
    LinearLayout nav_tip;

    @D3View
    TextView direction_tip;

    @D3View
    TextView tv_left_location, tv_arrive_location;

    @D3View
    MapView mMapView;
    private static final int BAIDU_READ_PHONE_STATE = 100;
    private MyApplication application;
    private MyPoint current;
    private LocationManager lm;
    private LocalReceiver receiver;
    private LocalBroadcastManager localBroadcastManager;
    //native
    private static final String FILE_EXTENSION = ".mmpk";
    private static File extStorDir;
    private static String extSDCardDirName;
    private static String filename;
    private static String mmpkFilePath;
    private MobileMapPackage mapPackage;

    private RouteTask localRouteTask;
    private OfflineLocationDataSource mSimulatedLocationDataSource;
    private LocationDisplay locationDisplay;

    private GraphicsOverlay mGraphicsOverlay;
    private GraphicsOverlay mOfflineOverlay;
    private GraphicsOverlay mRouteOverlay;
    private GraphicsOverlay mPolygonOverlay;

    private Graphic mRouteAheadGraphic;
    private Graphic mRouteAheadGraphic2;

    private TextToSpeech mTextToSpeech;
    private boolean mIsTextToSpeechInitialized = false;

//    private List<LocationInfo> locationInfos = new ArrayList<>();


    private SearchAdapter searchAdapter;

    private TaskInfoMessage taskInfoMessage;
    private ArrayList<TaskInfo> taskInfos;

    private NewUser user;

    private LocationPointList locationPointList;
//    private int selectLocitonType;

    private List<LocationPointList.LocationPoint> locationPoints = new ArrayList<>();


    private RouteTracker mRouteTracker;
    private TrackingStatus trackingStatus;
    private RouteResult routeResult;
    private RouteParameters routeParameters;

    private  LocationDisplay.LocationChangedListener locationListener;

    private RouteTracker.NewVoiceGuidanceListener newVocieListenr;

    private Point startPoint ;
    private Point endPoint;
    private  String linePart;
    private  String linePart1;
    private  int type = 0;

    private Graphic mPolylineGraphic;

    private LocationPointList.LocationPoint locationInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_new_task);
        application = MyApplication.getInstance();

        double latitude = 1.3434836900969456;
        double longitude = 103.85739813145422;
        current = new MyPoint(latitude + "", longitude + "");
        MyApplication.getInstance().setCurrentPoint(current);

//        UserCoordinate userCoordinate = new UserCoordinate();
//        userCoordinate.setUserId(CacheUtils.getUserId(this));
//        MyApplication.getInstance().setUserCoordinates(userCoordinate);
//        UserPosition userPosition = new UserPosition();
//        MyApplication.getInstance().setUserPosition(userPosition);
        initData();
        getLocationInfo();

        UserCoordinate userCoordinate = new UserCoordinate();
        MyApplication.getInstance().setUserCoordinates(userCoordinate);

        localBroadcastManager = application.getLocalBroadcastManager();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            showContacts();
        } else {
            startLocation();
        }

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Glide.with(TaskActivity.this).load(R.mipmap.uparrow).into(iv_arrow);
                ll_menue_main.setAnimation(AnimationUtil.moveToViewBottom());
                ll_menue_main.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()>0){
                    initSearchLocationData(s.toString());
                    lv_search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_search.getEditableText().length() >= 1) {
                    iv_search_text.setVisibility(View.VISIBLE);
                } else {
                    iv_search_text.setVisibility(View.GONE);
                    lv_search.setVisibility(View.GONE);
                }
            }
        });


    }

    private void getLocationInfo() {

        String locationStr = "{\"resp_code\":1,\"resp_msg\":[{\"location_name\":\"location1\",\"lat\":\"1.348269999632\",\"lng\":\"103.9246999996\"},{\"location_name\":\"location2\",\"lat\":\"1.3362452828449902\",\"lng\":\"103.91591299618003\"},{\"location_name\":\"location3\",\"lat\":\"1.367810000441\",\"lng\":\"103.9464100001\"}]}";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(locationStr);
            if (jsonObject.getInt("resp_code") == 0) {
                return;
            }
            locationPointList = new LocationPointList(jsonObject);
        } catch (JSONException e) {
                    e.printStackTrace();
        }
//        Map<String, String> params = new HashMap<>();
//        Call<JsonObject> call = RetrofitUtils.getInstance().selectLocation(params);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//        });
    }


    private void initData() {
//        user = CacheUtils.getUser(TaskActivity.this);
//        taskInfoMessage = (TaskInfoMessage) getIntent().getSerializableExtra("taskInfo");
////        taskInfoMessage.getTaskInfo().get(0).setCompleteTime("");
//        taskInfos  = taskInfoMessage.getTaskInfo();
//        initView();
    }



    private void initSearchLocationData(String s) {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.openDatabase();
        locationPoints = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select *, count(distinct name) " +
                "from osm_pois_location where name like ? GROUP BY name limit 15", new String[]{s+"%"});
        while (cursor.moveToNext()){
            locationPoints.add(new LocationPointList.LocationPoint
                    (cursor.getString(3),cursor.getString(6),
                            cursor.getString(5)));
        }
        Log.e("TEST",locationPoints.toString()+"~~~~~~~~~~~~");
        searchAdapter = new SearchAdapter(this,locationPoints);
        lv_search.setAdapter(searchAdapter);
    }

    private void initView() {
        updateTaskView();
        searchAdapter = new SearchAdapter(this,locationPoints);
        lv_search.setAdapter(searchAdapter);
        lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationPointList.LocationPoint locationPoint = locationPoints.get(position);

                ll_search.setVisibility(View.GONE);
                lv_search.setVisibility(View.GONE);
                drawLocationPoint(locationPoint);
            }
        });

//        case R.id.iv_arrive_lup:
//        if (signInInfos.contains("left_base")) {
//            signInInfos.add("arrive_lup");
//            Glide.with(TaskActivity.this).load(R.mipmap.left_base_grey).into(iv_left_base);
//        } else {
//            showToast("Please do it in order.");
//        }
//        break;
//        case R.id.iv_left_lup:
//        if (signInInfos.contains("arrive_lup")) {
//            signInInfos.add("left_lup");
//            Glide.with(TaskActivity.this).load(R.mipmap.arrive_lup_grey).into(iv_arrive_lup);
//            showSelectLocaitons();
//        } else {
//            showToast("Please do it in order.");
//        }
//        break;
//        case R.id.iv_arrive_location:
//        if (signInInfos.contains("left_lup")) {
//            signInInfos.add("arrive_location");
//            Glide.with(TaskActivity.this).load(R.mipmap.left_lup_grey).into(iv_left_lup);
//        } else {
//            showToast("Please do it in order.");
//        }
//        break;
//        case R.id.iv_left_location:
//        if (signInInfos.contains("arrive_location")) {
//            signInInfos.add("left_location");
//            Glide.with(TaskActivity.this).load(R.mipmap.arrive_evac_grey).into(iv_arrive_location);
//        } else {
//            showToast("Please do it in order.");
//        }
//        break;
//        case R.id.iv_return_to_base:
//        if (signInInfos.contains("left_location")) {
//            signInInfos.add("return_to_base");
//            Glide.with(TaskActivity.this).load(R.mipmap.left_evac_grey).into(iv_left_location);
//        } else {
//            showToast("Please do it in order.");
//        }
//        break;



    }

    private void drawLocationPoint(LocationPointList.LocationPoint locationPoint) {
        mRouteOverlay.getGraphics().clear();
        mOfflineOverlay.getGraphics().clear();
        mGraphicsOverlay.getGraphics().clear();
        Point point = PointChange.getMapPoint(new MyPoint(locationPoint.getLat(),locationPoint.getLng()));
        BitmapDrawable bd = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.end_icon);
        PictureMarkerSymbol endfy = new PictureMarkerSymbol(bd);
        endfy.setHeight(15);
        endfy.setWidth(15);
        endfy.loadAsync();
        Point endPoint = new Point(point.getX(), point.getY(), SpatialReferences.getWgs84());
        Graphic endgp = new Graphic(endPoint, endfy);
        mGraphicsOverlay.getGraphics().add(endgp);

        BitmapDrawable bd2 = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.new_end);
        PictureMarkerSymbol endfy2 = new PictureMarkerSymbol(bd2);
        endfy2.setHeight(34);
        endfy2.setWidth(22);
        endfy2.setOffsetY(20);
        endfy2.loadAsync();
        Point endPoint2 = new Point(point.getX(), point.getY(), SpatialReferences.getWgs84());
        Graphic endgp2 = new Graphic(endPoint2, endfy2);
        mGraphicsOverlay.getGraphics().add(endgp2);
        mMapView.setViewpointCenterAsync(endPoint2);
        locationInfo = locationPoint;
        drawAllLineWithNavigation(type-1);

//        callSeverUpdateTask(taskInfos.get(type),locationInfo);
//        if (locationInfo != null) {
//            callSeverChangeLocation(locationInfo);
//        }
//        drawTraveldLine(type-1);

//        if (selectLocitonType == 0){
//            callSeverLocaitonUpdateTask(taskInfos.get(2),locationPoint);
//        }else {
//            callSeverChangeLocation(locationPoint);
//        }

    }

    private void updateTaskView() {
        TaskInfo taskInfo1 = taskInfos.get(0);
        if (!taskInfo1.getCompleteTime().equals("")){
            Glide.with(TaskActivity.this).load(R.mipmap.left_base_grey).into(iv_left_base);
            type = 1;
        }
        TaskInfo taskInfo2 = taskInfos.get(1);
        if (!taskInfo2.getCompleteTime().equals("")){
            Glide.with(TaskActivity.this).load(R.mipmap.arrive_lup_grey).into(iv_arrive_lup);
            type = 2;
        }
        TaskInfo taskInfo3 = taskInfos.get(2);
        if (!taskInfo3.getCompleteTime().equals("")){
//            selectLocitonType = 1;
            Glide.with(TaskActivity.this).load(R.mipmap.left_lup_grey).into(iv_left_lup);
            type = 3;
        }else {
//            selectLocitonType = 0;
        }
        TaskInfo taskInfo4 = taskInfos.get(3);
        if (!taskInfo4.getCompleteTime().equals("")){
            Glide.with(TaskActivity.this).load(R.mipmap.arrive_evac_grey).into(iv_arrive_location);
            type = 5;
        }
        TaskInfo taskInfo5 = taskInfos.get(4);
        if (!taskInfo5.getCompleteTime().equals("")){
            Glide.with(TaskActivity.this).load(R.mipmap.left_evac_grey).into(iv_left_location);
            type = 6;
        }
        TaskInfo taskInfo6 = taskInfos.get(5);
        if (!taskInfo6.getCompleteTime().equals("")){
            Glide.with(TaskActivity.this).load(R.mipmap.arrive_base_grey).into(iv_return_to_base);
            type = 7;
        }
        tv_arrive_location.setText(taskInfo4.getName());
        tv_left_location.setText(taskInfo5.getName());




    }

    private void getNavgaitonAllLine(int type0) {
        TaskInfo taskInfo = taskInfos.get(type0);
        startPoint = new Point(Double.valueOf(taskInfo.getLng()),
                    Double.valueOf(taskInfo.getLat()), SpatialReferences.getWgs84());
        TaskInfo taskInfo1 = taskInfos.get(type0+1);
        endPoint = new Point(Double.valueOf(taskInfo1.getLng()),
                    Double.valueOf(taskInfo1.getLat()), SpatialReferences.getWgs84());
        startNavigationWithLine();
    }


    private void getNavgaitonLine(int type2) {
//        TaskInfo taskInfo = taskInfos.get(type);
        startPoint = new Point(Double.valueOf(MyApplication.getInstance().
                getCurrentPoint().getLng()),
                Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()),
                SpatialReferences.getWgs84());
        if (locationInfo!= null&&(type==2||type==3)){
            endPoint = new Point(Double.valueOf(locationInfo.getLng()),
                    Double.valueOf(locationInfo.getLat()), SpatialReferences.getWgs84());
        }else {
            TaskInfo taskInfo1 = taskInfos.get(type2+1);
            endPoint = new Point(Double.valueOf(taskInfo1.getLng()),
                    Double.valueOf(taskInfo1.getLat()), SpatialReferences.getWgs84());
        }
        startNavigationWithLine();

    }

//    private void drawTraveldLine(int type1, String all){
//        TaskInfo taskInfo = taskInfos.get(type1);
//        Point  traveldSP = new Point(Double.valueOf(taskInfo.getLng()),
//                Double.valueOf(taskInfo.getLat()),
//                SpatialReferences.getWgs84());
//        Point  traveldEP = new Point(Double.valueOf(MyApplication.getInstance().
//                getCurrentPoint().getLng()),
//                Double.valueOf(MyApplication.getInstance().
//                        getCurrentPoint().getLat()),SpatialReferences.getWgs84());
//        List<Stop> stops = new ArrayList<>();
//        Stop navigationStopfist = new Stop(traveldSP);
//        navigationStopfist.setType(Stop.Type.STOP);
//        stops.add(navigationStopfist);
//        Stop navigationStoplast = new Stop(traveldEP);
//        navigationStoplast.setType(Stop.Type.STOP);
//        stops.add(navigationStoplast);
//        ListenableFuture<RouteParameters> routeParametersFuture = localRouteTask.createDefaultParametersAsync();
//        routeParametersFuture.addDoneListener(() -> {
//            try {
//                // define the route parameters
//                RouteParameters  routePTraved = routeParametersFuture.get();
//                routePTraved.setStops(stops);
//                routePTraved.setReturnDirections(true);
//                routePTraved.setReturnRoutes(true);
//                routePTraved.setReturnStops(true);
//                routePTraved.setOutputSpatialReference(SpatialReferences.getWgs84());
//                ListenableFuture<RouteResult> routeResultFuture = localRouteTask.solveRouteAsync(routePTraved);
//                routeParametersFuture.addDoneListener(() -> {
//                    try {
//                        // get the route geometry from the route result
//                        RouteResult routeResult1 = routeResultFuture.get();
//                        Polyline routeGeometry = routeResult1.getRoutes().get(0).getRouteGeometry();
//                        if (routeGeometry.getParts().size()==0){
//                            getNavgaitonLine(type-1);
//                            return;
//                        }
//                        linePart1 = routeResult1.getRoutes().get(0).getRouteGeometry().toJson();
//                        // get the route's geometry from the route result
////                        mRouteAheadGraphic2 = new Graphic(routeGeometry,
////                                new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,  Color.GRAY, 5f));
////                        mRouteOverlay.getGraphics().add(mRouteAheadGraphic2);
//                        mRouteAheadGraphic = new Graphic(routeGeometry,
//                                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,  Color.GRAY, 5f));
//                        mRouteOverlay.getGraphics().add(mRouteAheadGraphic);
//                        // create a graphic (solid) to represent the route that's been traveled (initially empty)
//                        BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.start_icon);
//                        PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
//                        facilitySymbol.setHeight(15);
//                        facilitySymbol.setWidth(15);
//                        facilitySymbol.loadAsync();
//                        Point pinStarBluePoint = routeGeometry.getParts().get(0).getStartPoint();
//                        Graphic pinStarBlueGraphic = new Graphic(pinStarBluePoint, facilitySymbol);
//                        mGraphicsOverlay.getGraphics().add(pinStarBlueGraphic);
//                        getNavgaitonLine(type-1);
//
//                    } catch (ExecutionException | InterruptedException e) {
//                        String error = "Error creating default route parameters: " + e.getMessage();
//
//                    }
//                });
//            } catch (InterruptedException | ExecutionException e) {
//
//
//            } });
//    }





    private void getNavgaitonLinePoint(String firstLng,String firstlat,String lastLng,String lastlat) {
        startPoint = new Point(Double.valueOf(firstLng),
                Double.valueOf(firstlat), SpatialReferences.getWgs84());
        endPoint = new Point(Double.valueOf(lastLng),
                Double.valueOf(lastlat), SpatialReferences.getWgs84());
        startNavigationWithLine();

    }


    public void onResume() {
        super.onResume();
        mMapView.resume(); //needed for compass, my location overlays, v6.0.0 and up
        registerReceiver(receiver,
                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }


    public void onPause() {
        super.onPause();
        mMapView.pause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, LocationService.class));
        unregisterReceiver(receiver);
        localBroadcastManager.unregisterReceiver(receiver);
        super.onDestroy();

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
            // Request one (or more) permissions and provide an access code (user-defined) for callback returns
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, BAIDU_READ_PHONE_STATE);
        } else {
            startLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode is the declared permission acquisition code, which is passed in checkSelfPermission
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    // Obtain the permission and handle it accordingly (calling the positioning SDK should ensure that the relevant permissions are authorized, otherwise the positioning may fail)
                    startLocation();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Create the mobile map package file location and name structure
     */
    private static String createMobileMapPackageFilePath() {
        return extStorDir.getAbsolutePath() + File.separator + extSDCardDirName + File.separator + filename
                + FILE_EXTENSION;
    }

    private void loadMapDir() {
        // get sdcard resource name
        extStorDir = Environment.getExternalStorageDirectory();
        // get the directory
        extSDCardDirName = this.getResources().getString(R.string.config_data_sdcard_offline_dir);
        // get mobile map package filename
        filename = this.getResources().getString(R.string.singapore_mmpk);
        // create the full path to the mobile map package file
        mmpkFilePath = createMobileMapPackageFilePath();
        loadMobileMapPackage(mmpkFilePath);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_menu_select:
                showmenu();
                break;
            case R.id.ll_search_btn:
                if (!et_search.getText().toString().equals("")){
                    lv_search.setVisibility(View.VISIBLE);
                }else {
                    showToast("Information not found.");
                }
                break;
            case R.id.iv_left_base:
                orderTask(0);
                break;
            case R.id.iv_arrive_lup:
                orderTask(1);
                break;
            case R.id.iv_left_lup:
                if (taskInfos.get(2).getCompleteTime().equals("")
                        &&!taskInfos.get(1).getCompleteTime().equals("")) {
                    showSelectLocaitons();
                } else {
                    showToast("Please do it in order.");
                }
                break;
            case R.id.iv_arrive_location:
                locationInfo = null;
                type=4;
                orderTask(3);
                break;
            case R.id.iv_left_location:

                orderTask(4);
                break;
            case R.id.iv_return_to_base:
                orderTask(5);
                break;
            case R.id.iv_arrive_edit:
                if (!taskInfos.get(2).getCompleteTime().equals("")&&
                        taskInfos.get(3).getCompleteTime().equals("")) {
//                    selectLocitonType = 1;
                    showSelectLocaitons();
                }else {
                    showToast("Please do it in order.");
                }
                break;
            case R.id.iv_search_text:
                et_search.setText("");
                iv_search_text.setVisibility(View.GONE);
                lv_search.setVisibility(View.GONE);
                break;

            case R.id.btn_head_left:
                Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_offline:
                if (type!=7) {
                    drawAllLineWithNavigation(type - 1);
                }
                break;
            case R.id.btn_cancel_n:
                nav_tip.setVisibility(View.GONE);
                ll_select_task.setVisibility(View.VISIBLE);
                if (mSimulatedLocationDataSource != null){
                    mSimulatedLocationDataSource.onStop();
                }
                mTextToSpeech.stop();
                mTextToSpeech.shutdown();
                mTextToSpeech = null;
                break;


            case R.id.ll_self:
                moveToCurrent();
                break;
            case R.id.iv_self:
                moveToCurrent();
                break;
        }

    }

    public void moveToCurrent() {
//        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
    }

    private void drawAllLineWithNavigation(int type1) {
        mGraphicsOverlay.getGraphics().clear();
        mRouteOverlay.getGraphics().clear();
        mOfflineOverlay.getGraphics().clear();
        int starttype = type1;
        if (type>4){
            starttype = starttype-1;
        }
        TaskInfo taskInfo = taskInfos.get(starttype);
        Point traveldSP = new Point(Double.valueOf(taskInfo.getLng()),
                Double.valueOf(taskInfo.getLat()),
                SpatialReferences.getWgs84());
        Point traveldEP = new Point(Double.valueOf(MyApplication.getInstance().
                getCurrentPoint().getLng()),
                Double.valueOf(MyApplication.getInstance().
                        getCurrentPoint().getLat()), SpatialReferences.getWgs84());
        List<Stop> stops = new ArrayList<>();
        Stop navigationStopfist = new Stop(traveldSP);
        navigationStopfist.setType(Stop.Type.STOP);
        stops.add(navigationStopfist);
        Stop navigationStoplast = new Stop(traveldEP);
        navigationStoplast.setType(Stop.Type.STOP);
        stops.add(navigationStoplast);
        ListenableFuture<RouteParameters> routeParametersFuture = localRouteTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(() -> {
            try {
                // define the route parameters
                RouteParameters routePTraved = routeParametersFuture.get();
                routePTraved.setStops(stops);
                routePTraved.setReturnDirections(true);
                routePTraved.setReturnRoutes(true);
                routePTraved.setReturnStops(true);
                routePTraved.setOutputSpatialReference(SpatialReferences.getWgs84());
                ListenableFuture<RouteResult> routeResultFuture = localRouteTask.solveRouteAsync(routePTraved);
                routeParametersFuture.addDoneListener(() -> {
                    try {
                        // get the route geometry from the route result
                        RouteResult routeResult1 = routeResultFuture.get();
                        Polyline routeGeometry = routeResult1.getRoutes().get(0).getRouteGeometry();
                        if (routeGeometry.getParts().size()==0){
                            getNavgaitonLine(type-1);
                            return;
                        }
                        linePart1 = routeResult1.getRoutes().get(0).getRouteGeometry().toJson();
                        // get the route's geometry from the route result
//                        mRouteAheadGraphic2 = new Graphic(routeGeometry,
//                                new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,  Color.GRAY, 5f));
//                        mRouteOverlay.getGraphics().add(mRouteAheadGraphic2);
                        mRouteAheadGraphic = new Graphic(routeGeometry,
                                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,  Color.GRAY, 5f));
                        mRouteOverlay.getGraphics().add(mRouteAheadGraphic);
                        // create a graphic (solid) to represent the route that's been traveled (initially empty)
                        BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.start_icon);
                        PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
                        facilitySymbol.setHeight(15);
                        facilitySymbol.setWidth(15);
                        facilitySymbol.loadAsync();
                        Point pinStarBluePoint = routeGeometry.getParts().get(0).getStartPoint();
                        Graphic pinStarBlueGraphic = new Graphic(pinStarBluePoint, facilitySymbol);
                        mGraphicsOverlay.getGraphics().add(pinStarBlueGraphic);
                        Point OtherstartPoint = new Point(Double.valueOf(MyApplication.getInstance().
                                getCurrentPoint().getLng()),
                                Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()),
                                SpatialReferences.getWgs84());
                        int newtype = type1;
                        if (type<=4){
                            newtype = newtype+1;
                        }
                        TaskInfo taskInfo1 = taskInfos.get(newtype);
                        String lat = taskInfo1.getLat();
                        String lng = taskInfo1.getLng();
                        if ((type==3||type==2)&&locationInfo!=null){
                            lng = locationInfo.getLng();
                            lat = locationInfo.getLat();
                        }
                        Point OtherendPoint = new Point(Double.valueOf(lng),
                                Double.valueOf(lat), SpatialReferences.getWgs84());
                        List<Stop> otherstops = new ArrayList<>();
                        Stop othernavigationStopfist = new Stop(OtherstartPoint);
                        othernavigationStopfist.setType(Stop.Type.STOP);
                        otherstops.add(othernavigationStopfist);
                        Stop othernavigationStoplast = new Stop(OtherendPoint);
                        othernavigationStoplast.setType(Stop.Type.STOP);
                        otherstops.add(othernavigationStoplast);
                        startNavigationWithStart(otherstops);

                    } catch (ExecutionException | InterruptedException e) {
                        String error = "Error creating default route parameters: " + e.getMessage();

                    }
                });
            } catch (InterruptedException | ExecutionException e) {


            } });

    }

    private void orderTask(int ordernumber){
        Log.e("TEST","~~~~~~~~ordernumber~~"+taskInfos.get(ordernumber).getName());
        if (taskInfos.get(ordernumber).getCompleteTime().equals("")) {
            Log.e("TEST","~~~~~~~~ordernumber");
            callSeverUpdateTask(taskInfos.get(ordernumber),locationInfo);
        } else {
            showToast("Please do it in order.");
        }
    }


//    private void callSeverUpdateTaskServer(TaskInfo taskInfo,LocationPointList.LocationPoint locationPoint) {
//        String currentTime = System.currentTimeMillis()+"";
//        Map<String, String> params = new HashMap<>();
//        params.put("user_id", user.getId());
//        params.put("task_id", taskInfoMessage.getTaskId());
//        params.put("lng",taskInfo.getLng());
//        params.put("lat",taskInfo.getLat());
//        params.put("step",type+"");
//        params.put("location_name",taskInfo.getLocationName());
//        params.put("create_time",currentTime);
//        if (locationInfo!= null&&!locationInfo.getLng().equals("")){
//            params.put("select_location_name",locationPoint.getLocationName());
//            params.put("select_lat",locationPoint.getLat());
//            params.put("select_lng",locationPoint.getLng());
//        }
//
//        if (linePart!= null&&!linePart.equals("")){
//            params.put("line2",linePart);
//        }else {
//            params.put("line2","");
//        }
//        if (linePart1 != null&&!linePart1.equals("")){
//            params.put("line1",linePart1);
//        }else {
//            params.put("line1","");
//        }
//        Call<JsonObject> call = RetrofitUtils.getInstance().updateTask(params);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(response.body().toString());
//                    if (jsonObject.getInt("resp_code") == 0) {
//                        return;
//                    }
//                    JSONObject resp_msg = jsonObject.getJSONObject("resp_msg");
//                    taskInfoMessage = new TaskInfoMessage(resp_msg);
//                    taskInfos = taskInfoMessage.getTaskInfo();
//                    updateTaskView();
//                    linePart1 = "";
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//        });
//
//    }



    private void callSeverUpdateTask(TaskInfo taskInfo,LocationPointList.LocationPoint locationPoint) {
            String currentTime = System.currentTimeMillis()+"";
            Map<String, String> params = new HashMap<>();
            params.put("user_id", user.getId());
            params.put("task_id", taskInfoMessage.getTaskId());
            params.put("lng",taskInfo.getLng());
            params.put("lat",taskInfo.getLat());
            params.put("step",type+"");
            params.put("location_name",taskInfo.getLocationName());
            params.put("create_time",currentTime);
            if (locationInfo!= null&&!locationInfo.getLng().equals("")){
                params.put("select_location_name",locationPoint.getLocationName());
                params.put("select_lat",locationPoint.getLat());
                params.put("select_lng",locationPoint.getLng());
            }

            if (linePart!= null&&!linePart.equals("")){
                params.put("line2",linePart);
            }else {
                params.put("line2","");
            }
            if (linePart1 != null&&!linePart1.equals("")){
                params.put("line1",linePart1);
            }else {
                params.put("line1","");
            }
            //one click
        String responseStr = "{\"resp_code\":1,\"resp_msg\":{\"user_id\":\"1\",\"task_id\":1,\"create_time\":1616983789000,\"incident_id\":\"INC1610438936446\",\"task_info\":[{\"name\":\"Left Base\",\"lat\":\"1.319517164094376\",\"lng\":\"103.95610398514872\",\"location_name\":\"location1\",\"complete_time\":1617003980000},{\"name\":\"Arrive LUP\",\"lat\":\"1.3382799997324502\",\"lng\":\"103.92390000043942\",\"location_name\":\"location2\",\"complete_time\":\"\"},{\"name\":\"Left LUP\",\"lat\":\"1.3382799997324502\",\"lng\":\"103.92390000043942\",\"location_name\":\"location2\",\"complete_time\":\"\"},{\"name\":\"Arrive Evac Pt\",\"lat\":\"\",\"lng\":\"\",\"location_name\":\"\",\"complete_time\":\"\"},{\"name\":\"Left Evac Pt\",\"lat\":\"\",\"lng\":\"\",\"location_name\":\"\",\"complete_time\":\"\"},{\"name\":\"Return to Base\",\"lat\":\"1.319517164094376\",\"lng\":\"103.95610398514872\",\"location_name\":\"location1\",\"complete_time\":\"\"}]}}";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseStr);
            if (jsonObject.getInt("resp_code") == 0) {
                return;
            }
            JSONObject resp_msg = jsonObject.getJSONObject("resp_msg");
            taskInfoMessage = new TaskInfoMessage(resp_msg);
            taskInfos = taskInfoMessage.getTaskInfo();
            updateTaskView();
            linePart1 = "";
            if (type==1||type==3||type==6){
                nav_tip.setVisibility(View.VISIBLE);
                ll_select_task.setVisibility(View.GONE);
                if (mTextToSpeech == null) {
                    mTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
                        if (status != TextToSpeech.ERROR) {
                            mTextToSpeech.setLanguage(Resources.getSystem().getConfiguration().locale);
                            mIsTextToSpeechInitialized = true;
                        }
                    });
                }
                startNavigation(localRouteTask,routeParameters,routeResult);
            }
            if (type==2||type==5){
                mOfflineOverlay.getGraphics().clear();
                mRouteOverlay.getGraphics().clear();
                mGraphicsOverlay.getGraphics().clear();
                if (type==5){
                    getNavgaitonAllLine(4);
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


//            Call<JsonObject> call = RetrofitUtils.getInstance().updateTask(params);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                }
//            });

    }


    private void startNavigationWithLine() {
        ListenableFuture<RouteParameters> routeParametersFuture = localRouteTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(() -> {
            try {
                // define the route parameters
                routeParameters = routeParametersFuture.get();
                routeParameters.setStops(getStops());
                routeParameters.setReturnDirections(true);
                routeParameters.setReturnRoutes(true);
                routeParameters.setReturnStops(true);
                routeParameters.setOutputSpatialReference(SpatialReferences.getWgs84());
                ListenableFuture<RouteResult> routeResultFuture = localRouteTask.solveRouteAsync(routeParameters);
                routeParametersFuture.addDoneListener(() -> {
                    try {
                        // get the route geometry from the route result
//                        MyLog.e("TEST","33333333");
                        routeResult = routeResultFuture.get();
                        linePart = routeResult.getRoutes().get(0).getRouteGeometry().toJson();
                        if (locationInfo!= null){
                            Log.e("TEST","~~~~~~~~startNavigationWithLine~~~~"+taskInfos.get(type-1).getName());
                            callSeverUpdateTask(taskInfos.get(type-1),locationInfo);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        String error = "Error creating default route parameters: " + e.getMessage();

                    }
                });
            } catch (InterruptedException | ExecutionException e) {


            } });
    }

    private void startNavigationWithStart(List<Stop> lstops) {
        ListenableFuture<RouteParameters> routeParametersFuture = localRouteTask.createDefaultParametersAsync();
        routeParametersFuture.addDoneListener(() -> {
            try {
                // define the route parameters
                routeParameters = routeParametersFuture.get();
                routeParameters.setStops(lstops);
                routeParameters.setReturnDirections(true);
                routeParameters.setReturnRoutes(true);
                routeParameters.setReturnStops(true);
                routeParameters.setOutputSpatialReference(SpatialReferences.getWgs84());
                ListenableFuture<RouteResult> routeResultFuture = localRouteTask.solveRouteAsync(routeParameters);
                routeParametersFuture.addDoneListener(() -> {
                    try {
                        // get the route geometry from the route result
//                        MyLog.e("TEST","33333333");
                        routeResult = routeResultFuture.get();
                        linePart = routeResult.getRoutes().get(0).getRouteGeometry().toJson();
                        nav_tip.setVisibility(View.VISIBLE);
                        ll_select_task.setVisibility(View.GONE);
                        if (mTextToSpeech == null) {
                            mTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
                                if (status != TextToSpeech.ERROR) {
                                    mTextToSpeech.setLanguage(Resources.getSystem().getConfiguration().locale);
                                    mIsTextToSpeechInitialized = true;
                                }
                            });
                        }
                        if (locationInfo!=null){
                            Log.e("TEST","~~~startNavigationWithStart~~~"+taskInfos.get(type-1).getName());
                            callSeverUpdateTask(taskInfos.get(type-1),locationInfo);
                        }else {
                            startNavigation(localRouteTask, routeParameters, routeResult);
                        }

                    } catch (ExecutionException | InterruptedException e) {
                        String error = "Error creating default route parameters: " + e.getMessage();

                    }
                });
            } catch (InterruptedException | ExecutionException e) {


            } });
    }


    private List<Stop> getStops() {
        List<Stop> stops = new ArrayList<>();
        Stop navigationStopfist = new Stop(startPoint);
        navigationStopfist.setType(Stop.Type.STOP);
        stops.add(navigationStopfist);
        Stop navigationStoplast = new Stop(endPoint);
        navigationStoplast.setType(Stop.Type.STOP);
        stops.add(navigationStoplast);
        return stops;
    }

    private void startNavigation(RouteTask routeTask, RouteParameters routeParameters, RouteResult routeResult) {

        // get the route's geometry from the route result
        Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();
        mRouteAheadGraphic2 = new Graphic(routeGeometry,
                new SimpleLineSymbol(SimpleLineSymbol.Style.DASH,  Color.BLUE, 5f));
        mRouteOverlay.getGraphics().add(mRouteAheadGraphic2);
        mRouteAheadGraphic = new Graphic(routeGeometry,
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,  Color.BLUE, 5f));
        mRouteOverlay.getGraphics().add(mRouteAheadGraphic);
        // create a graphic (solid) to represent the route that's been traveled (initially empty)

        BitmapDrawable pinStarBlueDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.start_icon);
        PictureMarkerSymbol facilitySymbol = new PictureMarkerSymbol(pinStarBlueDrawable);
        facilitySymbol.setHeight(15);
        facilitySymbol.setWidth(15);
        facilitySymbol.loadAsync();
        Point pinStarBluePoint = routeGeometry.getParts().get(0).getStartPoint();
        Graphic pinStarBlueGraphic = new Graphic(pinStarBluePoint, facilitySymbol);
        mGraphicsOverlay.getGraphics().add(pinStarBlueGraphic);

        BitmapDrawable bd = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.end_icon);
        PictureMarkerSymbol endfy = new PictureMarkerSymbol(bd);
        endfy.setHeight(15);
        endfy.setWidth(15);
        endfy.loadAsync();
        Point endPoint = routeGeometry.getParts().get(0).getEndPoint();
        Graphic endgp = new Graphic(endPoint, endfy);
        mGraphicsOverlay.getGraphics().add(endgp);
//        route_endP = new Point(endPoint.getX(),endPoint.getY(),SpatialReferences.getWgs84());

        BitmapDrawable bd2 = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.new_end);
        PictureMarkerSymbol endfy2 = new PictureMarkerSymbol(bd2);
        endfy2.setHeight(35);
        endfy2.setWidth(22);
        endfy2.setOffsetY(20);
        endfy2.loadAsync();
        Point endPoint2 = routeGeometry.getParts().get(0).getEndPoint();
        Graphic endgp2 = new Graphic(endPoint2, endfy2);
        mGraphicsOverlay.getGraphics().add(endgp2);
        mSimulatedLocationDataSource = new OfflineLocationDataSource(routeGeometry);
        // set the simulated location data source as the loc    ation data source for this app
        locationDisplay.setLocationDataSource(mSimulatedLocationDataSource);
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        //        locationDisplay.setNavigationPointHeightFactor((float) 0.5);
        mRouteTracker = new RouteTracker(getApplicationContext(), routeResult, 0);
        mRouteTracker.enableReroutingAsync(routeTask, routeParameters,
                RouteTracker.ReroutingStrategy.TO_NEXT_WAYPOINT, false);


        newVocieListenr = new RouteTracker.NewVoiceGuidanceListener() {
            @Override
            public void onNewVoiceGuidance(RouteTracker.NewVoiceGuidanceEvent newVoiceGuidanceEvent) {
                    speakVoiceGuidance(newVoiceGuidanceEvent.getVoiceGuidance().getText());
                    direction_tip.setText(newVoiceGuidanceEvent.getVoiceGuidance().getText());
            }
        };
        locationListener = new LocationDisplay.LocationChangedListener() {
            @Override
            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
                ListenableFuture<Void> trackLocationFuture = mRouteTracker.trackLocationAsync(locationChangedEvent.getLocation());
                trackLocationFuture.addDoneListener(() -> {
                    mRouteTracker.addNewVoiceGuidanceListener(newVocieListenr);
                    trackingStatus = mRouteTracker.getTrackingStatus();
                    if (trackingStatus!= null) {
                        if (trackingStatus.getRouteProgress() != null&&trackingStatus.getRouteProgress().getRemainingGeometry() != null){
                            mRouteAheadGraphic2.setGeometry(trackingStatus.getRouteProgress().getRemainingGeometry());
                        }
                        if (trackingStatus.getDestinationStatus() == DestinationStatus.REACHED) {
                            // if there are more destinations to visit. Greater than 1 because the start point is considered a "stop"
                            if (mRouteTracker.getTrackingStatus().getRemainingDestinationCount() > 1) {
                                // switch to the next destination
                                mRouteTracker.switchToNextDestinationAsync();
                            } else {

                                mSimulatedLocationDataSource.onStop();
                                if (locationListener != null){
                                    locationDisplay.removeLocationChangedListener(locationListener);
                                }
                                if (mRouteTracker != null){
                                    mRouteTracker.cancelRerouting();
                                }
                            }
                        }
                    }
                });
            }
        };
        locationDisplay.addLocationChangedListener(locationListener);
        locationDisplay.startAsync();
        mSimulatedLocationDataSource.startAsync();

    }



    /**
     * Uses Android's text to speak to say the latest voice guidance from the RouteTracker out loud.
     */
    private void speakVoiceGuidance(String voiceGuidanceText) {
        if (mIsTextToSpeechInitialized && !mTextToSpeech.isSpeaking()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech.speak(voiceGuidanceText, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                mTextToSpeech.speak(voiceGuidanceText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }


//    private void callSeverLocaitonUpdateTask(TaskInfo taskInfo, LocationPointList.LocationPoint locationPoint) {
//            String currentTime = System.currentTimeMillis()+"";
//            Map<String, String> params = new HashMap<>();
//            params.put("user_id", user.getId());
//            params.put("task_id", taskInfoMessage.getTaskId());
//            params.put("lng",taskInfo.getLng());
//            params.put("lat",taskInfo.getLat());
//            params.put("location_name",taskInfo.getLocationName());
//            params.put("create_time",currentTime);
//            params.put("select_location_name",locationPoint.getLocationName());
//            params.put("select_lat",locationPoint.getLat());
//            params.put("select_lng",locationPoint.getLng());
//            params.put("line2",linePart);
//            if (linePart1!=null&& !linePart1.equals(""))
//            {
//                params.put("line1",linePart1);
//            }else {
//                params.put("line1","");
//            }
//            Call<JsonObject> call = RetrofitUtils.getInstance().updateTask(params);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response.body().toString());
//                        if (jsonObject.getInt("resp_code") == 0) {
//                            return;
//                        }
//                        Log.e("TEST","~~~~~~~"+jsonObject.toString());
//                        JSONObject resp_msg = jsonObject.getJSONObject("resp_msg");
//                        taskInfoMessage = new TaskInfoMessage(resp_msg);
//                        taskInfos = taskInfoMessage.getTaskInfo();
//                        updateTaskView();
//                        nav_tip.setVisibility(View.VISIBLE);
//                        ll_select_task.setVisibility(View.GONE);
//                        if (mTextToSpeech == null) {
//                            mTextToSpeech = new TextToSpeech(getApplicationContext(), status -> {
//                                if (status != TextToSpeech.ERROR) {
//                                    mTextToSpeech.setLanguage(Resources.getSystem().getConfiguration().locale);
//                                    mIsTextToSpeechInitialized = true;
//                                }
//                            });
//                        }
//                        startNavigation(localRouteTask,routeParameters,routeResult);
//
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
////        }
//
//
//
//    }

    private void showmenu() {
        if (ll_menue_main.getVisibility() == View.GONE) {
            Glide.with(TaskActivity.this).load(R.mipmap.min).into(iv_arrow);
            ll_menue_main.setAnimation(AnimationUtil.moveToViewLocation());
            ll_menue_main.setVisibility(View.VISIBLE);
        } else {
            Glide.with(TaskActivity.this).load(R.mipmap.uparrow).into(iv_arrow);
            ll_menue_main.setAnimation(AnimationUtil.moveToViewBottom());
            ll_menue_main.setVisibility(View.GONE);
        }
    }


    private void showSelectLocaitons() {

        SelectLocationPopupWindow spw = new SelectLocationPopupWindow(TaskActivity.this, locationsSelectListener);
        spw.initData(locationPointList.getLocationPoints());
        spw.showAtLocation(rl_main, Gravity.CENTER, 0, 0);

    }

    private SelectLocationPopupWindow.LocationsSelectListener locationsSelectListener = new SelectLocationPopupWindow.LocationsSelectListener() {
        @Override
        public void selectFromYES(LocationPointList.LocationPoint lInfo) {
            locationInfo = lInfo;
            String mLocaitonName = locationInfo.getLocationName();
            if (mLocaitonName.equals("Search More")) {
                showmenu();
                ll_search.setVisibility(View.VISIBLE);
                et_search.setFocusable(true);
                et_search.setFocusableInTouchMode(true);
                et_search.requestFocus();
                Timer timer = new Timer();
                timer.schedule(new TimerTask()   {
                    public void run() {
                        InputMethodManager inputManager = (InputMethodManager)et_search.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(et_search, 0);
                    }
                }, 998);
                return;
            }
            drawAllLineWithNavigation(type-1);
//            getNavgaitonLinePoint(taskInfos.get(2).getLng(),
//                    taskInfos.get(2).getLat(),locationInfo.getLng()
//            ,locationInfo.getLat());

        }
    };





    public class OfflineLocationDataSource extends LocationDataSource {

        private Polyline mRoute;

        private Point mCurrentLocation;

        private RouteWay mRouteWay;

        private Context mContext;

        private List<String> mWaypointLat = new ArrayList<>();

        public OfflineLocationDataSource(Polyline route, RouteWay routeWay, Context context) {
            mRoute = route;
            mRouteWay = routeWay;
            mContext = context;
        }

        private Timer mTimer;
        private double distance = 0.0;
        private static final double distanceInterval = .00025;

        public OfflineLocationDataSource(Polyline route) {
            mRoute = route;
        }


        public OfflineLocationDataSource() {
        }

        @Override
        public void onStop() {
            if (mTimer != null){
                mTimer.cancel();
                mTimer = null;
            }

        }

        @Override
        protected void onStart() {
            onStartCompleted(null);
            // start at the beginning of the route
            if (MyApplication.getInstance().getCurrentPoint()!= null) {
                UpdateLocation(new Location(new Point(Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLng()), Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()), SpatialReferences.getWgs84())));
            }
            if (mRoute != null) {
                mCurrentLocation = mRoute.getParts().get(0).getStartPoint();
                UpdateLocation(new LocationDataSource.Location(mCurrentLocation));
                mTimer = new Timer("OfflineLocationDataSource Timer", false);
                mTimer.scheduleAtFixedRate(new TimerTask() {
                                               @Override
                                               public void run() {
                                                   // get a reference to the previous point
                                                   Point previousPoint = mCurrentLocation;
                                                   // update current location by moving [distanceInterval] meters along the route
                                                   mCurrentLocation = GeometryEngine.createPointAlong(mRoute, distance);
                                                   DecimalFormat df = new DecimalFormat("######0.0000000");
                                                   mCurrentLocation = new Point(Double.valueOf(df.format(mCurrentLocation.getX()))
                                                           , Double.valueOf(df.format(mCurrentLocation.getY()))
                                                           , SpatialReferences.getWgs84());
                                                   MyApplication.getInstance().setCurrentPoint(PointChange.getMyPointFromPoint(mCurrentLocation));
                                                   // get the geodetic distance result between the two points
                                                   GeodeticDistanceResult distanceResult = GeometryEngine.distanceGeodetic(previousPoint, mCurrentLocation,
                                                           new LinearUnit(LinearUnitId.METERS), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);

                                                   updateLocation(new LocationDataSource.Location(mCurrentLocation, 1, 1, distanceResult.getAzimuth1(), false));
//                        traveledPoint.add(mCurrentLocation);
                                                   drawLineRoute(mCurrentLocation, previousPoint);
//                                                   CacheUtils.saveCurrentLatLng(TaskActivity.this, mCurrentLocation.getX() + "," + mCurrentLocation.getY());

//                        MyApplication.getInstance().setPrevious_point(point);
                        // increment the distance
                        distance += distanceInterval;
                    }
                }, 0, 200);
            }
        }
        public void UpdateLocation(LocationDataSource.Location location){
            this.updateLocation(location);
        }
    }


    private void drawLineRoute(Point mCurrentPoint, Point previousPoint) {
        if ((mCurrentPoint.getX() == previousPoint.getX())&&(mCurrentPoint.getY()==previousPoint.getY()))
        {
            return;
        }
        PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
        List<Point> points = new ArrayList<>();
        points.add(previousPoint);
        points.add(mCurrentPoint);
        polylinePoints.addAll(points);
        Polyline polyline = new Polyline(polylinePoints, SpatialReferences.getWgs84());
        SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GRAY, 5.0f);
        mPolylineGraphic = new Graphic(polyline, polylineSymbol);
        mOfflineOverlay.getGraphics().add(mPolylineGraphic);
//        MyLog.e("TEST","drawLine"+traveledPoint.size());
    }


//    private void callSeverChangeLocation(LocationPointList.LocationPoint locationPoint) {
//            Map<String, String> params = new HashMap<>();
//            params.put("user_id", user.getId());
//            params.put("task_id", taskInfoMessage.getTaskId());
//            params.put("select_location_name",locationPoint.getLocationName());
//            params.put("select_lat",locationPoint.getLat());
//            params.put("select_lng",locationPoint.getLng());
//
//            Call<JsonObject> call = RetrofitUtils.getInstance().changeTaskLocation(params);
//            call.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response.body().toString());
//                        if (jsonObject.getInt("resp_code") == 0) {
//                            return;
//                        }
//                        tv_arrive_location.setText("Arrive " + locationPoint.getLocationName());
//                        tv_left_location.setText("Left " + locationPoint.getLocationName());
//                        showToast("Location name modified successfully.");
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
//    }


    /**
     * Load a mobile map package into a MapView
     *
     * @param mmpkFile Full path to mmpk file
     */
    private void loadMobileMapPackage(String mmpkFile) {
        //[DocRef: Name=Open Mobile Map Package-android, Category=Work with maps, Topic=Create an offline map]
        // create the mobile map package
        mapPackage = new MobileMapPackage(mmpkFile);
        // load the mobile map package asynchronously
        mapPackage.loadAsync();

        // add done listener which will invoke when mobile map package has loaded
        mapPackage.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                // check load status and that the mobile map package has maps
                if (mapPackage.getLoadStatus() == LoadStatus.LOADED && !mapPackage.getMaps().isEmpty()) {
                    // add the map from the mobile map package to the MapView
                    ArcGISMap streetMap = mapPackage.getMaps().get(0);
                    mMapView.setMap(streetMap);
//                    ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud1653450425,none,E9PJD4SZ8P0YF5KHT144");
                    if (streetMap != null) {
//                        // get the first transportation network in the map
                        TransportationNetworkDataset transportationNetwork = streetMap.getTransportationNetworks().get(0);
//                        // instantiate a RouteTask with the network dataset
                        localRouteTask = new RouteTask(TaskActivity.this, transportationNetwork);
                        localRouteTask.loadAsync();
                    }
                    mMapView.getMap().setMaxScale(9000);
                    mRouteOverlay = new GraphicsOverlay();
                    mMapView.getGraphicsOverlays().add(0, mRouteOverlay);
                    mOfflineOverlay = new GraphicsOverlay();
                    mMapView.getGraphicsOverlays().add(1, mOfflineOverlay);
                    mGraphicsOverlay = new GraphicsOverlay();
                    mMapView.getGraphicsOverlays().add(2, mGraphicsOverlay);
                    mPolygonOverlay = new GraphicsOverlay();
                    mMapView.getGraphicsOverlays().add(3, mPolygonOverlay);

                    double latitude = 1.3434836900969456;
                    double longitude = 103.85739813145422;
//                    Point point = new Point(Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLng()), Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()), SpatialReferences.getWgs84());
                    Point point = new Point(longitude,latitude, SpatialReferences.getWgs84());
                    mMapView.setViewpointCenterAsync(point);
                    locationDisplay = mMapView.getLocationDisplay();
                    locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);

                    Resources resources = getResources();
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(BitmapFactory.decodeResource(resources, R.mipmap.current_gps));
                    final PictureMarkerSymbol campsiteSymbol = new PictureMarkerSymbol(bitmapDrawable);
                    campsiteSymbol.setWidth(35);
                    campsiteSymbol.setHeight(35);
                    campsiteSymbol.loadAsync();
                    campsiteSymbol.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            locationDisplay.setDefaultSymbol(campsiteSymbol);
                            locationDisplay.setShowAccuracy(false);
                        }
                    });
                    BitmapDrawable courseDrawable= new BitmapDrawable(BitmapFactory.decodeResource(resources,R.mipmap.current_gps));
                    final PictureMarkerSymbol courseSymbol = new PictureMarkerSymbol(courseDrawable);
                    courseSymbol.setWidth(35);
                    courseSymbol.setHeight(35);
                    courseSymbol.loadAsync();
                    courseSymbol.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            locationDisplay.setCourseSymbol(courseSymbol);
                        }
                    });
                    mSimulatedLocationDataSource = new OfflineLocationDataSource();
                    locationDisplay.setLocationDataSource(mSimulatedLocationDataSource);
                    locationDisplay.setNavigationPointHeightFactor((float) 0.5);
                    locationDisplay.startAsync();
                    mSimulatedLocationDataSource.startAsync();

                    mMapView.setAttributionTextVisible(false);
                    mMapView.getMap().setMinScale(800000.5942343245);
                    
                    if (type == 0){
                        getNavgaitonAllLine(type);
                    }
//                    else {
//                        if (type!=6) {
//                            drawTraveldLine(type - 1);
//                        }
//                    }

                } else {
                    // log an issue if the mobile map package fails to load
                    Log.e("TEST", mapPackage.getLoadError().getMessage());
                }
            }
        });
        //[DocRef: END]
    }

    private void startLocation() {
//        double latitude = 1.3434836;
//        double longitude = 103.8573981;
        if (current == null) {
            if (LocationUtil.getBestLocation(lm, this) != null) {
                Location location = LocationUtil.getBestLocation(lm, this);
                DecimalFormat df = new DecimalFormat("######0.0000000");
                ;
                String lat = df.format(location.getLatitude()) + "";
                String lng = df.format(location.getLongitude()) + "";
                current = new MyPoint(lat, lng);
                application.setCurrentPoint(current);
                MyApplication.getInstance().setPrevious_point(current);
            }
//            else {
//                current = new MyPoint(latitude + "", longitude + "");
//            }
        }
//        Log.e("TEST","11111111"+CacheUtils.getCurrentLatLng(TaskActivity.this));
//        if (CacheUtils.getCurrentLatLng(TaskActivity.this) != null){
//            String latlng = CacheUtils.getCurrentLatLng(TaskActivity.this);
//            String[] strings = latlng.split(",");
//            current = new MyPoint(strings[1] + "", strings[0] + "");
//            Log.e("current","~~~~"+current.toString());
//            application.setCurrentPoint(current);
//            MyApplication.getInstance().setPrevious_point(current);
//        }

        receiver = new LocalReceiver();
        startService(
                new Intent(this, LocationService.class));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOCSERVER);
        localBroadcastManager.registerReceiver(receiver, intentFilter);


//        String runtimeLicenseKey = "runtimestandard,1000,rud000477276,none,9TJCA0PL4SS7LMZ59047";
//        List<String> extensions = new ArrayList<>();
//        extensions.add("runtimesmpap,1000,rud000577276,29-nov-2021,C6JDPRFHZMP1NPHFF192");
//        ArcGISRuntimeEnvironment.setLicense(runtimeLicenseKey, extensions);
//        ArcGISRuntimeEnvironment.initialize();
        loadMapDir();

        application.getUserCoordinates().setLocationtime(System.currentTimeMillis());
        startService(new Intent(this, UploadLocationService2.class));

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(true);
//            return true;//Do not perform parent class click events
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    /**
     * Broadcast system receiving positioning information
     *
     * @author guan
     */
    public class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (intent.getAction().equals(
                    LocationManager.PROVIDERS_CHANGED_ACTION)) {
                // Restart server again
                startService(
                        new Intent(TaskActivity.this, LocationService.class));
            } else if (action.equals(Constants.LOCSERVER)) {
                // Get location data
                Location loc = intent.getExtras().getParcelable("location");
//                currentPoint = new GeoPoint(loc);
                Log.e("TEST", loc + "");
                DecimalFormat df = new DecimalFormat("######0.0000000");
                ;
                String lat = df.format(loc.getLatitude()) + "";
                String lng = df.format(loc.getLongitude()) + "";
                MyPoint point = new MyPoint(lat, lng);
                application.setCurrentPoint(point);
                application.setCurrentLocation(loc);

                if (MyApplication.getInstance().getPrevious_point() == null){
                    MyApplication.getInstance().setPrevious_point(point);
                }
                GeodeticDistanceResult distanceResult = GeometryEngine
                        .distanceGeodetic(PointChange.getMapPoint(MyApplication.getInstance().getPrevious_point()), PointChange.getMapPoint(point),
                        new LinearUnit(LinearUnitId.METERS), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);
                Point location = new Point(PointChange.getMapPoint(point).getX(), PointChange.getMapPoint(point).getY(), SpatialReferences.getWgs84());
                if (location != null) {
                    try {
                        LocationDataSource.Location ldata = new LocationDataSource.Location(location, 1, 1, distanceResult.getAzimuth1(), false);
                        if (mSimulatedLocationDataSource != null) {
                            mSimulatedLocationDataSource.UpdateLocation(ldata);
                            mSimulatedLocationDataSource.startAsync();
                        }
                        MyApplication.getInstance().setPrevious_point(point);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
