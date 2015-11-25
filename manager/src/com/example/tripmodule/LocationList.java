package com.example.tripmodule;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IDeleteLocation;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

public class LocationList extends Fragment implements IDeleteLocation {

    ListView dest;
    DBAdapter db;
    final IDeleteLocation mDeleteLocation = this;
    String Id;
    LinearLayout noDataLayout;
    String adress = new IpAddress().getIpAddress();
    TextView ok,message,cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location_list,
                container, false);
        db = new DBAdapter(getActivity());
        noDataLayout = (LinearLayout) view.findViewById(R.id.inboxLinearL);
        dest = (ListView) view.findViewById(R.id.DestinationList);

        StartMethod();
        this.dest.setLongClickable(true);
        this.dest.setClickable(true);
        // ListView Item Long click triggering
        this.dest.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                alertBackPressed(id);
                return false;
            }
        });

        return view;
    }

    public void alertBackPressed(final long id1)
    {
        String DestName1 = null;
        db.open();
        Cursor c = db.getLatLongDest(id1);
        if (c.moveToFirst()) {

            DestName1 = c.getString(3);
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Delete "+DestName1+" ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("DELETE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DeleteDestination(id1);
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


    protected void DeleteDestination(long id1) {
        String DestName1 = null;
        db.open();
        Cursor c = db.getLatLongDest(id1);
        if (c.moveToFirst()) {

            DestName1 = c.getString(3);
        }
        Id = String.valueOf(id1);

        SendToWebService send = new SendToWebService(getActivity(),
                mDeleteLocation);
        try {
                send.execute("17", "DeleteLocation", DestName1);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Try after sometime...",
                        Toast.LENGTH_SHORT).show();
                ExceptionMessage
                        .exceptionLog(getActivity(), this.getClass().toString()
                                + " " + "[DeleteDestination]", e.toString());
            }
        db.close();
    }

    protected void StartMethod() {
        db.open();
        Cursor Cleaneraccounts = db.getAllDestination();
        if(Cleaneraccounts.getCount()>0)
        {
            noDataLayout.setVisibility(View.GONE);
        dest.setVisibility(View.VISIBLE);
        String from[] = {DBAdapter.getKeyLocName(),
                DBAdapter.getKeyLocationtype()};
        int to[] = {R.id.textViewaccount21, R.id.textView2account22};

        MyCursorAdapter caCleaner = new MyCursorAdapter(getActivity(),
                R.layout.accountlocation, Cleaneraccounts, from, to, 0);
        dest.setAdapter(caCleaner);
        db.close();
    }

    else
    {
        noDataLayout.setVisibility(View.VISIBLE);
        dest.setVisibility(View.GONE);
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.nodata);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 100;
        imageView.setLayoutParams(layoutParams);
        noDataLayout.addView(imageView);
        TextView textView=new TextView(getActivity());
        textView.setText("NO DATA");
        textView.setTextSize(14);
        textView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER;
        layoutParams1.topMargin = 20;
        textView.setLayoutParams(layoutParams1);
       noDataLayout.addView(textView);
    }

    }

    public void refreshActivity() {
        getActivity().finish();
        Intent i = new Intent(getActivity(), LocationActivity.class);
        startActivity(i);

    }

    // @Override
    // public void setMenuVisibility(boolean menuVisible) {
    // // super.setMenuVisibility(menuVisible);
    // if (AddLocation.a != null) {
    // refreshActivity();
    // AddLocation.a = null;
    // }
    // }

    @Override
    public void onDeleteLocation(String response) {
        if (response.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            cd.ConnectingToInternet();
        } else if (response.contains("refused") || response.contains("timed out")) {
            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setPositiveButton("OK",
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setPositiveButton("OK",
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
                jsonData = d.getString("status").trim();

                switch (jsonData) {
                    case "deleted":
                        db.open();
                        db.deleteDest(DBAdapter.getLocation(), Id);
                        db.close();
                        refreshActivity();
                        break;

                    case "invalid authkey":
                        ExceptionMessage.exceptionLog(getActivity(),
                                this.getClass().toString() + " "
                                        + "[onDeleteLocation]", jsonData);
                        break;

                    case "location name does not exist":
                        ExceptionMessage.exceptionLog(getActivity(),
                                this.getClass().toString() + " "
                                        + "[onDeleteLocation]", jsonData);
                        break;

                    case "unknown error":
                        ExceptionMessage.exceptionLog(getActivity(),
                                this.getClass().toString() + " "
                                        + "[onDeleteLocation]", jsonData);
                        break;

                    default:
                        Toast.makeText(getActivity(),
                                "Please try after sometime....", Toast.LENGTH_LONG)
                                .show();
                        ExceptionMessage.exceptionLog(getActivity(),
                                this.getClass().toString() + " "
                                        + "[onDeleteLocation]", jsonData);
                        break;

                }

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onDeleteLocation]", e.toString());
            } catch (SQLiteException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onDeleteLocation]", e.toString());
            }

        }

    }

    // extend the SimpleCursorAdapter to create a custom class where we
    // can override the getView to change the row colors
    private class MyCursorAdapter extends SimpleCursorAdapter {

        public MyCursorAdapter(Context context, int layout, Cursor c,
                               String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // get reference to the row
            View view = super.getView(position, convertView, parent);
            // check for odd or even to set alternate colors to the row
            // background
            // if(position % 2 == 0){
            // view.setBackgroundColor(Color.rgb(238, 233, 233));
            // }
            // else {
            // view.setBackgroundColor(Color.rgb(255, 255, 255));
            // }
            return view;
        }

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            LocationActivity.pos=1;
        }
    }
}
