/**
 *
 */
package com.example.ManageResources;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

/**
 * @author MKSoft01
 */
public class DriverList extends Fragment {
    DBAdapter db;
    ListView driverList;
    static String DriverName;
    String srvrStatus;
    LinearLayout noDataLayout;
    TextView ok,message,cancel;
    TextView tDeletDriver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_entry_vehiclelist,
                container, false);

        driverList = (ListView) view.findViewById(R.id.LVNewEntryVehicleList);
        noDataLayout = (LinearLayout)view.findViewById(R.id.inboxLinearL);
        try {
            db = new DBAdapter(getActivity());
            db.open();

            Cursor driverCursor = db.getAllEmp(DBAdapter.getEmployeeDetails(),
                    "Driver");
            if(driverCursor.getCount()>0) {
                noDataLayout.setVisibility(View.GONE);
                driverList.setVisibility(View.VISIBLE);
                // LIST OF VEHICLES
                String from[] = {DBAdapter.getKeyName()};
                int to[] = {R.id.vehNumbersListViewTrackVehicle};

                SimpleCursorAdapter caDriver = new SimpleCursorAdapter(
                        getActivity(), R.layout.account3, driverCursor, from, to,
                        0);
                driverList.setAdapter(caDriver);
                driverList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View selectedView, int arg2, long arg3) {
                        TextView vehNumbers = (TextView) selectedView
                                .findViewById(R.id.vehNumbersListViewTrackVehicle);

                        DriverName = vehNumbers.getText().toString();
                        ((DriverEntryActivity) getActivity()).setCurrentItem(1, true);

                    }
                });

                driverList.setOnItemLongClickListener(new OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent,
                                                           View view, int position, long id) {
                                // VEHICLE NUMBER TO DLETE FROM SERVER DATABASE
                           tDeletDriver = (TextView) view.findViewById(R.id.vehNumbersListViewTrackVehicle);
                                db.open();
                                String deleteEmpId = db.checkDrvierTableforDataExist(tDeletDriver
                                        .getText().toString());

                                alertLongPressed(deleteEmpId);
                                return false;
                            }
                        });

                db.close();
            }
            else {
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
                    .toString() + " " + "[onCreateView]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onCreateView]", e.toString());
        }
        return view;

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {

            DriverName = null;

        }
    }

    public void alertLongPressed(final String EmpId) {
        db = new DBAdapter(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        String driver=tDeletDriver.getText().toString();
        message.setText("Delete "+driver+" ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendToWebService send = new SendToWebService(getActivity());
                try {
                    db.open();

                    String responseDelete = send.execute("3", "DeleteEmployee", EmpId).get();

                    if (responseDelete.contains("refused") || responseDelete.contains("timed out")) {
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
                    } else if (responseDelete
                            .contains("java.net.SocketTimeoutException")) {

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

                        String DeletejsonParsed = jsonParsing(responseDelete);

                        switch (DeletejsonParsed) {

                            case "deleted":
                                db.deleteEmployee(DBAdapter.getEmployeeDetails(),
                                        EmpId);
                                db.close();
                                refreshActivity();
                                break;

                            case "employee id does not exist":
                                ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString()
                                        + " "
                                        + "[alertLongPressed]", DeletejsonParsed);
                                refreshActivity();
                                break;

                            case "invalid authkey":
                                ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString()
                                        + " "
                                        + "[alertLongPressed]", DeletejsonParsed);
                                refreshActivity();
                                break;

                            case "unknown error":
                                ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString()
                                        + " "
                                        + "[alertLongPressed]", DeletejsonParsed);
                                refreshActivity();
                                break;

                            default:
                                ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString()
                                        + " "
                                        + "[alertLongPressed]", DeletejsonParsed);
                                break;

                        }

                    }

                } catch (SQLiteException e) {
                    ExceptionMessage.exceptionLog(getActivity(),
                            this.getClass().toString() + " "
                                    + "[alertLongPressed]", e.toString());
                } catch (InterruptedException e) {
                    ExceptionMessage.exceptionLog(getActivity(),
                            this.getClass().toString() + " "
                                    + "[alertLongPressed]", e.toString());
                } catch (ExecutionException e) {
                    ExceptionMessage.exceptionLog(getActivity(),
                            this.getClass().toString() + " "
                                    + "[alertLongPressed]", e.toString());
                }
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

    public void refreshActivity() {

        Intent intent = new Intent(getActivity(), DriverEntryActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public String jsonParsing(String response) {
        String jsonData = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);

                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                srvrStatus = d.getString("status").trim();
                return srvrStatus;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[alertLongPressed]", e.toString());
            }

        return srvrStatus;

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            DriverEntryActivity.pos=1;
        }
    }
}