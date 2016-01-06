/**
 *
 */
package com.example.ManageResources;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IGetImeiNumbers;
import com.example.Interface.IManageResourcesVehicle;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddVehicle extends Fragment implements IManageResourcesVehicle,View.OnTouchListener
                     ,IGetImeiNumbers {
    IGetImeiNumbers mGetImeiNumbers=this;
    List deviceCount;
    DBAdapter db;
    private ArrayList<String> vehicleType;
    Spinner spinnerVehicleType, spinnerVehicleImeiNo;
    EditText manVehVNoEt, manVehMileage;
    int val;
    String prevImei;
    MatrixCursor addVehicleCursor = null;
    String statuschk = null;
    int sKey = 1;
    Button manVBtnSave;
    String VehNo, sImei, sVehPhNo, vehMileage, typeOfVehicle,
            srvrStatus, srvrVehicleId, srvrIMEINumber, srvrMobileNumber, sProtocol;
    ContentValues cv = new ContentValues();
    IManageResourcesVehicle mInterfaceManageResourcesVehicle = this;
    String VehicleNumber = null;
    List<String> lables1 = new ArrayList<>();
    ArrayAdapter<String> imeiDataAdapter;
    TextView message, ok, cancel;
    public static MatrixCursor imeiList = null;
    String clientName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_entry_new_vehicle,
                container, false);
        vehicleType = new ArrayList<String>();

        manVehVNoEt = (EditText) view
                .findViewById(R.id.EtfragmentAddVehicleVehNumber);
        manVehMileage = (EditText) view
                .findViewById(R.id.EtfragmentAddVehicleMileage);
        spinnerVehicleImeiNo = (Spinner) view
                .findViewById(R.id.SpinnerfragmentAddVehicleVehImei);
        manVBtnSave = (Button) view
                .findViewById(R.id.btnFragmentNewEntryVehicleListSave);

        spinnerVehicleType = (Spinner) view.findViewById(R.id.SpinnerfragmentAddVehicleVehType);

        //bind imei number to spinner
        bindImeiToSpinner();

        manVBtnSave.setText("ADD");
        Bundle savedData = this.getArguments();
        if (savedData != null) {
            VehicleNumber = savedData.getString("Response");
            try {

                DBAdapter db = new DBAdapter(getActivity());
                db.open();

                Cursor cursor = db.getVehicleEntry(VehicleNumber);
                if (cursor.moveToFirst()) {
                    // update view
                    manVehVNoEt.setText(cursor.getString(cursor
                            .getColumnIndex(DBAdapter.getKeyVehicleNo())));

                    if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Car")) {
                        spinnerVehicleType.setSelection(1);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Mini Truck")) {
                        spinnerVehicleType.setSelection(2);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Truck")) {
                        spinnerVehicleType.setSelection(3);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Bus")) {
                        spinnerVehicleType.setSelection(4);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("School Bus")) {
                        spinnerVehicleType.setSelection(5);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Mini Bus")) {
                        spinnerVehicleType.setSelection(6);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Trailer")) {
                        spinnerVehicleType.setSelection(7);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Van")) {
                        spinnerVehicleType.setSelection(8);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Cab")) {
                        spinnerVehicleType.setSelection(9);
                    } else if (cursor.getString(
                            cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                            .equals("Tanker")) {
                        spinnerVehicleType.setSelection(10);
                    }

                    manVehMileage.setText(cursor.getString(cursor
                            .getColumnIndex(DBAdapter.getKeyVehMileage())));

                    imeiDataAdapter.add(cursor.getString(cursor
                            .getColumnIndex(DBAdapter.getKeyImei())));
                    imeiDataAdapter.notifyDataSetChanged();

                    prevImei = cursor.getString(cursor.getColumnIndex(DBAdapter.getKeyImei()));
                    int position = lables1.indexOf(cursor.getString(cursor
                            .getColumnIndex(DBAdapter.getKeyImei())));
                    spinnerVehicleImeiNo.setSelection(position);

                    sVehPhNo = cursor.getString(cursor
                            .getColumnIndex(DBAdapter.getKeyVehPhNo()));

                    cursor.close();
                    db.close();
                }
            } catch (SQLiteException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onCreateView]", e.toString());
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onCreateView]", e.toString());
            }

        }
        vehicleType.add("Select Vehicle Type");
        vehicleType.add("Car");
        vehicleType.add("Mini Truck");
        vehicleType.add("Truck");
        vehicleType.add("Bus");
        vehicleType.add("School Bus");
        vehicleType.add("Mini Bus");
        vehicleType.add("Trailer");
        vehicleType.add("Van");
        vehicleType.add("Cab");
        vehicleType.add("Tanker");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vehicleType);
        //   arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the Adapter
        spinnerVehicleType.setAdapter(arrayAdapter);
        // Setting vehicle type
        spinnerVehicleType
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        typeOfVehicle = vehicleType.get(arg2);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

        manVBtnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).refresh(0);
                if (manVBtnSave.getText().toString().equals("ADD")) {
                    VehNo = manVehVNoEt.getText().toString()
                            .toUpperCase(Locale.getDefault());
                    sImei = spinnerVehicleImeiNo.getSelectedItem().toString();
                    getCursorData();
                    vehMileage = manVehMileage.getText().toString();
                    typeOfVehicle = spinnerVehicleType.getSelectedItem().toString();
                    if (VehNo.equals("")) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE VEHICLE NUMBER",
                                Toast.LENGTH_LONG).show();
                    } else if ((typeOfVehicle.equals("Select Vehicle Type"))) {
                        Toast.makeText(getActivity(),
                                "PLEASE SELECT THE VEHICLE TYPE",
                                Toast.LENGTH_LONG).show();
                    } else if ((vehMileage.equals(""))) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE MILEAGE", Toast.LENGTH_LONG)
                                .show();
                    } else if ((sImei.equals("Select Imei Number"))) {
                        Toast.makeText(getActivity(), "PLEASE SELECT THE IMEI",
                                Toast.LENGTH_LONG).show();
                    } else if (sVehPhNo.equals("")) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE VEHICLE PHONE NUMBER",
                                Toast.LENGTH_LONG).show();
                    } else {
                        DBAdapter db = new DBAdapter(getActivity());
                        db.open();
                        //
                        cv.put(DBAdapter.getKeyVehicleNo(), VehNo);
                        cv.put(DBAdapter.getKeyVehType(), typeOfVehicle);
                        cv.put(DBAdapter.getKeyVehMileage(), vehMileage);
                        cv.put(DBAdapter.getKeyImei(), sImei);
                        cv.put(DBAdapter.getKeyVehPhNo(), sVehPhNo);

                        try {

                            new SendToWebService(getActivity(),
                                    mInterfaceManageResourcesVehicle)
                                    .execute("8", "ManageVehicle", VehNo,
                                            typeOfVehicle, vehMileage,
                                            sImei, sVehPhNo, sProtocol, "4");

                        } catch (Exception e) {
                            Toast.makeText(getActivity(),
                                    "Try after sometime...",
                                    Toast.LENGTH_SHORT).show();
                            ExceptionMessage
                                    .exceptionLog(
                                            getActivity(),
                                            this.getClass().toString()
                                                    + " "
                                                    + "[manVBtnSave.setOnClickListener]",
                                            e.toString());
                        }

                        db.close();
                    }


                } else if (manVBtnSave.getText().toString().equals("UPDATE")) {
                    VehNo = manVehVNoEt.getText().toString()
                            .toUpperCase(Locale.getDefault());
                    sImei = spinnerVehicleImeiNo.getSelectedItem().toString();
                    if (prevImei.equals(sImei)) {
                        sProtocol = "";
                    } else {
                        getCursorData();
                    }
                    vehMileage = manVehMileage.getText().toString();
                    typeOfVehicle = spinnerVehicleType.getSelectedItem().toString();

                    if (VehNo.equals("")) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE VEHICLE NUMBER",
                                Toast.LENGTH_LONG).show();
                    } else if ((typeOfVehicle.equals("Select Vehicle Type"))) {
                        Toast.makeText(getActivity(),
                                "PLEASE SELECT THE VEHICLE TYPE",
                                Toast.LENGTH_LONG).show();
                    } else if ((vehMileage.equals(""))) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE MILEAGE", Toast.LENGTH_LONG)
                                .show();
                    } else if ((sImei.equals("Select Imei Number"))) {
                        Toast.makeText(getActivity(), "PLEASE SELECT THE IMEI",
                                Toast.LENGTH_LONG).show();
                    } else if (sVehPhNo.equals("")) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE VEHICLE PHONE NUMBER",
                                Toast.LENGTH_LONG).show();
                    } else {
                        DBAdapter db = new DBAdapter(getActivity());
                        db.open();
                        //
                        cv.put(DBAdapter.getKeyVehicleNo(), VehNo);
                        cv.put(DBAdapter.getKeyVehType(), typeOfVehicle);
                        cv.put(DBAdapter.getKeyVehMileage(), vehMileage);
                        cv.put(DBAdapter.getKeyImei(), sImei);
                        cv.put(DBAdapter.getKeyVehPhNo(), sVehPhNo);
                        //
                        // // METHOD TO CHECK WHETHER VEHICLE EXIST
                        String storedVeh = db.checkvehTforDataExist(VehNo);

                        // IF PRESENT UPDATE EXISTING VEHICLE
                        if (storedVeh.equals(VehNo)) {
                            alertUpdate();

                        }
                    }

                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //spinner to load imei numbers
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (VehicleList.VehicleNumber1 != null) {
                switch (VehicleList.VehicleNumber1) {
                    case "refresh":
                        refreshActivity();
                        break;

                    default:
                        VehicleNumber = VehicleList.VehicleNumber1;
                        populateVehicle(VehicleNumber);
                        break;
                }
            }

        }
    }

    public String jsonParsing(String response) {
        String jsonData = null;
        if (response != null) {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                if (d.getString("status").trim() != null) {
                    srvrStatus = d.getString("status").trim();
                }
                if (d.getString("vehicleNumber").trim() != null) {
                    srvrVehicleId = d.getString("vehicleNumber").trim();
                }
                if (d.getString("imeiNumber").trim() != null) {
                    srvrIMEINumber = d.getString("IMEINumber").trim();
                }

                if (d.getString("mobileNumber").trim() != null) {
                    srvrMobileNumber = d.getString("MobileNumber").trim();
                }
                return jsonData;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing]", e.toString());
            }
        }

        return jsonData;

    }

    private void alertUpdate() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel = (TextView) dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Update " + VehicleList.VehicleNumber1 + " ?");
        View v1 = inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("UPDATE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToWebService send = new SendToWebService(getActivity(),
                        mInterfaceManageResourcesVehicle);
                send.execute("8", "ManageVehicle", VehNo, typeOfVehicle,
                        vehMileage, sImei, sVehPhNo, sProtocol, "1");
                alertDialog1.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Update Cancelled", Toast.LENGTH_LONG).show();
                alertDialog1.dismiss();
            }
        });
        Resources resources = alertDialog1.getContext().getResources();
        int color = resources.getColor(R.color.white);
        alertDialog1.show();
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(color);
    }

    public void refreshActivity() {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void populateVehicle(String vehicleNo) {
        try {

            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            Cursor cursor = db.getVehicleEntry(vehicleNo);
            if (cursor.moveToFirst()) {
                // update view
                manVehVNoEt.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyVehicleNo())));

                if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Car")) {
                    spinnerVehicleType.setSelection(1);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Mini Truck")) {
                    spinnerVehicleType.setSelection(2);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Truck")) {
                    spinnerVehicleType.setSelection(3);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Bus")) {
                    spinnerVehicleType.setSelection(4);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("School Bus")) {
                    spinnerVehicleType.setSelection(5);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Mini Bus")) {
                    spinnerVehicleType.setSelection(6);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Trailer")) {
                    spinnerVehicleType.setSelection(7);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Van")) {
                    spinnerVehicleType.setSelection(8);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Cab")) {
                    spinnerVehicleType.setSelection(9);
                } else if (cursor.getString(
                        cursor.getColumnIndex(DBAdapter.getKeyVehType()))
                        .equals("Tanker")) {
                    spinnerVehicleType.setSelection(10);
                }


                manVehMileage.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyVehMileage())));

                imeiDataAdapter.add(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyImei())));
                imeiDataAdapter.notifyDataSetChanged();

                prevImei = cursor.getString(cursor.getColumnIndex(DBAdapter.getKeyImei()));
                int position = lables1.indexOf(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyImei())));
                spinnerVehicleImeiNo.setSelection(position);

                sVehPhNo = cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyVehPhNo()));

                cursor.close();
                db.close();
                manVBtnSave.setText("UPDATE");
            }
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[populateVehicle]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[populateVehicle]", e.toString());
        }
    }

    @Override
    public void onTaskCompleteVehicle(String result) {
        if (result.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            cd.ConnectingToInternet();
        } else if (result.contains("refused") || result.contains("timed out")) {
            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();
        } else if (result.contains("java.net.SocketTimeoutException")) {

            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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
            jsonParsing(result);
            db = new DBAdapter(getActivity());
            try {
                db.open();

                switch (srvrStatus) {

                    case "recovered":
                        db.insertContact(DBAdapter.getVehicleDetails(), cv);
//                        new SendToWebService(getActivity(),
//                                mInterfaceManageResourcesVehicle).execute("8",
//                                "ManageVehicle", VehNo, typeOfVehicle, vehMileage,
//                                sImei, sVehPhNo, sProtocol, "1");
                        updateCursor();
                        Toast.makeText(getActivity(), "Data Saved Successfull",
                                Toast.LENGTH_LONG).show();
                        refreshActivity();


                        break;

                    case "inserted":
                        long id = db.insertContact(DBAdapter.getVehicleDetails(),
                                cv);
                        if (id != -1) {

                            // imeiDataAdapter.remove(sImei);
                            updateCursor();
                            Toast.makeText(getActivity(), "Data Saved Successfull",
                                    Toast.LENGTH_LONG).show();
                        }
                        refreshActivity();
                        break;

                    case "imeinumber already exist":
                        Toast.makeText(
                                getActivity(),
                                "Vehicle Number : " + srvrVehicleId
                                        + "  is mapped with this IMEI Number",
                                Toast.LENGTH_LONG).show();
                        break;

                    case "vehicle already exist":
                        Toast.makeText(
                                getActivity(),
                                "Vehicle Number already existsr",
                                Toast.LENGTH_LONG).show();
                        break;

                    case "phonenumber already exist":
                        Toast.makeText(
                                getActivity(),
                                "Vehicle Number : " + srvrVehicleId
                                        + "  is mapped with this IMEI Number",
                                Toast.LENGTH_LONG).show();
                        break;

                    case "updated":
                        long rowsaffected = db.updateVehicleContact(
                                DBAdapter.getVehicleDetails(), cv, VehNo);
                        if (rowsaffected != -1) {
                            //  imeiDataAdapter.remove(sImei);
                            updateCursor();
                            Toast.makeText(getActivity(), "Data Updated",
                                    Toast.LENGTH_LONG).show();
                        }
                        refreshActivity();
                        break;

                    case "invalid authkey":
                        Toast.makeText(getActivity(), "Failed.. Try Again",
                                Toast.LENGTH_LONG).show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString()
                                + " "
                                + "[onTaskCompleteVehicle]", srvrStatus);

                        break;

                    case "unknown error":
                        Toast.makeText(getActivity(), "Failed.. Try Again",
                                Toast.LENGTH_LONG).show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString()
                                + " "
                                + "[onTaskCompleteVehicle]", srvrStatus);
                        break;

                    default:
                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString()
                                + " "
                                + "[onTaskCompleteVehicle]", srvrStatus);
                        break;

                }

            } catch (SQLiteException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                .toString() + " " + "[onTaskCompleteVehicle]",
                        e.toString());
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                .toString() + " " + "[onTaskCompleteVehicle]",
                        e.toString());
            }
            db.close();
        }

    }

    public void loadImeiSpinnerData() {
        try {

            if (imeiList == null)
            {
             //code here
            } else {
                try {
                    if (imeiList.moveToFirst()) {
                        for (int i = 0; i < imeiList.getCount(); i++) {

                            lables1.add(i + 1, imeiList.getString(1));

                            imeiList.moveToNext();
                        }
                    }
                    imeiList.close();
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[loadImeiSpinnerData]",
                            e.toString());
                }
            }
        } catch (Exception e) {

            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[loadImeiSpinnerData]",
                    e.toString());
        }
    }

    public void getCursorData() {
        try {
            if (imeiList.moveToFirst()) {
                for (int i = 1; i <= imeiList.getCount(); i++) {

                    if (i == val) {
                        sProtocol = imeiList.getString(2);
                        sVehPhNo = imeiList.getString(3);
                    }

                    imeiList.moveToNext();
                }
            }
            imeiList.close();
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString()
                    + " " + "[getCursorData()]", e.toString());
            e.toString();
        }
    }

    public void updateCursor() {
        int sKey = 1;
        String protocol, phone, imei;
        String[] columnNames = {"_id", "Imei", "Protocol", "PhoneNumber"};
        addVehicleCursor = new MatrixCursor(columnNames);
        try {
            if (imeiList != null) {
                if (imeiList.moveToFirst()) {
                    for (int i = 1; i <= imeiList.getCount(); i++) {
                        if (i != val) {
                            sKey = imeiList.getInt(0);
                            imei = imeiList.getString(1);
                            protocol = imeiList.getString(2);
                            phone = imeiList.getString(3);
                            addVehicleCursor.addRow(new Object[]{sKey, imei, protocol, phone});
                        }
                        imeiList.moveToNext();
                    }
                }
                imeiList.close();
                imeiList = null;
                imeiList = addVehicleCursor;
                addVehicleCursor = null;
            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString()
                    + " " + "[updataCursor]", e.toString());
            e.toString();
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            MainActivity.pos = 2;

        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onGetImeiNums(String result) {
        if (result.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            cd.ConnectingToInternet();
        } else if (result.contains("refused") || result.contains("timed out")) {
            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();
        }else if(result.contains("data does not exist"))
        {
            Toast.makeText(getActivity(),"Sorry! no device exist",Toast.LENGTH_LONG).show();
        }
        else if (result.contains("java.net.SocketTimeoutException")) {

            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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
            try {
                JSONObject jsonResponse = new JSONObject(result);
                // getting the data with tag d
                String jsonData = null;
                jsonData = jsonResponse.getString("d");

                // convert the string to Json array
                JSONArray imeiArray = new JSONArray(jsonData);
                JSONObject status1 = imeiArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();
                // iterating the array
                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[getClientDevices]", statuschk);

                } else if (statuschk.equals("ok"))
                {
                    String[] columnNames = {"_id", "Imei", "Protocol", "PhoneNumber"};
                    imeiList = new MatrixCursor(columnNames);

                    JSONArray clientArray = imeiArray.getJSONArray(1);

                    for (int i = 0; i < clientArray.length(); i++)
                    {
                        JSONObject Performance = clientArray
                                .getJSONObject(i);
                        String imei = Performance.getString("imei");
                        String protocol = Performance.getString("protocol");
                        String phone = Performance.getString("phone");
                        imeiList.addRow(new Object[]{sKey, imei, protocol, phone});
//                        deviceCount=imeiList;
                        sKey++;
                    }
                    if(imeiList!=null)
                    {
                        if (imeiList.moveToFirst()) {
                            for (int i = 0; i < imeiList.getCount(); i++) {

                                lables1.add(i + 1, imeiList.getString(1));

                                imeiList.moveToNext();
                            }
                            deviceCount=lables1;

                            imeiDataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                                    deviceCount);
                            imeiDataAdapter.notifyDataSetChanged();
                            spinnerVehicleImeiNo.setAdapter(imeiDataAdapter);
                        }
                    }

                }
            } catch (Exception e)
            {

            }
        }
    }

    public void bindImeiToSpinner()
    {
        DBAdapter db = new DBAdapter(getActivity());
        try {
            db.open();
            Cursor profileDataCursor = db.retrieveProfileData();
            if (profileDataCursor.getCount() == 1) {
                profileDataCursor.moveToFirst();
                clientName = profileDataCursor.getString(profileDataCursor
                        .getColumnIndex(DBAdapter.getKeyClientname()));

                clientName = profileDataCursor.getString(profileDataCursor
                        .getColumnIndex(db.getKeyClientname()));

                profileDataCursor.close();
            }
            db.close();

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this
                    .getClass().toString()
                    + " "
                    + "New Entries GetClientName", e.toString());
        }
        //@first seen it should contain "Select Imei Number"
         if(lables1.size()==0) {
             lables1.add(0, "Select Imei Number");

             imeiDataAdapter = new ArrayAdapter<String>(
                     getActivity(), android.R.layout.simple_spinner_item,
                     lables1);
             spinnerVehicleImeiNo.setAdapter(imeiDataAdapter);
         }
        //on touch of spinner spinner should load data
        View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    //first time it should fetch data from server
                   if (deviceCount==null) {
                        SendToWebService send = new SendToWebService(getActivity(), mGetImeiNumbers);
                        send.execute("50", "GetClientsDevices", clientName);
                    }

                }
                return false;
            }
        };
        spinnerVehicleImeiNo.setOnTouchListener(spinnerOnTouch);

        spinnerVehicleImeiNo.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int i, long l)
            {
                System.out.println(lables1);
           //     loadImeiSpinnerData();
                sImei = lables1.get(i);
                val = i;
            }
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
    }
}
