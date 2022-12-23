package com.example.st.arcgiscss.constant;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask;
import com.example.st.arcgiscss.activites.LoginActivity;
import com.example.st.arcgiscss.dao.DataBaseHelper;
import com.example.st.arcgiscss.model.MyPoint;
import com.example.st.arcgiscss.model.NavigationInfo;
import com.example.st.arcgiscss.model.RouteWay;
import com.example.st.arcgiscss.model.UserPosition;
import com.example.st.arcgiscss.upload.UserCoordinate;
import com.example.st.arcgiscss.util.CacheUtils;
import com.example.st.arcgiscss.util.LogcatHelper;
import com.example.st.arcgiscss.util.TypefaceUtil;

import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MyApplication extends Application implements TextToSpeech.OnInitListener, LifecycleObserver {
	private static final int READ_PHONE_STATE = 100;
	private static MyApplication instance;
	public static SharedPreferences sharedPreferences;
	private static final String TAG = "QSBApplication";

	private static MyApplication mInstance;

	private NavigationInfo nearNavigationInfo;
	private String convyNumber;

//	private UserCoordinate userCoordinates;

	private UserPosition userPosition;

	private boolean isNavigation=false;

	public boolean isUploadServer=false;

	public boolean isWriterServer=false;

	public RouteWay routeWay;

	public boolean exit_nav_model = false;

	public boolean isExit_nav_model() {
		return exit_nav_model;
	}

	public void setExit_nav_model(boolean exit_nav_model) {
		this.exit_nav_model = exit_nav_model;
	}

	public RouteWay getRouteWay() {
		return routeWay;
	}

	public void setRouteWay(RouteWay routeWay) {
		this.routeWay = routeWay;
	}

	//ZN - 20201201
	private UserCoordinate userCoordinates;

	//ZN - 20210201
	private String username;

	//ZN - 20120215
	private String currentIncidentID;

	//ZN - 20210420
	private boolean isIncidentAck;
	private MobileMapPackage mmpk;
	private RouteTask routeTask;

	//ZN - 20221123
	private ArcGISMap map;

	//ZN - 20210611
	private MyPoint homePoint;

	//ZN - 20210620
	private String POC_Contact = "";
	private String POC_Name = "";
	private String POC_Unit = "";
	private boolean isCheckUpdatePOC;

	//ZN - 20210819
	private boolean isRcvIncidentNotification;

	//ZN - 20210922
	private int incompleteIncidentStatus;

	//ZN - 20220208 delete apk upon first run of app - global variable of version number
	private String str_version_number = "v0.5.0";

	//ZN - 20220629 sending of logs to server
	private String logDirectory;
	private String logFilename;

	public MyApplication() {
		Log.i("MyApplication", "MyApplication constructor called");
	}

	public NavigationInfo getNearNavigationInfo() {
		return nearNavigationInfo;
	}

	public void setNearNavigationInfo(NavigationInfo nearNavigationInfo) {
		this.nearNavigationInfo = nearNavigationInfo;
	}

	public String getDistancetravel() {
		return distancetravel;
	}

	public void setDistancetravel(String distancetravel) {
		this.distancetravel = distancetravel;
	}

	public String distancetravel;


	public boolean isGetServer = false;

	private MyPoint currentPoint = null;//user current coordinates
	//User's current geographic information
	private Location currentLocation = null;

	private MyPoint startPoint;//navigation start coordinates, the default is the user's current coordinates
	private MyPoint endPoint;//navigation endpoint coordinates, the default is null

	private boolean b_exit =false;

	private int frequency = 10;

	private int signalStrength = 0;

	private List<String> waypointName = new ArrayList<>();

	private Boolean isStart = true;

	private Boolean isEnd = true;

	public List<String> getWaypointName() {
		return waypointName;
	}

	public void setWaypointName(List<String> waypointName) {
		this.waypointName = waypointName;
	}

	public Boolean getStart() {
		return isStart;
	}

	public void setStart(Boolean start) {
		isStart = start;
	}

	public Boolean getEnd() {
		return isEnd;
	}

	public void setEnd(Boolean end) {
		isEnd = end;
	}

	public MyPoint getPrevious_point() {
		return previous_point;
	}

	public void setPrevious_point(MyPoint previous_point) {
		this.previous_point = previous_point;
	}

	private MyPoint previous_point;

    private int satellite_count = 0;

	private int batteryCount = 0;

	private TextToSpeech tts;

	public static MyApplication getInstance() {
		return mInstance;
	}

	private String imei;

	private String timeRemaining;

	public static Typeface typeFace;

	private Bitmap shotMap;

	private List<NavigationInfo> mNavigationInfos = new ArrayList<>();

	public List<NavigationInfo> getmTravedNInfos() {
		return mTravedNInfos;
	}

	public void setmTravedNInfos(List<NavigationInfo> mTravedNInfos) {
		this.mTravedNInfos = mTravedNInfos;
	}

	private List<NavigationInfo> mTravedNInfos = new ArrayList<>();

	private Point nearRoutePoint;

	public Point getNearRoutePoint() {
		return nearRoutePoint;
	}

	public void setNearRoutePoint(Point nearRoutePoint) {
		this.nearRoutePoint = nearRoutePoint;
	}

	public List<NavigationInfo> getmNavigationInfos() {
		return mNavigationInfos;
	}

	public void setmNavigationInfos(List<NavigationInfo> mNavigationInfos) {
		this.mNavigationInfos = mNavigationInfos;
	}

	public Bitmap getShotMap() {
		return shotMap;
	}

	public void setShotMap(Bitmap shotMap) {
		this.shotMap = shotMap;
	}

	private LocalBroadcastManager localBroadcastManager;



	public MyPoint getStartPoint() {
		return startPoint;
	}


	public void setStartPoint(MyPoint startPoint) {
		this.startPoint = startPoint;
	}


	public MyPoint getEndPoint() {
		return endPoint;
	}


	public void setEndPoint(MyPoint endPoint) {
		this.endPoint = endPoint;
	}

	public UserPosition getUserPosition() {
		return userPosition;
	}

	public void setUserPosition(UserPosition userPosition) {
		this.userPosition = userPosition;
	}

	private DataBaseHelper dbHelper;


	public LocalBroadcastManager getLocalBroadcastManager() {

		return localBroadcastManager;
	}

	public void setLocalBroadcastManager(LocalBroadcastManager localBroadcastManager) {
		this.localBroadcastManager = localBroadcastManager;
	}

	public String getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(String timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	//ZN - 20201201
	public UserCoordinate getUserCoordinates() {
		return userCoordinates;
	}

	public void setUserCoordinates(UserCoordinate userCoordinates) {
		this.userCoordinates = userCoordinates;
	}

	//ZN - 20210201
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	//ZN - 20210215
	public String getCurrentIncidentID() {
		return currentIncidentID;
	}

	public void setCurrentIncidentID(String currentIncidentID) {
		this.currentIncidentID = currentIncidentID;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		localBroadcastManager= LocalBroadcastManager.getInstance(this);
		sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		tts=new TextToSpeech(this, this );
		b_exit = false;
		x.Ext.init(this); // After this step, we can use x.app() anywhere to get an instance of the Application..
		x.Ext.setDebug(true); // Whether to output debug logs
		String typeFonts = CacheUtils.getTypeFonts(this);
        if (typeFonts == null) {
        	typeFonts = "fonts/tw_cen_mt.ttf";
		}else {
        	typeFonts = "fonts/"+typeFonts+".ttf";
		}
		TypefaceUtil.replaceSystemDefaultFont(this,typeFonts);

		//ZN - 20220619 logging to external file - log app in background / foreground
//		LogcatHelper.getInstance(getApplicationContext()).start();
		ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

//		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//			// TODO: Consider calling
//			//    ActivityCompat#requestPermissions
//			// here to request the missing permissions, and then overriding
//			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//			//                                          int[] grantResults)
//			// to handle the case where the user grants the permission. See the documentation
//			// for ActivityCompat#requestPermissions for more details.
//			return;
//		}
//		setTypeface();

		//ZN - 20220208 check for valid IMEI - use phone number instead
//		SubscriptionManager subsManager = (SubscriptionManager) this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
//		List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();
//		if (subsList!=null) {
//			for (SubscriptionInfo subsInfo : subsList) {
//				if (subsInfo != null) {
//					imei  = subsInfo.getNumber();
//				}
//			}
//		}

//		Log.i("TEST", "imei is : " + imei);

	}

	public void setTypeface(){
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/tw_cen_mt.ttf");
		try
		{
			Field field = Typeface.class.getDeclaredField("SANS_SERIF");
			field.setAccessible(true);
			field.set(null, typeFace);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onInit(int status) {
		//If the TTS engine is installed successfully
		if(status==TextToSpeech.SUCCESS){
			tts.setLanguage(Locale.getDefault());
		}
	}

	public TextToSpeech getTts() {
		return tts;
	}

	public void setTts(TextToSpeech tts) {
		this.tts = tts;
	}

	public DataBaseHelper getDbHelper() {
		if(dbHelper!=null){
			return dbHelper;
		}
		return new DataBaseHelper(this);
	}

	public void setCurrentPoint(MyPoint currentPoint) {
		this.currentPoint = currentPoint;
	}


	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public MyPoint getCurrentPoint() {
		return currentPoint;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getFrequency() {
		return frequency;
	}
	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public int getBatteryCount() {
		return batteryCount;
	}

	public void setBatteryCount(int batteryCount) {
		this.batteryCount = batteryCount;
	}


	public boolean isB_exit() {
		return b_exit;
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public int getSatellite_count() {
		return satellite_count;
	}

	public void setSatellite_count(int satellite_count) {
		this.satellite_count = satellite_count;
	}

	public boolean isNavigation() {
		return isNavigation;
	}

	public void setNavigation(boolean navigation) {
		isNavigation = navigation;
	}

	public void setB_exit(boolean b_exit) {
		this.b_exit = b_exit;
	}

	public void loginAgain(String message){
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putBoolean(Constants.INTENT_LOGIN_AGAIN,true);
			bundle.putString(Constants.INTENT_LOGIN_Message,message);
			intent.putExtras(bundle);
			startActivity(intent);
	}

	//ZN - 20210420
	public boolean isIncidentAck() {
		return isIncidentAck;
	}

	//ZN - 20210420
	public void setIncidentAck(boolean incidentAck) {
		isIncidentAck = incidentAck;
		Log.i("MyApplication", "setIncidentAck: " + isIncidentAck);
	}

	//ZN - 20210420
	public MobileMapPackage getMobileMapPackage() {
		return this.mmpk;
	}

	//ZN -20210420
	public void setMobileMapPackage(MobileMapPackage mmpk) {
		this.mmpk = mmpk;
	}

	//ZN - 20210420
	public RouteTask getRouteTask() {
		return this.routeTask;
	}

	//ZN - 20210420
	public void setRouteTask(RouteTask routeTask) {
		this.routeTask = routeTask;
	}

	public String getPOC_Contact() {	return POC_Contact;	}

	public void setPOC_Contact(String POC_Contact) {	this.POC_Contact = POC_Contact;	}

	public String getPOC_Name() {	return POC_Name;	}

	public void setPOC_Name(String POC_Name) {	this.POC_Name = POC_Name;	}

	public String getPOC_Unit() {	return POC_Unit;	}

	public void setPOC_Unit(String POC_Unit) {	this.POC_Unit = POC_Unit;	}

	//ZN - 20210711
	public boolean isCheckUpdatePOC() {
		return isCheckUpdatePOC;
	}

	public void setCheckUpdatePOC(boolean isCheckUpdatePOC) {
		this.isCheckUpdatePOC = isCheckUpdatePOC;
	}

	//ZN - 20210427 declare constants for hospital names
	public static final String EVAC_AH = "Alexandra Hospital";
	public static final String EVAC_KTPH = "Khoo Teck Puat Hospital";
	public static final String EVAC_NTFGH = "Ng Teng Fong Hospital";
	public static final String EVAC_NUH = "National University Hospital";
	public static final String EVAC_SGH = "Singapore General Hospital";
	public static final String EVAC_MC = "Medical Centre";

	//ZN - 20211201 add new hospitals - CGH, SKGH, IMH
	public static final String EVAC_CGH = "Changi General Hospital";
	public static final String EVAC_SKGH = "Sengkang General Hospital";
	public static final String EVAC_IMH = "Institute of Mental Health";

	//ZN - 20210819 declare constants for peer relationship
	//key: username, value: peer of username
	public static HashMap<String, String> peerList;
	static {
		peerList = new HashMap<String,String>();
		peerList.put("PL EAS", "KH EAS");
		peerList.put("KH EAS", "SG EAS" + ":" + "KJ EAS");
		peerList.put("KJ EAS", "KH EAS" + ":" + "MH EAS");
		peerList.put("MH EAS", "KJ EAS" + ":" + "NS EAS");
		peerList.put("NS EAS", "MH EAS");
		peerList.put("SG EAS", "KH EAS");
	}

	//ZN - 20210819 declare constants for non-peer relationship
	//key: username, value: peer of username
	public static HashMap<String, String> nonPeerList;
	static {
		nonPeerList = new HashMap<String,String>();
		nonPeerList.put("PL EAS", "SG EAS" + ":" + "KJ EAS" + ":" + "MH EAS" + ":" + "NS EAS");
		nonPeerList.put("KH EAS", "MH EAS" + ":" + "NS EAS" + ":" + "PL EAS");
		nonPeerList.put("KJ EAS", "SG EAS" + ":" + "PL EAS" + ":" + "NS EAS");
		nonPeerList.put("MH EAS", "SG EAS" + ":" + "PL EAS" + ":" + "KH EAS");
		nonPeerList.put("NS EAS", "SG EAS" + ":" + "PL EAS" + ":" + "KH EAS" + ":" + "KJ EAS");
		nonPeerList.put("SG EAS", "MH EAS" + ":" + "PL EAS" + ":" + "NS EAS" + ":" + "KJ EAS");
	}

	//ZN - 20210819 hashmap for EAS username and status
	//key: username, value: status
	private HashMap<String, String> userEASStatus;
	public HashMap<String, String> getUserEASStatus() {
		return userEASStatus;
	}

	//ZN - 20220322 smart search feature for medical centre / hospital
	public HashMap<String, String> evac_mc_Locations = new HashMap<String, String>();
	public HashMap<String, String> evac_hos_Locations = new HashMap<String, String>();

	//ZN - 20220224 new send-out feature - add to incident record
	private String str_sendOutIncidentID;

	//ZN - 20220606 store and forward
	private HashMap<String, String> activation_timings = new HashMap<String, String>();

	public void setUserEASStatus(HashMap<String, String> userEASStatus) {
		this.userEASStatus = userEASStatus;
	}

	//ZN - 20210819
	public boolean isRcvIncidentNotification() {
		return isRcvIncidentNotification;
	}

	public void setRcvIncidentNotification(boolean rcvIncidentNotification) {
		isRcvIncidentNotification = rcvIncidentNotification;
	}

	//ZN - 20210922
	public int getIncompleteIncidentStatus() {
		return incompleteIncidentStatus;
	}

	public void setIncompleteIncidentStatus(int incompleteIncidentStatus) {
		this.incompleteIncidentStatus = incompleteIncidentStatus;
	}

	//ZN - 20220208 delete apk upon first run of app
	public String getVersionNumber() {
		return str_version_number;
	}

	public void setVersionNumber(String str_version_number) {
		this.str_version_number = str_version_number;
	}

	//ZN - 20220322 smart search feature for medical centre / hospital
	public HashMap getEvacMCLocations() {
		return evac_mc_Locations;
	}

	public void setEvacMCLocations(HashMap evac_mc_Locations) {
		this.evac_mc_Locations = evac_mc_Locations;
	}

	public HashMap<String, String> getEvacHospitalLocations() {
		return evac_hos_Locations;
	}

	public void setEvacHospitalLocations(HashMap<String, String> evac_hos_Locations) {
		this.evac_hos_Locations = evac_hos_Locations;
	}

	//ZN - 20220224 new send-out feature - add to incident record
	public String getSendOutIncidentID() {
		return str_sendOutIncidentID;
	}

	public void setSendOutIncidentID(String str_sendOutIncidentID) {
		this.str_sendOutIncidentID = str_sendOutIncidentID;
	}

	//ZN - 20220606 store and forward
	public HashMap<String, String> getActivationTimingsMap() {
		return this.activation_timings;
	}

	//ZN - 20220629 sending of logs to server
	public String getLogDirectory() {
		return logDirectory;
	}

	//ZN - 20220629 sending of logs to server
	public void setLogDirectory(String logDirectory) {
		this.logDirectory = logDirectory;
	}

	//ZN - 20220629 sending of logs to server
	public String getLogFilename() {
		return logFilename;
	}

	//ZN - 20220629 sending of logs to server
	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}

	//ZN - 20221123
	public ArcGISMap getMap() {
		return map;
	}

	//ZN - 20221123
	public void setMap(ArcGISMap map) {
		this.map = map;
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	public void onAppBackgrounded() {
		//ZN - 20220619 logging to external file
		Log.i("EVENT", "[onAppBackgrounded] app in background");

	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	public void onAppForegrounded() {
		//ZN - 20220619 logging to external file
		Log.i("EVENT", "[onAppForegrounded] app in foreground");
	}

	//	@Override
//	public void uncaughtException(Thread thread, Throwable ex) {
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		android.os.Process.killProcess(android.os.Process.myPid());
//		System.exit(1);
//	}

}
