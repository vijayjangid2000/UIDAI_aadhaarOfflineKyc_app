package com.vijayjangid.aadharkyc.in;

import android.app.Application;
import android.content.pm.PackageManager;

import com.onesignal.OneSignal;
import com.vijayjangid.aadharkyc.R;
import com.vijayjangid.aadharkyc.util.APIs;
import com.vijayjangid.aadharkyc.util.RootTools;

public class MyApplication extends Application {
    public static boolean isRooted;
    public static String currentAppVersion;

    static {
        System.loadLibrary("native-lib");
    }

    private native String HJJ29D();

    private native String TCCGHU();

    private native String EE2JT();

    private native String FFEP();

    private native String FFEK();

    @Override
    public void onCreate() {
        super.onCreate();

        isRooted = RootTools.isDeviceRooted();
        new APIs(HJJ29D(), TCCGHU(), EE2JT(), FFEP(), FFEK()
                , getApplicationContext()
                .getResources()
                .getString(R.string.open));
        currentAppVersion = getCurrentAppVersion();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }

    private String getCurrentAppVersion() {
        String currentVersion = "0";
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersion;
    }
}
