package com.coretronic.bdt.Person.Register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.Person.Module.SmsInfo;
import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by Morris on 2014/2/18.
 */
public class SMSReceiver extends BroadcastReceiver {
    private final static int requestCode = 0;
    private String TAG = "SMSReceiver";
    private Gson gson = new Gson();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String msgBody = "";
        String sendAddr = "";
        Date time = new Date(0);

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] smsMessages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                msgBody += smsMessages[i].getDisplayMessageBody();
            }
            sendAddr = smsMessages[0].getDisplayOriginatingAddress();
            time = new Date(smsMessages[0].getTimestampMillis());
        }

        SmsInfo smsInfo = new SmsInfo();
        smsInfo.setRequestCode(requestCode);
        smsInfo.setSendAddr(sendAddr);
        smsInfo.setTime(time.toString());
        smsInfo.setMsgBody(msgBody);
        Log.i(TAG, "msgBody = " + msgBody);

        Intent newIntent = new Intent(AppConfig.SMS_INTENT_FILTER);
        newIntent.putExtra(AppConfig.SMS_PROF,gson.toJson(smsInfo,SmsInfo.class).toString());
        context.sendBroadcast(newIntent);
    }
}
