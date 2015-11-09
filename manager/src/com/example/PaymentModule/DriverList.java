/*
 * Purpose - DriverList It shows all the drivers from the database
 * @author - Pravitha 
 * Created on May 22, 2014
 * Modified on June 10, 2014
 */
package com.example.PaymentModule;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.AbsenceReport.DatePickerFragment;
import com.example.Interface.IDriverCleanerPayment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DriverList extends Fragment  implements View.OnClickListener,IDriverCleanerPayment {
    View view;
    ListView driverList;
    TextView driver;
    DBAdapter db;
    static String sDriverName, sEmployeeType = "Driver";

    TextView driverPayBtnFromDate, driverPayBtnToDate;
    public String mFromDate, mToDate;

    final IDriverCleanerPayment mDriverCleaner = this;

    int cleanerCommission = 0;
    String mDateType, mUzrDate, mDriverName, mEmployeeId,
            mVehicleNumber, mSourceName, mDestinationName, mCleanerName,
            mStdMileage, mGivenMileage, mDriverSalary, mDriverCommission,mExpenseAmount;

    double mTotalTripAmountNoCleaner = 0.0, mTotalTripAmountWithCleaner = 0.0,
            mTripKMTotal = 0, mTotalMoney = 0.0, mAdvance = 0.0,
            mDeduction = 0.0, mTotalDriAdvanceAmount = 0,
            mTotalVehAdvanceAmount = 0, mTotalFuelLossLtrs = 0;

    static String sDriAdvance, sVehAdvance, sCommissionString, sMileage="0.0",
            sDriverSalary,sExpense, sAmount, pa, fDate, tDate, fromDate, toDate;

    MatrixCursor cursorTrip;
    Button paidList;
    LinearLayout noDataLayout;
    CustomAlertDialog ald;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater
                .inflate(R.layout.fragment_driver_list, container, false);
        db = new DBAdapter(getActivity());
        driverList = (ListView) view.findViewById(R.id.fragmentDriverListLV);
        ald=new CustomAlertDialog();
        bindData();
        driverPayBtnFromDate.setOnClickListener(this);
        driverPayBtnToDate.setOnClickListener(this);
        paidList.setOnClickListener(this);
        return view;
    }

    private void bindData() {
        driverPayBtnFromDate = (TextView) view
                .findViewById(R.id.fragmentDriverPaymentBtnFromDate);
        driverPayBtnToDate = (TextView) view
                .findViewById(R.id.fragmentDriverPaymentBtnToDate);
        paidList = (Button) view.findViewById(R.id.fragmentDriverPaymentListBtn);
        noDataLayout = (LinearLayout)view.findViewById(R.id.inboxLinearL);
    }
	/*
     * Purpose - Loads data from local database to Listview Event Name -
	 * onStart() Parameters - No parameter Return Type - No Return Type
	 */

    @Override
    public void onStart() {
        super.onStart();
        db = new DBAdapter(getActivity());
        try {
            db.open();
            Cursor vehicleCursor = db.getData(DBAdapter.getEmployeeDetails(), "Driver");
            int count=vehicleCursor.getCount();
            if(count>0)
            {
                noDataLayout.setVisibility(View.GONE);
             driverList.setVisibility(View.VISIBLE);
            String from[] = {DBAdapter.getKeyName()};
            int to[] = {R.id.vehNumbersListViewTrackVehicle};

            SimpleCursorAdapter caCleaner1 = new SimpleCursorAdapter(
                    getActivity(), R.layout.account3, vehicleCursor, from, to,
                    0);
            driverList.setAdapter(caCleaner1);
            driverList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View selectedView, int arg2, long arg3) {
                    System.out.println(driverPayBtnFromDate.getText().toString()+","+ driverPayBtnToDate.getText().toString());
                    if ( driverPayBtnFromDate.getText().toString().equals("FROM DATE") &&
                            driverPayBtnToDate.getText().toString().equals("TO DATE"))
                    {
                        ald.alertDialog(getActivity(),"Please select from date and to Date !");
                    }
                    else if( driverPayBtnToDate.getText().toString().equals("TO DATE"))
                     {
                         ald.alertDialog(getActivity(),"Please select to Date !");
                     }
                    else
                    {
                        driver = (TextView) selectedView.findViewById(R.id.vehNumbersListViewTrackVehicle);

                        sDriAdvance = null;
                        sVehAdvance = null;
                        sCommissionString = null;
                        sMileage = null;
                        sDriverSalary = null;
                        sExpense = null;
                        sAmount = null;

                        sDriverName = driver.getText().toString();
                        System.out.println(sDriverName);
                        sEmployeeType = "Driver";
                        CleanerList.sEmployeeType = null;

                        mDriverName = sDriverName;
                        db.open();
                        // mEmployeeId = "218";
                        mEmployeeId = db.checkDrvierTableforDataExist(mDriverName);
                        db.close();
                        SendToWebService send1 = new SendToWebService(
                                getActivity(), mDriverCleaner);
//                        if (send1.isConnectingToInternet()) {
                        try {

                            send1.execute("6", "GetDriverPaymentDetails",
                                    mEmployeeId, mFromDate, mToDate);
                        } catch (SQLiteException e) {
                            ExceptionMessage.exceptionLog(getActivity(),
                                    this.getClass().toString() + " "
                                            + "[MyDatePickerDilog]",
                                    e.toString());
                        } catch (Exception e) {
                            Toast.makeText(getActivity(),
                                    "Try after sometime...",
                                    Toast.LENGTH_SHORT).show();
                            ExceptionMessage.exceptionLog(getActivity(),
                                    this.getClass().toString() + " "
                                            + "[MyDatePickerDilog]",
                                    e.toString());
                        }
                    }
                }
            });
            db.close();
            }
            else {
                noDataLayout.removeAllViews();
                noDataLayout.setVisibility(View.VISIBLE);
                driverList.setVisibility(View.GONE);
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageResource(R.drawable.nodata);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.topMargin = 100;
                imageView.setLayoutParams(layoutParams);
                noDataLayout.addView(imageView);

                TextView textView=new TextView(getActivity());
                textView.setText("NO DRIVER LIST");
                textView.setTextSize(14);
                textView.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.gravity = Gravity.CENTER;
                layoutParams1.topMargin = 20;
                textView.setLayoutParams(layoutParams1);
                noDataLayout.addView(textView);
            }
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onStart]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onStart]", e.toString());
            Toast.makeText(getActivity(), "Please Select From Date!! ", Toast.LENGTH_LONG).show();
        }
    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), PaymentDriver.class);
        startActivity(intent);

    }

    @Override
    public void ongetCleanerPaymentDetails(String response) {

    }

    @Override
    public void ongetDriverPaymentDetails(String response) {
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
                driverParser(response);
                driverDisplayDialog();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();

            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[ongetDriverPaymentDetails]",
                    e.toString());
        }

    }

    @Override
    public void onManagePaymentDetails(String response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fragmentDriverPaymentBtnFromDate:
                try {

                    mDateType = "from";
                    MyDatePickerDialog();
                    fDate = mFromDate;

                } catch (Exception e) {
                    Toast.makeText(getActivity(),
                            "Please Select Date!! ",
                            Toast.LENGTH_LONG).show();

                        ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                        .toString() + " " + "[R.id.fragmentDriverPaymentBtnFromDate]",
                                e.toString());
                }
                break;

            case R.id.fragmentDriverPaymentBtnToDate:
                try {
                    if (driverPayBtnFromDate.getText().toString().equals("FROM DATE")) {
                        ald.alertDialog(getActivity(), "Please select From date !");
                    } else {
                        mDateType = "to";
                        MyDatePickerDialog();
                        tDate = mToDate;
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(),
                            "Please Select From Date!! ",
                            Toast.LENGTH_LONG).show();
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[R.id.fragmentDriverPaymentBtnToDate]",
                            e.toString());
                }
                break;

            case R.id.fragmentDriverPaymentListBtn:
                Intent intent = new Intent(getActivity(), DriverPaidList1.class);
                startActivity(intent);
                break;
        }
    }

    public void MyDatePickerDialog() {

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

            mUzrDate = (String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-" + String
                    .valueOf(dayOfMonth));
            if (mDateType.equals("from")) {
                driverPayBtnFromDate.setText(mUzrDate);
                mFromDate = mUzrDate;
                fromDate = mUzrDate;
                fromDateValidation();
            } else if (mDateType.equals("to")) {
                driverPayBtnToDate.setText(mUzrDate);

                mToDate = mUzrDate;
                toDate = mUzrDate;
                toDateValidation();
            } else
            {
               ald.alertDialog(getActivity(),"Please select date !");
            }
        }


    };

