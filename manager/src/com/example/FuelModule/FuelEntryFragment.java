package com.example.FuelModule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IFuelEntryFragment;
import com.example.anand_roadwayss.BackUpService;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;
import com.example.anand_roadwayss.Welcome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.xmlpull.v1.sax2.Driver;

public class FuelEntryFragment extends Fragment implements OnClickListener,
        IFuelEntryFragment
{
    Button save;
    TextView ChooseDate;
    Calendar c = Calendar.getInstance();
    String UzrDate, selDriver, selVehicle, Date1, VehicleStr, DriverStr, SpeedStr, FuelStr, Date2;
    Spinner VehicleS, DriverS;
    List<String> VehicleList, DriverList;
    EditText SpeedM, FuelV;
    DBAdapter db;
    TextView ok, cancel, message;
    View view;
    ArrayAdapter<String> vehicleDataAdapter,driverDataAdapter;
    final IFuelEntryFragment mFuelEntryFragment = this;
    int pos;
    CustomAlertDialog ald;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fuel_entry, container, false);
        db = new DBAdapter(getActivity());
        ald=new CustomAlertDialog();
        selDriver = "Select Driver";
        selVehicle = "Select Vehicle";
        bindData();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        ChooseDate.setText(formattedDate);
        Date1 = formattedDate;

        loadDriverSpinnerData();
        loadVehicleSpinnerData();
        ChooseDate.setOnClickListener(this);
        save.setOnClickListener(this);

        return view;
    }

    /*
     * Purpose - Binds XMl Id reference to java Method Name - bindData()
	 * Parameters - No parameters Return Type - No Return Type
	 */

    private void bindData() {
        ChooseDate = (TextView) view.findViewById(R.id.fragmentFuelEntryBtnDate);
        save = (Button) view.findViewById(R.id.fragmentFuelEntryBtnSave);
        SpeedM = (EditText) view
                .findViewById(R.id.fragmentFuelEntryEtSpeedometer);
        FuelV = (EditText) view.findViewById(R.id.fragmentFuelEntryEtFuelVol);
        DriverS = (Spinner) view
                .findViewById(R.id.fragmentFuelEntrySpinnerDriver);

        VehicleS = (Spinner) view
                .findViewById(R.id.fragmentFuelEntrySpinnerVehNo);

    }

    private void getSetData() {
        VehicleStr = String.valueOf(VehicleS.getSelectedItem());
        DriverStr = String.valueOf(DriverS.getSelectedItem());
        SpeedStr = SpeedM.getText().toString();
        FuelStr = FuelV.getText().toString();
        Date2 = ChooseDate.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragmentFuelEntryBtnDate:
                MyDatePickerDialog();
                break;
            case R.id.fragmentFuelEntryBtnSave:
                Double f = 0.0;
                getSetData();
                if (!FuelStr.equals("")) {
                    f = Double.valueOf(FuelStr);
                }
                if (VehicleStr == selVehicle) {
                    Toast.makeText(getActivity(), "Please Select Vehicle",
                            Toast.LENGTH_SHORT).show();
                } else if (DriverStr == selDriver) {
                    Toast.makeText(getActivity(), "Please Select Driver",
                            Toast.LENGTH_SHORT).show();
                } else if (SpeedStr.equals("")) {
                    Toast.makeText(getActivity(), "Please enter Speedometer Value",
                            Toast.LENGTH_SHORT).show();
                } else if (FuelStr.equals("")) {
                    Toast.makeText(getActivity(), "Please Enter the Fuel Volume",
                            Toast.LENGTH_SHORT).show();
                } else if (f < 0) {
                    Toast.makeText(getActivity(),
                            "Please Enter the Correct Fuel level",
                            Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog();
                }
                break;
            }
        }


    // SAVE BUTTON FUNCTION
    private void fuelDetails()
    {
        try {
                Double f = 0.0;
                getSetData();
                db.open();
                String empId = db.checkDrvierTableforDataExist(DriverStr);
                db.close();
                SendToWebService send = new SendToWebService(getActivity(),
                        mFuelEntryFragment);

                    int t = c.get(Calendar.HOUR_OF_DAY);
                    int t1 = c.get(Calendar.MINUTE);

                    if (f == 0 || f == 0.0) {
                        try {
                            send.execute("16", "SaveFuelDetails", VehicleStr,
                                    empId, SpeedStr, "0.001", Date2);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Try after sometime...",
                                    Toast.LENGTH_SHORT).show();
                            ExceptionMessage.exceptionLog(getActivity(), this
                                            .getClass().toString() + " " + "[fuelDetails]",
                                    e.toString());
                        }
                    } else {
                        try {
                            send.execute("16", "SaveFuelDetails", VehicleStr,
                                    empId, SpeedStr, FuelStr, Date2);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Try after sometime...",
                                    Toast.LENGTH_SHORT).show();
                            ExceptionMessage.exceptionLog(getActivity(), this
                                            .getClass().toString() + " " + "[fuelDetails]",
                                    e.toString());
                        }
                    }
                } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[fuelDetails]", e.toString());
            }
        }
    // END


    private void saveFuelDetails(String parsedValue) {
        String status = null, Rid = null;
        Double f = 0.0;
        try {
            JSONObject jsonResponse = new JSONObject(parsedValue);
            String jsonData = jsonResponse.getString("d");
            JSONObject d = new JSONObject(jsonData);
            status = d.getString("status");
           // Rid = d.getString("id");

            if (!FuelStr.equals("")) {
                f = Double.valueOf(FuelStr);
            }
            if (f == 0 || f == 0.0) {
                FuelStr = "0.001";
            }

            switch (status) {

                case "inserted":
                    bindData();
                    getSetData();
                    db.open();
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyMdate(), Date2);
                    cv.put(DBAdapter.getKeyMvehicle(), VehicleStr);
                    cv.put(DBAdapter.getKeyMdriver(), DriverStr);
                    cv.put(DBAdapter.getKeySpeedometer(), SpeedStr);
                    cv.put(DBAdapter.getKeyFuel(), FuelStr);
                    cv.put(DBAdapter.getKeyFuelRowId(), Rid);
                    long id = db.insertContactWithDelete(
                            DBAdapter.getFuelDetails(), cv);
                    db.close();
                    if (id != -1) {
                        Toast.makeText(getActivity(), "Saved Successfuly",
                                Toast.LENGTH_SHORT).show();
                    }
                    refreshActivity();
                    break;

                case "fuel cannot be null":
                    Toast.makeText(getActivity(), "Fuel field cannot be null",
                            Toast.LENGTH_SHORT).show();
                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[saveFuelDetails]", status);
                    break;

                case "employee id or vehicle number does not exist":
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View dialogView = inflater.inflate(R.layout.date_custom_dialog, null);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog1 = builder.create();
                    message = (TextView) dialogView.findViewById(R.id.textmsg);
                    ok = (TextView) dialogView.findViewById(R.id.textBtn);
                    message.setText("The driver name or vehicle number has deleted by another manager.");
                    View v1= inflater.inflate(R.layout.title_dialog_layout, null);
                    alertDialog1.setCustomTitle(v1);

                    ok.setText("REFRESH");
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent ii = new Intent(getActivity(), Welcome.class);
                            ii.putExtra("comeback", "true");
                            ii.putExtra("class",FuelActivity.class);
                            startActivity(ii);
                            alertDialog1.dismiss();
                        }
                    });
                    Resources resources =alertDialog1.getContext().getResources();
                    int color = resources.getColor(R.color.white);
                    alertDialog1.show();
                    int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
                    View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
                    titleDivider.setBackgroundColor(color);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[saveFuelDetails]", status);
                    break;

                default:
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[saveFuelDetails]", status);
                    break;

            }

        } catch (SQLiteException e) {

            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[saveFuelDetails]", e.toString());
        } catch (Exception e) {

            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[saveFuelDetails]", e.toString());

        }
    }

    // DRIVER SPINNER LOADER
    private void loadDriverSpinnerData() {
        try {
            db.open();
            DriverList = db.getAllLabels(DBAdapter.getEmployeeDetails(), "Driver");
            DriverList.add(0, selDriver);
            // DriverList.add(1, "RAM");
            db.close();
            // Creating adapter for spinner
            driverDataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                    DriverList);
            // attaching data adapter to spinner
            DriverS.setAdapter(driverDataAdapter);
            DriverS.setSelection(pos);

            // Set the ClickListener for Spinner
            DriverS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> adapterView,
                                           View view, int i, long l)
                {
                    pos=i;
                }

                // If no option selected
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        } catch (SQLiteException e) {
            ExceptionMessage
                    .exceptionLog(getActivity(), this.getClass().toString()
                            + " " + "[loadDriverSpinnerData]", e.toString());
        } catch (Exception e) {
            ExceptionMessage
                    .exceptionLog(getActivity(), this.getClass().toString()
                            + " " + "[loadDriverSpinnerData]", e.toString());

        }
    }

    // END

    // VEHICLE SPINNER LOADER
    private void loadVehicleSpinnerData() {
        try {
            db.open();
            // Spinner Drop down elements
            VehicleList = db.getAllLabels(DBAdapter.getVehicleDetails());
            VehicleList.add(0, selVehicle);
            // VehicleList.add(1, "TN0001");
            db.close();
            // Creating adapter for spinner
          vehicleDataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    VehicleList);
            // Drop down layout style - list view with radio button
            // dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            VehicleS.setAdapter(vehicleDataAdapter);
            vehicleDataAdapter.notifyDataSetChanged();

            // Set the ClickListener for Spinner
            VehicleS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView,
                                           View view, int i, long l)
                {

                }

                // If no option selected
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[loadVehicleSpinnerData]",
                    e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[loadVehicleSpinnerData]",
                    e.toString());

        }

    }

    // END

    // MYDATE PICKER DIALOG FOR DATE PICKING
    public void MyDatePickerDialog() {
        DatePickerDialog dp = new DatePickerDialog(getActivity(), dt,
                c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    public DatePickerDialog.OnDateSetListener dt = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            UzrDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            if (dayOfMonth < 10 && monthOfYear < 9) {
                UzrDate = year + "-0" + (monthOfYear + 1) + "-" + "0"
                        + dayOfMonth;
            } else {
                if (dayOfMonth < 10)
                    UzrDate = year + "-" + (monthOfYear + 1) + "-" + "0"
                            + dayOfMonth;
                if (monthOfYear < 9)
                    UzrDate = year + "-0" + (monthOfYear + 1) + "-"
                            + dayOfMonth;
            }
            ChooseDate.setText(UzrDate);
        }
    };

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), FuelActivity.class);
        startActivity(intent);

    }

    @Override
    public void onInsertToVehicleFuel(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                cd.ConnectingToInternet();
            } else if (response.contains("refused") || response.contains("timed out")) {
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
                saveFuelDetails(response);
            }
        } catch (Exception e) {
            ExceptionMessage
                    .exceptionLog(getActivity(), this.getClass().toString()
                            + " " + "[onInsertToVehicleFuel]", e.toString());
        }
    }

    public void alertDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();

        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel = (TextView) dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Are you sure you want to save this details?\n" +
                "\nNOTE : Can not be changed later");
        //for hiding title layout
        View v1 = inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("OK");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DeleteFuelDetails(id1);
                fuelDetails();
                alertDialog1.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog1.dismiss();
            }
        });
        Resources resources = alertDialog1.getContext().getResources();
        int color = resources.getColor(R.color.white);
        alertDialog1.show();

        //changing default color of divider
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(color);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            FuelActivity.pos = 1;

//            if (FuelEntryListFragment.speedoVal != null
//                 && FuelEntryListFragment.date !=null
//                    && FuelEntryListFragment.fuelVolume != null)
//            {
//                if(ChooseDate != null && SpeedM != null && FuelV !=null) {
//                    ChooseDate.setText(FuelEntryListFragment.date);
//                    Date2 = ChooseDate.getText().toString();
//                    SpeedM.setText(FuelEntryListFragment.speedoVal);
//                    SpeedStr = SpeedM.getText().toString();
//                    FuelV.setText(FuelEntryListFragment.fuelVolume);
//                    FuelStr = FuelV.getText().toString();
//                    int v = vehicleDataAdapter.getPosition(FuelEntryListFragment.veh);
//                    VehicleS.setSelection(v);
//                    VehicleStr = VehicleS.getSelectedItem().toString();
//
//                    int d = driverDataAdapter.getPosition(FuelEntryListFragment.driver);
//                    DriverS.setSelection(d);
//                    DriverStr = DriverS.getSelectedItem().toString();
//                    update.setVisibility(View.VISIBLE);
//                    save.setVisibility(View.GONE);
//                    // fuelDetails();
//                }
//            }
        }


    }



}
