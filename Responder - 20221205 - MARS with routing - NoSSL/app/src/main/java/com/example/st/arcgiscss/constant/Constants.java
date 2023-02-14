package com.example.st.arcgiscss.constant;


import com.example.st.arcgiscss.util.CacheUtils;

import org.json.JSONObject;

import java.util.List;

public class Constants {

	public final static boolean IS_DEBUG = true;

	/**Location Service**/
	public static final String LOCSERVER="ggx.server";
	public final static String USER_CHECK = "usercheck";
	public static final String LOGIN_USER = "loginuser";
	public static final String LOGIN_PWD = "loginpwd";

	//Lightweight cache data
	public static String CACHE_INFO = "cache";

	public static String CACHE_V_Number = "VehicleNo";
	public static String CACHE_APPOINT = "Appointment";


	public static String CACHE_PROTOCOL = "http";
	public static String CACHE_IP = "172.20.10.4";
	public static String CACHE_PORT = "3333";

	public static String CACHE_FRIENDLIST = "";
	public static String CACHE_USER = "CACHENEWUSER";

	public static String  USERID = "";

	public static String CACHE_IMEI = "imei";
	public static String CACHE_TOKEN = "token";

	public static String CACHE_SENDTIME = "sendlocation";

	public static String CACHE_AllowImage = "allowImage";
	public static String CACHE_AllowVideo = "allowVideo";

	public static String CACHE_MAPNAME = "mapName";

	public static String CACHE_TYPEFONTS = "typeFonts";

	public final static String SHARED_PREFERENCES = "userInfo";

	public final static String LOGIN_ACCOUNT = "account";

	public final static String LOGIN_CHECK = "logined";

	public final static String INTENT_LOGIN_AGAIN = "login_again";

	public final static String INTENT_LOGIN_Message = "login_message";

	public final static String CACHE_CURRENT = "current_point";


	public final static String USER_ID = "userID";

	public final static String ACTIVATIONLOCATION = "activationLocation";

	public static int PORT = 3100;

	public static final int TIME_OUT = 3000;

	public static final String ACTION_CONNECT_STATUS = "android.intent.action.NET_CONNECT";

	public static String USER_NAME = "";
	public static String PWD = "";

	public static String getIP(){
		return CacheUtils.getIP(MyApplication.getInstance().getApplicationContext()) ;
	}

	public static String getPort(){
		return CacheUtils.getPort(MyApplication.getInstance().getApplicationContext()) ;
	}

	public static String getProtocol(){
		return CacheUtils.getProtocol(MyApplication.getInstance().getApplicationContext()) ;
	}

	/**Navigation broadcast service**/
	public static final String KEY_INFO="com.st.ns.keyinfo";//Send key information broadcast

	/*
	 * Mobile phone and MapQuest connection address
	 */
	private static String QUEST_KEY="xL1obTJToNyvGHWqlMfnXvTAKdSUGa09";
	public static String MAP_QUEST_REQUEST="https://open.mapquestapi.com/guidance/v1/route?key="+QUEST_KEY+
			"&narrativeType=text&fishbone=false&unit=k&routeType=shortest&generalize=0&";
	public static String MAP_QUEST_REQUEST_V2="https://open.mapquestapi.com/directions/v2/route?key="+QUEST_KEY+
			"&narrativeType=text&routeType=shortest&unit=k&generalize=0&drivingStyle=2&";
	//Search coordinates based on place names
//	public static String SEARCH_ADDRESS="http://photon.komoot.de/api/?q=";
	public static String SEARCH_ADDRESS="http://photon.komoot.de/api/?q=";

	public static String UPLOAD_LOCATION_URL="http://thecrowdera.com/api/reportLocation";

}
