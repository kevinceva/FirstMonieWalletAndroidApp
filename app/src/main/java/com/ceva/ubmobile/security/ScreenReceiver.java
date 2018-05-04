package com.ceva.ubmobile.security;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ceva.ubmobile.core.ui.Log;


/**
 * Created by brian on 30/01/2017.
 */

public class ScreenReceiver extends BroadcastReceiver {
    public boolean isScreenOff = false;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            isScreenOff = true;
            Log.debug("UBNReceiver screen off");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            isScreenOff = false;
            Log.debug("UBNReceiver screen on");
        }
    }
}
