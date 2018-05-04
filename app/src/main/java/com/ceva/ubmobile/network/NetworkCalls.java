package com.ceva.ubmobile.network;

import com.ceva.ubmobile.core.constants.Constants;

/**
 * Created by brian on 28/09/2016.
 */
public class NetworkCalls {
    public static String generalLogin(String params) {
        String url = Constants.NET_URL + Constants.NET_PATH + params;
        return url;
    }
}