/*
	 * Purpose - Parse the data got from the server Method Name -driverParser
	 * Parameters - response Return Type - String JsonData Variable list String
	 * jsonData - To get the data with tag d JSONArray
	 */


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void driverParser(String response) {
        String jsonData = null;
        String statuschk = null;
        if (response != null)
            mTotalTripAmountNoCleaner = 0.0;
        mTotalTripAmountWithCleaner = 0.0;
        mTripKMTotal = 0;
        mTotalMoney = 0.0;
        mAdvance = 0.0;
        mDeduction = 0.0;
        mTotalDriAdvanceAmount = 0;
        mTotalVehAdvanceAmount = 0;
        mExpenseAmount="0";

        double tripKMDouble = 0.0;
        sDriAdvance="0.0"; sVehAdvance="0.0"; sCommissionString="0.0"; sMileage="0.0";
                sDriverSalary="0.0";sExpense="0.0"; sAmount="0.0";

        try {
            // TO CONVERT THE STRING TO OBJECT
            JSONObject jsonResponse = new JSONObject(response);

            // GET THE VALUE OF d:
            jsonData = jsonResponse.getString("d");

            // INITIALIZING THE ARRAY
            JSONArray paymentArray = new JSONArray(jsonData);

            JSONObject status1 = paymentArray.getJSONObject(0);
            statuschk = status1.getString("status").trim();

            if (statuschk.equals("invalid authkey")) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), statuschk);
            } else if (statuschk.equals("employee does not exist")) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), statuschk);
            }


            else if (statuschk.equals("OK")) {
                // TO CREATE A CUSTOM
                String[] columnNames = {"_id", "date", "vehicle No",
                        "destination", "cleaner", "total kms"};
                String cleanerId;

                cursorTrip = new MatrixCursor(columnNames);

                int key = 1;
                JSONArray tripArray = null;
                //   --------------------------------------
                //Trip array
                //JSONObject tripDetail = paymentArray.getJSONObject(1);
                try {
                     tripArray = paymentArray.getJSONArray(1);
                    if (tripArray.length() != 0) {

                        for (int i = 0; i < tripArray.length(); i++) {

                            JSONObject c = tripArray.getJSONObject(i);

                            String tripAmount = c.getString("tripAmount");

                            if (!tripAmount.equals("0.0") && !tripAmount.equals("0")) {

                                mVehicleNumber = c.getString("vehicleNumber");
                                String tripKM = c.getString("totalKm");
                                mSourceName = c.getString("sourceName");
                                mDestinationName = c.getString("destinationName");

                                String tripStartDate = c.getString("tripStartDate");
                                String[] parts = tripStartDate.split("T");
                                tripStartDate = parts[0];

                                cleanerId = c.getString("cleaner");
                                if (!cleanerId.equals("") && cleanerId != null) {

                                    mCleanerName = cleanerId;
                                } else {
                                    mCleanerName = "NO CLEANER";
                                    db.open();
                                    cleanerCommission = db.getCleanerCommission(
                                            mCleanerName, DBAdapter.getEmployeeDetails());
                                    db.close();
                                    if (cleanerCommission == 0) {
                                        alertInsert();
                                    }

                                }


                                if (!cleanerId.equals("")
                                        || !mCleanerName.equals("NOT EXIST")) {

                                    double tripAmountLocal = Double
                                            .parseDouble(tripAmount);

                                    // TOTAL AMOUNT WITH OUT CLEANER (ADD CLEANER
                                    // COMMISSION AS BONUS)
                                    mTotalTripAmountNoCleaner = mTotalTripAmountNoCleaner
                                            + tripAmountLocal;

                                } else {
                                    double tripAmountLocal2 = Double
                                            .parseDouble(tripAmount);

                                    // TRIP AMOUNT WITH CLEANER (ONLY DRIVER COMMISSION)
                                    mTotalTripAmountWithCleaner = mTotalTripAmountWithCleaner
                                            + tripAmountLocal2;

                                }
                                tripKMDouble = Double.parseDouble(tripKM);

                                // TOTAL KMS DROVE BY THE DRIVER
                                mTripKMTotal = mTripKMTotal + tripKMDouble;
                                cursorTrip.addRow(new Object[]{key, tripStartDate,
                                        mVehicleNumber, mDestinationName, mCleanerName,
                                        tripKMDouble});
                                key++;

                            }
                        }
                    }


                    cursorTrip.close();
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[jsonParsing-tripArray]",
                            e.toString());
                    e.printStackTrace();
                }
                //Vehicle Advance array
                //   --------------------------------------------------------------

                // JSONObject vehicleAdvanceDetail = paymentArray.getJSONObject(2);
                try {
                    JSONArray vehiclAdvanceArray = paymentArray.getJSONArray(2);
                    if (tripArray.length() != 0) {

                        for (int j = 0; j < vehiclAdvanceArray.length(); j++) {

                            JSONObject va = vehiclAdvanceArray.getJSONObject(j);

                            String vehicleAdvanceAmountString = va.getString("advanceAmount");

                            if (!vehicleAdvanceAmountString.equals("0.0")) {
                                // String advanceDate = c.getString("AdvanceDate");
                                va.getString("advanceDate");
                                double totalAdvanceDouble = Double
                                        .parseDouble(vehicleAdvanceAmountString);

                                // TOTAL VEHICLE ADVANCE RECEIVED
                                mTotalVehAdvanceAmount = mTotalVehAdvanceAmount
                                        + totalAdvanceDouble;
                            }

                        }

                    }
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[jsonParsing-vehiclAdvanceArray]",
                            e.toString());
                    e.printStackTrace();
                }
                //--------------------------------------------------------------------
                try {

                    //  Driver Advance Array
                    // JSONObject driverAdvanceDetail = paymentArray.getJSONObject(3);
                    JSONArray driverAdvanceArray = paymentArray.getJSONArray(3);
                    if (driverAdvanceArray.length() != 0) {

                        for (int k = 0; k < driverAdvanceArray.length() ; k++) {

                            JSONObject da = driverAdvanceArray.getJSONObject(k);

                            String driverAdvance1 = da.getString("advanceAmount");

                            if (!driverAdvance1.equals("0.0")) {
                                double totalAdvanceDouble2 = Double
                                        .parseDouble(driverAdvance1);

                                // TOTAL DRIVER ADVANCE RECEIVED
                                mTotalDriAdvanceAmount = mTotalDriAdvanceAmount
                                        + totalAdvanceDouble2;

                            }


                        }

                    }
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[jsonParsing-driverAdvanceArray]",
                            e.toString());
                    e.printStackTrace();
                }
                // --------------------------------------------------------------------------------------------
                //  Fuel Array
                //  JSONObject fuelDetail = paymentArray.getJSONObject(4);

                try {
                    JSONArray fuelArray = paymentArray.getJSONArray(4);
                    if (fuelArray.length() != 0) {

                        for (int l = 0; l < fuelArray.length(); l++) {
                            JSONObject f = fuelArray.getJSONObject(l);

                            String Fuel = f.getString("fuelConsumed");
                            if (!Fuel.equals("0.0")) {
                                // vehicleNumber=c.getString("VehicleNumber");
                                mStdMileage = f.getString("standardMileage");
                                mGivenMileage = f.getString("givenMileage");
                                double fuelDouble = Double
                                        .parseDouble(Fuel);
                                double stdMlgDouble = Double
                                        .parseDouble(mStdMileage);
                                double gvnMlgDouble = Double
                                        .parseDouble(mGivenMileage);
                                double fuelLoss = ((stdMlgDouble * fuelDouble) - (gvnMlgDouble * fuelDouble))
                                        / stdMlgDouble;

                                // TOTAL FUEL LOSS IN LTRS
                                mTotalFuelLossLtrs = mTotalFuelLossLtrs
                                        + fuelLoss;

                            }
                        }
                    }
                }

                catch (Exception e){
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[jsonParsing-fuelArray]",
                            e.toString());
                    e.printStackTrace();
                }
                //------------------------------------------------------------------------------------------
                //Commission and salary

                //JSONObject commSalaryDetail = paymentArray.getJSONObject(5);

                try {
                    JSONArray commSalaryArray = paymentArray.getJSONArray(5);
                    if (commSalaryArray.length() != 0) {


                            JSONObject sal = commSalaryArray.getJSONObject(0);
                            mDriverSalary = sal.getString("salary");

                            JSONObject com = commSalaryArray.getJSONObject(1);
                            mDriverCommission = com.getString("commission");

                            JSONObject expense = commSalaryArray.getJSONObject(2);
                            mExpenseAmount = expense.getString("expenseAmount");


                    }
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[jsonParsing-commSalaryArray]",
                            e.toString());
                    e.printStackTrace();
                }

                getDriverData();
            }

            else {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[driverParser]", statuschk);
            }

        }


        //------------------------------------------------------------------------------------------

        catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[driverParser]", e.toString());
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

            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[driverParser]", e.toString());
        }

    }

    private void driverDisplayDialog() {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setTitle("Trip Details");

            dialog.setContentView(R.layout.dialog_driver_data_list);
            ListView dialogDriverDataList = (ListView) dialog.findViewById(R.id.dialogDriverDataList);
            Button dialogDriverDataListBtnOk = (Button) dialog.findViewById(R.id.dialogDriverDataListBtnOk);


            String from[] = {"date", "total kms", "vehicle no", "cleaner", "destination"};
            int to[] = {R.id.txtDate, R.id.txtKMtravelled,
                    R.id.txtVehicleNo,
                    R.id.txtNameDriverCleaner, R.id.txtDestination};

            // @SuppressWarnings("deprecation")
            SimpleCursorAdapter caCleaner1 = new SimpleCursorAdapter(
                    getActivity(), R.layout.driverdatadetails, cursorTrip,
                    from, to, 0);
            dialogDriverDataList.setAdapter(caCleaner1);

            dialogDriverDataListBtnOk
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                            ((PaymentDriver) getActivity()).setCurrentItem(1, true);

                        }
                    });

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[driverDisplayDialog]", e.toString());
        }

    }

    public void getDriverData() {

        try {

            sDriAdvance = String.valueOf(mTotalDriAdvanceAmount);

            sVehAdvance = String.valueOf(mTotalVehAdvanceAmount);

            sDriverSalary = mDriverSalary;

            sExpense=mExpenseAmount;

            System.out.println(sDriverSalary);
            double dCommission = Double.valueOf(mDriverCommission);
            double CommissionA = ((mTotalTripAmountNoCleaner * (cleanerCommission + dCommission)) / 100)
                    + ((mTotalTripAmountWithCleaner * dCommission) / 100);
            sCommissionString = Double.toString(CommissionA);

            double MileageDeductionLtr = Double.valueOf(mTotalFuelLossLtrs);
            DecimalFormat numberFormat = new DecimalFormat("##.00");
            sMileage=String.valueOf(numberFormat.format(MileageDeductionLtr));
          //  sMileage = Double.toString(MileageDeductionLtr);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[getDriverData]", e.toString());
        }
    }

    private void alertInsert() {
        ald.alertDialog(getActivity(),"Sorry There is no Cleaner !");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            PaymentDriver.pos=1;
        }
    }

    public void fromDateValidation()
    {
        try{
            String today = fromDate;
            DateFormat format = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
           Date dateToday = format.parse(today);

            Calendar calendar = Calendar.getInstance();
            Date tomo = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            DateFormat format2 = new SimpleDateFormat("yyyy-M-dd");
            String tomDate = format2.format(tomorrow);
           Date nextday = format.parse(tomDate);
            if(dateToday.after(nextday)||dateToday.equals(nextday))
            {
                ald.alertDialog(getActivity(),"From Date cannot be greater than Today's Date !");
                driverPayBtnFromDate.setText("FROM DATE");

            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[fromDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
    }

    public void toDateValidation()
    {
        try{
            String today = toDate;
            DateFormat format = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
            Date dateToday = format.parse(today);

            Calendar calendar = Calendar.getInstance();
            Date tomo = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            DateFormat format2 = new SimpleDateFormat("yyyy-M-dd");
            String tomDate = format2.format(tomorrow);
            Date nextday = format.parse(tomDate);


            Date preFromDate = format.parse(fromDate);

            if(dateToday.after(nextday) || dateToday.equals(nextday))
            {
                ald.alertDialog(getActivity(),"To Date cannot be greater than Today's Date !");
                driverPayBtnToDate.setText("TO DATE");
            }
            else if(dateToday.before(preFromDate)){
                ald.alertDialog(getActivity(),"To Date cannot be less than than From Date !");
                driverPayBtnToDate.setText("TO DATE");
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[toDateValidation]",
                    e.toString());
            e.printStackTrace();
        }
    }
}