package com.example.anand_roadwayss;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class RemoteLogger extends IntentService {
    String className, error;

    public RemoteLogger() {
        super("Log intent services");

    }

    public void postData(String IMEI_NUMBER, String DEVICE_TIME,
                         String CLASS_NAME, String ERROR)
            throws UnsupportedEncodingException {

        String fullUrl = "https://docs.google.com/forms/d/1AzpF0JoSpkei_67YM-UWqi4hAlIBDDj8T0CYVqFrg2I/formResponse";

        HttpRequest mReq = new HttpRequest();
        String data = "entry_767452325="
                + URLEncoder.encode(IMEI_NUMBER, "UTF-8") + " &"
                + "entry_1124480502=" + URLEncoder.encode(DEVICE_TIME, "UTF-8")
                + " &" + "entry_1714943608="
                + URLEncoder.encode(CLASS_NAME, "UTF-8") + " &"
                + "entry_1472415747=" + URLEncoder.encode(ERROR, "UTF-8");
        String response = mReq.sendPost(fullUrl, data);
        // Log.i("myTag", response);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        className = intent.getStringExtra("className");
        error = intent.getStringExtra("error");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        // Time now = new Time();
        // now.setToNow();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        try {
            postData(telephonyManager.getDeviceId(), currentDateandTime,
                    className, error);
        } catch (UnsupportedEncodingException e) {
            ExceptionMessage.exceptionLog(this, this.getClass()
                    .toString() + " " + "[onHandleIntent]", e.toString());
            e.printStackTrace();
        }

    }
}
