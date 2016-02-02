package com.example.TrackingModule;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.ILiveTrack;
import com.example.Interface.ITrackingVehicleTrip;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TripMapFragment extends Fragment implements ILiveTrack, ITrackingVehicleTrip {

    static int count = 0;
    static String disable;
    static List<LatLng> routePoints = new ArrayList<LatLng>();
    final ILiveTrack mTrackLive = this;
    final ITrackingVehicleTrip mTrackingVehicleTrip = this;
    GoogleMap map;
    MarkerOptions currMark;
    ProgressDialog pd;
    Button track, loc;
    ArrayList<LatLng> markerPoints;
    String currentPlace = "";
    MarkerOptions in;
    ArrayList<LatLng> valuesList;
    LatLng currentVal;
    LatLng origin, dest;
    LatLng current;
    String tripStatus;
    String currId, srcId, destId;
    String vehNo;
    RelativeLayout ml;
    Boolean val = true;
    PolylineOptions polylineOptions;
    String currentLat,currentLong;
    String  Status,driverName, Speed="", RunningTime, KMTravelled,tripstatus1;
    double SourceLatitude = 0.0, SourceLongitude = 0.0,
            DestinationLatitude = 0.0, DestinationLongitude = 0.0,
            dCurrentLat = 0.0, dCurrentLng = 0.0;
    String SourceName = null, DestinationName = null, VehLV = "", CurrentTime = "";
    View view;
    Bundle save;
    AlertDialog.Builder builder;
    JSONObject o;
    //Older init
    private SupportMapFragment fragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {


        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(3);

        save = savedInstanceState;

        view = inflater.inflate(R.layout.track_map_fragment, container,
                false);

        ml = (RelativeLayout) view.findViewById(R.id.mapLayout);

        //Getting Button IDs
        track = (Button) view.findViewById(R.id.live);


        //Getting Image view id
        loc = (Button) view.findViewById(R.id.placeId);

        vehNo = TripDetailsFragment.VehLV;


        //Live Tracking

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (vehNo == null) {
                        Toast t = Toast.makeText(getActivity(), "Please Select Vehicle First", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                        ((TrackActivity) getActivity()).setCurrentItem(0, true);
                    }

                    if (o != null) {
                        if (o.getString("status").equals("data does not exist")) {
                            disable = "data does not exist";
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("No recent data!")
                                    .setCancelable(false)
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.setTitle("Vehicle not in Trip");
                            alert.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (origin == null && dest == null) {
                        Toast t = Toast.makeText(getActivity(), "Vehicle not in trip", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                        ((TrackActivity) getActivity()).setCurrentItem(0, true);
                    }
                    else {
                        SendToWebService send = new SendToWebService(getActivity(), mTrackLive);
                        if (send.isConnectingToInternet()) {
                            track.setEnabled(false);
                            MarkerOptions srcMrk = new MarkerOptions();
                            MarkerOptions destnMrk = new MarkerOptions();
                            if(origin!=null && dest!=null) {
                                srcMrk.position(origin);
                                destnMrk.position(dest);
                            }
                            srcMrk.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            // srcMrk.icon(BitmapDescriptorFactory.fromResource(R.drawable.startflag));
                            destnMrk.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            //  destnMrk.icon(BitmapDescriptorFactory.fromResource(R.drawable.endflag3));

                            if(srcMrk!=null && destnMrk!=null) {
                                Marker s = map.addMarker(srcMrk);
                                srcId = s.getId();
                                Marker d = map.addMarker(destnMrk);
                                destId = d.getId();
                            }


                            // Getting URL to the Google Directions API
                            String url = getDirectionsUrl(origin, current);

                            DownloadTask downloadTask = new DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);

                            startLiveTracking();
                        } else {

                            Toast.makeText(getActivity(),
                                    "No Internet Connection",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[track.onClickListener]",
                            e.toString());
                    e.printStackTrace();
                    e.printStackTrace();
                    Log.d("Err", e.getStackTrace().toString());
                }
            }
        });


        // Method to get current location & time
        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (vehNo == null) {
                        Toast t = Toast.makeText(getActivity(), "Please select vehicle first", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                        t.show();
                        ((TrackActivity) getActivity()).setCurrentItem(0, true);
                    } else if (currentPlace != null) {

                        SendToWebService send = new SendToWebService(getActivity(),
                                mTrackingVehicleTrip);

                        //if (send.isConnectingToInternet()) {
                        try {
                              if(VehicleListFragment.vehicleName!=null)
                            send.execute("15", "GetTrackingTripDetails", VehicleListFragment.vehicleName);

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
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString()
                            + " "
                            + "[CurrentLocation.setOnClickListener]", e.toString());
                }
            }
        });

        return view;
    }


    @Override
    public void onStop() {
        Fragment f = getFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        VehicleListFragment.res = null;
        super.onStop();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        try {
            super.onActivityCreated(savedInstanceState);

            FragmentManager fm = getChildFragmentManager();
            fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map, fragment).commit();
            }

            if (map == null) {
                map = fragment.getMap();
                map.getUiSettings().setZoomControlsEnabled(true);

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (val == false) {
                            ml.setVisibility(View.VISIBLE);
                            val = true;
                        } else if (val == true) {
                            ml.setVisibility(View.GONE);
                            val = false;
                        }
                    }
                });

            }


