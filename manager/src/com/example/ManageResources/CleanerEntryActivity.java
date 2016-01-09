package com.example.ManageResources;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.TabView.SlidingTabLayout;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;

public class CleanerEntryActivity extends AppCompatActivity implements
        OnNavigationListener {
    int check = 0;
    ViewPager pager;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"CLEANER LIST", "ADD/UPDATE CLEANER"};
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

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(toolbar.getContext(), R.array.ManageResource3, R.layout.support_simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        Intent in1 = new Intent(CleanerEntryActivity.this,MainActivity.class);
                        startActivity(in1);
                        finish();
                        break;
                    case 2:
                        Intent in2 = new Intent(CleanerEntryActivity.this, DriverEntryActivity.class);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public void setCurrentItem(int item, boolean smoothScroll) {
        pager.setCurrentItem(item, smoothScroll);
    }

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
            if (position == 0) // if the position is 0 we are returning the First tab
            {
                CleanerList tab1 = new CleanerList();
                return tab1;
            } else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                AddCleaner tab2 = new AddCleaner();
                return tab2;
            }


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

        }

    }

    @Override
    public boolean onNavigationItemSelected(int arg0, long arg1) {
        switch (arg0) {
            case 0:
                Intent intent0 = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(intent0);
                finish();
                break;
            case 1:
                Intent intent1 = new Intent(getApplicationContext(),
                        DriverEntryActivity.class);
                startActivity(intent1);
                finish();
                break;
            // case 2:
            // Intent intent2 = new Intent(getApplicationContext(),
            // CleanerEntryActivity.class);
            // startActivity(intent2);
            // break;
        }
        return false;
    }

}
