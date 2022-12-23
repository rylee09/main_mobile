package com.example.st.arcgiscss.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class AppManager {public static Stack<Activity> getActivityStack() {
    return activityStack;
}

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**

     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**

     */
    public Activity getTopActivity() {
        return activityStack.lastElement();
    }

    /**
     */
    public void finishTopActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**

     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        Iterator iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = (Activity) iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /**

     */
    @SuppressWarnings("WeakerAccess")
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }


    public void appExit() {
        try {
            finishAllActivity();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

        } catch (Exception e) {
        }
    }


    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }


    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }


    public boolean isTopActivity(Activity activity){
        String packageName = "com.d3.d3xmpp";
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if(tasksInfo.size() > 0){
            System.out.println("--------------------------"+tasksInfo.get(0).topActivity.getPackageName());

            if(packageName.equals(tasksInfo.get(0).topActivity.getPackageName())){
                return true;
            }
        }
        return false;
    }
}

