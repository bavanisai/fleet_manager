package com.example.Reminder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

import com.example.anand_roadwayss.ExceptionMessage;

public class OnAlarmReceiver extends BroadcastReceiver
{
//    public Activity _context;
//    public OnAlarmReceiver(Activity _context)
//    {
//        this._context=_context;
//    }

    private static final String TAG = ComponentInfo.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(TAG, "Received wake up from alarm manager.");

            long rowid = intent.getExtras().getLong(
                    RemindersDbAdapter.KEY_ROWID);

            WakeReminderIntentService.acquireStaticLock(context);

            Intent i = new Intent(context, ReminderService.class);
            i.putExtra(RemindersDbAdapter.KEY_ROWID, rowid);
            context.startService(i);
        } catch (Exception e)
        {
//            ExceptionMessage.exceptionLog(_context, this.getClass()
//                    .toString() + " " + "[onReceive()]", e.toString());
            e.printStackTrace();
        } catch (Error e)
        {
//            ExceptionMessage.exceptionLog(_context, this.getClass()
//                    .toString() + " " + "[onReceive()]", e.toString());
            e.printStackTrace();
        }

    }
}
