package com.example.Expense;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AbsenceReport.DatePickerFragment;
import com.example.AdvanceModule.AdvanceMain;
import com.example.AdvanceModule.AdvanceVehicleFragment;
import com.example.Interface.IAddExpense;
import com.example.ManageResources.DriverList;
import com.example.ManageResources.VehicleList;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class AddExpense extends Fragment implements View.OnClickListener, IAddExpense {

    View view;
    DBAdapter db;
    int year, month, day;
    Bitmap bitmap;
    SendToWebService send;
    List<String> label1, label2;
    String mSpinnerVehicleNo, mSpinnerDriver, mUzrDate, mVoucherNum, mParticular, mAmount = "0", mEmpId, parsedString = null,
            LocalVoucherNo, srvrStatus, srvrVoucher,updateExpenseId;
    final int CAMERA_CAPTURE = 1;
    byte[] byteArray,receipt;
   // ImageView fragmentExpenseAddImgVReceipt;
    TextView fragmentExpenseAddTvDate;
    Spinner fragmentExpenseAddSpinnerVehicle, fragmentExpenseAddSpinnerDriver;
    EditText fragmentExpenseAddEdtVoucherNo, fragmentExpenseAddEdtParticular, fragmentExpenseAddEdtAmount;
    Button fragmentExpenseAddBtnAdd, fragmentExpenseAddBtnView;
    final IAddExpense mAddExpense = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_expense_add, container,false);
        db = new DBAdapter(getActivity());
        bindData();
        fragmentExpenseAddBtnAdd.setText("ADD");
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        fragmentExpenseAddTvDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("-").append(month + 1).append("-")
                .append(day).append(" "));
        loadVehicleSpinnerData();
        loadDriverSpinnerData();
//        fragmentExpenseAddBtnAdd.setText("ADD");

        fragmentExpenseAddBtnAdd.setOnClickListener(this);
        fragmentExpenseAddBtnView.setOnClickListener(this);
        fragmentExpenseAddTvDate.setOnClickListener(this);
        //fragmentExpenseAddImgVReceipt.setOnClickListener(this);
        return view;
    }

    private void bindData() {
        //fragmentExpenseAddImgVReceipt = (ImageView) view.findViewById(R.id.fragmentExpenseAddImgVReceipt);
        fragmentExpenseAddTvDate = (TextView) view.findViewById(R.id.fragmentExpenseAddTvDate);
        fragmentExpenseAddSpinnerVehicle = (Spinner) view.findViewById(R.id.fragmentExpenseAddSpinnerVehicle);
        fragmentExpenseAddSpinnerDriver = (Spinner) view.findViewById(R.id.fragmentExpenseAddSpinnerDriver);
        fragmentExpenseAddEdtVoucherNo = (EditText) view.findViewById(R.id.fragmentExpenseAddEdtVoucherNo);
        fragmentExpenseAddEdtParticular = (EditText) view.findViewById(R.id.fragmentExpenseAddEdtParticular);
        fragmentExpenseAddEdtAmount = (EditText) view.findViewById(R.id.fragmentExpenseAddEdtAmount);
        fragmentExpenseAddBtnAdd = (Button) view.findViewById(R.id.fragmentExpenseAddBtnAdd);
        fragmentExpenseAddBtnView = (Button) view.findViewById(R.id.fragmentExpenseAddBtnView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.fragmentExpenseAddImgVReceipt:
//                try {
//                    // use standard intent to capture an image
//                    Intent captureIntent = new Intent(
//                            MediaStore.ACTION_IMAGE_CAPTURE);
//                    // we will handle the returned data in onActivityResult
//                    startActivityForResult(captureIntent, CAMERA_CAPTURE);
//                } catch (Exception e) {
//                    ExceptionMessage.exceptionLog(getActivity(), this
//                            .getClass().toString()
//                            + " "
//                            + "[fragmentExpenseAddImgVReceipt.setOnClickListener]", e.toString());
//                }
//                break;

            case R.id.fragmentExpenseAddTvDate:
                MyDatePickerDialog();
                break;

            case R.id.fragmentExpenseAddBtnAdd:
                onSaveAdvance();
                break;

            case R.id.fragmentExpenseAddBtnView:
                ((Expense) getActivity()).setCurrentItem(0, true);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == CAMERA_CAPTURE && resultCode == getActivity().RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
             //   fragmentExpenseAddImgVReceipt.setImageBitmap(thumbnail);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byteArray = stream.toByteArray();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Sorry Unable to get Data",
                    Toast.LENGTH_LONG).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onActivityResult]", e.toString());
        }

    }

    @Override
    public void onAddExpense(String response) {

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
                jsonParsing(response).trim();
                saveExpense(srvrStatus);

            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[onAddExpense]",
                    e.toString());
        }
    }

