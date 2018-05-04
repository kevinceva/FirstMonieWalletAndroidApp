package com.ceva.ubmobile.azure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.microsoft.windowsazure.notifications.NotificationsManager;

/**
 * Created by brian on 30/08/2016.
 */
public class AzureStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationsManager.handleNotifications(context, NotificationSettings.SenderId, MyHandler.class);
        Intent service = new Intent(context, RegistrationIntentService.class);
        context.startService(service);
    }
}