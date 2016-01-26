package com.example.TrackingModule;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

//import com.example.Interface.IStatusTrack;
import com.example.Interface.ITrackingVehicleTrip;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONException;
import org.json.JSONObject;

public class
        VehicleListFragment extends Fragment implements
        ITrackingVehicleTrip
{
    ArrayAdapter<String> adp;
    String vehNum;
    SearchView searchView;
    static String res = null;
    ListView vehPayLV;
    final ITrackingVehicleTrip mTrackingVehicleTrip = this;
    MenuItem item;
    LinearLayout noDataLayout;

    // final IStatusTrack mStatusTrack=this;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
        View view = inflater.inflate(R.layout.vehicle_lis_fragment, container,
                false);
        noDataLayout = (LinearLayout)view.findViewById(R.id.inboxLinearL);
        vehPayLV = (ListView) view.findViewById(R.id.TrackVehicleLV);
        System.out.println("inside VehicleListFragment-->onCreateView()");
//        SendToWebService send = new SendToWebService(getActivity(), mStatusTrack);
//        if (send.isConnectingToInternet()) {
//            try {
//
//                send.execute("29", "StatusIndicator");
//
//            } catch (Exception e) {
//                Toast.makeText(getActivity(), "Try after sometime...",
//                        Toast.LENGTH_SHORT).show();
//                ExceptionMessage.exceptionLog(getActivity(), this
//                        .getClass().toString()
//                        + " "
//                        + "[vehPayLV.setOnItemClickListener]", e
//                        .toString());
//            }
//        } else {
//            Toast.makeText(getActivity().getApplicationContext(),
//                    "INTERNET CONNECTION ERROR!! PLEASE CHECK NETWORK",
//                    Toast.LENGTH_SHORT).show();
//        }



        DBAdapter db = new DBAdapter(getActivity());
        db.open();

        Cursor vehicleCursor = db.getAllData(DBAdapter.getVehicleDetails());
         int count=vehicleCursor.getCount();
        if(count>0) {
            noDataLayout.setVisibility(View.GONE);
            vehPayLV.setVisibility(View.VISIBLE);
            // Putting CursorDB values into String array
            String[] vehArray = new String[vehicleCursor.getCount()];
            int i = 0;
            while (vehicleCursor.moveToNext()) {
                vehNum = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.getKeyVehicleNo()));
                vehArray[i] = vehNum;
                i++;
            }

            adp = new ArrayAdapter<String>(getActivity(), R.layout.account3, R.id.vehNumbersListViewTrackVehicle, vehArray);

            vehPayLV.setAdapter(adp);
            vehPayLV.setTextFilterEnabled(true);
            db.close();


            vehPayLV.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View selectedView,
                                        int arg2, long arg3) {
                    String Veh = vehPayLV.getItemAtPosition(arg2).toString();
                    TripDetailsFragment.VehLV = Veh;
                    MenuItemCompat.collapseActionView(item);


                    searchView.setIconifiedByDefault(true);
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


                    SendToWebService send = new SendToWebService(getActivity(),
                            mTrackingVehicleTrip);

                    //if (send.isConnectingToInternet()) {
                    try {

                        send.execute("15", "GetTrackingTripDetails", Veh);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Try after sometime...",
                                Toast.LENGTH_SHORT).show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString()
                                + " "
                                + "[vehPayLV.setOnItemClickListener]", e
                                .toString());
                    }
                }
            });

        }
    else
    {
        noDataLayout.setVisibility(View.VISIBLE);
        vehPayLV.setVisibility(View.GONE);
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(R.drawable.nodata);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 100;
        imageView.setLayoutParams(layoutParams);
        noDataLayout.addView(imageView);

        TextView textView=new TextView(getActivity());
        textView.setText("NO VEHICLE LIST");
        textView.setTextSize(14);
        textView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER;
        layoutParams1.topMargin = 20;
        textView.setLayoutParams(layoutParams1);
        noDataLayout.addView(textView);
    }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.remove("android:support:fragments");
    }



    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        item = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Enter Vehicle Number");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adp.getFilter().filter(newText);
                System.out.println("on text change text: " + newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adp.getFilter().filter(query);
                System.out.println("on query submit: " + query);
                MenuItemCompat.collapseActionView(item);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean started = true;
        if(started)
        {
            menu.removeItem(R.id.action_profile);
            menu.removeItem(R.id.action_help);
            menu.removeItem(R.id.action_logout);
        }
    }

    @Override
    public void onTrackingVehicleTrip(String response) {
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
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    // getting the data with tag d
                    String jsonData = jsonResponse.getString("d");
                    // String jsonData="";
                    // convert the string to Json array
                    if (jsonData.equals("[]")) {
                        res = null;
                        Toast.makeText(
                                getActivity(),
                                "Sorry This " + TripDetailsFragment.VehLV
                                        + " is not in trip!!!!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        TripMapFragment.routePoints.clear();
                        TripDetailsFragment.bValid = false;
                        res = response;
                        // ExceptionMessage.exceptionLog(getActivity(),
                        // this.getClass().toString(), response);

                        ((TrackActivity) getActivity()).setCurrentItem(1, true);

                    }
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

                    } else {
                        Toast.makeText(getActivity(),
                                "Try After SomeTimes....", Toast.LENGTH_LONG)
                                .show();
                    }
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString()
                            + " "
                            + "[onTrackingVehicleTrip]", e.toString() + " "
                            + TripDetailsFragment.VehLV);
                }
            }
        }
    }







