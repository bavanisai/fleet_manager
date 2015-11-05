/*
 * Purpose - VehicleDistancePiechart implements Vehicle distance details in Pie chart
 * @author - Pravitha 
 * Created on May 22, 2014
 * Modified on May 22, 2014
 */

package com.example.DashBoardModule;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AbsenceReport.DatePickerFragment;
import com.example.Interface.IVehicleDistancePieChart;
import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class VehicleDistancePiechart extends ActionBarActivity implements
        OnClickListener, IVehicleDistancePieChart {

    String mdateType, mfrom_date, mto_date, currentTime, jdriver, jdistance;
    private static int sKey = 0;
    MatrixCursor vehicleDistanceCursor;
    TextView vehicleDistancePieChartTvTotalDistance, vehicleDistancePieChartTv;
    double mTotalKm = 0.0;
    String str;
    private GraphicalView mChartView = null;
    Button vehicleDistancePieChartBtnFromDate,
            vehicleDistancePieChartBtnToDate;
    LinearLayout layout;
    final IVehicleDistancePieChart mVehicleDistancePieChart = this;
    ImageView vehicleDistancePieChartRefresh;
    String adress = new IpAddress().getIpAddress();
    String fDate, tDate;
    Toolbar toolbar;
    Font head_main = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    int page;
    CustomAlertDialog ald;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_distance_piechart);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("DASHBOARD");
        setSupportActionBar(toolbar);
        ald=new CustomAlertDialog();
        ImageView img = (ImageView) toolbar.findViewById(R.id.vehicleDistancePieChartPdfReport1);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vehicleDistancePieChartBtnFromDate.getText().toString().equals("FROM DATE")
                                && vehicleDistancePieChartBtnToDate.getText().toString().equals("TO DATE"))
                {
                    ald.alertDialog(VehicleDistancePiechart.this,"Please select from date and to date !");
                }
                else if(vehicleDistancePieChartBtnToDate.getText().toString().equals("TO DATE"))
                {
                    ald.alertDialog(VehicleDistancePiechart.this, "Please select To date !");
                }
                else
                {
                    try {

                        if (str != null && str.contains("OK")) {
                            createPDF(str);
                            Toast toast = Toast.makeText(VehicleDistancePiechart.this, "Report generating..", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

                            File file = new File(path, "Vehicle Distance Report.pdf");

                            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                            startActivity(intent);
                        } else {
                            ald.alertDialog(VehicleDistancePiechart.this, "Record not found from " + fDate + " to " + tDate + " !");
                        }

                    } catch (Exception e) {
                        Toast.makeText(VehicleDistancePiechart.this,
                                "Try after sometime...", Toast.LENGTH_SHORT).show();
                        ExceptionMessage.exceptionLog(getApplicationContext(), this
                                .getClass().toString()
                                + " "
                                + "[calenderAlertDialog]", e.toString());

                    }
                }
            }
        });

        bindData();
        currentTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        vehicleDistancePieChartRefresh.setVisibility(View.INVISIBLE);
        layout.removeAllViews();
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.selectdate);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 100;
        imageView.setLayoutParams(layoutParams);
        layout.addView(imageView);
        vehicleDistancePieChartTv.setVisibility(View.INVISIBLE);
        vehicleDistancePieChartBtnFromDate.setOnClickListener(this);
        vehicleDistancePieChartBtnToDate.setOnClickListener(this);
        vehicleDistancePieChartRefresh.setOnClickListener(this);

    }

    /*
     * Purpose - Binds XMl Id reference to java Method Name - bindData()
     * Parameters - No parameters Return Type - No Return Type
     */
    private void bindData() {
        vehicleDistancePieChartRefresh = (ImageView) findViewById(R.id.vehicleDistancePieChartRefresh);
        vehicleDistancePieChartTv = (TextView) findViewById(R.id.vehicleDistancePieChartTv);
        vehicleDistancePieChartTvTotalDistance = (TextView) findViewById(R.id.vehicleDistancePieChartTvTotalDistance);
        vehicleDistancePieChartBtnFromDate = (Button) findViewById(R.id.vehicleDistancePieChartBtnFromDate);
        vehicleDistancePieChartBtnToDate = (Button) findViewById(R.id.vehicleDistancePieChartBtnToDate);
        layout = (LinearLayout) findViewById(R.id.vehicleDistancePieChartContent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.vehicleDistancePieChartBtnFromDate:
                vehicleDistancePieChartBtnToDate.setText("TO DATE");
                mdateType = "from";
                calenderAlertDialog(mdateType);
                break;

            case R.id.vehicleDistancePieChartBtnToDate:
                if (vehicleDistancePieChartBtnFromDate.getText().toString() != ""
                        && vehicleDistancePieChartBtnFromDate.getText().toString() != null
                        && !vehicleDistancePieChartBtnFromDate.getText().toString()
                        .contains("FROM DATE")) {
                    mdateType = "to";
                    calenderAlertDialog(mdateType);
                } else
                {
                    ald.alertDialog(VehicleDistancePiechart.this,"Please select from date !");
                }
                break;
            case R.id.vehicleDistancePieChartRefresh:
                mChartView.zoomReset();
                break;

            default:
                break;

        }

    }

	/*
     * Purpose - DatePicker to set From date and To date Method Name -
	 * calenderAlertDialog Parameters - dateType Return Type - No Return Type
	 */

    public void calenderAlertDialog(String dateType) {
        if (dateType == "from") {
            // AlertDialog.Builder alertDialog = new AlertDialog.Builder(
            // VehicleDistancePiechart.this);
            //
            // // Setting Dialog Title
            // alertDialog.setTitle("Select FromDate");
            // final DatePicker vehicleDistancePieChartFromDatePicker = new
            // DatePicker(
            // VehicleDistancePiechart.this);
            //
            // final TextView vehicleDistancePieChartFromDate = new TextView(
            // getApplicationContext());
            // vehicleDistancePieChartFromDate.setText("FROM DATE");
            // vehicleDistancePieChartFromDate.setTextColor(Color.BLACK);
            // vehicleDistancePieChartFromDate.setTextSize(20);
            // LinearLayout vehicleDistancePieChartLinearLayout = new
            // LinearLayout(
            // this);
            // vehicleDistancePieChartLinearLayout
            // .setOrientation(LinearLayout.VERTICAL);
            //
            // vehicleDistancePieChartLinearLayout
            // .addView(vehicleDistancePieChartFromDate);
            // vehicleDistancePieChartLinearLayout
            // .addView(vehicleDistancePieChartFromDatePicker);
            // alertDialog.setView(vehicleDistancePieChartLinearLayout);
            // alertDialog.setPositiveButton("YES",
            // new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int which) {
            //
            // int fromday = vehicleDistancePieChartFromDatePicker
            // .getDayOfMonth();
            // int frommonth = vehicleDistancePieChartFromDatePicker
            // .getMonth() + 1;
            // int fromyear = vehicleDistancePieChartFromDatePicker
            // .getYear();
            // mfrom_date = "" + fromyear + "-" + "" + frommonth
            // + "-" + "" + fromday;
            //
            // vehicleDistancePieChartBtnFromDate
            // .setText(mfrom_date);
            //
            // }
            // });
            // alertDialog.setNegativeButton("NO",
            // new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int which) {
            // dialog.cancel();
            // }
            // });
            //
            // alertDialog.show();

            DatePickerFragment date = new DatePickerFragment();
            /**
             * Set Up Current Date Into dialog
             */
            Calendar calender = Calendar.getInstance();
            Bundle args = new Bundle();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
            date.setArguments(args);
            /**
             * Set Call back to capture selected date
             */
            date.setCallBack(ondate);
            date.show(getSupportFragmentManager(), "Date Picker");
        } else if (dateType == "to") {
            // AlertDialog.Builder alertDialog = new AlertDialog.Builder(
            // VehicleDistancePiechart.this);
            //
            // // Setting Dialog Title
            // alertDialog.setTitle("Select ToDate");
            // final DatePicker vehicleDistancePieChartToDatePicker = new
            // DatePicker(
            // VehicleDistancePiechart.this);
            // final TextView ToDa = new TextView(getApplicationContext());
            // ToDa.setText("TO DATE");
            // ToDa.setTextColor(Color.BLACK);
            // ToDa.setTextSize(20);
            // LinearLayout ll = new LinearLayout(this);
            // ll.setOrientation(LinearLayout.VERTICAL);
            // ll.addView(ToDa);
            // ll.addView(vehicleDistancePieChartToDatePicker);
            //
            // alertDialog.setView(ll);
            //
            // alertDialog.setPositiveButton("YES",
            // new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int which) {
            //
            // int today = vehicleDistancePieChartToDatePicker
            // .getDayOfMonth();
            // int tomonth = vehicleDistancePieChartToDatePicker
            // .getMonth() + 1;
            // int toyear = vehicleDistancePieChartToDatePicker
            // .getYear();
            //
            // mto_date = "" + toyear + "-" + "" + tomonth + "-"
            // + "" + today;
            //
            // vehicleDistancePieChartBtnToDate.setText(mto_date);
            // dialog.cancel();
            //
            // SendToWebService send = new SendToWebService(
            // VehicleDistancePiechart.this,
            // mVehicleDistancePieChart);
            // // if (send.isConnectingToInternet()) {
            // try {
            // send.execute("20",
            // "DistanceTravelledByVehicle",
            // mfrom_date, mto_date);
            // } catch (Exception e) {
            // Toast.makeText(VehicleDistancePiechart.this,
            // "Try after sometime...",
            // Toast.LENGTH_SHORT).show();
            // ExceptionMessage.exceptionLog(
            // getApplicationContext(), this
            // .getClass().toString()
            // + " "
            // + "[calenderAlertDialog]", e
            // .toString());
            // }
            // // } else {
            // // Toast.makeText(
            // // getApplicationContext(),
            // // "INTERNET CONNECTION ERROR!! PLEASE CHECK NETWORK",
            // // Toast.LENGTH_SHORT).show();
            // // }
            //
            // }
            // });
            //
            // alertDialog.setNegativeButton("NO",
            // new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int which) {
            // // Write your code here to execute after dialog
            // dialog.cancel();
            // }
            // });
            //
            // alertDialog.show();

            DatePickerFragment date = new DatePickerFragment();
            /**
             * Set Up Current Date Into dialog
             */
            Calendar calender = Calendar.getInstance();
            Bundle args = new Bundle();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
            date.setArguments(args);
            /**
             * Set Call back to capture selected date
             */
            date.setCallBack(ondate);
            date.show(getSupportFragmentManager(), "Date Picker");

        }
    }

    OnDateSetListener ondate = new OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            if (mdateType == "from") {

                fDate = String.valueOf(year) + "-"
                        + String.valueOf(monthOfYear + 1) + "-"
                        + String.valueOf(dayOfMonth);
               String status= fromDateValidation();
                if(status.equals("success")) {
                    vehicleDistancePieChartBtnFromDate.setText(String.valueOf(year)
                            + "-" + String.valueOf(monthOfYear + 1) + "-"
                            + String.valueOf(dayOfMonth));
                }
            } else if (mdateType == "to") {
                tDate = String.valueOf(year) + "-"
                        + String.valueOf(monthOfYear + 1) + "-"
                        + String.valueOf(dayOfMonth);
                String status=toDateValidation();
                if(status.equals("success")) {
                    vehicleDistancePieChartBtnToDate.setText(String.valueOf(year)
                            + "-" + String.valueOf(monthOfYear + 1) + "-"
                            + String.valueOf(dayOfMonth));
                }



            }

        }
    };

    /*
     * Purpose - Get the data from the server Method Name - VehicleDistance()
     * Parameters - mfrom_date and mto_date Return Type - No Return Type
     *
     * Variable list String response - To fetch the response from the server
     * double[] vehicleDistanceKM - Double Array which holds all the distance
     * traveled by the driver. String[] driverName - String array which holds
     * all the driver name. Int[] colors - Integer Array which holds the total
     * No of colors. Return Type - No Return Type
     */
    public void vehicleDistance(String mfrom_date, String mto_date) {
        try {

            if (vehicleDistanceCursor.getCount() == 0) {
                vehicleDistancePieChartRefresh.setVisibility(View.INVISIBLE);
                layout.removeAllViews();
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.nodata1);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.topMargin = 100;
                imageView.setLayoutParams(layoutParams);
                layout.addView(imageView);
                vehicleDistancePieChartTv.setVisibility(View.INVISIBLE);
                vehicleDistancePieChartTvTotalDistance.setText("");

            } else {
                vehicleDistancePieChartRefresh.setVisibility(View.VISIBLE);
                double[] vehicleDistanceKM = new double[vehicleDistanceCursor
                        .getCount()];
                CategorySeries vehicleSeries = new CategorySeries("pie");
                String[] vehicleNo = new String[vehicleDistanceCursor
                        .getCount()];
                int[] colors = new int[vehicleDistanceCursor.getCount()];

                if (vehicleDistanceCursor.moveToFirst()) {
                    for (int i = 0; i < vehicleDistanceCursor.getCount(); i++) {
                        vehicleNo[i] = vehicleDistanceCursor.getString(1);
                        vehicleDistanceKM[i] = vehicleDistanceCursor
                                .getDouble(2);
                        vehicleSeries.add(vehicleNo[i], vehicleDistanceKM[i]);
                        Random rnd = new Random();
                        colors[i] = Color.argb(255, rnd.nextInt(256),
                                rnd.nextInt(256), rnd.nextInt(256));
                        vehicleDistanceCursor.moveToNext();
                    }
                }
                vehicleDistanceCursor.close();

                DefaultRenderer renderer = new DefaultRenderer();

                for (int color : colors) {
                    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
                    r.setColor(color);
                    r.setDisplayBoundingPoints(true);
                    r.setDisplayChartValuesDistance(5);
                    r.setDisplayChartValues(true);
                    r.setChartValuesTextSize(15);
                    r.setHighlighted(true);
                    renderer.addSeriesRenderer(r);
                }
                renderer.isInScroll();
                renderer.setZoomButtonsVisible(false);
                renderer.setExternalZoomEnabled(true);
                renderer.setShowLabels(true);
                renderer.setLabelsTextSize(13);
                renderer.setLabelsColor(Color.BLACK);
                renderer.setLegendTextSize(25);
                renderer.setDisplayValues(true);
                renderer.setShowLegend(false);
                renderer.setScale(0.85f);

                mChartView = ChartFactory.getPieChartView(this, vehicleSeries,
                        renderer);
                layout.removeAllViews();
                layout.addView(mChartView);
                // vehicleDistancePieChartTv.setAlpha(1.0f);
                vehicleDistancePieChartTv.setVisibility(View.VISIBLE);
                vehicleDistancePieChartTvTotalDistance.setText(Double
                        .toString(mTotalKm));

            }
        } catch (Exception e) {

            Toast.makeText(VehicleDistancePiechart.this,
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[vehicleDistance]", e.toString());
        }

    }

	/*
	 * Purpose - Parse the data got from the server Method Name -
	 * VehicledistanceParser Parameters - response Return Type - No Return Type
	 * 
	 * Variable list String jsonData - To get the data with tag d JSONArray
	 * distance - JSon array is Stored String[] driverName - String array which
	 * holds all the driver name. Int[] colors - Integer Array which holds the
	 * total No of colors. Return Type - No Return Type
	 */

    public String vehicleDistanceParser(String response) {
        String statuschk = null;
        if (response != null)

            try {

                JSONObject jsonResponse = new JSONObject(response);
                String jsondata=jsonResponse.getString("d");
                JSONArray distance=new JSONArray(jsondata);
                JSONObject jsonResponse1 = distance.getJSONObject(0);
                statuschk = jsonResponse1.getString("status").trim();
                statuschk = jsonResponse1.getString("status").trim();

                // iterating the array
                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("data does not exist")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("OK")) {
                    String[] columnNames = {"_id", "Name", "Distance"};
                    vehicleDistanceCursor = new MatrixCursor(columnNames);
                    for (int i = 1; i < distance.length(); i++) {
                        JSONObject c = distance.getJSONObject(i);

                       // JSONObject Performance = distance.getJSONObject(i);
                        // if (!(Performance.isNull("VehicleNumber"))) {
                        String Name = c.getString("driverName");
                        // }
                        // if (!(Performance.isNull("Distance"))) {
                        String Distance = c.getString("distance");
                        // }
                        vehicleDistanceCursor.addRow(new Object[]{sKey, Name,
                                Distance});
                        sKey++;
                        mTotalKm = mTotalKm + Double.parseDouble(Distance);

                    }
                }

            } catch (JSONException e) {

                Toast.makeText(this, "Try after sometime...",
                        Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[vehicleDistanceParser]", e.toString());
            }

        return statuschk;
    }

    @Override
    public void onVehicleDistancePieChart(String response) {
        try {
            str = response;
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused")
                    || response.contains("timed out")) {
                ImageView image = new ImageView(this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
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

                ImageView image = new ImageView(this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
                String stat = vehicleDistanceParser(response);
                layout = (LinearLayout) findViewById(R.id.vehicleDistancePieChartContent);
                vehicleDistancePieChartTvTotalDistance = (TextView) findViewById(R.id.vehicleDistancePieChartTvTotalDistance);
                if (!stat.equals("data does not exist")) {
                    vehicleDistance(mfrom_date, mto_date);
                } else {
                    layout.removeAllViews();
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(R.drawable.alert_nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    layout.addView(imageView);

                    TextView textView=new TextView(this);
                    textView.setText("NO DATA");
                    textView.setTextSize(14);
                    textView.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.gravity = Gravity.CENTER;
                    layoutParams1.topMargin = 20;
                    textView.setLayoutParams(layoutParams1);
                    layout.addView(textView);
                    vehicleDistancePieChartTv.setVisibility(View.INVISIBLE);
                    vehicleDistancePieChartTvTotalDistance.setText("");
                }
            }
        } catch (Exception e) {
            Toast.makeText(VehicleDistancePiechart.this,
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onVehicleDistancePieChart]", e.toString());
        }
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
    class HeaderFooterPageEvent extends PdfPageEventHelper {
        int pageNumber;
        Chunk[] header = new Chunk[2];
        @Override
        public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
            super.onChapter(writer, document, paragraphPosition, title);
            pageNumber = 1;

        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            super.onOpenDocument(writer, document);
            header[0] = new Chunk("From : " + fDate, font1);
            header[1]= new Chunk("To      : " + tDate, font1);
        }

        public void onStartPage(PdfWriter writer, Document document) {
            pageNumber++;
            try {
                Rectangle rect = writer.getBoxSize("art");
                int a = writer.getPageNumber();
                switch (a / a) {
                    case 1:
                        Chunk headReport = new Chunk("Vehicle Distance Report", head_main);
                        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(headReport), 300, 780, 0);

                        document.add(new LineSeparator());
                        LineSeparator sep1 = new LineSeparator();
                        sep1.setOffset(0);
                        document.add(sep1);

                        ColumnText.showTextAligned(writer.getDirectContent(),
                                Element.ALIGN_LEFT, new Phrase(header[0]),
                                rect.getLeft(), rect.getTop() - 30, 0);
                        ColumnText.showTextAligned(writer.getDirectContent(),
                                Element.ALIGN_LEFT, new Phrase(header[1]),
                                rect.getLeft(), rect.getTop() - 50, 0);
                        document.add(Chunk.NEWLINE);

                        document.add(new LineSeparator());
                        LineSeparator sep2 = new LineSeparator();
                        sep2.setOffset(0);
                        document.add(sep2);
                        document.add(Chunk.NEWLINE);
                        break;
                }
            }
                catch(DocumentException e)
                {
                    ExceptionMessage.exceptionLog(VehicleDistancePiechart.this, this
                                    .getClass().toString() + " " + "[onStartPage()-DocumentException]",
                            e.toString());
                    e.printStackTrace();
                }
            }
        public void onEndPage(PdfWriter writer, Document document) {
            String currentTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Fleet Manager"+" / "+currentTime), 130, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()+" of "+page), 550, 30, 0);
        }

    }
    public void createPDF(String response) {
        Document doc = new Document(PageSize.A4,  20, 20, 120, 60);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "Vehicle Distance Report.pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter w = PdfWriter.getInstance(doc, fOut);
            Rectangle rect = new Rectangle(30, 30, 550, 780);
            w.setBoxSize("art", rect);

            //HeaderFooterPageEvent is a class for adding header n footer
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            w.setPageEvent(event);

            doc.open();
            doc.add(Chunk.NEWLINE);
            //pdf containing table with 2 column
            PdfPTable table = new PdfPTable(2);

            table.setSpacingBefore(20f);
            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);

            table.getDefaultCell().setBorderWidth(0);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Chunk vehicle = new Chunk("VEHICLE", font1);
            Chunk distance = new Chunk("DISTANCE", font1);

            PdfPCell cell = new PdfPCell(new Paragraph(vehicle));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(distance));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            try {
                //  String stat = driverDistanceParser(response);
                String statuschk = null;
                String jsonData = null;
                // Create the root JSONObject from the JSON string.
                JSONObject jsonResponse = new JSONObject(response);

                //Get the instance of JSONArray that contains JSONObjects
                jsonData = jsonResponse.getString("d");

                JSONArray fuelArray = new JSONArray(jsonData);
                if(fuelArray.length()/40>=0)
                {
                    page=fuelArray.length()/40;
                    page++;
                }

                JSONObject status1 = fuelArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(VehicleDistancePiechart.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("vehicle number does not exist")) {
                    ExceptionMessage.exceptionLog(VehicleDistancePiechart.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("data does not exist")) {
                    Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                } else if (statuschk.equals("OK")) {

                    for (int i = 1; i < fuelArray.length(); i++) {
                        JSONObject jsonObject = fuelArray.getJSONObject(i);
                        JSONArray names = jsonObject.names();

                        for (int k = 0; k < names.length(); k++) {
                            jdriver = jsonObject.optString("driverName").toString();
                            jdistance = jsonObject.optString("distance").toString();

                        }

                        table.addCell(jdriver);
                        table.addCell(jdistance);

                    }
                }

            } catch (JSONException e)
            {
                ExceptionMessage.exceptionLog(getApplicationContext(), this
                        .getClass().toString()
                        + " "
                        + "[createPdf()-JSONException]", e.toString());
                e.printStackTrace();
            }
            doc.add(table);

        } catch (DocumentException de)
        {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                    .getClass().toString()
                    + " "
                    + "[createPdf()-DocumentException]", de.toString());
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e)
        {
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                    .getClass().toString()
                    + " "
                    + "[createPdf()-IOException]", e.toString());
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }
    }

    public String fromDateValidation()
    {
        String msg=null;
        try{
            String today = fDate;
            DateFormat format = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
            Date dateToday = format.parse(today);

            Calendar calendar = Calendar.getInstance();
            Date tomo = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            DateFormat format2 = new SimpleDateFormat("yyyy-M-dd");
            String tomDate = format2.format(tomorrow);
            Date nextday = format.parse(tomDate);
            if(dateToday.after(nextday)||dateToday.equals(nextday))
            {
                ald.alertDialog(VehicleDistancePiechart.this,"From Date cannot be greater than Today's Date !");
                vehicleDistancePieChartBtnFromDate.setText("FROM DATE");
                msg= "failed";

            }
            else{
                msg= "success";
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(VehicleDistancePiechart.this, this
                            .getClass().toString() + " " + "[fromDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
        return msg;
    }

    public String toDateValidation()
    {
        String msg=null;
        try{
            String today = tDate;
            DateFormat format = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
            Date dateToday = format.parse(today);

            Calendar calendar = Calendar.getInstance();
            Date tomo = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            DateFormat format2 = new SimpleDateFormat("yyyy-M-dd");
            String tomDate = format2.format(tomorrow);
            Date nextday = format.parse(tomDate);

            Date preFromDate = format.parse(fDate);

            if(dateToday.after(nextday)||dateToday.equals(nextday))
            {
                ald.alertDialog(VehicleDistancePiechart.this,"To Date cannot be greater than Today's Date !");
                vehicleDistancePieChartBtnToDate.setText("TO DATE");
                msg= "failed";
            }

            else if(dateToday.before(preFromDate)){
                ald.alertDialog(VehicleDistancePiechart.this,"To Date cannot be less than From Date !");
                vehicleDistancePieChartBtnToDate.setText("TO DATE");
                msg= "failed";
            }

            else{
                msg= "success";
                SendToWebService send = new SendToWebService(
                        VehicleDistancePiechart.this, mVehicleDistancePieChart);
                // if (send.isConnectingToInternet()) {
                try {
                    send.execute("20", "GetVehicleDistance", fDate,
                            tDate);
                } catch (Exception e) {
                    Toast.makeText(VehicleDistancePiechart.this,
                            "Try after sometime...", Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString()
                            + " "
                            + "[calenderAlertDialog]", e.toString());
                }
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(VehicleDistancePiechart.this, this
                            .getClass().toString() + " " + "[toDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
        return msg;
    }

}
