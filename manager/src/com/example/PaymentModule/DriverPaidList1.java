package com.example.PaymentModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IDeletePayment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverPaidList1 extends ActionBarActivity implements IDeletePayment {

    DBAdapter db;
    ListView driverPaidList;
    String Id;
    final IDeletePayment mDeletePayment = this;
    SimpleCursorAdapter caDriver;
    Toolbar toolbar;
    LinearLayout  noDataLayout;
    TextView ok,cancel,message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_driver_paid_list);


        toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("Driver Paid Payment List");
        setSupportActionBar(toolbar);
        db = new DBAdapter(getApplicationContext());
        driverPaidList = (ListView) findViewById(R.id.fragmentDriverPaidListLV);
        noDataLayout = (LinearLayout)findViewById(R.id.inboxLinearL);
    }


    /*
     * Purpose - Loads data from local database to Listview Event Name -
	 * onStart() Parameters - No parameter Return Type - No Return Type
	 */

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences UserType = getSharedPreferences(
                "RegisterName", 0);
        String UserTyp = UserType.getString("Name", "");
        if (UserTyp.equals("DEMO")) {
            String[] columnNames = {"_id", "Date","Voucher", "Driver", "Amount"};
           MatrixCursor tripCursor = new MatrixCursor(columnNames);
            tripCursor.addRow(new Object[]{1,"27-Aug-2015","1001","MADAN","5500"});
            tripCursor.addRow(new Object[]{2,"26-Aug-2015","1002","RAM","5900"});
            tripCursor.addRow(new Object[]{3,"27-Aug-2015","1003","MADAN","6500"});
            noDataLayout.setVisibility(View.GONE);
            driverPaidList.setVisibility(View.VISIBLE);

            String from[] = {"Date","Voucher", "Driver", "Amount"};
            int to[] = {R.id.empDate, R.id.empVoucherNo,
                    R.id.EmpNameDriverCleaner, R.id.empAmountRs};

            caDriver = new SimpleCursorAdapter(this, R.layout.emp_advance_payment_layout,
                    tripCursor, from, to,
                    0);

            driverPaidList.setAdapter(caDriver);

            this.driverPaidList.setLongClickable(true);
            this.driverPaidList
                    .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            alertLongPressedDemo(id);
                            return false;
                        }
                    });


        }

        else {

            db = new DBAdapter(getApplicationContext());
            try {
                db.open();
                Cursor Driveraccounts = db.getAllDriverPayments();
                int count = Driveraccounts.getCount();
                if (count > 0) {
                    noDataLayout.setVisibility(View.GONE);
                    driverPaidList.setVisibility(View.VISIBLE);
                    String from[] = {DBAdapter.getKeyDate(),
                            DBAdapter.getKeyVoucherNo(), DBAdapter.getKeyName(),
                            DBAdapter.getKeyAmount()};

                    int to[] = {R.id.empDate, R.id.empVoucherNo,
                            R.id.EmpNameDriverCleaner, R.id.empAmountRs};

                    // @SuppressWarnings("deprecation")
                    caDriver = new SimpleCursorAdapter(this, R.layout.emp_advance_payment_layout,
                            Driveraccounts, from, to,
                            0);
                    // DriverCleanerPay.sAmount = "0";
                    driverPaidList.setAdapter(caDriver);
                    db.close();

                    this.driverPaidList.setLongClickable(true);
                    this.driverPaidList
                            .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent,
                                                               View view, int position, long id) {
                                    alertLongPressed(id);
                                    return false;
                                }
                            });
                } else {

                    noDataLayout.setVisibility(View.VISIBLE);
                    driverPaidList.setVisibility(View.GONE);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(R.drawable.nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    noDataLayout.addView(imageView);

                    TextView textView=new TextView(this);
                    textView.setText("NO DRIVER PAID LIST");
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
                ExceptionMessage.exceptionLog(this, this.getClass()
                        .toString() + " " + "[onStart]", e.toString());
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(this, this.getClass()
                        .toString() + " " + "[onStart]", e.toString());
            }

        }

    }

    public void alertLongPressed(final long id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DriverPaidList1.this);
        LayoutInflater inflater = LayoutInflater.from(DriverPaidList1.this);
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Delete payment ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deletePayment(id);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


    public void alertLongPressedDemo(final long id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DriverPaidList1.this);
        LayoutInflater inflater = LayoutInflater.from(DriverPaidList1.this);
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Delete payment ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deletePaymentDemo(id);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    public void deletePaymentDemo(final long id) {

        Toast.makeText(DriverPaidList1.this, "Payment Deleted Successfully...",
                Toast.LENGTH_SHORT).show();
        refreshActivity();
    }

    public void deletePayment(final long id) {
        try {
            String Voucher = null;
            SendToWebService send = new SendToWebService(DriverPaidList1.this,
                    mDeletePayment);
            Id = String.valueOf(id);
            DBAdapter db = new DBAdapter(getApplicationContext());
            db.open();
            Cursor paymentDetails = db.getVoucherPayment(Id);
            if (paymentDetails.moveToFirst()) {
                Voucher = paymentDetails.getString(paymentDetails.getColumnIndex(DBAdapter.getKeyVoucherNo()));
            }
            if (Voucher != null) {

                try {
                    send.execute("28", "DeleteEmployeePayment", Voucher);

                } catch (Exception e) {
                    Toast.makeText(DriverPaidList1.this, "Try after sometime...",
                            Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(DriverPaidList1.this, this
                                    .getClass().toString() + " " + "[deletePayment]",
                            e.toString());
                }

            }

            db.close();
        } catch (Exception e) {
            Toast.makeText(DriverPaidList1.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                    .toString() + " " + "[deletePayment]", e.toString());
        }

    }

    public void deletePaymentData(String result) {
        try
        {

            switch (result) {

                case "deleted":
                    db.open();
                    db.deleteLocaly(DBAdapter.getPaymentDetails(), Id);
                    db.close();
                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                            .toString() + " " + "[deletePaymentData]", result);
                    break;

                case "voucher number does not exist":
                    ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                            .toString() + " " + "[deletePaymentData]", result);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                            .toString() + " " + "[deletePaymentData]", result);
                    break;

                default:
                    ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                            .toString() + " " + "[deletePaymentData]", result);
                    break;

            }

        } catch (SQLiteException e) {

            Toast.makeText(DriverPaidList1.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                    .toString() + " " + "[deletePaymentData]", e.toString());
        } catch (Exception e) {

            Toast.makeText(DriverPaidList1.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                    .toString() + " " + "[deletePaymentData]", e.toString());
        }

       // caDriver.notifyDataSetChanged();
        refreshActivity();

    }
    public void refreshActivity() {
        finish();
        Intent intent = new Intent(DriverPaidList1.this, DriverPaidList1.class);
        startActivity(intent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeletePayment(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(DriverPaidList1.this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused") || response.contains("timed out")) {
                ImageView image = new ImageView(DriverPaidList1.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DriverPaidList1.this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else if (response.contains("java.net.SocketTimeoutException")) {

                ImageView image = new ImageView(DriverPaidList1.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DriverPaidList1.this).setPositiveButton("OK",
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
                deletePaymentData(result);
            }
        } catch (Exception e) {
            Toast.makeText(DriverPaidList1.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(DriverPaidList1.this, this.getClass()
                    .toString() + " " + "[onDeletePayment]", e.toString());
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
                status = d.getString("status").trim();
                return status;

            } catch (JSONException e) {
                if (e.toString().contains("refused") || e.toString().contains("timed out")) {
                    ImageView image = new ImageView(DriverPaidList1.this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            DriverPaidList1.this).setPositiveButton("OK",
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

                    ImageView image = new ImageView(DriverPaidList1.this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            DriverPaidList1.this).setPositiveButton("OK",
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

}
