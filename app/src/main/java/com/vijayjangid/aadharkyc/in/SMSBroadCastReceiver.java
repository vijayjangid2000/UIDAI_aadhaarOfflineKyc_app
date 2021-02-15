package com.vijayjangid.aadharkyc.in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSBroadCastReceiver extends BroadcastReceiver {
    private static SmsListener mListener;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    public static String extractDigits(final String in) {
        final Pattern p = Pattern.compile("(\\d{6})");
        final Matcher m = p.matcher(in);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsm = null;
        String sms_str = "";

        if (bundle != null) {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            smsm = new SmsMessage[pdus.length];
            for (int i = 0; i < smsm.length; i++) {

                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                String mainMessage = currentMessage.getDisplayMessageBody();


                if ((mainMessage.contains("otp") || mainMessage.contains("OTP")) && mainMessage.contains(":")) {
                    String message = mainMessage.split(":")[1].trim();

                    // Log.e("sms message",message);
                    Intent myIntent = new Intent("otp");
                    myIntent.putExtra("message", message.substring(0, 6).trim());
                    //Log.e("sms message1",message.substring(0, 6).trim());
                    if (mListener != null)
                        mListener.messageReceived(message.substring(0, 6).trim());
                    //LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                } else if (mainMessage.contains("OTP")) {

                    String message = extractDigits(mainMessage);

                    Intent myIntent = new Intent("otp_dmt");
                    myIntent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);

                }


                // Show Alert
            }


        }
    }

    public interface SmsListener {
        public void messageReceived(String messageText);
    }
}
