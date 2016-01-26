/*
 * Purpose - AdvanceCleanerFragment displays list of cleaner advances.
 * @author - Pravitha 
 * Created on May 27, 2014
 * Modified on June 10, 2014
 */
package com.example.AdvanceModule;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SimpleCursorAdapter;

import com.example.Interface.IDeleteAdvance;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

/**
 * @author MKSoft01
 */
public class AdvanceDriverFragment extends Fragment implements
        IDeleteAdvance {
    View view;
    ListView listDriverAdvance;
    DBAdapter db;
    String Id;
    final IDeleteAdvance mDeleteAdvance = this;
    LinearLayout noDataLayout;
    TextView message,ok,cancel;
    String veh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_advance_driver, container,
                false);
        noDataLayout = (LinearLayout)view.findViewById(R.id.inboxLinearL);
        listDriverAdvance=(ListView)view.findViewById(R.id.fragmentAdvanceDriverLV);

        SharedPreferences UserType = getActivity().getSharedPreferences(
                "RegisterName", 0);
        String UserTyp = UserType.getString("Name", "");

        if (UserTyp.equals("DEMO")) {
            String[] columnNames = {"_id", "Date","Voucher","Driver", "Amount"};
            MatrixCursor tripCursor = new MatrixCursor(columnNames);
            tripCursor.addRow(new Object[]{1,"27-Aug-2015","10001","RAM","500"});
            tripCursor.addRow(new Object[]{2,"26-Aug-2015","10002","MADAN","200"});
            tripCursor.addRow(new Object[]{3,"27-Aug-2015","10003","MANISH","200"});

            noDataLayout.setVisibility(View.GONE);
            listDriverAdvance.setVisibility(View.VISIBLE);

            String from[] = {"Date","Voucher", "Driver", "Amount"};
            int to[] = {R.id.empDate, R.id.empVoucherNo,
                    R.id.EmpNameDriverCleaner, R.id.empAmountRs};

            SimpleCursorAdapter caDriver = new SimpleCursorAdapter(
                    getActivity(), R.layout.emp_advance_payment_layout, tripCursor, from, to, 0);

            listDriverAdvance.addHeaderView(new View(getActivity()));
            listDriverAdvance.addFooterView(new View(getActivity()));

            listDriverAdvance.setAdapter(caDriver);

            this.listDriverAdvance.setLongClickable(true);
            this.listDriverAdvance
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

            db = new DBAdapter(getActivity());
            try {
                db.open();
                Cursor Driveraccounts = db
                        .getAllContactsDriverAdvance("Driver Advance");
                int count = Driveraccounts.getCount();
                if (count > 0) {
                    noDataLayout.setVisibility(View.GONE);
                    listDriverAdvance.setVisibility(View.VISIBLE);
                    String from[] = {DBAdapter.getKeyDate(),
                            DBAdapter.getKeyVoucherNo(), DBAdapter.getKeyName(),
                            DBAdapter.getKeyAmount()};
                    int to[] = {R.id.empDate, R.id.empVoucherNo,
                            R.id.EmpNameDriverCleaner, R.id.empAmountRs};

                    // @SuppressWarnings("deprecation")
                    SimpleCursorAdapter caDriver = new SimpleCursorAdapter(
                            getActivity(), R.layout.emp_advance_payment_layout, Driveraccounts, from, to, 0);

                    listDriverAdvance.addHeaderView(new View(getActivity()));
                    listDriverAdvance.addFooterView(new View(getActivity()));

                    listDriverAdvance.setAdapter(caDriver);
                    db.close();
                    this.listDriverAdvance.setLongClickable(true);
                    this.listDriverAdvance
                            .setOnItemLongClickListener(new OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent,
                                                               View view, int position, long id) {
                                    alertLongPressed(id);
                                    return false;
                                }
                            });
                } else {
                    //if listview is empty at first time,instead of blank page image will be there.
                    noDataLayout.setVisibility(View.VISIBLE);
                    listDriverAdvance.setVisibility(View.GONE);
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setImageResource(R.drawable.nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    noDataLayout.addView(imageView);

                    TextView textView=new TextView(getActivity());
                    textView.setText("NO DRIVER ADVANCE LIST");
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

        return view;
    }

	/*
	 * Purpose - Deletes a row on long press of row. Method Name -
	 * alertLongPressed Parameters - Row id Return Type - No Return Type
	 */

    public void alertLongPressed(final long id) {

        DBAdapter db = new DBAdapter(getActivity());
        db.open();
        Cursor advanceDetails = db.getDriverAdvance(String.valueOf(id));
        if (advanceDetails.moveToFirst()) {
            veh = advanceDetails.getString(advanceDetails
                    .getColumnIndex(DBAdapter.getKeyName()));

        }db.close();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Delete advance of "+veh+" ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteAdvance(id);
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
        //change color of default blue divider into white in alert dialog
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(color);
    }

    public void alertLongPressedDemo(final long id)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Delete advance ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteAdvanceDemo(id);
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
        //change color of default blue divider into white in alert dialog
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(color);
    }

    public void deleteAdvanceDemo(final long id) {
        Toast.makeText(getActivity().getBaseContext(),
                "Advance Deleted Successfully...", Toast.LENGTH_SHORT).show();
        refreshActivity();
    }

    public void deleteAdvance(final long id) {
        String Voucher = null;
        SendToWebService send = new SendToWebService(getActivity(),
                mDeleteAdvance);
        Id = String.valueOf(id);
        DBAdapter db = new DBAdapter(getActivity());
        db.open();
        Cursor advanceDetails = db.getVoucherAdvance(Id);
        if (advanceDetails.moveToFirst()) {
            Voucher = advanceDetails.getString(advanceDetails
                    .getColumnIndex(DBAdapter.getKeyVoucherNo()));
        }
        if (Voucher != null) {

            try {
                send.execute("27", "DeleteAdvance", Voucher);

            } catch (Exception e) {
                Toast.makeText(getActivity().getBaseContext(),
                        "Try after sometime...", Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[deleteAdvance]", e.toString());
            }

        }

        db.close();

    }

    public void deleteAdvanceData(String result) {
        try {

            switch (result) {

                case "deleted":
                    db.open();
                    db.deleteLocaly(DBAdapter.getAdvanceDetails(), Id);
                    db.close();
                    Toast.makeText(getActivity().getBaseContext(),
                            "Advance Deleted Successfully...", Toast.LENGTH_SHORT).show();
                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteAdvanceData]", result);
                    break;

                case "voucher number does not exist":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteAdvanceData]", result);
                    break;

                 case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteAdvanceData]", result);
                    break;

                default:
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[deleteAdvanceData]", result);
                    break;

            }

        } catch (SQLiteException e) {

            Toast.makeText(getActivity().getBaseContext(),
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[deleteAdvanceData]", e.toString());
        } catch (Exception e) {

            Toast.makeText(getActivity().getBaseContext(),
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[deleteAdvanceData]", e.toString());
        }
        refreshActivity();

    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), AdvanceMain.class);
        startActivity(intent);

    }

    @Override
    public void onDeleteAdvance(String response) {
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
                deleteAdvanceData(result);
            }
        } catch (Exception e) {

            Toast.makeText(getActivity().getBaseContext(),
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onDeleteAdvance]", e.toString());
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
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing1]", e.toString());
            }
        return status;
    }

	/*
	 * Purpose - Loads the data from local database to listview Method Name -
	 * SetListViewCleanerAdv Parameters - Row id Return Type - No Return Type
	 */

    public void SetListViewDriverAdv(String tableName, String columnName,
                                     String advanceType) {
        db.open();
        try {
            Cursor Driveraccounts = db.getAllDataInOrder(tableName, columnName,
                    advanceType);
            String from[] = {DBAdapter.getKeyVoucherNo(),
                    DBAdapter.getKeyDate(), DBAdapter.getKeyName(),
                    DBAdapter.getKeyAmount()};
            int to[] = {R.id.textViewaccount2, R.id.textView2account2,
                    R.id.textView3account2, R.id.textView4account2};

            SimpleCursorAdapter caDriver = new SimpleCursorAdapter(
                    getActivity(), R.layout.account1, Driveraccounts, from, to,
                    0);
            listDriverAdvance.setAdapter(caDriver);
            Driveraccounts.close();
            db.close();
            this.listDriverAdvance.setLongClickable(true);
            this.listDriverAdvance
                    .setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            alertLongPressed(id);
                            return false;
                        }
                    });
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[setListViewDriverAdv]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[setListViewDriverAdv]", e.toString());

        }

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            AdvanceMain.pos=3;
        }

    }

}
