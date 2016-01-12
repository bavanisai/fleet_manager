package com.example.anand_roadwayss;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends ActionBarActivity {
    Button regBtnRegister;
    EditText regEtName, regEtEmail, regEtPhno, regEtPin;
    ImageButton regTvPinNumberShow;
    Spinner empType;
    ArrayList<String> UserType = new ArrayList<>();
    String strEmpType = null;
    String ProductKey;
    int count = 0;
    ArrayList<String> processList = new ArrayList<>();
    DBAdapter db;
    String adress = new IpAddress().getIpAddress();
    boolean value = false;
    ProgressDialog proDialog;
    SendToWebService send1;
    //Product Key Inits
    EditText p1, p2, p3, p4, p5, p6;
    ClipboardManager cb;
    String val;
    List<String> wordList;
    String response;
    byte[] byteArray, receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration);

            db = new DBAdapter(getApplicationContext());
            proDialog = new ProgressDialog(RegistrationActivity.this);
            proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            proDialog.setMessage("Please wait...");
            proDialog.setIndeterminate(false);
            proDialog.setProgressNumberFormat(null);
            proDialog.setCancelable(false);
            proDialog.setProgress(0);

            bindData();
            UserType.add("SELECT USER TYPE");
            UserType.add("MANAGER");
            UserType.add("DEMO");
            ArrayAdapter<String> UserList = new ArrayAdapter<>(RegistrationActivity.this,
                    android.R.layout.simple_spinner_item, UserType);
            UserList.getCount();
            UserList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            empType.setAdapter(UserList);

            // Product key EditText-1 Focus changing
            p1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (p1.getText().toString().length() == 4)
                        p2.requestFocus();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // Product key EditText-2 Focus changing
            p2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (p2.getText().toString().length() == 4)
                        p3.requestFocus();

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // Product key EditText-3 Focus changing
            p3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (p3.getText().toString().length() == 4)
                        p4.requestFocus();

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            // Product key EditText-4 Focus changing
            p4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (p4.getText().toString().length() == 4)
                        p5.requestFocus();

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // Product key EditText-5 Focus changing
            p5.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (p5.getText().toString().length() == 4)
                        p6.requestFocus();

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // Registration Name Validation
            regEtName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (regEtName.getText().toString() != null | !regEtName.getText().toString().trim().equals("")) {
                        for (int i = 0; i < regEtName.getText().toString().length(); i++) {
                            if (!Character.isLetter(regEtName.getText().toString().charAt(i))) {
                                regEtName.setError("Name should contain only Letters! ");
                            }
                        }
                        if (regEtName.getText().toString().length() < 3)
                            regEtName.setError("Name should contain minimum 3 letters");

                        else if (regEtName.getText().toString().length() > 30)
                            regEtName.setError("Name cannot be more than 30 letters");
                    }
                }
            });

            // Registration Phone Number Validation
            regEtPhno.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (regEtPhno.getText().toString().length() > 10) {
                        regEtPhno.setError("Mobile number should not be more than 10 Digits");
                    } else if (regEtPhno.getText().toString().length() < 10) {
                        regEtPhno.setError("Mobile number cannot be less than 10 Digits");
                    } else if (regEtPhno.getText().toString() != null | !regEtPhno.getText().toString().trim().equals("")) {
                        for (int i = 0; i < regEtPhno.getText().toString().length(); i++) {
                            if (!Character.isDigit(regEtPhno.getText().toString().charAt(i))) {
                                regEtPhno.setError("Mobile number should contain only Digits!");
                            }
                        }
                    }
                }
            });


            // Registration PIN Validation
            regEtPin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (regEtPin.getText().toString().length() > 4) {
                        regEtPin.setError("PIN should not be more than 4 Digits");
                    } else if (regEtPin.getText().toString() != null | !regEtPin.getText().toString().trim().equals("")) {
                        for (int i = 0; i < regEtPin.getText().toString().length(); i++) {
                            if (!Character.isDigit(regEtPin.getText().toString().charAt(i))) {
                                regEtPin.setError("PIN should contain only Digits!");
                            }
                        }
                    }
                }
            });


            // Registration Product key Validation and
            // If product key is already there in clipboard paste key onCreate

            p1.setFocusableInTouchMode(true);
            p1.setFocusable(true);
            p1.requestFocus();


            cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);  // permission to access clipboard data

            if (cb != null) {
                if (cb.hasPrimaryClip() == true) {
                    ClipData.Item data = cb.getPrimaryClip().getItemAt(0);
                    val = data.getText().toString().trim();
                    if (val.length() == 29) {
                        String[] splitKey = val.split("-");
                        p1.setText(splitKey[0]);
                        p2.setText(splitKey[1]);
                        p3.setText(splitKey[2]);
                        p4.setText(splitKey[3]);
                        p5.setText(splitKey[4]);
                        p6.setText(splitKey[5]);
                        p6.setSelection(4);
                    }
                }
            }


            // on touch of first edit text paste product key

            p1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cb.hasPrimaryClip() == true) {
                        ClipData.Item data = cb.getPrimaryClip().getItemAt(0);
                        val = data.getText().toString().trim();
                        int len = val.length();
                        if (len == 29) {
                            String[] splitKey = val.split("-");
                            p1.setText(splitKey[0]);
                            p2.setText(splitKey[1]);
                            p3.setText(splitKey[2]);
                            p4.setText(splitKey[3]);
                            p5.setText(splitKey[4]);
                            p6.setText(splitKey[5]);
                            p6.setSelection(4);
                        }
                    }
                }
            });


            regTvPinNumberShow.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (regEtPin.getText().toString() != "") {
                        if (!value) {
                            regEtPin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            regTvPinNumberShow.setImageResource(R.drawable.invisibleicon);
                            value = true;
                        } else if (value) {
                            regEtPin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            regTvPinNumberShow.setImageResource(R.drawable.visibleicon);
                            value = false;
                        }
                    } else
                        regEtPin.setError("Enter Pin");
                }
            });


            regBtnRegister.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
