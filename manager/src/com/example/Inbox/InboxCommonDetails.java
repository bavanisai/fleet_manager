package com.example.Inbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.R;

import org.json.JSONObject;

public class InboxCommonDetails extends AppCompatActivity {
    TextView inboxCommonDetailsTvSubject, inboxCommonDetailsTvDate, inboxCommonDetailsTvName,
            inboxCommonDetailsTvMessage;
    String Id;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_common_details);
        db = new DBAdapter(this);
        Id = String.valueOf(getIntent().getExtras().getLong("inboxId"));
        bindData();
        getAllData(Id);
    }

    private void bindData() {
        inboxCommonDetailsTvSubject = (TextView) findViewById(R.id.inboxCommonDetailsTvSubject);
        inboxCommonDetailsTvDate = (TextView) findViewById(R.id.inboxCommonDetailsTvDate);
        inboxCommonDetailsTvName = (TextView) findViewById(R.id.inboxCommonDetailsTvName);
        inboxCommonDetailsTvMessage = (TextView) findViewById(R.id.inboxCommonDetailsTvMessage);
    }


    private void getAllData(String Id) {
        db.open();
        Cursor profileDataCursor = db.retrieveProfileData();
        if (profileDataCursor.getCount() == 1) {
            profileDataCursor.moveToFirst();
            inboxCommonDetailsTvName.setText("Hello ," + " " +
                    profileDataCursor.getString(profileDataCursor
                            .getColumnIndex(DBAdapter.getKeyName())));
            profileDataCursor.close();

        }

        db.close();
        db.open();

        Cursor InboxDetails = db.getInboxDetails(Id);
        if (InboxDetails.moveToFirst()) {
            inboxCommonDetailsTvDate.setText(InboxDetails.getString(InboxDetails
                    .getColumnIndex(DBAdapter.getKeyDate())));
            inboxCommonDetailsTvSubject.setText(InboxDetails.getString(InboxDetails
                    .getColumnIndex(DBAdapter.getKeySubject())));

            inboxCommonDetailsTvMessage.setText(InboxDetails.getString(InboxDetails
                    .getColumnIndex(DBAdapter.getKeyMessage())));
            InboxDetails.close();
        }

        db.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.inbox:
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        InboxCommonDetails.this);
                alert.setTitle("Do u want to delete this item?");

                alert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                //deleteInboxItem(Id);
                            }

                        });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                            }
                        });

                alert.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