//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        try {
//            if (getView() != null) {
//               // isViewShown = true;
//                // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
//               // fetchData();
//            } else {
//                //isViewShown = false;
//            }
//        }
//        catch(Exception e){
//
//        }
//    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        try {
            if (visible) {
                try {
                    if (ExpenseList.expenseListVoucherNum != null) {
                        switch (ExpenseList.expenseListVoucherNum) {
                            //if (ExpenseList.expenseListVoucherNum != "" && ExpenseList.expenseListVoucherNum!=null && ExpenseList.expenseCursor.getCount()!=0) {
                            case "Refresh":
                                refreshActivity();
                                break;
                            default:
                                populateData(ExpenseList.expenseListVoucherNum);
                                ExpenseList.expenseListVoucherNum = null;
                             //   ExpenseList.expenseCursor = null;
                                ExpenseList.rowid = 0;
                                break;


                        }
                    }
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[setMenuVisibility]", e.toString());
                }
            }
        }
        catch(Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[setMenuVisibility]", e.toString());
        }
    }

    private void loadVehicleSpinnerData() {
        try {
            db.open();
            label2 = db.getAllLabels(DBAdapter.getVehicleDetails());
            label2.add(0, "Select The Vehicle");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    label2);
          //  dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fragmentExpenseAddSpinnerVehicle.setAdapter(dataAdapter);
            fragmentExpenseAddSpinnerVehicle
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView,
                                                   View view, int i, long l) {

                            mSpinnerVehicleNo = label2.get(i);
                        }

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

    private void loadDriverSpinnerData() {
        try {
            db.open();
            label1 = db.getAllLabels(DBAdapter.getEmployeeDetails(), "Driver");
            label1.add(0, "Select The Driver");
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    label1);
      //      dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fragmentExpenseAddSpinnerDriver.setAdapter(dataAdapter);
            fragmentExpenseAddSpinnerDriver
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        public void onItemSelected(AdapterView<?> adapterView,
                                                   View view, int i, long l) {

                            mSpinnerDriver = label1.get(i);
                        }

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

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            fragmentExpenseAddTvDate.setText(String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-"
                    + String.valueOf(dayOfMonth));
        }
    };


    public void onSaveAdvance() {
        getData();
        if (mUzrDate.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "PLEASE SELECT THE DATE", Toast.LENGTH_LONG).show();
        } else if (mSpinnerVehicleNo.equals("Select The Vehicle")) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "PLEASE SELECT THE VEHICLE", Toast.LENGTH_LONG)
                    .show();
        } else if (mSpinnerDriver.equals("Select The Driver")) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "PLEASE SELECT THE DRIVER", Toast.LENGTH_LONG)
                    .show();
        } else if (mVoucherNum.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "PLEASE ENTER THE VOUCHER NUMBER", Toast.LENGTH_LONG)
                    .show();
        } else if (mParticular.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "PLEASE ENTER THE PARTICULAR", Toast.LENGTH_LONG)
                    .show();
        } else if (mAmount.equals("") || mAmount.equals(".00")) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "PLEASE ENTER THE AMOUNT", Toast.LENGTH_LONG).show();
        } else {
            try {
                db.open();
                mEmpId = db
                        .checkDrvierTableforDataExist(mSpinnerDriver);
                db.close();


                if( fragmentExpenseAddBtnAdd.getText().toString().equals("UPDATE"))
                {
                    alertUpdate();
                } else {

                  //  String receipt=Base64.encodeToString(byteArray, Base64.DEFAULT);

                    send = new SendToWebService(getActivity(), mAddExpense);
                    try {
                        send.execute("35", "ManageExpense",
                                mEmpId, mSpinnerVehicleNo, mUzrDate, mVoucherNum, mParticular, mAmount, "", "4","0");

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
            } catch (Exception e) {

            }
        }

    }

    public void getData() {
       // getBytesFromBitmap(fragmentExpenseAddImgVReceipt);
        mUzrDate = fragmentExpenseAddTvDate.getText().toString();
        mSpinnerVehicleNo = String.valueOf(fragmentExpenseAddSpinnerVehicle.getSelectedItem());
        mSpinnerDriver = String.valueOf(fragmentExpenseAddSpinnerDriver.getSelectedItem());
        mVoucherNum = fragmentExpenseAddEdtVoucherNo.getText().toString();
        mParticular = fragmentExpenseAddEdtParticular.getText().toString();
        mAmount = fragmentExpenseAddEdtAmount.getText().toString();
        if (mAmount.equals("")) {
            mAmount = "0";
        }
        double number = Double.parseDouble(mAmount);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        mAmount = numberFormat.format(number);


    }

    public byte[] getBytesFromBitmap(ImageView DriverPhoto2) {
        try {
            DriverPhoto2.buildDrawingCache();
            Bitmap bmap = DriverPhoto2.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error while Uploading Photo",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
        return byteArray;
    }

    public String jsonParsing(String response) {
        String jsonData = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                srvrStatus = d.getString("status").trim();
                //srvrVoucher = d.getString("voucherNo").trim();
                return jsonData;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing]", e.toString());
            }
        return jsonData;
    }

    public void saveExpense(String parsedString) {

        try {

            switch (parsedString) {
                case "inserted":

                        Toast.makeText(getActivity().getApplicationContext(),
                                "EXPENSE SAVED", Toast.LENGTH_LONG).show();

                    refreshActivity();
                    break;

                case "voucher already exist":
                    Toast.makeText(getActivity().getApplicationContext(),
                            "VOUCHER NUMBER ALREADY EXISTS", Toast.LENGTH_LONG)
                            .show();
                    break;
					
				case "cannot be changed beyond 30 days":
                    Toast.makeText(getActivity().getApplicationContext(),
                            "EXPENSE CANNOT BE MODIFIED AFTER 30 DAYS", Toast.LENGTH_LONG)
                            .show();
                    break;

                case "employee does not exist":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveExpense]",
                            parsedString);
                    break;
					
				

                case "updated":
//                    db.open();
//
//                    long rowsaffected = db.updateExpense(
//                            DBAdapter.getExpensedetails(), cv, srvrVoucher);
                    //if (rowsaffected != -1) {
                        Toast.makeText(getActivity(), "Data Updated",
                                Toast.LENGTH_LONG).show();
//                    }
//                    db.close();
                    refreshActivity();
//                    ((Expense) getActivity()).setCurrentItem(0, true);
                    break;


                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveExpense]",
                            parsedString);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveExpense]",
                            parsedString);
                    break;

                default:
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveExpense]",
                            parsedString);
                    break;

            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[saveExpense]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[saveExpense]", e.toString());
        }

    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), Expense.class);
        startActivity(intent);

    }

    private void alertUpdate() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Do u want to update the item?");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

