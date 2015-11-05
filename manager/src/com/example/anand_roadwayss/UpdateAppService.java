package com.example.anand_roadwayss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;

import com.example.AppUpdate.AppUpdateCheck;

public class UpdateAppService extends IntentService {

    String location;
    String packageName;


    public UpdateAppService() {
        super("Update App");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String clientName = intent.getStringExtra("client");
            packageName="package:com.example.anand_roadwayss".toString();
            SendToWebService send = new SendToWebService(this, 1);
            String authKey = new Constants().getRegistrationAuthKey();
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String deviceIMEI = mngr.getDeviceId();
            int versionCode = new Constants()
                    .getApplicationVersionCode(UpdateAppService.this);
            String versionName = new Constants()
                    .getApplicationVersionName(UpdateAppService.this);
            String AppType = new Constants().getAppType();
            String currentVersion = AppType + "_" + versionName + "_Build" + "_"
                    + versionCode;

//            String currentVersion = "DEMO_"+ AppType + "_" + versionName + "_Build" + "_"
//                    + versionCode;

            try {
                String res = send.execute("33", "ApplicationUpdateCheck", authKey, deviceIMEI, currentVersion).get();
                if (res != null) {
                    String jsonData = null;
                    String fileName;
                    try {
                        JSONObject jsonResponse = new JSONObject(res);
                        jsonData = jsonResponse.getString("d");
                        JSONObject d = new JSONObject(jsonData);
                        String status = d.getString("status").trim();
                        if (status.equals("Update Available")) {
                            String url1 = d.getString("updateUrl");
                            String[] value = url1.split("/");
                            fileName = value[value.length - 1];


//						Intent appIntent = new Intent(Intent.ACTION_VIEW);
//
//						appIntent.setDataAndType(Uri.fromFile(new File(url)),
//								"application/vnd.android.package-archive");
//						appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//						startActivity(appIntent);
//						boolean corruptedApkFile = false;
//						try {
//						     new JarFile(url); //Detect if the file have been corrupted
//						} catch (Exception ex) {
//						     corruptedApkFile = true;
//						}


                            try {
                                URL url = new URL(url1);
                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                urlConnection.setRequestMethod("GET");
                                //urlConnection.setDoOutput(true);
                                urlConnection.connect();

                                File sdcard = Environment.getExternalStorageDirectory();
                                File file = new File(sdcard, fileName);
                                location = file.toString();
                                if (file.exists()) {
                                    file.delete();
                                }

                                FileOutputStream fileOutput = new FileOutputStream(file);

                                InputStream inputStream = urlConnection.getInputStream();

                                byte[] buffer = new byte[1024];
                                int bufferLength = 0;

                                while ((bufferLength = inputStream.read(buffer)) > 0) {
                                    fileOutput.write(buffer, 0, bufferLength);
                                }
                                fileOutput.close();
//						        this.checkUnknownSourceEnability();
                                //   this.initiateInstallation();

                            } catch (MalformedURLException e)
                            {
                                ExceptionMessage.exceptionLog(this, this
                                                .getClass().toString() + " " + "[onHandleIntent()]",
                                        e.toString());
                                e.printStackTrace();
                            } catch (IOException e) {
                                ExceptionMessage.exceptionLog(this, this
                                                .getClass().toString() + " " + "[onHandleIntent()]",
                                        e.toString());
                                e.printStackTrace();
                            }

                            Uri packageURI = Uri.parse(packageName);
                            Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, packageURI);
                            intent1.setDataAndType
                                    (Uri.fromFile(new File(location)),
                                            "application/vnd.android.package-archive");
                           // intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);

                            ExceptionMessage.exceptionLog(UpdateAppService.this, this
                                    .getClass().toString()
                                    + " "
                                    + "OnHandleIntent", "App Updated for " + clientName);

                        }

                    } catch (JSONException e)
                    {
                        ExceptionMessage.exceptionLog(this, this
                                        .getClass().toString() + " " + "[onHandleIntent()-jsonException]",
                                e.toString());
                    } catch (Exception e) {
                        ExceptionMessage.exceptionLog(this, this
                                        .getClass().toString() + " " + "[onHandleIntent()]",
                                e.toString());
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(this, this
                                .getClass().toString() + " " + "[onHandleIntent()]",
                        e.toString());
                e.printStackTrace();
            }
        }
        catch(Exception e){
            ExceptionMessage.exceptionLog(UpdateAppService.this, this
                    .getClass().toString()
                    + " "
                    + "OnHandleIntent", e.toString() );
        }
    }

    public void update(){
        try {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Uri packageURI = Uri.parse(packageName);
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, packageURI);
                            intent.setDataAndType
                                    (Uri.fromFile(new File(location)),
                                            "application/vnd.android.package-archive");
                            startActivity(intent);

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked

                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("New Apk Available..").setPositiveButton("Yes Proceed", dialogClickListener)
                    .setNegativeButton("No.", dialogClickListener);
//            AlertDialog dialog = builder.create();
//            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
            builder.show();
        }
        catch(Exception e){
            ExceptionMessage.exceptionLog(UpdateAppService.this, this
                    .getClass().toString()
                    + " "
                    + "OnHandleIntent", e.toString());

        }
    }

}
