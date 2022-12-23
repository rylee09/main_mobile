package com.example.st.arcgiscss.util;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.example.st.arcgiscss.constant.MyApplication;

public class MyAndroidUtil {
    private static Notification myNoti = new Notification();
    /**
     * @param context
     * @param title
     * @param message
     * @param icon
     * @param okBtn
     */
    public static void showDialog(Context context , String title, String message, int icon, DialogInterface.OnClickListener okBtn){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(icon)
                .setMessage(message)
                .setPositiveButton("submit",okBtn)
                .setNegativeButton("goback", null).show();
    }


    public static void editXml(String name,Object object) {
        SharedPreferences.Editor editor = MyApplication.sharedPreferences.edit();
        if (MyApplication.sharedPreferences.getString(name, null) != null) {
            editor.remove(name);
        }
        editor.putString(name, JsonUtil.objectToJson(object));
        editor.commit();
    }



    public static void editXmlByString(String name,String result) {
        SharedPreferences.Editor editor = MyApplication.sharedPreferences.edit();
        if (MyApplication.sharedPreferences.getString(name, null) != null) {
            editor.remove(name);
        }
        editor.putString(name, result);
        editor.commit();
    }


    public static void editXml(String name,boolean is) {
        SharedPreferences.Editor editor = MyApplication.sharedPreferences.edit();
        editor.putBoolean(name, is);
        editor.commit();
    }


    public static void removeXml(String name){
        SharedPreferences.Editor editor = MyApplication.sharedPreferences.edit();
        editor.remove(name);
        editor.commit();
    }

}

