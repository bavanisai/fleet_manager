package com.example.anand_roadwayss;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;;

import com.example.Interface.IInstallation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Developed By : pravitha
 * Created by MKSoft-02 on 31-03-2015.
 */
public class SynchronizeServerDataService extends IntentService {
    public SynchronizeServerDataService() {
        super("SynchronizeServerDataService");
    }

    ArrayList<String> processList = new ArrayList<String>();
    int count = 0;
    DBAdapter db;
    String name = "", email = "", phone = "", pin = "", strEmpType = "", productKey = "";
    byte[] byteArray, receipt;

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            db = new DBAdapter(this);

            updateToValue();

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
                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = mngr.getDeviceId();
                SendToWebService send = new SendToWebService(
                        this, 1);
                String response = send.execute("9", "RegisterAnApplication",
                        registrationAuthKey, name,
                        strEmpType, email, phone, pin,
                        IMEI, productKey).get();

                if (response.equals("No Internet")) {
                } else if (response.equals("There was an error processing")) {
                } else if (response.contains("refused")) {
                } else if (response.contains("java.net.SocketTimeoutException")) {
                } else if (response.contains("The remote server returned an error")) {
                } else {

                    jsonParsing(response);
                }

            } catch (Exception e)
            {
                ExceptionMessage.exceptionLog(this, this
                                .getClass().toString() + " " + "[onHandleIntent()]",
                        e.toString());
            }


        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this
                            .getClass().toString() + " " + "[onHandleIntent()]",
                    e.toString());
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
//                    db.open();
//                    db.deleteAllRowsOfTable(DBAdapter.getTableRegistration());
//                    db.close();
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

                    SendToWebService send = new SendToWebService(
                            this, 1);
                    String response1 = send.execute("24", "SyncCheckAfterInstall").get();

                    if (response1.equals("No Internet")) {
                    } else if (response1.equals("There was an error processing")) {
                    } else if ((response1.contains("refused") || (response.contains("timed out")))) {
                    } else if (response1.contains("java.net.SocketTimeoutException")) {
                    } else if (response1.contains("The remote server returned an error")) {
                    } else {
                        jsonParser(response1);
                    }
                } else {

                }
            } catch (Exception e) {


                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[jsonParsing]" + " " + response, e.toString());
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

                } else {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[jsonParser]", response);
                }

            } catch (Exception e) {
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[jsonParser]", e.toString());
            }
    }

    private void SyncStart(int i) {
        SendToWebService send = new SendToWebService(this, 1);
        if (count < processList.size()) {
            try {
                String response = send.execute("25", "DataSync", processList.get(count)).get();
                onSynchronization(response);
            } catch (InterruptedException e)
            {
                ExceptionMessage.exceptionLog(this, this
                                .getClass().toString() + " " + "[SyncStart()-InterruptedException]",
                        e.toString());
                e.printStackTrace();
            } catch (ExecutionException e) {
                ExceptionMessage.exceptionLog(this, this
                                .getClass().toString() + " " + "[syncStart()-ExecutionException]",
                        e.toString());
                e.printStackTrace();
            }
        } else if (count == processList.size()) {
            SharedPreferences login = getSharedPreferences("testapp",
                    MODE_PRIVATE);
            SharedPreferences.Editor edit = login.edit();
            edit.putString("register", "true");
            edit.commit();

            updateToValue();
//            Intent intent_welcome = new Intent(getApplicationContext(),
//                    Welcome.class);
//            startActivity(intent_welcome);

        }

    }

    public void onSynchronization(String response) {

        if (response != null)
            try {
//                db.open();
//                db.onTerminate();
//                db.close();
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
                            "Error occured In DataSync For This Process_Id:"
                                    + processList.get(count));
                    Log.i("Invalid ProcessId", processList.get(count));
                    SyncStart(++count);
                }

            } catch (Exception e) {


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

                DBAdapter db = new DBAdapter(this);
                db.open();
                // Cursor adv=db.retrieveAllDataFrom(DBAdapter.getEmployeeDetails());

//                try {
//                 boolean delete= db.deleteAllRowsOfTable(DBAdapter.getAdvanceDetails());
//                }
//                catch(Exception e){
//                    ExceptionMessage.exceptionLog(this, this.getClass().toString()
//                            + " " + "[addToAdvance]", e.toString());
//                }
                int a = 1;
                for (int i = 1; i < table1.length() - 1; i++) {


                    JSONObject c = table1.getJSONObject(i);
                    String A_Date = c.getString("advance_date");
                    String date = A_Date.substring(0, 10);
                    String Type = c.getString("advance_type");
                    String VehNo = c.getString("vehicle_no").toUpperCase(Locale.getDefault());
                    String ename = c.getString("employee_name");
                    String VNo = c.getString("bill_no");
                    Double amount = c.getDouble("amount");
                    String Receipt = c.getString("bill_image");
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
                    cv.put(DBAdapter.getKeyReceipt(), receipt);
                    long No = db.insertContact(DBAdapter.getAdvanceDetails(),
                            cv);
                    if (No != -1)
                        ++a;
                }
                db.close();
                if (a == table1.length() - 1) {
                    SendToWebService send = new SendToWebService(this, 1);
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
            DBAdapter db = new DBAdapter(this);
            db.open();
            //  Cursor adv=db.retrieveAllDataFrom(DBAdapter.getVehicleDetails());

//            try {
//             boolean delete= db.deleteAllRowsOfTable(DBAdapter.getVehicleDetails());
//            }
//            catch(Exception e){
//                ExceptionMessage.exceptionLog(this, this.getClass().toString()
//                        + " " + "[addToVehicleTable]", e.toString());
//            }
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
                SendToWebService send = new SendToWebService(this, 1);
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
            DBAdapter db = new DBAdapter(this);
            db.open();
            //    Cursor adv=db.retrieveAllDataFrom(DBAdapter.getPaymentDetails());

//            try {
//               boolean delete= db.deleteAllRowsOfTable(DBAdapter.getPaymentDetails());
//            }
//            catch(Exception e){
//                ExceptionMessage.exceptionLog(this, this.getClass().toString()
//                        + " " + "[addToAdvance]", e.toString());
//            }
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

                long No = db.insertContact(DBAdapter.getPaymentDetails(), cv);
                if (No != -1)
                    ++a;
            }
            db.close();
            if (a == table1.length() - 1) {
                SendToWebService send = new SendToWebService(this, 1);
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
            DBAdapter db = new DBAdapter(this);
            db.open();
            //    Cursor adv=db.retrieveAllDataFrom(DBAdapter.getLocation());

//            try {
//              boolean delete=db.deleteAllRowsOfTable(DBAdapter.getLocation());
//            }
//            catch(Exception e){
//                ExceptionMessage.exceptionLog(this, this.getClass().toString()
//                        + " " + "[addToLocationTable]", e.toString());
//            }
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
                SendToWebService send = new SendToWebService(this, 1);
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
            DBAdapter db = new DBAdapter(this);
            db.open();
            //    Cursor adv=db.retrieveAllDataFrom(DBAdapter.getEmployeeDetails());

//            try {
//              boolean delete=db.deleteAllRowsOfTable(DBAdapter.getEmployeeDetails());
//            }
//            catch(Exception e){
//                ExceptionMessage.exceptionLog(this, this.getClass().toString()
//                        + " " + "[addToEmployeeTable]", e.toString());
//            }
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
                SendToWebService send = new SendToWebService(this, 1);
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
            DBAdapter db = new DBAdapter(this);
            db.open();
            //  Cursor adv=db.retrieveAllDataFrom(DBAdapter.getFuelDetails());

//            try {
//              boolean delete=db.deleteAllRowsOfTable(DBAdapter.getFuelDetails());
//            }
//            catch(Exception e){
//                ExceptionMessage.exceptionLog(this, this.getClass().toString()
//                        + " " + "[addToFuelTable]", e.toString());
//            }
            int a = 1;
            for (int i = 1; i < table1.length() - 1; i++) {

                JSONObject c = table1.getJSONObject(i);
                String Date1 = c.getString("fuel_filled_date");
                String Date = Date1.substring(0, 10);
                String VNo = c.getString("vehicle_no").toUpperCase(Locale.getDefault());
                String ename = c.getString("employee_name");
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

                SendToWebService send = new SendToWebService(this, 1);
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

    public void updateToNil() {
        SharedPreferences login = getSharedPreferences("updateApp",
                MODE_PRIVATE);
        SharedPreferences.Editor edit = login.edit();
        edit.putString("update", "nil");
        edit.commit();
    }

    public void updateToValue() {
        SharedPreferences login = getSharedPreferences("updateApp",
                MODE_PRIVATE);
        SharedPreferences.Editor edit = login.edit();
        edit.putString("update", "true");
        edit.commit();
    }

    public byte[] StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            return byteArray;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
