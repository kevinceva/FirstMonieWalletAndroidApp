package com.zercomsystems.android.unionatmlocator.helpers;

/**
 * Created by oreofe on 6/29/2016.
 */

public class RResponse {

    public static final int SUCCESS = 200;
    public static final int UNAUTHORIZED = 401;
    public static final int BAD_REQUEST = 400;
    public static final int USER_ALREADY_EXIST = 402;
    public static final int UNKNOWN_USER = 403;
    public static final int INVALID_PASSWORD = 403;
    public static final int RESOURCE_NOT_FOUND = 404;
    public static final int EXPIRED = 405;
    public static final int ACCOUNT_UNCONFIRMED = 406;
    public static final int INTERNET_UNAVAILABLE = 1400;
    public static final int REQUEST_TIMED_OUT = 408;
    public static final int SERVER_ERROR = 500;
    public static final int MAIL_ERROR = 502;


    public int code;

    public String message;

    public String body;

    public RResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.body = null;

    }

    public RResponse(int code, String message, String body) {
        this.code = code;
        this.message = message;
        this.body = body;

    }

}
