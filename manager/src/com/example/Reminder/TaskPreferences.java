package com.example.Reminder;

import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TaskPreferences extends PreferenceActivity {
    // @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.task_preferences);

            // Set the time default to a numeric number only
            EditTextPreference timeDefault = (EditTextPreference) findPreference(getString(R.string.pref_default_time_from_now_key));
            timeDefault.getEditText().setKeyListener(
                    DigitsKeyListener.getInstance());
        } catch (Exception e)
        {
            ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass()
                    .toString() + " " + "[onCreate()]", e.toString());
            e.printStackTrace();
        } catch (Error e) {
            ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass()
                    .toString() + " " + "[onCreate()]", e.toString());
            e.printStackTrace();
        }
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

            case R.id.action_help:
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse("http://www.stackoverflow.com/"));
                startActivity(viewIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
