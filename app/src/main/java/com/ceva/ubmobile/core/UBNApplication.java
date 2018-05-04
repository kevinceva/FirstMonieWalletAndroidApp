package com.ceva.ubmobile.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.Log;
import com.github.ajalt.reprint.core.Reprint;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by brian on 05/10/2016.
 */
public class UBNApplication extends MultiDexApplication {
    public static final String TAG = UBNApplication.class.getSimpleName();
    protected static UBNApplication instance;
    private static Context mContext;
    private static UBNApplication mInstance;
    private static Bus bus;
    public ArrayList<Integer> notificationIDs = new ArrayList<>();
    SharedPreferences securePreferences;
    private RequestQueue mRequestQueue;
    private Picasso picasso;

    public UBNApplication() {
        super();
        instance = this;
    }

    public static UBNApplication get() {
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }

    public static UBNApplication getInstance() {
        return mInstance;
    }

    public static Bus getBus() {
        return bus;
    }

    public static float convertDpToPixel(float dp) {
        Resources resources = mInstance.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static boolean isBioPresent() {
        return Reprint.isHardwarePresent();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/GothamRounded-Book.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Iconify.with(new FontAwesomeModule());
        mContext = getApplicationContext();
        mInstance = this;

        bus = new Bus(ThreadEnforcer.MAIN);
        picasso = Picasso.with(this);

        //PrefUtils.putBoolean(PrefUtils.IS_REFRESHING, false); // init
        Reprint.initialize(this, new Reprint.Logger() {
            @Override
            public void log(String message) {
                Log.debug("Reprint", message);
            }

            @Override
            public void logException(Throwable throwable, String message) {
                Log.debug("Reprint", message + throwable.toString());
            }
        });

    }

    public SharedPreferences getSharedPreferences() {
        if (securePreferences == null) {
            securePreferences = new SecurePreferences(this);
            SecurePreferences.setLoggingEnabled(true);
        }
        return securePreferences;
    }

    public Picasso getPicasso() {
        return picasso;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req).setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
