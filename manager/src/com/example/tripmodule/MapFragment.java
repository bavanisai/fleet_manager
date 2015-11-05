package com.example.tripmodule;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends Fragment {

    public static GoogleMap mMap;
    public SupportMapFragment fragment;
    public Marker marker;
    ImageView btnSearch;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    GetLatLong getLatLng;
    Double lat, lng;
    LatLng LocationHere;
    MenuItem item;
    SearchView searchView;
    public static Geocoder geocoder;
    public static Dialog d;
    public static Context con;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
       geocoder = new Geocoder(getActivity(),Locale.getDefault());
       d = new Dialog(getActivity());
        con=getActivity();

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        // setUpMapIfNeeded();
        try {
       //     ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

            atvPlaces = (AutoCompleteTextView) view
                    .findViewById(R.id.atv_places);


           // AUTOCOMPLETE TEXT VIEW

            atvPlaces.setThreshold(3);

            atvPlaces.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                    try {
                        if (isConnectingToInternet()) {
                            // GOOGLE PLACES API
                            placesTask = new PlacesTask();
                            placesTask.execute(s.toString());
                        }
                    } catch (Exception e) {

                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString(), e.toString());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception e) {

            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map1);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map1, fragment).commit();
        }
    }

    @Override
    public void onPause() {
        // super.onDestroyView();
        Fragment f = getFragmentManager().findFragmentById(R.id.map1);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
        AddLocation.Longitude = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            mMap = fragment.getMap();
            // mMap.addMarker(new MarkerOptions().position(new LatLng(0,
            // 0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.blink)));
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLongClickListener(my2_OnMapLongClickListener);

            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                public void onInfoWindowClick(Marker marker) {
                    LatLng point = marker.getPosition();
                    // Bitmap blink =
                    // BitmapFactory.decodeResource(getResources(),
                    // R.drawable.blink);
                    // BitmapDescriptor blinker =
                    // BitmapDescriptorFactory.fromBitmap(blink);
                    // GroundOverlay groundOverlay = mMap.addGroundOverlay(new
                    // GroundOverlayOptions()
                    // .image(blinker).position(point, 100)
                    // .transparency((float) 0.5));
                    AddLocation.Lattitude = String.valueOf(point.latitude);
                    AddLocation.Longitude = String.valueOf(point.longitude);
                    AddLocation.Name = null;
                    ((LocationActivity) getActivity()).setCurrentItem(1, true);
                }
            });
        }
        if (AddLocation.Longitude != null) {
            LatLng Location = new LatLng(Double.valueOf(AddLocation.Lattitude),
                    Double.valueOf(AddLocation.Longitude));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Location, 17));
            mMap.addMarker(new MarkerOptions().position(Location).title(
                    AddLocation.Name));
        }
    }

    OnMapLongClickListener my2_OnMapLongClickListener = new OnMapLongClickListener() {

        public void onMapLongClick(final LatLng point) {

            String Address;
            Address = getAddress1(point.latitude, point.longitude);
            if (Address == null) {
                Address = "Location";
            }

            marker = mMap.addMarker(new MarkerOptions().position(point));
            final Dialog d = new Dialog(getActivity());
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            d.setContentView(R.layout.info_window_layout2);
            TextView t1 = (TextView) d.findViewById(R.id.locHead1);
            TextView t2 = (TextView) d.findViewById(R.id.locInfo1);
            Button ok=(Button)d.findViewById(R.id.ok);
            Button cancel=(Button)d.findViewById(R.id.cancel);
            t1.setTextColor(Color.parseColor("#009acd"));
            t2.setTextColor(Color.BLACK);
            t1.setText("Location");
            String currPlace = getLocation(LocationHere);
            t2.setText(currPlace);
            t2.setTextSize(12);
            marker.setTitle(currPlace);
            //m.showInfoWindow();
            ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    AddLocation.Lattitude = String.valueOf(point.latitude);
                    AddLocation.Longitude = String.valueOf(point.longitude);
                    AddLocation.Name = null;
                    ((LocationActivity) getActivity()).setCurrentItem(1, true);
                    d.dismiss();
                }
            });

            cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.clear();
                    d.dismiss();
                }
            });
            d.show();
