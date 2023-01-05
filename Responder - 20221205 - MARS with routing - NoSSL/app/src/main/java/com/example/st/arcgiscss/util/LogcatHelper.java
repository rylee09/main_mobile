package com.example.st.arcgiscss.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.st.arcgiscss.activites.MainActivity;
import com.example.st.arcgiscss.constant.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.READ_PHONE_STATE;

public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;

    //ZN - 20220720 restore activation
    private static String PATH_ACTIVATON_LOG;
    private static String NAME_ACTIVATION_RECORD_LOGFILE = "activationLogFile.txt";
    private LogDumper activationLogDumper = null;
    private static FileOutputStream activationLogFile_out = null;
    private static boolean isStarted;

    /**
     * init data
     */
    public void init(Context context) {

//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "No read/write permission for LogCatHelper", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(context, "read/write permission granted for LogCatHelper", Toast.LENGTH_LONG).show();
//        }

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// sd first
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "logcat";

//            PATH_ACTIVATON_LOG = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + File.separator + "activationLog";
        } else {
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + "logcat";

//            PATH_ACTIVATON_LOG = context.getFilesDir().getAbsolutePath()
//                    + File.separator + "activationLog";
        }

        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }

//        File file2 = new File(PATH_ACTIVATON_LOG);
//        if (!file2.exists()) {
//            file2.mkdirs();
//        }

        MyApplication.getInstance().setLogDirectory(PATH_LOGCAT);
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);

        if (!isStarted) {
            mLogDumper.start();
            isStarted = true;
        }


//        if (activationLogDumper == null)
//            activationLogDumper = new LogDumper(String.valueOf(mPId), PATH_ACTIVATON_LOG);
//        activationLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
            isStarted = false;
        }

//        if (activationLogDumper != null) {
//            activationLogDumper.stopLogs();
//            activationLogDumper = null;
//        }
    }

    //ZN - 20220720 restore activation
    public static void clearActivationLog() {
        try {
            activationLogFile_out = new FileOutputStream(new File(PATH_LOGCAT, NAME_ACTIVATION_RECORD_LOGFILE), false);
            activationLogFile_out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ZN - 20220720 restore activation
    public static void addActivationLog(String value, boolean append) {
        try{
            activationLogFile_out = new FileOutputStream(new File(PATH_LOGCAT, NAME_ACTIVATION_RECORD_LOGFILE), append);
            activationLogFile_out.write((value).getBytes());
            activationLogFile_out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //ZN - 20220720 restore activation
    public static String readActivationLog() {
        String ret_string = "-1";
        try {
            File f = new File (PATH_LOGCAT,NAME_ACTIVATION_RECORD_LOGFILE);
            if (!f.exists())
                ret_string = "-1";

            BufferedReader br = new BufferedReader(new FileReader(f));
            ret_string = br.readLine();
            if (ret_string == null || ret_string == "")
                ret_string = "-1";

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("ACTIVATION_LOG", "readActivationLog: " + ret_string);
        return ret_string;
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            try {
                out = new FileOutputStream(new File(dir, "logcat"
                        + getFileName() + ".txt"), true);

//                out = new FileOutputStream(new File(dir, "logcat"
//                        + getDateEN() + ".txt"));

                MyApplication.getInstance().setLogFilename("logcat" + getFileName() + ".txt");
//                MyApplication.getInstance().setLogFilename("logcat" + getDateEN() + ".txt");

                //ZN - 20220720 restore activation
//                activationLogFile_out = new FileOutputStream(new File(dir, NAME_ACTIVATION_RECORD_LOGFILE));
//                activationLogFile_out.write(("TEST TEST" + "\n").getBytes());
//                activationLogFile_out.write(("TEST TEST" + "\n").getBytes());
//                activationLogFile_out.write(("TEST TEST" + "\n").getBytes());
//                activationLogFile_out.close();

            } catch (FileNotFoundException e) {
//                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             *
             * Level：*:v , *:d , *:w , *:e , *:f , *:s
             *
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\""; // print e level and ilevel info
            // cmds = "logcat  | grep \"(" + mPID + ")\"";// print all
            // cmds = "logcat -s \"ON_CLICK\"";// print filter info

            //cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
            cmds = "logcat ON_CLICK:* INCIDENT:* ROUTE:* ERS_EVENT:* EVENT:* NOTIFICATION:* *:s";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
//                        out.write((getDateEN() + "  " + line + "\n")
//                                .getBytes());
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }

    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String temp = format1.format(new Date(System.currentTimeMillis()));
        String date1 = temp.replace(" ", "-");
        Log.i("TESTDATE", "test dateeeeee: "+date1);
        return date1;
    }
}
