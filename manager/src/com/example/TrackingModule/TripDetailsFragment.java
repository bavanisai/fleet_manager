package com.example.TrackingModule;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.Interface.IAck;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;
import com.example.anand_roadwayss.IpAddress;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TripDetailsFragment extends Fragment implements IAck {

    String addressline, SourceName, DestinationName, CurrentDateTime, driverId, Status,driverName,
            Speed="", RunningTime, KMTravelled,tripstatus1;
    static String VehLV;
    static boolean bValid = false;
    private static String Fuel = "0";
    double SourceLatitude, SourceLongitude, DestinationLatitude,
            DestinationLongitude, dCurrentLat, dCurrentLng;
    TextView vehNumbersListView, textView2vehNO, tVehicle, tDriver, tContact,
            ErrorMessage, tSource, tDestination, tCurrent, tFuel, tStatus, tSpeed, tRunningTime,
            tKmTravel;
    Button viewMap, ErrorBtn;
    ImageView iDriver;
    private final IAck mAck = this;
    String adress = new IpAddress().getIpAddress();
    LinearLayout SpeedLay,distanceLay;
    MatrixCursor cursorDestination;
    EditText et1;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vehicle_trip_details_fragment,
                container, false);

        view.findViewById(R.id.btnAddFuel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (VehLV != null) {
                            DialogForFuel();
                        } else {
                            Toast.makeText(getActivity(),
                                    "Please Select Vehicle from List View",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        viewMap = (Button) view.findViewById(R.id.btnViewonMap);

        viewMap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if(TripMapFragment.disable!=null) {
                    if (TripMapFragment.disable.equals("data does not exist"))
                        viewMap.setEnabled(false);
                    if(TripMapFragment.disable.equals("OK")) {
                        viewMap.setEnabled(true);
                        ((TrackActivity) getActivity()).setCurrentItem(2, true);
                    }
                }

            }
        });
        ErrorBtn = (Button) view.findViewById(R.id.ErrorTrackbtn);
        ErrorBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String map = ErrorBtn.getText().toString();
                if (map.equals("See Current Location")) {
                    Toast.makeText(getActivity(),"map loading..",Toast.LENGTH_LONG).show();
                    ((TrackActivity) getActivity()).setCurrentItem(2, true);
                }
                else if (map.equals("OK"))
                    ((TrackActivity) getActivity()).setCurrentItem(0, true);

            }
        });

        return view;
    }

    private void DialogForFuel() {


//        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//        alert.setTitle("Fuel Level"); // Set Alert dialog title here
//        alert.setMessage("ENTER THE CURRENT FUEL LEVEL FOR " + VehLV); // Message
//        // here
//        // Set an EditText view to get user input
//        final EditText input = new EditText(getActivity());
//        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        alert.setView(input);
//        alert.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.dismiss();
//
//                    } // End of onClick(DialogInterface dialog, int
//                    // whichButton)
//                }); // End of alert.setPositiveButton
//        alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//                Fuel = input.getText().toString().trim();
////                Fuel.matches("^[0-9]*$");
//
//                if (!Fuel.equals("0") && !Fuel.equals("")) {
//                    SendToWebService send = new SendToWebService(getActivity(),
//                            mAck);
//                    send.execute("23", "FuelInLiters", VehLV, Fuel);
//                    dialog.cancel();
//
//                } else {
//                    Toast.makeText(getActivity(),
//                            "Please enter the Fuel value...", Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        }); // End of alert.setNegativeButton
//
//        AlertDialog alertDialog = alert.create();
//        alertDialog.show();

        //-----------------------------------

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.payment_dialog);
        dialog.setTitle("Fuel");
        et1 = (EditText) dialog.findViewById(R.id.editText);
        et1.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextView t1 = (TextView) dialog.findViewById(R.id.cancle);
        TextView t2 = (TextView) dialog.findViewById(R.id.ok);
        TextView title=(TextView)dialog.findViewById(R.id.paymentTxt);
        title.setText("ENTER THE FUEL VALUE");
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fuel = et1.getText().toString();
                if (!Fuel.equals("0") && !Fuel.equals("")) {
                    SendToWebService send = new SendToWebService(getActivity(),
                            mAck);
                    send.execute("23", "FuelInLiters", VehLV, Fuel);
                    dialog.cancel();

                } else {
                    Toast.makeText(getActivity(),
                            "Please enter the Fuel value...", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        dialog.show();

    }

    // @Override
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
            super.setMenuVisibility(menuVisible);
            if (VehLV != null && VehicleListFragment.res != null) {
                TrackingVehicleTrip(VehicleListFragment.res);
            }
        }
    }

    public void getDriver(String EmpName) {
        DBAdapter db = new DBAdapter(getActivity());
        try {
            db.open();
            Cursor cursor = db.getDriverEntryName(EmpName);
            if (cursor.moveToFirst()) {
                tDriver.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyName())));
                tContact.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyPhNo())));
                byte[] bitmapData = cursor.getBlob(cursor
                        .getColumnIndex(DBAdapter.getKeyPhoto()));
                if (bitmapData != null) {
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(
                            bitmapData);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                    if (theImage != null) {
                        iDriver.setImageBitmap(theImage);
                    }
                }
                cursor.close();
                db.close();
            }
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[getDriver]", e.toString());
        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Try after sometime...",
