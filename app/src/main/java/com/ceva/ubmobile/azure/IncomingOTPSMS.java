package com.ceva.ubmobile.azure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


/**
 * Created by brian on 20/07/2016.
 */
public class IncomingOTPSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    try {
                        if (senderNum.equals("NBK")) {
                            message = message.toLowerCase();

                            if (message.contains("social")) {
                                message = message.replaceAll("[^0-9]", "");
                                //Auth_Dialog otp = new Auth_Dialog();
                                //Log.d("OTP Pin:",message);
                                // otp.receivedSMS(message);
                            }

                        }

                    } catch (Exception e) {

                    }
                }

            }
        } catch (Exception e) {

        }
    }
}
