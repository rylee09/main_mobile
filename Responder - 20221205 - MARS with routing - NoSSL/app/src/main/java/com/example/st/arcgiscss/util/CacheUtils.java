package com.example.st.arcgiscss.util;

import android.content.Context;
import android.util.Log;

import com.example.st.arcgiscss.constant.Constants;
import com.example.st.arcgiscss.model.NewUser;
import com.example.st.arcgiscss.model.User;


public class CacheUtils {
    private static final String TAG = "CacheUtils";


    public static void clearCacheInfo(Context context) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.clear();
        Log.d(TAG, "clear all info success!");
    }



    public static void savePort(Context context,
                                    String port) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_PORT, port);

    }





    public static String getPort(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String port =  mCache
                .getAsString(Constants.CACHE_PORT);

        return port;
    }



    public static void saveProtocol(Context context,
                              String protocol) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_PROTOCOL, protocol);

    }


    public static String getProtocol(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String protocol =  mCache
                .getAsString(Constants.CACHE_PROTOCOL);

        return protocol;
    }


    public static void saveIp(Context context,
                                  String ip) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_IP, ip);

    }


    public static String getIP(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String token =  mCache
                .getAsString(Constants.CACHE_IP);

        return token;
    }



    public static void saveToken(Context context,
                              String token) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_TOKEN, token);

    }





    public static String getToken(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String ip =  mCache
                .getAsString(Constants.CACHE_TOKEN);

        return ip;
    }


    public static void saveMapName(Context context,
                              String mapName) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_MAPNAME, mapName);

    }


    public static String getMapName(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String mapName =  mCache
                .getAsString(Constants.CACHE_MAPNAME);

        return mapName;
    }


    public static void saveTypeFonts(Context context,
                                   String mapName) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_TYPEFONTS, mapName);

    }


    public static String getTypeFonts(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String mapName =  mCache
                .getAsString(Constants.CACHE_TYPEFONTS);

        return mapName;
    }

    public static void saveVehicleNo(Context context,
                              String number) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_V_Number, number);

    }


    public static String getVehicleNo(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String number =  mCache
                .getAsString(Constants.CACHE_V_Number);

        return number;
    }


    public static void saveAppoint(Context context,
                                     String number) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_APPOINT, number);

    }


    public static String getAppoint(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String number =  mCache
                .getAsString(Constants.CACHE_APPOINT);

        return number;
    }


    public static void saveLogincheck(Context context,
                                   String isLogin) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.LOGIN_CHECK, isLogin);
    }

    public static String getLogincheck(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String isLogin =  mCache
                .getAsString(Constants.LOGIN_CHECK);

        return isLogin;
    }

    public static void saveSendLocationTime(Context context,String time) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_SENDTIME,time);

    }


    public static String getSendLocationTime(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String settime = mCache
                .getAsString(Constants.CACHE_SENDTIME);
        return settime;
    }




    public static void saveUserId(Context context,String userid) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.USER_ID,userid);
    }


    public static String getUserId(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String userid = mCache
                .getAsString(Constants.USER_ID);
        return userid;
    }




    public static void saveActivationLocation(Context context,String activation) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.ACTIVATIONLOCATION,activation);
    }


    public static String getActivationLocation(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String userid = mCache
                .getAsString(Constants.ACTIVATIONLOCATION);
        return userid;
    }


//    public static void saveUser(Context context,
//                                NewUser user) {
//
//        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
//        mCache.put(Constants.CACHE_USER, user);
//
//
//    }

    public static void saveUser(Context context,
                                User user) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.CACHE_USER, user);
    }


//    public static NewUser getUser(Context context) {
//        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
//        NewUser user = (NewUser) mCache
//                .getAsObject(Constants.CACHE_USER);
//
//        return user;
//    }

    public static User getUser(Context context) {
        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        User user = (User) mCache
                .getAsObject(Constants.CACHE_USER);

        return user;
    }


    public static void saveUsername(Context context,
                                    String username) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        mCache.put(Constants.LOGIN_USER, username);
    }

    public static String getUsername(Context context) {

        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
        String username = mCache
                .getAsString(Constants.LOGIN_USER);
        return username;
    }



//    public static void saveProtocolValue(Context context,
//                              String protocolValue) {
//
//        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
//        mCache.put(Constants.PROTOCOL_VALUE, protocolValue);
//
//    }
//
//    public static String getProtocolValue(Context context) {
//        ACache mCache = ACache.get(context, Constants.CACHE_INFO);
//        String protocolValue =  mCache
//                .getAsString(Constants.PROTOCOL_VALUE);
//
//        return protocolValue;
//    }
}
