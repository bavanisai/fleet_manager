/**
 *
 */
package com.example.anand_roadwayss;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AbsenceReport.LeaveMainActivity;
import com.example.AdvanceModule.AdvanceMain;
import com.example.DashBoardModule.DashBoard;
import com.example.Expense.Expense;
import com.example.FuelModule.FuelActivity;
import com.example.FuelModule.FuelReport;
import com.example.Inbox.InboxList;
import com.example.ManageResources.MainActivity;
import com.example.ManualAppServerSync.RepairApp;
import com.example.PaymentModule.PaymentDriver;
import com.example.Reminder.ReminderListActivity;
import com.example.TrackingModule.TrackActivity;
import com.example.TrackingModule.TrackReportActivity;
import com.example.tripmodule.ConflictTripList;
import com.example.tripmodule.TripActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * @author MKSoft01
 */
public class Welcome extends ActionBarActivity implements OnClickListener {

    String selectedModule;
    Toolbar toolbar;
    DBAdapter db;
    SharedPreferences sp;
    ImageView img;
    TextView txt;
    String clientName;
    public static MatrixCursor imeiList=null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        sp = getSharedPreferences("testapp", Context.MODE_PRIVATE);
        String regComplete = sp.getString("checkReg", "false");
        if(regComplete.equals("true")) {
            //         App Demo
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.transparent);
            RelativeLayout layout= (RelativeLayout) dialog.findViewById(R.id.transparent);
            layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                    final Dialog d1 = new Dialog(Welcome.this, android.R.style.Theme_Translucent_NoTitleBar);
                    d1.setContentView(R.layout.transparent_fuel);
                    RelativeLayout layoutfuel = (RelativeLayout) d1.findViewById(R.id.fueltransparent);

