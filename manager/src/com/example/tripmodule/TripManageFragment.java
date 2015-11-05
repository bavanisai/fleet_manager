
package com.example.tripmodule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.Interface.ITripManageFragment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

public class TripManageFragment extends Fragment implements OnClickListener,
        ITripManageFragment{

    String  formattedDate, CleanerStr, TripVoucherId = null, Voucher,TripId;
    Button Save, AddDest, AddSource,DestinationButton;//ViewDestination;
    Spinner VehicleSpinner, DriverSpinner, CleanerSpinner,
            SourceSpinner; //DestSpinner,
    List<String> Destination,  CleanerList;
    List<String> Source=new ArrayList<>();
    List<String>  DriverList=new ArrayList<>();
    List<String> VehicleList=new ArrayList<>();
    ListView trip;
    String regid = "", selDest, selSource, selDriver, selVehicle, selCeaner,
            DriverId, CleanerId;
    //	CharSequence at = "@";
    Context context;
    View view;
    DBAdapter db;
    final ITripManageFragment mTripManageFragment = this;
    public static boolean isRunningActivity;
   // CheckBox isRegularCB;
    Boolean isRegular;
    int selPosition;
    public static String VehicleStr, DriverStr, DestStr,SourceStr;

    // @SuppressLint("SimpleDateFormat")
    @SuppressLint("SimpleDateFormat")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null)
            TripVoucherId = bundle.getString("VoucherId");
        view = inflater.inflate(R.layout.fragment_trip_manage, container, false);
        isRunningActivity = true;
        db = new DBAdapter(getActivity());

        //Selecting Values For Spinner
        selCeaner = "Select The Cleaner";
        selDriver = "Select Driver";
        selVehicle = "Select Vehicle";
       // selDest = "SELECT DESTINATION";
        selSource = "Select Source";
        //Calling All Views
        bindData();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        formattedDate = df.format(date);

        loadCleanerSpinnerData(null);
        //Pravitha commented code
        //loadDestSpinnerData(null);
        loadVehicleSpinnerData(TripVoucherId);
        loadDriverSpinnerData(null);

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

                        // selPosition=(int)SourceSpinner.getSelectedItemId();


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

            else{
                loadSourceSpinnerData(null);
            }




        AddSource.setOnClickListener(this);
        Save.setOnClickListener(this);
        DestinationButton.setOnClickListener(this);
        return view;
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
       // DestSpinner = (Spinner) view.findViewById(R.id.DestinationSpinner);
        DestinationButton=(Button)view.findViewById(R.id.fragmentTripManageBtnDestination);
       // ViewDestination=(Button)view.findViewById(R.id.fragmentTripManageBtnDestinationView);
       // isRegularCB = (CheckBox) view.findViewById(R.id.IsRegularCheckBox);

    }

    private void attachSpinnerValues(String vehicle, String dri, String cle, String source, String dest,
                                     boolean isTrue) {
        if (VehicleList.contains(vehicle) && vehicle != null) {
            int pos = VehicleList.indexOf(vehicle);
            VehicleSpinner.setSelection(pos);
        }
        if (DriverList.contains(dri) && dri != null) {
            int pos = DriverList.indexOf(dri);
            DriverSpinner.setSelection(pos);
        }

        if (CleanerList.contains(cle) && cle != null) {
            int pos = CleanerList.indexOf(cle);
            CleanerSpinner.setSelection(pos);
        }

//        if (Destination.contains(dest) && dest != null) {
//            int pos = Destination.indexOf(dest);
//            DestSpinner.setSelection(pos);
//        }

        if (Source.contains(source) && source != null) {
            int pos = Source.indexOf(source);
            SourceSpinner.setSelection(pos);
        }
        //isRegularCB.setChecked(isTrue);
    }

    @SuppressLint("SimpleDateFormat")
    private void getSetData() {
        try {
            VehicleStr = String.valueOf(VehicleSpinner.getSelectedItem());
            DriverStr = String.valueOf(DriverSpinner.getSelectedItem());
            // DestStr = String.valueOf(DestSpinner.getSelectedItem());
            CleanerStr = String.valueOf(CleanerSpinner.getSelectedItem());
            SourceStr = String.valueOf(SourceSpinner.getSelectedItem());
            // isRegular = isRegularCB.isChecked();
            //    IsReg = isRegular.toString();
        }
        catch (Exception e){
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
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[fragmentTripManageBtnDestination button click]", e.toString());
                }
                break;
//             case R.id.fragmentTripManageBtnDestinationView:
//                 if (VehicleStr != selVehicle && DriverStr != selDriver && SourceStr != selSource) {
//                     Intent intent2=new Intent(getActivity(),TripDetailsActivity.class);
////                     intent2.putExtra("vehNo",VehicleStr);
////                     intent2.putExtra("driv",DriverStr);
////                     intent2.putExtra("src",SourceStr);
////                     intent2.putExtra("clnr", CleanerStr);
//                     intent2.putExtra("vehNo","KA001");
//                     intent2.putExtra("driv","DARISH");
//                     intent2.putExtra("src","MNK");
//                     intent2.putExtra("clnr", "HARI");
//                     startActivity(intent2);
//                 }

           //      break;

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
//             if(MultipleDestinationActivity.arrayList.size()!=0)
//             {
                SendToWebService send = new SendToWebService(getActivity(),
                        mTripManageFragment);


                //   a=MultipleDestinationActivity.arrayList.toString();

                try {
                    JSONObject subTripObject = getJsonObjectData();
                    a = subTripObject.toString();
                } catch (JSONException e)
                {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[TripData()]", e.toString());
                    e.printStackTrace();
                }

//            if (send.isConnectingToInternet()) {
                try {
                    send.execute("11", "saveTripDetails",
                            DriverId, VehicleStr, CleanerId, SourceStr,
                            Voucher, formattedDate, a);
//                         send.execute("11", "SaveSourceDestinationDetails",
//                                 "2", "KA001", "4", "MNK",
//                                "Trip001", formattedDate,"MNKqwertyuiop","4");

                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString(), e.toString());
                }
