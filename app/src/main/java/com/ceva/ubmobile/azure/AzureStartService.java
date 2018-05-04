package com.ceva.ubmobile.azure;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by brian on 30/08/2016.
 */
public class AzureStartService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        // Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        //Log.d(TAG, "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleStart();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart();
        return START_NOT_STICKY;
    }

    public void handleStart() {

    }
}
