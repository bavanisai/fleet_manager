package com.example.anand_roadwayss;

import org.json.JSONObject;

import com.example.Interface.IRegistration;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileEdit extends ActionBarActivity implements OnClickListener,
        IRegistration {

    Button profileBtnUpdate;
    EditText profileEtName, profileEtEmail, profileEtPhno, profileEtPin;
    TextView profileTvUserType, profileTvProductKey, profileTvClientName;
    String strEmpType = null;
    String productKey, clientName;
    final IRegistration mRegistration = this;
    CheckBox mCbShowPwd;
    DBAdapter db;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        bindData();
        db = new DBAdapter(getApplicationContext());
        profileBtnUpdate.setOnClickListener(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("PROFILE");
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        setSupportActionBar(toolbar);

        mCbShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    profileEtPin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    profileEtPin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


    }

    private void bindData() {
        profileEtName = (EditText) findViewById(R.id.profileEtName);
        profileEtEmail = (EditText) findViewById(R.id.profileEtEmail);
        profileEtPhno = (EditText) findViewById(R.id.profileEtPhno);
        profileEtPin = (EditText) findViewById(R.id.profileEtPinNumber);
        profileTvUserType = (TextView) findViewById(R.id.profileTvUserType);
        profileTvProductKey = (TextView) findViewById(R.id.profileTvProductKey);
        profileTvClientName = (TextView) findViewById(R.id.profileTvClientName);
        profileBtnUpdate = (Button) findViewById(R.id.profileBtnUpdate);
        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);

    }

    @Override
    protected void onStart() {
        db.open();
        Cursor profileDataCursor = db.retrieveProfileData();
        if (profileDataCursor.getCount() == 1) {
            profileDataCursor.moveToFirst();
            profileEtName.setText(profileDataCursor.getString(profileDataCursor
                    .getColumnIndex(DBAdapter.getKeyName())));
            profileEtPhno.setText(profileDataCursor.getString(profileDataCursor
                    .getColumnIndex(DBAdapter.getKeyPhNo())));
            profileEtEmail.setText(profileDataCursor
                    .getString(profileDataCursor.getColumnIndex(DBAdapter
                            .getRegistrationMail())));
            profileEtPin.setText(profileDataCursor.getString(profileDataCursor
                    .getColumnIndex(DBAdapter.getRegistrationPin())));
            profileTvClientName.setText(profileDataCursor.getString(profileDataCursor
                    .getColumnIndex(DBAdapter.getKeyClientname())));
            profileTvUserType.setText(profileDataCursor
                    .getString(profileDataCursor.getColumnIndex(DBAdapter
                            .getKeyUserType())));
            productKey = profileDataCursor.getString(profileDataCursor
                    .getColumnIndex(DBAdapter.getKeyProductKey()));
            String[] s = productKey.split("-");
            String a = s[0] + "****************" + s[5];

            profileTvProductKey.setText(a);
            profileDataCursor.close();
        }
        db.close();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileBtnUpdate:
                strEmpType = profileTvUserType.getText().toString();
                clientName = profileTvClientName.getText().toString();
                String Name = profileEtName.getText().toString();
                String Email = profileEtEmail.getText().toString();
                String Phone = profileEtPhno.getText().toString();
                String Pin = profileEtPin.getText().toString();

                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = mngr.getDeviceId();
                if (Pin.length() == 4) {

                    if (Name.equals("") || Email.equals("") || Phone.equals("")
                            || Pin.equals("") || productKey.equals("")) {
                        Toast.makeText(getApplicationContext(),
                                "Fields Cannot be Empty", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // Shared Preference For Typr of User
                        SharedPreferences TypeName = getSharedPreferences(
                                "RegisterName", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = TypeName.edit();
                        edit.putString("Name", strEmpType);
                        edit.commit();
                        // End

                        String registrationAuthKey = new Constants()
                                .getRegistrationAuthKey();
                        SendToWebService send = new SendToWebService(
                                ProfileEdit.this, mRegistration);

                        try {
                            send.execute("9", "RegisterAnApplication",
                                    registrationAuthKey, Name, strEmpType, Email,
                                    Phone, Pin, IMEI, productKey);
                        } catch (Exception e) {
                            Toast.makeText(ProfileEdit.this,
                                    "Try after sometime...", Toast.LENGTH_SHORT)
                                    .show();
                            ExceptionMessage
                                    .exceptionLog(
                                            getApplicationContext(),
                                            this.getClass().toString()
                                                    + " "
                                                    + "[profileBtnUpdate.setOnClickListener]",
                                            e.toString());
                        }

                    }

                } else {
                    profileEtPin.setText("");
                    Toast.makeText(getApplicationContext(), "Enter 4 digit Pin",
                            Toast.LENGTH_LONG).show();
                }

                break;
        }

    }

    @Override
    public void onRegistrationComplete(String response) {

        if (response.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(ProfileEdit.this);
            cd.ConnectingToInternet();
        } else if (response.equals("There was an error processing")) {
            Toast.makeText(getApplicationContext(), "Try After Sometime ",
                    Toast.LENGTH_SHORT).show();

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

                    strEmpType = profileTvUserType.getText().toString();
                    clientName = profileTvClientName.getText().toString();
                    String Name = profileEtName.getText().toString();
                    String Email = profileEtEmail.getText().toString();
                    String Phone = profileEtPhno.getText().toString();
                    String Pin = profileEtPin.getText().toString();

                    db.open();
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyName(), Name);
                    cv.put(DBAdapter.getRegistrationMail(), Email);
                    cv.put(DBAdapter.getKeyPhNo(), Phone);
                    cv.put(DBAdapter.getRegistrationPin(), Pin);
                    cv.put(DBAdapter.getRegistrationIs(), 0);
                    cv.put(DBAdapter.getKeyProductKey(), productKey);
                    cv.put(DBAdapter.getKeyUserType(), strEmpType);
                    cv.put(DBAdapter.getKeyClientname(), clientName);

                    long id = db.updateProfile(
                            DBAdapter.getTableRegistration(), cv);
                    db.close();
                    if (id != -1) {
                        Toast.makeText(getApplicationContext(),
                                "Your Profile is updated", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else {
                    OneJSONValue one = new OneJSONValue();
                    String status = one.jsonParsing1(response);
                    // Toast.makeText(getApplicationContext(), status,
                    // Toast.LENGTH_SHORT).show();
                    switch (status) {
                        case "Invalid ServerAuthKey":
                            Toast.makeText(getApplicationContext(),
                                    "Please Contact Server ", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case "Does not Exist":
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

                if (e.toString().contains("java.net.SocketTimeoutException")) {
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
                        + " " + "[jsonParsing]", e.toString());
            }
    }

}