//                 } else {
//                     Toast.makeText(getActivity().getBaseContext(),
//                             "INTERNET CONNECTION ERROR!! PLEASE CHECK NETWORK",
//                             Toast.LENGTH_SHORT).show();
//                 }
//             }
//            else{
//                 Toast.makeText(getActivity().getBaseContext(),
//                         "Please Add minimum 1 Destination", Toast.LENGTH_SHORT).show();
//             }
            } else {
                Toast.makeText(getActivity().getBaseContext(),
                        "Please Select all Values", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[TripData]", e.toString());
        }

    }

    private void saveTripData(String parsedValue) {
        try {


             //   bindData();
             //   getSetData();
//                //addToSharedPreference();
//                db.open();
//                ContentValues cv = new ContentValues();
//                cv.put(DBAdapter.getKeyDate(), formattedDate);
//                cv.put(DBAdapter.getKeyVehicleNo(), VehicleStr);
//                cv.put(DBAdapter.getKeyDriver(), DriverStr);
//                cv.put(DBAdapter.getKeyCleaner(), CleanerStr);
//                cv.put(DBAdapter.getKeyDestination(), DestStr);
//                cv.put(DBAdapter.getKeySource(), SourceStr);
//                cv.put(DBAdapter.getKeyVoucherNo(), Voucher);
//                cv.put(DBAdapter.getKeyIsregular(), IsReg);
//                cv.put(DBAdapter.getKeyTripstatus(), "true");
//                long id = db
//                        .insertTripDetails(cv);
//                db.close();
//                ContentValues cv1=new ContentValues();
//                int jj;
//                String firstRow =MultipleDestinationActivity.arrayList.get(0).toString();
//                String[] firstRowArr=firstRow.split(",");
//                for(jj=0;jj<firstRowArr.length;jj++)
//                {
//                    cv1.put(DBAdapter.getKeyDate(), formattedDate);
//                    cv1.put(DBAdapter.getKeyTripId(),TripId);
//                    cv1.put(DBAdapter.getKeyVehicleNo(), VehicleStr);
//                    cv1.put(DBAdapter.getKeyDriver(), DriverStr);
//                    cv1.put(DBAdapter.getKeyCleaner(), CleanerStr);
//                    cv1.put(DBAdapter.getKeyDestination(), firstRowArr[3]);
//                    cv1.put(DBAdapter.getKeySource(), SourceStr);
//                    cv1.put(DBAdapter.getKeyVoucherNo(), Voucher);
//                    cv1.put(DBAdapter.getKeyIsregular(), "false");
//                    cv1.put(DBAdapter.getKeyTripstatus(), "true");
//                    long id=db.insertTripDetails(cv1);
//
//                }


//                int i,j;
//                ContentValues cv=new ContentValues();
//                for ( i=0;i<MultipleDestinationActivity.arrayList.size();i++)
//                {
//
//                    String s =MultipleDestinationActivity.arrayList.get(i).toString();
//                    String[] arr=s.split(",");
//                    for(j=0;j<arr.length;j++)
//                    {
//                        cv.put(DBAdapter.getKeyTripid(),arr[0]);
//                        cv.put(DBAdapter.getKeyLastdestination(),arr[1]);
//                        cv.put(DBAdapter.getKeyProduct(),arr[2]);
//                        cv.put(DBAdapter.getKeyQuantity(),arr[3]);
//                        cv.put(DBAdapter.getKeyDestname(),arr[4]);
//                        cv.put(DBAdapter.getKeyDestdistance(),arr[5]);
//                        cv.put(DBAdapter.getKeyRoutename(),arr[6]);
//                        cv.put(DBAdapter.getKeyRent(),arr[7]);
//                        cv.put(DBAdapter.getKeyType(),arr[8]);
//                        cv.put(DBAdapter.getKeyPaymentValue(),arr[9]);
//                    }
//                    db.open();
//                    long id=db.insertRows(cv,DBAdapter.gettMultidestination());
//                    db.close();
//                    Toast.makeText(getActivity(), "after spliting (cv) =" + cv, Toast.LENGTH_LONG).show();
//                    Toast.makeText(getActivity(), "single row=" + s, Toast.LENGTH_LONG).show();
                  //  if (id != -1) {

                  //  }
              //  }

            if (parsedValue.equals("inserted")) {

                Toast.makeText(getActivity().getApplicationContext(),
                        "Data Saved Successfully", Toast.LENGTH_SHORT)
                        .show();

            } else if (parsedValue.equals("vehicle already in trip")) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Vehicle Already in trip...",
                        Toast.LENGTH_SHORT).show();
            }
            else if (parsedValue.equals("driver already in trip")) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Driver Already in trip...",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), parsedValue);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sorry Trip Not Added... Try After SomeTimes",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
        refreshActivity();
    }

    //	private void addToSharedPreference() {
    //		SharedPreferences LatestTrip = getActivity().getSharedPreferences("LatestTrip",
    //getActivity().MODE_PRIVATE);
    //		SharedPreferences.Editor edit = LatestTrip.edit();
    //		edit.putString("Vehicle", VehicleStr);
    //		edit.putString("Driver", DriverStr);
    //		edit.putString("Cleaner", CleanerStr);
    //		edit.putString("Source", SourceStr);
    //		edit.putString("Dest", DestStr);
    //		edit.commit();
    //	}

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

                   // selPosition=(int)SourceSpinner.getSelectedItemId();


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//            if (Source.contains(object) && object != null) {
//                int position = Source.indexOf(object);
//                SourceSpinner.setSelection(position);
//            }
//            // Set the ClickListener for Spinner
//            SourceSpinner
//                    .setOnItemSelectedListener(new OnItemSelectedListener() {
//
//                        public void onItemSelected(AdapterView<?> adapterView,
//                                                   View view, int i, long l) {
//
//                        }
//
//                        // If no option selected
//                        public void onNothingSelected(AdapterView<?> arg0) {
//                        }
//                    });
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
    }

    @SuppressLint("ShowToast")
    private void loadDriverSpinnerData(Object object) {
        try {

                    String jsonData = null;
                    String status = null;
                    DriverList.add(0, selDriver);
                     SendToWebService send = new SendToWebService(getActivity(), 1);
                     String response=send.execute("46","GetDriversNotInTrip").get();
                     if(response!=null){

                         JSONObject obj=new JSONObject(response);
                         jsonData=obj.getString("d");
                         JSONArray arr=new JSONArray(jsonData);
                         JSONObject d = arr.getJSONObject(0);
                         status=d.getString("status");

                         if(status.equals("OK")){

                             JSONObject o=arr.getJSONObject(1);
                             JSONArray driver=o.getJSONArray("drivers");
                             for(int i=0;i<driver.length();i++){
                                 DriverList.add(i+1,driver.get(i).toString().toUpperCase());
                             }

                         }

                     }

//            db.open();
//            DriverList = db.getAllLabels(DBAdapter.getEmployeeDetails(),
//                    "Driver");
//            DriverList.add(0, selDriver);
//            db.close();
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    DriverList);

            // Drop down layout style - list view with radio button
       //     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            DriverSpinner.setAdapter(dataAdapter);

            if (DriverList.contains(object) && object != null) {
                int position = DriverList.indexOf(object);
                DriverSpinner.setSelection(position);
            }



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

        } catch (Exception e) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    DriverList);

            // Drop down layout style - list view with radio button
            dataAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            DriverSpinner.setAdapter(dataAdapter);

            if (DriverList.contains(object) && object != null) {
                int position = DriverList.indexOf(object);
                DriverSpinner.setSelection(position);
            }
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
    }

    @SuppressLint("ShowToast")
    private void loadVehicleSpinnerData(Object object) {
        try {
//            db.open();
//            // Spinner Drop down elements
//            VehicleList = db.getAllLabels(DBAdapter.getVehicleDetails());

            String jsonData = null;
            String status = null;
            VehicleList.add(0, selVehicle);

            SendToWebService send1 = new SendToWebService(getActivity(), 1);
            String response=send1.execute("46","GetVehiclesNotInTrip").get();
            if(response!=null){

                JSONObject obj=new JSONObject(response);
                jsonData=obj.getString("d");
                JSONArray arr=new JSONArray(jsonData);
                JSONObject d = arr.getJSONObject(0);
                status=d.getString("status");

                if(status.equals("OK")){

                    JSONObject o=arr.getJSONObject(1);
                    JSONArray vehicle=o.getJSONArray("vehicles");
                    for(int i=0;i<vehicle.length();i++){
                        VehicleList.add(i+1,vehicle.get(i).toString().toUpperCase());
                    }

                }

            }


//            VehicleList.add(0, selVehicle);
//            db.close();

            // Creating adapter for spinner

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    VehicleList);
            // Drop down layout style - list view with radio button
           // dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            VehicleSpinner.setAdapter(dataAdapter);
            // VehicleList.contains(object) && !
            if (VehicleList.contains(object) && object != null) {
                int position = VehicleList.indexOf(object);
                VehicleSpinner.setSelection(position);
            }




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

        }  catch (Exception e) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    VehicleList);
            // Drop down layout style - list view with radio button
       //     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            VehicleSpinner.setAdapter(dataAdapter);
            // VehicleList.contains(object) && !
            if (VehicleList.contains(object) && object != null) {
                int position = VehicleList.indexOf(object);
                VehicleSpinner.setSelection(position);
            }
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
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
                    .toString(), e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
    }

