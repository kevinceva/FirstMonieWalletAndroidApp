package com.zercomsystems.android.unionatmlocator.helpers;

import android.view.View;

import com.zercomsystems.android.unionatmlocator.models.Location;

/**
 * Created by oreofe on 5/10/2016.
 */
public class Constants {
    public static final String apiUrl = "http://tangerine-zercom.rhcloud.com/firstbank/";
    private static final String mURL = "http://terminoxx.unionbankng.com";
    private static final String BASE_URL = mURL + "/api/";
    public static final String ATM_DETAILS = BASE_URL + "terminal/";
    public static final String ATM_SCAN = BASE_URL + "atms";
    public static final String BRANCH_SCAN = BASE_URL + "branches";
    public static final String SMART_BRANCH_SCAN = BASE_URL + "branches/smart";

    public static final boolean DEBUG = true;
    public static final boolean IS_CLIENT_VERSION = true;


    // user home has seene Tooltip
    public static final String SEEN_HOME = "has_seen_home_tip";
    // USER HAS SEEN TUTORIAL
    public static final String SEEN_TUTORIAL = "has_seen_home";
    // USER HAS SEEN MAP TUTORIAL
    public static final String USER_LEARNED = "has_seen_learned";


    public interface OnClickListener {

        void item(Location object, View view, int position);
    }

}
