package com.ceva.ubmobile.core.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ceva.ubmobile.azure.MyHandler;
import com.ceva.ubmobile.azure.NotificationSettings;
import com.ceva.ubmobile.azure.RegistrationIntentService;
import com.ceva.ubmobile.security.UBNSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.scottyab.rootbeer.RootBeer;

public class SplashActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //private ProgressBar progressBar;
    UBNSession session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UBNSession(this);
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();
        /*if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
            System.exit(0);
        }else{
            Intent intent = new Intent(this, LandingPage.class);
            startActivity(intent);
        }*/


        /*if (session.getBoolean(UBNSession.KEY_APP_CLOSE)) {
           // if (session.getBoolean(UBNSession.KEY_LOGIN_STATUS)) {
                try {
                    session.setBoolean(UBNSession.KEY_APP_CLOSE, false);
                    session.setBoolean(UBNSession.KEY_LOGIN_STATUS, false);
                    finish();
                    System.exit(0);
                } catch (Exception e) {
                    Log.Error(e);
                }
           // }
        }else{
            Intent intent = new Intent(this, LandingPage.class);
            startActivity(intent);
        }*/
        Intent intent = new Intent(this, LandingPage.class);
        startActivity(intent);
        //finish();

    }

    private boolean checkPlayServices() {
        RootBeer rootBeer = new RootBeer(this);
        /*if (rootBeer.isRootedWithoutBusyBoxCheck()) {
            //we found indication of root
            //Toast.makeText(this, "This device is currently unsupported", Toast.LENGTH_LONG).show();
            //finish();
            return false;
        } else {*/
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                //  Log.i(TAG, "This device is not supported by Google Play Services.");
                //ToastNotify("This device is not supported by Google Play Services.");
                Toast.makeText(this, "This device is currently unsupported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
        //}

    }

    public void registerWithNotificationHubs() {
        Log.debug("azure", " Registering with Notification Hubs");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            //if (!Constants.NET_URL.contains(Constants.HOSTNAME)) {

            if (session.getUserName() != null) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra("username", session.getUserName().toUpperCase());
                //intent.putExtra("username",session.getUserName().toUpperCase());
                Log.debug("AZURE USER:", session.getUserName().toUpperCase());
                startService(intent);
            }
            // }

        }
    }
}
