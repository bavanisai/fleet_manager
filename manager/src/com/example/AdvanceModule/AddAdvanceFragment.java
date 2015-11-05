/*
 * Purpose - AddAdvanceFragment implements adding advance to driver, cleaner or Vehicle.
 * @author - Pravitha 
 * Created on May 26, 2014
 * Modified on June 10, 2014
 */
package com.example.AdvanceModule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.AbsenceReport.DatePickerFragment;
import com.example.Interface.IAddAdvanceFragment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author MKSoft01
 */
public class AddAdvanceFragment extends Fragment implements OnClickListener,
        IAddAdvanceFragment {

    LinearLayout mContent;
    int a=0;
    Button ok, clear;
    Signature mSignature;
    byte[] imgArr, voucherDb, signDb;
    Bitmap bp1, bp2;
    Cursor cursor;
    Button addAdvanceBtnSave;
    EditText addAdvanceEtVoucherNum, addAdvanceEtAmount;
    ArrayList<String> advanceType = new ArrayList<String>();
    Spinner advanceTypeSpinner, driverSpinner, vehicleSpinner, cleanerSpinner;
    String mUzrDate, mTypeOfAdvance, mSpinnerVehicleNo, mSpinnerDriver,
            mSpinnerCleaner, mEmpId, mVoucherNum, mAmount = "0", voucherImg="",
            parsedString = null;
    List<String> lables1, lables2, lables3;
    SendToWebService send;
    View view;
    DBAdapter db;
    int year, month, day;
    final IAddAdvanceFragment mAddAdvanceFragment = this;
    TextView tv, addAdvanceTvDate;
    static Bitmap restore;
    static String sAdvanceType, sVehicleType, sDriverType, sCleanerType, sVoucherNo, sAmount;
   CustomAlertDialog ald;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_advance_add, container, false);
        db = new DBAdapter(getActivity());
        ald=new CustomAlertDialog();
        bindData();


//        check = (Button) view.findViewById(R.id.btnViewReceipt);
//        capture = (Button) view.findViewById(R.id.btnAttachReceipt);
//        img = (ImageView) view.findViewById(R.id.img_clicked);
//        tick = (ImageView) view.findViewById(R.id.tick_mark);
//        if (restore == null) {
//            check.setVisibility(View.INVISIBLE);
//            tick.setVisibility(View.INVISIBLE);
//        } else {
//            thumbnail = restore;
//        }