                    layoutfuel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d1.dismiss();
                            final Dialog d2 = new Dialog(Welcome.this, android.R.style.Theme_Translucent_NoTitleBar);
                            d2.setContentView(R.layout.transparent_advance);
                            RelativeLayout layoutAdv = (RelativeLayout) d2.findViewById(R.id.transAdvan);
                            layoutAdv.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d2.dismiss();
                                    final Dialog d3 = new Dialog(Welcome.this, android.R.style.Theme_Translucent_NoTitleBar);
                                    d3.setContentView(R.layout.transparent_trips);
                                    RelativeLayout transpTrips = (RelativeLayout) d3.findViewById(R.id.transTrips);
                                    transpTrips.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            d3.dismiss();
                                            final Dialog d4 = new Dialog(Welcome.this, android.R.style.Theme_Translucent_NoTitleBar);
                                            d4.setContentView(R.layout.transparent_tracking);
                                            RelativeLayout transTracking = (RelativeLayout) d4.findViewById(R.id.transTracking);
                                            transTracking.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    d4.dismiss();
                                                    final  Dialog d5 = new Dialog(Welcome.this, android.R.style.Theme_Translucent_NoTitleBar);
                                                    d5.setContentView(R.layout.transparent_payment);
                                                    RelativeLayout transPay = (RelativeLayout) d5.findViewById(R.id.transPayment);
                                                    transPay.setOnClickListener(new OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            d5.dismiss();
                                                            final Dialog d6 = new Dialog(Welcome.this, android.R.style.Theme_Translucent_NoTitleBar);
                                                            d6.setContentView(R.layout.transparent_dashboard);
                                                            RelativeLayout transDashbd = (RelativeLayout) d6.findViewById(R.id.transDashboard);
                                                            transDashbd.setOnClickListener(new OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    d6.dismiss();
                                                                    final Dialog d7 = new Dialog(Welcome.this,android.R.style.Theme_Translucent_NoTitleBar);
                                                                    d7.setContentView(R.layout.transparent_reminder);
                                                                    RelativeLayout transpRemind = (RelativeLayout) d7.findViewById(R.id.transReminder);
                                                                    transpRemind.setOnClickListener(new OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            d7.dismiss();
                                                                            final Dialog d8 = new Dialog(Welcome.this, android.R.style.Theme_Translucent_NoTitleBar);
                                                                            d8.setContentView(R.layout.transparent_leave);
                                                                            RelativeLayout transnpLeave = (RelativeLayout) d8.findViewById(R.id.transLeave);
                                                                            transnpLeave.setOnClickListener(new OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    d8.dismiss();
                                                                                }
                                                                            });
                                                                            d8.show();
                                                                        }
                                                                    });
                                                                    d7.show();
                                                                }
                                                            });
                                                            d6.show();
                                                        }
                                                    });
                                                    d5.show();
                                                }
                                            });
                                            d4.show();
                                        }
                                    });
                                    d3.show();
                                }
                            });
                            d2.show();
                        }
                    });
                    d1.show();

                }

            });
            dialog.show();

            SharedPreferences.Editor edit = sp.edit();
            edit.putString("checkReg", "false");
            edit.commit();
        }


        toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        db = new DBAdapter(getApplicationContext());
        db.open();
        String a = db.getphone();
        int i = db.isRegistered(a);
        db.close();
        try {
            if (!SendToWebService.isDemoApp && i == 0) {
                if (isConnectingToInternet()) {

                    Intent RegIntent = new Intent(
                            "com.google.android.c2dm.intent.REGISTER");
                    RegIntent.putExtra("app", PendingIntent.getBroadcast(
                            getBaseContext(), 0, RegIntent, 0));
                    RegIntent.putExtra("sender", "628380084865");
                    startService(RegIntent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please Connect To Internet", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(this, this
                            .getClass().toString() + " " + "[onCreate()]",
                    e.toString());
            e.printStackTrace();
        }
        // END

        findViewById(R.id.welcomeIVReminders).setOnClickListener(this);
        findViewById(R.id.welcomeIVAdvance).setOnClickListener(this);
        findViewById(R.id.welcomeIVCurrentTrips).setOnClickListener(this);
        findViewById(R.id.welcomeIVDashBoard).setOnClickListener(this);
        findViewById(R.id.welcomeIVLeave).setOnClickListener(this);
        findViewById(R.id.welcomeIVNewEntries).setOnClickListener(this);
        findViewById(R.id.welcomeIVSalary).setOnClickListener(this);
        findViewById(R.id.welcomeIVTracking).setOnClickListener(this);
        findViewById(R.id.welcomeIvFuel).setOnClickListener(this);
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.welcomeIVReminders:
                SharedPreferences login = getSharedPreferences("updateApp",
                        MODE_PRIVATE);
                String upStatus=login.getString("update","nil");
                if(!upStatus.equals("nil")) {
                    Intent welcomeIntent = new Intent(Welcome.this,
                            ReminderListActivity.class);
                    startActivity(welcomeIntent);
                }
                else{
                    Intent welcomeIntent = new Intent(Welcome.this,
                            RepairApp.class);
                    startActivity(welcomeIntent);
                }
                break;
            case R.id.welcomeIVAdvance:
                SharedPreferences login1 = getSharedPreferences("updateApp",
                        MODE_PRIVATE);
                String upStatus1=login1.getString("update","nil");
                if(!upStatus1.equals("nil")) {
                    Intent intentAdvanceSync = new Intent(getApplicationContext(),
                            BackUpService.class);
                    intentAdvanceSync.putExtra("act", "AdvanceModule");
                    startService(intentAdvanceSync);
                    Intent advanceIntent = new Intent(Welcome.this, AdvanceMain.class);
                    startActivity(advanceIntent);
                }
                else{
                    Intent welcomeIntent = new Intent(Welcome.this,
                            RepairApp.class);
                    startActivity(welcomeIntent);
                }
                break;
            case R.id.welcomeIVCurrentTrips:
                Intent intentTripSync = new Intent(getApplicationContext(),
                        BackUpService.class);
                intentTripSync.putExtra("act", "TripModule");
                startService(intentTripSync);
                TripAlertDialog();
                break;

            case R.id.welcomeIVDashBoard:
                SharedPreferences login3 = getSharedPreferences("updateApp",
                        MODE_PRIVATE);
                String upStatus3=login3.getString("update","nil");
                if(!upStatus3.equals("nil")) {
                    Intent dashBoardIntent = new Intent(Welcome.this, DashBoard.class);
                    startActivity(dashBoardIntent);
                }
                else{
                Intent welcomeIntent = new Intent(Welcome.this,
                        RepairApp.class);
                startActivity(welcomeIntent);
            }
                break;
            case R.id.welcomeIVLeave:
                SharedPreferences login4 = getSharedPreferences("updateApp",
                        MODE_PRIVATE);
                String upStatus4=login4.getString("update","nil");
                if(!upStatus4.equals("nil")) {
                    Intent leaveIntent = new Intent(Welcome.this,
                            LeaveMainActivity.class);
                    startActivity(leaveIntent);
                }

                else{
                    Intent welcomeIntent = new Intent(Welcome.this,
                            RepairApp.class);
                    startActivity(welcomeIntent);
                }
                break;
            case R.id.welcomeIVNewEntries:
                try {
                    db.open();
                    Cursor profileDataCursor = db.retrieveProfileData();
                    if (profileDataCursor.getCount() == 1) {
                        profileDataCursor.moveToFirst();
                        clientName = profileDataCursor.getString(profileDataCursor
                                .getColumnIndex(DBAdapter.getKeyClientname()));

                        profileDataCursor.close();
                    }
                    db.close();

                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(this, this
                            .getClass().toString()
                            + " "
                            + "New Entries GetClientName", e.toString());
                }
            try {
                getClientDevices();

            }
            catch (Exception e){
                ExceptionMessage.exceptionLog(this, this
                        .getClass().toString()
                        + " "
                        + "New Entries GetClientDevices", e.toString());
            }

                try{
                SharedPreferences login5 = getSharedPreferences("updateApp",
                        MODE_PRIVATE);
                String upStatus5=login5.getString("update","nil");
                if(!upStatus5.equals("nil")) {
                    Intent intentNewEntriesSync = new Intent(getApplicationContext(),
                            BackUpService.class);
                    intentNewEntriesSync.putExtra("act", "ManageResources");
                    startService(intentNewEntriesSync);
                    Intent newEntriesIntent = new Intent(Welcome.this,
                            MainActivity.class);
                    startActivity(newEntriesIntent);
                }
                else{
                Intent welcomeIntent = new Intent(Welcome.this,
                        RepairApp.class);
                startActivity(welcomeIntent);
            }
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(this, this
                                    .getClass().toString() + " " + "[R.id.welcomeIVNewEntries-login5]",
                            e.toString());
                    e.toString();
                }
                break;
            case R.id.welcomeIVSalary:
                Intent intentPaymentSync = new Intent(getApplicationContext(),
                        BackUpService.class);
                intentPaymentSync.putExtra("act", "PaymentModule");
                startService(intentPaymentSync);
                paymentAlertDialog();
                break;
            case R.id.welcomeIVTracking:
                Intent intentTrackSync = new Intent(getApplicationContext(),
                        BackUpService.class);
                intentTrackSync.putExtra("act", "TrackingModule");
                startService(intentTrackSync);
                trackAlertDialog();
                break;
            case R.id.welcomeIvFuel:
                Intent intentFuelSync = new Intent(getApplicationContext(),
                        BackUpService.class);
                intentFuelSync.putExtra("act", "FuelModule");
                startService(intentFuelSync);
//                Intent fuelIntent = new Intent(Welcome.this, FuelActivity.class);
//                startActivity(fuelIntent);
                fuelAlertDialog();
                break;

        }
    }

    private void paymentAlertDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_payment_expense);
        LinearLayout salary=(LinearLayout)dialog.findViewById(R.id.dialogPaymentExpenseLinLayoutSalary);
        LinearLayout expense=(LinearLayout)dialog.findViewById(R.id.dialogPaymentExpenseLinLayoutExpense);
        salary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salaryIntent = new Intent(Welcome.this, PaymentDriver.class);
                startActivity(salaryIntent);
                dialog.cancel();
            }
        });

        expense.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent expenseIntent = new Intent(Welcome.this, Expense.class);
                startActivity(expenseIntent);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void TripAlertDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dilog_trip_conflict);
        LinearLayout salary=(LinearLayout)dialog.findViewById(R.id.dialogTrips);
        LinearLayout expense=(LinearLayout)dialog.findViewById(R.id.dialogConflictTrips);
        salary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent salaryIntent = new Intent(Welcome.this, TripActivity.class);
                startActivity(salaryIntent);
                dialog.cancel();
            }
        });

        expense.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent expenseIntent = new Intent(Welcome.this, ConflictTripList.class);
                startActivity(expenseIntent);
                dialog.cancel();
            }
        });
        dialog.show();
    }


    private void trackAlertDialog(){
        final Dialog dialog = new Dialog(this);
        //   dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dilog_track_layout);
        //    dialog.setTitle("SELECT OPTIONS");
        LinearLayout track=(LinearLayout)dialog.findViewById(R.id.dialogTrackLinLayoutTrack);
        LinearLayout report=(LinearLayout)dialog.findViewById(R.id.dialogTrackLinLayoutReport);
        track.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trackIntent = new Intent(Welcome.this, TrackActivity.class);
                startActivity(trackIntent);
                dialog.cancel();
            }
        });

        report.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = new Intent(Welcome.this, TrackReportActivity.class);
                startActivity(reportIntent);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    //Fuel Alert Dialog
    private void fuelAlertDialog(){
        final Dialog dialog = new Dialog(this);
        //   dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Drawable d = new ColorDrawable(Color.BLACK);
//        d.setAlpha(130);
//        dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_payment);
        //    dialog.setTitle("SELECT OPTIONS");
        LinearLayout entry=(LinearLayout)dialog.findViewById(R.id.dialogPaymentEntry);
        LinearLayout report=(LinearLayout)dialog.findViewById(R.id.dialogPaymentReport);
        entry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent entryIntent = new Intent(Welcome.this, FuelActivity.class);
                startActivity(entryIntent);
                dialog.cancel();
            }
        });

        report.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PayReportIntent = new Intent(Welcome.this, FuelReport.class);
                startActivity(PayReportIntent);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void alertDialog() {
        final CharSequence[] items = {"Trips", "Conflict Trips"};
        selectedModule = (String) items[0];
        // String
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SELECT OPTIONS").setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface,
                                        int item) {
                        // Toast.makeText(getApplicationContext(), items[item],
                        // Toast.LENGTH_SHORT).show();
                        selectedModule = items[item].toString();
                    }
                });
        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(),
                        // "You clicked on Cancel", Toast.LENGTH_SHORT)
                        // .show();
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (selectedModule != null) {
                    switch (selectedModule) {
                        case "Trips":
                            Intent currentTripIntent = new Intent(Welcome.this,
                                    TripActivity.class);
                            startActivity(currentTripIntent);

                            break;

                        case "Conflict Trips":

                            Intent conflictTripIntent = new Intent(Welcome.this,
                                    ConflictTripList.class);
                            startActivity(conflictTripIntent);

                            break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please select the module", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu_welcome, menu);

        MenuItem item = menu.findItem(R.id.action_inbox);
        MenuItemCompat.setActionView(item, R.layout.badge_message_counter_layout);
        View view = MenuItemCompat.getActionView(item);
        txt = (TextView) view.findViewById(R.id.inbox_count);
        img = (ImageView) view.findViewById(R.id.inbox_img);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Welcome.this, InboxList.class);
                startActivity(intent1);
            }
        });
        //UNREAD MESSAGE COUNT
        messageCounter();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_inbox:
                Intent intent1 = new Intent(Welcome.this, InboxList.class);
                startActivity(intent1);
                return true;

            case R.id.action_profile:
                Intent intent = new Intent(Welcome.this, ProfileEdit.class);
                startActivity(intent);
                return true;
            case R.id.action_help:
                String adress = new IpAddress().getIpAddress();
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse(adress));
                startActivity(viewIntent);
                return true;
            case R.id.action_about_us:
                Intent aboutus = new Intent(Welcome.this, AboutUs.class );
                startActivity(aboutus);
                return true;
            case R.id.action_logout:
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
    protected void onRestart() {
        super.onRestart();
        messageCounter();
    }

    public void messageCounter() {
        SharedPreferences UserType = getSharedPreferences(
                "RegisterName", 0);
        String UserTyp = UserType.getString("Name", "");

        if (UserTyp.equals("DEMO")) {
            try {
                txt.setVisibility(View.VISIBLE);
                txt.setText("4");
            } catch (Exception e)
            {
                ExceptionMessage.exceptionLog(this, this
                                .getClass().toString() + " " + "[messageCounter()]",
                        e.toString());
                e.toString();
            }
        }
        else {
            try {
                //UNREAD MESSAGE COUNT ON ICON
                db.open();
                int count = db.unreadMessageCount();
                db.close();
                if (count == 0) {
                    txt.setVisibility(View.INVISIBLE);
                } else {
                    txt.setVisibility(View.VISIBLE);
                    txt.setText(String.valueOf(count));
                }
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(this, this
                                .getClass().toString() + " " + "[messageCounter()-else]",
                        e.toString());
                e.toString();
            }
        }
    }

    public void refresh() {
        Intent i = new Intent(Welcome.this, Welcome.class);
        startActivity(i);

    }

    public void getClientDevices(){
        try {
            SendToWebService send = new SendToWebService(this, 1);
            String res = null;
            String statuschk = null;
            int sKey = 1;

            res = send.execute("50", "GetClientsDevices", clientName).get();

            if (res != null) {
                try {
                JSONObject jsonResponse = new JSONObject(res);
                // getting the data with tag d
                String jsonData = null;
                    jsonData = jsonResponse.getString("d");

                // convert the string to Json array
                JSONArray imeiArray = new JSONArray(jsonData);
                JSONObject status1 = imeiArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();
                // iterating the array
                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                                    .toString() + " " + "[getClientDevices]",
                            statuschk);

                } else if (statuschk.equals("data does not exist")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                                    .toString() + " " + "[getClientDevices]",
                            statuschk);
                } else if (statuschk.equals("ok")) {

                    String[] columnNames = {"_id", "Imei", "Protocol","PhoneNumber"};
                    imeiList = new MatrixCursor(columnNames);

                    JSONArray clientArray=imeiArray.getJSONArray(1);

                    for (int i = 0; i < clientArray.length(); i++) {
                        JSONObject Performance = clientArray
                                .getJSONObject(i);
                        String imei = Performance.getString("imei");
                        String protocol=Performance.getString("protocol");
                        String phone=Performance.getString("phone");
                        imeiList.addRow(new Object[]{sKey,imei,protocol,phone});
                        sKey++;

                    }
                    imeiList.close();
                }

                } catch (JSONException e) {
                    ExceptionMessage.exceptionLog(this, this
                            .getClass().toString()
                            + " "
                            + "New Entries GetClientDevices", e.toString());
                }
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(this, this
                    .getClass().toString()
                    + " "
                    + "New Entries GetClientDevices", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i=new Intent(Welcome.this,Login.class);
        startActivity(i);

    }
}