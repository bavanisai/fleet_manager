package com.example.tripmodule;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.Interface.IConflictTrips;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConflictTripList extends ActionBarActivity implements
        IConflictTrips {

    MatrixCursor ConflictTrips;
    final IConflictTrips mConflictTrips = this;
    ListView trip;
    LinearLayout layout;
    DBAdapter db;
    Toolbar toolbar;
    LinearLayout noDataLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conflict_trip_list);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("CONFLICT TRIP");
        setSupportActionBar(toolbar);
        noDataLayout = (LinearLayout)findViewById(R.id.inboxLinearL);
        trip = (ListView) findViewById(R.id.conflictTripListLV);
        layout = (LinearLayout) findViewById(R.id.conflictRelativeLayout);

        getData();
        db = new DBAdapter(this);

    }

    public void getData() {
        SendToWebService send = new SendToWebService(ConflictTripList.this,
                mConflictTrips);

        try {
            send.execute("31", "GetConflictTrips");

        } catch (Exception e) {
            Toast.makeText(ConflictTripList.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(ConflictTripList.this, this
                    .getClass().toString() + " " + "[onCreate]", e.toString());
        }

    }

    @Override
    public void onConflictTrips(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(
                        ConflictTripList.this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused")
                    || response.contains("TimeoutException") || response.contains("timed out")) {
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

            } else {
                MatrixCursor trip = conflictTripsJsonParsing(response);
                updateConflictData(trip);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(ConflictTripList.this, this
                            .getClass().toString() + " " + "[onConflictTrips]",
                    e.toString());
        }

    }

    private MatrixCursor conflictTripsJsonParsing(String response) {

        String statuschk = null;
        int sKey = 1;

        if (response != null)
        {
            noDataLayout.setVisibility(View.GONE);
            trip.setVisibility(View.VISIBLE);
            try {

                JSONObject jsonResponse = new JSONObject(response);
                // getting the data with tag d
                String jsonData = jsonResponse.getString("d");
                // convert the string to Json array
                JSONArray conflictTripArray = new JSONArray(jsonData);
                JSONObject status1 = conflictTripArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();
                // iterating the array
                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                                    .toString() + " " + "[driverDistanceParser]",
                            statuschk);

                } else if (statuschk.equals("data does not exist")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                                    .toString() + " " + "[driverDistanceParser]",
                            statuschk);

                    noDataLayout.setVisibility(View.VISIBLE);
                    trip.setVisibility(View.GONE);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(R.drawable.nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    noDataLayout.addView(imageView);
                    TextView textView=new TextView(this);
                    textView.setText("NO DATA");
                    textView.setTextSize(14);
                    textView.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.gravity = Gravity.CENTER;
                    layoutParams1.topMargin = 20;
                    textView.setLayoutParams(layoutParams1);
                    noDataLayout.addView(textView);
                } else {
                    String[] columnNames = {"_id", "Vehicle", "Date",
                            "Source", "Destination", "Distance", "Amount",
                            "Status"};

                    ConflictTrips = new MatrixCursor(columnNames);
                    for (int i = 1; i < conflictTripArray.length(); i++) {
                        // JSONObject c = trackVehicle.getJSONObject(i);

                        JSONObject Performance = conflictTripArray
                                .getJSONObject(i);
                        // if (!(Performance.isNull("Name"))) {
                        String vehicleNo = Performance.getString("vehicleNumber");
                        // }
                        // if (!(Performance.isNull("Distance"))) {
                        String date = Performance.getString("tripDate");
                        String[] parts = date.split("T");
                        date = parts[0];
                        // }

                        String source = Performance
                                .getString("sourceName");
                        String destination = Performance
                                .getString("destinationName");
                        String distance =String.valueOf(Performance.getDouble("distance"));
                        String amount =String.valueOf(Performance.getInt("amount"));
                        String status = Performance.getString("status").toUpperCase();
                        ConflictTrips.addRow(new Object[]{sKey, vehicleNo,
                                date, source, destination, distance, amount,
                                status});
                        sKey++;

                    }

                    ConflictTrips.close();
                }

            } catch (JSONException e) {
                if (e.toString().contains("refused")) {
                    ImageView image = new ImageView(this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    }).setView(image);
                    builder.create().show();

                } else if (e.toString().contains(
                        "java.net.SocketTimeoutException")) {

                    ImageView image = new ImageView(this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    }).setView(image);
                    builder.create().show();

                } else {
                    Toast.makeText(ConflictTripList.this,
                            "Try after sometime...", Toast.LENGTH_SHORT).show();
                }
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[driverDistanceParser]", e.toString());

            }

    }
        return ConflictTrips;
    }

    private void updateConflictData(MatrixCursor conflictTripCursor) {

        // String from[] = { conflictTripCursor.getColumnName(1),
        // conflictTripCursor.getColumnName(2),
        // conflictTripCursor.getColumnName(3),
        // conflictTripCursor.getColumnName(4) };


            String from[] = {"Vehicle", "Date", "Source", "Destination", "Distance", "Amount", "Status"};

            int to[] = {R.id.conflictTripListVehNum, R.id.conflictTripListDate,
                    R.id.conflictTripListSrc, R.id.conflictTripListDest, R.id.conflictTripListDistance,
                    R.id.conflictTripListAmount, R.id.conflictTripListStatus};

            // @SuppressWarnings("deprecation")
            SimpleCursorAdapter caCleaner = new SimpleCursorAdapter(
                    ConflictTripList.this, R.layout.conflicttriplistlayout, conflictTripCursor,
                    from, to, 0);

            trip.setAdapter(caCleaner);

    }

