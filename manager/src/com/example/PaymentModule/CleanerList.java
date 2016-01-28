/*
 * Purpose - DriverList It shows all the cleaners from the database
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
import android.content.res.Resources;
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
import com.example.anand_roadwayss.Welcome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CleanerList extends Fragment implements View.OnClickListener,IDriverCleanerPayment {

    View view;
    ListView cleanerList;
    TextView cleaner;
    DBAdapter db;
    static String sCleanerName, sEmployeeType,fromDate,toDate;
    LinearLayout noDataLayout;
    TextView driverPayBtnFromDate, driverPayBtnToDate;
    public String mFromDate, mToDate;
    final IDriverCleanerPayment mDriverCleaner = this;
    String mDateType, mUzrDate, mDriverName, mEmployeeId, mCommissionString, salaryString;
    double mTotalTripAmount = 0.0, mTotalAdvanceAmount = 0.0,
            mCommission = 0.0;
    static String  sCleanerAdvance, sCleanerCommissionString,
            sCleanerSalary, sAmount, pa, fDate, tDate;
    MatrixCursor cursorCleanerTrip;
    Button paidList;
    TextView ok, message;
    CustomAlertDialog ald;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sEmployeeType = "Cleaner";

        view = inflater.inflate(R.layout.fragment_cleaner_list, container,
                false);
        noDataLayout = (LinearLayout)view.findViewById(R.id.inboxLinearL);
        cleanerList = (ListView) view.findViewById(R.id.fragmentCleanerListLV);
        ald=new CustomAlertDialog();
        bindData();
        driverPayBtnFromDate.setOnClickListener(this);
        driverPayBtnToDate.setOnClickListener(this);
        paidList.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        db = new DBAdapter(getActivity());
        try {
            db.open();

            Cursor vehicleCursor = db.getData(DBAdapter.getEmployeeDetails(),
                    "Cleaner");
            int count = vehicleCursor.getCount();
            if (count > 0) {
                noDataLayout.setVisibility(View.GONE);
                cleanerList.setVisibility(View.VISIBLE);
                // LIST OF VEHICLES
                String from[] = {DBAdapter.getKeyName()};
                int to[] = {R.id.vehNumbersListViewTrackVehicle};
                // @SuppressWarnings("deprecation")
                SimpleCursorAdapter caCleaner1 = new SimpleCursorAdapter(
                        getActivity(), R.layout.account3, vehicleCursor, from, to,
                        0);
                cleanerList.setAdapter(caCleaner1);
                     //all type date validation.
                cleanerList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View selectedView, int arg2, long arg3)
                    {
                        if ( driverPayBtnFromDate.getText().toString().equals("FROM DATE") &&
                                driverPayBtnToDate.getText().toString().equals("TO DATE"))
                        {
                            ald.alertDialog(getActivity(),"Please select from date and to Date !");
                        }
                        else if( driverPayBtnToDate.getText().toString().equals("TO DATE"))
                        {
                            ald.alertDialog(getActivity(),"Please select to Date !");
                        }
                        else {
                            cleaner = (TextView) selectedView
                                    .findViewById(R.id.vehNumbersListViewTrackVehicle);
                            sCleanerName = cleaner.getText().toString();
                            DriverList.sEmployeeType = null;
                            sEmployeeType = "Cleaner";
                            mDriverName = sCleanerName;
                            db.open();
                            mEmployeeId = db.checkCleanerTableforDataExist(mDriverName);
                            db.close();
                            SendToWebService send = new SendToWebService(
                                    getActivity(), mDriverCleaner);
                            try {
                                send.execute("5", "GetCleanerPaymentDetails",
                                        mEmployeeId, mFromDate, mToDate);
                            } catch (SQLiteException e) {
                                ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString()
                                        + " "
                                        + "[MyDatePickerDialog]", e.toString());


                            } catch (Exception e) {
                                Toast.makeText(getActivity(),
                                        "Try after sometime...", Toast.LENGTH_SHORT)
                                        .show();
                                ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString()
                                        + " "
                                        + "[MyDatePickerDialog]", e.toString());
                            }

                        }}});

                db.close();
            } else {
                noDataLayout.removeAllViews();
                noDataLayout.setVisibility(View.VISIBLE);
                cleanerList.setVisibility(View.GONE);
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageResource(R.drawable.nodata);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.topMargin = 100;
                imageView.setLayoutParams(layoutParams);
                noDataLayout.addView(imageView);

                TextView textView=new TextView(getActivity());
                textView.setText("NO CLEANER LIST");
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
        }

    }


    private void bindData() {
        driverPayBtnFromDate = (TextView) view
                .findViewById(R.id.fragmentDriverPaymentBtnFromDate);
        driverPayBtnToDate = (TextView) view
                .findViewById(R.id.fragmentDriverPaymentBtnToDate);
        paidList = (Button) view.findViewById(R.id.fragmentDriverPaymentListBtn);
    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), PaymentCleaner.class);
        startActivity(intent);

    }

    @Override
    public void ongetCleanerPaymentDetails(String response)
    {
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
                cleanerParser(response);
                cleanerDisplayDialog();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[ongetCleanerPaymentDetails]",
                    e.toString());
        }

    }

    @Override
    public void ongetDriverPaymentDetails(String response) {

    }

    @Override
    public void onManagePaymentDetails(String response)
    {

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
                                    .toString() + " " + "[fromDateBtnclick]",
                            e.toString());
                }
                break;

            case R.id.fragmentDriverPaymentBtnToDate:
                try {
                    if (driverPayBtnFromDate.getText().toString().equals("FROM DATE")) {
                        ald.alertDialog(getActivity(), "Select the from date !");
                    } else {
                        tDate = mToDate;
                        mDateType = "to";
                        MyDatePickerDialog();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(),
                            "Please Select From Date!! ",
                            Toast.LENGTH_LONG).show();
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[toDateBtnClick]",
                            e.toString());
                }
                break;

            case R.id.fragmentDriverPaymentListBtn:
                Intent intent = new Intent(getActivity(), CleanerPaidList.class);
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

            }
        }
    };



	/*
	 * Purpose - Opens a alert dialog if there is no cleaner Method Name -
	 * alertInsert() Parameters - No parameter Return Type - No return type
	 */


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void cleanerParser(String response) {
        String jsonData = null;
        String statuschk = null;
        // double tripAmount = 0.0;
        double TripAmountDouble = 0.0;
        mTotalTripAmount = 0.0;
        double tripKMTotal = 0;
        double tripKMDouble = 0;
        mTotalAdvanceAmount = 0.0;
        // cleanerAdv=0.0;
        double totalAdvanceDouble = 0.0;

        sCleanerAdvance="0.0"; sCleanerCommissionString="0.0";
                sCleanerSalary="0.0"; sAmount="0.0";


        if (response != null)
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View dialogView = inflater.inflate(R.layout.date_custom_dialog, null);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog1 = builder.create();
                    message = (TextView) dialogView.findViewById(R.id.textmsg);
                    ok = (TextView) dialogView.findViewById(R.id.textBtn);
                    message.setText("The driver name has deleted by another manager !");
                    View v1= inflater.inflate(R.layout.title_dialog_layout, null);
                    alertDialog1.setCustomTitle(v1);

                    ok.setText("REFRESH");
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent ii = new Intent(getActivity(), Welcome.class);
                            ii.putExtra("comeback", "true");
                            ii.putExtra("class",PaymentDriver.class);
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
                }


                else if (statuschk.equals("OK")) {

                    String[] columnNames = {"_id", "date", "destination",
                            "vehicle", "kms", "driver"};
                    cursorCleanerTrip = new MatrixCursor(columnNames);
                    String driverId;
                    int key = 1;
                    JSONArray tripArray=null;
                    //------------------------------------------------------------------------------
                    //Trip array
                    // JSONObject tripDetail = paymentArray.getJSONObject(1);
                    try {
                        tripArray = paymentArray.getJSONArray(1);
                        if (tripArray.length() != 0) {

                            for (int i = 0; i < tripArray.length(); i++) {

                                JSONObject c = tripArray.getJSONObject(i);
                                String vehicle = c.getString("vehicleNumber");
                                String tripAmount = c.getString("tripAmount");
                                String tripKM = c.getString("totalKm");
                                String destinationName = c.getString("destinationName");

                                String tripStartDate = c.getString("tripStartDate");
                                String[] parts = tripStartDate.split("T");
                                tripStartDate = parts[0];

                                String driverName = c.getString("driver");

                                cursorCleanerTrip.addRow(new Object[]{key,
                                        tripStartDate, destinationName,
                                        vehicle, tripKM, driverName});

                                key++;
                                TripAmountDouble = Double.parseDouble(tripAmount);
                                mTotalTripAmount = mTotalTripAmount
                                        + TripAmountDouble;
                                tripKMDouble = Double.parseDouble(tripKM);
                                tripKMTotal = tripKMTotal + tripKMDouble;


                            }

                        }
                        cursorCleanerTrip.close();
                    }

                    catch (Exception e){

                            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                            .toString() + " " + "[jsonParser-tripArray]",
                                    e.toString());
                        e.printStackTrace();
                    }
                    //-------------------------------------------------------------------------------------

                    //  Cleaner Advance Array
                    //JSONObject cleanerAdvanceDetail = paymentArray.getJSONObject(2);
                    try {
                        JSONArray cleanerAdvanceArray = paymentArray.getJSONArray(2);
                        if (cleanerAdvanceArray.length() != 0) {

                            for (int k = 0; k < cleanerAdvanceArray.length(); k++) {

                                JSONObject ca = cleanerAdvanceArray.getJSONObject(k);

                                String advanceAmountString = ca.getString("advanceAmount");

                                if (!advanceAmountString.equals("0.0")) {
                                    totalAdvanceDouble = Double
                                            .parseDouble(advanceAmountString);
                                    mTotalAdvanceAmount = mTotalAdvanceAmount
                                            + totalAdvanceDouble;
                                }


                            }

                        }
                    }
                    catch (Exception e){

                            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                            .toString() + " " + "[jsonParser-cleanerAdvanceArray]",
                                    e.toString());
                        e.printStackTrace();
                    }
                    // --------------------------------------------------------------------------------------------
                    //Commision and salary

                    // JSONObject commSalaryDetail = paymentArray.getJSONObject(3);
                    try {
                        JSONArray commSalaryArray = paymentArray.getJSONArray(3);
                        if (commSalaryArray.length() != 0) {

                            JSONObject sal = commSalaryArray.getJSONObject(0);
                            salaryString = sal.getString("salary");

                            JSONObject com = commSalaryArray.getJSONObject(1);
                            mCommissionString = com.getString("commission");


                        }
                    }

                    catch (Exception e){
                        ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                        .toString() + " " + "[jsonParser-commSalaryArray]",
                                e.toString());
                        e.printStackTrace();
                    }

                    //---------------------------------------------------------------------------------------------

                    getCleanerData();
                }

                else {
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString(), statuschk);
                }

            } catch (SQLiteException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[cleanerParser]", e.toString());
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

                } else if (e.toString().contains(
                        "java.net.SocketTimeoutException")) {

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
                        .toString() + " " + "[cleanerParser]", e.toString());
            }

    }

    public void cleanerDisplayDialog() {

        try {
            final Dialog dialog = new Dialog(getActivity());

            dialog.setContentView(R.layout.dialog_cleaner_data_list);

            dialog.setTitle("Trip Details");

            ListView dialogCleanerDataList = (ListView) dialog
                    .findViewById(R.id.dialogCleanerDataList);
            Button dialogCleanerDataListBtnOk = (Button) dialog
                    .findViewById(R.id.dialogCleanerDataListBtnOk);

            String from[] = {"date", "kms", "vehicle", "driver", "destination"};
            int to[] = {R.id.txtDate, R.id.txtKMtravelled,
                    R.id.txtVehicleNo,
                    R.id.txtNameDriverCleaner, R.id.txtDestination};

            SimpleCursorAdapter caCleaner1 = new SimpleCursorAdapter(
                    getActivity(), R.layout.driverdatadetails,
                    cursorCleanerTrip, from, to, 0);
            dialogCleanerDataList.setAdapter(caCleaner1);

            dialogCleanerDataListBtnOk
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                            ((PaymentCleaner) getActivity()).setCurrentItem(1, true);

                        }
                    });

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[cleanerDisplayDialog]", e.toString());
        }

    }

    public void getCleanerData() {
        try {
            String cAdvance = String.valueOf(mTotalAdvanceAmount);
            sCleanerAdvance = cAdvance;
            sCleanerSalary = salaryString;
            System.out.println(sCleanerSalary);
            double CommissionA = Double.valueOf(mCommissionString);
            mCommission = (mTotalTripAmount * CommissionA) / 100;
            String commission=String.valueOf(mCommission);
            sCleanerCommissionString = commission;
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[getCleanerData]", e.toString());
        }

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            PaymentCleaner.pos=1;
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
            if(dateToday.after(nextday)|| dateToday.equals(nextday))
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


            if(dateToday.after(nextday)||dateToday.equals(nextday))
            {
                ald.alertDialog(getActivity(),"To Date cannot be greater than Today's Date !");
                driverPayBtnToDate.setText("TO DATE");
            }

            else if(dateToday.before(preFromDate)){
                ald.alertDialog(getActivity(),"To Date cannot be less than From Date !");
                driverPayBtnToDate.setText("TO DATE");
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[toDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
    }

}


