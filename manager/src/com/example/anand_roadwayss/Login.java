package com.example.anand_roadwayss;

import com.example.ManualAppServerSync.RepairApp;
import com.example.anand_roadways.util.SystemUiHider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Login extends Activity {

    SharedPreferences pref;
    Editor editor;
    DBAdapter db;
    EditText loginEdtPin1;
    Button loginBtn;
    TextView Fpswd;
    String regid;
    Context context;
    String clientName = "", name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginn);
        pref = getSharedPreferences("testapp", MODE_PRIVATE);
        // editor = pref.edit();
        db = new DBAdapter(this);

        Fpswd = (TextView) findViewById(R.id.loginBtnFgtPwd);
        loginBtn = (Button) findViewById(R.id.loginBtnLogin);
        loginEdtPin1 = (EditText) findViewById(R.id.loginEdtPin11);

        try {
            db.open();
            Cursor profileDataCursor = db.retrieveProfileData();
            if (profileDataCursor.getCount() == 1) {
                profileDataCursor.moveToFirst();
                name = profileDataCursor.getString(profileDataCursor
                        .getColumnIndex(DBAdapter.getKeyName()));
                clientName = profileDataCursor.getString(profileDataCursor
                        .getColumnIndex(DBAdapter.getKeyClientname()));

                profileDataCursor.close();
            }
            db.close();


//for time being am commenting

//            Intent in = new Intent(this, UpdateAppService.class);
//            in.putExtra("client", clientName);
//            startService(in);
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this
                    .getClass().toString()
                    + " "
                    + "OnCreate", e.toString());
        }


        try {
            loginBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    hideSoftKeyboard(Login.this, v);

                    String pin = loginEdtPin1.getText().toString();
                    db.open();
                    String storedPin = db.getSinlgeEntry(pin);

                    if (pin.equals(storedPin)) {
                        // editor.putString("register", "true");
                        // editor.commit();
                        loginEdtPin1.setText("");
                        Intent intent_welcome = new Intent(
                                getApplicationContext(), Welcome.class);
                        startActivity(intent_welcome);
                        ExceptionMessage.exceptionLog(Login.this, this.getClass().toString(),
                                clientName + " " + name + " " + getAppData());

                    } else if (pin.equals("")) {
                        loginEdtPin1.setError("Enter valid Pin");
//						Toast.makeText(getApplicationContext(),
//								"Enter your Pin", Toast.LENGTH_LONG).show();
                    } else {
                        loginEdtPin1.setText("");
                        loginEdtPin1.setError("Incorrect Pin");
//						Toast.makeText(getApplicationContext(),
//								"Incorrect Pin", Toast.LENGTH_LONG).show();
                    }

                }
            });
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString(),
                    e.toString());
        }
        Fpswd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentpwd = new Intent(getApplicationContext(),
                        ForgotPassword.class);
                startActivity(intentpwd);
            }
        });

        loginEdtPin1.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {

                    hideSoftKeyboard(Login.this, v);
                    loginEdtPin1 = (EditText) findViewById(R.id.loginEdtPin11);
                    String pin = loginEdtPin1.getText().toString();
                    db.open();
                    String storedPin = db.getSinlgeEntry(pin);

                    if (pin.equals(storedPin)) {
                        // editor.putString("register", "true");
                        // editor.commit();
                        loginEdtPin1.setText("");
                        Intent intent_welcome = new Intent(
                                getApplicationContext(), Welcome.class);
                        startActivity(intent_welcome);
                        ExceptionMessage.exceptionLog(Login.this, this.getClass().toString(),
                                clientName + " " + name + " " + getAppData());
                    } else if (pin.equals("")) {
                        loginEdtPin1.setError("Enter valid Pin");
//						Toast.makeText(getApplicationContext(),
//								"Enter your Pin", Toast.LENGTH_LONG).show();
                    } else {
                        loginEdtPin1.setText("");
                        loginEdtPin1.setError("Incorrect Pin");
//						Toast.makeText(getApplicationContext(),
//								"Incorrect Pin", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {

        try {
            SharedPreferences login = getSharedPreferences("updateApp",
                    MODE_PRIVATE);
            String upStatus = login.getString("update", "nil");
            pref = getSharedPreferences("testapp", MODE_PRIVATE);
            String Status = pref.getString("register", "nil");
            if (Status.equals("nil")) {
                Intent reg = new Intent(this, RegistrationActivity.class);
                startActivity(reg);
                finish();
            } else if (!Status.equals("nil") && upStatus.equals("nil")) {
                Intent reg = new Intent(this, RepairApp.class);
                startActivity(reg);
                finish();
            } else {
                db.open();
                String signExist = db.checkTforSignExist();
                if (signExist.equals("NOT EXIST")) {
                    Intent in = new Intent(Login.this, SignRegisterActivity.class);
                    startActivity(in);
                }

                db.close();
            }

        } catch (Exception e)
        {
            ExceptionMessage.exceptionLog(getApplicationContext(), this.getClass()
                    .toString() + " " + "[onStart()]", e.toString());
        }

        super.onStart();
    }

    public String getAppData() {
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceIMEI = mngr.getDeviceId();
        int versionCode = new Constants()
                .getApplicationVersionCode(Login.this);
        String versionName = new Constants()
                .getApplicationVersionName(Login.this);
        String AppType = new Constants().getAppType();
        int dbVersion = DBAdapter.getDBVersion();

        String currentVersion = AppType + "_" + versionName + "_Build" + "_"
                + versionCode + "_DBVersion " + "_" + dbVersion;
        return currentVersion;

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.profile_edit, menu);
//		 //menu.getItem(0).setIcon(R.drawable.profile);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//
//		case R.id.action_profile:
//			Intent viewIntent = new Intent(Login.this,ProfileEdit.class);
//			startActivity(viewIntent);
//			return true;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
@Override
public void onBackPressed() {
    super.onBackPressed();
    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
    homeIntent.addCategory(Intent.CATEGORY_HOME);
    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(homeIntent);
}


}