//                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[getDriver]", e.toString());

        }

    }

    public boolean jsonParsing1(String response) {
        boolean isValidData = false;
        if (response != null) {
            try {
                ErrorBtn = (Button) getActivity().findViewById(
                        R.id.ErrorTrackbtn);
                LinearLayout Error = new LinearLayout(getActivity());
                Error = (LinearLayout) getActivity().findViewById(
                        R.id.ErrorTripManageLayout);
                LinearLayout details = new LinearLayout(getActivity());
                details = (LinearLayout) getActivity().findViewById(
                        R.id.TrackDetailsLayout);
                Error.setVisibility(View.VISIBLE);
                details.setVisibility(View.GONE);
                JSONObject jsonResponse = new JSONObject(response);
                // getting the data with tag d
                String jsonData = jsonResponse.getString("d");
                // String jsonData="";
                // convert the string to Json array
                JSONArray trackVehicle = new JSONArray(jsonData);
                JSONObject c = trackVehicle.getJSONObject(0);

                // Manu changes
                Status = c.getString("vehicleStatus");
                Speed = c.getString("CurrentSpeed");

                // Checking For Current Latitude And Longitude

                if (c.getString("status").equals("OnTrip")) {
                    Error.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                    isValidData = true;
                    bValid = true;
                    // Vehicle Node
                    JSONObject VehicleJson = c.getJSONObject("Vehicle");
                    if (!(VehicleJson.isNull("SourceName"))) {
                        SourceName = VehicleJson.getString("SourceName");
                    }
                    if (!(VehicleJson.isNull("DestinationName"))) {
                        DestinationName = VehicleJson
                                .getString("DestinationName");
                    }
                    Fuel = c.getString("CurrentFuel").trim();
                    //New Feature added On Feb
                    RunningTime = c.getString("runningTime");
                    KMTravelled = c.getString("distnaceInKM");

                    //End
                    // Driver Child node
                    JSONObject driver = VehicleJson.getJSONObject("Driver");
                    if (!(driver.isNull("empId"))) {
                        driverId = driver.getString("empId");
                    }

                } else if (c.getString("status").equals("OffTrip"))
                {

                    ErrorMessage = (TextView) getActivity().findViewById(
                            R.id.ErrorMessageinTrack);
                    tStatus = (TextView) getActivity().findViewById(
                            R.id.ErrorStatusinTrack);
                    tSpeed = (TextView) getActivity().findViewById(
                            R.id.ErrorSpeedinTrack);
                    tStatus.setText("Status : " + Status);
                    tStatus.setVisibility(View.VISIBLE);
                    if (Status.equals("Running") && !Speed.equals("")) {
                        double spd = Double.parseDouble(Speed);
                        DecimalFormat numberFormat = new DecimalFormat("##.00");

                        tSpeed.setText("SPEED(Km/h) : " + numberFormat.format(spd));
                        tSpeed.setVisibility(View.VISIBLE);
                    }
                    ErrorMessage.setText("TRIP IS NOT YET ADDED");
                    ErrorBtn.setText("See Current Location");
                    JSONObject CurrLatLong = c.getJSONObject("CurrentLatLong");
                    if (!(CurrLatLong.getString("Latitude").equals(""))) {
                        bValid = true;
                    } else {
                         ErrorMessage.setText("Try After Some Times");
                        ErrorBtn.setText("OK");
                    }
                } else {
                    Toast.makeText(getActivity(), "Try After Some Times",
                            Toast.LENGTH_LONG).show();
                    ErrorMessage = (TextView) getActivity().findViewById(
                            R.id.ErrorMessageinTrack);
                    ErrorMessage.setText("TRY AFTER SOME TIME");

                }

            } catch (JSONException e) {

                Toast.makeText(getActivity(), "Try After Some Times",
                        Toast.LENGTH_LONG).show();
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            }
        }
        return isValidData;

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean jsonParsing(String response) {
        boolean isValidData = false;
        String statuschk = null;
        String tripStatus=null;
        int key = 1;
        if (response != null) {
            try {
                ErrorBtn = (Button) getActivity().findViewById(
                        R.id.ErrorTrackbtn);
                LinearLayout Error = new LinearLayout(getActivity());
                Error = (LinearLayout) getActivity().findViewById(
                        R.id.ErrorTripManageLayout);
                LinearLayout details = new LinearLayout(getActivity());
                details = (LinearLayout) getActivity().findViewById(
                        R.id.TrackDetailsLayout);
                Error.setVisibility(View.VISIBLE);
                details.setVisibility(View.GONE);

                //--------------------------------------------------------------------------------------------------------

                JSONObject jsonResponse1 = new JSONObject(response);
                // getting the data with tag d
                String jsonData1 = jsonResponse1.getString("d");

                JSONArray trackArray = new JSONArray(jsonData1);
                JSONObject status1 = trackArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("vehicle does not exist")) {
                    ErrorMessage.setText("TRY AFTER SOME TIME");
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("OK")) {

                    String[] columnNames = {"_id", "destination", "tripStatus"};
                    cursorDestination = new MatrixCursor(columnNames);

                  //  JSONObject sourceDetail = trackArray.getJSONObject(1);
                    JSONArray sourceArray = trackArray.getJSONArray(1);
                    if (sourceArray.length() != 0) {

                        JSONObject sName = sourceArray.getJSONObject(0);
                        if (!(sName.isNull("sourceName"))) {
                            SourceName = sName.getString("sourceName");
                        }

                    }


                    //JSONObject driverDetail = trackArray.getJSONObject(2);
                    JSONArray driverArray = trackArray.getJSONArray(2);
                    if (driverArray.length() != 0) {

                        JSONObject dName = driverArray.getJSONObject(0);
                        if (!(dName.isNull("driverName"))) {
                            driverName = dName.getString("driverName");
                        }

                    }


                  //  JSONObject destinationDetail = trackArray.getJSONObject(3);
                    JSONArray destinationArray = trackArray.getJSONArray(3);
                    if (destinationArray.length() != 0) {

                        for (int i = 0; i < destinationArray.length() ; i++) {

                            JSONObject dest = destinationArray.getJSONObject(i);
                            if (!(dest.isNull("destinationName"))) {
                                DestinationName = dest.getString("destinationName");
                            }

                            if (!(dest.isNull("tripStatus"))) {
                                tripStatus = dest.getString("tripStatus");
                            }

                            cursorDestination.addRow(new Object[]{key,DestinationName,tripStatus});
                            key++;

                        }
                    }



                   // JSONObject vehicleDetail = trackArray.getJSONObject(4);
                    JSONArray vehicleArray = trackArray.getJSONArray(4);
                    if (vehicleArray.length() != 0) {

                        JSONObject vStatus = vehicleArray.getJSONObject(0);
                        if (!(vStatus.isNull("vehicleStatus"))) {
                            Status = vStatus.getString("vehicleStatus");
                        }

                        //JSONObject rTime = vehicleArray.getJSONObject(1);
                        if (!(vStatus.isNull("runningTime"))) {
                            RunningTime = vStatus.getString("runningTime");
                        }

                        //JSONObject dist = vehicleArray.getJSONObject(2);
                        if (!(vStatus.isNull("distance"))) {
                            KMTravelled =vStatus.getString("distance");
                        }

                        JSONObject cLat = vehicleArray.getJSONObject(1);

                        if (!(cLat.getString("currLatitude").equals(""))) {
                            bValid = true;
                        }

                        if (!(cLat.isNull("currSpeed"))) {
                            Fuel = cLat.getString("currFuel").trim();
                        }

                        if (!(cLat.isNull("currSpeed"))) {
                            Speed = cLat.getString("currSpeed").trim();
                        }

                        if (!(cLat.isNull("tripStatus"))) {
                            tripstatus1=cLat.getString("tripStatus").trim();
                        }

                    }

                  //  JSONObject currentDetail = trackArray.getJSONObject(5);
//                    JSONArray currentArray = trackArray.getJSONArray(5);
//                    if (currentArray.length() != 0) {
//
//                        JSONObject cLat = currentArray.getJSONObject(0);
//                        if (!(cLat.getString("currLatitude").equals(""))) {
//                            bValid = true;
//                        }
//
//
//                        //JSONObject cFuel = sourceArray.getJSONObject(2);
//                        if (!(cLat.isNull("currFuel"))) {
//                            Fuel = cLat.getString("currFuel").trim();
//                        }
//
//                       // JSONObject cSpeed = sourceArray.getJSONObject(3);
//                        if (!(cLat.isNull("currSpeed"))) {
//                            Speed = cLat.getString("currSpeed").trim();
//                        }
//
//                       // JSONObject trStatus = sourceArray.getJSONObject(4);
//                        if (!(cLat.isNull("tripStatus"))) {
//                            tripstatus1=cLat.getString("tripStatus").trim();
//                        }
//
//                    }

//--------------------------------------------
                    if(tripstatus1.equals("on trip")){

                        Error.setVisibility(View.GONE);
                        details.setVisibility(View.VISIBLE);
                        isValidData = true;
                        bValid = true;

                    }


                    else if (tripstatus1.equals("off trip"))
                    {

                        ErrorMessage = (TextView) getActivity().findViewById(
                                R.id.ErrorMessageinTrack);
                        tStatus = (TextView) getActivity().findViewById(
                                R.id.ErrorStatusinTrack);
                        tSpeed = (TextView) getActivity().findViewById(
                                R.id.ErrorSpeedinTrack);

                        tStatus.setText("Status : " + Status);
                        tStatus.setVisibility(View.VISIBLE);

                        if (Status.equals("running") && !Speed.equals("")) {
                            double spd = Double.parseDouble(Speed);
                            DecimalFormat numberFormat = new DecimalFormat("##.00");

                            String sp=String.valueOf(numberFormat.format(spd));

                            tSpeed.setText("SPEED(Km/h) : " + sp);
                            tSpeed.setVisibility(View.VISIBLE);
                        }

                        ErrorMessage.setText("TRIP IS NOT YET ADDED");
                        ErrorBtn.setText("See Current Location");

                        if(bValid==false){

                            ErrorBtn.setText("OK");
                        }

                    }


                    else {
//                        Toast.makeText(getActivity(), "Try After Some Times",
//                                Toast.LENGTH_LONG).show();
                        ErrorMessage = (TextView) getActivity().findViewById(
                                R.id.ErrorMessageinTrack);
                        ErrorMessage.setText("TRY AFTER SOME TIME");

                    }




                }

            }catch (JSONException e) {
                ErrorMessage.setText("TRY AFTER SOME TIME");
                Toast.makeText(getActivity(), "Try After Some Times",
                        Toast.LENGTH_LONG).show();
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            }
        }
        return isValidData;

    }

    public void TrackingVehicleTrip(String response) {
try {
    if (jsonParsing(response)) {
        Activity view = (TrackActivity) getActivity();
        //********Intialising Views Here********

        viewMap = (Button) view.findViewById(R.id.btnViewonMap);
        tDriver = (TextView) view.findViewById(R.id.TrackDriverNametxt);
        iDriver = (ImageView) view.findViewById(R.id.trackVehImageVDriver);
        tStatus = (TextView) view.findViewById(R.id.trackTVStatus);
        tVehicle = (TextView) view.findViewById(R.id.trackTVVehNo);
        tContact = (TextView) view.findViewById(R.id.TrackDriverContacttxt);
        tSource = (TextView) view.findViewById(R.id.trackTVSource);
        tDestination = (TextView) view.findViewById(R.id.trackTVDestination);
        tFuel = (TextView) view.findViewById(R.id.trackTVFuel);
        tRunningTime = (TextView) view.findViewById(R.id.tvRunTime);
        tKmTravel = (TextView) view.findViewById(R.id.tvKMtravel);
        SpeedLay = (LinearLayout) getActivity().findViewById(R.id.laySpeed);
        distanceLay=(LinearLayout)getActivity().findViewById(R.id.laykmtravel);

        //********End****************************

        tStatus.setText(Status);
        tVehicle.setText(VehLV);
        getDriver(driverName);
        SpeedLay.setVisibility(View.GONE);
        if (Status.equals("Running") && !Speed.equals("")) {
            double spd = Double.parseDouble(Speed);
            DecimalFormat numberFormat = new DecimalFormat("##.00");
            String sp=String.valueOf(numberFormat.format(spd));
            tSpeed = (TextView) getActivity().findViewById(
                    R.id.trackTVSpeed);
            SpeedLay.setVisibility(View.VISIBLE);
            tSpeed.setText(sp);
        }
        if (!SourceName.equals("") & !DestinationName.equals("")) {
            tSource.setText(SourceName);
            tDestination.setText(DestinationName);
        } else {
            tDestination.setText("Not Available");
            tSource.setText("Not Available");
        }
        if (!Fuel.equals("0.0") && (!Fuel.equals(""))
                && (!Fuel.equals("0"))) {
            tFuel.setText(Fuel);
        }
        if (RunningTime.equals(""))
            tRunningTime.setText("Not Available");
        else
            tRunningTime.setText(RunningTime);

        if (KMTravelled.equals(""))
            tKmTravel.setText("Not Available");
        else {
            distanceLay.setVisibility(View.VISIBLE);
            tKmTravel.setText(KMTravelled);
        }
    }
}

catch(Exception e)
{ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                .toString() + " "
                + "[TrackingVehicleTrip]", e.toString());}


}

    @Override
    public void onFuelInLiters(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                cd.ConnectingToInternet();
            } else if (response.contains("refused")) {
                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity()).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else if (response.contains("java.net.SocketTimeoutException")) {

                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity()).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else {

                String result = fuelJsonParsing(response);


                if (result.equals("inserted")) {
                    tFuel = (TextView) getActivity().findViewById(R.id.trackTVFuel);
                    tFuel.setText(Fuel);
                    Fuel = "0";
                    Toast.makeText(getActivity(), "Saved Successfully....",
                            Toast.LENGTH_LONG).show();
                } else if (result.equals("fuel cannot be empty")) {
                    Toast.makeText(getActivity(), "Fuel cannot be Empty....",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),
                            "Please try after sometime....", Toast.LENGTH_LONG)
                            .show();
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString(), "Try after sometime...");

                }

            }
        }catch (Exception e) {
                if (e.toString().contains("refused")) {
                    ImageView image = new ImageView(getActivity());
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity()).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
                    builder.create().show();

                } else if (e.toString().contains("java.net.SocketTimeoutException")) {

                    ImageView image = new ImageView(getActivity());
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity()).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
                    builder.create().show();

                }
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onFuelInLiters]", e.toString());
            }
        }


    public String fuelJsonParsing(String response) {
        String jsonData = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);

                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                jsonData = d.getString("status").trim();
                return jsonData;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[alertLongPressed]", e.toString());
            }

        return jsonData;

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
           TrackActivity.pos=2;
        }
    }
}
