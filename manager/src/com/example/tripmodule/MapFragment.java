package com.example.tripmodule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class MapFragment extends Fragment implements GoogleMap.OnMarkerDragListener {

    public static GoogleMap mMap;
    String currPlace;
    double dragLat, dragLng;
    String dragPlace;
    public SupportMapFragment fragment;
    public Marker marker;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    GetLatLong getLatLng;
    Double lat, lng;
    LatLng LocationHere;
    public static Geocoder geocoder;
    public static Dialog d;
    public static Context con;
    String info_Text, info_detail_text;
    ProgressDialog pd;
    static String loc;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
       geocoder = new Geocoder(getActivity(),Locale.getDefault());
        pd=new ProgressDialog(getActivity());
        d = new Dialog(getActivity());
        con=getActivity();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        try {
            if (atvPlaces != null) {
                atvPlaces = (AutoCompleteTextView) view.findViewById(R.id.atv_places);
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
            }
        }catch (Exception e) {

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
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        Fragment f = getFragmentManager().findFragmentById(R.id.map1);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
        AddLocation.Longitude = null;
        super.onPause();
        //map is null when fragment is not visible
        mMap = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {

            mMap = fragment.getMap();
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMapLongClickListener(my2_OnMapLongClickListener);
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(final Marker marker) {
                    // Getting view from the layout file info_window_layout
                    View v = getActivity().getLayoutInflater().inflate(R.layout.info_window_layout, null);
                    //Getting references of all views
                    TextView heading = (TextView) v.findViewById(R.id.place_info);
                    TextView allData = (TextView) v.findViewById(R.id.detail_place_info);
                    //data from marker
                    LatLng point = marker.getPosition();
                    AddLocation.Lattitude = String.valueOf(point.latitude);
                    AddLocation.Longitude = String.valueOf(point.longitude);
                    AddLocation.Name = getLocation(point);
                    //add value to info window
                    heading.setText(info_Text);
                    allData.setText(info_detail_text);
                    return v;
                }
            });

            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                public void onInfoWindowClick(Marker marker) {

                    ((LocationActivity) getActivity()).setCurrentItem(1, true);
                }
            });

            if (AddLocation.Longitude != null) {
                    LatLng Location = new LatLng(Double.valueOf(AddLocation.Lattitude),
                            Double.valueOf(AddLocation.Longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Location, 17));
                    mMap.addMarker(new MarkerOptions().position(Location).title(
                            AddLocation.Name));
                }
        }
    }

    OnMapLongClickListener my2_OnMapLongClickListener = new OnMapLongClickListener() {

        public void onMapLongClick(final LatLng point) {
           Log.d("MSG", "On map long click");
            if(mMap!=null){
                mMap.clear();
                MarkerOptions marOpt = new MarkerOptions();
                marOpt.position(point);
                mMap.addMarker(marOpt);
            }

            String Address;

            Address = getAddress1(point.latitude, point.longitude);
            if (Address == null) {
                Address = "Location";
            }

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
            String state = addresses.get(0).getAddressLine(2);
            info_detail_text = address + " ," + city + " ," + state;
            String s1 = addresses.get(0).getSubLocality();
            String s2 = addresses.get(0).getLocality();
            System.out.println(addresses);
            if (s1 == null) {
                res = s2 + "\n";
                info_Text = res;
            } else {
                res = s1 + " " + s2;
                info_Text = res;
            }
        }
        catch (Exception e)
        {
//            ExceptionMessage.exceptionLog(getActivity(), this
//                    .getClass().toString() + " " + "[getLocation()]", e.toString());
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

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        try {
            //drag marker to desire place
            //invisible default marker
            LatLng dragPosition = marker.getPosition();
            dragLat = dragPosition.latitude;
            dragLng = dragPosition.longitude;
            dragPlace = getLocation(dragPosition);
            getLatLng = new GetLatLong();
            marker.setVisible(false);
            getLatLng.execute(dragPlace);
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onMarkerDragEnd()]", e.toString());
        }
    }


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
                }
                else {
                    latlng = "No";
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
            super.onPostExecute(result);
            // Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            try {
                if (!result.equals("No")) {
                    StringTokenizer a1 = new StringTokenizer(result, ":");
                    if (a1.hasMoreTokens()) {
                        lng = Double.valueOf(a1.nextToken());
                        lat = Double.valueOf(a1.nextToken());
                        LocationHere = new LatLng(lat, lng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LocationHere, 15));
                        currPlace = getLocation(LocationHere);
                        System.out.println("Current palce" + currPlace);
                        System.out.println("latitude=" + dragLat);
                        System.out.println("longitude=" + dragLng);
                        //  mMap.setMyLocationEnabled(true);
                        marker = mMap.addMarker(new MarkerOptions().position(LocationHere).title("Place Name")
                                .snippet(currPlace).draggable(true));
                        System.out.println(marker.isVisible());
                        marker.setVisible(true);
                    }
                }
                else
                {
                    loc="no";
                    atvPlaces.setError("Invalid place name.click here ");
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("please wait");
            pd.show();
        }

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
            if(pd!=null)
                pd.dismiss();
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
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait");
            pd.show();
        }

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
            if(pd!=null)
                pd.dismiss();
        }
    }

    public void getSearchData(String loc) {
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


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            LocationActivity.pos=3;
            //some times search icon was invisible(contol is moving between AddLocation & MapFragment)
            //below code is a solution
            LocationActivity.search1.setVisibility(View.VISIBLE);
            LocationActivity.autoPlace.setVisibility(View.VISIBLE);
            LocationActivity.image.setVisibility(View.GONE);
            LocationActivity.loca.setVisibility(View.GONE);
        }
    }


}
