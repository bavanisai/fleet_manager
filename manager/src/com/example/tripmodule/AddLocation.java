package com.example.tripmodule;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IAddLocation;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

public class AddLocation extends Fragment implements OnClickListener,
        IAddLocation {

    Button savebtn, pasteBtn;
    EditText DestName;
    TextView Long, Latt;
    Spinner LocType;
    ArrayList<String> LocationType = new ArrayList<String>();
    // LinearLayout l1,l2;
    DBAdapter db = new DBAdapter(getActivity());
    String dname, Location, lat, lon, dist, amount, ClipLat, ClipLong;
    private boolean isUpdated = false;
    static String Lattitude = null, Longitude = null, Name = null,
            Amount1 = null, Distance = null, Type = null;
    static View view;
    final IAddLocation mAddLocation = this;
    String adress = new IpAddress().getIpAddress();
    ClipboardManager cli;
    ArrayAdapter<String> LocList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_location, container,
                false);
        bindData();
        try {
            cli = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
            if (cli.hasPrimaryClip()) {
                ClipData abc = cli.getPrimaryClip();

                ClipData.Item item = abc.getItemAt(0);

                if (item.getText() != null) {
                    String text = item.getText().toString();
                    if (text.contains("@")) {
                        StringTokenizer Trip = new StringTokenizer(text, "@");
                        ClipLat = Trip.nextToken();
                        ClipLong = Trip.nextToken();
                        pasteBtn.setVisibility(View.VISIBLE);
                    }
                }

            }
            DestName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //click on add location n move to map

                    ((LocationActivity) getActivity()).setCurrentItem(2, true);

                }
            });
            db = new DBAdapter(getActivity());
            pasteBtn.setOnClickListener(this);
            savebtn.setOnClickListener(this);
            LocationType.add("Destination");
            LocationType.add("Source");
            LocList = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, LocationType);
            LocList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            LocType.setAdapter(LocList);

            LocType.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    String type = LocType.getSelectedItem().toString();


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        catch (Exception e)
        {
            ExceptionMessage.exceptionLog(getActivity(), this
                    .getClass().toString()
                    + " "
                    + "[onCreate]", e.toString());
        }
        return view;
    }

    private void bindData() {
        Long = (TextView) view.findViewById(R.id.LongText);
        Latt = (TextView) view.findViewById(R.id.LatText);
        DestName = (EditText) view.findViewById(R.id.DestAddText);
        savebtn = (Button) view.findViewById(R.id.AddDestSavebtn);
        LocType = (Spinner) view.findViewById(R.id.AddLocTypeSpinner);
        pasteBtn = (Button) view.findViewById(R.id.fragmentaddlocationpasteBtn);
    }

    private void getSetData() {
        lon = Long.getText().toString();
        lat = Latt.getText().toString();
        dname = DestName.getText().toString();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AddDestSavebtn:
                SaveDestination();
                break;
            case R.id.fragmentaddlocationpasteBtn:
                Latt.setText(ClipLat);
                Long.setText(ClipLong);
                cli = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text", null);
                cli.setPrimaryClip(myClip);
                pasteBtn.setVisibility(View.GONE);
                break;


        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        // super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            if (Lattitude != null && Longitude != null) {
                bindData();
                if (Type != null) {
                    if (Type.equals("SOURCE"))
                        LocType.setSelection(1);
                    else
                        LocType.setSelection(0);
                }

                if (Name != null && Distance != null && Amount1 != null) {
                    DestName.setText(Name);


                } else if (Name == null) {
                    DestName.setText("");

                    LocType.setSelection(0);
                }
                Long.setText(Longitude);
                Latt.setText(Lattitude);
                DestName.setText(Name);

            }
        }

    }

    private void SaveDestination() {
        String srcExist = "";
        getSetData();
        if(lon.equals("") && lat.equals("") && dname.equals(""))
        {
            Toast.makeText(getActivity(),"Please fill all the fields !",Toast.LENGTH_LONG).show();
        }
        else if(lon.equals(""))
        {
            Toast.makeText(getActivity(),"Please add Longitude !",Toast.LENGTH_LONG).show();
        }
        else if(lat.equals(""))
        {
            Toast.makeText(getActivity(),"Please add Latitude !",Toast.LENGTH_LONG).show();
        }
        else if(dname.equals(""))
        {
            Toast.makeText(getActivity(),"Please add Destination name!",Toast.LENGTH_LONG).show();
        }
        else
        {
            db.open();
            String Loc = db.checkDestTableforDataExist(dname);

            db.close();
            Location = LocType.getSelectedItem().toString();
            if (Location.contains("SOURCE")) {
                db.open();
                srcExist = db.checkLocationTableforDataExist();
                db.close();
            }
            if (srcExist == "NOT EXIST" || srcExist == "") {
                SendToWebService send = new SendToWebService(getActivity(),
                        mAddLocation);

                if (send.isConnectingToInternet()) {
                    try {
                        if (Loc == "NOT EXIST") {
                            send.execute("2", "ManageLocation", dname,
                                    Location, lat, lon, "4");
                        } else {
                            alertUpdate();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Try after sometime...",
                                Toast.LENGTH_SHORT).show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString()
                                + " "
                                + "[SaveDestination]", e.toString());
                    }
                } else {
                    Toast.makeText(getActivity().getBaseContext(),
                            "INTERNET CONNECTION ERROR!! PLEASE CHECK NETWORK",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Long.setText("");
                Latt.setText("");
                DestName.setText("");
                LocType.setSelection(LocList.getPosition("DESTINATION"));
                Toast.makeText(
                        getActivity(),
                        "Source Already exists.Delete the existing Source to add new source",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean alertUpdate() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Do u want Update Location?");
        // alert.setMessage("Message");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Your action here
                SendToWebService send = new SendToWebService(getActivity(),
                        mAddLocation);
                if (send.isConnectingToInternet()) {
                    try {
                        send.execute("2", "ManageLocationTable", dname,
                                Location, lat, lon, "1");

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Try after sometime...",
                                Toast.LENGTH_SHORT).show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString() + " " + "[alertUpdate]",
                                e.toString());
                    }
                } else {
                    Toast.makeText(getActivity().getBaseContext(),
                            "INTERNET CONNECTION ERROR!! PLEASE CHECK NETWORK",
                            Toast.LENGTH_SHORT).show();
                }
                isUpdated = true;
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        isUpdated = false;
                    }
                });

        alert.show();
        return isUpdated;

    }

    private void saveDestinationData(String parsedValue) {
        bindData();
        getSetData();
        try {
            switch (parsedValue) {
                case "inserted":
                    Location = LocType.getSelectedItem().toString();
                    db.open();
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyLongitude(), lon);
                    cv.put(DBAdapter.getKeyLatitude(), lat);
                    cv.put(DBAdapter.getKeyLocName(), dname);
                    cv.put(DBAdapter.getKeyLocationtype(), Location);
                    long id = db.insertContact(DBAdapter.getLocation(), cv);

                    if (id != -1)
                        Toast.makeText(getActivity(),
                                "Location Saved Successfully", Toast.LENGTH_SHORT)
                                .show();
                    else {

                    }
                    db.close();
                    break;

                case "location name already exist":
                    Toast.makeText(getActivity(),
                            "Sorry.. Entered location name is already exists",
                            Toast.LENGTH_SHORT).show();
                    break;

                case "updated":
                    db.open();
                    ContentValues cv1 = new ContentValues();
                    cv1.put(DBAdapter.getKeyLongitude(), lon);
                    cv1.put(DBAdapter.getKeyLatitude(), lat);
                    cv1.put(DBAdapter.getKeyLocName(), dname);
                    cv1.put(DBAdapter.getKeyLocationtype(), Location);
                    db.updateDest(DBAdapter.getLocation(), cv1, dname);
                    Toast.makeText(getActivity(), "Location is Updated",
                            Toast.LENGTH_SHORT).show();
                    db.close();
                    break;

                case "location name does not exist":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveDestinationData]",
                            parsedValue);
                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveDestinationData]",
                            parsedValue);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveDestinationData]",
                            parsedValue);
                    break;

                default:
                    Toast.makeText(getActivity(), "Please try after sometime....",
                            Toast.LENGTH_LONG).show();
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[saveDestinationData]",
                            parsedValue);
                    break;

            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[saveDestinationData]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[saveDestinationData]", e.toString());
        }

        refreshActivity();

    }

    public void refreshActivity() {

        Intent intent = new Intent(getActivity(), LocationActivity.class);
        startActivity(intent);
        getActivity().finish();

    }

    @Override
    public void onManageLocationTable(String response) {
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
                String parsedValue = jsonParsing1(response);
                saveDestinationData(parsedValue);
                ((LocationActivity) getActivity()).setCurrentItem(0, true);
            }
        } catch (Exception e) {
            ExceptionMessage
                    .exceptionLog(getActivity(), this.getClass().toString()
                            + " " + "[onManageLocationTable]", e.toString());
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            LocationActivity.pos=2;
            LocationActivity.search1.setVisibility(View.GONE);
            LocationActivity.search.setVisibility(View.GONE);
        }
    }
}
