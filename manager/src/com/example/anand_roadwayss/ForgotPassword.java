package com.example.anand_roadwayss;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPassword extends ActionBarActivity {
    Button btnLogin;
    DBAdapter db;
    public static final String ACTION_SMS_SENT = "com.example.anand_roadwayss.android.apis.os.SMS_SENT_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_password);
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        final EditText phoneET = (EditText) findViewById(R.id.retrievePinEtPhone);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        db = new DBAdapter(this);


        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String message = null;
                boolean error = true;
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        message = "Message sent!";
                        Toast.makeText(
                                getApplicationContext(),
                                "SMS SENT to the registed mobile number",
                                Toast.LENGTH_LONG).show();
                        error = false;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        message = "Error.";
                        Toast.makeText(
                                getApplicationContext(),
                                "Failed to send SMS Please check whether SMS Pack is activated or not",
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        message = "Error: No service.";
                        Toast.makeText(
                                getApplicationContext(),
                                message,
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        message = "Error: Null PDU.";
//		                 Toast.makeText(
//									getApplicationContext(),
//									message,
//									Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        message = "Error: Radio off.";
//		                 Toast.makeText(
//									getApplicationContext(),
//									message,
//									Toast.LENGTH_LONG).show();
                        break;
                }

                finish();
            }
        }, new IntentFilter(ACTION_SMS_SENT));


        btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    String phone = phoneET.getText().toString();
                    db.open();
                    String storedPhone = db.retrieve(phone);

                    if (!phone.equals("") && phone.length() == 10 && phone.startsWith("9") || phone.startsWith("8") || phone.startsWith("7")) {

                        if (phone.equals(storedPhone)) {
                            String storedPin = db.retrievePin(phone);
                            try {
//								SmsManager smsManager = SmsManager.getDefault();
//								smsManager.sendTextMessage(phone, null,
//										"PIN NUMBER FOR FLEETMANAGER : "
//												+ storedPin, null, null);
//								Toast.makeText(
//										getApplicationContext(),
//										"SMS has been sent to the registed mobile number",
//										Toast.LENGTH_LONG).show();
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(phone, null, "PIN NUMBER : " + storedPin, PendingIntent.getBroadcast(
                                        ForgotPassword.this, 0, new Intent(ACTION_SMS_SENT), 0), null);
                                //   sendSMS(phone,"PIN NUMBER : " + storedPin);

//                                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                                sendIntent.putExtra("sms_body", "PIN NUMBER : " + storedPin);
//                                sendIntent.setType("vnd.android-dir/mms-sms");
//                                startActivity(sendIntent);
                                //finish();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "SMS failed, please try again later!",
                                        Toast.LENGTH_LONG).show();
                                ExceptionMessage
                                        .exceptionLog(
                                                getApplicationContext(),
                                                this.getClass().toString()
                                                        + " "
                                                        + "[btnLogin.setOnClickListener]",
                                                e.toString());
                            }
                        } else {
                            phoneET.setText("");

                            Toast.makeText(getApplicationContext(),
                                    "Phone Number does not match",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {
                        phoneET.setError("Enter valid 10 digit number");
//						Toast.makeText(getApplicationContext(),
//								"Phone Number cannot be empty",
//								Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString()
                            + " "
                            + "[btnLogin.setOnClickListener]", e.toString());
                }
            }
        });

//		phoneET.setOnEditorActionListener(new OnEditorActionListener() {
//
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				  if(actionId==EditorInfo.IME_ACTION_DONE){
//	                    if( phoneET.getText().toString().trim().equalsIgnoreCase(""))
//	                        phoneET.setError("Please enter some thing!!!");
////	                    else
////	                        Toast.makeText(getApplicationContext(),"Notnull",Toast.LENGTH_SHORT).show();
//	                }
//				return false;
//			}
//        });
    }


    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }



}
