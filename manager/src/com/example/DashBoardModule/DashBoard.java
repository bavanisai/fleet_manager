/*
 * Purpose - DashBoard implements Pie chart and Bar graph
 * @author - Pravitha 
 * Created on May 23, 2014
 * Modified on May 23, 2014
 */
package com.example.DashBoardModule;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;

public class DashBoard extends AppCompatActivity {

    ImageView driverImg, vehicleImg;
    TextView driverTv, vehicleTv;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setTitle("DASHBOARD");
        toolbar.setNavigationIcon(R.drawable.gray_back_arrow);
        setSupportActionBar(toolbar);
        BindData();

		/*
         * Purpose - Set the Driver data Event - OnTouchListener Parameters - No
		 * parameters Return Type - No Return Type
		 */
        driverImg.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("Driver", "Driver");
                    DragShadowBuilder shadowBuilder = new DragShadowBuilder(
                            v);

                    v.startDrag(data, shadowBuilder, v, 0);
                    vehicleImg.setVisibility(View.INVISIBLE);
                    vehicleTv.setVisibility(View.INVISIBLE);

                    return true;
                } else {
                    return false;
                }
            }

        });

		/*
		 * Purpose - Set the Driver data Event - OnTouchListener Parameters - No
		 * parameters Return Type - No Return Type
		 */

        vehicleImg.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    ClipData data = ClipData.newPlainText("Vehicle", "Vehicle");
                    DragShadowBuilder shadowBuilder = new DragShadowBuilder(
                            v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    driverImg.setVisibility(View.INVISIBLE);
                    driverTv.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }

        });

		/*
		 * Purpose - Opens Pie chart activity based on Driver or Vehicle Event -
		 * OnDragListener Parameters - No parameters Return Type - No Return
		 * Type
		 */

        findViewById(R.id.dashBoardLinearLayoutRightTop).setOnDragListener(
                new OnDragListener() {

                    @Override
                    public boolean onDrag(View v, DragEvent event) {

                        switch (event.getAction()) {

                            case DragEvent.ACTION_DRAG_STARTED:
                                break;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                break;

                            case DragEvent.ACTION_DROP:
                                if (event.getClipDescription().getLabel()
                                        .equals("Driver")) {
                                    vehicleImg.setVisibility(View.VISIBLE);
                                    vehicleTv.setVisibility(View.VISIBLE);
                                    Intent driverDistancePiechartIntent = new Intent(
                                            getApplicationContext(),
                                            DriverDistancePiechart.class);

                                    startActivity(driverDistancePiechartIntent);


                                } else if (event.getClipDescription().getLabel()
                                        .equals("Vehicle")) {
                                    driverImg.setVisibility(View.VISIBLE);
                                    driverTv.setVisibility(View.VISIBLE);
                                    Intent vehicleDistancePiechartIntent = new Intent(
                                            getApplicationContext(),
                                            VehicleDistancePiechart.class);
                                    startActivity(vehicleDistancePiechartIntent);
                                }

                                break;

                            default:
                                break;
                        }
                        return true;
                    }

                });

		/*
		 * Purpose - Opens Bar graph activity based on Driver or Vehicle Event -
		 * OnDragListener Parameters - No parameters Return Type - No Return
		 * Type
		 */

        findViewById(R.id.dashBoardLinearLayoutRightButtom).setOnDragListener(
                new OnDragListener() {

                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        switch (event.getAction()) {
                            case DragEvent.ACTION_DROP:

                                if (event.getClipDescription().getLabel()
                                        .equals("Driver")) {
                                    vehicleImg.setVisibility(View.VISIBLE);
                                    vehicleTv.setVisibility(View.VISIBLE);

                                    Intent intent = new Intent(
                                            getApplicationContext(),
                                            DashBoardDriverChart.class);
                                    startActivity(intent);
                                } else if (event.getClipDescription().getLabel()
                                        .equals("Vehicle")) {
                                    driverImg.setVisibility(View.VISIBLE);
                                    driverTv.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(
                                            getApplicationContext(),
                                            DashBoardVehicleChart.class);
                                    startActivity(intent);
                                }
                                break;

                            default:
                                break;
                        }
                        return true;
                    }

                });

		/*
		 * Purpose - Disables the driver image or Vehicle image based on the
		 * value Event - onDragListener Parameters - No parameters Return Type -
		 * No Return Type
		 */

        findViewById(R.id.dashBoardLinearLayoutUnWantedArea).setOnDragListener(
                new OnDragListener() {

                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        switch (event.getAction()) {
                            case DragEvent.ACTION_DRAG_STARTED:
                                break;
                            case DragEvent.ACTION_DROP:

                                if (event.getClipDescription().getLabel()
                                        .equals("Driver")) {
                                    vehicleImg.setVisibility(View.VISIBLE);
                                    vehicleTv.setVisibility(View.VISIBLE);
                                } else if (event.getClipDescription().getLabel()
                                        .equals("Vehicle")) {
                                    driverImg.setVisibility(View.VISIBLE);
                                    driverTv.setVisibility(View.VISIBLE);
                                }
                                break;
                            case DragEvent.ACTION_DRAG_EXITED:

                                break;
                            default:
                                break;
                        }

                        return true;

                    }
                });

    }

	/*
	 * Purpose - Binds XMl Id reference to java Method Name - BindData()
	 * Parameters - No parameters Return Type - No Return Type
	 */

    private void BindData() {
        driverImg = (ImageView) findViewById(R.id.dashBoardImgVdriverImage);
        vehicleImg = (ImageView) findViewById(R.id.dashBoardImgVVehicleImage);
        driverTv = (TextView) findViewById(R.id.driverTv);
        vehicleTv = (TextView) findViewById(R.id.vehicleTv);

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


}
