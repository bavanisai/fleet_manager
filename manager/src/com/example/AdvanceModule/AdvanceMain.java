/*
 * Purpose - AdvanceMain implements viewPager holding AddAdvance,VehicleAdvance,DriverAdvance and CleanerAdvance. 
 * @author - Pravitha 
 * Created on May 26, 2014
 * Modified on June 10, 2014
 */
package com.example.AdvanceModule;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.TabView.SlidingTabLayout;

import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;

public class AdvanceMain extends AppCompatActivity {

    ViewPager mViewPager;
    int check=0;
    ViewPageAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"ADD ADVANCE","VEHICLE ADVANCE","DRIVER ADVANCE","CLEANER ADVANCE"};
    int Numboftabs =4;
    Toolbar toolbar;
    static int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("ADVANCE");
        setSupportActionBar(toolbar);


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

    public void setCurrentItem(int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
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
    public void onBackPressed()
    {
       // super.onBackPressed();
        AddAdvanceFragment.sAdvanceType=null;
        AddAdvanceFragment.sVehicleType=null;
        AddAdvanceFragment.sDriverType=null;
        AddAdvanceFragment.sCleanerType=null;
        AddAdvanceFragment.sVoucherNo=null;
        AddAdvanceFragment.sAmount=null;
        AddAdvanceFragment.restore=null;

        switch (pos)
        {
            case 1:
                finish();
                break;

            case 2:
                setCurrentItem(0, true);
                break;
            case 3:
                setCurrentItem(0, true);
                break;
            case 4:
                setCurrentItem(0, true);
                break;



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
                case 0: AddAdvanceFragment tab1 = new AddAdvanceFragment();
                    return tab1;
                case 1: AdvanceVehicleFragment tab2 = new AdvanceVehicleFragment() ;
                    return tab2;
                case 2:AdvanceDriverFragment tab3=new AdvanceDriverFragment();
                    return tab3;
                case 3: AdvanceCleanerFragment tab4 = new AdvanceCleanerFragment();
                    return tab4;
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
}
