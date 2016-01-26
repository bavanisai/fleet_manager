package com.example.ManageResources;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.FuelModule.FuelEntryFragment;
import com.example.FuelModule.FuelEntryListFragment;
import com.example.PaymentModule.*;
import com.example.PaymentModule.CleanerList;
import com.example.TabView.SlidingTabLayout;
import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.Welcome;

public class MainActivity extends AppCompatActivity implements
        ActionBar.OnNavigationListener {
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

    Bundle bundle = new Bundle();
    private AddVehicle addVehicleFragment;
    DBAdapter db = new DBAdapter(this);
    int check = 0;
    ViewPager pager;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"VEHICLE LIST", "ADD/UPDATE VEHICLE"};
    int Numboftabs = 2;
    Toolbar toolbar;
    Spinner spinner;
    static int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("");
        spinner=(Spinner)findViewById(R.id.spinner);
        setSupportActionBar(toolbar);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(toolbar.getContext(), R.array.ManageResource1, R.layout.support_simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        Intent in1 = new Intent(MainActivity.this, DriverEntryActivity.class);
                        startActivity(in1);
                        finish();
                        break;
                    case 2:
                        Intent in2 = new Intent(MainActivity.this, CleanerEntryActivity.class);
                        startActivity(in2);
                        finish();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

    public void setCurrentItem(int item, boolean smoothScroll)
    {
        pager.setCurrentItem(item, smoothScroll);
    }

    public void refresh(int frgmentNumber) {

        switch (frgmentNumber) {
            case 1:
                addVehicleFragment.refreshActivity();
                break;
        }
        // mViewPager.setCurrentItem(item, smoothScroll);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileEdit.class);
                startActivity(intent);
                return true;
            case R.id.action_help:
                String adress = new IpAddress().getIpAddress();
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse(adress));
                startActivity(viewIntent);
                return true;
            case R.id.action_logout:
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
            case R.id.action_about_us:
                Intent aboutus = new Intent(this, AboutUs.class );
                startActivity(aboutus);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Bundle getSavedData() {

        return bundle;
        // here you'll save the data previously retrieved from the fragments and
        // return it in a Bundle
    }

    public void saveData(int id, Bundle data) {
        // based on the id you'll know which fragment is trying to save data(see
        // below)
        // the Bundle will hold the data
        if (id == 1) {
            bundle = data;
        }

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
                case 0: VehicleList tab1 = new VehicleList();
                    return tab1;
                case 1:AddVehicle tab2=new AddVehicle();
                    return tab2;
            }
            return  null;
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
    public boolean onNavigationItemSelected(int arg0, long arg1) {
        switch (arg0) {

            case 1:
                Intent intent1 = new Intent(getApplicationContext(),
                        DriverEntryActivity.class);
                startActivity(intent1);
                finish();
                break;
            case 2:
                Intent intent2 = new Intent(getApplicationContext(),
                        CleanerEntryActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
        return false;
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

        }

    }

}
