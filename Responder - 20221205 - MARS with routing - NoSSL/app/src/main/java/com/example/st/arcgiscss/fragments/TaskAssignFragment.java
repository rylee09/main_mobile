package com.example.st.arcgiscss.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.TransportationNetworkDataset;
import com.esri.arcgisruntime.geometry.AngularUnit;
import com.esri.arcgisruntime.geometry.AngularUnitId;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.GeodeticCurveType;
import com.esri.arcgisruntime.geometry.GeodeticDistanceResult;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.LinearUnitId;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.navigation.DestinationStatus;
import com.esri.arcgisruntime.navigation.RouteTracker;
import com.esri.arcgisruntime.navigation.TrackingStatus;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.tasks.networkanalysis.DirectionManeuver;
import com.esri.arcgisruntime.tasks.networkanalysis.Route;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.esri.arcgisruntime.tasks.networkanalysis.Stop;
import com.example.st.arcgiscss.R;
import com.example.st.arcgiscss.activites.MainActivity;
import com.example.st.arcgiscss.activites.NewMainActivity;
import com.example.st.arcgiscss.activites.SelectEvacActivity;
import com.example.st.arcgiscss.activites.UpdateIncidentActivity;
import com.example.st.arcgiscss.activites.WatchIncidentActivity;
import com.example.st.arcgiscss.constant.Constants;
import com.example.st.arcgiscss.constant.MyApplication;
import com.example.st.arcgiscss.d3View.D3Fragment;
import com.example.st.arcgiscss.d3View.D3View;
import com.example.st.arcgiscss.dao.NewIncidentDBHelper;
import com.example.st.arcgiscss.model.FuelTopUp;
import com.example.st.arcgiscss.model.LocationType;
import com.example.st.arcgiscss.model.MyPoint;
import com.example.st.arcgiscss.model.NewIncident;
import com.example.st.arcgiscss.model.User;
import com.example.st.arcgiscss.upload.UserCoordinate;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.IMEIUtil;
import com.example.st.arcgiscss.util.LocationUtil;
import com.example.st.arcgiscss.util.OfflineLocationDataSource;
import com.example.st.arcgiscss.util.PointChange;
import com.example.st.arcgiscss.util.RetrofitUtils;
import com.example.st.arcgiscss.views.IosAlertDialog;
import com.example.st.arcgiscss.views.RotationLoadingView;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CALL_PHONE;

