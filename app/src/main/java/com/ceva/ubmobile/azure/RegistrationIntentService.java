package com.ceva.ubmobile.azure;

/**
 * Created by brian on 29/08/2016.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ceva.ubmobile.BuildConfig;
import com.ceva.ubmobile.core.UBNApplication;
import com.ceva.ubmobile.core.ui.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.microsoft.windowsazure.messaging.NotificationHub;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    private NotificationHub hub;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = UBNApplication.get().getSharedPreferences();
        String resultString = null;
        String regID = null;
        String storedToken = null;
        String username = intent.getExtras().getString("username");
        String hubname = null;
        String hubpath = null;

        if (BuildConfig.DEBUG) {
            hubname = NotificationSettings.HubNameUAT;
            hubpath = NotificationSettings.HubListenConnectionStringUAT;
        } else {
            hubname = NotificationSettings.HubName;
            hubpath = NotificationSettings.HubListenConnectionString;
        }
        // hubname = NotificationSettings.HubName;
        //hubpath = NotificationSettings.HubListenConnectionString;
        try {
            String FCM_token = FirebaseInstanceId.getInstance().getToken();
            Log.debug(TAG, "FCM Registration Token: " + FCM_token);

            // Storing the registration id that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server,
            // otherwise your server should have already received the token.
            if (((regID = sharedPreferences.getString("registrationID", null)) == null)) {

                NotificationHub hub = new NotificationHub(hubname, hubpath, this);
                Log.debug(TAG, "Attempting a new registration with NH using FCM token : " + FCM_token);
                regID = hub.register(FCM_token, username).getRegistrationId();

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.debug(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply();
            }

            // Check if the token may have been compromised and needs refreshing.
            else if ((storedToken = sharedPreferences.getString("FCMtoken", "")) != FCM_token) {

                NotificationHub hub = new NotificationHub(hubname, hubpath, this);
                Log.debug(TAG, "NH Registration refreshing with token : " + FCM_token);
                regID = hub.register(FCM_token, username).getRegistrationId();

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/en-us/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();

                resultString = "New NH Registration Successfully - RegId : " + regID;
                Log.debug(TAG, resultString);

                sharedPreferences.edit().putString("registrationID", regID).apply();
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply();
            } else {
                //NotificationHub hub = new NotificationHub(NotificationSettings.HubName,
                //      NotificationSettings.HubListenConnectionString, this);
                //regID = hub.register(FCM_token, username).getRegistrationId();
                resultString = "Previously Registered Successfully - RegId : " + regID;
                Log.debug(TAG, resultString);
            }
        } catch (Exception e) {
            Log.debug(TAG, "Failed to complete registration" + e);

        }
    }
}