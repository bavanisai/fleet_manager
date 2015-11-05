package com.example.Expense;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AbsenceReport.DatePickerFragment;
import com.example.AdvanceModule.AdvanceMain;
import com.example.Interface.IAddExpense;
import com.example.Interface.IDeleteExpense;
import com.example.Interface.IExpenseList;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ExpenseList extends Fragment implements View.OnClickListener,IDeleteExpense,IExpenseList {

    View view;
    DBAdapter db;
    Bitmap bitmap;
    int year, month, day;
    SendToWebService send;
    List<String> label1;
    String mSpinnerVehicleNo, mDateType,Id, fromDate,toDate;
    TextView fragmentExpenseListTvFromDate, fragmentExpenseListTvToDate;
    Spinner fragmentExpenseListSpinnerVehicle;
    ListView fragmentExpenseListLV;
    LinearLayout fragmentExpenseLayout;
    final IDeleteExpense mDeleteExpense=this;
    public static String expenseListVoucherNum=null;
    final IExpenseList mExpenseList = this;
    public static MatrixCursor expenseCursor;
    public MatrixCursor cur;
    private int sKey = 0;
    public static int rowid;
    public static String veh;
	byte[] byteArray,receipt;
    CustomAlertDialog ald;
    SimpleCursorAdapter expenseAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_expense_list, container, false);
        db = new DBAdapter(getActivity());
        bindData();
        ald=new CustomAlertDialog();
        loadVehicleSpinnerData();
        fragmentExpenseListTvFromDate.setOnClickListener(this);
        fragmentExpenseListTvToDate.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragmentExpenseListTvFromDate:
                mDateType = "from";
                fragmentExpenseListTvToDate.setText("TO DATE");
                MyDatePickerDialog();
                break;
            case R.id.fragmentExpenseListTvToDate:
                if (fragmentExpenseListTvFromDate.getText().toString() != ""
                        && fragmentExpenseListTvFromDate.getText().toString() != null
                        && fragmentExpenseListTvFromDate.toString() != "CLICK HERE") {
                    mDateType = "to";
                    MyDatePickerDialog();
                } else {
                    ImageView image = new ImageView(getActivity());
                    image.setImageResource(R.drawable.selectdate);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity())
                            .setMessage("Select the From date")
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
                }
                break;
        }
    }

    private void bindData() {
        fragmentExpenseListTvFromDate = (TextView) view.findViewById(R.id.fragmentExpenseListTvFromDate);
        fragmentExpenseListTvToDate = (TextView) view.findViewById(R.id.fragmentExpenseListTvToDate);
        fragmentExpenseListSpinnerVehicle = (Spinner) view.findViewById(R.id.fragmentExpenseListSpinnerVehicle);
        fragmentExpenseListLV = (ListView) view.findViewById(R.id.fragmentExpenseListLV);
        fragmentExpenseLayout=(LinearLayout)view.findViewById(R.id.fragmentExpenseLayout);

    }

    private void loadVehicleSpinnerData() {
        try {
            db.open();
            label1 = db.getAllLabels(DBAdapter.getVehicleDetails());
            label1.add(0, "SELECT THE VEHICLE");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item,
                    label1);
          //  dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fragmentExpenseListSpinnerVehicle.setAdapter(dataAdapter);
            fragmentExpenseListSpinnerVehicle
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> adapterView,
                                                   View view, int i, long l) {
                            if (fragmentExpenseListTvFromDate.getText().toString() != "CLICK HERE" && fragmentExpenseListTvToDate.getText().toString() != "CLICK HERE") {
                                mSpinnerVehicleNo = label1.get(i);
                                if(!mSpinnerVehicleNo.equals("SELECT THE VEHICLE")) {
                                    cur=null;
                                    expenseCursor=null;
                                    displayExpenseData();
                                }

                            } else if (fragmentExpenseListTvFromDate.getText().toString() == "CLICK HERE" && fragmentExpenseListTvToDate.getText().toString() != "CLICK HERE") {
                                ImageView image = new ImageView(getActivity());
                                image.setImageResource(R.drawable.selectdate);

                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        getActivity())
                                        .setMessage("Select the From date")
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
                            } else if (fragmentExpenseListTvFromDate.getText().toString() != "CLICK HERE" && fragmentExpenseListTvToDate.getText().toString() == "CLICK HERE") {
                                ImageView image = new ImageView(getActivity());
                                image.setImageResource(R.drawable.selectdate);

                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        getActivity())
                                        .setMessage("Select the To date")
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
                            }
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

            if (mDateType.equals("from")) {
                fromDate=String.valueOf(year) + "-"
                        + String.valueOf(monthOfYear + 1) + "-"
                        + String.valueOf(dayOfMonth);
                fromDateValidation();



            } else if (mDateType.equals("to")) {
                toDate=String.valueOf(year) + "-"
                        + String.valueOf(monthOfYear + 1) + "-"
                        + String.valueOf(dayOfMonth);
                toDateValidation();

            }


        }
    };

    public  void displayExpenseData()
    {

//       Cursor expenseCursor= db.getParticularExpenseData(fragmentExpenseListTvFromDate.toString(), fragmentExpenseListTvToDate.toString());
//        String from[] = new String[]{DBAdapter.getKeyDate(),
//                DBAdapter.getKeyVoucherNo(), DBAdapter.getKeyPhoto(),DBAdapter.getKeyVehicleNo(),DBAdapter.getKeyParticular(),
//                DBAdapter.getKeyName(), DBAdapter.getKeyAmount()};
//        int to[] = new int[]{R.id.accountExpenseListDate, R.id.accountExpenseListvoucher,
//                R.id.accountExpenseListImgVExpenseReceipt, R.id.accountExpenseListTvVehicleNo,R.id.accountExpenseListParticular,
//                R.id.accountExpenseListTvDriver,R.id.accountExpenseListTvAmount};
//
//        SimpleCursorAdapter caPersonal = new SimpleCursorAdapter(
//                getActivity(), R.layout.accountexpenselist,expenseCursor , from,
//                to, 0);
//
//        fragmentExpenseListLV.setAdapter(caPersonal);
//        fragmentExpenseListLV.setLongClickable(true);
//        fragmentExpenseListLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                alertLongPressed(id);
//                return false;
//            }
//        });
//
//        fragmentExpenseListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                alertClickPressed(id);
//
//            }
//        });
        expenseCursor = null;
        SendToWebService send=new SendToWebService(getActivity(),mExpenseList);
        try{

            send.execute("39", "GetExpenseList",
                    fragmentExpenseListTvFromDate.getText().toString(), fragmentExpenseListTvToDate.getText().toString(), mSpinnerVehicleNo);
        }
        catch (Exception e) {
            Toast.makeText(getActivity().getBaseContext(),
                    "Try after sometime...", Toast.LENGTH_SHORT)
                    .show();
            ExceptionMessage.exceptionLog(getActivity(), this
                    .getClass().toString()
                    + " "
                    + "[onSaveAdvance]", e.toString());

        }

    }



    public void alertLongPressed(final int id){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Do u want to Cancel the Expense?");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteExpense(id);
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        alert.show();
    }

    public void alertClickPressed(final int id) {
try {
    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    alert.setTitle("Do you want to update expense ?");
    // alert.setMessage("Message");

    alert.setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String Voucher = null;
                    Id = String.valueOf(id);
                    rowid = id;
                    veh = mSpinnerVehicleNo;
//                        DBAdapter db = new DBAdapter(getActivity());
//                        db.open();
//                        Cursor expenseDetails = db.getVoucherExpense(DBAdapter.getExpensedetails(), Id);
//                        if (expenseDetails.moveToFirst()) {
//                            Voucher = expenseDetails.getString(expenseDetails
//                                    .getColumnIndex(DBAdapter.getKeyVoucherNo()));
//                        }

                    if (expenseCursor.moveToFirst()) {
                        for (int i = 0; i < expenseCursor.getCount(); i++) {

                            if (i == rowid) {
                                Voucher = expenseCursor.getString(5);
                            }

                            expenseCursor.moveToNext();
                        }
                    }
                    expenseCursor.close();
                    expenseListVoucherNum = Voucher;


                    //  dialog.dismiss();
                    ((Expense) getActivity()).setCurrentItem(1, true);

                }
            });


    alert.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });

    alert.show();
}
catch (Exception e){
    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
            .toString() + " " + "[alertClickPressed]", e.toString());
}

    }

    public void deleteExpense(final int id) {
        String Voucher = null;
        SendToWebService send = new SendToWebService(getActivity(),
                mDeleteExpense);
        Id = String.valueOf(id);
        if (expenseCursor.moveToFirst()) {
            for (int i = 0; i < expenseCursor.getCount(); i++) {

                if(i==id) {
                    Voucher = expenseCursor.getString(5);
                }

                expenseCursor.moveToNext();
            }
        }
        expenseCursor.close();
        if (Voucher != null) {

            try {
                send.execute("37", "DeleteExpense", Voucher);

            } catch (Exception e) {
                Toast.makeText(getActivity().getBaseContext(),
                        "Try after sometime...", Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[deleteAdvance]", e.toString());
            }
        }
        db.close();
    }

    @Override
    public void onDeleteExpense(String response) {
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
                // OneJSONValue one = new OneJSONValue();
                String result = jsonParsing1(response);
                deleteExpenseData(result);
            }
        } catch (Exception e) {

            Toast.makeText(getActivity().getBaseContext(),
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onDeleteExpense]", e.toString());
        }

    }

    public void deleteExpenseData(String result) {
        try {

            switch (result) {

                case "deleted":
                    Toast.makeText(getActivity().getBaseContext(),
                            "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteExpenseData]", result);
                    break;

                case "voucher number does not exist":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteExpenseData]", result);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteExpenseData]", result);
                    break;

                default:
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteExpenseData]", result);
                    break;

            }

        } catch (SQLiteException e) {
            Toast.makeText(getActivity().getBaseContext(),
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[deleteExpenseData]", e.toString());
        } catch (Exception e) {

            Toast.makeText(getActivity().getBaseContext(),
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[deleteExpenseData]", e.toString());
        }
        refreshActivity();

    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), Expense.class);
        startActivity(intent);

    }

    public String jsonParsing1(String response) {
        String jsonData = null;
        String status = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                status = d.getString("status").trim();
                return status;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing1]", e.toString());
            }
        return status;
    }

    @Override
    public void onGetExpenseList(String response) {

        try{
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

              String status= getExpensejsonParsing(response);
              displayExpenseList(status);


            }
        }

        catch(Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[onGetExpenseList]",
                    e.toString());
        }
    }


    public String getExpensejsonParsing(String response) {
        String jsonData = null;
        String statuschk = null;
        if (response != null)
            try {

                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
                JSONArray expense = new JSONArray(jsonData);
                JSONObject status1 = expense.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString() + " " + "[getExpensejsonParsing]",
                            statuschk);

                } else if (statuschk.equals("data does not exist")) {
				
//					Toast.makeText(getActivity().getBaseContext(),
//                    "DATA IS NOT AVAILABLE", Toast.LENGTH_SHORT).show();
//                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
//                                    .toString() + " " + "[getExpensejsonParsing]",
//                            statuschk);
                }

                else if (statuschk.equals("OK")){
                    String[] columnNames = {"_id","Expense_Id", "Name", "Date","Particular","Voucher","Amount","Receipt","vehNo"};
                    expenseCursor = new MatrixCursor(columnNames);
                    cur=new MatrixCursor(columnNames);
                    for (int i = 1; i < expense.length(); i++) {

                        JSONObject expenseJSONObject = expense.getJSONObject(i);
                        String ExpenseId=expenseJSONObject.getString("expenseId");
                        String Name = expenseJSONObject.getString("employeeName");
                        String Date1 = expenseJSONObject.getString("expenseDate");
                         String Date = Date1.substring(0, 10);
                        String Particular = expenseJSONObject.getString("particular");
                        String Voucher = expenseJSONObject.getString("billNumber");
                        String Amount = expenseJSONObject.getString("amount");
                        String Receipt = expenseJSONObject.getString("receipt");
						if(!Receipt.equals("") || Receipt==null)
							{
								receipt=StringToBitMap(Receipt);
							}
							else
							{
								Resources res = getResources();
								Drawable drawable = res.getDrawable(R.drawable.no_image);
								 bitmap = ((BitmapDrawable)drawable).getBitmap();
								ByteArrayOutputStream stream = new ByteArrayOutputStream();
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
								receipt = stream.toByteArray();
							}
                        //Bitmap receipt= StringToBitMap(receipt);
                        expenseCursor.addRow(new Object[]{sKey, ExpenseId,Name,Date,Particular,Voucher,Amount,receipt,mSpinnerVehicleNo});
                        cur.addRow(new Object[]{sKey, ExpenseId,Name,Date,Particular,"# "+Voucher,"Rs "+Amount,receipt,mSpinnerVehicleNo});

                    }


                }

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing]", e.toString());
            }

        return statuschk;

    }


    public void displayExpenseList(String status){

        if (status.equals("data does not exist"))
        {
            fragmentExpenseListLV.setVisibility(View.GONE);
            fragmentExpenseLayout.setVisibility(View.VISIBLE);
            fragmentExpenseLayout.removeAllViews();
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.nodata1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.topMargin = 25;
            imageView.setLayoutParams(layoutParams);
            fragmentExpenseLayout.addView(imageView);

            TextView textView=new TextView(getActivity());
            textView.setText("NO EXPENSE LIST");
            textView.setTextSize(14);
            textView.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER;
            layoutParams1.topMargin = 20;
            textView.setLayoutParams(layoutParams1);
            fragmentExpenseLayout.addView(textView);


        }

        else{

            fragmentExpenseListLV.setVisibility(View.VISIBLE);
            fragmentExpenseLayout.setVisibility(View.GONE);

//                fragmentExpenseLayout.removeAllViews();
//                fragmentExpenseLayout.setVisibility(View.INVISIBLE);
                String from[] = new String[]{"_id","Date","Voucher","Particular","vehNo","Name","Amount"};

                int to[] = new int[]{R.id.accountExpenseListId,R.id.accountExpenseListDate, R.id.accountExpenseListvoucher,
                                R.id.accountExpenseListParticular,R.id.accountExpenseListTvVehicleNo,
                                 R.id.accountExpenseListTvDriver, R.id.accountExpenseListTvAmount};
                 expenseAdapter = new SimpleCursorAdapter(
                        getActivity(), R.layout.accountexpenselist, cur , from,
                        to, 0);

                fragmentExpenseListLV.setAdapter(expenseAdapter);
                expenseAdapter.notifyDataSetChanged();

                fragmentExpenseListLV.setLongClickable(true);
                fragmentExpenseListLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                   int position, long id) {

                        alertLongPressed(position);
                        return false;
                    }
                });
                fragmentExpenseListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        alertClickPressed(position);

                    }
                });
        }

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
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        try {
            if (visible) {
                rowid = 0;
                veh = null;
               // expenseCursor = null;
                cur = null;
            }
        }
        catch(Exception e){

        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            Expense.pos=1;
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
                fragmentExpenseListTvFromDate.setText("FROM DATE");

            }
            else{
                fragmentExpenseListTvFromDate.setText(fromDate);
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString() + " " + "[fromDateValidation()]",
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

            if(dateToday.after(nextday)|| dateToday.equals(nextday))
            {
                ald.alertDialog(getActivity(),"To Date cannot be greater than Today's Date !");
                fragmentExpenseListTvToDate.setText("TO DATE");
            }

            else if(dateToday.before(preFromDate)){
                ald.alertDialog(getActivity(),"To Date cannot be less than From Date !");
                fragmentExpenseListTvToDate.setText("TO DATE");
            }
            else{
                fragmentExpenseListTvToDate.setText(toDate);
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString() + " " + "[toDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
    }

}