//                try {
//                    String receipt = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                }
//                catch(Exception e){
//
//                }
                SendToWebService send = new SendToWebService(getActivity(),
                        mAddExpense);
                send.execute("35", "ManageExpense",
                       mEmpId, mSpinnerVehicleNo, mUzrDate, mVoucherNum, mParticular, mAmount, "", "1",updateExpenseId);

            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getActivity(), "Update Cancelled",
                                Toast.LENGTH_LONG).show();
                    }
                });

        alert.show();

    }

    public void populateData(String voucher) {
        try {

            MatrixCursor cursor=ExpenseList.expenseCursor;

            if (cursor.moveToFirst()) {
                for (int i = 0; i <cursor.getCount(); i++) {

                    if(i==ExpenseList.rowid) {
                        fragmentExpenseAddBtnAdd.setText("UPDATE");
                        updateExpenseId=cursor.getString(1);
                        fragmentExpenseAddTvDate.setText(cursor.getString(3));
                        fragmentExpenseAddEdtVoucherNo.setText(cursor.getString(5));
                        fragmentExpenseAddEdtParticular.setText(cursor.getString(4));
                        fragmentExpenseAddEdtAmount.setText(cursor.getString(6));
                        mSpinnerDriver=cursor.getString(2);
                        mSpinnerVehicleNo=ExpenseList.veh;
                        fragmentExpenseAddSpinnerVehicle.setSelection(getIndex(fragmentExpenseAddSpinnerVehicle, ExpenseList.veh));
                        fragmentExpenseAddSpinnerDriver.setSelection(getIndex(fragmentExpenseAddSpinnerDriver, mSpinnerDriver));

                        mVoucherNum=cursor.getString(5);
                        mParticular=cursor.getString(4);
                        mAmount=cursor.getString(6);
                        mUzrDate=cursor.getString(3);
                        String Receipt = cursor.getString(7);

                    }

                    cursor.moveToNext();
                }
            }
            cursor.close();


        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[populate]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[populate]", e.toString());
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

     public byte [] StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
            return byteArray;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            Expense.pos=2;
        }

    }

}
