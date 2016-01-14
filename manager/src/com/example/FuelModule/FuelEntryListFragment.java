package com.example.FuelModule;

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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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

import com.example.Interface.IFuelEntryFragment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

public class FuelEntryListFragment extends Fragment implements
        IFuelEntryFragment {
    String id;
    LinearLayout noDataLayout;
    final IFuelEntryFragment mFuelEntryFragment = this;
    ListView listPersonalAdvance;
    TextView ok, cancel, message;
    static String veh,date,driver,speedoVal,fuelVolume;
    static long edit_id;
    long idMax;
    Cursor csr1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fuel_entry_list,
                container, false);
        listPersonalAdvance = (ListView) view.findViewById(R.id.fragmentFuelEntryListLV);

        noDataLayout = (LinearLayout) view.findViewById(R.id.inboxLinearL);

        SharedPreferences UserType = getActivity().getSharedPreferences(
                "RegisterName", 0);
        String UserTyp = UserType.getString("Name", "");

        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.open();
            csr1 = db.getMaxId();
            int s = csr1.getCount();
            if (csr1 != null)
                idMax = csr1.getLong(0);
            edit_id=idMax;
            csr1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (UserTyp.equals("DEMO")) {
            try {
                String[] columnNames = {"_id", "Date", "Vehicle No", "Driver", "Fuel", "Speedometer"};
                MatrixCursor tripCursor = new MatrixCursor(columnNames);
                tripCursor.addRow(new Object[]{1, "27-Aug-2015", "TN 63 AC 1643", "RAM", "500", "10100"});
                tripCursor.addRow(new Object[]{2, "26-Aug-2015", "TN 63 AC 1644", "MADAN", "20", "10110"});
                tripCursor.addRow(new Object[]{3, "27-Aug-2015", "TN 63 AC 1645", "MANISH", "200", "10100"});
                noDataLayout.setVisibility(View.GONE);
                listPersonalAdvance.setVisibility(View.VISIBLE);

                String from[] = {"Date", "Vehicle No", "Driver", "Fuel", "Speedometer"};
                int to[] = {R.id.fuelEntryDate, R.id.fuelEntryVehNum,
                        R.id.fuelEntryDriverName, R.id.fuelLevel, R.id.fuelSpeedometerVal};

                SimpleCursorAdapter caPersonal = new SimpleCursorAdapter(
                        getActivity(), R.layout.fuelentrylistview, tripCursor, from,
                        to, 0);

                listPersonalAdvance.addHeaderView(new View(getActivity()));
                listPersonalAdvance.addFooterView(new View(getActivity()));

                listPersonalAdvance.setAdapter(caPersonal);

                this.listPersonalAdvance.setLongClickable(true);
                this.listPersonalAdvance
                        .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent,
                                                           View view, int position, long id) {
                                alertLongPressedDemo(id);
                                return false;
                            }
                        });
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onCreateView]", e.toString());
            }
        } else {
            DBAdapter db = new DBAdapter(getActivity());
            try {
                db.open();
                Cursor Personalaccounts = db.getAllFuelDetails();
                int count = Personalaccounts.getCount();
                if (count > 0) {
                    noDataLayout.setVisibility(View.GONE);
                    listPersonalAdvance.setVisibility(View.VISIBLE);
                    String from[] = {DBAdapter.getKeyMdate(),
                            DBAdapter.getKeyMvehicle(), DBAdapter.getKeyMdriver(),
                            DBAdapter.getKeyFuel(), DBAdapter.getKeySpeedometer()};
                    int to[] = {R.id.fuelEntryDate, R.id.fuelEntryVehNum,
                            R.id.fuelEntryDriverName, R.id.fuelLevel, R.id.fuelSpeedometerVal};

                    SimpleCursorAdapter caPersonal = new SimpleCursorAdapter(
                            getActivity(), R.layout.fuelentrylistview, Personalaccounts, from,
                            to, 0);

                    listPersonalAdvance.setAdapter(caPersonal);
                    db.close();

                    listPersonalAdvance.setLongClickable(true);
                    listPersonalAdvance.setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            if (id == idMax)
                            {
                               //alertLongPressed(id);
                            }
                            return false;
                        }
                    });
                } else {
                    //if listview is empty at 1st time
                    noDataLayout.setVisibility(View.VISIBLE);
                    listPersonalAdvance.setVisibility(View.GONE);
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setImageResource(R.drawable.nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    noDataLayout.addView(imageView);

                    TextView textView = new TextView(getActivity());
                    textView.setText("NO FUEL DATA");
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
        }
        return view;
    }

    public void alertLongPressed(final long id1) {
        DBAdapter db = new DBAdapter(getActivity());
        db.open();
        Cursor FuelDetails = db.getOneVehicleFuelDetails(String.valueOf(id1));
        int cnt = FuelDetails.getCount();
        if (cnt > 0) {
            FuelDetails.moveToFirst();
            veh = FuelDetails.getString(FuelDetails.getColumnIndex(DBAdapter.getKeyMvehicle()));
            date= FuelDetails.getString(FuelDetails.getColumnIndex(DBAdapter.getKeyMdate()));
            driver=FuelDetails.getString(FuelDetails.getColumnIndex(DBAdapter.getKeyMdriver()));
            speedoVal=FuelDetails.getString(FuelDetails.getColumnIndex(DBAdapter.getKeySpeedometer()));
            fuelVolume=FuelDetails.getString(FuelDetails.getColumnIndex(DBAdapter.getKeyFuel()));

        }
        db.close();

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel = (TextView) dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Edit fuel entry details of " + veh + " ?");
        //for hiding title layout
        View v1 = inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("EDIT");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // DeleteFuelDetails(id1);
                ((FuelActivity) getActivity()).setCurrentItem(0, true);
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

    public void alertLongPressedDemo(final long id1) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel = (TextView) dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Delete fuel entry details ?");
        View v1 = inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteFuelDetailsDemo(id1);
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


    protected void DeleteFuelDetails(long id1) {
        try {

            DBAdapter db = new DBAdapter(getActivity());
            db.open();
            Cursor FuelDetails = db.getOneFuelDetails(String.valueOf(id1));
            if (FuelDetails.moveToFirst()) {
                String Id = FuelDetails.getString(FuelDetails
                        .getColumnIndex(DBAdapter.getKeyFuelRowId()));
                //  veh=FuelDetails.getString(FuelDetails.getColumnIndex(DBAdapter.getKeyMvehicle()));
                id = String.valueOf(id1);
                SendToWebService send = new SendToWebService(getActivity(),
                        mFuelEntryFragment);
                // if (send.isConnectingToInternet()) {
                try {
                    send.execute("29", "DeleteFuel", Id);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Try after sometime...",
                            Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString(), e.toString());
                }

            }
            db.close();
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[DeleteFuelDetails]", e.toString());

        }

    }

    protected void DeleteFuelDetailsDemo(long id1) {
        try {
            Toast.makeText(getActivity(), "Deleted Successfuly",
                    Toast.LENGTH_SHORT).show();
            refreshActivity();
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[DeleteFuelDetails]", e.toString());

        }

    }

    public void refreshActivity() {
        // vg.invalidate();
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
                JSONObject jsonResponse;

                try {
                    jsonResponse = new JSONObject(response);
                    String jsonData = jsonResponse.getString("d");
                    JSONObject d = new JSONObject(jsonData);
                    String status = d.getString("status").trim();
                    switch (status) {

                        case "deleted":
                            DBAdapter db = new DBAdapter(getActivity());
                            db.open();
                            db.deleteFuel(String.valueOf(id));
                            db.close();
                            Toast.makeText(getActivity(), "Deleted Successfuly",
                                    Toast.LENGTH_SHORT).show();
                            refreshActivity();
                            break;

                        case "invalid authkey":
                            ExceptionMessage.exceptionLog(getActivity(), this
                                    .getClass().toString()
                                    + " "
                                    + "[onInsertToVehicleFuel]", jsonData);
                            break;

                        case "invalid rowid":
                            ExceptionMessage.exceptionLog(getActivity(), this
                                    .getClass().toString()
                                    + " "
                                    + "[onInsertToVehicleFuel]", jsonData);
                            break;

                        case "unknown error":
                            ExceptionMessage.exceptionLog(getActivity(), this
                                    .getClass().toString()
                                    + " "
                                    + "[onInsertToVehicleFuel]", jsonData);
                            break;

                        default:
                            ExceptionMessage.exceptionLog(getActivity(), this
                                    .getClass().toString()
                                    + " "
                                    + "[onInsertToVehicleFuel]", jsonData);
                            break;

                    }

                } catch (Exception e) {

                    Toast.makeText(getActivity(), "Try after sometime...",
                            Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString()
                            + " "
                            + "[onInsertToVehicleFuel]", e.toString());
                }
            }
        } catch (Exception e) {

            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage
                    .exceptionLog(getActivity(), this.getClass().toString()
                            + " " + "[onInsertToVehicleFuel]", e.toString());
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            FuelActivity.pos = 2;
        }

    }
}