//    private void loadDestSpinnerData(Object object) {
//        try {
//            db.open();
//            Destination = db.getLocationForSpinner("DESTINATION");
//            Destination.add(0, selDest);
//            // Destination.add(1,"Maintenance");
//            db.close();
//
//            // Creating adapter for spinner
//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
//                    getActivity(), android.R.layout.simple_spinner_item,
//                    Destination);
//
//            // Drop down layout style - list view with radio button
//            dataAdapter
//                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//            // attaching data adapter to spinner
//            DestSpinner.setAdapter(dataAdapter);
//
//            if (Destination.contains(object) && object != null) {
//                int position = Destination.indexOf(object);
//                DestSpinner.setSelection(position);
//            }
//
//        } catch (SQLiteException e) {
//            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
//                    .toString(), e.toString());
//        } catch (Exception e) {
//            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
//                    .toString(), e.toString());
//        }
//    }

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
                    .toString(), e.toString());
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
        //SharedPreferences LatestTrip = getActivity().getSharedPreferences("LatestTrip",
        //getActivity().MODE_PRIVATE);
        //Pravitha commented code
        //loadDestSpinnerData(null);//LatestTrip.getString("Dest", null));
    try {
        SharedPreferences UserType = getActivity().getSharedPreferences(
                "RegisterName", 0);
        String UserTyp = UserType.getString("Name", "");

        if (UserTyp.equals("DEMO")) {

        } else {
            loadSourceSpinnerData(SourceStr);///LatestTrip.getString("Source", null));
        }
        isRunningActivity = true;
        if (MultipleDestinationActivity.arrayList.size() != 0) {
            DestinationButton.setEnabled(false);
        }
    }
    catch (Exception e){
        ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                .toString(), e.toString());
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

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position,
//                               long id) {
//        String selectedItem = parent.getItemAtPosition(position).toString();
//        db.open();
//        boolean check;
//        switch (parent.getId()) {
//            case R.id.fragmentTripManageVehicleSpinner:
//                if (!selectedItem.equals(selVehicle)) {
//                    check = db.checkVehicleInTrip(selectedItem, true);
//                    if (check) {
//                        Cursor c = db.getParticularTripDetails(selectedItem);
//                        if (c.getCount() > 0) {
//                            c.moveToFirst();
//                            String Dri = c.getString(c.getColumnIndex(DBAdapter.getKeyDriver()));
//                            String cle = c.getString(c.getColumnIndex(DBAdapter.getKeyCleaner()));
//                            String dest = c.getString(c.getColumnIndex(DBAdapter.
//                                    getKeyDestination()));
//                            String source = c.getString(c.getColumnIndex(DBAdapter.
//                                    getKeySource()));
//                            attachSpinnerValues(selectedItem, Dri, cle, source, dest, true);
//                        }
//                    } else {
//                        Toast.makeText(getActivity(), "Sorry Vehicle Is Already In Trip",
//                                Toast.LENGTH_SHORT).show();
//                        attachSpinnerValues(selVehicle, selDriver, selCeaner, selSource, selDest, false);
//                    }
//                }
//                break;
//            case R.id.fragmentTripManageDriverSpinner:
//                if (!selectedItem.equals(selDriver)) {
//                    check = db.checkVehicleInTrip(selectedItem, false);
//                    if (!check) {
//                        Toast.makeText(getActivity(), "Sorry Driver Is Already In Trip",
//                                Toast.LENGTH_SHORT).show();
//                        attachSpinnerValues(null, selDriver, selCeaner, selSource, selDest, false);
////					loadDriverSpinnerData(null);
//                    }
//                }
//                break;
//        }
//    }

//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
@Override
public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    try {
        if (isVisibleToUser) {
            TripActivity.pos = 1;
        }
    }
    catch (Exception e){
        ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                .toString() + " " + "[setUserVisibleHint]", e.toString());
    }
}
}
