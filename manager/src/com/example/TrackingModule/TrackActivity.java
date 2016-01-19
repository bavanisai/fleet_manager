package com.example.TrackingModule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.AdvanceModule.AddAdvanceFragment;
import com.example.AdvanceModule.AdvanceCleanerFragment;
import com.example.AdvanceModule.AdvanceDriverFragment;
import com.example.AdvanceModule.AdvanceVehicleFragment;
import com.example.FuelModule.FuelEntryFragment;
import com.example.FuelModule.FuelEntryListFragment;
import com.example.TabView.SlidingTabLayout;

import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;

import java.util.Locale;

public class TrackActivity extends AppCompatActivity {

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
    SearchView sv;
    MenuItem item;
    ViewPager mViewPager = null;
    int check = 0;
    Bundle bundle = new Bundle();
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"VEHICLE LIST","TRIP DETAILS","MAP"};
    int Numboftabs =3;
    Toolbar toolbar;
    static int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("TRACKING");
        setSupportActionBar(toolbar);

        //CODE FOR TAB WITH SWIPE VIEW
        adapter =  new ViewPageAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);

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
        tabs.setViewPager(mViewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
                "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        item = menu.findItem(R.id.action_search);

        sv = new SearchView((TrackActivity.this).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM | MenuItemCompat.SHOW_AS_ACTION_NEVER);
        MenuItemCompat.setActionView(item, sv);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
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
        int count=0;
        //This method return the fragment for the every position in the View Pager
        @Override

        public Fragment getItem(int position) {

            System.out.println("pos : "+position+", count : "+count );
            count++;
           check=position;
            switch (position)
            {
                case 0: VehicleListFragment tab1 = new VehicleListFragment();
                    return tab1;
                case 1: TripDetailsFragment tab2 = new TripDetailsFragment();
                    return tab2;
                case 2:TripMapFragment tab3=new TripMapFragment();
                    return tab3;
            }
            return  null;
        }

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



    public void setCurrentItem(int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
    }

    public void saveData(int id, Bundle data) {
        // based on the id you'll know which fragment is trying to save data(see
        // below)
        // the Bundle will hold the data
        if (id == 1) {
            bundle = data;
        }
    }

    public Bundle getSavedData() {

        return bundle;
        // here you'll save the data previously retrieved from the fragments and
        // return it in a Bundle
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
}