//        trip.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, final long id) {
//
//                // AlertDialog.Builder alert = new AlertDialog.Builder(
//                // ConflictTripList.this);
//                // alert.setTitle("Conflict Trip Functions:");
//                //
//                // alert.setPositiveButton("Allow Payment",
//                // new DialogInterface.OnClickListener() {
//                // public void onClick(DialogInterface dialog,
//                // int whichButton) {
//                // paymentMethod(id, "1");
//                //
//                // }
//                // });
//                //
//                // alert.setNeutralButton("Deny Payment",
//                // new DialogInterface.OnClickListener() {
//                // public void onClick(DialogInterface dialog,
//                // int whichButton) {
//                // paymentMethod(id, "0");
//                //
//                // }
//                // });
//                //
//                // alert.setNegativeButton("Cancel",
//                // new DialogInterface.OnClickListener() {
//                // public void onClick(DialogInterface dialog,
//                // int whichButton) {
//                //
//                // }
//                // });
//                //
//                // alert.show();
//
//                final Dialog dialog = new Dialog(ConflictTripList.this);
//                dialog.setTitle("Conflict Trip Functions:");
//
//                dialog.setContentView(R.layout.activity_resolve_conflict_trip);
//
//                TextView resolveConflictCancel = (TextView) dialog
//                        .findViewById(R.id.resolveConflictCancel);
//
//                TextView resolveConflictAllowPayment = (TextView) dialog
//                        .findViewById(R.id.resolveConflictAllowPayment);
//
//                TextView resolveConflictDenyPayment = (TextView) dialog
//                        .findViewById(R.id.resolveConflictDenyPayment);
//
//                Spinner resolveConflictSpinnerDriver = (Spinner) dialog
//                        .findViewById(R.id.resolveConflictSpinnerDriver);
//
//                Spinner resolveConflictSpinnerCleaner = (Spinner) dialog
//                        .findViewById(R.id.resolveConflictSpinnerCleaner);
//
//                Spinner resolveConflictSpinnerDestination = (Spinner) dialog
//                        .findViewById(R.id.resolveConflictSpinnerDestination);
//
//                db.open();
//                lables1 = db.getAllLabels(DBAdapter.getEmployeeDetails(),
//                        "Driver");
//                lables1.add(0, "SELECT THE DRIVER");
//                // Creating adapter for spinner
//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
//                        ConflictTripList.this,
//                        android.R.layout.simple_spinner_item, lables1);
//
//                // Drop down layout style - list view with radio button
//                dataAdapter
//                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                // attaching data adapter to spinner
//                resolveConflictSpinnerDriver.setAdapter(dataAdapter);
//
//                // Set the ClickListener for Spinner
//                resolveConflictSpinnerDriver
//                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                            public void onItemSelected(
//                                    AdapterView<?> adapterView, View view,
//                                    int i, long l) {
//
//                                mSpinnerDriver = lables1.get(i);
//                            }
//
//                            // If no option selected
//                            public void onNothingSelected(AdapterView<?> arg0) {
//
//                            }
//                        });
//                db.close();
//
//                db.open();
//                // Spinner Drop down elements
//                lables3 = db.getAllLabels(DBAdapter.getEmployeeDetails(),
//                        "Cleaner");
//                lables3.add(0, "SELECT THE CLEANER");
//                // Creating adapter for spinner
//                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(
//                        ConflictTripList.this,
//                        android.R.layout.simple_spinner_item, lables3);
//                // Drop down layout style - list view with radio button
//                dataAdapter1
//                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                // attaching data adapter to spinner
//                resolveConflictSpinnerCleaner.setAdapter(dataAdapter1);
//                // Set the ClickListener for Spinner
//                resolveConflictSpinnerCleaner
//                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            public void onItemSelected(
//                                    AdapterView<?> adapterView, View view,
//                                    int i, long l) {
//
//                                mSpinnerCleaner = lables3.get(i);
//                            }
//
//                            // If no option selected
//                            public void onNothingSelected(AdapterView<?> arg0) {
//                            }
//                        });
//
//                db.close();
//
//                db.open();
//                // Spinner Drop down elements
//                lables2 = db.getLocationForSpinner("DESTINATION");
//                lables2.add(0, "SELECT THE DESTINATION");
//                // Creating adapter for spinner
//                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(
//                        ConflictTripList.this,
//                        android.R.layout.simple_spinner_item, lables2);
//                // Drop down layout style - list view with radio button
//                dataAdapter2
//                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                // attaching data adapter to spinner
//                resolveConflictSpinnerDestination.setAdapter(dataAdapter2);
//                // Set the ClickListener for Spinner
//                resolveConflictSpinnerDestination
//                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            public void onItemSelected(
//                                    AdapterView<?> adapterView, View view,
//                                    int i, long l) {
//
//                                mSpinnerDest = lables2.get(i);
//                            }
//
//                            // If no option selected
//                            public void onNothingSelected(AdapterView<?> arg0) {
//                            }
//                        });
//                db.close();
//
//                int a = (int) id;
//
//                if (ConflictTrips.moveToFirst()) {
//                    for (int i = 1; i <= ConflictTrips.getCount(); i++) {
//                        if (i == a) {
//                            locate = ConflictTrips.getString(3);
//                            dest = ConflictTrips.getString(4);
//                            voucherNo = ConflictTrips.getString(5);
//                            driver = ConflictTrips.getInt(6);
//                            cleaner = ConflictTrips.getInt(7);
//                            if ((locate.contains("UnKnown") || dest
//                                    .contains("UnKnown"))
//                                    && driver == 0
//                                    && cleaner == 0) {
//                                resolveConflictSpinnerDestination
//                                        .setVisibility(View.VISIBLE);
//                                resolveConflictSpinnerDriver
//                                        .setVisibility(View.VISIBLE);
//                                resolveConflictSpinnerCleaner
//                                        .setVisibility(View.VISIBLE);
//
//                            } else if ((locate.contains("UnKnown") && !dest
//                                    .contains("UnKnown"))
//                                    && driver != 0
//                                    && cleaner != 0) {
//                                resolveConflictSpinnerDestination
//                                        .setVisibility(View.VISIBLE);
//
//                                resolveConflictSpinnerDriver
//                                        .setVisibility(View.INVISIBLE);
//
//                                resolveConflictSpinnerCleaner
//                                        .setVisibility(View.INVISIBLE);
//
//                            } else if ((locate.contains("UnKnown") && !dest
//                                    .contains("UnKnown"))
//                                    && driver == 0
//                                    && cleaner != 0) {
//                                resolveConflictSpinnerDestination
//                                        .setVisibility(View.VISIBLE);
//                                resolveConflictSpinnerDriver
//                                        .setVisibility(View.VISIBLE);
//                                resolveConflictSpinnerCleaner
//                                        .setVisibility(View.GONE);
//                            } else if ((locate.contains("UnKnown") && !dest
//                                    .contains("UnKnown"))
//                                    && driver != 0
//                                    && cleaner == 0) {
//                                resolveConflictSpinnerDestination
//                                        .setVisibility(View.VISIBLE);
//                                resolveConflictSpinnerDriver
//                                        .setVisibility(View.GONE);
//                                resolveConflictSpinnerCleaner
//                                        .setVisibility(View.VISIBLE);
//                            } else if ((!locate.contains("UnKnown") && !dest
//                                    .contains("UnKnown"))
//                                    && driver == 0
//                                    && cleaner == 0) {
//                                resolveConflictSpinnerDestination
//                                        .setVisibility(View.GONE);
//                                resolveConflictSpinnerDriver
//                                        .setVisibility(View.VISIBLE);
//                                resolveConflictSpinnerCleaner
//                                        .setVisibility(View.VISIBLE);
//                            } else if ((!locate.contains("UnKnown") && !dest
//                                    .contains("UnKnown"))
//                                    && driver == 0
//                                    && cleaner != 0) {
//                                resolveConflictSpinnerDestination
//                                        .setVisibility(View.INVISIBLE);
//                                resolveConflictSpinnerDriver
//                                        .setVisibility(View.VISIBLE);
//                                resolveConflictSpinnerCleaner
//                                        .setVisibility(View.INVISIBLE);
//                            }
//
//                            break;
//
//                        }
//                        ConflictTrips.moveToNext();
//                    }
//
//                }

