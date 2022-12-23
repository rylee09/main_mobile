package com.example.st.arcgiscss.util;

import android.content.Context;

import java.io.IOException;

public class HelpRequestUtil{
        private volatile static HelpRequestUtil reSyncUtil;

        public  boolean requestStatue = true;
        //    public onRequestServerListener listener;
        public Context context;

        private HelpRequestUtil() {
        }

        public static HelpRequestUtil getInstance(Context context) {
            if (reSyncUtil == null) {
                synchronized (HelpRequestUtil.class) {
                    if (reSyncUtil == null) {
                        reSyncUtil = new HelpRequestUtil();

                    }
                }
            }
            reSyncUtil.context = context;
            return reSyncUtil;
        }




        private boolean pingIpAddress(String ipAddress) {
            try {
                Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 100 " + ipAddress);
                int status = process.waitFor();
                if (status == 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }




}