//        pd = new ProgressDialog(getActivity());
//        pd.setTitle("Loading Map");
//        pd.setMessage("Please wait until map loads...");
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setIndeterminate(true);
//        pd.setCancelable(true);
//        pd.show();

            GetLiveTracking(vehNo);
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onActivityCreated]", e.toString());
        }
    }


// @ Override Resume

    public void Resume() {
        try {
            if (map == null) {
                map = fragment.getMap();
            }
//        MarkerOptions currMark1 = new MarkerOptions();
//        currMark1.position(current);
//        map.clear();

            if (map != null && current != null) {
                currMark = new MarkerOptions();
                currMark.position(current);

                currMark.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                Marker m = map.addMarker(currMark);
                currId = m.getId();
                currentPlace = getLocation(current);
//            m.setTitle(currentPlace);
//            m.showInfoWindow();
                //----------------------------------------------------

                final Dialog d = new Dialog(getActivity());
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                d.setContentView(R.layout.info_window_layout1);
                TextView t1 = (TextView) d.findViewById(R.id.locHead);
                TextView t2 = (TextView) d.findViewById(R.id.locInfo);
                t1.setTextColor(Color.parseColor("#009acd"));
                t2.setTextColor(Color.BLACK);
                t1.setText("Current Location");
                String currPlace = getLocation(current);
                t2.setText(currentPlace);
                t2.setTextSize(12);
                m.setTitle(currentPlace);
                d.show();

                //---------------------------------------------------
                animatedCircles(current);
                map.moveCamera(CameraUpdateFactory.newLatLng(current));
                map.animateCamera(CameraUpdateFactory.zoomTo(10));
            }


            //     Customizing Marker Info Window
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater(save).inflate(R.layout.info_window_layout1, null);

                    v.setLayoutParams(new RelativeLayout.LayoutParams(400, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    TextView t1 = (TextView) v.findViewById(R.id.locHead);
                    TextView t2 = (TextView) v.findViewById(R.id.locInfo);
                    t1.setTextColor(Color.parseColor("#009acd"));
                    t2.setTextColor(Color.BLACK);
                    t2.setTextSize(12);

                    String s = marker.getId();
                    if (s.equals(currId)) {
                        t1.setText(Html.fromHtml("<font color='#009acd'><b>Current Location </b></font>"));
                        String currPlace = getLocation(current);
                        t2.setText(currPlace);
                        marker.setTitle(currPlace);
                    } else if (s.equals(srcId)) {
                        t1.setText(Html.fromHtml("<font color='#009acd'><b>Source </b></font>"));
                        String srcPlace = getLocation(origin);
                        t2.setText(srcPlace);
                        marker.setTitle(srcPlace);
                    } else if (s.equals(destId)) {
                        t1.setText(Html.fromHtml("<font color='#009acd'><b>Destination</b></font>"));
                        String destPlace = getLocation(dest);
                        t2.setText(destPlace);
                        marker.setTitle(destPlace);
                    }

                    return v;
                }
            });


            //      Method to stop Loading the spinner on MapLoad
//        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                try {
//                    spinner = (ProgressBar) getActivity().findViewById(R.id.progressBar);
//                    spinner.destroyDrawingCache();
//                    spinner.setVisibility(View.GONE);
//                    //               stopSpinner();
//                    //                     pd.dismiss();
//                } catch (Exception e) {
//                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
//                                    .toString() + " " + "[map.setOnMapLoadedCallback]",
//                            e.toString());
//
//                }
//            }
//        });


        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[resume()]", e.toString());
        }

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        if (menuVisible) {
            if (VehicleListFragment.res != null && TripDetailsFragment.bValid) {
                if (routePoints.isEmpty()) {
                    jsonParsing(VehicleListFragment.res);

                } else {
                    Log.e("msg", "Some Error has occurred");
                }
            }
        }
    }


    // JSON Parsing | Getting Lat & Lang of Source, Destination and Current
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void jsonParsing(String response) {

        try {
            String sCurrentLat = null, sCurrentLong = null;
            JSONObject jsonResponse1 = new JSONObject(response);
            // getting the data with tag d
            String jsonData1 = jsonResponse1.getString("d");
            // convert the string to Json array
            JSONArray trackArray = new JSONArray(jsonData1);

            // JSONObject sourceDetail = trackArray.getJSONObject(1);
            JSONArray sourceArray = trackArray.getJSONArray(1);
            if (sourceArray.length() != 0) {

                JSONObject sName = sourceArray.getJSONObject(0);
                if (!(sName.isNull("sourceName"))) {
                    SourceName = sName.getString("sourceName");
                }

                //JSONObject sLat = sourceArray.getJSONObject(1);
                if (!(sName.isNull("sourceLat"))) {
                    SourceLatitude = Double.valueOf(sName.getString("sourceLat"));
                }

                // JSONObject sLong = sourceArray.getJSONObject(2);
                if (!(sName.isNull("sourceLong"))) {
                    SourceLongitude = Double.valueOf(sName.getString("sourceLong"));
                }


            }


            //JSONObject destinationDetail = trackArray.getJSONObject(3);
            JSONArray destinationArray = trackArray.getJSONArray(3);
            if (destinationArray.length() != 0) {

                for (int i = 0; i < destinationArray.length(); i++) {

                    JSONObject dest = destinationArray.getJSONObject(i);
                    DestinationName = dest.getString("destinationName");


                    if (!(dest.isNull("destinationLat"))) {
                        DestinationLatitude = Double.valueOf(dest.getString("destinationLat"));
                    }

                    if (!(dest.isNull("destinationLong"))) {
                        DestinationLongitude = Double.valueOf(dest.getString("destinationLong"));
                    }


                }
            }


            //  JSONObject currentDetail = trackArray.getJSONObject(5);
            JSONArray currentArray = trackArray.getJSONArray(4);
            if (currentArray.length() != 0) {

                JSONObject cLat = currentArray.getJSONObject(1);
                if (!(cLat.isNull("currLatitude"))) {
                    sCurrentLat = cLat.getString("currLatitude").trim();
                }

                // JSONObject cLong = currentArray.getJSONObject(1);
                if (!(cLat.isNull("currLongitude"))) {
                    sCurrentLong = cLat.getString("currLongitude").trim();
                }

                if ((!sCurrentLat.equals("null"))
                        && !sCurrentLong.equals("null")
                        && !sCurrentLat.equals("")
                        && !sCurrentLong.equals("")) {
                    dCurrentLat = Double.parseDouble(sCurrentLat);
                    dCurrentLng = Double.parseDouble(sCurrentLong);
                    current = new LatLng(dCurrentLat, dCurrentLng);
//                    MarkerOptions currLoc = new MarkerOptions();
//                    currLoc.position(current);
//                    currLoc.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // JSONObject trStatus = sourceArray.getJSONObject(4);
                if (!(cLat.isNull("tripStatus"))) {
                    tripStatus = cLat.getString("tripStatus").trim();
                }


                //JSONObject recTime = sourceArray.getJSONObject(5);
                if (!(cLat.isNull("deviceDateTime"))) {
                    CurrentTime = cLat.getString("deviceDateTime").trim();
                }
            }

            if (tripStatus.equals("on trip")) {
                origin = new LatLng(SourceLatitude, SourceLongitude);
                dest = new LatLng(DestinationLatitude, DestinationLongitude);
                routePoints.add(dest);
            }

            Resume();

        } catch (JSONException e) {
            Toast.makeText(getActivity(),
                    "Vehicle : " + VehLV + " not in trip....",
                    Toast.LENGTH_LONG).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[JsonParsing]", e.toString());
        }

    }


    //   Method Load Spinner
//    public void startSpinner() {
//        spinner = (ProgressBar) getActivity().findViewById(R.id.progressBar);
//        spinner.setVisibility(View.VISIBLE);
//    }

//    // Method Stop Spinner
//    public void stopSpinner() {
//        spinner.destroyDrawingCache();
//        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
//    }

    // Animate circle
    public void animatedCircles(LatLng latlng) {
        try {
            final Circle circle = map.addCircle(new CircleOptions().center(latlng)
                    .strokeColor(Color.rgb(102, 255, 0)).radius(2500).fillColor(0x110000FF));

            ValueAnimator vAnimator = new ValueAnimator();
            vAnimator.setRepeatCount(ValueAnimator.INFINITE);
            vAnimator.setRepeatMode(ValueAnimator.RESTART);
            vAnimator.setIntValues(0, 100);
            vAnimator.setDuration(1000);
            vAnimator.setEvaluator(new IntEvaluator());
            vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    @SuppressLint({"NewApi", "LocalSuppress"}) float animatedFraction = valueAnimator.getAnimatedFraction();
                    circle.setRadius(animatedFraction * 100);
                }
            });
            vAnimator.start();
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[animatedCircles()]", e.toString());
            e.printStackTrace();
        }

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        // Getting lat and lang onClick of Map used in Distance calculation

        return url;

    }


    //Method for finding Location Address

    public String getLocation(LatLng sample) {
        String res = "Location Not Available";

        try {
            List<Address> addresses;
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(sample.latitude, sample.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            res = address + " " + city + " ";
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[getLocation()]", e.toString());
            e.printStackTrace();
        }
        return res;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[downloadUrl()]", e.toString());
            Log.d("Excptn in downloading", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    public void GetLiveTracking(String vehicleNumber) {
        SendToWebService send = new SendToWebService(getActivity(), mTrackLive);
        try {
            send.execute("42", "GetLiveTracking", vehicleNumber);

        } catch (Exception e) {

            ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString() + " " + "[LiveTrack]", e.toString());
        }
    }

    @Override
    public void onTrackLive(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                cd.ConnectingToInternet();
            } else if (response.contains("refused") || response.contains("timed out")) {
                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setPositiveButton("OK",
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();
            } else {
                try {
                    valuesList = new ArrayList<LatLng>();
//                    valuesList.add(new LatLng(12.953388, 77.533638));
//                    valuesList.add(new LatLng(12.907880, 77.462226));
//                    valuesList.add(new LatLng(12.867719, 77.432014));
//                    valuesList.add(new LatLng(12.814161, 77.407295));
//                    valuesList.add(new LatLng(12.806126, 77.396309));
//                    valuesList.add(new LatLng(12.782021, 77.363350));
//                    valuesList.add(new LatLng(12.739161, 77.302925));
//                    valuesList.add(new LatLng(12.698973, 77.250740));
//                    valuesList.add(new LatLng(12.664139, 77.201301));
//                    valuesList.add(new LatLng(12.642700, 77.151863));
//                    valuesList.add(new LatLng(12.621259, 77.102424));
//                    valuesList.add(new LatLng(12.602497, 77.041999));
//                    valuesList.add(new LatLng(12.573010, 77.000801));
//                    valuesList.add(new LatLng(12.554245, 76.954109));
//                    valuesList.add(new LatLng(12.516709, 76.868965));
//                    valuesList.add(new LatLng(12.476487, 76.753608));
//                    valuesList.add(new LatLng(12.446986, 76.701423));
//                    valuesList.add(new LatLng(12.387975, 76.660225));
//                    valuesList.add(new LatLng(12.310167, 76.558601));
//                    valuesList.add(new LatLng(12.240388, 76.473457));

                    JSONObject jsonResponse = new JSONObject(response);
                    String jsonData = jsonResponse.getString("d");
                    JSONArray tripData = new JSONArray(jsonData);
                    o = tripData.getJSONObject(0);
                    if (o.getString("status").equals("data does not exist")) {

                    } else {
                        disable = "OK";
                        boolean a = ((!o.getString("status").equals("invalid authkey")) && (!o.getString("status").equals("data does not exist")));
                        for (int i = 1; i < tripData.length(); i++) {
                            JSONObject c = tripData.getJSONObject(i);

                            String lat = c.getString("latitude");
                            String lon = c.getString("longitude");
                            String[] latlong = {lat, lon};
                            double latitude1 = Double.parseDouble(latlong[0]);
                            double longitude1 = Double.parseDouble(latlong[1]);
                            LatLng val = new LatLng(latitude1, longitude1);
                            valuesList.add(val);

                        }
                    }
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[onTrackLive()]", e.toString());
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onTrackLive()]", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackingVehicleTrip(String response)
    {
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
                String statuschk = null;
                MatrixCursor cursorDestination;
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String jsonData = jsonResponse.getString("d");
                    if (jsonData.equals("[]")) {
                        Toast.makeText(
                                getActivity(),
                                "Sorry This " + TripDetailsFragment.VehLV
                                        + " is not in trip!!!!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        TripMapFragment.routePoints.clear();
                        TripDetailsFragment.bValid = false;
                        if (response != null)
                        {
                            try {
                                JSONObject jsonResponse1 = new JSONObject(response);
                                // getting the data with tag d
                                String jsonData1 = jsonResponse1.getString("d");

                                JSONArray trackArray = new JSONArray(jsonData1);
                                JSONObject status1 = trackArray.getJSONObject(0);
                                statuschk = status1.getString("status").trim();

                                if (statuschk.equals("invalid authkey")) {
                                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                            .toString(), statuschk);
                                } else if (statuschk.equals("vehicle does not exist")) {
                                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                            .toString(), statuschk);
                                } else if (statuschk.equals("OK")) {
                                    JSONArray vehicleArray = trackArray.getJSONArray(4);
                                    if (vehicleArray.length() != 0) {

                                        JSONArray currentArray = trackArray.getJSONArray(4);
                                        if (currentArray.length() != 0) {

                                            JSONObject cLat = currentArray.getJSONObject(1);
                                            if (!(cLat.isNull("currLatitude"))) {
                                                currentLat = cLat.getString("currLatitude").trim();
                                            }

                                            // JSONObject cLong = currentArray.getJSONObject(1);
                                            if (!(cLat.isNull("currLongitude"))) {
                                                currentLong = cLat.getString("currLongitude").trim();
                                            }

                                            if ((!currentLat.equals("null"))
                                                    && !currentLong.equals("null")
                                                    && !currentLat.equals("")
                                                    && !currentLong.equals("")) {
                                                dCurrentLat = Double.parseDouble(currentLat);
                                                dCurrentLng = Double.parseDouble(currentLong);
                                                current = new LatLng(dCurrentLat, dCurrentLng);
                                            }
                                            if (!(cLat.isNull("deviceDateTime"))) {
                                                CurrentTime = cLat.getString("deviceDateTime").trim();
                                            }
                                            try {
                                                map.moveCamera(CameraUpdateFactory.newLatLng(current));
                                                map.animateCamera(CameraUpdateFactory.zoomTo(10));
                                                map.clear();
                                                MarkerOptions mark = new MarkerOptions();
                                                mark.position(current);
                                                map.addMarker(mark);

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setMessage(Html.fromHtml("<font color='#009acd'><b>Vehicle Name : </b></font>" + "<small>" + VehicleListFragment.vehicleName + "</small>" + "<br/><br/>" + "<font color='#009acd'><b>Location : </b></font>" + "<small>" + getLocation(current) + "</small>" + "<br/><br/>" + ("<font color='#009acd'><b>Time : </b></font>" + "<small>" + CurrentTime + "</small>")))

                                                        .setCancelable(false)
                                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                AlertDialog alert = builder.create();
                                                alert.setTitle(Html.fromHtml("<font color='#009acd'><b><small>Current Location & Time</small> </b></font>"));   // alert
                                                alert.show();
                                            } catch (Exception e) {
                                                ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString(), e.toString());
                                            }
                                        }

                                    }
                                }
                            }
                            catch (JSONException e)

                            {
                              ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString(), response);
                            }
                        }

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

    public void startLiveTracking() {
        HandlerThread hThread = new HandlerThread("HandlerThread");
        hThread.start();
        Handler handler = new Handler(Looper.getMainLooper());
        final Handler handler1 = new Handler(Looper.getMainLooper());
        final long twoSec = 2000;

        Runnable eachMinute = new Runnable() {
            @Override
            public void run() {
                if (count < valuesList.size()) {
                    if (map == null) {
                        map = fragment.getMap();

                    }
                    map.clear();

                    currentVal = valuesList.get(count);
                    map.moveCamera(CameraUpdateFactory.newLatLng(currentVal));
                    in = new MarkerOptions();
                    in.position(currentVal);

//                    // for adding polyline to map
//                    for(int i=0; i<valuesList.size(); i++) {
//                        polylineOptions.add(currentVal);
//                        map.addPolyline(polylineOptions);
//                    }

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, currentVal);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);


//                    animatedCircles(currentVal);
                    // in.icon(BitmapDescriptorFactory.fromResource(R.drawable.current));
                    in.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    MarkerOptions temp1 = new MarkerOptions();
                    MarkerOptions temp2 = new MarkerOptions();
                    temp1.position(origin);
                    temp2.position(dest);
//                    temp1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    // temp1.icon(BitmapDescriptorFactory.fromResource(R.drawable.sourceicon));
                    temp1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                    temp2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    //      temp2.icon(BitmapDescriptorFactory.fromResource(R.drawable.destinationicon));
                    temp2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    Marker t1 = map.addMarker(temp1);
                    Marker t2 = map.addMarker(temp2);

                    srcId = t1.getId();
                    destId = t2.getId();

                    try {
                        map.addMarker(in);
                        animatedCircles(currentVal);
                        currentPlace = getLocation(currentVal);
                        count++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("ErrMsg", "No values available in list");
                }

                handler1.postDelayed(this, twoSec);

            }
        };
        eachMinute.run();
        handler.postDelayed(eachMinute, twoSec);
        //     GetLiveTracking(vehNo);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            TrackActivity.pos = 3;
        }
    }

    // Fetches data from url passed
    public class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[doInBackground()]", e.toString());

                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser(getActivity());

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                .toString() + " " + "[ParserTask(doInBackground())]",
                        e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            //           markerOptions.title(getLocation(markerPoints.get(0)));
            try {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.color(Color.rgb(0, 172, 243));
                    lineOptions.width(4);
                }

                if (result.size() < 1) {
                    Toast.makeText(getActivity(), "No Points, Click on the Road Route", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Drawing polyline in the Google Map for the i-th route
                map.addPolyline(lineOptions);
                // To stop loading spinner after drawing Road Route between Source & Destination
                //       stopSpinner();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