//            marker = mMap.addMarker(new MarkerOptions().position(point).title(
//                    Address));
//            marker.showInfoWindow();
        }

    };

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    //Method for finding Location Address

    public String getLocation(LatLng sample) {
        String res = "Location Not Available";

        try {
            List<Address> addresses;

            addresses = geocoder.getFromLocation(sample.latitude, sample.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            res = address + " " + city + " " ;
        }
        catch (Exception e)
        {
            ExceptionMessage.exceptionLog(getActivity(), this
                    .getClass().toString()+ " "
                    + "[getLocation()]", e.toString());
            e.printStackTrace();
        }
        return res;
    }
    private String getAddress1(double LATITUDE, double LONGITUDE) {
        String strAdd = null;
     //   Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        Geocoder geocoder=new Geocoder(getActivity());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE,
                    LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            "\n");
                }
                strAdd = strReturnedAddress.toString();
                // Log.w("My Current loction address",
                // "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (IOException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
        return strAdd;
    }

    // private void setUpMapIfNeeded() {
    //
    // try
    // {
    // if (mMap == null) {
    //
    // if (mMap != null) {
    // }
    // }
    // }
    // catch(Exception e)
    // {
    // //Toast.makeText(getActivity(), "Error in set uping map",
    // Toast.LENGTH_SHORT).show();
    // Log.e("My App", e.toString(), e);
    // }
    // }
    // GEOCODER WITH GOOGLE MAP

    private class GetLatLong extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // android.os.Debug.waitForDebugger();
            // For storing data from web service
            String uri = null;
            try {
                uri = "http://maps.google.com/maps/api/geocode/json?address="
                        + URLEncoder.encode(place[0], "UTF-8")
                        + "&sensor=false";
            } catch (UnsupportedEncodingException e) {

                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            }

            HttpGet httpGet = new HttpGet(uri);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();
            String latlng = null;
            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            } catch (IOException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());
                // "status" : "ZERO_RESULTS"
                String status = jsonObject.getString("status").trim();
                if (status.equals("OK")) {
                    double lng = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").getDouble("lng");

                    double lat = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").getDouble("lat");

                    Log.d("latitude", "" + lat);
                    Log.d("longitude", "" + lng);
                    latlng = Double.toString(lng) + ":" + Double.toString(lat);
                } else {
                    latlng = "No";
                    // Toast.makeText(getActivity(),
                    // "Data not available", 0).show();
                }
            } catch (JSONException e) {

                Toast.makeText(getActivity(), "Try after some time",
                        Toast.LENGTH_LONG).show();
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());

            }
            return latlng;

        }

        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(getActivity(), result, 0).show();
            if (!result.equals("No")) {
                StringTokenizer a1 = new StringTokenizer(result, ":");
                if (a1.hasMoreTokens()) {

                    lng = Double.valueOf(a1.nextToken());
                    lat = Double.valueOf(a1.nextToken());
                    LocationHere = new LatLng(lat, lng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LocationHere, 15));
                 //   String currPlace =getLocation(LocationHere);
//                    mMap.addMarker(new MarkerOptions().position(LocationHere)
//                            .title(currPlace));

                                 marker = mMap.addMarker(new MarkerOptions().position(LocationHere));


                                d.setContentView(R.layout.info_window_layout2);
                                TextView t1 = (TextView) d.findViewById(R.id.locHead1);
                                TextView t2 = (TextView) d.findViewById(R.id.locInfo1);
                                Button ok=(Button)d.findViewById(R.id.ok);
                                Button cancel=(Button)d.findViewById(R.id.cancel);
                                t1.setTextColor(Color.parseColor("#009acd"));
                                t2.setTextColor(Color.BLACK);
                                t1.setText("Location");
                                String currPlace = getLocation(LocationHere);
                                t2.setText(currPlace);
                                t2.setTextSize(12);
                                marker.setTitle(currPlace);
                                //m.showInfoWindow();
                                ok.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        AddLocation.Lattitude = String.valueOf(LocationHere.latitude);
                                        AddLocation.Longitude = String.valueOf(LocationHere.longitude);
                                        AddLocation.Name = null;

                                        ((LocationActivity)con).setCurrentItem(1, true);
                                        d.dismiss();
                                    }
                                });

                                cancel.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    mMap.clear();
                                        d.dismiss();
                                    }
                                });
                                d.show();

                }
            } else {
                Toast.makeText(getActivity(), "Please Enter Valid Location",
                        Toast.LENGTH_LONG).show();
                atvPlaces.setText("");
            }
            super.onPostExecute(result);

        }
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyDs1SibAHORxgrR39lDDDJsE1xH87EOWYI";

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                    + output + "?" + parameters;

            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /**
     * A method to download json data from url
     */
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

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser(getActivity());

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString(), e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), result,
                    android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }

    public void getSearchData(String loc){

        getLatLng = new GetLatLong();
        try {

            getLatLng.execute(loc);

        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Try after some time",
                    Toast.LENGTH_LONG).show();
            ExceptionMessage.exceptionLog(getActivity(),
                    this.getClass().toString(),
                    e.toString());
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//    {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater = getActivity().getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
//
//        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
//        item = menu.findItem(R.id.action_search);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setQueryHint("Enter Vehicle Number");
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
//        searchView.setIconifiedByDefault(false);
//
//        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adp.getFilter().filter(newText);
//                System.out.println("on text change text: " + newText);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adp.getFilter().filter(query);
//                System.out.println("on query submit: " + query);
//                MenuItemCompat.collapseActionView(item);
//                return true;
//            }
//        };
//        searchView.setOnQueryTextListener(textChangeListener);
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        boolean started = true;
//        if(started)
//        {
//            menu.removeItem(R.id.action_profile);
//            menu.removeItem(R.id.action_help);
//            menu.removeItem(R.id.action_logout);
//        }
//    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            LocationActivity.pos=3;
        }
    }
}