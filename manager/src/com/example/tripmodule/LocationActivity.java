package com.example.tripmodule;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TabView.SlidingTabLayout;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.Welcome;

import org.json.JSONObject;

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

public class LocationActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */


    /**
     * The {@link ViewPager} that will host the section contents.
     */
  //  ViewPager mViewPager;
    int check = 0;
    Toolbar toolbar;
    ViewPager pager;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    ImageView img,sea,sea1;
    TextView txtLocation;
    PlacesTask placesTask;
    ParserTask parserTask;
    AutoCompleteTextView atvPlaces;
    static int pos;
    static ImageView search, search1, image;
    static AutoCompleteTextView autoPlace;
    static TextView loca;
    CharSequence Titles[] = {"LOCATION LIST", "ADD LOCATION","MAP"};
    int Numboftabs = 3;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        toolbar = (Toolbar) findViewById(R.id.tool);
//        toolbar.setNavigationIcon(R.drawable.back_arrow);
//        toolbar.setTitle("LOCATION");
 //       setSupportActionBar(toolbar);
        pd=new ProgressDialog(this);
        txtLocation=(TextView)findViewById(R.id.txtLocation);
        atvPlaces=(AutoCompleteTextView)findViewById(R.id.atv_places);
        sea=(ImageView)findViewById(R.id.imgVSearch);
        sea1=(ImageView)findViewById(R.id.imgVSearch1);
        img=(ImageView)findViewById(R.id.arrow_img);
        search = sea;
        search1 = sea1;
        autoPlace = atvPlaces;
        image = img;
        loca = txtLocation;
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationActivity.this,TripActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sea.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               txtLocation.setVisibility(View.GONE);
                atvPlaces.setVisibility(View.VISIBLE);
               // edtLocatin.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
                sea.setVisibility(View.GONE);
                sea1.setVisibility(View.VISIBLE);
            }
        });

        sea1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (atvPlaces.getText().length() > 1
                            && isConnectingToInternet()) {
                        MapFragment m = new MapFragment();
                        String loc=atvPlaces.getText().toString();
                        atvPlaces.setText("");
                        try {
                            ((LocationActivity.this)).setCurrentItem(2, true);
                            }
                        catch(Exception e)
                        {
                            ExceptionMessage.exceptionLog(LocationActivity.this, this
                                    .getClass().toString(), e.toString());
                            e.toString();
                        }
                        m.getSearchData(loc);
                    } else {
                        Toast.makeText(
                                LocationActivity.this,
                                "Please Enter Valid Location , Please  Check Internet Connection",
                                Toast.LENGTH_LONG).show();
                    }
                }

                catch (Exception e) {
                    ExceptionMessage.exceptionLog(LocationActivity.this, this
                            .getClass().toString(), e.toString());
                }
            }
        });

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

                    ExceptionMessage.exceptionLog(LocationActivity.this, this
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

        //CODE FOR TAB WITH SWIPE VIEW
        adapter = new ViewPageAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }
    public void setCurrentItem(int item, boolean smoothScroll) {
       pager.setCurrentItem(item, smoothScroll);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ViewPageAdapter extends FragmentStatePagerAdapter {

        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


        // Build a Constructor and assign the passed Values to appropriate values in the class
        public ViewPageAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
            super(fm);

            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;

        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            check=position;
            switch (position)
            {
                case 0:LocationList tab1 = new LocationList();
                        img.setVisibility(View.VISIBLE);
                        txtLocation.setVisibility(View.VISIBLE);
                        atvPlaces.setVisibility(View.GONE);
                        sea.setVisibility(View.GONE);
                        sea1.setVisibility(View.GONE);
                    return tab1;

                case 1:AddLocation tab2 = new AddLocation();
                        img.setVisibility(View.VISIBLE);
                        txtLocation.setVisibility(View.VISIBLE);
                        atvPlaces.setVisibility(View.GONE);
                        sea.setVisibility(View.GONE);
                        sea1.setVisibility(View.GONE);
                    return tab2;

                case 2:MapFragment tab3=new MapFragment();
                    sea1.setVisibility(View.VISIBLE);

                    return tab3;
            }
            return null;
        }

        // This method return the titles for the Tabs in the Tab Strip

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip

        @Override
        public int getCount() {
            return NumbOfTabs;
        }
    }




    @Override
    public void onBackPressed() {

        switch (pos) {

            case 1:
                finish();
                break;

            case 2:

                setCurrentItem(0, true);
                break;

            case 3:

                setCurrentItem(0, true);
                break;
        }
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd.setMessage("Please wait");
//            pd.show();

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
                ExceptionMessage.exceptionLog(LocationActivity.this, this.getClass()
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
                ExceptionMessage.exceptionLog(LocationActivity.this, this.getClass()
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
            ExceptionMessage.exceptionLog(LocationActivity.this, this.getClass()
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
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait");
            pd.show();
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser(LocationActivity.this);

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                ExceptionMessage.exceptionLog(LocationActivity.this, this.getClass()
                        .toString(), e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(LocationActivity.this, result,
                    android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
            if(pd!=null)
                pd.dismiss();
        }
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) this
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


}
