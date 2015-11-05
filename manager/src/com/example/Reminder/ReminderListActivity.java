package com.example.Reminder;

import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.Welcome;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.support.v4.widget.SimpleCursorAdapter;

public class ReminderListActivity extends ListActivity {
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private RemindersDbAdapter mDbHelper;
    Button addReminder;
    Toolbar toolbar;
    ImageView img;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reminder_list);

            toolbar = (Toolbar) findViewById(R.id.tool);
            toolbar.setTitle("");
            img=(ImageView)findViewById(R.id.arrow_img);
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReminderListActivity.this,Welcome.class);
                    startActivity(intent);
                }
            });

            addReminder = (Button) findViewById(R.id.btnAddReminder);
            addReminder.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),
                            ReminderEditActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
            mDbHelper = new RemindersDbAdapter(this);
            mDbHelper.open();
            fillData();
            registerForContextMenu(getListView());

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onCreate]", e.toString());
        } catch (Error e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onCreate]", e.toString());
        }
    }

    // @SuppressWarnings("deprecation")
    private void fillData() {
        Cursor remindersCursor = mDbHelper.fetchAllReminders();
        startManagingCursor(remindersCursor);

        // Create an array to specify the fields we want to display in the list
        // (only TITLE)
        String[] from = new String[]{RemindersDbAdapter.KEY_TITLE,
                RemindersDbAdapter.KEY_BODY};

        // and an array of the fields we want to bind those fields to (in this
        // case just text1)
        int[] to = new int[]{R.id.textViewrow1, R.id.textViewrow2};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter reminders = new SimpleCursorAdapter(this,
                R.layout.reminder_row, remindersCursor, from, to, 0);
        setListAdapter(reminders);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.action_menu
                , menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(this, TaskPreferences.class);
                startActivity(i);
                return true;

            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileEdit.class);
                startActivity(intent);
                return true;
            case R.id.action_help:
                String adress = new IpAddress().getIpAddress();
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse(adress));
                startActivity(viewIntent);
                return true;
            case R.id.action_logout:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
            case R.id.action_about_us:
                Intent aboutus = new Intent(this, AboutUs.class );
                startActivity(aboutus);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_menu_item_longpress, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                mDbHelper.deleteReminder(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createReminder() {
        Intent i = new Intent(this, ReminderEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(RemindersDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
