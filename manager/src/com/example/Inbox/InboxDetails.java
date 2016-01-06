package com.example.Inbox;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.R;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

public class InboxDetails extends AppCompatActivity {

    TextView inboxDetailsTvSubject, inboxDetailsTvDate, inboxDetailsTvName,
            inboxDetailsTvMessage, inboxDetailsTvVehicleNo,
            inboxDetailsTvTripStatus, inboxDetailsTvDriverName,
            inboxDetailsTvDriverPhoneNo, inboxDetailsTvCleanerName, inboxDetailsTvvTripStatus,
            inboxDetailsTvCleanerPhoneNo, inboxDetailsTvCurrentLocation, inboxDetailsTvvCurrentLocation;
    LinearLayout inboxDetailsTvvDriverName,
            inboxDetailsTvvDriverPhoneNo, inboxDetailsTvvCleanerName,
            inboxDetailsTvvCleanerPhoneNo;
    String Id;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_details);
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setIcon(R.drawable.inboxopen);
//        getActionBar().setDisplayShowTitleEnabled(false);
        db = new DBAdapter(this);
        Id = String.valueOf(getIntent().getExtras().getLong("inboxId"));
        bindData();
        getAllData(Id);

    }

    private void bindData() {
        inboxDetailsTvSubject = (TextView) findViewById(R.id.inboxDetailsTvSubject);
        inboxDetailsTvDate = (TextView) findViewById(R.id.inboxDetailsTvDate);
        inboxDetailsTvName = (TextView) findViewById(R.id.inboxDetailsTvName);
        inboxDetailsTvMessage = (TextView) findViewById(R.id.inboxDetailsTvMessage);
        inboxDetailsTvVehicleNo = (TextView) findViewById(R.id.inboxDetailsTvVehicleNo);
        inboxDetailsTvTripStatus = (TextView) findViewById(R.id.inboxDetailsTvTripStatus);
        inboxDetailsTvCurrentLocation = (TextView) findViewById(R.id.inboxDetailsTvCurrentLocation);
        inboxDetailsTvDriverName = (TextView) findViewById(R.id.inboxDetailsTvDriverName);
        inboxDetailsTvDriverPhoneNo = (TextView) findViewById(R.id.inboxDetailsTvDriverPhoneNo);
        inboxDetailsTvCleanerName = (TextView) findViewById(R.id.inboxDetailsTvCleanerName);
        inboxDetailsTvCleanerPhoneNo = (TextView) findViewById(R.id.inboxDetailsTvCleanerPhoneNo);
        inboxDetailsTvvTripStatus = (TextView) findViewById(R.id.inboxDetailsTvvTripStatus);
        inboxDetailsTvvCurrentLocation = (TextView) findViewById(R.id.inboxDetailsTvvCurrentLocation);
        inboxDetailsTvvDriverName = (LinearLayout) findViewById(R.id.inboxDetailsTvvDriverName);
        inboxDetailsTvvDriverPhoneNo = (LinearLayout) findViewById(R.id.inboxDetailsTvvDriverPhoneNo);
        inboxDetailsTvvCleanerName = (LinearLayout) findViewById(R.id.inboxDetailsTvvCleanerName);
        inboxDetailsTvvCleanerPhoneNo = (LinearLayout) findViewById(R.id.inboxDetailsTvvCleanerPhoneNo);

    }

    private void getAllData(String Id) {
        db.open();
        Cursor profileDataCursor = db.retrieveProfileData();
        if (profileDataCursor.getCount() == 1) {
            profileDataCursor.moveToFirst();
            inboxDetailsTvName.setText("Hello , " + " " +
                    profileDataCursor.getString(profileDataCursor
                            .getColumnIndex(DBAdapter.getKeyName())));
            profileDataCursor.close();
        }
        db.close();
        db.open();

        Cursor InboxDetails = db.getInboxDetails(Id);
        if (InboxDetails.moveToFirst()) {
            inboxDetailsTvDate.setText(InboxDetails.getString(InboxDetails
                    .getColumnIndex(DBAdapter.getKeyDate())));
            inboxDetailsTvSubject.setText(InboxDetails.getString(InboxDetails
                    .getColumnIndex(DBAdapter.getKeySubject())));


            String message = InboxDetails.getString(InboxDetails
                    .getColumnIndex(DBAdapter.getKeyMessage()));

            try {
//				JSONObject jsonResponse = new JSONObject(message);
//				jsonData = jsonResponse.getString("d");

				JSONArray a=new JSONArray(message);
				JSONObject d = a.getJSONObject(1);
				
               // JSONObject d = new JSONObject(message);
                inboxDetailsTvVehicleNo.setText(d.getString("vehicleNumber")
                        .trim());
                inboxDetailsTvTripStatus.setText(d.getString("tripStatus")
                        .trim());
                inboxDetailsTvCurrentLocation.setText(d.getString("address")
                        .trim());
                if (d.getString("tripStatus").trim().contains("on trip")) {
                    inboxDetailsTvDriverName.setText(d.getString("diverName")
                            .trim());
                    inboxDetailsTvDriverPhoneNo.setText(d.getString(
                            "driverPhoneNumber").trim());
                    inboxDetailsTvCleanerName.setText(d
                            .getString("cleanerName").trim());
                    inboxDetailsTvCleanerPhoneNo.setText(d.getString(
                            "cleanerPhoneNumber").trim());
                } else {
                    inboxDetailsTvvDriverName.setVisibility(View.GONE);
                    inboxDetailsTvvDriverPhoneNo.setVisibility(View.GONE);
                    inboxDetailsTvvCleanerName.setVisibility(View.GONE);
                    inboxDetailsTvvCleanerPhoneNo.setVisibility(View.GONE);
                }

            } catch (Exception e) {

            }

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
                        InboxDetails.this);
                alert.setTitle("Do u want to delete this item?");

                alert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                             //   deleteInboxItem(Id);
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

//    private void deleteInboxItem(String id) {
//        db.open();
//        db.deleteTripLocaly(DBAdapter.getInboxDetails(), id);
//        db.close();
////		Intent i=new Intent(InboxDetails.this,InboxList.class);
////		startActivity(i);
//        finish();
//    }

}
