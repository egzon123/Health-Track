package com.solaborate.healthtrack;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ec.easylibrary.AppManager;
import com.ihealth.communication.manager.iHealthDevicesManager;

public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    /** global connect */
    public static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        applicationContext = this;
        init();
    }

    private void init() {
        /*
         * Initializes the iHealth devices manager. Can discovery available iHealth devices nearby
         * and connect these devices through iHealthDevicesManager.
         */
        iHealthDevicesManager.getInstance().init(this,  Log.VERBOSE, Log.VERBOSE);
    }


    public static BaseApplication instance() {
        return mInstance;
    }

    /**
     *
     * <li>Some operations to exit login, such as clearing some user data, etc.</li>
     */
    public void logOut() {
        AppManager.instance().finishAllActivity();
    }
}
