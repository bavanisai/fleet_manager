package com.example.Reminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.database.Cursor;
import android.util.Log;

import com.example.anand_roadwayss.ExceptionMessage;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = ComponentInfo.class.getCanonicalName();
    public Activity _context;
    public  OnBootReceiver()
    {

    }
    public OnBootReceiver(Activity _context)
    {
        this._context=_context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            ReminderManager reminderMgr = new ReminderManager(context);

            RemindersDbAdapter dbHelper = new RemindersDbAdapter(context);
            dbHelper.open();

            Cursor cursor = dbHelper.fetchAllReminders();

            if (cursor != null) {
                cursor.moveToFirst();

                int rowIdColumnIndex = cursor
                        .getColumnIndex(RemindersDbAdapter.KEY_ROWID);
                int dateTimeColumnIndex = cursor
                        .getColumnIndex(RemindersDbAdapter.KEY_DATE_TIME);

                while (cursor.isAfterLast() == false) {

                    Log.d(TAG, "Adding alarm from boot.");
                    Log.d(TAG, "Row Id Column Index - " + rowIdColumnIndex);
                    Log.d(TAG, "Date Time Column Index - "
                            + dateTimeColumnIndex);

                    Long rowId = cursor.getLong(rowIdColumnIndex);
                    String dateTime = cursor.getString(dateTimeColumnIndex);

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat(
                            ReminderEditActivity.DATE_TIME_FORMAT,
                            Locale.getDefault());

                    try {
                        java.util.Date date = format.parse(dateTime);
                        cal.setTime(date);

                        reminderMgr.setReminder(rowId, cal);
                    } catch (java.text.ParseException e)
                    {
                        ExceptionMessage.exceptionLog(_context, this.getClass()
                                .toString() + " " + "[onReceive()-java.text.ParseException]", e.toString());
                        Log.e("OnBootReceiver", e.getMessage(), e);
                    }

                    cursor.moveToNext();
                }
                cursor.close();
            }

            dbHelper.close();
        } catch (Exception e)
        {
            ExceptionMessage.exceptionLog(_context, this.getClass()
                    .toString() + " " + "[onReceive()-Exception]", e.toString());
            e.printStackTrace();
        } catch (Error e)
        {
            ExceptionMessage.exceptionLog(_context, this.getClass()
                    .toString() + " " + "[onReceive()-Error]", e.toString());
            e.printStackTrace();
        }
    }
}