//        capture.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    mUzrDate = addAdvanceTvDate.getText().toString();
//                    mVoucherNum = addAdvanceEtVoucherNum.getText().toString();
//                    mAmount = addAdvanceEtAmount.getText().toString();
//                    if (mAmount.equals("")) {
//                        mAmount = "0";
//                    }
//                    double number = Double.parseDouble(mAmount);
//                    DecimalFormat numberFormat = new DecimalFormat("#.00");
//                    mAmount = numberFormat.format(number);
//                    mSpinnerVehicleNo = String.valueOf(vehicleSpinner.getSelectedItem());
//                    mSpinnerDriver = String.valueOf(driverSpinner.getSelectedItem());
//                    mSpinnerCleaner = String.valueOf(cleanerSpinner.getSelectedItem());
//
//                    if (mUzrDate.equals("")) {
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "PLEASE SELECT THE DATE", Toast.LENGTH_LONG).show();
//                    } else if (mTypeOfAdvance.equals("SELECT ADVANCE TYPE")) {
//                        //advanceTypeSpinner.setError("PLEASE SELECT ADVANCE TYPE");
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "PLEASE SELECT ADVANCE TYPE", Toast.LENGTH_LONG).show();
//
//                    } else if (mVoucherNum.equals("")) {
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "PLEASE ENTER THE VOUCHER NUMBER", Toast.LENGTH_LONG)
//                                .show();
//                    } else if (mAmount.equals("") || mAmount.equals(".00")) {
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                "PLEASE ENTER THE AMOUNT", Toast.LENGTH_LONG).show();
//                    } else {
//                        // use standard intent to capture an image
//                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        // we will handle the returned data in onActivityResult
//                        startActivityForResult(captureIntent, CAMERA_IMAGE);
//                    }
//                } catch (Exception e) {
//                    ExceptionMessage.exceptionLog(getActivity(), this
//                            .getClass().toString()
//                            + " "
//                            + "[iVempPhoto.setOnClickListener]", e.toString());
//                }
//
//            }
//        });
//        check.setOnClickListener(new OnClickListener() {
//            @Override
//
//            public void onClick(View v) {
//                try {
//                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//                    dialogBuilder.setTitle("Receipt View");
//                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    LayoutInflater inflater = getActivity().getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.capturedimageview, null);
//                    dialogBuilder.setView(dialogView);
//
//                    iv = (ImageView) dialogView.findViewById(R.id.showimage);
//                    iv.setImageBitmap(thumbnail);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    voucherDb = stream.toByteArray();
//                    System.out.println(voucherDb);
//
//                    AlertDialog alertDialog = dialogBuilder.create();
//                    alertDialog.show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        addAdvanceTvDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("-").append(month + 1).append("-")
                .append(day).append(" "));

        advanceType.add("Select Advance Type");
        advanceType.add("Vehicle Advance");
        advanceType.add("Driver Advance");
        advanceType.add("Cleaner Advance");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1,
                advanceType);
       // arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        advanceTypeSpinner.setAdapter(arrayAdapter);
        advanceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int i, long l) {
                mTypeOfAdvance = advanceType.get(i);

                if (advanceType.get(i).equalsIgnoreCase(
                        "Select Advance Type")) {

                } else if (advanceType.get(i).equalsIgnoreCase(
                        "Vehicle Advance")) {

                    vehicleSpinner.setVisibility(View.VISIBLE);
                    driverSpinner.setVisibility(View.VISIBLE);
                    cleanerSpinner.setVisibility(View.GONE);
                } else if (advanceType.get(i).equalsIgnoreCase(
                        "Driver Advance")) {
                    vehicleSpinner.setVisibility(View.GONE);
                    cleanerSpinner.setVisibility(View.GONE);
                    driverSpinner.setVisibility(View.VISIBLE);
                } else if (advanceType.get(i).equalsIgnoreCase(
                        "Cleaner Advance")) {
                    vehicleSpinner.setVisibility(View.GONE);
                    driverSpinner.setVisibility(View.GONE);
                    cleanerSpinner.setVisibility(View.VISIBLE);
                }
            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });

        // Loading Spinner Datas
        loadDriverSpinnerData();
        loadVehicleSpinnerData();
        loadCleanerSpinnerData();

        addAdvanceTvDate.setOnClickListener(this);
        addAdvanceBtnSave.setOnClickListener(this);

        return view;
    }

    /*
     * Purpose - Binds XMl Id reference to java Method Name - BindData()
     * Parameters - No parameters Return Type - No Return Type
     */
    private void bindData() {

        addAdvanceTvDate = (TextView) view.findViewById(R.id.addAdvanceTvDate);


        addAdvanceBtnSave = (Button) view.findViewById(R.id.addAdvanceBtnSave);
        advanceTypeSpinner = (Spinner) view
                .findViewById(R.id.addAdvanceSpinnerAdvanceType);
        vehicleSpinner = (Spinner) view
                .findViewById(R.id.addAdvanceSpinnerVehicle);
        cleanerSpinner = (Spinner) view
                .findViewById(R.id.addAdvanceSpinnerCleaner);
        driverSpinner = (Spinner) view
                .findViewById(R.id.addAdvanceSpinnerDriver);
        addAdvanceEtVoucherNum = (EditText) view
                .findViewById(R.id.addAdvanceEtVoucherNo);
        addAdvanceEtAmount = (EditText) view
                .findViewById(R.id.addAdvanceEtAmount);

    }

    private void getSetData() {
        mVoucherNum = addAdvanceEtVoucherNum.getText().toString();
        mAmount = addAdvanceEtAmount.getText().toString();
        double number = Double.parseDouble(mAmount);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        mAmount = numberFormat.format(number);
        mSpinnerVehicleNo = String.valueOf(vehicleSpinner.getSelectedItem());
        mSpinnerDriver = String.valueOf(driverSpinner.getSelectedItem());
        mSpinnerCleaner = String.valueOf(cleanerSpinner.getSelectedItem());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addAdvanceBtnSave:
                onSaveAdvance();
                break;

            case R.id.addAdvanceTvDate:
                MyDatePickerDialog();
                break;

            default:
                break;
        }

    }

    public void saveVehicleAdvance(String parsedString) {
        try {
            ContentValues cv = new ContentValues();
            switch (parsedString) {
                case "inserted":
                    bindData();
                    getSetData();
                    db.open();
                    cv.put(DBAdapter.getKeyDate(), mUzrDate);
                    cv.put(DBAdapter.getKeyVehicleNo(), mSpinnerVehicleNo);
                    cv.put(DBAdapter.getKeyName(), mSpinnerDriver);
                    cv.put(DBAdapter.getKeyAdvanceType(), "Vehicle Advance");
                    cv.put(DBAdapter.getKeyVoucherNo(), mVoucherNum);
                    cv.put(DBAdapter.getKeyAmount(), mAmount);
                    cv.put(DBAdapter.getKeyReceipt(), voucherDb);
                    long id = db.insertContactWithDelete(DBAdapter.getAdvanceDetails(), cv);

                    if (id != -1) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "ADVANCE SAVED", Toast.LENGTH_LONG).show();
                    } else {

                    }
                    db.close();

                    refreshActivity();
                    ((AdvanceMain) getActivity()).setCurrentItem(1, true);
                    break;

                case "voucher number already exist":
                    Toast.makeText(getActivity().getApplicationContext(),
                            "VOUCHER NUMBER ALREADY EXISTS", Toast.LENGTH_LONG)
                            .show();
                    break;

                case "employee id does not exist":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveVehicleAdvance]",
                            parsedString);
                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveVehicleAdvance]",
                            parsedString);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveVehicleAdvance]",
                            parsedString);
                    break;

                default:
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveVehicleAdvance]",
                            parsedString);
                    break;

            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SaveVehicleAdvance]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SaveVehicleAdvance]", e.toString());
        }

    }

    public void saveDriverAdvance(String parsedString) {
        try {
            ContentValues cv = new ContentValues();

            switch (parsedString) {
                case "inserted":
                    bindData();
                    getSetData();
                    db.open();
                    cv.put(DBAdapter.getKeyDate(), mUzrDate);
                    cv.put(DBAdapter.getKeyName(), mSpinnerDriver);
                    cv.put(DBAdapter.getKeyAdvanceType(), "Driver Advance");
                    cv.put(DBAdapter.getKeyVoucherNo(), mVoucherNum);
                    cv.put(DBAdapter.getKeyAmount(), mAmount);
                    cv.put(DBAdapter.getKeyReceipt(), voucherDb);
                    long id = db.insertContactWithDelete(
                            DBAdapter.getAdvanceDetails(), cv);
                    if (id != -1) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "ADVANCE SAVED", Toast.LENGTH_LONG).show();

                    } else {

                    }

                    db.close();

                    refreshActivity();
                    ((AdvanceMain) getActivity()).setCurrentItem(2, true);
                    break;

                case "voucher number already exist":
                    Toast.makeText(getActivity().getApplicationContext(),
                            "VOUCHER NUMBER ALREADY EXISTS", Toast.LENGTH_LONG)
                            .show();
                    break;

                case "employee id does not exist":
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[SaveDriverAdvance]", parsedString);
                    break;

                case "invalid authkey":
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[SaveDriverAdvance]", parsedString);
                    break;

                case "unknown error":
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[SaveDriverAdvance]", parsedString);
                    break;

                default:
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[SaveDriverAdvance]", parsedString);
                    break;

            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SaveDriverAdvance]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SaveDriverAdvance]", e.toString());
        }
    }

    public void saveCleanerAdvance(String parsedString) {
        try {
            ContentValues cv = new ContentValues();

            switch (parsedString) {
                case "inserted":
                    bindData();
                    getSetData();
                    db.open();
                    cv.put(DBAdapter.getKeyDate(), mUzrDate);
                    cv.put(DBAdapter.getKeyName(), mSpinnerCleaner);
                    cv.put(DBAdapter.getKeyAdvanceType(), "Cleaner Advance");
                    cv.put(DBAdapter.getKeyVoucherNo(), mVoucherNum);
                    cv.put(DBAdapter.getKeyAmount(), mAmount);
                    cv.put(DBAdapter.getKeyReceipt(), voucherDb);
                    long id = db.insertContactWithDelete(
                            DBAdapter.getAdvanceDetails(), cv);
                    if (id != -1) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "ADVANCE SAVED", Toast.LENGTH_LONG).show();
                    } else {

                    }
                    db.close();
                    refreshActivity();
                    ((AdvanceMain) getActivity()).setCurrentItem(3, true);
                    break;

                case "voucher number already exist":
                    Toast.makeText(getActivity().getApplicationContext(),
                            "VOUCHER NUMBER ALREADY EXISTS", Toast.LENGTH_LONG)
                            .show();
                    break;

                case "employee id does not exist":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveCleanerAdvance]",
                            parsedString);
                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveCleanerAdvance]",
                            parsedString);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveCleanerAdvance]",
                            parsedString);
                    break;

                default:
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[SaveCleanerAdvance]",
                            parsedString);
                    break;

            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SaveCleanerAdvance]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[SaveCleanerAdvance]", e.toString());
        }
    }

	/*
     * Purpose - Save advance to local database and server Method Name
	 * -onSaveAdvance() Parameters - No parameter Return Type - No Return Type
	 */

    public void onSaveAdvance() {
        mUzrDate = addAdvanceTvDate.getText().toString();
        mVoucherNum = addAdvanceEtVoucherNum.getText().toString();
        mAmount = addAdvanceEtAmount.getText().toString();
        if (mAmount.equals("")) {
            mAmount = "0";
        }
        double number = Double.parseDouble(mAmount);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        //
        mAmount = numberFormat.format(number);
        mSpinnerVehicleNo = String.valueOf(vehicleSpinner.getSelectedItem());
        mSpinnerDriver = String.valueOf(driverSpinner.getSelectedItem());
        mSpinnerCleaner = String.valueOf(cleanerSpinner.getSelectedItem());

        if (mUzrDate.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(),
            "PLEASE SELECT THE DATE", Toast.LENGTH_LONG).show();
        } else if (mTypeOfAdvance.equals("Select Advance Type")) {
            //advanceTypeSpinner.setError("PLEE SELECT ADVANCE TYPE");
            Toast.makeText(getActivity().getApplicationContext(),
                    "PLEASE SELECT ADVANCE TYPE", Toast.LENGTH_LONG).show();

        }