//                resolveConflictCancel
//                        .setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//
//                                dialog.dismiss();
////								AlertDialog.Builder alert = new AlertDialog.Builder(
////										ConflictTripList.this);
////								alert.setTitle("Conflict Trip Functions:");
////
////								alert.setPositiveButton("Allow Payment",
////										new DialogInterface.OnClickListener() {
////											public void onClick(
////													DialogInterface dialog,
////													int whichButton) {
////												// paymentMethod(id, "1");
////
////											}
////										});
////
////								alert.setNeutralButton("Deny Payment",
////										new DialogInterface.OnClickListener() {
////											public void onClick(
////													DialogInterface dialog,
////													int whichButton) {
////												// paymentMethod(id, "0");
////
////											}
////										});
////
////								alert.setNegativeButton("Cancel",
////										new DialogInterface.OnClickListener() {
////											public void onClick(
////													DialogInterface dialog,
////													int whichButton) {
////
////											}
////										});
////
////								alert.show();
//
//                            }
//                        });
//
//                resolveConflictAllowPayment
//                        .setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//
//                                if ((locate.contains("UnKnown") || dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner == 0) {
//
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")
//                                            || mSpinnerDriver.equals("")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerCleaner
//                                            .equals("SELECT THE CLEANER")
//                                            || mSpinnerCleaner.equals("")) {
//
//                                        AlertDialog.Builder alert = new AlertDialog.Builder(
//                                                ConflictTripList.this);
//                                        alert.setTitle("Trip Without Cleaner?");
//
//                                        alert.setPositiveButton(
//                                                "Yes",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//                                                        mSpinnerCleaner = "0";
//                                                        db.open();
//                                                        mSpinnerDriver = db
//                                                                .getEmployeeId(
//                                                                        mSpinnerDriver,
//                                                                        DBAdapter
//                                                                                .getEmployeeDetails(),
//                                                                        DBAdapter
//                                                                                .getKeyEmployeeId());
//                                                        db.close();
//                                                        paymentMethod(
//                                                                voucherNo, "1",
//                                                                mSpinnerDest,
//                                                                mSpinnerDriver,
//                                                                mSpinnerCleaner);
//                                                    }
//                                                });
//
//                                        alert.setNegativeButton(
//                                                "Cancel",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//
//                                                    }
//                                                });
//
//                                        alert.show();
//                                    } else {
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        mSpinnerCleaner = db.getEmployeeId(
//                                                mSpinnerCleaner,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        paymentMethod(voucherNo, "1",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver != 0
//                                        && cleaner != 0) {
//
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        mSpinnerDriver = Integer
//                                                .toString(driver);
//                                        mSpinnerCleaner = Integer
//                                                .toString(cleaner);
//                                        paymentMethod(voucherNo, "1",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner != 0) {
//
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        mSpinnerCleaner = Integer
//                                                .toString(cleaner);
//                                        paymentMethod(voucherNo, "1",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver != 0
//                                        && cleaner == 0) {
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerCleaner
//                                            .equals("SELECT THE CLEANER")) {
//
//                                        AlertDialog.Builder alert = new AlertDialog.Builder(
//                                                ConflictTripList.this);
//                                        alert.setTitle("Trip Without Cleaner?");
//
//                                        alert.setPositiveButton(
//                                                "Yes",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//                                                        mSpinnerDriver = Integer
//                                                                .toString(driver);
//                                                        mSpinnerCleaner = "0";
//                                                        paymentMethod(
//                                                                voucherNo, "1",
//                                                                mSpinnerDest,
//                                                                mSpinnerDriver,
//                                                                mSpinnerCleaner);
//                                                    }
//                                                });
//
//                                        alert.setNegativeButton(
//                                                "Cancel",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//
//                                                    }
//                                                });
//
//                                        alert.show();
//                                    } else {
//                                        mSpinnerDriver = Integer
//                                                .toString(driver);
//                                        db.open();
//                                        mSpinnerCleaner = db.getEmployeeId(
//                                                mSpinnerCleaner,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        paymentMethod(voucherNo, "1",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((!locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner == 0) {
//                                    if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerCleaner
//                                            .equals("SELECT THE CLEANER")) {
//
//                                        AlertDialog.Builder alert = new AlertDialog.Builder(
//                                                ConflictTripList.this);
//                                        alert.setTitle("Trip Without Cleaner?");
//
//                                        alert.setPositiveButton(
//                                                "Yes",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//                                                        mSpinnerDest = dest;
//                                                        mSpinnerCleaner = "0";
//                                                        db.open();
//                                                        mSpinnerDriver = db
//                                                                .getEmployeeId(
//                                                                        mSpinnerDriver,
//                                                                        DBAdapter
//                                                                                .getEmployeeDetails(),
//                                                                        DBAdapter
//                                                                                .getKeyEmployeeId());
//                                                        db.close();
//                                                        paymentMethod(
//                                                                voucherNo, "1",
//                                                                mSpinnerDest,
//                                                                mSpinnerDriver,
//                                                                mSpinnerCleaner);
//                                                    }
//                                                });
//
//                                        alert.setNegativeButton(
//                                                "Cancel",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//
//                                                    }
//                                                });
//
//                                        alert.show();
//                                    } else {
//                                        mSpinnerDest = dest;
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        mSpinnerCleaner = db.getEmployeeId(
//                                                mSpinnerCleaner,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        paymentMethod(voucherNo, "1",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((!locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner != 0) {
//                                    if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        mSpinnerDest = dest;
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        mSpinnerCleaner = Integer
//                                                .toString(cleaner);
//                                        paymentMethod(voucherNo, "1",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                }
//
//                                // else {
//                                // paymentMethod(voucherNo, "1", mSpinnerDest,
//                                // mSpinnerDriver, mSpinnerCleaner);
//                                //
//                                // }
//
//                            }
//
//                        });
//
//                resolveConflictDenyPayment
//                        .setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//
//                                if ((locate.contains("UnKnown") || dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner == 0) {
//
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")
//                                            || mSpinnerDriver.equals("")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerCleaner
//                                            .equals("SELECT THE CLEANER")
//                                            || mSpinnerCleaner.equals("")) {
//
//                                        AlertDialog.Builder alert = new AlertDialog.Builder(
//                                                ConflictTripList.this);
//                                        alert.setTitle("Trip Without Cleaner?");
//
//                                        alert.setPositiveButton(
//                                                "Yes",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//                                                        mSpinnerCleaner = "0";
//                                                        db.open();
//                                                        mSpinnerDriver = db
//                                                                .getEmployeeId(
//                                                                        mSpinnerDriver,
//                                                                        DBAdapter
//                                                                                .getEmployeeDetails(),
//                                                                        DBAdapter
//                                                                                .getKeyEmployeeId());
//                                                        db.close();
//                                                        paymentMethod(
//                                                                voucherNo, "0",
//                                                                mSpinnerDest,
//                                                                mSpinnerDriver,
//                                                                mSpinnerCleaner);
//                                                    }
//                                                });
//
//                                        alert.setNegativeButton(
//                                                "Cancel",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//
//                                                    }
//                                                });
//
//                                        alert.show();
//                                    } else {
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        mSpinnerCleaner = db.getEmployeeId(
//                                                mSpinnerCleaner,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        paymentMethod(voucherNo, "0",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver != 0
//                                        && cleaner != 0) {
//
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        mSpinnerDriver = Integer
//                                                .toString(driver);
//                                        mSpinnerCleaner = Integer
//                                                .toString(cleaner);
//                                        paymentMethod(voucherNo, "0",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner != 0) {
//
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        mSpinnerCleaner = Integer
//                                                .toString(cleaner);
//                                        paymentMethod(voucherNo, "0",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver != 0
//                                        && cleaner == 0) {
//                                    if (mSpinnerDest
//                                            .equals("SELECT THE DESTINATION")
//                                            || mSpinnerDest.equals("")) {
//                                        Toast.makeText(
//                                                ConflictTripList.this,
//                                                "PLEASE SELECT THE DESTINATION",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerCleaner
//                                            .equals("SELECT THE CLEANER")) {
//
//                                        AlertDialog.Builder alert = new AlertDialog.Builder(
//                                                ConflictTripList.this);
//                                        alert.setTitle("Trip Without Cleaner?");
//
//                                        alert.setPositiveButton(
//                                                "Yes",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//                                                        mSpinnerDriver = Integer
//                                                                .toString(driver);
//                                                        mSpinnerCleaner = "0";
//                                                        paymentMethod(
//                                                                voucherNo, "0",
//                                                                mSpinnerDest,
//                                                                mSpinnerDriver,
//                                                                mSpinnerCleaner);
//                                                    }
//                                                });
//
//                                        alert.setNegativeButton(
//                                                "Cancel",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//
//                                                    }
//                                                });
//
//                                        alert.show();
//                                    } else {
//                                        mSpinnerDriver = Integer
//                                                .toString(driver);
//                                        db.open();
//                                        mSpinnerCleaner = db.getEmployeeId(
//                                                mSpinnerCleaner,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        paymentMethod(voucherNo, "0",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((!locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner == 0) {
//                                    if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else if (mSpinnerCleaner
//                                            .equals("SELECT THE CLEANER")) {
//
//                                        AlertDialog.Builder alert = new AlertDialog.Builder(
//                                                ConflictTripList.this);
//                                        alert.setTitle("Trip Without Cleaner?");
//
//                                        alert.setPositiveButton(
//                                                "Yes",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//                                                        mSpinnerDest = dest;
//                                                        mSpinnerCleaner = "0";
//                                                        db.open();
//                                                        mSpinnerDriver = db
//                                                                .getEmployeeId(
//                                                                        mSpinnerDriver,
//                                                                        DBAdapter
//                                                                                .getEmployeeDetails(),
//                                                                        DBAdapter
//                                                                                .getKeyEmployeeId());
//                                                        db.close();
//                                                        paymentMethod(
//                                                                voucherNo, "0",
//                                                                mSpinnerDest,
//                                                                mSpinnerDriver,
//                                                                mSpinnerCleaner);
//                                                    }
//                                                });
//
//                                        alert.setNegativeButton(
//                                                "Cancel",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int whichButton) {
//
//                                                    }
//                                                });
//
//                                        alert.show();
//                                    } else {
//                                        mSpinnerDest = dest;
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        mSpinnerCleaner = db.getEmployeeId(
//                                                mSpinnerCleaner,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        paymentMethod(voucherNo, "0",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                } else if ((!locate.contains("UnKnown") && !dest
//                                        .contains("UnKnown"))
//                                        && driver == 0
//                                        && cleaner != 0) {
//                                    if (mSpinnerDriver
//                                            .equals("SELECT THE DRIVER")) {
//                                        Toast.makeText(ConflictTripList.this,
//                                                "PLEASE SELECT THE DRIVER",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        mSpinnerDest = dest;
//                                        db.open();
//                                        mSpinnerDriver = db.getEmployeeId(
//                                                mSpinnerDriver,
//                                                DBAdapter.getEmployeeDetails(),
//                                                DBAdapter.getKeyEmployeeId());
//                                        db.close();
//                                        mSpinnerCleaner = Integer
//                                                .toString(cleaner);
//                                        paymentMethod(voucherNo, "0",
//                                                mSpinnerDest, mSpinnerDriver,
//                                                mSpinnerCleaner);
//
//                                    }
//
//                                }
//                                // else {
//                                //
//                                // paymentMethod(voucherNo, "0", mSpinnerDest,
//                                // mSpinnerDriver, mSpinnerCleaner);
//                                //
//                                // }
//
//                            }
//                        });
//
//                dialog.show();
//
//            }
//
//        });



    public void paymentMethod(String voucher, String pay, String spinnerDest,
                              String spinnerDriver, String spinnerCleaner) {
        SendToWebService send = new SendToWebService(ConflictTripList.this);
        try {
            String res = send.execute("32", "ResolveConflictTrips", voucher,
                    pay, spinnerDest, spinnerDriver, spinnerCleaner).get();

            if (res != null)
                try {
                    String status = null;
                    JSONObject jsonResponse = new JSONObject(res);
                    String jsonData = jsonResponse.getString("d");
                    JSONObject d = new JSONObject(jsonData);
                    status = d.getString("status");
                    switch (status) {

                        case "Updated":
                            trip.removeAllViewsInLayout();
                            ConflictTrips = null;
                            refreshActivity();
                            break;

                        case "Invalid Authey":
                            break;

                        case "Voucher Does not Exist":
                            break;
                        default:
                            ExceptionMessage.exceptionLog(this, this.getClass()
                                    .toString() + " " + "[PaymentMethod]", status);
                            break;
                    }

                } catch (Exception e) {
                    ExceptionMessage
                            .exceptionLog(this, this.getClass().toString()
                                    + " " + "[PaymentMethod]", e.toString());
                }

        } catch (InterruptedException e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[PaymentMethod]", e.toString());
        } catch (ExecutionException e) {
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[PaymentMethod]", e.toString());
        }
    }

    public void paymentMethod(long id, String pay) {
        int a = (int) id;
        if (ConflictTrips.moveToFirst()) {
            for (int i = 1; i <= ConflictTrips.getCount(); i++) {
                if (i == a) {

                    String voucher = ConflictTrips.getString(5);
                    SendToWebService send = new SendToWebService(
                            ConflictTripList.this);
                    try {
                        String res = send.execute("32", "ResolveConflictTrips",
                                voucher, pay).get();

                        if (res != null)
                            try {
                                String status = null;
                                JSONObject jsonResponse = new JSONObject(res);
                                String jsonData = jsonResponse.getString("d");
                                JSONObject d = new JSONObject(jsonData);
                                status = d.getString("status");
                                switch (status) {

                                    case "Updated":
                                        trip.removeAllViewsInLayout();
                                        ConflictTrips = null;
                                        refreshActivity();
                                        // getData();
                                        break;

                                    case "Invalid Authey":
                                        break;

                                    case "Voucher Does not Exist":
                                        break;
                                    default:
                                        ExceptionMessage.exceptionLog(this, this
                                                .getClass().toString()
                                                + " "
                                                + "[PaymentMethod]", status);
                                        break;
                                }

                            } catch (Exception e) {
                                ExceptionMessage.exceptionLog(this, this
                                        .getClass().toString()
                                        + " "
                                        + "[PaymentMethod]", e.toString());
                            }

                    } catch (InterruptedException e) {
                        ExceptionMessage.exceptionLog(this, this.getClass()
                                        .toString() + " " + "[PaymentMethod]",
                                e.toString());
                    } catch (ExecutionException e) {
                        ExceptionMessage.exceptionLog(this, this.getClass()
                                        .toString() + " " + "[PaymentMethod]",
                                e.toString());
                    }

                    break;

                }
                ConflictTrips.moveToNext();
            }

        }

        // ConflictTrips.close();

    }

    public void refreshActivity() {
        this.finish();
        Intent intent = new Intent(this, ConflictTripList.class);
        startActivity(intent);

    }

}