public class TaskAssignFragment extends D3Fragment {
//
//    private User user;
//
//    private RouteTracker mRouteTracker;
//    private Graphic mRouteAheadGraphic;
//    private Graphic mRouteTraveledGraphic;
//
//    private static final String TAG = MainActivity.class.getSimpleName();
//
//    @D3View(click = "onClick")
//    LinearLayout ll_iv_tips, ll_chat, ll_self, ll_incident,ll_offline;
//    @D3View(click = "onClick")
//    ImageView iv_tips, iv_offline, iv_chat, iv_menu, iv_self,  iv_incident, iv_c_incident, iv_phone;
//
//    @D3View
//    RelativeLayout rl_main;
//
//    @D3View
//    TextView tv_gonewmain;
//
//    @D3View
//    RotationLoadingView item_loading_image;
//
//    private MapView mMapView;
//
//    private static final int READ_PHONE_STATE = 100;
//    private int GPS_REQUEST_CODE = 1;
//
//    private MyApplication application;
//
//    private MyPoint current;
//
//    private LocationManager lm;
//
//    private MainActivity.LocalReceiver receiver;
//    private LocalBroadcastManager localBroadcastManager;
//
//    private static final String FILE_EXTENSION = ".mmpk";
//    private static File extStorDir;
//    private static String extSDCardDirName;
//    private static String filename;
//    private static String mmpkFilePath;
//
//    private MobileMapPackage mapPackage;
//
//    private OfflineLocationDataSource mSimulatedLocationDataSource;
//
//    private LocationDisplay locationDisplay;
//
//    private GraphicsOverlay mGraphicsOverlay;
//
//    private GraphicsOverlay mOfflineOverlay;
//
//    private GraphicsOverlay mRouteOverlay;
//
//    private ArrayList<String> incidentType = new ArrayList<>();
//
//    private LocationType locationType;
//
//    private MainActivity.NewMQTTMsgReceiver newMReceiver;
//
//    private List<Graphic> incidentGraphics = new ArrayList<>();
//
//    //ZN - 20200608
//    private TransportationNetworkDataset streetNetwork;
//    private RouteTask routeTask;
//    private RouteParameters defaultRouteParams;
//    private boolean isMapLoaded = false;
//    private boolean isRouteShown = false;
//    private boolean isIncidentReceived = false;
//    private Graphic routeGraphic;
//    private Graphic CCP_pt_grap;
//
//    //ZN - 20201216
//    NotificationManagerCompat notificationManagerCompat;
//
//    //ZN - 20201219
//    private static final String INCIDENT_ACKNOWLEDGED = "acknowledged";
//    private static final String INCIDENT_LEFT_BASE_TIME = "left_base_time";
//    private static final String INCIDENT_ARRIVE_LUP_TIME = "arrive_LUP_time";
//    private static final String INCIDENT_LEFT_LUP_TIME = "left_LUP_time";
//    private static final String INCIDENT_ARRIVE_EVACPT_TIME = "arrive_EvacPt_time";
//    private static final String INCIDENT_LEFT_EVACPT_TIME = "left_EvacPt_time";
//    private static final String INCIDENT_COMPLETE = "complete";
//    //default, need to relook the default value for first switch-case for onClick()
//    private String str_nextIncidentStatus = INCIDENT_LEFT_BASE_TIME;
//
//    //ZN - 20201221
//    //string container to store incident details for notification
//    private String n_incident_LUP = "";
//    private String n_incident_type = "";
//    private String n_incident_location = "";
//
//    //ZN - 20210214 point to store dest
//    private Point destination_point;
//
//    //ZN - 20210214 for navigation text-to-speech
//    private TextToSpeech mTextToSpeech;
//    private boolean mIsTextToSpeechInitialized = false;
//
//    //ZN - 20210223
//    TextView distanceRemainingTextView;
//    TextView timeRemainingTextView;
//    TextView nextDirectionTextView;
//
//    //ZN - 20210427
//    private boolean isEvacSelected;
//    private HashMap evacLocations;
//    private String evacName;
//    private Point evacPoint;
//
//    //ZN - 20210510
//    private final int FOR_LUP = 1;
//    private final int FOR_EVAC = 2;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        Log.i("EVENT","[MainActivity] onCreate");
//        View view = setContentView(inflater, R.layout.activity_main);
//        initView();
//
//        return view;
//    }
//
//    private void initView() {
//        getIncidentType();
//        application = MyApplication.getInstance();
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mMapView = view.findViewById(R.id.mapView);
//
//        //ZN - 20210420 reference map objects from initialised from LoginActivity
//        mapPackage = application.getMobileMapPackage();
//        routeTask = application.getRouteTask();
//        if (mapPackage != null && routeTask != null)
//            Log.i("map", "[MainActivity] map objects referenced");
//
//        UserCoordinate userCoordinate = new UserCoordinate();
//        userCoordinate.setUserId(CacheUtils.getUserId(getContext()));
//        application.setUserCoordinates(userCoordinate);
//
//        localBroadcastManager = application.getLocalBroadcastManager();
//        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            showContacts();
//        } else {
//            Log.i("SL", "onCreate else");
//            startLocation();
//        }
//
//        //ZN - 20210201
//        LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.ll_menmue);
//        mainLayout.setVisibility(LinearLayout.GONE);
//
//        //ZN - 20210201 to display username
//        TextView tv_username = (TextView) view.findViewById(R.id.tv_callsign);
//        tv_username.setText(application.getUsername());
//
//        //ZN - 20210214 initialize text-to-speech to replay navigation voice guidance
//        mTextToSpeech = new TextToSpeech(getContext(), status -> {
//            if (status != TextToSpeech.ERROR) {
//                mTextToSpeech.setLanguage(Resources.getSystem().getConfiguration().locale);
//                mIsTextToSpeechInitialized = true;
//            }
//
//            System.out.println("Text to Speech: " + mIsTextToSpeechInitialized);
//        });
//
//        //ZN - 20210223
//        distanceRemainingTextView = (TextView) view.findViewById(R.id.tv_remain_dist);
//        timeRemainingTextView = (TextView) view.findViewById(R.id.tv_ETA);
//        nextDirectionTextView = (TextView) view.findViewById(R.id.tv_directions);
//
//        //add
//        iv_self.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
//            }
//        });
//
//        //ZN - 20210620 function for phone call
//        iv_phone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel: +65" + application.getPOC_Contact()));
//
//                if (ContextCompat.checkSelfPermission(getContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                    startActivity(callIntent);
//                } else {
//                    requestPermissions(new String[]{CALL_PHONE}, 1);
//                }
//
//            }
//        });
//
//        //ZN - 20210624 function to open incident details
//        iv_incident.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NewIncident incident = NewIncidentDBHelper.getInstance(getContext()).getLatestIncidentMsg();
//                if (incident != null) {
//                    Intent intent2 = new Intent(getContext(), UpdateIncidentActivity.class);
//                    intent2.putExtra("incident", incident);
//                    startActivity(intent2);
//                }
//            }
//        });
//
//        //ZN - 20201218
//        if(isIncidentReceived) {
//            Log.e("INCIDENT", "INCIDENT Has Been Received!!!!");
//            showAllIncidentUpdateIconVisible();
//        }
//
//        //ZN - 20210427 initialise hospital locations
//        //ZN - 20210611 initialise evac locations
//        evacLocations = new HashMap();
//        evacLocations.put(MyApplication.EVAC_AH, "1.2868027216745843,103.80119979571164");
//        evacLocations.put(MyApplication.EVAC_KTPH, "1.4248179838249517,103.83828137057739");
//        evacLocations.put(MyApplication.EVAC_NTFGH, "1.3339378657452172,103.74537003989087");
//        evacLocations.put(MyApplication.EVAC_NUH, "1.2941559037504022,103.78317211290606");
//        evacLocations.put(MyApplication.EVAC_SGH, "1.279873275672685,103.83598410284796");
//        //ZN - 20210611 add medical centre location
//        evacLocations.put(MyApplication.EVAC_MC, CacheUtils.getUser(getContext()).getActivation_latlon());
//
//        Toast.makeText(getContext(), "Home ground: " + evacLocations.get(MyApplication.EVAC_MC), Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.i("EVENT","[MainActivity] onCreate");
//
////        done
//        //        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////        application = MyApplication.getInstance();
////        mMapView = findViewById(R.id.mapView);
//
//        //locationType = (LocationType) getIntent().getSerializableExtra("locations");
//
////        done
//        //ZN - 20210420 reference map objects from initialised from LoginActivity
////        mapPackage = application.getMobileMapPackage();
////        routeTask = application.getRouteTask();
////        if (mapPackage != null && routeTask != null)
////            Log.i("map", "[MainActivity] map objects referenced");
//
//
////        done
////        //ZN - 20201201
////        UserCoordinate userCoordinate = new UserCoordinate();
////        userCoordinate.setUserId(CacheUtils.getUserId(this));
////        application.setUserCoordinates(userCoordinate);
//
//
////        done
////        localBroadcastManager = application.getLocalBroadcastManager();
////        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
////        initView();
////
////        if (Build.VERSION.SDK_INT >= 23) {
////            showContacts();
////        } else {
////            Log.i("SL", "onCreate else");
////            startLocation();
////        }
//
//
//        //TODO - come back later
//        //ZN - 20201216
//        //create notification channel for app notification
////        notificationManagerCompat = NotificationManagerCompat.from(this);
////        createNotificationChannel();
//
//
////        done
////        //ZN - 20210201
////        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.ll_menmue);
////        mainLayout.setVisibility(LinearLayout.GONE);
//
//
////        done
////        //ZN - 20210201 to display username
////        TextView tv_username = (TextView) findViewById(R.id.tv_callsign);
////        tv_username.setText(application.getUsername());
//
//
////        done
////        //ZN - 20210214 initialize text-to-speech to replay navigation voice guidance
////        mTextToSpeech = new TextToSpeech(this, status -> {
////            if (status != TextToSpeech.ERROR) {
////                mTextToSpeech.setLanguage(Resources.getSystem().getConfiguration().locale);
////                mIsTextToSpeechInitialized = true;
////            }
////
////            System.out.println("Text to Speech: " + mIsTextToSpeechInitialized);
////        });
//
//
////        done
////        //ZN - 20210223
////        distanceRemainingTextView = findViewById(R.id.tv_remain_dist);
////        timeRemainingTextView = findViewById(R.id.tv_ETA);
////        nextDirectionTextView = findViewById(R.id.tv_directions);
//
//
////        done
////        //add
////        iv_self.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
////            }
////        });
//
//
////        done
////        //ZN - 20210620 function for phone call
////        iv_phone.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent callIntent = new Intent(Intent.ACTION_CALL);
////                callIntent.setData(Uri.parse("tel: +65" + application.getPOC_Contact()));
////
////                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
////                    startActivity(callIntent);
////                } else {
////                    requestPermissions(new String[]{CALL_PHONE}, 1);
////                }
////
////            }
////        });
//
//
////        done
////        //ZN - 20210624 function to open incident details
////        iv_incident.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                NewIncident incident = NewIncidentDBHelper.getInstance(MainActivity.this).getLatestIncidentMsg();
////                if (incident != null) {
////                    Intent intent2 = new Intent(MainActivity.this, UpdateIncidentActivity.class);
////                    intent2.putExtra("incident", incident);
////                    startActivity(intent2);
////                }
////            }
////        });
//
//        //updateIncident();
//
//
////        done
////        //ZN - 20201218
////        if(isIncidentReceived) {
////            Log.e("INCIDENT", "INCIDENT Has Been Received!!!!");
////            showAllIncidentUpdateIconVisible();
////        }
//
//        //TODO - come back later
//        //ZN - 20210420 reorder from onResume
//        registerReceiver(receiver,
//                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
//
//        //ZN - 20210415 initialise incident listener
//        //Intent intent1 = new Intent(this, GetNewInfoService.class);
//        //startService(intent1);
//        newMReceiver = new MainActivity.NewMQTTMsgReceiver();
//
//
////        done
////        //ZN - 20210427 initialise hospital locations
////        //ZN - 20210611 initialise evac locations
////        evacLocations = new HashMap();
////        evacLocations.put(MyApplication.EVAC_AH, "1.2868027216745843,103.80119979571164");
////        evacLocations.put(MyApplication.EVAC_KTPH, "1.4248179838249517,103.83828137057739");
////        evacLocations.put(MyApplication.EVAC_NTFGH, "1.3339378657452172,103.74537003989087");
////        evacLocations.put(MyApplication.EVAC_NUH, "1.2941559037504022,103.78317211290606");
////        evacLocations.put(MyApplication.EVAC_SGH, "1.279873275672685,103.83598410284796");
////        //ZN - 20210611 add medical centre location
////        evacLocations.put(MyApplication.EVAC_MC, CacheUtils.getUser(this).getActivation_latlon());
//
////        Toast.makeText(MainActivity.this, "Home ground: " + evacLocations.get(MyApplication.EVAC_MC), Toast.LENGTH_LONG).show();
//    }
//
//    private void getIncidentType() {
//        Call<JsonObject> call = RetrofitUtils.getInstance().getIncidentTypeList();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response.body().toString());
//                    String resp_msg = jsonObject.getString("resp_msg");
//                    JSONArray jsonArray = new JSONArray(resp_msg);
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        incidentType.add(jsonArray.get(i).toString());
//                    }
//                    Log.e("TEST",incidentType.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
//            }
//        });
//
//    }
//
//
//    public class NewMQTTMsgReceiver extends BroadcastReceiver {
//        @RequiresApi(api = Build.VERSION_CODES.M)
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.i("onReceive", "rcv MQTT msg");
//            if (intent.getAction().equals("NewMQTTMsgReceiver")) {
//                if (intent.getExtras() != null) {
//                    Log.i("onReceive","[MainActivity] onReceive");
//                    //ZN - 20201216
////                  notificationManager.notify(100, builder.build());
//
//                    //ZN - 20210116 keep alert and vibrate until user ack incident
////                    TipHelper.Vibrate(MainActivity.this,1000);
////                    TipHelper.playSound(MainActivity.this);
//
////                  NewIncident incident = (NewIncident) intent.getExtras().getSerializable("incident");
////                  NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(incident);
//
//                    if (mGraphicsOverlay != null && !isIncidentReceived) {
//                        Log.i("Test", "updateIncident()");
//
//
//                        //ZN - 20201220
//                        NewIncident incident = (NewIncident) intent.getExtras().getSerializable("incident");
//                        NewIncidentDBHelper.getInstance(getApplicationContext()).saveNewIncidentMsg(incident);
//                        n_incident_location = "Location: " + incident.getIncidentLocation();
//                        n_incident_type = "Type: " + incident.getType();
//                        n_incident_LUP = "Collection Point: " + incident.getActivationLocation();
//
//                        //ZN - 20210215 save incident id to MyApplication
//                        //MyApplication.getInstance().setCurrentIncidentID(incident.getId());
//                        application.setCurrentIncidentID(incident.getId());
//                        //ZN - 20210620 save POC contact
//                        application.setPOC_Contact(incident.getPOC_Contact());
//
//
//                        //display notification
//                        createNotificationAndIntent();
//                        Notification n = builder.build();
//                        n.flags |= Notification.FLAG_INSISTENT;
//                        //notificationManagerCompat.notify(100, builder.build());
//                        notificationManagerCompat.notify(100, n);
//
//                        //add intent info for Watch Activity dialog and start activity for notification to go to
//                        intent_watchIncidentActivity.putExtra("incident", incident);
//                        startActivity(intent_watchIncidentActivity);
//
//                        updateIncident();
//
//                        //ZN - 20201218
//                        showAllIncidentUpdateIconVisible();
//                        isIncidentReceived = true;
//                    }
////                    Log.e("TEST","~~~~~~"+incident.toString());
//
//
//                }
//            }
//        }
//    }
//
//
//    private void updateIncident() {
//        List<NewIncident> incidents = NewIncidentDBHelper.getInstance(getApplicationContext()).getIncidentMsg();
//        //ZN - 20200618
//        //NewIncident incident = NewIncidentDBHelper.getInstance(getApplicationContext()).getLatestIncidentMsg();
//
//        if (incidents.size() == 0) {
//            isIncidentReceived = false;
//            return;
//        }
//
//        isIncidentReceived = true;
//
//        if (incidentGraphics != null&&incidentGraphics.size()>0){
//            Log.i("Test", "remove previous incident and route");
//            mGraphicsOverlay.getGraphics().removeAll(incidentGraphics);
//        }
//        incidentGraphics = new ArrayList<>();
//        Log.e("TEST","incident: "+incidents.size());
//        for (int i = 0; i < incidents.size(); i++) {
//            final NewIncident incident = incidents.get(i);
//
//            //MyApplication.getInstance().setCurrentIncidentID(incident.getId());
//            application.setCurrentIncidentID(incident.getId());
//
//            BitmapDrawable redwarn = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.redwarn);
//            if (incident.getStatus().equals("CLOSED")){
//                redwarn = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.warn_grey);
//            }
//            PictureMarkerSymbol redwarnpms = new PictureMarkerSymbol(redwarn);
//            redwarnpms.setHeight(32);
//            redwarnpms.setWidth(32);
//            redwarnpms.loadAsync();
//            //for testing
//            Point redwarnP = new Point(Double.valueOf(incident.getLng()), Double.valueOf(incident.getLat()), SpatialReferences.getWgs84());
//            //Point redwarnP = new Point(103.84782655272848, 1.3901713267606508, SpatialReferences.getWgs84());
//            Graphic redwgp = new Graphic(redwarnP, redwarnpms);
//            redwgp.getAttributes().put("incidentNo", incident.getId());
//            incidentGraphics.add(redwgp);
//
//            //ZN - 20200602
//            mMapView.setViewpointCenterAsync(redwarnP);
//
//            //ZN - 20200610
//            if (mMapView.getGraphicsOverlays() != null){
//                //startReadyNavigation2();
//
//                //ZN - 20210131 - use current point instead as no method UserCoordinate is not updated by LocationService
//                //ZN - 20201209
//                //UserPosition usrPos = MyApplication.getInstance().getUserPosition();
//                //UserCoordinate usrCoord = MyApplication.getInstance().getUserCoordinates();
//                //Point usrPoint = new Point(Double.valueOf(usrCoord.getLongitude()), Double.valueOf(usrCoord.getLatitude()), SpatialReferences.getWgs84());
//                //MyPoint currentPoint = MyApplication.getInstance().getCurrentPoint();
//                MyPoint currentPoint = application.getCurrentPoint();
//                Point usrPoint = new Point(Double.valueOf(currentPoint.getLng()), Double.valueOf(currentPoint.getLat()), SpatialReferences.getWgs84());
//
//                if (incident.getLocationType().equalsIgnoreCase("4")) {
//                    //training area; route to CCP
//                    //for testing
//                    Point linkupPoint = new Point(Double.valueOf(incident.getCCP_lon()), Double.valueOf(incident.getCCP_lat()), SpatialReferences.getWgs84());
//                    //Point linkupPoint = new Point(103.84782655272848, 1.3901713267606508, SpatialReferences.getWgs84());
//                    showRoute(usrPoint, linkupPoint, FOR_LUP);
//                } else {
//                    //camp; route to incident directly
//                    showRoute(usrPoint, redwarnP, FOR_LUP);
//                }
//                isRouteShown = true;
//            }
//
//
//        }
//        mGraphicsOverlay.getGraphics().addAll(incidentGraphics);
//        mMapView.invalidate();
//
//        //ZN - 20210420 set incident ack false
//        //MyApplication.getInstance().setIncidentAck(false);
//        application.setIncidentAck(false);
//    }
//
//
//    @Override
//    protected void onStart() {
//        Log.i("EVENT","[MainActivity] onStart");
//        super.onStart();
//    }
//
//
//    public void onResume() {
//        Log.i("EVENT", "[MainActivity] onResume");
//
//        super.onResume();
//        mMapView.resume();
////        registerReceiver(receiver,
////                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
//
//        //ZN - 20210420 to refresh username in event of logout
//        TextView tv_username = (TextView) getView().findViewById(R.id.tv_callsign);
////        tv_username.setText(MyApplication.getInstance().getUsername());
//        tv_username.setText(application.getUsername());
//
//        //ZN - 20210420 resume listening for incidents
//        registerReceiver(newMReceiver, new IntentFilter("NewMQTTMsgReceiver"));
//    }
//
//    //ZN - 20210427 to rcv and process selected evac
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        Log.i("EVENT", "[MainActivity] onNewIntent");
//        setIntent(intent);
//        String intent_select_evac_extra = getIntent().getStringExtra("Evac");
//
//        //ZN - 20210503 if new intent comes from elsewhere, ignore and return
//        if (intent_select_evac_extra == null)
//            return;
//
//        Toast.makeText(this,"rcv evac from selection: " + intent_select_evac_extra, Toast.LENGTH_SHORT).show();
//
//        //ZN - 20210611 need to have route for all evac
//        isEvacSelected = true;
//        updateAndShowEvacRoute(intent_select_evac_extra);
//
//    }
//
//    public void onPause() {
//        Log.i("EVENT", "[MainActivity] onPause");
//        super.onPause();
//        mMapView.pause();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        Log.i("EVENT", "[MainActivity] onDestroy");
////        stopService(new Intent(this, LocationService.class));
//
//        unregisterReceiver(receiver);
//        localBroadcastManager.unregisterReceiver(receiver);
////        stopService(new Intent(this, UploadLocationService2.class));
//        application.isUploadServer = false;
//
//        //stopService(new Intent(this, GetNewInfoService.class));
//
//        super.onDestroy();
//    }
//
//    public void showContacts() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(this, new String[]{
//                    Manifest.permission.INTERNET,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_NETWORK_STATE,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            }, READ_PHONE_STATE);
//        } else {
//            Log.i("SL", "showContacts");
//            startLocation();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case READ_PHONE_STATE:
//                if (grantResults[1] == PackageManager.PERMISSION_GRANTED
//                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
//                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
//                    Log.i("SL", "onRequestPermissionsResult");
//                    startLocation();
//                } else {
//                    finish();
//                }
//                break;
//            default:
//                break;
//        }
//    }
//
//    private static String createMobileMapPackageFilePath() {
//        return extStorDir.getAbsolutePath() + File.separator + extSDCardDirName + File.separator + filename
//                + FILE_EXTENSION;
//    }
//
////    private void loadMapDir() {
////        extStorDir = Environment.getExternalStorageDirectory();
////        extSDCardDirName = this.getResources().getString(R.string.config_data_sdcard_offline_dir);
////        filename = this.getResources().getString(R.string.singapore_mmpk);
////        mmpkFilePath = createMobileMapPackageFilePath();
////        loadMobileMapPackage2(mmpkFilePath);
////
////    }
//
//    private void loadMobileMapPackage2() {
//        Log.i("map", "[MainActivity] streetMap: "+ mapPackage);
//        ArcGISMap streetMap= mapPackage.getMaps().get(0);
//        mMapView.setMap(streetMap);
//        //ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud1653450425,none,E9PJD4SZ8P0YF5KHT144");
//        mMapView.getMap().setMaxScale(9000);
//        mRouteOverlay = new GraphicsOverlay();
//        mMapView.getGraphicsOverlays().add(0,mRouteOverlay);
//        mOfflineOverlay = new GraphicsOverlay();
//        mMapView.getGraphicsOverlays().add(1,mOfflineOverlay);
//        mGraphicsOverlay = new GraphicsOverlay();
//        mMapView.getGraphicsOverlays().add(2,mGraphicsOverlay);
//
//        //Point point = new Point(Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLng()), Double.valueOf(MyApplication.getInstance().getCurrentPoint().getLat()), SpatialReferences.getWgs84());
//        //Point point = new Point(Double.valueOf(application.getCurrentPoint().getLng()), Double.valueOf(application.getCurrentPoint().getLat()), SpatialReferences.getWgs84());
//        //mMapView.setViewpointCenterAsync(point);
//
//        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(MainActivity.this, mMapView) {
//            @Override
//            public void onLongPress(MotionEvent e) {
//                super.onLongPress(e);
//            }
//
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                // create a point from where the user clicked
//                android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
//
//                // identify graphics on the graphics overlay
//                final ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphic = mMapView.identifyGraphicsOverlayAsync(mGraphicsOverlay, screenPoint, 10.0, false, 1);
//
//                identifyGraphic.addDoneListener(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            IdentifyGraphicsOverlayResult grOverlayResult = identifyGraphic.get();
//                            // get the list of graphics returned by identify graphic overlay
//                            List<Graphic> graphic = grOverlayResult.getGraphics();
//                            // get size of list in results
//                            int identifyResultSize = graphic.size();
//                            if (!graphic.isEmpty()) {
//                                // show a toast message if graphic was returned
////                                          Toast.makeText(getApplicationContext(), "Tapped on " + identifyResultSize + " Graphic", Toast.LENGTH_SHORT).show();
//                                if (graphic.get(0).getAttributes().get("incidentNo") != null) {
//                                    String incidentNo = (String) graphic.get(0).getAttributes().get("incidentNo");
//                                    NewIncident incident = NewIncidentDBHelper.getInstance(MainActivity.this).getIncidentsInfo(incidentNo);
//                                    if (incident != null) {
//                                        //ZN - 20210204 show update incident POC dialog instead
//                                        //ZN - 20210420 revert to display incident summary
//                                        //ZN - 20210616 show update incident activity for casualty condition update
//                                        //Intent intent2 = new Intent(MainActivity.this, WatchIncidentActivity.class);
//                                        Intent intent2 = new Intent(MainActivity.this, UpdateIncidentActivity.class);
//                                        intent2.putExtra("incident", incident);
//                                        startActivity(intent2);
//                                    }
//                                }
//
//                            }
//                        } catch (InterruptedException | ExecutionException ie) {
//                            ie.printStackTrace();
//                        }
//
//                    }
//                });
//
//                return super.onSingleTapConfirmed(e);
//            }
//        });
//
//        locationDisplay = mMapView.getLocationDisplay();
//        mSimulatedLocationDataSource = new OfflineLocationDataSource();
//        //mSimulatedLocationDataSource = new SimulatedLocationDataSource(routeGeometry);
//        locationDisplay.setLocationDataSource(mSimulatedLocationDataSource);
//        locationDisplay.setNavigationPointHeightFactor((float) 0.5);
//        locationDisplay.startAsync();
//        mSimulatedLocationDataSource.startAsync();
//        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
//        mMapView.getGraphicsOverlays().add(graphicsOverlay);
//        mMapView.setAttributionTextVisible(false);
//        mMapView.getMap().setMinScale(800000.5942343245);
//        updateIncident();
//        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
//
//        mMapView.getMap().setMaxScale(3000);
//        mMapView.setAttributionTextVisible(false);
//        mMapView.getMap().setMinScale(800000.5942343245);
//    }
//
//    private void startLocation() {
//        Basemap.Type basemapType = Basemap.Type.LIGHT_GRAY_CANVAS_VECTOR;
//        int levelOfDetail = 18;
//        double latitude = 1.3434836900969456;
//        double longitude = 103.85739813145422;
//
//        if (current == null) {
//            if (LocationUtil.getBestLocation(lm, this) != null) {
//                Location location = LocationUtil.getBestLocation(lm, this);
//                Point point = new Point(location.getLongitude(), location.getLatitude(), SpatialReferences.getWgs84());
//                current = new MyPoint(point.getY() + "", point.getX() + "");
//            } else {
//                current = new MyPoint(latitude + "", longitude + "");
//            }
//        }
//        application.setCurrentPoint(current);
//        //MyApplication.getInstance().setPrevious_point(current);
//        application.setPrevious_point(current);
//        receiver = new MainActivity.LocalReceiver();
////        startService(
////                new Intent(this, LocationService.class));
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.LOCSERVER);
//        localBroadcastManager.registerReceiver(receiver, intentFilter);
//        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        //ZN - 20201201
//        //loadMapDir();
//        //application.getUserPosition().setLat(current.getLat());
//        //application.getUserPosition().setLng(current.getLng());
//
//        //loadMapDir();
//        loadMobileMapPackage2();
//
//        //ZN - 20201201
//        lm.addGpsStatusListener(statusListener);
//        application.getUserCoordinates().setLatitude(current.getLat());
//        application.getUserCoordinates().setLongitude(current.getLng());
//        application.getUserCoordinates().setImei(IMEIUtil.getIMEI(getApplicationContext()));
//        application.getUserCoordinates().setLocationtime(System.currentTimeMillis());
//
//        if (!application.isUploadServer) {
//            application.isUploadServer = true;
////            startService(new Intent(this, UploadLocationService2.class));
//        }
//
//    }
//
//    //   @Override
////    public boolean onKeyDown(int keyCode, KeyEvent event) {
////        if (keyCode == KeyEvent.KEYCODE_BACK) {
////            Log.i("EVENT", "[MainActivity] onKeyDown KEYCODE_BACK");
////            moveTaskToBack(true);
////
////            //return to NewMainActivity
////
////            return true;
////        }
////        return super.onKeyDown(keyCode, event);
////    }
//
//
////    public void onClick(View v) {
////        ImageView iv;
////        //ZN - 20201203
////        switch (v.getId()) {
////            case R.id.iv_leftbase:
////                setIncidentStatus("left_base_time");
////                iv = (ImageView) findViewById(R.id.iv_leftbase);
////                iv.setVisibility(View.INVISIBLE);
////                break;
////            case R.id.iv_arriveLUP:
////                setIncidentStatus("arrive_LUP_time");
////                iv = (ImageView) findViewById(R.id.iv_arriveLUP);
////                iv.setVisibility(View.INVISIBLE);
////                break;
////            case R.id.iv_arriveEvacPt:
////                setIncidentStatus("arrive_EvacPt_time");
////                iv = (ImageView) findViewById(R.id.iv_arriveEvacPt);
////                iv.setVisibility(View.INVISIBLE);
////                break;
////            case R.id.iv_leftEvacPt:
////                setIncidentStatus("left_EvacPt_time");
////                iv = (ImageView) findViewById(R.id.iv_leftEvacPt);
////                iv.setVisibility(View.INVISIBLE);
////                break;
////            case R.id.iv_complete:
////                setIncidentComplete();
////                iv = (ImageView) findViewById(R.id.iv_complete);
////                iv.setVisibility(View.INVISIBLE);
////                break;
////        }
////
////    }
//
//    @SuppressLint("ResourceType")
//    public void onClick(View v) {
//        Log.e("BUTTON", "CLICKED!!!!");
//
//        ImageView iv = (ImageView) findViewById(R.id.iv_leftbase);
//
//        //ZN - 20210116
//        TextView tv_statusText = (TextView) findViewById(R.id.tv_statusText);
//
//        //ZN - 20210201
//        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ll_menmue);
//
//        //ZN - 20210214
//        LinearLayout naviLayout = (LinearLayout) findViewById(R.id.ll_navigation);
//
//        //ZN - 20201203
//        switch (str_nextIncidentStatus) {
//            case INCIDENT_LEFT_BASE_TIME:
//                setIncidentStatus("left_base_time");
//                str_nextIncidentStatus = INCIDENT_ARRIVE_LUP_TIME;
//                iv.setImageResource(0);
//                //iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.waypoint_enter));
//                iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.ccp_in_clear));
//
//                //ZN - 20210116
//                tv_statusText.setText("Arrive Link Up Point");
//
//                //ZN - 20210123
//                //remove callout
//                mMapView.getCallout().dismiss();
//
//                //ZN - 20210214 start navigation mode
//                naviLayout.setVisibility(LinearLayout.VISIBLE);
//                startReadyNavigation();
//
//                break;
//            case INCIDENT_ARRIVE_LUP_TIME:
//                setIncidentStatus("arrive_LUP_time");
//                str_nextIncidentStatus = INCIDENT_LEFT_LUP_TIME;
//                iv.setImageResource(0);
//                //iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.waypoint_exit));
//                iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.ccp_out_clear));
//
//                //ZN - 20210116
//                tv_statusText.setText("Left Link Up Point");
//
//                //ZN - 20120214
//                naviLayout.setVisibility(LinearLayout.INVISIBLE);
//
//                //ZN - 20210221 remove base-to-LUP route from map
//                removeRoute();
//                stopNavigation();
//
//                break;
//            case INCIDENT_LEFT_LUP_TIME:
//                if (!isEvacSelected) {
//                    //ZN - 20210426 show select evac activity
//                    Intent intent = new Intent(MainActivity.this, SelectEvacActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    startActivity(intent);
//                } else {
//                    setIncidentStatus("left_LUP_time");
//                    str_nextIncidentStatus = INCIDENT_ARRIVE_EVACPT_TIME;
//                    iv.setImageResource(0);
//                    //iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.red_cross_enter));
//                    iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.hospital_in_clear));
//
//                    //ZN - 20210116
//                    tv_statusText.setText("Arrive Evac");
//
//                    //ZN - 20210427 show navigation from LUP to evac
//                    naviLayout.setVisibility(LinearLayout.VISIBLE);
//                    startReadyNavigation();
//                    isEvacSelected = false;
//                }
//
//                break;
//            case INCIDENT_ARRIVE_EVACPT_TIME:
//                setIncidentStatus("arrive_EvacPt_time");
//                str_nextIncidentStatus = INCIDENT_LEFT_EVACPT_TIME;
//                iv.setImageResource(0);
//                //iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.red_cross_exit));
//                iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.hospital_out_clear));
//
//                //ZN - 20210116
//                tv_statusText.setText("Left Evac");
//
//                //ZN - 20210427 remove navigation route
//                removeRoute();
//                stopNavigation();
//                naviLayout.setVisibility(LinearLayout.INVISIBLE);
//
//                break;
//            case INCIDENT_LEFT_EVACPT_TIME:
//                setIncidentStatus("left_EvacPt_time");
//                str_nextIncidentStatus = INCIDENT_COMPLETE;
//                //iv = (ImageView) findViewById(R.id.iv_leftEvacPt);
//                iv.setImageResource(0);
//                //iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.base_enter));
//                iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.base_in_clear));
//
//                //ZN - 20210116
//                tv_statusText.setText("Arrive Base");
//                break;
//            case INCIDENT_COMPLETE:
//                setIncidentComplete();
//                iv.setVisibility(View.INVISIBLE);
//                str_nextIncidentStatus = INCIDENT_LEFT_BASE_TIME;
//
//                //ZN - 20210116
//                tv_statusText.setVisibility(View.INVISIBLE);
//
//                //ZN - 20210201
//                mainLayout.setVisibility(LinearLayout.INVISIBLE);
//
//                LinearLayout iv_bg = (LinearLayout) findViewById(R.id.iv_bg);
//                iv_bg.setVisibility(LinearLayout.INVISIBLE);
//
//                //ZN - 20210620
//                iv_phone.setVisibility(View.INVISIBLE);
//
//                //ZN - 20210624
//                iv_incident.setVisibility(View.INVISIBLE);
//
//                break;
//        }
//
//    }
//
//    private void autoWalk() {
//        PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
//        String routeStr = "1.390394119015525:103.8473845161112;1.3904300002639587:103.84753999968265;1.3904300002664776:103.8475399996936;1.3904499998572701:103.84762000005955;1.3904499998572701:103.84762000005955;1.390650000246343:103.84840000003422;1.390650000246343:103.84840000003422;1.3907199997049586:103.84867000037094;1.3907199997049586:103.84867000037094;1.390830000075165:103.84867000037094;1.3908300001329748:103.84867000037094;1.3912600002057158:103.84867000037094;1.3913400003095528:103.84866000032852;1.3913400003359386:103.84866000032522;1.3916800004099779:103.84862000014223;1.3917300002582214:103.84861000009649;1.3917300002700783:103.84861000009336;1.3919199998503222:103.8485599998681;1.3919199998516072:103.84855999986777;1.3920499998009015:103.84852999973057;1.3922199997928844:103.84849999959334;1.3923099996780024:103.84884000025026;1.392379999985419:103.84913999982594;1.3924299998188665:103.84941000016268;1.3924600000654528:103.8496600003065;1.3924600000776153:103.84966000040794;1.392490000336033:103.84995999998357;1.392519999695957:103.85023000032031;1.3925199997028905:103.8502300004554;1.3925400001671269:103.85062000030763;1.3925400001671269:103.85090999967207;1.3925400001671269:103.85090999983755;1.392519999695957:103.85135000005361;1.3925199996864788:103.85135000019899;1.392490000336033:103.85181000036117;1.3924600000776153:103.85208999984532;1.3924252405316855:103.85233331672121;1.3924200000318734:103.85237000022781;1.392379999985419:103.85261000042732;1.3923300001629888:103.8528799998006;1.3923300001509282:103.85287999986576;1.3922199997928844:103.85341999964093;1.392080000066113:103.85403999977503;1.3920800000645355:103.85403999978202;1.391990000170601:103.85439999963215;1.3919900001675984:103.85439999964498;1.3918799997967053:103.85486999998543;1.391850000428601:103.8550099997275;1.3917800001190852:103.8553200001868;1.391780000105447:103.8553200002472;1.3915700000215787:103.85631000028418;1.3915700000103706:103.85631000033061;1.3914999996935649:103.85659999979978;1.3914999996901172:103.8565999998141;1.3914099997740843:103.85701999993867;1.39140999976612:103.85701999997848;1.3913999999845719:103.85707000015839;1.3913999999827782:103.85707000016737;1.3913500001296366:103.85731000035665;1.3913500001274992:103.85731000036687;1.391250000413761:103.85773999963888;1.391250000413761:103.85773999963888;1.3912299999316877:103.85784000009629;1.391210000347498:103.85792999960967;1.3911899998650814:103.85804000011284;1.3911700002805227:103.85811999958048;1.3911499997977628:103.85819999994644;1.3911300002128606:103.8582900003581;1.3910000002128553:103.85889000040774;1.3910000002069651:103.85889000043427;1.3908999995862596:103.85933999977121;1.3908999995845548:103.85933999977787;1.3907699995735832:103.85985000030745;1.3907699995659881:103.85985000033779;1.3906299997591685:103.8604100001741;1.3905778559805508:103.86061205668105;1.3905499996049133:103.86071999979549;1.3898699999802653:103.86316000017696;1.3898699999675177:103.86316000022191;1.3896800002218215:103.86382999964846;1.3896099998343057:103.86408999993948;1.3891900001672395:103.86557999954238;1.389190000159835:103.86557999956864;1.3891199997605386:103.86580999971335;1.3891199997577905:103.86580999972239;1.3890799996554095:103.86596999955597;1.3890199999496151:103.86620999975548;1.3888399999249985:103.86691000026255;1.3888399999248457:103.86691000026316;1.3887999998179101:103.86707000009609;1.3887599997101092:103.86721999988393;1.3887599997077047:103.86721999989196;1.3886999999961855:103.86741999990048;1.3886299995839506:103.86761999990469;1.3886299995796376:103.86761999991703;1.388519999964597:103.8679599996219;1.3885199999472058:103.86795999967566;1.388460000227188:103.86814999964645;1.3884600002162724:103.8681499996792;1.3883999996076204:103.86832999957147;1.3884200001136748:103.86849000030337;1.3884500004227907:103.8686199999971;1.3884500004233886:103.86861999999972;1.3884899996383824:103.86872999960457;1.3885399995542191:103.868830000062;1.3891000000859322:103.86891999956416;1.3891000001557008:103.86891999957537;1.3892899999450723:103.86894999971013;1.3892899999607842:103.8689499997126;1.389630000330106:103.86898999989559;1.3901799999787048:103.86905000017002;1.3901799999787048:103.86905000017002;1.3903699996969083:103.86907000026153;1.3903699996969083:103.86907000026153;1.3906299997591685:103.86909000035301;1.3906299997591685:103.86909000035301;1.3909200000711444:103.86911000044448;1.3912600002057158:103.86912999963766;1.3912600002057158:103.86912999963766;1.3913299996462658:103.86912999963766;1.391319999854578:103.86926000023232;1.3913199998543617:103.86926000023551;1.3912899995813262:103.86970000044836;1.391210000347498:103.87065000030236;1.3911899998650814:103.87087000041039;1.3911899998639492:103.87087000042166;1.3911400000053498:103.87137000000257;1.3911300002128606:103.8714699995617;1.3911300002125042:103.87146999956546;1.3911099997297698:103.87167999962399;1.3910400002827186:103.8723299999023;1.3909900004198064:103.87287999972324;1.3909900004198064:103.87287999972324;1.3909200000711444:103.87363999960641;1.3908399999267107:103.87439000034217;1.3908399999251841:103.87439000035684;1.3908099996485608:103.87467999986035;1.3908099996473393:103.87467999987209;1.3907699995773743:103.87500999955367;1.3907699995735832:103.87500999958499;1.3907499999855004:103.87517000031684;1.3907400001913954:103.87528999996745;1.390730000397214:103.87542999970954;1.390730000397214:103.87554000021271;1.390730000397214:103.87554000021457;1.390730000397214:103.87563999977183;1.3907300003990206:103.87563999979164;1.3907400001913954:103.875750000275;1.3907499999855004:103.87583999978838;1.3907699995735832:103.87596000033729;1.3907999998534888:103.87605999989641;1.3908499997203831:103.87618999959274;1.3908999995862596:103.87631000014166;1.390939999657815:103.87640999970075;1.3909399996602068:103.8764099997066;1.3910254607038388:103.87661890473557;";
//        String[] s = routeStr.split(";");
//        ArrayList<MyPoint> lines = new ArrayList<>();
//        for (int i = 0; i < s.length; i++) {
//            String[] m = s[i].split(":");
//            MyPoint myPoint = new MyPoint(m[0], m[1]);
//            lines.add(myPoint);
//        }
//        List<Point> points = new ArrayList<>();
//        for (int i = 0; i < lines.size(); i++) {
//            MyPoint myPoint = lines.get(i);
//            double d_lat = Double.valueOf(myPoint.getLat());
//            double d_lon = Double.valueOf(myPoint.getLng());
//            points.add(new Point(d_lon, d_lat));
//        }
//        polylinePoints.addAll(points);
////        polylinePoints.add(new Point(-118.67999016098526, 34.035828839974684));
////        polylinePoints.add(new Point(-118.65702911071331, 34.07649252525452));
//        Polyline polyline = new Polyline(polylinePoints);
//        mSimulatedLocationDataSource = new OfflineLocationDataSource(polyline);
//        // set the simulated location data source as the loc    ation data source for this app
//        locationDisplay.setLocationDataSource(mSimulatedLocationDataSource);
//        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
//        mSimulatedLocationDataSource.startAsync();
//    }
//
//
//    public class LocalReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (intent.getAction().equals(
//                    LocationManager.PROVIDERS_CHANGED_ACTION)) {
//                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                } else {
//                }
////                startService(
////                        new Intent(MainActivity.this, LocationService.class));
//            } else if (action.equals(Constants.LOCSERVER)) {
//
//                Location loc = intent.getExtras().getParcelable("location");
//                MyPoint point = new MyPoint(loc.getLatitude()+"",loc.getLongitude()+"");
//                application.setCurrentPoint(point);
//                application.setCurrentLocation(loc);
//                GeodeticDistanceResult distanceResult = GeometryEngine.distanceGeodetic(PointChange.getMapPoint(application.getPrevious_point()),PointChange.getMapPoint(point),
//                        new LinearUnit(LinearUnitId.METERS), new AngularUnit(AngularUnitId.DEGREES), GeodeticCurveType.GEODESIC);
//                Point location = new Point(PointChange.getMapPoint(point).getX(),PointChange.getMapPoint(point).getY(),SpatialReferences.getWgs84());
//                if (location != null){
//                    try {
//                        LocationDataSource.Location ldata = new LocationDataSource.Location(location, 1, 1, distanceResult.getAzimuth1(), false);
////                        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
//                        if (mSimulatedLocationDataSource != null) {
//                            mSimulatedLocationDataSource.UpdateLocation(ldata);
//                            mSimulatedLocationDataSource.startAsync();
//                        }
//
//                        //MyApplication.getInstance().setPrevious_point(point);
//                        application.setPrevious_point(point);
//
//                        //ZN - 20210204 to centre map on location change
//                        //mMapView.setViewpointCenterAsync(location);
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    private void setIncidentComplete() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
//        String now = sdf.format(new Date());
//        String userId = CacheUtils.getUserId(getApplicationContext());
//        List<NewIncident> incidentList = NewIncidentDBHelper.getInstance(getApplicationContext()).getIncidentMsg();
//        String incidentId = incidentList.get(0).getId();
//        Map<String,String> params = new HashMap<>();
//        params.put("userId",userId);
//        params.put("incidentId",incidentId);
//        params.put("completed_time", now);
//        Call<JsonObject> call = RetrofitUtils.getInstance().setIncidentComplete(params);
//        call.enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                //remove drawn route , CCP and incident
//                removeRoute();
//                mRouteOverlay.getGraphics().remove(CCP_pt_grap);
//                mGraphicsOverlay.getGraphics().removeAll(incidentGraphics);
//                isIncidentReceived = false;
//                Toast.makeText(MainActivity.this, "Incident Closed", Toast.LENGTH_LONG).show();
//                //ZN - 20200620 delete incident from mobile db
//                NewIncidentDBHelper.getInstance(getApplicationContext()).clear();
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//        });
//    }
//
//    //ZN - 20201204
//    private void setIncidentStatus(String status) {
//        Log.i("Test", "Incident Status: " + status);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
//        String now = sdf.format(new Date());
//        //String userId = CacheUtils.getUserId(getApplicationContext());
//        //String username = MyApplication.getInstance().getUsername();
//        String username = application.getUsername();
//        Log.i("Test", "setIncidentStatus: username: " + username);
//        List<NewIncident> incidentList = NewIncidentDBHelper.getInstance(getApplicationContext()).getIncidentMsg();
//        String incidentId = incidentList.get(0).getId();
//        Map<String,String> params = new HashMap<>();
//        params.put("username",username);
//        params.put("incidentId",incidentId);
//        params.put("status", status);
//        params.put("time", now);
//
//        //ZN - 20201203
//        Call<JsonObject> call = RetrofitUtils.getInstance().setIncidentStatus(params);
//
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                //show incident has been updated
//                Toast.makeText(MainActivity.this, "Incident updated", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//            }
//        });
//
//    }
//
//    private void showRoute(Point startPoint, Point endPoint, int purpose) {
//        //ZN - 20200608
//        //show route
//
//        List<Stop> stops = Arrays.asList(new Stop(startPoint), new Stop(endPoint));
//
//        //ZN - 20200624 for drawing CCP point graphic
//        BitmapDrawable CCP_pt = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.marker);
//
//        //ZN - 20210214 to store CCP as navigation destination
//        destination_point = new Point(endPoint.getX(), endPoint.getY(), endPoint.getSpatialReference());
//
//        final ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
//        routeParametersFuture.addDoneListener(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Log.i("Test", "route parameters created " + routeParametersFuture.isDone());
//
//                    // define the route parameters
//                    RouteParameters routeParameters = routeParametersFuture.get();
//                    routeParameters.setStops(stops);
//                    routeParameters.setReturnDirections(true);
//                    routeParameters.setReturnStops(true);
//                    routeParameters.setReturnRoutes(true);
//                    routeParameters.setOutputSpatialReference(SpatialReferences.getWgs84());
//
//                    Log.i("Test", "ready to run solve route task");
//                    ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
//                    routeResultFuture.addDoneListener(() -> {
//                        try {
//                            Log.i("Test", "Route results completed");
//
//                            // get the route geometry from the route result
//                            RouteResult routeResult = routeResultFuture.get();
//                            //startNavigation(routeTask,routeParameters,routeResult);
//                            Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();
//
//                            Route retRoute = routeResult.getRoutes().get(0);
//                            List<DirectionManeuver> retDirections = retRoute.getDirectionManeuvers();
//                            for (DirectionManeuver directionManeuver : retDirections) {
//                                Log.i("Directions", directionManeuver.getDirectionText());
//                            }
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
//                            mRouteOverlay.getGraphics().add(CCP_pt_grap);
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
//                        } catch (ExecutionException | InterruptedException e) {
//                            String error = "Error creating default route parameters: " + e.getMessage();
//                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
//                            Log.e("TEST", error);
//                        }
//                    });
//                } catch (InterruptedException | ExecutionException e) {
//                    String error = "Error setting the route parameters " + e.getMessage();
//                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
//                    Log.e("TEST", error);
//                }
//            }
//        });
//    }
//
//    private static List<Stop> getStops() {
//        List<Stop> stops = new ArrayList<>(2);
//        Stop conventionCenter = new Stop(new Point(103.847376, 1.390431, SpatialReferences.getWgs84()));
//        stops.add(conventionCenter);
//        Stop aerospaceMuseum = new Stop(new Point(103.876615, 1.391035, SpatialReferences.getWgs84()));
//        stops.add(aerospaceMuseum);
//        return stops;
//    }
//
////    private void startReadyNavigation2() {
////        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();
////        ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
////        routeParametersFuture.addDoneListener(() -> {
////            try {
////                RouteParameters routeParameters = routeParametersFuture.get();
////                routeParameters.setStops(getStops());
////                routeParameters.setReturnDirections(true);
////                routeParameters.setReturnStops(true);
////                routeParameters.setReturnRoutes(true);
////                routeParameters.setOutputSpatialReference(SpatialReferences.getWgs84());
////                ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
////                routeParametersFuture.addDoneListener(() -> {
////                    try {
////                        RouteResult routeResult = routeResultFuture.get();
////                        Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();
////                        Graphic routeGraphic = new Graphic(routeGeometry,
////                                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5f));
////                        mMapView.getGraphicsOverlays().get(0).getGraphics().add(routeGraphic);
////                        mMapView.setViewpointAsync(new Viewpoint(routeGeometry.getExtent()));
////                        startNavigation(routeTask, routeParameters, routeResult);
////                    } catch (ExecutionException | InterruptedException e) {
////                        String error = "Error creating default route parameters: " + e.getMessage();
////                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
////                        Log.e(TAG, error);
////                    }
////                });
////            } catch (InterruptedException | ExecutionException e) {
////                String error = "Error getting the route result " + e.getMessage();
////                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
////                Log.e(TAG, error);
////
////            }
////        });
////
////    }
//
//    private void startNavigation(RouteTask routeTask, RouteParameters routeParameters, RouteResult routeResult) {
//
//        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();
//        Polyline routeGeometry = routeResult.getRoutes().get(0).getRouteGeometry();
//        mRouteAheadGraphic = new Graphic(routeGeometry,
//                new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.MAGENTA, 5f));
//        mMapView.getGraphicsOverlays().get(0).getGraphics().add(mRouteAheadGraphic);
//        mRouteTraveledGraphic = new Graphic(routeGeometry,
//                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5f));
//        mMapView.getGraphicsOverlays().get(0).getGraphics().add(mRouteTraveledGraphic);
//        LocationDisplay locationDisplay = mMapView.getLocationDisplay();
//        mSimulatedLocationDataSource = new OfflineLocationDataSource(routeGeometry);
//        locationDisplay.setLocationDataSource(mSimulatedLocationDataSource);
//        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
//        mRouteTracker = new RouteTracker(getApplicationContext(), routeResult, 0);
//        mRouteTracker.enableReroutingAsync(routeTask, routeParameters,
//                RouteTracker.ReroutingStrategy.TO_NEXT_WAYPOINT, true);
//
////        TextView distanceRemainingTextView = findViewById(R.id.tv_remain_dist);
////        TextView timeRemainingTextView = findViewById(R.id.tv_ETA);
////        TextView nextDirectionTextView = findViewById(R.id.tv_directions);
//
//        locationDisplay.addLocationChangedListener(locationChangedlistener);
////        locationDisplay.addLocationChangedListener(locationChangedEvent -> {
////            ListenableFuture<Void> trackLocationFuture = mRouteTracker.trackLocationAsync(locationChangedEvent.getLocation());
////            trackLocationFuture.addDoneListener(() -> {
////                TrackingStatus trackingStatus = mRouteTracker.getTrackingStatus();
////                mRouteAheadGraphic.setGeometry(trackingStatus.getRouteProgress().getRemainingGeometry());
////                mRouteTraveledGraphic.setGeometry(trackingStatus.getRouteProgress().getTraversedGeometry());
////
////                // listen for new voice guidance events
////                mRouteTracker.addNewVoiceGuidanceListener(newVoiceGuidanceEvent -> {
////                    // use Android's text to speech to speak the voice guidance
////                    speakVoiceGuidance(newVoiceGuidanceEvent.getVoiceGuidance().getText());
////                    nextDirectionTextView
////                            .setText(getString(R.string.next_direction, newVoiceGuidanceEvent.getVoiceGuidance().getText()));
////                });
////
////                // get remaining distance information
////                TrackingStatus.Distance remainingDistance = trackingStatus.getDestinationProgress().getRemainingDistance();
////                // covert remaining minutes to hours:minutes:seconds
////                String remainingTimeString = DateUtils
////                        .formatElapsedTime((long) (trackingStatus.getDestinationProgress().getRemainingTime()));
////                // update text views
////                distanceRemainingTextView.setText(getString(R.string.distance_remaining, remainingDistance.getDisplayText(),
////                        remainingDistance.getDisplayTextUnits().getPluralDisplayName()));
////                timeRemainingTextView.setText(getString(R.string.time_remaining, remainingTimeString+" minutes"));
////
////                //update server on ETA
////                sendResponderETA(trackingStatus.getDestinationProgress().getRemainingTime());
////
////                if (trackingStatus.getDestinationStatus() == DestinationStatus.REACHED) {
////                    if (mRouteTracker.getTrackingStatus().getRemainingDestinationCount() > 1) {
////                        mRouteTracker.switchToNextDestinationAsync();
////                    } else {
////                        Toast.makeText(this, "Arrived at the final destination.", Toast.LENGTH_LONG).show();
////                        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();
////                        //locationDisplay.stop();
////                    }
////                }
////            });
////        });
//        locationDisplay.startAsync();
//    }
//
//    private void startReadyNavigation() {
//        //MyPoint currentPoint = MyApplication.getInstance().getCurrentPoint();
//        MyPoint currentPoint = application.getCurrentPoint();
//        Point usrPoint = new Point(Double.valueOf(currentPoint.getLng()), Double.valueOf(currentPoint.getLat()), SpatialReferences.getWgs84());
//
//        //ZN - 20210213 dest location , either CCP or evac pt
//        //Point dest = new Point(103.85739813145422, 1.3434836900969456, SpatialReferences.getWgs84());
//        List<Stop> stops = Arrays.asList(new Stop(usrPoint), new Stop(destination_point));
//
//        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();
//        ListenableFuture<RouteParameters> routeParametersFuture = routeTask.createDefaultParametersAsync();
//        routeParametersFuture.addDoneListener(() -> {
//            try {
//                RouteParameters routeParameters = routeParametersFuture.get();
//                routeParameters.setStops(stops);
//                routeParameters.setReturnDirections(true);
//                routeParameters.setReturnStops(true);
//                routeParameters.setReturnRoutes(true);
//                routeParameters.setOutputSpatialReference(SpatialReferences.getWgs84());
//                ListenableFuture<RouteResult> routeResultFuture = routeTask.solveRouteAsync(routeParameters);
//                routeParametersFuture.addDoneListener(() -> {
//                    try {
//                        RouteResult routeResult = routeResultFuture.get();
//                        startNavigation(routeTask, routeParameters, routeResult);
//                    } catch (ExecutionException | InterruptedException e) {
//                        String error = "Error creating default route parameters: " + e.getMessage();
//                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//                        Log.e("Test", error);
//                    }
//                });
//            } catch (InterruptedException | ExecutionException e) {
//                String error = "Error getting the route result " + e.getMessage();
//                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//                Log.e("Test", error);
//            }
//        });
//
//    }
//
//    //ZN - 20201201
//    GpsStatus.Listener statusListener = new GpsStatus.Listener() {
//        public void onGpsStatusChanged(int event) {
//            switch (event) {
//                case GpsStatus.GPS_EVENT_FIRST_FIX:
//                    break;
//                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                    checkGps_event_satellite_status();
//                    break;
//                case GpsStatus.GPS_EVENT_STARTED:
//                    break;
//                case GpsStatus.GPS_EVENT_STOPPED:
//                    break;
//            }
//        };
//    };
//
//    //ZN - 20201201
//    private void checkGps_event_satellite_status() {
//        int gpscount=0;
//        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for userCoordinateActivity#requestPermissions for more details.
//            return;
//        }
//        GpsStatus gpsStauts = lm.getGpsStatus(null);
//        int maxSatellites = gpsStauts.getMaxSatellites();
//        Iterator<GpsSatellite> it = gpsStauts.getSatellites().iterator();
//        while (it.hasNext() && gpscount <= maxSatellites) {
//            GpsSatellite s = it.next();
//            if(s.usedInFix()){
//                gpscount++;
//            }
//        }
////        MyApplication.getInstance().getUserCoordinates().setSatellite_count(gpscount);
//        application.getUserCoordinates().setSatellite_count(gpscount);
//        if (gpscount < 3) {
//
//        } else {
//
//        }
////        Log.e("TEST",gpscount+"------getMaxSatellites");
//    }
//
//
//
//
//    //ZN - 20201216
//    //notification code
//    PendingIntent pendingIntent;
//    NotificationCompat.Builder builder;
//    Intent intent_watchIncidentActivity;
//
//    private void createNotificationAndIntent() {
//        NewIncident incident = NewIncidentDBHelper.getInstance(MainActivity.this).getLatestIncidentMsg();
//
////        Intent intent_t = new Intent(getApplicationContext(), MainActivity.class);
////        intent_t.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent_t, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        intent_watchIncidentActivity = new Intent(MainActivity.this, WatchIncidentActivity.class);
//        intent_watchIncidentActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent_watchIncidentActivity, PendingIntent.FLAG_UPDATE_CURRENT);
//
////        builder  = new NotificationCompat.Builder(this, "Testing")
////                .setSmallIcon(R.mipmap.red_cross)
////                .setContentTitle("New Incident")
////                .setContentText("CCP Alpha, 2 Casualties")
////                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                .setContentIntent(pendingIntent)
////                .setAutoCancel(true);
//
//        //ZN - 20210503 fix for alert tone
//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        //ZN - 20201221
//        //adjust for more text in notification
//        builder  = new NotificationCompat.Builder(this, "Testing")
//                .setSmallIcon(R.mipmap.red_cross)
//                .setContentTitle("New Incident")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(n_incident_LUP + "\n" + n_incident_type + "\n" + n_incident_location))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setSound(soundUri)
//                .setVibrate(new long[] {1000,1000})
//                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
//                .setAutoCancel(true);
//
//
//    }
//
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Sample";
//            String description = "Testing Notification";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("Testing", name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//        }
//    }
//
//    //ZN - 20201218
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void showAllIncidentUpdateIconVisible() {
//        ImageView iv = (ImageView) findViewById(R.id.iv_leftbase);
//
//        LinearLayout iv_bg = (LinearLayout) findViewById(R.id.iv_bg);
//
//        iv_bg.setVisibility(View.VISIBLE);
//        iv.setVisibility(View.VISIBLE);
//        iv.setImageResource(0);
//        //iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.base_exit));
//        iv.setImageDrawable(this.getResources().getDrawable(R.mipmap.base_out_clear));
//        TextView tv_statusText = (TextView) findViewById(R.id.tv_statusText);
//
//        //ZN - 20210119
//        tv_statusText.setVisibility(View.VISIBLE);
//        tv_statusText.setText("Left Base");
//
//        //ZN - 20210201
//        LinearLayout mainLayout = (LinearLayout)this.findViewById(R.id.ll_menmue);
//        mainLayout.setVisibility(LinearLayout.VISIBLE);
//
//        //ZN - 20210620
//        iv_phone.setVisibility(View.VISIBLE);
//
//        //ZN - 20210624
//        iv_incident.setVisibility(View.VISIBLE);
//
//    }
//
//    //ZN - 20210123
//    private void sendResponderRoute(StringBuilder route, double dist, double time, int purpose) {
//        Map<String,String> params = new HashMap<>();
//        params.put("user_name",MyApplication.getInstance().getUsername());
//        params.put("incident_id",MyApplication.getInstance().getCurrentIncidentID());
//        params.put("route_pts",route.toString());
//        params.put("distance",Double.toString(dist));
//        params.put("ETA",Double.toString(time));
//
//        //ZN - 20210510 add in new param to determine type of route (incident, evac)
//        params.put("purpose", Integer.toString(purpose));
//
//        //ZN - 20210510 put in extra evac params if route is evac (lat:lon)
//        if (purpose == FOR_EVAC) {
//            params.put("evac_name", evacName);
//            params.put("evac_point", evacPoint.getY() + ":" + evacPoint.getX());
//        }
//
//        Call<JsonObject> call = RetrofitUtils.getInstance().setResponderRoute(params);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.e("TEST",response.body().toString());
//                showToast("Success send Route data");
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
//                showToast("Failure to send Route data");
//            }
//        });
//
//    }
//
//    //ZN - 20210214 for navigation text-to-speech
//    private void speakVoiceGuidance(String voiceGuidanceText) {
//        if (mIsTextToSpeechInitialized && !mTextToSpeech.isSpeaking()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mTextToSpeech.speak(voiceGuidanceText, TextToSpeech.QUEUE_FLUSH, null, null);
//            } else {
//                mTextToSpeech.speak(voiceGuidanceText, TextToSpeech.QUEUE_FLUSH, null);
//            }
//        }
//    }
//
//    //ZN - 20210215
//    private void sendResponderETA(double time) {
//        Map<String,String> params = new HashMap<>();
//        params.put("user_name",MyApplication.getInstance().getUsername());
//        params.put("incident_id",MyApplication.getInstance().getCurrentIncidentID());
//        params.put("ETA",Double.toString(time));
//
//        Call<JsonObject> call = RetrofitUtils.getInstance().setResponderETA(params);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                Log.e("TEST",response.body().toString());
//                showToast("Success send ETA");
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("TEST",t.getMessage().toString());
//                showToast("Failure to send Route data");
//            }
//        });
//
//    }
//
//    //ZN - 20210221 method to remove route from map
//    private void removeRoute() {
//        mRouteOverlay.getGraphics().remove(routeGraphic);
//    }
//
//    //ZN - 20210222 method to stop navigation processes
//    private void stopNavigation() {
//        //clear all navi routes
//        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();
//
//        //stop text to speech
//        mTextToSpeech.stop();
//        mTextToSpeech.shutdown();
//
//        //remove related route tracker stuff
//        //need to have declared listener
//        mRouteTracker.removeNewVoiceGuidanceListener(newVoiceGuidanceListener);
//        mRouteTracker.disableRerouting();
//        mMapView.getLocationDisplay().removeLocationChangedListener(locationChangedlistener);
//
//        //stop location icon from getting updates from arcgis location service
//        mMapView.getLocationDisplay().removeLocationChangedListener(locationChangedlistener);
//        //mMapView.getLocationDisplay().stop();
//    }
//
//    LocationDisplay.LocationChangedListener locationChangedlistener = new LocationDisplay.LocationChangedListener() {
////        TextView distanceRemainingTextView = findViewById(R.id.tv_remain_dist);
////        TextView timeRemainingTextView = findViewById(R.id.tv_ETA);
////        TextView nextDirectionTextView = findViewById(R.id.tv_directions);
//
//        @Override
//        public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
//            ListenableFuture<Void> trackLocationFuture = mRouteTracker.trackLocationAsync(locationChangedEvent.getLocation());
//            trackLocationFuture.addDoneListener(() -> {
//                TrackingStatus trackingStatus = mRouteTracker.getTrackingStatus();
//                mRouteAheadGraphic.setGeometry(trackingStatus.getRouteProgress().getRemainingGeometry());
//                mRouteTraveledGraphic.setGeometry(trackingStatus.getRouteProgress().getTraversedGeometry());
//
//                // listen for new voice guidance events
//                mRouteTracker.addNewVoiceGuidanceListener(newVoiceGuidanceListener);
////                mRouteTracker.addNewVoiceGuidanceListener(newVoiceGuidanceEvent -> {
////                    // use Android's text to speech to speak the voice guidance
////                    speakVoiceGuidance(newVoiceGuidanceEvent.getVoiceGuidance().getText());
////                    nextDirectionTextView
////                            .setText(getString(R.string.next_direction, newVoiceGuidanceEvent.getVoiceGuidance().getText()));
////                });
//
//                // get remaining distance information
//                TrackingStatus.Distance remainingDistance = trackingStatus.getDestinationProgress().getRemainingDistance();
//                // covert remaining minutes to hours:minutes:seconds
//                String remainingTimeString = DateUtils
//                        .formatElapsedTime((long) (trackingStatus.getDestinationProgress().getRemainingTime()));
//                // update text views
//                distanceRemainingTextView.setText(getString(R.string.distance_remaining, remainingDistance.getDisplayText(),
//                        remainingDistance.getDisplayTextUnits().getPluralDisplayName()));
//                timeRemainingTextView.setText(getString(R.string.time_remaining, remainingTimeString+" minutes"));
//
//                //update server on ETA
//                sendResponderETA(trackingStatus.getDestinationProgress().getRemainingTime());
//
//                if (trackingStatus.getDestinationStatus() == DestinationStatus.REACHED) {
//                    if (mRouteTracker.getTrackingStatus().getRemainingDestinationCount() > 1) {
//                        mRouteTracker.switchToNextDestinationAsync();
//                    } else {
//                        //Toast.makeText(this, "Arrived at the final destination.", Toast.LENGTH_LONG).show();
//                        nextDirectionTextView
//                                .setText("Arrived at the final destination");
//                        mMapView.getGraphicsOverlays().get(0).getGraphics().clear();
//                        //locationDisplay.stop();
//                    }
//                }
//            });
//        }
//    };
//
//    RouteTracker.NewVoiceGuidanceListener newVoiceGuidanceListener = new RouteTracker.NewVoiceGuidanceListener() {
//        //TextView nextDirectionTextView = findViewById(R.id.tv_directions);
//
//        @Override
//        public void onNewVoiceGuidance(RouteTracker.NewVoiceGuidanceEvent newVoiceGuidanceEvent) {
//            speakVoiceGuidance(newVoiceGuidanceEvent.getVoiceGuidance().getText());
//            nextDirectionTextView
//                    .setText(getString(R.string.next_direction, newVoiceGuidanceEvent.getVoiceGuidance().getText()));
//        }
//    };
//
//    @Override
//    public void onBackPressed() {
//        Log.i("BACK", "back button pressed");
//        Intent intent = new Intent(MainActivity.this, NewMainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        startActivity(intent);
//
//        ////ZN - 20210420 pause listening for incidents
//        unregisterReceiver(newMReceiver);
//        List<ResolveInfo> receivers = getPackageManager().queryBroadcastReceivers(new Intent("NewMQTTMsgReceiver"), 0);
//        Log.i("RCV", "[MainActivity] unregister: number of MQTT rcv: " + receivers.size());
//    }
//
//    //ZN - 20210427 method to show evac point and create route
//    private void updateAndShowEvacRoute(String evacLocation) {
//        MyPoint currentPoint = application.getCurrentPoint();
//        Point usrPoint = new Point(Double.valueOf(currentPoint.getLng()), Double.valueOf(currentPoint.getLat()), SpatialReferences.getWgs84());
//        evacName = evacLocation;
//
//        //get lat lon of evac point
//        String[] evac_lat_lon = ((String) evacLocations.get(evacLocation)).split(",");
//        evacPoint = new Point(Double.valueOf(evac_lat_lon[1]), Double.valueOf(evac_lat_lon[0]), SpatialReferences.getWgs84());
//        Log.i("EVAC", "selected evac point: " + evacLocation + " " +  evacPoint.getX() + " " + evacPoint.getY());
//
//        BitmapDrawable redwarn = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.redwarn);
//        PictureMarkerSymbol redwarnpms = new PictureMarkerSymbol(redwarn);
//        redwarnpms.setHeight(32);
//        redwarnpms.setWidth(32);
//        redwarnpms.loadAsync();
//
//        Graphic redwgp = new Graphic(evacPoint, redwarnpms);
//        //redwgp.getAttributes().put("incidentNo", incident.getId());
//        incidentGraphics.add(redwgp);
//
//        //mMapView.setViewpointCenterAsync(evacPoint);
//        showRoute(usrPoint, evacPoint, FOR_EVAC);
//
//
////        mGraphicsOverlay.getGraphics().addAll(incidentGraphics);
////        mMapView.invalidate();
}

