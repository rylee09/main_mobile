//package com.example.st.arcgiscss.receiver;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//import com.example.st.arcgiscss.mqtt.MQTTService;
//
//
//public class MQTTReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action=intent.getAction();
//        if("com.example.st.arcgiscss.MQTTService".equals(action)){
//            Intent i=new Intent(context, MQTTService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(i);
//            } else {
//                context.startService(i);
//            }
//
//        }
//    }
//}
//