//    @Override
//    public void onGetStatusTrack(String response)
//    {
//        if (response.equals("No Internet")) {
//            ConnectionDetector cd = new ConnectionDetector(getActivity());
//            cd.ConnectingToInternet();
//        } else if (response.contains("refused") || response.contains("timed out")) {
//            ImageView image = new ImageView(getActivity());
//            image.setImageResource(R.drawable.lowconnection3);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
//                    .setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    dialog.dismiss();
//                                }
//                            }).setView(image);
//            builder.create().show();
//
//        } else if (response.contains("java.net.SocketTimeoutException")) {
//
//            ImageView image = new ImageView(getActivity());
//            image.setImageResource(R.drawable.lowconnection3);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
//                    .setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int which) {
//                                    dialog.dismiss();
//                                }
//                            }).setView(image);
//            builder.create().show();
//
//        } else {
//
//            if (response != null) {
//                try {
//                    JSONObject jsonResponse = new JSONObject(response);
//                    // getting the data with tag d
//                    String jsonData = jsonResponse.getString("d");
//                    // String jsonData="";
//                    // convert the string to Json array
//                    if (jsonData.equals("[]")) {
//                        res = null;
//                        Toast.makeText(
//                                getActivity(), "sorry no data found on server!!!!", Toast.LENGTH_SHORT).show();
//                    }
//                    } else
//                    {
//                        String id = jsonResponse.getString("_id");
//                        String vehicleNum = jsonResponse.getString("vehicleNum");
//                        final String status= jsonResponse.getString("status");
//
//                        MatrixCursor mc = new MatrixCursor(new String[] { "_id" ,"vehicleNum", "status" });
//                        mc.addRow(new Object[]{id,vehicleNum ,status});
//
//                        caCleaner = new SimpleCursorAdapter(getActivity(), R.layout.status, mc, new String[]{"VehicleNum", "status"}, new int[]{R.id.vehNumbersListViewTrackVehicle, R.id.statusIndicatorTrackVehicle},0);
//
//                        caCleaner.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//                            @Override
//                            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                                if (columnIndex == 2) {
//
//
//                                    if (status.equals("present")) {
//                                        Resources res = getResources();
//                                        Drawable drawable = res.getDrawable(R.drawable.green);
//                                        bitmap = ((BitmapDrawable) drawable).getBitmap();
//
//                                        ((ImageView) view).setImageBitmap(bitmap);
//
//                                    } else if (status.equals("last")) {
//                                        Resources res = getResources();
//                                        Drawable drawable = res.getDrawable(R.drawable.red);
//                                        bitmap = ((BitmapDrawable) drawable).getBitmap();
//
//                                        ((ImageView) view).setImageBitmap(bitmap);
//
//                                    } else {
//                                        Toast.makeText(getActivity(), "its not valid status" + columnIndex, Toast.LENGTH_LONG).show();
//                                    }
//                                    return true;
//                                }
//                                return false;
//                            }
//                        });
//                        vehPayLV.setAdapter(caCleaner);
//                       caCleaner.notifyDataSetChanged();
//                    }
//                } catch (JSONException e) {
//
//                    if (e.toString().contains("refused")) {
//                        ImageView image = new ImageView(getActivity());
//                        image.setImageResource(R.drawable.lowconnection3);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(
//                                getActivity()).setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).setView(image);
//                        builder.create().show();

    // }
//else if (e.toString().contains(
//                            "java.net.SocketTimeoutException")) {
//
//                        ImageView image = new ImageView(getActivity());
//                        image.setImageResource(R.drawable.lowconnection3);
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(
//                                getActivity()).setPositiveButton("OK",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).setView(image);
//                        builder.create().show();
//
//                    } else {
//                        Toast.makeText(getActivity(),
//                                "Try After SomeTimes....", Toast.LENGTH_LONG)
//                                .show();
//                    }
//                    ExceptionMessage.exceptionLog(getActivity(), this
//                            .getClass().toString()
//                            + " "
//                            + "[onTrackingVehicleTrip]", e.toString() + " "
//                            + TripDetailsFragment.VehLV);
//                }
//            }
//    }
    //  }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            TrackActivity.pos=1;
        }
    }
}
