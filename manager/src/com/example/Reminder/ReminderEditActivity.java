package com.example.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.RemoteLogger;
import com.example.anand_roadwayss.Welcome;

public class ReminderEditActivity extends ActionBarActivity {

    //
    // Dialog Constants
    //
    // private static final int DATE_PICKER_DIALOG = 0;
    // private static final int TIME_PICKER_DIALOG = 1;

    //
    // Date Format
    //
    Toolbar toolbar;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "kk:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

    // private EditText mTitleText;
    private EditText mBodyText;
    //	private Button mDateButton;
//	private Button mTimeButton;
    private TextView mDateButton, mTimeButton;
    private Button mConfirmButton;
    private Long mRowId;
    private RemindersDbAdapter mDbHelper;
    private Calendar mCalendar;
    private AutoCompleteTextView mTitleText;
    DBAdapter db;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            mDbHelper = new RemindersDbAdapter(this);

            setContentView(R.layout.reminder_edit);
            toolbar = (Toolbar) findViewById(R.id.tool);
            img=(ImageView)findViewById(R.id.arrow_img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReminderEditActivity.this, Welcome.class);
                    startActivity(intent);
                }
            });
//            final android.app.ActionBar actionBar = getActionBar();
//            actionBar.setTitle("REMINDER");
//            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));


            // String[] countries = { "apple", "orange", "banana" };

            db = new DBAdapter(getApplicationContext());
            db.open();
            ArrayList<String> driverList = new ArrayList<String>();
            Cursor driverCursor = db.getAllEmp(DBAdapter.getEmployeeDetails(),
                    "Driver");
            if (driverCursor.moveToFirst()) {
                do {
                    try {
                        driverList.add(driverCursor.getString(driverCursor
                                .getColumnIndex("Name")));
                    } catch (Exception h)
                    {
                        Intent intent = new Intent(ReminderEditActivity.this,
                                RemoteLogger.class);
                        intent.putExtra("className", this.getClass().toString());
                        intent.putExtra("error", h.toString());
                        startService(intent);
                    }
                } while (driverCursor.moveToNext());
            }

            Cursor cleanerCursor = db.getAllEmp(DBAdapter.getEmployeeDetails(),
                    "Cleaner");
            if (cleanerCursor.moveToFirst()) {
                do {
                    try {
                        driverList.add(cleanerCursor.getString(cleanerCursor
                                .getColumnIndex("Name")));
                    } catch (Exception h) {
                        Intent intent = new Intent(ReminderEditActivity.this,
                                RemoteLogger.class);
                        intent.putExtra("className", this.getClass().toString());
                        intent.putExtra("error", h.toString());
                        startService(intent);
                    }
                } while (cleanerCursor.moveToNext());
            }

            Cursor vehicleCursor = db.getAllData(DBAdapter.getVehicleDetails());
            if (vehicleCursor.moveToFirst()) {
                do {
                    try {
                        driverList.add(vehicleCursor.getString(vehicleCursor
                                .getColumnIndex("VehNo")));
                    } catch (Exception h) {
                        Intent intent = new Intent(ReminderEditActivity.this,
                                RemoteLogger.class);
                        intent.putExtra("className", this.getClass().toString());
                        intent.putExtra("error", h.toString());
                        startService(intent);
                    }
                } while (vehicleCursor.moveToNext());
            }

            // @SuppressWarnings({ "unchecked", "rawtypes" })
            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, driverList);

            // actv = (AutoCompleteTextView) findViewById(R.id.atv);

            mCalendar = Calendar.getInstance();
            mTitleText = (AutoCompleteTextView) findViewById(R.id.atv);
            mTitleText.setAdapter(adapter);
            mBodyText = (EditText) findViewById(R.id.body);
            mDateButton = (TextView) findViewById(R.id.reminder_date);
            mTimeButton = (TextView) findViewById(R.id.reminder_time);

            mConfirmButton = (Button) findViewById(R.id.confirm);

            mRowId = savedInstanceState != null ? savedInstanceState
                    .getLong(RemindersDbAdapter.KEY_ROWID) : null;

            registerButtonListenersAndSetDefaultText();

             mDateButton.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
             showDatePicker();

             }
             });

             mTimeButton.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
             showTimePicker();

             }
             });

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onCreate]", e.toString());
        } catch (Error e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onCreate]", e.toString());
        }
    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras
                    .getLong(RemindersDbAdapter.KEY_ROWID) : null;

        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            mDbHelper.close();
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onPause]", e.toString());
        } catch (Error e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onPause]", e.toString());
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            mDbHelper.open();
            setRowIdFromIntent();
            populateFields();
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onResume]", e.toString());
        } catch (Error e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onResume]", e.toString());
        }
    }

    // @SuppressWarnings("deprecation")
    // @Override
    // protected Dialog onCreateDialog(int id) {
    // switch (id) {
    // case DATE_PICKER_DIALOG:
    // return showDatePicker();
    // case TIME_PICKER_DIALOG:
    // return showTimePicker();
    // }
    // return super.onCreateDialog(id);
    // }

    private void showDatePicker() {

        DatePickerDialog datePicker = new DatePickerDialog(
                ReminderEditActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateButtonText();
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();

        // DatePickerFragment date = new DatePickerFragment();
        // /**
        // * Set Up Current Date Into dialog
        // */
        // Calendar calender = Calendar.getInstance();
        // Bundle args = new Bundle();
        // args.putInt("year", calender.get(Calendar.YEAR));
        // args.putInt("month", calender.get(Calendar.MONTH));
        // args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        // date.setArguments(args);
        // /**
        // * Set Call back to capture selected date
        // */
        // date.setCallBack(ondate);
        // date.show(getSupportFragmentManager(), "Date Picker");

        // return datePicker;
    }

    // OnDateSetListener ondate = new OnDateSetListener() {
    //
    // public void onDateSet(DatePicker view, int year, int monthOfYear,
    // int dayOfMonth) {
    //
    // mDateButton.setText(String.valueOf(year) + "-"
    // + String.valueOf(monthOfYear + 1) + "-"
    // + String.valueOf(dayOfMonth));
    //
    // }
    // };

    private void showTimePicker() {

        TimePickerDialog timePicker = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar.set(Calendar.MINUTE, minute);
                        updateTimeButtonText();
                    }
                }, mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE), true);
        timePicker.show();

        // return timePicker;

        // TimePickerFragment time = new TimePickerFragment();
        // /**
        // * Set Up Current Date Into dialog
        // */
        // Calendar calender = Calendar.getInstance();
        // Bundle args = new Bundle();
        // args.putInt("hour", calender.get(Calendar.HOUR_OF_DAY));
        // args.putInt("minute", calender.get(Calendar.MINUTE));
        // time.setArguments(args);
        // /**
        // * Set Call back to capture selected date
        // */
        // time.setCallBack(ontime);
        // time.show(getSupportFragmentManager(), "Time Picker");
    }

    // OnTimeSetListener ontime = new OnTimeSetListener() {
    //
    // public void onTimeSet(TimePicker view, int hour, int minute) {
    //
    // mTimeButton.setText(String.valueOf(hour) + ":"
    // + String.valueOf(minute) + "-");
    //
    // }
    // };

    private void registerButtonListenersAndSetDefaultText() {

        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // showDialog(DATE_PICKER_DIALOG);
                showDatePicker();
            }

        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {

            // @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                // showDialog(TIME_PICKER_DIALOG);
                showTimePicker();
            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String data = saveState();
                if (data.contains("Yes")) {
                    setResult(RESULT_OK);
                    Toast.makeText(ReminderEditActivity.this,
                            getString(R.string.task_saved_message),
                            Toast.LENGTH_SHORT).show();
                    Intent intentReminderList = new Intent(getApplicationContext(),
                            ReminderListActivity.class);
                    startActivity(intentReminderList);
                    finish();
                }
            }
        });

        updateDateButtonText();
        updateTimeButtonText();
    }

    private void populateFields() {

        // Only populate the text boxes and change the calendar date
        // if the row is not null from the database.
        if (mRowId != null) {
            Cursor reminder = mDbHelper.fetchReminder(mRowId);
            startManagingCursor(reminder);
            mTitleText.setText(reminder.getString(reminder
                    .getColumnIndexOrThrow(RemindersDbAdapter.KEY_TITLE)));
            mBodyText.setText(reminder.getString(reminder
                    .getColumnIndexOrThrow(RemindersDbAdapter.KEY_BODY)));

            // Get the date from the database and format it for our use.
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                    DATE_TIME_FORMAT, Locale.getDefault());
            Date date = null;
            try {
                String dateString = reminder
                        .getString(reminder
                                .getColumnIndexOrThrow(RemindersDbAdapter.KEY_DATE_TIME));
                date = dateTimeFormat.parse(dateString);
                mCalendar.setTime(date);
            } catch (ParseException e) {
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[populateFields]", e.toString());
            }
        } else {
            // This is a new task - add defaults from preferences if set.
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = prefs.getString(defaultTitleKey, null);
            String defaultTime = prefs.getString(defaultTimeKey, null);

            if (defaultTitle != null)
                mTitleText.setText(defaultTitle);

            if (defaultTime != null)
                mCalendar.add(Calendar.MINUTE, Integer.parseInt(defaultTime));

        }

        updateDateButtonText();
        updateTimeButtonText();

    }

    private void updateTimeButtonText() {
        // Set the time button text based upon the value from the database
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT,
                Locale.getDefault());
        String timeForButton = timeFormat.format(mCalendar.getTime());
        mTimeButton.setText(timeForButton);
    }

    private void updateDateButtonText() {
        // Set the date button text based upon the value from the database
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT,
                Locale.getDefault());
        String dateForButton = dateFormat.format(mCalendar.getTime());
        mDateButton.setText(dateForButton);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putLong(RemindersDbAdapter.KEY_ROWID, mRowId);
        }
        catch(Exception e)
        {
            ExceptionMessage.exceptionLog(this, this.getClass()
                    .toString() + " " + "[onSaveInstanceState()]", e.toString());
        }
    }

    private String saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        String ret;
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                DATE_TIME_FORMAT, Locale.getDefault());
        String reminderDateTime = dateTimeFormat.format(mCalendar.getTime());

        if (title.equals("") || body.equals("")) {
            Toast.makeText(this,
                    "Fields Cannot be empty", Toast.LENGTH_SHORT).show();
            ret = "No";
        } else {
            if (mRowId == null) {

                long id = mDbHelper.createReminder(title, body, reminderDateTime);
                if (id > 0) {
                    mRowId = id;
                }
            } else {
                mDbHelper.updateReminder(mRowId, title, body, reminderDateTime);
            }

            new ReminderManager(this).setReminder(mRowId, mCalendar);
            ret = "Yes";
        }
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_profile:

                Intent intent = new Intent(this, ProfileEdit.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_help:
                String adress = new IpAddress().getIpAddress();
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse(adress));
                startActivity(viewIntent);
                finish();
                return true;
            case R.id.action_logout:
                finish();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(this, ReminderListActivity.class);
        startActivity(i);
    }
}