//        else if (mVoucherNum.equals("")) {
//            Toast.makeText(getActivity().getApplicationContext(),
//                    "PLEASE ENTER THE VOUCHER NUMBER", Toast.LENGTH_LONG)
//                    .show();
//        } else if (mAmount.equals("") || mAmount.equals(".00")) {
//            Toast.makeText(getActivity().getApplicationContext(),
//                    "PLEASE ENTER THE AMOUNT", Toast.LENGTH_LONG).show();
//       }
        else {
            if (mTypeOfAdvance.equals("Vehicle Advance")) {
                if (mSpinnerVehicleNo.equals("Select The Vehicle")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE SELECT THE VEHICLE", Toast.LENGTH_LONG)
                            .show();
                } else if (mSpinnerDriver.equals("Select The Driver")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE SELECT THE DRIVER", Toast.LENGTH_LONG)
                            .show();
                }
                else if (mVoucherNum.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE ENTER THE VOUCHER NUMBER", Toast.LENGTH_LONG)
                            .show();
                } else if (mAmount.equals("") || mAmount.equals(".00")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE ENTER THE AMOUNT", Toast.LENGTH_LONG).show();
                }
                else{
                    alertDialog();
                }
            }

            else if (mTypeOfAdvance.equals("Driver Advance")) {
                if (mSpinnerDriver.equals("Select The Driver")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE SELECT THE DRIVER", Toast.LENGTH_LONG)
                            .show();
                }
                else if (mVoucherNum.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE ENTER THE VOUCHER NUMBER", Toast.LENGTH_LONG)
                            .show();
                } else if (mAmount.equals("") || mAmount.equals(".00")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE ENTER THE AMOUNT", Toast.LENGTH_LONG).show();
                }
                else{
                    alertDialog();
                }
            }

            else if (mTypeOfAdvance.equals("Cleaner Advance")) {
                if (mSpinnerCleaner.equals("Select The Cleaner")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE SELECT THE CLEANER", Toast.LENGTH_LONG)
                            .show();
                }
                else if (mVoucherNum.equals("")) {

                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE ENTER THE VOUCHER NUMBER", Toast.LENGTH_LONG)
                            .show();
                } else if (mAmount.equals("") || mAmount.equals(".00")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE ENTER THE AMOUNT", Toast.LENGTH_LONG).show();
                }
                else{
                    alertDialog();
                }
            }


        }
    }

	/*
	 * Purpose - Loads Date picker Dialog Method Name - MyDatePickerDialog()
	 * Parameters - No parameter Return Type - No Return Type
	 */

    public void MyDatePickerDialog() {
        // DatePickerDialog dp = new DatePickerDialog(getActivity(), dt,
        // c.get(Calendar.YEAR), c.get(Calendar.MONTH),
        // c.get(Calendar.DAY_OF_MONTH));
        // dp.getDatePicker().setMaxDate(new Date().getTime());
        // dp.show();

        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");

    }

    OnDateSetListener ondate = new OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            addAdvanceTvDate.setText(String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-"
                    + String.valueOf(dayOfMonth));

        }
    };

    public OnDateSetListener dt = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mUzrDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
            if (dayOfMonth < 10 && monthOfYear < 9) {
                mUzrDate = year + "-0" + (monthOfYear + 1) + "-" + "0"
                        + dayOfMonth;
            } else {
                if (dayOfMonth < 10)
                    mUzrDate = year + "-" + (monthOfYear + 1) + "-" + "0"
                            + dayOfMonth;
                if (monthOfYear < 9)
                    mUzrDate = year + "-0" + (monthOfYear + 1) + "-"
                            + dayOfMonth;
            }
            addAdvanceTvDate.setText(mUzrDate);
        }

    };

	/*
	 * Purpose - Loads Driver names from local database to driverSpinner Method
	 * Name - loadDriverSpinnerData() Parameters - No parameter Return Type - No
	 * Return Type
	 */

    private void loadDriverSpinnerData() {
        try {
            db.open();
            lables1 = db.getAllLabels(DBAdapter.getEmployeeDetails(), "Driver");
            lables1.add(0, "Select The Driver");
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    lables1);

            // Drop down layout style - list view with radio button
         //   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            driverSpinner.setAdapter(dataAdapter);

            // Set the ClickListener for Spinner
            driverSpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        public void onItemSelected(AdapterView<?> adapterView,
                                                   View view, int i, long l) {

                            mSpinnerDriver = lables1.get(i);
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

	/*
	 * Purpose - Loads Vehicle No's from local database to vehicleSpinner Method
	 * Name - loadVehicleSpinnerData() Parameters - No parameter Return Type -No
	 * Return Type
	 */

    private void loadVehicleSpinnerData() {
        try {
            db.open();
            // Spinner Drop down elements
            lables2 = db.getAllLabels(DBAdapter.getVehicleDetails());
            lables2.add(0, "Select The Vehicle");
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    lables2);
            // Drop down layout style - list view with radio button
          //  dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            vehicleSpinner.setAdapter(dataAdapter);
            // Set the ClickListener for Spinner
            vehicleSpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView,
                                                   View view, int i, long l) {

                            mSpinnerVehicleNo = lables2.get(i);
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

	/*
	 * Purpose - Loads Cleaner names from local database to cleanerSpinner
	 * Method Name - loadCleanerSpinnerData() Parameters - No parameter Return
	 * Type - No Return Type
	 */

    private void loadCleanerSpinnerData() {
        try {
            db.open();
            // Spinner Drop down elements
            lables3 = db
                    .getAllLabels(DBAdapter.getEmployeeDetails(), "Cleaner");
            lables3.add(0, "Select The Cleaner");
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    lables3);
            // Drop down layout style - list view with radio button
          //  dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            cleanerSpinner.setAdapter(dataAdapter);
            // Set the ClickListener for Spinner
            cleanerSpinner
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView,
                                                   View view, int i, long l) {

                            mSpinnerCleaner = lables3.get(i);
                        }

                        // If no option selected
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[loadCleanerSpinnerData]",
                    e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[loadCleanerSpinnerData]",
                    e.toString());
        }
    }

	/*
	 * Purpose - Parse the data got from the server Method Name -jsonParsing
	 * Return Type - String JsonData
	 * 
	 * Variable list String jsonData - To get the data with tag d JSONArray
	 */

    public String jsonParsing(String response) {
        String jsonData = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
				JSONObject d = new JSONObject(jsonData);
				String status = d.getString("status").trim();
                return status;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing]", e.toString());
            }

        return jsonData;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        advanceType.clear();
    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), AdvanceMain.class);
        startActivity(intent);

    }

    @Override
    public void onManageAdvanceDetailsTable(String response) {
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
                parsedString = jsonParsing(response).trim();
                if (mTypeOfAdvance.equals("Vehicle Advance")) {
                    saveVehicleAdvance(parsedString);
                }

                if (mTypeOfAdvance.equals("Driver Advance")) {
                    saveDriverAdvance(parsedString);
                }

                if (mTypeOfAdvance.equals("Cleaner Advance")) {
                    saveCleanerAdvance(parsedString);
                }
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[onManageAdvanceDetailsTable]",
                    e.toString());
        }

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        try {
//
//            if (requestCode == CAMERA_IMAGE && resultCode == getActivity().RESULT_OK) {
//                thumbnail = (Bitmap) data.getExtras().gFet("data");
//                img.setImageBitmap(thumbnail);
//                check.setVisibility(View.VISIBLE);
//                tick.setVisibility(View.VISIBLE);
//                click++;
//                restore = thumbnail;
//            }
//
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Sorry Unable to get Data",
//                    Toast.LENGTH_LONG).show();
//            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
//                    .toString() + " " + "[onActivityResult]", e.toString());
//        }
//
//    }

    public void signMatch()
    {
        //CONVERTING SIGN AS A IMAGE N THEN BYTE ARRAY
        Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),mContent.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        mContent.draw(canvas);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);

        try {
            imgArr = bs.toByteArray();
                db.open();
                cursor = db.getSignature();
                signDb = cursor.getBlob(cursor.getColumnIndex("signature"));
                //matching current sign with database sign
                bp1 = BitmapFactory.decodeByteArray(signDb, 0, signDb.length);
                bp2 = BitmapFactory.decodeByteArray(imgArr,
                        0, imgArr.length);
                mSignature.clear();

                a = ((signDb.length - imgArr.length) * 100 / imgArr.length);


                if (a > 0 && a <= 30 || a<0 ) {
                    if (!mVoucherNum.equals("0")
                            && !mVoucherNum.equals("")) {
                        saveDataToServer();
                        db.close();
                    }
                } else
                {
                    ald.alertDialog(getActivity(),"Signature mismatch !");}
              } catch (Exception e)
            {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[signMatch()]", e.toString());
            }
        }
    public void saveDataToServer()
    {
        try
        {

            if (mTypeOfAdvance.equals("Vehicle Advance")) {
                if (mSpinnerVehicleNo.equals("Select The Vehicle")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE SELECT THE VEHICLE", Toast.LENGTH_LONG)
                            .show();
                } else if (mSpinnerDriver.equals("Select The Driver")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "PLEASE SELECT THE DRIVER", Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    db.open();
                    mEmpId = db
                            .checkDrvierTableforDataExist(mSpinnerDriver);
                    db.close();

                    send = new SendToWebService(getActivity(),
                            mAddAdvanceFragment);

                    // if (send.isConnectingToInternet()) {
                    try {

                        send.execute("1", "ManageAdvance",
                                mEmpId, mSpinnerVehicleNo, mTypeOfAdvance,
                                mUzrDate, mVoucherNum, mAmount, voucherImg, "4");

                    } catch (Exception e) {
                        Toast.makeText(getActivity().getBaseContext(),
                                "Try after sometime...", Toast.LENGTH_SHORT)
                                .show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString()
                                + " " + "[onSaveAdvance]", e.toString());
                    }
                }
            }

                if (mTypeOfAdvance.equals("Driver Advance")) {
                    if (mSpinnerDriver.equals("Select The Driver")) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "PLEASE SELECT THE DRIVER", Toast.LENGTH_LONG)
                                .show();
                    } else {

                        mSpinnerVehicleNo = "";
                        db.open();
                        mEmpId = db
                                .checkDrvierTableforDataExist(mSpinnerDriver);
                        db.close();

                        send = new SendToWebService(getActivity(),
                                mAddAdvanceFragment);

                        // if (send.isConnectingToInternet()) {
                        try {
                            send.execute("1", "ManageAdvance",
                                    mEmpId, mSpinnerVehicleNo, mTypeOfAdvance,
                                    mUzrDate, mVoucherNum, mAmount, voucherImg, "4");

                        } catch (Exception e) {
                            Toast.makeText(getActivity().getBaseContext(),
                                    "Try after sometime...", Toast.LENGTH_SHORT)
                                    .show();
                            ExceptionMessage.exceptionLog(getActivity(), this
                                    .getClass().toString()
                                    + " "
                                    + "[onSaveAdvance]", e.toString());

                        }

                    }
                }
                else if (mTypeOfAdvance.equals("Cleaner Advance")) {
                    if (mSpinnerCleaner.equals("Select The Cleaner")) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "PLEASE SELECT THE CLEANER", Toast.LENGTH_LONG)
                                .show();
                    } else {

                        mSpinnerVehicleNo = "";
                        db.open();
                        mEmpId = db
                                .checkCleanerTableforDataExist(mSpinnerCleaner);
                        db.close();
                        send = new SendToWebService(getActivity(),
                                mAddAdvanceFragment);

                        // if (send.isConnectingToInternet()) {
                        try {
                            send.execute("1", "ManageAdvance",
                                    mEmpId, mSpinnerVehicleNo, mTypeOfAdvance,
                                    mUzrDate, mVoucherNum, mAmount, voucherImg, "4");

                        } catch (Exception e) {
                            Toast.makeText(getActivity().getBaseContext(),
                                    "Try after sometime...", Toast.LENGTH_SHORT)
                                    .show();
                            ExceptionMessage.exceptionLog(getActivity(), this
                                    .getClass().toString()
                                    + " "
                                    + "[onSaveAdvance]", e.toString());
                        }

                    }
                }


        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "NETWORK PROBLEM!" + " \nPLEASE TRY AFTER SOMETIME",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onSaveAdvance]", e.toString());
        }

    }

    public void alertDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("SIGN HERE");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_layout, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();

        mContent = (LinearLayout) dialogView.findViewById(R.id.mysignature);
        ok = (Button) dialogView.findViewById(R.id.btnReg);
        clear = (Button) dialogView.findViewById(R.id.btnClear);
        mSignature = new Signature(getActivity(), null);
        mContent.addView(mSignature);

        //  s=restore.toString();

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.clear();
            }
        });

        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
          try
            {
                alertDialog.dismiss();
                signMatch();
            }
          catch (Exception e)
          {
              ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                      .toString() + " " + "[alertDialog()-ok button Click]", e.toString());
             Toast.makeText(getActivity(),"something is missing..!!",Toast.LENGTH_LONG).show();
          }

           }});
        alertDialog.show();

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            AdvanceMain.pos=1;
        }

    }

}

