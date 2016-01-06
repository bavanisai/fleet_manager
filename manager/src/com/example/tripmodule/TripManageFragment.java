
package com.example.tripmodule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.Interface.IDriverNotInTrip;
import com.example.Interface.ITripManageFragment;
import com.example.Interface.IVehicleNotInTrip;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TripManageFragment extends Fragment implements OnClickListener,
        ITripManageFragment,IVehicleNotInTrip,IDriverNotInTrip {
    List val,Dval;
    String formattedDate, CleanerStr, TripVoucherId = null, Voucher, TripId;
    Button Save, AddSource, DestinationButton;//ViewDestination;
    Spinner VehicleSpinner, DriverSpinner, CleanerSpinner,
            SourceSpinner; //DestSpinner,
    List<String> CleanerList;
    List<String> Source = new ArrayList<>();
    List<String> DriverList = new ArrayList<>();
    List<String> VehicleList = new ArrayList<>();
    ProgressDialog pd;
    String selSource, selDriver, selVehicle, selCeaner,
            DriverId, CleanerId;
    //	CharSequence at = "@";
    View view;
    DBAdapter db;
    final ITripManageFragment mTripManageFragment = this;
    final IVehicleNotInTrip mVehicleNotInTrip = this;
    final IDriverNotInTrip mDriverNotInTrip=this;
    public static boolean isRunningActivity;
    // CheckBox isRegularCB;
    public static String VehicleStr, DriverStr, SourceStr;

    // @SuppressLint("SimpleDateFormat")
    @SuppressLint("SimpleDateFormat")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trip_manage, container, false);
        isRunningActivity = true;
        db = new DBAdapter(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null)
            TripVoucherId = bundle.getString("VoucherId");
        //Selecting Values For Spinner
        selCeaner = "Select The Cleaner";
        selDriver = "Select Driver";
        selVehicle = "Select Vehicle";
        selSource = "Select Source";
        //Calling All Views
        bindData();
        bindVehicleData(TripVoucherId);
        bindDriverData(null);
        loadSourceSpinnerData(null);
        loadCleanerSpinnerData(null);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        formattedDate = df.format(date);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences UserType = getActivity().getSharedPreferences(
                "RegisterName", 0);
        String UserTyp = UserType.getString("Name", "");

        if (UserTyp.equals("DEMO")) {
            try {
                Source.add(0, selSource);
                Source.add(1, "MNK");

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_spinner_item, Source);
                SourceSpinner.setAdapter(dataAdapter);

                SourceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        SourceStr = SourceSpinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onCreateView]", e.toString());
            }
        }
        AddSource.setOnClickListener(this);
        Save.setOnClickListener(this);
        DestinationButton.setOnClickListener(this);

    }

    /*
     * Purpose - Binds XMl Id reference to java Method Name - bindData()
	 * Parameters - No parameters Return Type - No Return Type
	 */

    private void bindData() {
        VehicleSpinner = (Spinner) view.findViewById(R.id.fragmentTripManageVehicleSpinner);
        DriverSpinner = (Spinner) view.findViewById(R.id.fragmentTripManageDriverSpinner);
        CleanerSpinner = (Spinner) view.findViewById(R.id.fragmentTripManageCleanerSpinnerTrip);
        Save = (Button) view.findViewById(R.id.fragmentTripManageBtnSave);
        AddSource = (Button) view.findViewById(R.id.fragmentTripManageBtnAddSource);
        SourceSpinner = (Spinner) view.findViewById(R.id.fragmentTripManageSourceSpinner);
        DestinationButton = (Button) view.findViewById(R.id.fragmentTripManageBtnDestination);
    }


    @SuppressLint("SimpleDateFormat")
    private void getSetData() {
        try {
            VehicleStr = String.valueOf(VehicleSpinner.getSelectedItem());
            DriverStr = String.valueOf(DriverSpinner.getSelectedItem());
            CleanerStr = String.valueOf(CleanerSpinner.getSelectedItem());
            SourceStr = String.valueOf(SourceSpinner.getSelectedItem());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[getSetData]", e.toString());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragmentTripManageBtnAddSource:
                Intent intent1 = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent1);
                break;
            case R.id.fragmentTripManageBtnDestination:
                try {
                    getSetData();
                    if (VehicleStr != selVehicle && DriverStr != selDriver && SourceStr != selSource) {
                        Intent intent2 = new Intent(getActivity(), MultipleDestinationActivity.class);
                        intent2.putExtra("vehNo", VehicleStr);
                        intent2.putExtra("driv", DriverStr);
                        intent2.putExtra("src", SourceStr);
                        intent2.putExtra("clnr", CleanerStr);
                        startActivity(intent2);
                    } else {
                        Toast.makeText(getActivity(),
                                "Please Select all Values", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[fragmentTripManageBtnDestination button click]", e.toString());
                }
                break;
            case R.id.fragmentTripManageBtnSave:
                TripData();
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void TripData() {
        try {
            String a = null;
            getSetData();
            DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd hhmmss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            Voucher = dateFormatter.format(today);
            db.open();
            DriverId = db.checkDrvierTableforDataExist(DriverStr.toUpperCase());

            if (CleanerStr == "SELECT THE CLEANER") {
                CleanerId = "0";
            } else {
                CleanerId = db.checkCleanerTableforDataExist(CleanerStr.toUpperCase());
            }
            db.close();
            // Checking All input values are correct or What?
            if (VehicleStr != selVehicle && DriverStr != selDriver
                    && SourceStr != selSource) {
                SendToWebService send = new SendToWebService(getActivity(), mTripManageFragment);


                //   a=MultipleDestinationActivity.arrayList.toString();

                try {
                    JSONObject subTripObject = getJsonObjectData();
                    a = subTripObject.toString();
                } catch (JSONException e) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[TripData()]", e.toString());
                    e.printStackTrace();
                }
                try {
                    send.execute("11", "saveTripDetails",
                            DriverId, VehicleStr, CleanerId, SourceStr,
                            Voucher, formattedDate, a);
//                         send.execute("11", "SaveSourceDestinationDetails",
//                                 "2", "KA001", "4", "MNK",
//                                "Trip001", formattedDate,"MNKqwertyuiop","4");

                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString() + " " + "[TripData]", e.toString());
                }
            } else {
                Toast.makeText(getActivity().getBaseContext(),
                        "Please Select all Values", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[TripData]", e.toString());
        }

    }

    private void saveTripData(String parsedValue) {
        try {
            if (parsedValue.equals("inserted")) {

                Toast.makeText(getActivity().getApplicationContext(),
                        "Data Saved Successfully", Toast.LENGTH_SHORT)
                        .show();

            } else if (parsedValue.equals("vehicle already in trip")) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Vehicle Already in trip...",
                        Toast.LENGTH_SHORT).show();
            } else if (parsedValue.equals("driver already in trip")) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Driver Already in trip...",
                        Toast.LENGTH_SHORT).show();
            } else {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), parsedValue);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry Trip Not Added... Try After SomeTimes",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SaveTripData]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SAveTripData]", e.toString());
        }
        refreshActivity();
    }

    @SuppressLint("ShowToast")
    private void loadSourceSpinnerData(Object object) {

        try {
            db.open();
            Source = db.getLocationForSpinner("Source");
            Source.add(0, selSource);
            // Source.add(1, "Mnk Office");

            db.close();
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item, Source);

            // Drop down layout style - list view with radio button
            //  dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            SourceSpinner.setAdapter(dataAdapter);

            if (Source.contains(object) && object != null) {
                int position = Source.indexOf(object);
                SourceSpinner.setSelection(position);
            }

            SourceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    SourceStr = SourceSpinner.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[loadSourceSpinnerData]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[loadSourceSpinnerData]", e.toString());
        }
    }


    @SuppressLint("ShowToast")
    private void bindDriverData(Object object) {
        try {
             if(DriverList.size()==0) {
                 DriverList.add(0, selDriver);
                 // Creating adapter for spinner
                 ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                         getActivity(), android.R.layout.simple_spinner_item,
                         DriverList);

                 // attaching data adapter to spinner
                 DriverSpinner.setAdapter(dataAdapter);
             }

            if (DriverList.contains(object) && object != null) {
                int position = DriverList.indexOf(object);
                DriverSpinner.setSelection(position);
            }

            //on click of spinner only data should load
            View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            if(Dval==null) {

                                SendToWebService send = new SendToWebService(getActivity(), mDriverNotInTrip);
                                send.execute("46", "GetDriversNotInTrip");
                            }
                        } catch (Exception e) {
                            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[loadVehicleSpinnerData]", e.toString());
                        }

                    }
                    return false;
                }

            };
           DriverSpinner.setOnTouchListener(spinnerOnTouch);

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[bindDriverSpinnerData]", e.toString());
        }
    }


    @SuppressLint("ShowToast")
    private void bindVehicleData(Object object)
    {
        try {
            if(VehicleList.size()==0) {
                // Spinner Drop down elements
                VehicleList.add(0, selVehicle);
                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                        getActivity(), android.R.layout.simple_spinner_item,
                        VehicleList);
                // attaching data adapter to spinner
                VehicleSpinner.setAdapter(dataAdapter);
            }

            if (VehicleList.contains(object) && object != null)
            {
                int position = VehicleList.indexOf(object);
                VehicleSpinner.setSelection(position);
            }

            //on click of spinner only data should load
            View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        try {
                            if(val==null) {
                                SendToWebService send = new SendToWebService(getActivity(), mVehicleNotInTrip);
                                send.execute("47", "GetVehiclesNotInTrip");

                            }

                        } catch (Exception e) {

                            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[loadVehicleSpinnerData]", e.toString());
                 }

                    }
                    return false;
                }

            };
            VehicleSpinner.setOnTouchListener(spinnerOnTouch);
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[loadVehicleSpinnerData]", e.toString());
        }

    }

    @SuppressLint("ShowToast")
    private void loadCleanerSpinnerData(Object object) {
        try {
            db.open();
            // Spinner Drop down elements
            CleanerList = db.getAllLabels(DBAdapter.getEmployeeDetails(),
                    "Cleaner");
            CleanerList.add(0, selCeaner);
            // Creating adapter for spinner
            db.close();
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    CleanerList);
            // Drop down layout style - list view with radio button
            // dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            CleanerSpinner.setAdapter(dataAdapter);

            if (CleanerList.contains(object) && object != null) {
                int position = CleanerList.indexOf(object);
                CleanerSpinner.setSelection(position);
            }


            CleanerSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    CleanerStr = CleanerSpinner.getSelectedItem().toString();


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[loadCleanerSpinnerData]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[loadCleanerSpinnerData]", e.toString());
        }
    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), TripActivity.class);
        startActivity(intent);

    }

    @Override
    public void onSaveSourceDestinationDetails(String response) {
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

                String parsedValue = jsonParsing1(response);
                saveTripData(parsedValue);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onSaveSourceDestinationDetails]", e.toString());
        }

    }

    public String jsonParsing1(String response) {

        String jsonData = null;
        String status = null;

        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                status = d.getString("status");
                //  TripId=d.getString("tripId");
                return status;

            } catch (JSONException e) {
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
            }
        return status;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            SharedPreferences UserType = getActivity().getSharedPreferences(
                    "RegisterName", 0);
            String UserTyp = UserType.getString("Name", "");
            loadSourceSpinnerData(SourceStr);///LatestTrip.getString("Source", null));

            isRunningActivity = true;
            if (MultipleDestinationActivity.arrayList.size() != 0) {
                DestinationButton.setEnabled(false);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onStart()]", e.toString());
        }

    }


    @Override
    public void onStop() {

        super.onStop();
        isRunningActivity = false;
        MultipleDestinationActivity.arrayList.clear();
    }

    public JSONObject getJsonObjectData() throws JSONException {
        int rowCount = MultipleDestinationActivity.arrayList.size();
        JSONObject obj = null;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < rowCount; i++) {

            String rowValue = MultipleDestinationActivity.arrayList.get(i).toString();
            String[] arr = rowValue.split(",");
            obj = new JSONObject();
            try {
                obj.put("vehicleNumber", arr[0]);
                obj.put("product", arr[1]);
                obj.put("quantity", arr[2]);
                obj.put("destination", arr[3]);
                obj.put("distance", arr[4]);
                obj.put("route", arr[5]);
                obj.put("amount", arr[6]);
                obj.put("paymentType", arr[7]);
                obj.put("paymentKm", arr[8]);
                obj.put("sourceName", arr[9]);
                obj.put("tripOrder", arr[10]);
                obj.put("subVoucher", arr[11]);

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[getJsonObjectData()]", e.toString());
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }

        JSONObject finalobject = new JSONObject();
        finalobject.put("subTrip", jsonArray);
        return finalobject;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {
                TripActivity.pos = 2;
            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[setUserVisibleHint]", e.toString());
        }
    }

    //vehicle list which are not in trip
    //live data from server
    @Override
    public void onVehicleData(String response) {
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

            } else  {
                String jsonData = null;
                String status = null;
                if (response != null) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        jsonData = obj.getString("d");
                        JSONArray arr = new JSONArray(jsonData);
                        JSONObject d = arr.getJSONObject(0);
                        status = d.getString("status");

                        if (status.equals("OK")) {

                            JSONObject o = arr.getJSONObject(1);
                            JSONArray vehicle = o.getJSONArray("vehicles");
                            for (int i = 0; i < vehicle.length(); i++) {
                                VehicleList.add(i + 1, vehicle.get(i).toString().toUpperCase());
                                val=VehicleList;
                            }
                            //binding server data to spinner
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                                        getActivity(), android.R.layout.simple_spinner_item,
                                        val);
                                //refresh the spinner
                                dataAdapter.notifyDataSetChanged();
                                // attaching data adapter to spinner
                                VehicleSpinner.setAdapter(dataAdapter);

                                VehicleSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int arg2, long arg3) {
                                        VehicleStr = VehicleSpinner.getSelectedItem().toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                          //  }


                        }
                    } catch (Exception e) {
                        ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                .toString() + " " + "[onVehicleNotInTrip]", e.toString());
                    }
                }
            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onSaveSourceDestinationDetails]", e.toString());
        }
    }
    //driver list which are not in trip
    //live data from server
    @Override
    public void onDriverData(String response) {
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
                String jsonData = null;
                String status = null;
                if (response != null) {
                    try {
                        JSONObject obj = new JSONObject(response);
                            jsonData = obj.getString("d");
                            JSONArray arr = new JSONArray(jsonData);
                            JSONObject d = arr.getJSONObject(0);
                            status = d.getString("status");

                            if (status.equals("OK")) {

                                JSONObject o = arr.getJSONObject(1);
                                JSONArray driver = o.getJSONArray("drivers");
                                for (int i = 0; i < driver.length(); i++) {
                                    DriverList.add(i + 1, driver.get(i).toString().toUpperCase());
                                    Dval=DriverList;
                                }

                            //binding server data to spinner
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                                    getActivity(), android.R.layout.simple_spinner_item,
                                    Dval);
                            //refresh the spinner
                                dataAdapter.notifyDataSetChanged();
                            // attaching data adapter to spinner
                           DriverSpinner.setAdapter(dataAdapter);

                                DriverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                               int arg2, long arg3) {
                                        DriverStr = DriverSpinner.getSelectedItem().toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                        }
                    } catch (Exception e) {
                        ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                .toString() + " " + "[onVehicleNotInTrip]", e.toString());
                    }
                }
            }
        }catch (Exception e)
        {

        }
    }
}
