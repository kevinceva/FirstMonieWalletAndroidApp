package com.zercomsystems.android.unionatmlocator.helpers;

/**
 * Created by oreofe on 6/29/2016.
 */
public class RError extends RResponse {

    public RError(int code, String message) {
        super(code, message);

    }


    public static RError UNKNOWN_USER() {
        return new RError(1101, "Unknown User");
    }


    public static RError SERVICE_UNAVAILABLE() {
        return new RError(500, "Service Unavailable");
    }

    public static RError INTERNET_UNAVAILABLE() {
        return new RError(RResponse.INTERNET_UNAVAILABLE, "Internet Unavailable");
    }

}
