package com.example.tripmodule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.MatrixCursor;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.example.Interface.IGetTripList;
import com.example.Interface.ITripListFragment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

public class TripListFragment extends Fragment implements ITripListFragment, IGetTripList {
    DBAdapter db;
    View view;
    // public static long tripId=0;
    public static boolean isRunningActivity1;
    final ITripListFragment mTripListFragment = this;
    final IGetTripList mGetTripList = this;
    String adress = new IpAddress().getIpAddress();
    TextView tripListfragmentTvGetTripList;
    MatrixCursor tripCursor;
    private int sKey = 0;
    LinearLayout fragmentTripLayout;
    ListView trip;
    LinearLayout noDataLayout;
    public int rowid;
    SimpleCursorAdapter tripAdapter;
    String vehicle;
    TextView ok,cancel,message,complete;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_trip_list, container, false);
        isRunningActivity1 = true;
        //noDataLayout = (LinearLayout)view.findViewById(R.id.inboxLinearL);
        //titlelayout=(LinearLayout)view.findViewById(R.id.fragment_trip_list_img);
        bindData();

        tripListfragmentTvGetTripList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SendToWebService send = new SendToWebService(getActivity(), mGetTripList);
                    send.execute("43", "GetActiveTrips");
                }

                catch (Exception e){
                    Toast.makeText(getActivity(),
                            "Try after sometime...",
                            Toast.LENGTH_SHORT).show();
                    ExceptionMessage
                            .exceptionLog(
                                    getActivity(),
                                    this.getClass().toString()
                                            + " "
                                            + "[tripListfragmentTvGetTripList.setOnClickListener]",
                                    e.toString());
                }

            }
        });

        return view;
    }

    public void bindData() {

        trip = (ListView) view
                .findViewById(R.id.fragmentTripEntryListLV);

        fragmentTripLayout = (LinearLayout) view.findViewById(R.id.fragment_trip_list_img);
        tripListfragmentTvGetTripList = (TextView) view.findViewById(R.id.fragmentTripListTVGetTripList);
    }


    public void deleteTrip(final int pos, String Sflag) {

        try {
            String Voucher = null;
            SendToWebService send = new SendToWebService(getActivity(),
                    mTripListFragment);
            rowid = pos;

            if (tripCursor.moveToFirst()) {
                for (int i = 0; i < tripCursor.getCount(); i++) {

                    if (i == rowid) {
                        Voucher = tripCursor.getString(4);
                    }

                    tripCursor.moveToNext();
                }
            }
            tripCursor.close();


            if (Voucher != null) {
               // if (send.isConnectingToInternet()) {
                    try {
                        send.execute("21", "CloseVehicleOrDeleteTrip", Voucher, Sflag);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Try after sometime...",
                                Toast.LENGTH_SHORT).show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString() + " " + "[deleteTrip]",
                                e.toString());
                    }
//                } else {
//                    Toast.makeText(getActivity().getBaseContext(),
//                            "INTERNET CONNECTION ERROR!! PLEASE CHECK NETWORK",
//                            Toast.LENGTH_SHORT).show();
//                }
            }

            db.close();
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[deleteTrip()]", e.toString());
            e.toString();
        }

    }

    public void deleteTripData(String result) {
        try {
            if (result.equals("closed")) {
                Toast.makeText(getActivity(), "Trip Completed",
                        Toast.LENGTH_SHORT).show();
                refreshActivity();
            } else if (result.equals("deleted")) {
                Toast.makeText(getActivity(), "Trip Deleted",
                        Toast.LENGTH_SHORT).show();
                refreshActivity();
            } else {
                Toast.makeText(getActivity(), "Try After Sometime",
                        Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), "Try after sometime...");

            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[deleteTripData]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[deleteTripData]", e.toString());
        }


    }

    public void refreshActivity() {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), TripActivity.class);
        startActivity(intent);

    }

    @Override
    public void onDeleteTrip(String response) {
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
                String result = jsonParsing1(response);
                deleteTripData(result);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onDeleteTrip]", e.toString());
        }

    }



    @Override
    public void onGetTripList(String response) {

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
                getTripListjsonParsing(response);
                //displayTripList(result);

            }
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onGetTripList]", e.toString());
        }
    }


    public void getTripListjsonParsing(String response) {

        String statuschk = null;
        if (response != null)
            try {
                JSONObject obj=new JSONObject(response);
                String d= obj.getString("d");
                JSONArray expense = new JSONArray(d);
                JSONObject status1 = expense.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString() + " " + "[getTripListjsonParsing]",
                            statuschk);

                } else if (statuschk.equals("data does not exist")) {

                    fragmentTripLayout.removeAllViews();
                    fragmentTripLayout.setVisibility(View.VISIBLE);
                    trip.setVisibility(View.GONE);
                    ImageView imageView = new ImageView(getActivity());
                    imageView.setImageResource(R.drawable.nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    fragmentTripLayout.addView(imageView);

                    TextView textView=new TextView(getActivity());
                    textView.setText("NO DATA");
                    textView.setTextSize(14);
                    textView.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.gravity = Gravity.CENTER;
                    layoutParams1.topMargin = 20;
                    textView.setLayoutParams(layoutParams1);
                    fragmentTripLayout.addView(textView);
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[getTripListjsonParsing]",
                            statuschk);
                } else if(statuschk.equals("OK")) {
                    fragmentTripLayout.removeAllViews();
                    fragmentTripLayout.setVisibility(View.GONE);
                    trip.setVisibility(View.VISIBLE);
                    String[] columnNames = {"_id", "Vehicle No", "Date", "Driver", "Voucher"};
                    tripCursor = new MatrixCursor(columnNames);
                   // JSONArray tripValueArray=
                    for (int i = 1; i < expense.length(); i++) {

                        JSONObject expenseJSONObject = expense.getJSONObject(i);
                        String VehicleNo = expenseJSONObject.getString("vehicleNumber");
                        String Date = expenseJSONObject.getString("tripDate");
                        String[] parts = Date.split("T");
                        Date = parts[0];
                        String Name = expenseJSONObject.getString("driverName");
                        String Voucher = expenseJSONObject.getString("tripVoucher");
                        tripCursor.addRow(new Object[]{sKey, VehicleNo, Date, Name, Voucher});

                    }

                    displayTripList(statuschk);
                }

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing]", e.toString());
            }



    }


    public void displayTripList(String status) {
try {

        fragmentTripLayout.removeAllViews();
        fragmentTripLayout.setVisibility(View.GONE);

        String from[] = new String[]{"Vehicle No", "Date", "Driver"};

            int to[] = {R.id.textViewaccount2,
                    R.id.textView2account2, R.id.textView3account2};

            tripAdapter = new SimpleCursorAdapter(
                    getActivity(), R.layout.account4, tripCursor, from,
                    to, 0);

            trip.setAdapter(tripAdapter);
            tripAdapter.notifyDataSetChanged();


            trip.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    alertLongPressed(position);
                    return true;

                }
            });
            trip.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    alertClickPressed(position);

                }
            });


    }

