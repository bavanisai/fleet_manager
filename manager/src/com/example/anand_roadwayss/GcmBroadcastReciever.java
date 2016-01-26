package com.example.anand_roadwayss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReciever extends WakefulBroadcastReceiver
{
    public Activity _context;
    public GcmBroadcastReciever(Activity _context)
    {
        this._context=_context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        if (extras != null) {

        }
        String Registration_Id = "";
        // boolean isit = true;

        try {
            if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
                Registration_Id = intent.getStringExtra("registration_id");
                Log.i("uo", Registration_Id);
                // String Error = intent.getStringExtra("error");
                // String UnRegistered = intent.getStringExtra("unregistered");
                Intent in = new Intent(context, GcmServiceIntent.class);
                in.putExtra("Data", Registration_Id);
                context.startService(in);

            } else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
                String data1 = intent.getStringExtra("message");
                //  Log.i("Msg", data1);
                Intent in = new Intent(context, GcmServiceIntent.class);
                in.putExtra("Msg", data1);
                context.startService(in);
            }
        }

        catch (Exception e)
        {
            ExceptionMessage.exceptionLog(_context, this.getClass().toString() + " " + "[onReceive()]",
                    e.toString());
            e.printStackTrace();
        }
    }

}
