package com.example.Reminder;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.anand_roadwayss.ExceptionMessage;

public class ReminderManager {

    private Context mContext;
    private AlarmManager mAlarmManager;

    public ReminderManager(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(Long taskId, Calendar when) {
        try {

            Intent i = new Intent(mContext, OnAlarmReceiver.class);
            i.putExtra(RemindersDbAdapter.KEY_ROWID, (long) taskId);

            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i,
                    PendingIntent.FLAG_ONE_SHOT);

            mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(),
                    pi);
        } catch (Exception e)
        {
            ExceptionMessage.exceptionLog(mContext, this.getClass()
                    .toString() + " " + "[setReminder()]", e.toString());
            e.printStackTrace();
        } catch (Error e)
        {
            ExceptionMessage.exceptionLog(mContext, this.getClass()
                    .toString() + " " + "[setReminder()]", e.toString());
            e.printStackTrace();
        }
    }
}
