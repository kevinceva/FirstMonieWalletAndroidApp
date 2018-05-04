package com.ceva.ubmobile.core.ui;

import com.ceva.ubmobile.BuildConfig;
import com.crashlytics.android.Crashlytics;

/**
 * Created by brian on 04/10/2016.
 */
public class Log {
    public static boolean DEBUG_MODE = false;
    private String message;

    public Log() {

    }

    public static void debug(String message) {
        if (BuildConfig.DEBUG) {
            //if(!Constants.NET_URL.contains(Constants.HOSTNAME)){
            System.out.println(message);

        }

    }

    public static void Error(Exception e) {
        if (BuildConfig.DEBUG) {
            // if(!Constants.NET_URL.contains(Constants.HOSTNAME)){
            e.printStackTrace();

            //}

        }
        //Crashlytics.log(e.toString());

    }

    public static void Error(String e) {
        if (BuildConfig.DEBUG) {
            //if(!Constants.NET_URL.contains(Constants.HOSTNAME)){
            debug(e);
            // }

        }
        // Crashlytics.log(e);

    }

    public static void debug(String title, String message) {
        if (BuildConfig.DEBUG) {

            //if(!Constants.NET_URL.contains(Constants.HOSTNAME)){
            System.out.println(title + ":" + message);
            // }
        }
    }
}
