package com.example.tripmodule;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.StringTokenizer;

import com.example.TabView.SlidingTabLayout;

import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;

public class TripActivity extends ActionBarActivity {

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

    CharSequence at = "@";
    String TripVoucherId = null;
    int check = 0;
    Toolbar toolbar;
    ViewPager pager;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    static int pos;

    CharSequence Titles[] = {"MANAGE TRIP","TRIP LIST"};
    int Numboftabs = 2;

    // public static String gcmVehicleNo=null;
    public static boolean isRunningActivity2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        isRunningActivity2 = true;

        // Message From Notification
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String Message = extras.getString("Message");
            if (Message.contains(at)) {
                StringTokenizer Trip = new StringTokenizer(Message, "@");
                // String TripMessage = Trip.nextToken();
                TripVoucherId = Trip.nextToken();
            }

            // gcmVehicleNo=Message.substring(Message.lastIndexOf("@")+1);
        }
        // End

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("TRIP");
        setSupportActionBar(toolbar);

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

    @Override
    public void onStart() {
        super.onStart();
        isRunningActivity2 = true;

    }

    @Override
    public void onStop() {
        super.onStop();
        isRunningActivity2 = false;

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
                case 0:TripManageFragment tab1 = new TripManageFragment();
                    return tab1;
                case 1:TripListFragment tab2 = new TripListFragment();
                    return tab2;
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

        switch (pos)
        {
            case 1:finish();
                break;
            case 2: setCurrentItem(0, true);
                break;
        }
    }
    }


    // /**
    // * A placeholder fragment containing a simple view.
    // */
    // public static class PlaceholderFragment extends Fragment {
    // /**
    // * The fragment argument representing the section number for this
    // * fragment.
    // */
    // private static final String ARG_SECTION_NUMBER = "section_number";
    //
    // /**
    // * Returns a new instance of this fragment for the given section number.
    // */
    // public static PlaceholderFragment newInstance(int sectionNumber) {
    // PlaceholderFragment fragment = new PlaceholderFragment();
    // Bundle args = new Bundle();
    // args.putInt(ARG_SECTION_NUMBER, sectionNumber);
    // fragment.setArguments(args);
    // return fragment;
    // }
    //
    // public PlaceholderFragment() {
    // }
    //
    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // View rootView = inflater.inflate(R.layout.fragment_trip, container,
    // false);
    // TextView textView = (TextView) rootView
    // .findViewById(R.id.section_label);
    // textView.setText(Integer.toString(getArguments().getInt(
    // ARG_SECTION_NUMBER)));
    // return rootView;
    // }
    // }

