package com.ceva.ubmobile.network;

import android.content.Context;

import com.ceva.ubmobile.core.dialogs.ResponseDialogs;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by brian on 15/05/2017.
 */

public class ConnectivityInterceptor implements Interceptor {

    private Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtils.isConnected(mContext)) {
            ResponseDialogs.noInternet(mContext);
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

}