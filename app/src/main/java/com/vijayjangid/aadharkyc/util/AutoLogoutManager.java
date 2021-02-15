package com.vijayjangid.aadharkyc.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.vijayjangid.aadharkyc.in.SharedPref;
import com.vijayjangid.aadharkyc.login.Login_activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoLogoutManager {

    public static boolean isSessionTimeout = false;
    private static Timer timer;

    public static void startUserSession() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isSessionTimeout = true;
            }
        }, 60000 * (30 * 48));

    }

    public static void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void logout(Context context) {

        SharedPref.getInstance(context).delete_id();
        SharedPref.getInstance(context).deleteEmail();
        SharedPref.getInstance(context).deleteOtp();
        SharedPref.getInstance(context).deletToken();
        SharedPref.getInstance(context).deleteUserBalance();
        SharedPref.getInstance(context).deleteRollId();
        SharedPref.getInstance(context).deleteName();
        SharedPref.getInstance(context).deleteAutoLogin();
        SharedPref.getInstance(context).deleteLoginPassword();

        isSessionTimeout = false;
        Intent intent = new Intent(context, Login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
