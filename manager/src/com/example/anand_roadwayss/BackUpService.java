package com.example.anand_roadwayss;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackUpService extends IntentService {

    // public BackUpService(String act) {
    // super("BackUpService");
    // activit = act;
    //
    // }

    public BackUpService() {
        super("BackUpService");
    }

    ArrayList<String> processList = new ArrayList<String>();
    int count = 0;
    String activit = null;
    byte[] byteArray, receipt;
    byte[] encodeByte;
    Bitmap bitmap;
    ByteArrayOutputStream stream;

    @Override
    protected void onHandleIntent(Intent intent) {
        activit = intent.getStringExtra("act");
        SendToWebService send = new SendToWebService(this, 1);
        try {
            String res = send.execute("34", "SyncCheck").get();
            if (res != null)
                try {
                    JSONObject jsonResponse = new JSONObject(res);
                    processList.clear();
                    count = 0;

                    String jsonData1 = jsonResponse.getString("d");
                    if (jsonData1.contains("\"process_id\"")) {
                        JSONObject d = new JSONObject(jsonData1);

                        String status = d.getString("status");
                        if (status.equals("process_id")) {
                            JSONArray listOfValues = d
                                    .getJSONArray("listOfValues");
                            // JSONArray jsonArray=new JSONArray(listOfValues);

                            for (int j = 0; j < listOfValues.length(); j++) {
                                String value = listOfValues.getString(j).trim();
                                processList.add(value);
                            }
                            if (processList.size() > 0) {

                                SyncStart(count);
                            }
                        }
                    }

                } catch (JSONException e) {
                    ExceptionMessage.exceptionLog(getApplicationContext(), this
                                    .getClass().toString() + " " + "[onHandleIntent]",
                            e.toString());

                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getApplicationContext(), this
                                    .getClass().toString() + " " + "[onHandleIntent]",
                            e.toString());
                }
        } catch (InterruptedException e) {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString() + " " + "[onHandleIntent]",
                    e.toString());
        } catch (ExecutionException e) {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString() + " " + "[onHandleIntent]",
                    e.toString());
        }
    }

    private void SyncStart(int i) {

        // final Synchronization Sync = this;
        SendToWebService send = new SendToWebService(this, 1);
        if (count < processList.size()) {
            try {
                String response = send.execute("25", "DataSync",
                        processList.get(i)).get();
                if (response != null)
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        String jsonData = jsonResponse.getString("d");
                        JSONArray table1 = new JSONArray(jsonData);
                        JSONObject c1 = table1.getJSONObject(0);
                        String status = c1.getString("status");
                        if (status.equals("OK")) {
                            JSONObject c = table1
                                    .getJSONObject(table1.length() - 1);
                            int tableId = c.getInt("tableId");
                            // switch (tableId) {
                            // case 1:
                            // AddToVehicleTable(response);
                            // break;
                            // case 2:
                            // AddToEmployeeTable(response);
                            // break;
                            //
                            // case 3:
                            // addToLocationTable(response);
                            // break;
                            // case 4:
                            // addToFuelTable(response);
                            // break;
                            // case 5:
                            // addToAdvance(response);
                            // break;
                            // case 6:
                            // addToPaymentTable(response);
                            // break;
                            // case 7:
                            // addToTripTable(response);
                            // break;
                            // default:
                            // AckProcessId(processList.get(count));
                            // break;
                            // }

                            if (activit.equals("ManageResources") || activit.equals("TrackingModule")) {
                                switch (tableId) {
                                    case 1:
                                        AddToVehicleTable(response);
                                        break;
                                    case 2:
                                        AddToEmployeeTable(response);
                                        break;
                                    default:
                                        break;
                                }

                            } else if (activit.equals("TripModule")) {
                                switch (tableId) {
                                    case 3:
                                        addToLocationTable(response);
                                        break;
                                    case 7:
                                        //addToTripTable(response);
                                        break;
                                    default:
                                        break;
                                }

                            } else if (activit.equals("FuelModule")) {
                                switch (tableId) {
                                    case 1:
                                        AddToVehicleTable(response);
                                        break;
                                    case 2:
                                        AddToEmployeeTable(response);
                                        break;
                                    case 4:
                                        addToFuelTable(response);
                                        break;
                                    default:
                                        break;

                                }
                            } else if (activit.equals("AdvanceModule")) {
                                switch (tableId) {
                                    case 1:
                                        AddToVehicleTable(response);
                                        break;
                                    case 2:
                                        AddToEmployeeTable(response);
                                        break;
                                    case 5:
                                        addToAdvance(response);
                                        break;
                                    default:
                                        break;

                                }

                            } else if (activit.equals("PaymentModule")) {
                                switch (tableId) {
                                    case 1:
                                        AddToVehicleTable(response);
                                        break;
                                    case 6:
                                        addToPaymentTable(response);
                                        break;
                                    default:
                                        break;

                                }

                            }
                            //    SyncStart(++count);
                        } else if (status.equals("data does not exist")) {
                            SyncStart(++count);
                        } else {
                            Log.i("invalid processid", processList.get(count));
                            SyncStart(++count);
                        }
                        // }
                    } catch (Exception e) {
                        ExceptionMessage.exceptionLog(getApplicationContext(),
                                this.getClass().toString() + " "
                                        + "[SyncStart]", e.toString());

                    }
            } catch (InterruptedException e) {
                ExceptionMessage.exceptionLog(getApplicationContext(), this
                                .getClass().toString() + " " + "[SyncStart]",
                        e.toString());
            } catch (ExecutionException e) {
                ExceptionMessage.exceptionLog(getApplicationContext(), this
                                .getClass().toString() + " " + "[SyncStart]",
                        e.toString());
            }
        } else if (count == processList.size()) {
            // finish();
        }

    }

    private void AddToEmployeeTable(String response) {

        try {

            Cursor EmployeeDetails;
            int key = 0;
            JSONObject jsonResponse = new JSONObject(response);
            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(this);
            db.open();
            int a = 1;

            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String Type = c.getString("employee_type");
                String Ename = c.getString("employee_name").toUpperCase(Locale.getDefault());
                String Ephoto = c.getString("employee_photo");
                receipt = StringToBitMap(Ephoto);
                String ELic = c.getString("licence_no");
                String E_pNo = c.getString("phone_no");
                String Employee_Address = c.getString("address");
                String EId = c.getString("employee_id");
                String Commission =String.valueOf(c.getInt("commission"));
                String salary =String.valueOf(c.getInt("salary"));
                String check = db.checkCleanerTableforDataExist(Ename);
                String check1 = db.checkDrvierTableforDataExist(Ename);
                if (check.equals("NOT EXIST") && check1.equals("NOT EXIST")) {
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
                } else if (!check.equals("NOT EXIST") || !check1.equals("NOT EXIST")) {
                    EmployeeDetails = db.getEmployeeTableDetails(DBAdapter.getEmployeeDetails(),
                            EId);
                    if (EmployeeDetails.moveToFirst()) {
                        if (EmployeeDetails.getString(EmployeeDetails
                                .getColumnIndex(DBAdapter.getKeyName())).equals(Ename) &&
                                EmployeeDetails.getString(EmployeeDetails
                                        .getColumnIndex(DBAdapter.getKeyManagetype())).equals(Type) &&
                                EmployeeDetails.getString(EmployeeDetails
                                        .getColumnIndex(DBAdapter.getKeyPhNo())).equals(E_pNo) &&
                                EmployeeDetails.getString(EmployeeDetails
                                        .getColumnIndex(DBAdapter.getKeyAddr())).equals(Employee_Address) &&
                                EmployeeDetails.getString(EmployeeDetails
                                        .getColumnIndex(DBAdapter.getKeyLicNo())).equals(ELic) &&
                                EmployeeDetails.getString(EmployeeDetails
                                        .getColumnIndex(DBAdapter.getKeyEmployeeId())).equals(EId) &&
                                EmployeeDetails.getString(EmployeeDetails
                                        .getColumnIndex(DBAdapter.getKeySalary())).equals(salary) &&
                                EmployeeDetails.getString(EmployeeDetails
                                        .getColumnIndex(DBAdapter.getKeyCommission())).equals(Commission)) {
                            db.deletePerticularrow(DBAdapter.getEmployeeDetails(),
                                    DBAdapter.getKeyEmployeeId(), EId);
                        } else {
                            ContentValues cv1 = new ContentValues();
                            cv1.put(DBAdapter.getKeyName(), Ename);
                            cv1.put(DBAdapter.getKeyManagetype(), Type);
                            cv1.put(DBAdapter.getKeyPhNo(), E_pNo);
                            cv1.put(DBAdapter.getKeyAddr(), Employee_Address);
                            cv1.put(DBAdapter.getKeyPhoto(), receipt);
                            cv1.put(DBAdapter.getKeyLicNo(), ELic);
                            cv1.put(DBAdapter.getKeyEmployeeId(), EId);
                            cv1.put(DBAdapter.getKeySalary(), salary);
                            cv1.put(DBAdapter.getKeyCommission(), Commission);
                            long No = db.updateContact(DBAdapter.getEmployeeDetails(),
                                    cv1, EId);

                        }
                        EmployeeDetails.moveToNext();
                        EmployeeDetails = null;
                        ++a;
                    }
                    //    EmployeeDetails.close();
                } else {
                    ++a;
                }

                key++;
            }

            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(this, 1);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString() + " " + "[AddToEmployeeTable]",
                    e.toString());
        }
    }

    // Data For Vehicle Table
    private void AddToVehicleTable(String response) {
        try {
            Cursor VehicleDetails;
            int a = 1;
            JSONObject jsonResponse = new JSONObject(response);
            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(this);
            db.open();

            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String vno = c.getString("vehicle_no").toUpperCase(Locale.getDefault());;
                String Type = c.getString("vehicle_type");
                String VehNo = String.valueOf(c.getInt("std_mileage"));
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
                } else if (!db.checkvehTforDataExist(vno).equals("NOT EXIST")) {
                    VehicleDetails = db.getVehicleEntry(vno);
                    if (VehicleDetails.moveToFirst()) {
                        String mi = VehicleDetails.getString(VehicleDetails
                                .getColumnIndex(DBAdapter.getKeyVehMileage()));
                        if (VehicleDetails.getString(VehicleDetails
                                .getColumnIndex(DBAdapter.getKeyVehicleNo())).equals(vno) &&
                                VehicleDetails.getString(
                                        VehicleDetails.getColumnIndex(DBAdapter.getKeyVehType())).equals(Type) &&
                                VehicleDetails.getString(VehicleDetails
                                        .getColumnIndex(DBAdapter.getKeyVehMileage())).equals(VehNo) &&
                                VehicleDetails.getString(VehicleDetails
                                        .getColumnIndex(DBAdapter.getKeyImei())).equals(imei) &&
                                VehicleDetails.getString(VehicleDetails
                                        .getColumnIndex(DBAdapter.getKeyVehPhNo())).equals(pno)) {

//                            db.deletePerticularrow(DBAdapter.getVehicleDetails(),
//                                    DBAdapter.getKeyVehicleNo(), vno);
                           db.deleteVehicle(DBAdapter.getVehicleDetails(),vno);
                        } else {

                            ContentValues cv1 = new ContentValues();
                            cv1.put(DBAdapter.getKeyVehicleNo(), vno);
                            cv1.put(DBAdapter.getKeyVehType(), Type);
                            cv1.put(DBAdapter.getKeyVehMileage(), String.valueOf(VehNo));
                            cv1.put(DBAdapter.getKeyImei(), imei);
                            cv1.put(DBAdapter.getKeyVehPhNo(), pno);
                            long No = db.updateVehicleContact(DBAdapter.getVehicleDetails(),
                                    cv1, vno);
                        }

                        VehicleDetails.moveToNext();
                        VehicleDetails = null;
                        ++a;
                    }
                    //  VehicleDetails.close();
                } else {
                    //	Cursor data=db.getRow(vno);
                    ++a;
                }
            }

            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(this, 1);
                String res = send.execute("26", "ack", processList.get(count))
                        .get();
                SyncStart(++count);

            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString() + " " + "[AddToVehicleTable]",
                    e.toString());

        }
    }

    private void addToLocationTable(String response) {
        try {
            Cursor LocationDetails;
            JSONObject jsonResponse = new JSONObject(response);
            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(this);
            db.open();
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String Lname = c.getString("location_name");
                String Type = c.getString("location_type");
                String Lat = c.getString("latitude");
                String Long = c.getString("longitude");
                //   String Dist = c.getString("Distance");
//                String[] DistPart=Dist.split("\\.");
//                if(DistPart[1].equals("0") || DistPart[1].equals("00")){
//                    Dist=DistPart[0];
//                }

                //           String Amount = c.getString("Amount");
//                String[] AmountPart=Amount.split("\\.");
//                if(AmountPart[1].equals("0") || AmountPart[1].equals("00")){
//                    Amount=AmountPart[0];
//                }

                String check = db.checkLocationTableforDataExist(Lname);
                if (check.equals("NOT EXIST")) {
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
                } else if (!check.equals("NOT EXIST")) {
                    String b, d;
                    LocationDetails = db.getLocationEntry(Lname);
                    if (LocationDetails.moveToFirst()) {

                        if (LocationDetails.getString(LocationDetails
                                .getColumnIndex(DBAdapter.getKeyLocName())).equals(Lname) &&
                                LocationDetails.getString(
                                        LocationDetails.getColumnIndex(DBAdapter.getKeyLongitude())).equals(Long) &&
                                LocationDetails.getString(LocationDetails
                                        .getColumnIndex(DBAdapter.getKeyLatitude())).equals(Lat) &&

                                LocationDetails.getString(LocationDetails
                                        .getColumnIndex(DBAdapter.getKeyLocationtype())).equals(Type)) {

                            db.deletePerticularrow(DBAdapter.getLocation(),
                                    DBAdapter.getKeyId(), check);
                            //    db.deleteLocation(DBAdapter.getLocation(),check);

                        } else {
                            ContentValues cv1 = new ContentValues();
                            cv1.put(DBAdapter.getKeyLocName(), Lname);
                            cv1.put(DBAdapter.getKeyLongitude(), Long);
                            cv1.put(DBAdapter.getKeyLatitude(), Lat);
//                            cv1.put(DBAdapter.getKeyDistance(), Dist);
//                            cv1.put(DBAdapter.getKeyAmount(), Amount);
                            cv1.put(DBAdapter.getKeyLocationtype(), Type);
//                            long No = db.updateContact(DBAdapter.getLocation(),
//                                    cv1, Lname);
                            long No = db.updateDest(DBAdapter.getLocation(),
                                    cv1, Lname);
                        }
                        LocationDetails.moveToNext();
                        LocationDetails = null;
                        ++a;
                    }
                    // LocationDetails.close();
                } else {
                    ++a;
                }
            }

            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(this, 1);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToLocationTable]", e.toString());
        }
    }

    private void addToFuelTable(String response) {

        try {
            Cursor FuelDetails;
            JSONObject jsonResponse = new JSONObject(response);
            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(this);
            db.open();

            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String Date1 = c.getString("fuel_filled_date");
                String Date = Date1.substring(0, 10);
                String VNo = c.getString("vehicle_no").toUpperCase(Locale.getDefault());;
                String ename = c.getString("employee_name");
                String Smtr = String.valueOf(c.getInt("km_in_speedometer"));
                String Fuel = String.valueOf(c.getInt("fuel_volume"));
                String rowId = String.valueOf(c.getInt("fuel_id"));


                String check = db.checkFuelTableforDataExist(rowId);
                if (check.equals("NOT EXIST")) {
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
                } else if (!check.equals("NOT EXIST")) {

//                    db.deletePerticularrow(DBAdapter.getFuelDetails(),
//                               DBAdapter.getKeyFuelRowId(), rowId);
                    FuelDetails = db.getFuelEntry(rowId);
                    if (FuelDetails.moveToFirst()) {
                        if (FuelDetails.getString(FuelDetails
                                .getColumnIndex(DBAdapter.getKeyMdate())).equals(Date) &&
                                FuelDetails.getString(
                                        FuelDetails.getColumnIndex(DBAdapter.getKeyMvehicle())).equals(VNo) &&
                                FuelDetails.getString(FuelDetails
                                        .getColumnIndex(DBAdapter.getKeyMdriver())).equals(ename) &&
                                FuelDetails.getString(FuelDetails
                                        .getColumnIndex(DBAdapter.getKeySpeedometer())).equals(Smtr) &&
                                FuelDetails.getString(FuelDetails
                                        .getColumnIndex(DBAdapter.getKeyFuel())).equals(Fuel) &&
                                FuelDetails.getString(FuelDetails
                                        .getColumnIndex(DBAdapter.getKeyFuelRowId())).equals(rowId)) {

                            db.deletePerticularrow(DBAdapter.getFuelDetails(),
                                    DBAdapter.getKeyFuelRowId(), rowId);

                        } else {
//                            ContentValues cv1 = new ContentValues();
//                            cv1.put(DBAdapter.getKeyMdate(), Date);
//                            cv1.put(DBAdapter.getKeyMvehicle(), VNo);
//                            cv1.put(DBAdapter.getKeyMdriver(), ename);
//                            cv1.put(DBAdapter.getKeySpeedometer(), Smtr);
//                            cv1.put(DBAdapter.getKeyFuel(), Fuel);
//                            cv1.put(DBAdapter.getKeyFuelRowId(), rowId);
//                            long No = db.updateContact(DBAdapter.getFuelDetails(),
//                                    cv1, rowId);
                        }
                        FuelDetails.moveToNext();
                        FuelDetails = null;
                        ++a;
                    }
                } else {
                    ++a;
                }
            }

            db.close();
            if (a == table1.length() - 1) {

                SendToWebService send = new SendToWebService(this, 1);
                String res = send.execute("26", "Ack", processList.get(count)).get();
                if (res != null) {

                }
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToFuelTable]", e.toString());
        }

    }

    private void addToAdvance(String response) {

        if (response != null)
            try {
                Cursor AdvanceDetails;
                JSONObject jsonResponse = new JSONObject(response);

                String jsonData = jsonResponse.getString("d");
                JSONArray table1 = new JSONArray(jsonData);

                DBAdapter db = new DBAdapter(this);
                db.open();
                int a = 1;
                for (int i = 1; i < table1.length() - 1; i++) {

                    JSONObject c = table1.getJSONObject(i);
                    String A_Date = c.getString("advance_date");
                    String date = A_Date.substring(0, 10);
                    String Type = c.getString("advance_type");
                    String VehNo = c.getString("vehicle_no").toUpperCase(Locale.getDefault());;
                    String ename = c.getString("employee_name");
                    String VNo = c.getString("bill_no");
                    Double amount = c.getDouble("amount");
                    String Receipt = c.getString("bill_image");
//                    if (!Receipt.equals("")) {
//                        receipt = StringToBitMap(Receipt);
//                    }
//                    else {
//                        Resources res = getResources();
//                        Drawable drawable = res.getDrawable(R.drawable.no_image);
//                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                        receipt = stream.toByteArray();
//                    }


                    String check = db.checkAdvanceTableforDataExist(VNo);

                    if (check.equals("NOT EXIST")) {
                        ContentValues cv = new ContentValues();
                        cv.put(DBAdapter.getKeyDate(), date);
                        cv.put(DBAdapter.getKeyVoucherNo(), VNo);
                        cv.put(DBAdapter.getKeyAmount(), amount);
                        cv.put(DBAdapter.getKeyName(), ename);
                        cv.put(DBAdapter.getKeyVehicleNo(), VehNo);
                        cv.put(DBAdapter.getKeyAdvanceType(), Type);
                        cv.put(DBAdapter.getKeyReceipt(), receipt);
                        long No = db.insertContact(
                                DBAdapter.getAdvanceDetails(), cv);
                        if (No != -1)
                            ++a;
                    } else if (!check.equals("NOT EXIST")) {

                        AdvanceDetails = db.getAdvanceEntry(DBAdapter.getAdvanceDetails(), VNo);

                        if (AdvanceDetails.moveToFirst()) {
                            if (AdvanceDetails.getString(AdvanceDetails
                                    .getColumnIndex(DBAdapter.getKeyDate())).equals(date) &&
                                    AdvanceDetails.getString(AdvanceDetails
                                            .getColumnIndex(DBAdapter.getKeyVoucherNo())).equals(VNo) &&
                                    AdvanceDetails.getString(AdvanceDetails
                                            .getColumnIndex(DBAdapter.getKeyAmount())).equals(amount) &&
                                    AdvanceDetails.getString(
                                            AdvanceDetails.getColumnIndex(DBAdapter.getKeyVehicleNo())).equals(VehNo) &&
                                    AdvanceDetails.getString(AdvanceDetails
                                            .getColumnIndex(DBAdapter.getKeyName())).equals(ename) &&
                                    AdvanceDetails.getString(AdvanceDetails
                                            .getColumnIndex(DBAdapter.getKeyAdvanceType())).equals(Type)) {

                                db.deletePerticularrow(DBAdapter.getAdvanceDetails(),
                                        DBAdapter.getKeyVoucherNo(), VNo);

                            } else {
                                db.deletePerticularrow(DBAdapter.getAdvanceDetails(),
                                        DBAdapter.getKeyVoucherNo(), VNo);

//                                ContentValues cv1 = new ContentValues();
//                                cv1.put(DBAdapter.getKeyDate(), date);
//                                cv1.put(DBAdapter.getKeyVoucherNo(), VNo);
//                                cv1.put(DBAdapter.getKeyAmount(), amount);
//                                cv1.put(DBAdapter.getKeyName(), ename);
//                                cv1.put(DBAdapter.getKeyVehicleNo(), VehNo);
//                                cv1.put(DBAdapter.getKeyAdvanceType(), Type);
//                                long No = db.updateContact(DBAdapter.getAdvanceDetails(),
//                                        cv1, VNo);
                            }
                            AdvanceDetails.moveToNext();
                            AdvanceDetails = null;
                            ++a;
                        }
                    } else {
                        ++a;
                    }
                }


                db.close();
                if (a == table1.length() - 1) {
                    SendToWebService send = new SendToWebService(this, 1);
                    String res = send.execute("26", "Ack",
                            processList.get(count)).get();
                    if (res != null) {

                    }
                    SyncStart(++count);
                }

            } catch (Exception e) {
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[addToAdvance]", e.toString());

            }

    }

    private void addToPaymentTable(String response) {

        try {
            Cursor PaymentDetails;

            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(this);
            db.open();
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String P_Date = c.getString("payment_date");
                String[] parts = P_Date.split("T");
                P_Date = parts[0];
                String comm = String.valueOf(c.getInt("commission"));
                String ename = c.getString("employee_name").toUpperCase(Locale.getDefault());;
                String vNo = c.getString("bill_no");
                String Amount = c.getString("amount");
                String Deduction = c.getString("amount_deduction");
                String Mdedu = c.getString("mileage_deduction");
                String pType = c.getString("payment_type");
                String Receipt = c.getString("bill_image");
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


                String check = db.checkPaymentTableforDataExist(vNo);
                if (check.equals("NOT EXIST")) {
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyDate(), P_Date);
                    cv.put(DBAdapter.getKeyPaymentType(), pType);
                    cv.put(DBAdapter.getKeyCommission(), comm);
                    cv.put(DBAdapter.getKeyName(), ename);
                    cv.put(DBAdapter.getKeyVoucherNo(), vNo);
                    cv.put(DBAdapter.getKeyAmount(), Amount);
                    cv.put(DBAdapter.getKeyDeduction(), Deduction);
                    cv.put(DBAdapter.getKeyMileagededuction(), Mdedu);
                    cv.put(DBAdapter.getKeyReceipt(), receipt);
                    long No = db.insertContact(DBAdapter.getPaymentDetails(),
                            cv);
                    if (No != -1)
                        ++a;
                } else if (!check.equals("NOT EXIST")) {

//                    db.deletePerticularrow(DBAdapter.getAdvanceDetails(),
//                               DBAdapter.getKeyVoucherNo(), vNo);
                    PaymentDetails = db.getAdvanceEntry(DBAdapter.getPaymentDetails(), vNo);
                    if (PaymentDetails.moveToFirst()) {
                        if (PaymentDetails.getString(PaymentDetails
                                .getColumnIndex(DBAdapter.getKeyDate())).equals(P_Date) &&
                                PaymentDetails.getString(
                                        PaymentDetails.getColumnIndex(DBAdapter.getKeyPaymentType())).equals(pType) &&
                                PaymentDetails.getString(PaymentDetails
                                        .getColumnIndex(DBAdapter.getKeyName())).equals(ename) &&
                                PaymentDetails.getString(PaymentDetails
                                        .getColumnIndex(DBAdapter.getKeyVoucherNo())).equals(vNo) &&
                                PaymentDetails.getString(PaymentDetails
                                        .getColumnIndex(DBAdapter.getKeyCommission())).equals(comm) &&
                                PaymentDetails.getString(PaymentDetails
                                        .getColumnIndex(DBAdapter.getKeyDeduction())).equals(Deduction) &&
                                PaymentDetails.getString(PaymentDetails
                                        .getColumnIndex(DBAdapter.getKeyMileagededuction())).equals(Mdedu) &&
                                PaymentDetails.getString(PaymentDetails
                                        .getColumnIndex(DBAdapter.getKeyAmount())).equals(Amount)) {

//                            db.deletePerticularrow(DBAdapter.getPaymentDetails(),
//                                    DBAdapter.getKeyVoucherNo(), vNo);
                            db.deletePayment(DBAdapter.getPaymentDetails(),vNo);

                        } else {
//                            db.deletePerticularrow(DBAdapter.getPaymentDetails(),
//                                    DBAdapter.getKeyVoucherNo(), vNo);
                            db.deletePayment(DBAdapter.getPaymentDetails(), vNo);
//                          ContentValues cv1 = new ContentValues();
//                          cv1.put(DBAdapter.getKeyDate(), P_Date);
//                          cv1.put(DBAdapter.getKeyPaymentType(), pType);
//                          cv1.put(DBAdapter.getKeyName(), ename);
//                          cv1.put(DBAdapter.getKeyVoucherNo(), vNo);
//                          cv1.put(DBAdapter.getKeyCommission(), comm);
//                          cv1.put(DBAdapter.getKeyDeduction(), Deduction);
//                          cv1.put(DBAdapter.getKeyMileagededuction(), Mdedu);
//                          cv1.put(DBAdapter.getKeyAmount(), Amount);
//                          long No = db.updateContact(DBAdapter.getPaymentDetails(),
//                                  cv1, vNo);
                        }
                        PaymentDetails.moveToNext();
                        PaymentDetails = null;
                        ++a;
                    }
                } else {
                    ++a;
                }

            }

            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(this, 1);
                String res = send.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToPaymentTable]", e.toString());
        }
    }


    public void AckProcessId(String process) {
        SendToWebService send = new SendToWebService(this, 1);
        try {
            String res = send.execute("26", "Ack", process).get();
        } catch (InterruptedException e) {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString() + " " + "[AckProcessId]",
                    e.toString());
        } catch (ExecutionException e) {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString() + " " + "[AcProcessId]",
                    e.toString());
        }
        SyncStart(++count);
    }

    public byte[] StringToBitMap(String encodedString) {
        try {
            encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            stream = new ByteArrayOutputStream();
            if(bitmap!=null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            return byteArray;
        } catch (Exception e)
        {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString() + " " + "[StringToBitMap()]",
                    e.toString());
            e.getMessage();
            return null;
        }
    }

}
