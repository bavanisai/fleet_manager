package com.example.anand_roadwayss;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.Inbox.InboxList;
import com.example.tripmodule.LocationActivity;
import com.example.tripmodule.TripActivity;
import com.example.tripmodule.TripListFragment;
import com.example.tripmodule.TripManageFragment;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import javax.security.auth.Subject;

public class GcmServiceIntent extends IntentService {
    static int notificationId = 0;
    public Activity _context;



    public GcmServiceIntent() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        String regId = "";
        CharSequence cs = "#";
        CharSequence Notice = "!";
        CharSequence alert = "$";
        if (extras != null) {
            regId = extras.getString("Data");
            String msg = extras.getString("Msg");

            if (msg != null && msg.length() != 0) {

                if (msg.contains(cs)) {
                    DoOperationOnMessage(msg);// For Deleting Trip in Locally
                    // without Notification
                } else if (msg.contains(Notice)) {
                    sendNotification1(msg);// For adding Location with
                    // notification
                } else if (msg.contains("@")) {
                    sendNotification(msg);// for adding Trip Details with
                    // notification we using "@" symbol
                } // forward

                else if (msg.contains(alert)) {
                    sendNotificationSourceDestinationAlert(msg);// Left from source and reached destination alert $ is used
                }
                else
                    sendNotificationFuel(msg);// FOR FUEL NOTIFICATION

            }
            if (regId != null) {
                if (regId != "" && regId.length() > 1 && regId != null) {
                    DBAdapter db = new DBAdapter(this);
                    db.open();
                    String phone = db.getphone();
                    // int isReg = db.isRegistered(phone);
                    db.close();
                    SendToWebService send = new SendToWebService(this, 1);
                    try {

                        String res = send.execute("12", "SaveGcmRegId", regId).get();
                        OneJSONValue one = new OneJSONValue();
                        String jsonData = one.jsonParsing1(res);

                        if (jsonData.equals("inserted")
                                || jsonData.equals("updated")) {
                            db.open();
                            ContentValues values = new ContentValues();
                            values.put(DBAdapter.getRegistrationIs(), 1);
                            db.updateRegId(values, phone);
                            db.close();
                        }
                    } catch (Exception e) {
                        ExceptionMessage.exceptionLog(this, this.getClass().toString(), e.toString());
                    }

                }
            }
        }

    }

    private void DoOperationOnMessage(String msg) {

//        try {
//            StringTokenizer Trip = new StringTokenizer(msg, "#");
//            String TripMessage = Trip.nextToken();
//            String TripVoucherId = Trip.nextToken();
//            if (TripVoucherId != null && TripVoucherId != "") {
//                // Do some Operation to delete Trip;
//                DBAdapter db = new DBAdapter(this);
//                db.open();
//                boolean a = db.deleteTrip(DBAdapter.getSourceDestDetails(),
//                        TripVoucherId);
//                if (a) {
//                    // Toast.makeText(GcmServiceIntent.this,
//                    // "Im deleting Trip" + TripVoucherId,
//                    // Toast.LENGTH_SHORT).show();
//
//                    ExceptionMessage
//                            .exceptionLog(
//                                    this,
//                                    this.getClass().toString(),
//                                    msg
//                                            + "Push notification has been received and deleted");
//
//                }
//                db.close();
//
//                if (TripActivity.isRunningActivity2 == true
//                        || TripManageFragment.isRunningActivity == true
//                        || TripListFragment.isRunningActivity1 == true) {
//                    // TripListFragment tl=new TripListFragment();
//                    // tl.refreshActivity();
//                    refreshActivity();
//                }
//            }
//            //
//        } catch (NoSuchElementException ne) {
//            ExceptionMessage.exceptionLog(this, this.getClass().toString(),
//                    ne.toString());
//        }

    }

    // @SuppressWarnings("static-access")
    public void refreshActivity() {
        Intent i = new Intent(this, TripActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        this.startActivity(i);
    }

    // @SuppressWarnings("deprecation")
    private void sendNotification(String msg)
    {try {
        String subject = null;
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_stat_gcm;
        CharSequence tickerText = "VEHICLE MESSAGE";
        long when = System.currentTimeMillis(); // now
        Context context = getApplicationContext();

        String[] Message = msg.split(",");
        subject = "VEHICLE MESSAGE";
        String mg = Message[0];
        mg.replace("@", "");

        DBAdapter db = new DBAdapter(getApplicationContext());
        ContentValues cv = new ContentValues();
        db.open();
        cv.put(DBAdapter.getKeyDate(), Message[1]);
        cv.put(DBAdapter.getKeySubject(), subject);
        cv.put(DBAdapter.getKeyMessage(), mg);
        cv.put(DBAdapter.getKeyFlag(), 0);
        long id = db.insertContactWithDelete(DBAdapter.getInboxDetails(), cv);
        db.close();


        Notification notification = new Notification(icon, tickerText, when);
        Intent Destination = new Intent(context, TripActivity.class);
        Destination.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle c = new Bundle();
        c.putString("Message", Message[0]);
        Destination.putExtras(c);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                Destination, PendingIntent.FLAG_UPDATE_CURRENT);

        // String text = "Calls will be blocked while driving";

        notification
                .setLatestEventInfo(context, tickerText, Message[0], contentIntent);

//        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;

        StringTokenizer Trip = new StringTokenizer(msg, "@");
        String TripMessage = Trip.nextToken();
        String TripVoucherId = Trip.nextToken();
//        SampleAlarmReceiver sample = new SampleAlarmReceiver();
//        sample.setAlarm(this, TripVoucherId);

        mNotificationManager.notify(notificationId, notification);
        mNotificationManager.cancel(notificationId);
        notificationId++;
    }catch (Exception e)
    {
        ExceptionMessage
                .exceptionLog(
                        getApplicationContext(),
                        this.getClass().toString()
                                + " "
                                + "[sendNotification]",
                        e.toString());
    }
    }

    private void sendNotification1(String msg)
    {try {
        String subject = null;

        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_stat_gcm;
        CharSequence tickerText = "VEHICLE MESSAGE";
        long when = System.currentTimeMillis(); // now
        Context context = getApplicationContext();

        String[] Message = msg.split(",");
        subject = "VEHICLE MESSAGE";
        String mg = Message[0];
        mg.replace("!", "");

        DBAdapter db = new DBAdapter(getApplicationContext());
        ContentValues cv = new ContentValues();
        db.open();
        cv.put(DBAdapter.getKeyDate(), Message[1]);
        cv.put(DBAdapter.getKeySubject(), subject);
        cv.put(DBAdapter.getKeyMessage(), mg);
        cv.put(DBAdapter.getKeyFlag(), 0);
        long id = db.insertContactWithDelete(DBAdapter.getInboxDetails(), cv);
        db.close();


        Notification notification = new Notification(icon, tickerText, when);
        Intent Destination = new Intent(context, LocationActivity.class);
        Destination.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                Destination, PendingIntent.FLAG_UPDATE_CURRENT);

        // String text = "Calls will be blocked while driving";

        notification
                .setLatestEventInfo(context, tickerText, Message[0], contentIntent);

//        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(notificationId, notification);
        notificationId++;
    }catch (Exception e)
    {
        ExceptionMessage
                .exceptionLog(
                        getApplicationContext(),
                        this.getClass().toString()
                                + " "
                                + "[sendNotification1]",
                        e.toString());
    }
    }

    // THIS METHOD FOR FUEL NOTIFICATION SEND BY SERVER MODIFIED ON 20/12/2014

    private void sendNotificationFuel(String msg) {
        try {
            //msg="{"d":"{"\subject\":\"Fuel pilferage alert\",\"date\":\"2014-12-24\",\"vehicleNumber\":\"MH009\",\"diverName\":\"Dean\",\"driverPhoneNumber\":\"9876543210\",\"cleanerName\":\"Sam\",\"cleanerPhoneNumber\":\"8765432190\",\"address\":\"New BEL Road, Devasandra\",\"tripStatus\":\"OnTrip\"}"}";
            String jsonData = null;
            String date = null;
            String subject = null;
            String message = null;
			 CharSequence tickerText=null;
			String display=null;
            try {

             JSONArray a=new JSONArray(msg);
            JSONObject d=a.getJSONObject(0);
            subject=d.getString("subject").trim();
            date=d.getString("date").trim();
            message=a.toString();
			
            } catch (Exception e) {
                String excep = e.toString();
            }

            DBAdapter db = new DBAdapter(getApplicationContext());
            ContentValues cv = new ContentValues();
            db.open();
            cv.put(DBAdapter.getKeyDate(), date);
            cv.put(DBAdapter.getKeySubject(), subject);
            cv.put(DBAdapter.getKeyMessage(), message);
            cv.put(DBAdapter.getKeyFlag(), 0);

            long id = db.insertContactWithDelete(DBAdapter.getInboxDetails(), cv);
            db.close();

            NotificationManager mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            int icon = R.drawable.ic_stat_gcm;
			
			if(subject.equals("Fuel pilferage alert")) {
				  tickerText = "Fuel pilferage alert";
					display="The vehicle fuel level is reduced by 5ltrs";

				}
            else if(subject.equals("Conflict trip")){
                tickerText = "Conflict trip";
                display="The vehicle trip is closed manually";
            }
				
            long when = System.currentTimeMillis(); // now
            Context context = getApplicationContext();
            Notification notification = new Notification(icon, tickerText, when);

            Intent Destination = new Intent(context, InboxList.class);
            Destination.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    Destination, PendingIntent.FLAG_UPDATE_CURRENT);

            // String text = "Calls will be blocked while driving";

            notification
                    .setLatestEventInfo(context, tickerText, display, contentIntent);

//            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            notification.defaults |= Notification.DEFAULT_SOUND;

            mNotificationManager.notify(notificationId, notification);
            notificationId++;
        }

        catch(Exception e){
            ExceptionMessage
                    .exceptionLog(
                            getApplicationContext(),
                            this.getClass().toString()
                                    + " "
                                    + "[sendNotificationFuel]",
                            e.toString());
        }
    }

    private void sendNotificationSourceDestinationAlert(String msg) {
        try {
            if (msg.contains("appUrl")) {
                String[] appUrlArray = msg.split(":");
                String appUrlData = appUrlArray[1];
                appUrlData = "http://" + appUrlData + ":" + appUrlArray[2]
                        + "/";
                SharedPreferences AppUrl = getSharedPreferences("AppUrl",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = AppUrl.edit();
                editor.putString("appUrl", appUrlData);
                editor.commit();
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[GCM]", "AppUrl Updated");

            } else if (msg.contains("reRegister")) {
                Intent in = new Intent(this, SynchronizeServerDataService.class);
                startService(in);
            } else if (msg.contains("updateApp")) {

                SharedPreferences AppUrl = getSharedPreferences("updateApp",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = AppUrl.edit();
                editor.putString("update", "true");
                editor.commit();
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[GCM]", "updateApp Updated");
            } else {
                String subject = null;
                NotificationManager mNotificationManager = (NotificationManager) this
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                int icon = R.drawable.ic_stat_gcm;

                long when = System.currentTimeMillis(); // now
                Context context = getApplicationContext();
                String[] Message = msg.split(",");
                //subject = "VEHICLE MESSAGE";
                String messag = Message[0].replace("$", "");
                CharSequence tickerText = Message[1];

                DBAdapter db = new DBAdapter(getApplicationContext());
                ContentValues cv = new ContentValues();
                db.open();
                cv.put(DBAdapter.getKeyDate(), Message[2]);
                cv.put(DBAdapter.getKeySubject(), Message[1]);
                cv.put(DBAdapter.getKeyMessage(), messag);
                cv.put(DBAdapter.getKeyFlag(), 0);
                long id = db.insertContactWithDelete(DBAdapter.getInboxDetails(), cv);
                db.close();


                Notification notification = new Notification(icon, tickerText, when);
                Intent Destination = new Intent(context, InboxList.class);
                Destination.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        Destination, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setLatestEventInfo(context, tickerText, messag, contentIntent);
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.defaults |= Notification.DEFAULT_SOUND;
                mNotificationManager.notify(notificationId, notification);
                notificationId++;
            }
        }catch (Exception e)
        {
            ExceptionMessage
                    .exceptionLog(
                            getApplicationContext(),
                            this.getClass().toString()
                                    + " "
                                    + "[sendNotificationSourceDestinationAlert]",
                            e.toString());
        }
    }
}