//                    if (!regEtName.getText().toString().equals("")
//                            && !regEtEmail.getText().toString().equals("")
//                            && !regEtPhno.getText().toString().equals("")
//                            && !regEtPin.getText().toString().equals("")
//                            && !ProductKey.equals(""))
//                    {
//                        proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                        proDialog.setMessage("Please wait...");
//                        proDialog.setIndeterminate(false);
//                        proDialog.setProgressNumberFormat(null);
//                        proDialog.setProgress(0);
//                        proDialog.show();
//
//                    }


//                        //Email Validation
                    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
                    Matcher m = Pattern.compile(EMAIL_PATTERN).matcher(regEtEmail.getText().toString());
                    if (!m.matches())
                        regEtEmail.setError("Invalid Email-Id");

                    strEmpType = empType.getSelectedItem().toString();
                    String Name = regEtName.getText().toString();
                    String Email = regEtEmail.getText().toString();
                    String Phone = regEtPhno.getText().toString();
                    String Pin = regEtPin.getText().toString();
                    ProductKey = p1.getText().toString() + "-" + p2.getText().toString() + "-" + p3.getText().toString() + "-" + p4.getText().toString() + "-" + p5.getText().toString() + "-" + p6.getText().toString();

                    TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String IMEI = mngr.getDeviceId();

                    if (Pin.length() == 4) {

                        if (Name.equals("") || Email.equals("")
                                || Phone.equals("") || Pin.equals("")
                                || ProductKey.equals("")) {
                            Toast.makeText(getApplicationContext(),
                                    "Fields cannot be empty",
                                    Toast.LENGTH_SHORT).show();
                        } else if (strEmpType.equals("SELECT USER TYPE")) {
                            Toast.makeText(getApplicationContext(),
                                    "Please Select User Type",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if (strEmpType.equals("DEMO")) {

                            SharedPreferences TypeName = getSharedPreferences(
                                    "RegisterName", Context.MODE_PRIVATE);
                            Editor edit = TypeName.edit();
                            edit.putString("Name", strEmpType);
                            edit.commit();

                            send1 = new SendToWebService(
                                    RegistrationActivity.this,
                                    1);
                            try {
                                response = send1.execute("9", "RegisterAnApplication",
                                        "regmnk", "MNK Software",
                                        "DEMO", "mnk@gmail.com", "9898987876", "1111",
                                        "000000000000001", "asdf-ghjk-qwer-tyui-oplk-sgd4").get();

                                if (response != null)
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        String jsonData = jsonResponse.getString("d");
                                        JSONObject d = new JSONObject(jsonData);
                                        if (!(d.isNull("status")) && !(d.isNull("appAuthKey"))
                                                && !(d.isNull("ipAddress"))
                                                && !(d.isNull("portNumber"))) {

//                if(d.getString("status")!=null){
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
                                            Editor editor = AppUrl.edit();
                                            editor.putString("appUrl", appUrl);
                                            editor.putString("AuthKey", appAuthKey);
                                            editor.commit();

                                            db.open();
                                            ContentValues cv = new ContentValues();
                                            cv.put(DBAdapter.getKeyName(), Name);
                                            cv.put(DBAdapter.getRegistrationMail(), Email);
                                            cv.put(DBAdapter.getKeyPhNo(), Phone);
                                            cv.put(DBAdapter.getRegistrationPin(), Pin);
                                            cv.put(DBAdapter.getRegistrationIs(), 0);
                                            cv.put(DBAdapter.getKeyProductKey(), ProductKey);
                                            cv.put(DBAdapter.getKeyUserType(), strEmpType);
                                            cv.put(DBAdapter.getKeyClientname(), clientName);
                                            long id = db.insertContact(DBAdapter.getTableRegistration(), cv);
                                            db.close();

                                            new Thread() {
                                                public void run() {
                                                    try {
                                                        // final IInstallation installed = this;
                                                        send1 = new SendToWebService(RegistrationActivity.this, 1);
                                                        try {
                                                            String res = send1.execute("24", "SyncCheckAfterInstall").get();
                                                            if (res != null)
                                                                try {
                                                                    processList.clear();
                                                                    count = 0;
                                                                    JSONObject jsonResponse1 = new JSONObject(res);

                                                                    String jsonData1 = jsonResponse1.getString("d");

                                                                    if (jsonData1.contains("\"process_id\"")) {
                                                                        JSONObject d1 = new JSONObject(jsonData1);

                                                                        JSONArray listOfValues = d1.getJSONArray("listOfValues");
                                                                        for (int j = 0; j < listOfValues.length(); j++) {
                                                                            String value = listOfValues.getString(j).trim();
                                                                            processList.add(value);
                                                                        }

                                                                        if (processList.size() > 0) {
                                                                            SyncStart(count);
                                                                            proDialog.setProgress(count);
                                                                        }
                                                                    } else if (jsonData1.contains("\"data does not exist\"")) {
                                                                        SharedPreferences login = getSharedPreferences("testapp",
                                                                                MODE_PRIVATE);
                                                                        Editor edit1 = login.edit();
                                                                        edit1.putString("register", "true");
                                                                        edit1.commit();

                                                                        SharedPreferences login1 = getSharedPreferences("updateApp",
                                                                                MODE_PRIVATE);
                                                                        Editor edit2 = login1.edit();
                                                                        edit2.putString("update", "true");
                                                                        edit2.commit();

                                                                        Intent intent_sign = new Intent(getApplicationContext(), SignRegisterActivity.class);
                                                                        startActivity(intent_sign);
                                                                        finish();

                                                                    } else {
                                                                        ExceptionMessage.exceptionLog(RegistrationActivity.this, this.getClass().toString() + " " + "[jsonParser]", response);
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
                                                                    } else if (e.toString().contains(
                                                                            "No Internet")) {
                                                                        Toast.makeText(
                                                                                getApplicationContext(),
                                                                                " PLEASE CONNECT TO INTERNET... ",
                                                                                Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(),
                                                                                " UNABLE TO REGISTER..PLEASE TRY AGAIN",
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                    ExceptionMessage.exceptionLog(RegistrationActivity.this, this.getClass().toString()
                                                                            + " " + "[jsonParser]", e.toString());
                                                                }


                                                        } catch (Exception e) {
                                                            ExceptionMessage.exceptionLog(getApplicationContext(), this
                                                                            .getClass().toString() + " " + "[onHandleIntent]",
                                                                    e.toString());
                                                        }

                                                    } catch (Exception e) {
                                                        Toast.makeText(getApplicationContext(),
                                                                "dialog pblm", Toast.LENGTH_SHORT)
                                                                .show();
                                                    } finally {
                                                        proDialog.dismiss();
                                                    }
                                                }
                                            }.start();


                                        } else {
                                            OneJSONValue one = new OneJSONValue();
                                            String status = one.jsonParsing1(response);
                                            switch (status) {
                                                case "invalid server authKey":
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
                                        } else if (e.toString().contains(
                                                "TimeoutException")) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    " UNABLE TO REGISTER..PLEASE TRY AGAIN",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        ExceptionMessage.exceptionLog(RegistrationActivity.this, this.getClass().toString()
                                                + " " + "[jsonParsing]" + " " + response, e.toString());
                                    }

                            } catch (Exception e) {
                                Toast.makeText(RegistrationActivity.this,
                                        "Try after sometime...",
                                        Toast.LENGTH_SHORT).show();
                                ExceptionMessage
                                        .exceptionLog(
                                                getApplicationContext(),
                                                this.getClass().toString()
                                                        + " "
                                                        + "[regBtnRegister.setOnClickListener]",
                                                e.toString());
                            }


                        } else {
                            proDialog.show();
                            // Shared Preference For Type of User
                            SharedPreferences TypeName = getSharedPreferences(
                                    "RegisterName", Context.MODE_PRIVATE);
                            Editor edit = TypeName.edit();
                            edit.putString("Name", strEmpType);
                            edit.commit();
                            // End

                            String registrationAuthKey = new Constants()
                                    .getRegistrationAuthKey();
                            send1 = new SendToWebService(
                                    RegistrationActivity.this,
                                    1);

                            try {
                                response = send1.execute("9", "RegisterAnApplication",
                                        registrationAuthKey, Name,
                                        strEmpType, Email, Phone, IMEI, Pin,
                                        ProductKey).get();

                                if (response != null)
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        String jsonData = jsonResponse.getString("d");
                                        JSONObject d = new JSONObject(jsonData);
                                        if (!(d.isNull("status")) && !(d.isNull("appAuthKey"))
                                                && !(d.isNull("ipAddress"))
                                                && !(d.isNull("portNumber"))) {

//                if(d.getString("status")!=null){
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
                                            Editor editor = AppUrl.edit();
                                            editor.putString("appUrl", appUrl);
                                            editor.putString("AuthKey", appAuthKey);
                                            editor.commit();

                                            db.open();
                                            ContentValues cv = new ContentValues();
                                            cv.put(DBAdapter.getKeyName(), Name);
                                            cv.put(DBAdapter.getRegistrationMail(), Email);
                                            cv.put(DBAdapter.getKeyPhNo(), Phone);
                                            cv.put(DBAdapter.getRegistrationPin(), Pin);
                                            cv.put(DBAdapter.getRegistrationIs(), 0);
                                            cv.put(DBAdapter.getKeyProductKey(), ProductKey);
                                            cv.put(DBAdapter.getKeyUserType(), strEmpType);
                                            cv.put(DBAdapter.getKeyClientname(), clientName);
                                            long id = db.insertContact(DBAdapter.getTableRegistration(), cv);
                                            db.close();

                                            new Thread() {
                                                public void run() {
                                                    try {
                                                        // final IInstallation installed = this;
                                                        send1 = new SendToWebService(RegistrationActivity.this, 1);
                                                        try {
                                                            String res = send1.execute("24", "SyncCheckAfterInstall").get();
                                                            if (res != null)
                                                                try {
                                                                    processList.clear();
                                                                    count = 0;
                                                                    JSONObject jsonResponse1 = new JSONObject(res);

                                                                    String jsonData1 = jsonResponse1.getString("d");

                                                                    if (jsonData1.contains("\"process_id\"")) {
                                                                        JSONObject d1 = new JSONObject(jsonData1);

                                                                        JSONArray listOfValues = d1.getJSONArray("listOfValues");
                                                                        for (int j = 0; j < listOfValues.length(); j++) {
                                                                            String value = listOfValues.getString(j).trim();
                                                                            processList.add(value);
                                                                        }

                                                                        if (processList.size() > 0) {
                                                                            SyncStart(count);
                                                                            proDialog.setProgress(count);
                                                                        }
                                                                    } else if (jsonData1.contains("\"data does not exist\"")) {
                                                                        SharedPreferences login = getSharedPreferences("testapp",
                                                                                MODE_PRIVATE);
                                                                        Editor edit1 = login.edit();
                                                                        edit1.putString("register", "true");
                                                                        edit1.commit();

                                                                        SharedPreferences login1 = getSharedPreferences("updateApp",
                                                                                MODE_PRIVATE);
                                                                        Editor edit2 = login1.edit();
                                                                        edit2.putString("update", "true");
                                                                        edit2.commit();

                                                                        Intent intent_sign = new Intent(RegistrationActivity.this, SignRegisterActivity.class);
                                                                        startActivity(intent_sign);
                                                                        finish();

                                                                    } else {
                                                                        ExceptionMessage.exceptionLog(RegistrationActivity.this, this.getClass().toString() + " " + "[jsonParser]", response);
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
                                                                    ExceptionMessage.exceptionLog(RegistrationActivity.this, this.getClass().toString()
                                                                            + " " + "[jsonParser]", e.toString());
                                                                }


                                                        } catch (Exception e) {
                                                            ExceptionMessage.exceptionLog(getApplicationContext(), this
                                                                            .getClass().toString() + " " + "[onHandleIntent]",
                                                                    e.toString());
                                                        }

                                                    } catch (Exception e) {
                                                        Toast.makeText(getApplicationContext(),
                                                                "dialog pblm", Toast.LENGTH_SHORT)
                                                                .show();
                                                    } finally {
                                                        proDialog.dismiss();
                                                    }
                                                }
                                            }.start();


                                        } else {
                                            OneJSONValue one = new OneJSONValue();
                                            String status = one.jsonParsing1(response);
                                            switch (status) {
                                                case "invalid server authKey":
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
                                        } else if (e.toString().contains(
                                                "TimeoutException")) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    " YOUR INTERNET CONNECTION IS SLOW UNABLE TO CONTACT SERVER ",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    " UNABLE TO REGISTER..PLEASE TRY AGAIN",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        ExceptionMessage.exceptionLog(RegistrationActivity.this, this.getClass().toString()
                                                + " " + "[jsonParsing]" + " " + response, e.toString());
                                    }

                            } catch (Exception e) {
                                Toast.makeText(RegistrationActivity.this,
                                        "Try after sometime...",
                                        Toast.LENGTH_SHORT).show();
                                ExceptionMessage
                                        .exceptionLog(
                                                getApplicationContext(),
                                                this.getClass().toString()
                                                        + " "
                                                        + "[regBtnRegister.setOnClickListener]",
                                                e.toString());
                            }

                        }

                    } else {
                        regEtPin.setText("");
                        Toast.makeText(getApplicationContext(),
                                "Enter 4 digit Pin",
                                Toast.LENGTH_LONG).show();
                    }
                              }
        catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Fields cannot be empty ",
                                Toast.LENGTH_LONG).show();
                        ExceptionMessage
                                .exceptionLog(
                                        getApplicationContext(),
                                        this.getClass().toString()
                                                + " "
                                                + "[regBtnRegister.setOnClickListener]",
                                        e.toString());
                    }
                }
            });
        } catch (Exception e) {
            ExceptionMessage
                    .exceptionLog(this, this.getClass().toString() + " "
                                    + "[regBtnRegister.setOnClickListener]",
                            e.toString());
        }
    }

    private void bindData() {
        regEtName = (EditText) findViewById(R.id.regEtName);
        regEtEmail = (EditText) findViewById(R.id.regEtEmail);
        regEtPhno = (EditText) findViewById(R.id.regEtPhno);
        regEtPin = (EditText) findViewById(R.id.regEtPinNumber);
        p1 = (EditText) findViewById(R.id.part1);
        p2 = (EditText) findViewById(R.id.part2);
        p3 = (EditText) findViewById(R.id.part3);
        p4 = (EditText) findViewById(R.id.part4);
        p5 = (EditText) findViewById(R.id.part5);
        p6 = (EditText) findViewById(R.id.part6);
        regBtnRegister = (Button) findViewById(R.id.regBtnRegister);
        empType = (Spinner) findViewById(R.id.RegSpinner);
        regTvPinNumberShow = (ImageButton) findViewById(R.id.regTvPinNumberShow);
        regTvPinNumberShow.setImageResource(R.drawable.invisibleicon);
    }


    // /************************************************************************************************
    // NEW CODE FOR SYNCHRONIZATION

    private void SyncStart(int i) {

        //  final Synchronization Sync = this;
        send1 = new SendToWebService(this, 1);

        try {
            if (count < processList.size()) {
                int id = processList.size();
                proDialog.setMax(id);
                proDialog.setProgress(count);
                String response = send1.execute("25", "DataSync", processList.get(i)).get();
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
                                    // addToTripTable(response);
                                    break;
                            }
                        } else if (status.equals("Data does not exist")) {
                            SyncStart(++count);
                            proDialog.setProgress(count);

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

            } else if (count == processList.size()) {
                SharedPreferences login = getSharedPreferences("testapp", MODE_PRIVATE);
                Editor edit = login.edit();
                edit.putString("register", "true");
                edit.putString("checkReg", "true");
                edit.commit();

                SharedPreferences login1 = getSharedPreferences("updateApp",
                        MODE_PRIVATE);
                Editor edit1 = login1.edit();
                edit1.putString("update", "true");
                edit1.commit();

                Intent intent_sign = new Intent(RegistrationActivity.this,
                        SignRegisterActivity.class);
                startActivity(intent_sign);
                finish();

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
    }


    private void addToAdvance(String response) {

        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);

                String jsonData = jsonResponse.getString("d");
                JSONArray table1 = new JSONArray(jsonData);

                DBAdapter db = new DBAdapter(RegistrationActivity.this);
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
                    String Receipt = c.getString("bill_image");

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
                    send1 = new SendToWebService(
                            RegistrationActivity.this, 1);
                    String res = send1.execute("26", "Ack",
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


    private void addToVehicleTable(String response) {

        try {

            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RegistrationActivity.this);
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
                send1 = new SendToWebService(
                        RegistrationActivity.this, 1);
                String res = send1.execute("26", "Ack", processList.get(count))
                        .get();
                SyncStart(++count);

            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToVehicleTable]", e.toString());
        }
    }


    private void addToPaymentTable(String response) {

        try {
            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RegistrationActivity.this);
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
                // receipt=StringToBitMap(Receipt);
                // String ename = db.getEmployeeName(Eid,
                //         DBAdapter.getEmployeeDetails(), "Name");
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
                send1 = new SendToWebService(
                        RegistrationActivity.this, 1);
                String res = send1.execute("26", "Ack", processList.get(count))
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

    private void addToLocationTable(String response) {

        try {

            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RegistrationActivity.this);
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
                send1 = new SendToWebService(
                        RegistrationActivity.this, 1);
                String res = send1.execute("26", "Ack", processList.get(count))
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


    private void addToEmployeeTable(String response) {

        try {

            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RegistrationActivity.this);
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
                send1 = new SendToWebService(
                        RegistrationActivity.this, 1);
                String res = send1.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToEmployeeTable]", e.toString());
        }
    }

    private void addToFuelTable(String response) {

        try {

            JSONObject jsonResponse = new JSONObject(response);

            String jsonData = jsonResponse.getString("d");
            JSONArray table1 = new JSONArray(jsonData);
            DBAdapter db = new DBAdapter(RegistrationActivity.this);
            db.open();
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

                send1 = new SendToWebService(
                        RegistrationActivity.this, 1);
                String res = send1.execute("26", "Ack", processList.get(count))
                        .get();
                if (res != null) {

                }
                SyncStart(++count);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[addToFuelTable]", e.toString());
        }

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