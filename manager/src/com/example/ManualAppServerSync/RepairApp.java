package com.example.ManualAppServerSync;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Interface.IInstallation;
import com.example.Interface.IRegistration;
import com.example.Interface.Synchronization;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.Constants;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.OneJSONValue;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;
import com.example.anand_roadwayss.Welcome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class RepairApp extends ActionBarActivity implements
        IRegistration, IInstallation, Synchronization {

    Button repairApp;
    ArrayList<String> processList = new ArrayList<String>();
    int count = 0;
    DBAdapter db;
    String name = "", email = "", phone = "", pin = "", strEmpType = "", productKey = "";
    final IRegistration mRegistration = this;
    String adress = new IpAddress().getIpAddress();
    byte[] byteArray,receipt,signDb;
    Toolbar toolbar;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_app);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("LEAVE");
        setSupportActionBar(toolbar);
        //updateToValue();

        repairApp = (Button) findViewById(R.id.activityRepairAppBtnRepair);
        repairApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    db = new DBAdapter(getApplicationContext());

                    try {
                        String registrationAuthKey = new Constants()
                                .getRegistrationAuthKey();
                        db.open();
                        Cursor regDataCursor = db.retrieveAllDataFromTable(DBAdapter.getTableRegistration());
                        if (regDataCursor.getCount() == 1) {
                            regDataCursor.moveToFirst();
                            name = regDataCursor.getString(regDataCursor
                                    .getColumnIndex(DBAdapter.getKeyName()));
                            email = regDataCursor.getString(regDataCursor
                                    .getColumnIndex(DBAdapter.getRegistrationMail()));
                            phone = regDataCursor.getString(regDataCursor
                                    .getColumnIndex(DBAdapter.getKeyPhNo()));
                            pin = regDataCursor.getString(regDataCursor
                                    .getColumnIndex(DBAdapter.getRegistrationPin()));
                            strEmpType = regDataCursor.getString(regDataCursor
                                    .getColumnIndex(DBAdapter.getKeyUserType()));
                            productKey = regDataCursor.getString(regDataCursor
                                    .getColumnIndex(DBAdapter.getKeyProductKey()));

                            regDataCursor.close();
                        }
                        db.close();

                        db.open();
                        cursor = db.getSignature();
                        signDb = cursor.getBlob(cursor.getColumnIndex("signature"));
                        db.close();
                        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String IMEI = mngr.getDeviceId();
                        SendToWebService send = new SendToWebService(
                                RepairApp.this,
                                mRegistration);

                        send.execute("9", "RegisterAnApplication",
                                registrationAuthKey, name,
                                strEmpType, email, phone, pin,
                                IMEI, productKey);

                    } catch (Exception e)
                    {
                        ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                                + " " + "[ repairApp.setOnClickListener]", e.toString());
                    }


                } catch (Exception e)
                {
                    ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass().toString()
                            + " " + "[ repairApp.setOnClickListener]", e.toString());
                }

            }
        });

    }

    @Override
    public void onRegistrationComplete(String response) {
        if (response.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(
                    RepairApp.this);
            cd.ConnectingToInternet();
        } else if (response.equals("There was an error processing")) {
            Toast.makeText(getApplicationContext(),
                    "Try After Sometime ", Toast.LENGTH_SHORT)
                    .show();


        } else if (response.contains("refused")) {
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();

        } else if (response.contains("java.net.SocketTimeoutException")) {

            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();

        } else if (response.contains("The remote server returned an error")) {

            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();

        } else {

            jsonParsing(response);
        }
    }

    void jsonParsing(String response) {
        String jsonData = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                if (!(d.isNull("status")) && !(d.isNull("appAuthKey"))
                        && !(d.isNull("ipAddress"))
                        && !(d.isNull("portNumber"))) {
                    db.open();
                    db.onTerminate();
                    db.close();

                    String srvrStatus = d.getString("status").trim();
                    String appAuthKey = d.getString("appAuthKey").trim();
                    String ipAddress = d.getString("ipAddress").trim();
                    String portNumber = d.getString("portNumber").trim();
                    String clientName = d.getString("clientName").trim();
                    String appUrl = "http://" + ipAddress + ":" + portNumber
                            + "/";

                    // Preference For Auth Key
                    SharedPreferences AppUrl = getSharedPreferences("AppUrl",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = AppUrl.edit();
                    editor.putString("appUrl", appUrl);
                    editor.putString("AuthKey", appAuthKey);
                    editor.commit();
                    // End

                    db.open();
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyName(), name);
                    cv.put(DBAdapter.getRegistrationMail(), email);
                    cv.put(DBAdapter.getKeyPhNo(), phone);
                    cv.put(DBAdapter.getRegistrationPin(), pin);
                    cv.put(DBAdapter.getRegistrationIs(), 0);
                    cv.put(DBAdapter.getKeyProductKey(), productKey);
                    cv.put(DBAdapter.getKeyUserType(), strEmpType);
                    cv.put(DBAdapter.getKeyClientname(), clientName);
                    long id = db.insertContact(DBAdapter.getTableRegistration(), cv);
                    db.close();

                    db.open();
                    db.addSignature(signDb);
                    db.close();

                    final IInstallation installed = this;
                    SendToWebService send = new SendToWebService(
                            RepairApp.this, installed);
                    send.execute("24", "SyncCheckAfterInstall");

                } else {
                    OneJSONValue one = new OneJSONValue();
                    String status = one.jsonParsing1(response);
                    // Toast.makeText(getApplicationContext(), status,
                    // Toast.LENGTH_SHORT).show();
                    switch (status) {
                        case "invalid server authkey":
                            Toast.makeText(getApplicationContext(),
                                    "Please Contact Server ", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case "does not exist":
                            Toast.makeText(getApplicationContext(),
                                    "Please Contact Server", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), status,
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            } catch (Exception e) {

                if (e.toString().contains(
                        "org.apache.http.conn.HttpHostConnectException: Connection to "
                                + adress + " refused")) {
                    Toast.makeText(
                            getApplicationContext(),
                            " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                            Toast.LENGTH_LONG).show();
                } else if (e.toString().contains(
                        "java.net.SocketTimeoutException")) {
                    Toast.makeText(
                            getApplicationContext(),
                            " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                            Toast.LENGTH_LONG).show();
                }
                else if (e.toString().contains(
                        "TimeoutException")) {
                    Toast.makeText(
                            getApplicationContext(),
                            " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            " UNABLE TO REGISTER..PLEASE TRY AGAIN",
                            Toast.LENGTH_LONG).show();
                }
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[jsonParsing]" + " " + response, e.toString());
            }
    }

    @Override
    public void iInstallation(String response) {
        if (response.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(
                    RepairApp.this);
            cd.ConnectingToInternet();
        } else if (response.equals("There was an error processing")) {
            Toast.makeText(getApplicationContext(),
                    "Try After Sometime ", Toast.LENGTH_SHORT)
                    .show();


        } else if ((response.contains("refused") || (response.contains("timed out")))) {
            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();

        } else if (response.contains("java.net.SocketTimeoutException")) {

            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();

        } else if (response.contains("The remote server returned an error")) {

            ImageView image = new ImageView(this);
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();

        } else {
            jsonParser(response);
        }

    }

    public void jsonParser(String response) {

        if (response != null)
            try {
                processList.clear();
                count = 0;
                String statuschk = null;
                JSONObject jsonResponse = new JSONObject(response);

                String jsonData1 = jsonResponse.getString("d");

                // if (!jsonData1.equals("\"Data does not Exist\"")) {
                if (jsonData1.contains("\"process_id\"")) {
                    JSONObject d = new JSONObject(jsonData1);

                    JSONArray listOfValues = d.getJSONArray("listOfValues");
                    // JSONArray jsonArray=new JSONArray(listOfValues);

                    for (int j = 0; j < listOfValues.length(); j++) {
                        String value = listOfValues.getString(j).trim();
                        processList.add(value);
                    }
                    if (processList.size() > 0) {
                        SyncStart(count);
                    }
                } else if (jsonData1.contains("\"data does not exist\"")) {
                    SharedPreferences login = getSharedPreferences("testapp",
                            MODE_PRIVATE);
                    SharedPreferences.Editor edit = login.edit();
                    edit.putString("register", "true");
                    edit.commit();

                    SharedPreferences login1 = getSharedPreferences("updateApp",
                            MODE_PRIVATE);
                    SharedPreferences.Editor edit1 = login1.edit();
                    edit1.putString("update", "true");
                    edit1.commit();
                    Intent intent_welcome = new Intent(getApplicationContext(),
                            Welcome.class);
                    startActivity(intent_welcome);
                    finish();

                } else {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[jsonParser]", response);
                }

            } catch (Exception e) {
                if (e.toString().contains(
                        "org.apache.http.conn.HttpHostConnectException: Connection to "
                                + adress + " refused")) {
                    Toast.makeText(
                            getApplicationContext(),
                            " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                            Toast.LENGTH_LONG).show();
                } else if (e.toString().contains(
                        "java.net.SocketTimeoutException")) {
                    Toast.makeText(
                            getApplicationContext(),
                            " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            " UNABLE TO REGISTER..PLEASE TRY AGAIN",
                            Toast.LENGTH_LONG).show();
                }
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[jsonParser]", e.toString());
            }
    }

    private void SyncStart(int i) {

        final Synchronization Sync = this;
        SendToWebService send = new SendToWebService(RepairApp.this,
                Sync);
        if (count < processList.size()) {
            send.execute("25", "DataSync", processList.get(count));
        } else if (count == processList.size()) {
            SharedPreferences login = getSharedPreferences("testapp",
                    MODE_PRIVATE);
            SharedPreferences.Editor edit = login.edit();
            edit.putString("register", "true");
            edit.commit();

            updateToValue();

            Intent intent_welcome = new Intent(getApplicationContext(),
                    Welcome.class);
            startActivity(intent_welcome);
            finish();
        }

    }

    @Override
    public void onSynchronization(String response) {
        jsonParsing1(response);
    }

    private void jsonParsing1(String response) {

        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);

                String jsonData = jsonResponse.getString("d");
                JSONArray table1 = new JSONArray(jsonData);
                JSONObject c1 = table1.getJSONObject(0);
                String status = c1.getString("status");
                if (status.equals("OK")) {
                    JSONObject c = table1.getJSONObject(table1.length() - 1);
                    int tableId = c.getInt("tableId");

                    switch (tableId) {
                        case 1:
                            addToVehicleTable(response);
                            break;
                        case 2:
                            addToEmployeeTable(response);
                            break;
                        case 3:
                            addToLocationTable(response);
                            break;
                        case 4:
                            addToFuelTable(response);
                            break;
                        case 5:
                            addToAdvance(response);
                            break;
                        case 6:
                            addToPaymentTable(response);
                            break;
                        case 7:
                            //addToTripTable(response);
                            break;
                    }
                } else if (status.equals("data does not exist")) {
                    SyncStart(++count);
                } else {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                                    .toString() + " " + "[jsonParsing1]",
                            "Error Occur In DataSync For This Process_Id:"
                                    + processList.get(count));
                    Log.i("Invalid ProcessId", processList.get(count));
                    SyncStart(++count);
                }

            } catch (Exception e) {
                if (e.toString().contains(
                        "org.apache.http.conn.HttpHostConnectException: Connection to "
                                + adress + " refused")) {
                    Toast.makeText(
                            getApplicationContext(),
                            " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                            Toast.LENGTH_LONG).show();
                } else if (e.toString().contains(
                        "java.net.SocketTimeoutException")) {
                    Toast.makeText(
                            getApplicationContext(),
                            " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            " UNABLE TO REGISTER..PLEASE TRY AGAIN",
                            Toast.LENGTH_LONG).show();
                }

                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[jsonParsing1]", e.toString());
            }

    }


    private void addToAdvance(String response) {

        if (response != null)
            try {
                updateToNil();
                JSONObject jsonResponse = new JSONObject(response);

                String jsonData = jsonResponse.getString("d");
                JSONArray table1 = new JSONArray(jsonData);

                DBAdapter db = new DBAdapter(RepairApp.this);
                db.open();
                int a = 1;
                for (int i = 1; i < table1.length() - 1; i++) {

                    JSONObject c = table1.getJSONObject(i);
                    String A_Date = c.getString("advance_date");
                    String date = A_Date.substring(0, 10);
                    String Type = c.getString("advance_type");
                    String VehNo = c.getString("vehicle_no").toUpperCase(Locale.getDefault());
                    String ename = c.getString("employee_name").toUpperCase(Locale.getDefault());
                    String VNo = c.getString("bill_no");
                    Double amount = c.getDouble("amount");
                    String Receipt=c.getString("bill_image");
//                    if (!Receipt.equals("")) {
//                        receipt = StringToBitMap(Receipt);
//                    } else {
//                        Resources res = getResources();
//                        Drawable drawable = res.getDrawable(R.drawable.no_image);
//                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        receipt = stream.toByteArray();
//                    }

                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyDate(), date);
                    cv.put(DBAdapter.getKeyVoucherNo(), VNo);
                    cv.put(DBAdapter.getKeyAmount(), amount);
                    cv.put(DBAdapter.getKeyName(), ename);
                    cv.put(DBAdapter.getKeyVehicleNo(), VehNo);
                    cv.put(DBAdapter.getKeyAdvanceType(), Type);
                    cv.put(DBAdapter.getKeyReceipt(),receipt);
                    long No = db.insertContact(DBAdapter.getAdvanceDetails(),
                            cv);
                    if (No != -1)
                        ++a;
                }
                db.close();
                if (a == table1.length() - 1) {
                    SendToWebService send = new SendToWebService(
                            RepairApp.this);
                    String res = send.execute("26", "Ack",
                            processList.get(count)).get();
                    if (res != null) {

                    }
                    updateToValue();
                    SyncStart(++count);
                }

            } catch (Exception e) {
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[addToAdvance]", e.toString());

            }

    }

    private void addToVehicleTable(String response) {

        try {
            updateToNil();
            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RepairApp.this);
            db.open();
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String vno = c.getString("vehicle_no").toUpperCase(Locale.getDefault());
                String Type = c.getString("vehicle_type");
                int VehNo = c.getInt("std_mileage");
                String imei = c.getString("imei_no");
                String pno = c.getString("phone_no");
                String check = db.checkvehTforDataExist(vno);
                if (check.equals("NOT EXIST")) {
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyVehicleNo(), vno);
                    cv.put(DBAdapter.getKeyVehType(), Type);
                    cv.put(DBAdapter.getKeyVehMileage(), VehNo);
                    cv.put(DBAdapter.getKeyImei(), imei);
                    cv.put(DBAdapter.getKeyVehPhNo(), pno);
                    long No = db.insertContact(DBAdapter.getVehicleDetails(),
                            cv);
                    if (No != -1)
                        ++a;
                } else
                    ++a;


            }
            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(
                        RepairApp.this);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                updateToValue();
                SyncStart(++count);

            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToVehicleTable]", e.toString());
        }
    }



    private void addToPaymentTable(String response) {

        try {
            updateToNil();
            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RepairApp.this);
            db.open();
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String P_Date = c.getString("payment_date");
                String[] parts = P_Date.split("T");
                P_Date = parts[0];
                String comm = String.valueOf(c.getInt("commission"));
                String ename = c.getString("employee_name").toUpperCase(Locale.getDefault());
                String vNo = c.getString("bill_no");
                String Amount = c.getString("amount");
                String Deduction = c.getString("amount_deduction");
                String Mdedu = c.getString("mileage_deduction");
                String pType = c.getString("payment_type");
                String Receipt=c.getString("bill_image");
//                if (!Receipt.equals("")) {
//                    receipt = StringToBitMap(Receipt);
//                } else {
//                    Resources res = getResources();
//                    Drawable drawable = res.getDrawable(R.drawable.no_image);
//                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                    receipt = stream.toByteArray();
//                }
                ContentValues cv = new ContentValues();
                cv.put(DBAdapter.getKeyDate(), P_Date);
                cv.put(DBAdapter.getKeyPaymentType(), pType);
                cv.put(DBAdapter.getKeyCommission(), comm);
                cv.put(DBAdapter.getKeyName(), ename);
                cv.put(DBAdapter.getKeyVoucherNo(), vNo);
                cv.put(DBAdapter.getKeyAmount(), Amount);
                cv.put(DBAdapter.getKeyDeduction(), Deduction);
                cv.put(DBAdapter.getKeyMileagededuction(), Mdedu);
                cv.put(DBAdapter.getKeyReceipt(),receipt);
                long No = db.insertContact(DBAdapter.getPaymentDetails(), cv);
                if (No != -1)
                    ++a;
            }
            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(
                        RepairApp.this);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }

                updateToValue();
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToPaymentTable]", e.toString());
        }
    }

    private void addToLocationTable(String response) {

        try {
            updateToNil();
            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RepairApp.this);
            db.open();
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String Lname = c.getString("location_name");
                String Type = c.getString("location_type");
                String Lat = c.getString("latitude");
                String Long = c.getString("longitude");
                //String Dist = c.getString("Distance");
                //String Amount = c.getString("Amount");
                ContentValues cv = new ContentValues();
                cv.put(DBAdapter.getKeyLocName(), Lname);
                cv.put(DBAdapter.getKeyLongitude(), Long);
                cv.put(DBAdapter.getKeyLatitude(), Lat);
                //  cv.put(DBAdapter.getKeyDistance(), Dist);
                //  cv.put(DBAdapter.getKeyAmount(), Amount);
                cv.put(DBAdapter.getKeyLocationtype(), Type);
                long No = db.insertContact(DBAdapter.getLocation(), cv);
                if (No != -1)
                    ++a;
            }
            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(
                        RepairApp.this);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }

                updateToValue();
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToLocationTable]", e.toString());
        }
    }

    private void addToEmployeeTable(String response) {

        try {
            updateToNil();
            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RepairApp.this);
            db.open();
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String Type = c.getString("employee_type");
                String Ename = c.getString("employee_name").toUpperCase(Locale.getDefault());
                String Ephoto = c.getString("employee_photo");
                receipt=StringToBitMap(Ephoto);
                String ELic = c.getString("licence_no");
                String E_pNo = c.getString("phone_no");
                String Employee_Address = c.getString("address");
                String EId = c.getString("employee_id");
                int Commission = c.getInt("commission");
                int salary = c.getInt("salary");
                String chec = db.checkCleanerTableforDataExist(Ename);
                String chec2 = db.checkDrvierTableforDataExist(Ename);
                if (chec.equals("NOT EXIST") && chec2.equals("NOT EXIST")) {

                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyName(), Ename);
                    cv.put(DBAdapter.getKeyManagetype(), Type);
                    cv.put(DBAdapter.getKeyPhNo(), E_pNo);
                    cv.put(DBAdapter.getKeyAddr(), Employee_Address);
                    cv.put(DBAdapter.getKeyPhoto(), receipt);
                    cv.put(DBAdapter.getKeyLicNo(), ELic);
                    cv.put(DBAdapter.getKeyEmployeeId(), EId);
                    cv.put(DBAdapter.getKeySalary(), salary);
                    cv.put(DBAdapter.getKeyCommission(), Commission);
                    long No = db.insertContact(DBAdapter.getEmployeeDetails(),
                            cv);
                    if (No != -1)
                        ++a;
                } else
                    ++a;
            }
            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(
                        RepairApp.this);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }

                updateToValue();
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToEmployeeTable]", e.toString());
        }
    }

    private void addToFuelTable(String response) {

        try {
            updateToNil();
            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RepairApp.this);
            db.open();
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String Date1 = c.getString("fuel_filled_date");
                String Date = Date1.substring(0, 10);
                String VNo = c.getString("vehicle_no");
                String ename = c.getString("employee_name").toUpperCase(Locale.getDefault());
                String Smtr = String.valueOf(c.getInt("km_in_speedometer"));
                String Fuel = String.valueOf(c.getInt("fuel_volume"));
                String rowId = String.valueOf(c.getInt("fuel_id"));
                //String ename = db.getEmployeeName(EId,
                //        DBAdapter.getEmployeeDetails(), "Name");
                ContentValues cv = new ContentValues();
                cv.put(DBAdapter.getKeyMdate(), Date);
                cv.put(DBAdapter.getKeyMvehicle(), VNo);
                cv.put(DBAdapter.getKeyMdriver(), ename);
                cv.put(DBAdapter.getKeySpeedometer(), Smtr);
                cv.put(DBAdapter.getKeyFuel(), Fuel);
                cv.put(DBAdapter.getKeyFuelRowId(), rowId);
                long No = db.insertContact(DBAdapter.getFuelDetails(), cv);
                if (No != -1)
                    ++a;
            }
            db.close();
            if (a == table1.length() - 1) {

                SendToWebService send = new SendToWebService(
                        RepairApp.this);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }

                updateToValue();
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToFuelTable]", e.toString());
        }

    }

    public void updateToNil(){
        SharedPreferences login = getSharedPreferences("updateApp",
                MODE_PRIVATE);
        SharedPreferences.Editor edit = login.edit();
        edit.putString("update", "nil");
        edit.commit();
    }

    public void updateToValue(){
        SharedPreferences login = getSharedPreferences("updateApp",
                MODE_PRIVATE);
        SharedPreferences.Editor edit = login.edit();
        edit.putString("update", "true");
        edit.commit();
    }

    public byte [] StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            return byteArray;
        }catch(Exception e)
        {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[StringToBitMap()]", e.toString());
            e.getMessage();
            return null;
        }
    }
}
