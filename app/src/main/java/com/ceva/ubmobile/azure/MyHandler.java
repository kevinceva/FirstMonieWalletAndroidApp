package com.ceva.ubmobile.azure;

/**
 * Created by brian on 29/08/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.ceva.ubmobile.R;
import com.ceva.ubmobile.core.ui.Log;
import com.ceva.ubmobile.core.ui.locator.RequestAgent;
import com.ceva.ubmobile.security.UBNSession;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import java.net.URLDecoder;


public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    Context ctx;
    private NotificationManager mNotificationManager;


    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        Log.debug("bundle", bundle.toString());
        try {

            if (bundle.containsKey("type")) {

                String nhMessage = URLDecoder.decode(bundle.getString("message"), "utf-8");
                String nhTitle = URLDecoder.decode(bundle.getString("title"), "utf-8");
                String type = bundle.getString("type");
                Log.debug("ubnbot", nhMessage);
                Bundle b = new Bundle();
                Class activity = null;

                if (type.equals("AGENT")) {
                    Log.debug("ubnreceiver latitude " + bundle.getString("lat"));
                    b.putString(RequestAgent.KEY_LAT, bundle.getString("lat"));
                    b.putString(RequestAgent.KEY_LON, bundle.getString("lon"));
                    b.putString(RequestAgent.KEY_NAME, URLDecoder.decode(bundle.getString("customer"), "utf-8"));
                    b.putString(RequestAgent.KEY_PHONE, URLDecoder.decode(bundle.getString("phone"), "UTF-8"));
                    b.putString("message", URLDecoder.decode(bundle.getString("message"), "UTF-8"));

                    activity = RequestAgent.class;
                    Intent intent = new Intent(ctx, activity);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                    // RequestAgent agent = new RequestAgent();
                    //agent.updateStatus(bundle.getString("lat"),bundle.getString("lon"),bundle.getString("customer"),bundle.getString("phone"));
                    UBNSession session = new UBNSession(ctx);

                    if (session.getString(RequestAgent.KEY_LAT) != null) {
                        if (session.getString(RequestAgent.KEY_COMMON_ID).equals(bundle.getString("common_id"))) {
                            sendNotification(nhMessage, nhTitle, b, activity);
                        }
                    }

                }
            } /*else if (bundle.containsKey("c360_message")) {
                Map<String, String> map = new HashMap<>();
                for (String key : bundle.keySet()) {
                    if (bundle.get(key) instanceof String) {
                        map.put(key, bundle.getString(key));
                    }
                }
                ScioContext.handlePushMessage(context, map);
                // ScioContext.handlePushMessage(ctx, bundle);
                // ScioContext.handlePushMessage(this, bundle);
            }*/ else {
                sendNotificationNoActivity(bundle.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendNotification(String msg, String title, Bundle bundle, Class activity) {

        Intent intent = new Intent(ctx, activity);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationNoActivity(String msg) {

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(ctx.getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setAutoCancel(true)
                        .setContentText(msg);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}