catch (Exception e){
    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
            .toString() + " " + "[displayTripList]", e.toString());

}

    }


    public void alertLongPressed(final int pos)
    {
        try {

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View dialogView = inflater.inflate(R.layout.dialog_three_btn, null);
            builder.setView(dialogView);
            final AlertDialog alertDialog1 = builder.create();
            message = (TextView) dialogView.findViewById(R.id.textmsg);
            ok = (TextView) dialogView.findViewById(R.id.textDeleteBtn);
            complete = (TextView) dialogView.findViewById(R.id.textCompletwBtn);
            cancel = (TextView) dialogView.findViewById(R.id.textCancelBtn);
            message.setText("Trip Functions:");
            View v1 = inflater.inflate(R.layout.title_dialog_layout, null);
            alertDialog1.setCustomTitle(v1);

            ok.setText("DELETE");
            complete.setText("COMPLETE");
            cancel.setText("CANCEL");
            complete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTrip(pos, "7");
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTrip(pos, "0");
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
            int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
            View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
            titleDivider.setBackgroundColor(color);
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[alertLongPressed]", e.toString());
        }
    }

    public void alertClickPressed(final int pos)
    {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
            builder.setView(dialogView);
            final AlertDialog alertDialog1 = builder.create();
            message = (TextView) dialogView.findViewById(R.id.textmsg);
            ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
            cancel = (TextView) dialogView.findViewById(R.id.textCancelBtn);
            message.setText("Do you want to edit the trip ?");
            View v1 = inflater.inflate(R.layout.title_dialog_layout, null);
            alertDialog1.setCustomTitle(v1);

            ok.setText("EDIT");
            cancel.setText("CANCEL");
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String voucher = getVoucher(pos);
                    Intent in = new Intent(getActivity(), TripListDetailsActivity.class);
                    in.putExtra("voucher", voucher);
                    in.putExtra("vehicle", vehicle);
                    startActivity(in);
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
            int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
            View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
            titleDivider.setBackgroundColor(color);
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[alertClickPressed]", e.toString());
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
                status = d.getString("status");
                return status;

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
            }
        return status;
    }


    public String getVoucher(int pos) {


        String Voucher=null;
        // Id = String.valueOf(id);
        rowid = pos;
        try {

            if (tripCursor.moveToFirst()) {
                for (int i = 0; i < tripCursor.getCount(); i++) {

                    if (i == rowid) {
                        vehicle = tripCursor.getString(1);
                        Voucher = tripCursor.getString(4);

                    }

                    tripCursor.moveToNext();
                }
            }
            tripCursor.close();
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[getVoucher]", e.toString());
        }
        return  Voucher;
    }

    @Override
    public void onStart() {
        super.onStart();
        isRunningActivity1 = true;

    }

    @Override
    public void onStop() {
        super.onStop();

        isRunningActivity1 = false;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

            super.setUserVisibleHint(isVisibleToUser);
        try {
            if (isVisibleToUser) {
                TripActivity.pos = 2;
            }
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[setUserVisibleHint]", e.toString());
        }
    }
}
