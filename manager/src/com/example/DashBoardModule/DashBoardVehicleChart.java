/*
 * Purpose - VehicleMileageBarGraph implements Vehicle Mileage details in Bar Graph
 * @author - Pravitha 
 * Created on May 22, 2014
 * Modified on May 22, 2014
 */
package com.example.DashBoardModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IDashBoardVehicleChart;
import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.ProfileEdit;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class DashBoardVehicleChart extends ActionBarActivity implements
        IDashBoardVehicleChart, OnClickListener {
Toolbar toolbar;
    MatrixCursor VehicleMileage;
    Button vehicleMileageBarGraph;
    double Jan = 0.0, Feb = 0.0, Mar = 0.0, Apr = 0.0, May = 0.0, Jun = 0.0,
            Jul = 0.0, Aug = 0.0, Sep = 0.0, Oct = 0.0, Nov = 0.0, Dec = 0.0;
    double mMaxY = 0.0, total = 0.0;
    String jvehicle, jmileage;
    private GraphicalView mChartView = null;
    ImageView vehicleMileageBarGraphRefresh;
    LinearLayout vehicleMileageBarGraphContent;
    final IDashBoardVehicleChart mDashBoardVehicleChart = this;
    String adress = new IpAddress().getIpAddress();
    String str;
    Font head_main = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    int page;
    CustomAlertDialog ald;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_mileage_bargraph);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("DASHBOARD");
        setSupportActionBar(toolbar);
        ald=new CustomAlertDialog();
        final ImageView img = (ImageView) toolbar.findViewById(R.id.driverDistancePieChartPdfReport1);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    if (str != null && str.contains("OK")) {
                        createPDF(str);
                        Toast toast = Toast.makeText(DashBoardVehicleChart.this, "Report generating..", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

                        File file = new File(path, "VehicleMileage Report.pdf");

                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        startActivity(intent);
                    } else {
                        ald.alertDialog(DashBoardVehicleChart.this, "No record found !");
                    }

                } catch (Exception e) {
                    Toast.makeText(DashBoardVehicleChart.this, "Try after sometime...", Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString()
                            + " "
                            + "[calenderAlertDialog]", e.toString());

                }
            }
        });

        bindData();
        vehicleMileageBarGraphRefresh.setVisibility(View.INVISIBLE);
        vehicleMileageBarGraphRefresh.setOnClickListener(this);
        SendToWebService send = new SendToWebService(
                DashBoardVehicleChart.this, mDashBoardVehicleChart);

        try {
            send.execute("14", "GetVehicleMileage");
        } catch (Exception e) {
            Toast.makeText(DashBoardVehicleChart.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onCreate]", e.toString());
        }

    }

	/*
     * Purpose - Binds XMl Id reference to java Method Name - bindData()
	 * Parameters - No parameters Return Type - No Return Type
	 */

    private void bindData() {
        vehicleMileageBarGraphContent = (LinearLayout) findViewById(R.id.vehicleMileageBarGraphContent);
        vehicleMileageBarGraphRefresh = (ImageView) findViewById(R.id.vehicleMileageBarGraphRefresh);

    }

	/*
	 * Purpose - Get the data from the server Method Name -
	 * openVehicleChartRevised( Parameters - No parameter Return Type - No
	 * Return Type
	 */

    public void openVehicleChartRevised() {
        try {

            String[] vehicleNo;
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
            XYSeriesRenderer vehicleMileageRendererJan = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererFeb = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererMar = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererApr = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererMay = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererJun = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererJul = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererAug = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererSep = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererOct = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererNov = new XYSeriesRenderer();
            XYSeriesRenderer vehicleMileageRendererDec = new XYSeriesRenderer();

            if (VehicleMileage.getCount() == 0) {
                vehicleMileageBarGraphRefresh.setVisibility(View.INVISIBLE);
                vehicleMileageBarGraphContent.removeAllViews();
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.nodata);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.topMargin = 100;
                imageView.setLayoutParams(layoutParams);
                vehicleMileageBarGraphContent.addView(imageView);

                TextView textView=new TextView(this);
                textView.setText("NO DATA");
                textView.setTextSize(14);
                textView.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.gravity = Gravity.CENTER;
                layoutParams1.topMargin = 20;
                textView.setLayoutParams(layoutParams1);
                vehicleMileageBarGraphContent.addView(textView);
            } else {
                vehicleMileageBarGraphRefresh.setVisibility(View.VISIBLE);
                vehicleNo = new String[VehicleMileage.getCount()];
                double[] January = new double[VehicleMileage.getCount()];
                double[] February = new double[VehicleMileage.getCount()];
                double[] March = new double[VehicleMileage.getCount()];
                double[] April = new double[VehicleMileage.getCount()];
                double[] May = new double[VehicleMileage.getCount()];
                double[] June = new double[VehicleMileage.getCount()];
                double[] July = new double[VehicleMileage.getCount()];
                double[] August = new double[VehicleMileage.getCount()];
                double[] September = new double[VehicleMileage.getCount()];
                double[] October = new double[VehicleMileage.getCount()];
                double[] November = new double[VehicleMileage.getCount()];
                double[] December = new double[VehicleMileage.getCount()];

                // XYSeries vehicleNoSeries = new XYSeries("vehicleNo");
                XYSeries vehicleMileageSeriesJan = new XYSeries("Jan");
                XYSeries vehicleMileageSeriesFeb = new XYSeries("Feb");
                XYSeries vehicleMileageSeriesMar = new XYSeries("Mar");
                XYSeries vehicleMileageSeriesApr = new XYSeries("Apr");
                XYSeries vehicleMileageSeriesMay = new XYSeries("May");
                XYSeries vehicleMileageSeriesJun = new XYSeries("Jun");
                XYSeries vehicleMileageSeriesJul = new XYSeries("Jul");
                XYSeries vehicleMileageSeriesAug = new XYSeries("Aug");
                XYSeries vehicleMileageSeriesSep = new XYSeries("Sep");
                XYSeries vehicleMileageSeriesOct = new XYSeries("Oct");
                XYSeries vehicleMileageSeriesNov = new XYSeries("Nov");
                XYSeries vehicleMileageSeriesDec = new XYSeries("Dec");

                if (VehicleMileage.moveToFirst()) {
                    for (int i = 0; i < VehicleMileage.getCount(); i++) {
                        DecimalFormat numberFormat = new DecimalFormat("#.0");

                        January[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(3)));
                        if (mMaxY < January[i])
                            mMaxY = January[i];

                        February[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(4)));
                        if (mMaxY < February[i])
                            mMaxY = February[i];

                        March[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(5)));
                        if (mMaxY < March[i])
                            mMaxY = March[i];

                        April[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(6)));
                        if (mMaxY < April[i])
                            mMaxY = April[i];

                        May[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(7)));
                        if (mMaxY < May[i])
                            mMaxY = May[i];

                        // June[i] = VehicleMileage.getDouble(8);
                        June[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(8)));
                        if (mMaxY < June[i])
                            mMaxY = June[i];

                        July[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(9)));
                        if (mMaxY < July[i])
                            mMaxY = July[i];

                        August[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(10)));
                        if (mMaxY < August[i])
                            mMaxY = August[i];

                        September[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(11)));
                        if (mMaxY < September[i])
                            mMaxY = September[i];

                        October[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(12)));
                        if (mMaxY < October[i])
                            mMaxY = October[i];

                        November[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(13)));
                        if (mMaxY < November[i])
                            mMaxY = November[i];

                        December[i] = Double.parseDouble(numberFormat
                                .format(VehicleMileage.getDouble(14)));
                        if (mMaxY < December[i])
                            mMaxY = December[i];

                        if (January[i] != 0) {
                            vehicleMileageSeriesJan.add(i, January[i]);
                        }
                        if (February[i] != 0) {
                            vehicleMileageSeriesFeb.add(i, February[i]);
                        }
                        if (March[i] != 0) {
                            vehicleMileageSeriesMar.add(i, March[i]);
                        }
                        if (April[i] != 0) {
                            vehicleMileageSeriesApr.add(i, April[i]);
                        }
                        if (May[i] != 0) {
                            vehicleMileageSeriesMay.add(i, May[i]);
                        }
                        if (June[i] != 0) {
                            vehicleMileageSeriesJun.add(i, June[i]);
                        }
                        if (July[i] != 0) {
                            vehicleMileageSeriesJul.add(i, July[i]);
                        }
                        if (August[i] != 0) {
                            vehicleMileageSeriesAug.add(i, August[i]);
                        }
                        if (September[i] != 0) {
                            vehicleMileageSeriesSep.add(i, September[i]);
                        }
                        if (October[i] != 0) {
                            vehicleMileageSeriesOct.add(i, October[i]);
                        }
                        if (November[i] != 0) {
                            vehicleMileageSeriesNov.add(i, November[i]);
                        }
                        if (December[i] != 0) {
                            vehicleMileageSeriesDec.add(i, December[i]);
                        }

                        VehicleMileage.moveToNext();
                    }
                }
                VehicleMileage.close();

                dataset.addSeries(vehicleMileageSeriesJan);
                dataset.addSeries(vehicleMileageSeriesFeb);
                dataset.addSeries(vehicleMileageSeriesMar);
                dataset.addSeries(vehicleMileageSeriesApr);
                dataset.addSeries(vehicleMileageSeriesMay);
                dataset.addSeries(vehicleMileageSeriesJun);
                dataset.addSeries(vehicleMileageSeriesJul);
                dataset.addSeries(vehicleMileageSeriesAug);
                dataset.addSeries(vehicleMileageSeriesSep);
                dataset.addSeries(vehicleMileageSeriesOct);
                dataset.addSeries(vehicleMileageSeriesNov);
                dataset.addSeries(vehicleMileageSeriesDec);

                vehicleMileageRendererJan.setColor(Color.rgb(205, 133, 63));
                vehicleMileageRendererJan.setFillPoints(true);
                // vehicleMileageRendererJan.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererJan.setLineWidth((float) 0.9d);
                vehicleMileageRendererJan.setDisplayChartValues(true);
                vehicleMileageRendererJan.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererJan.setChartValuesTextSize(15f);

                vehicleMileageRendererFeb.setColor(Color.rgb(0, 255, 255));
                vehicleMileageRendererFeb.setFillPoints(true);
                // vehicleMileageRendererFeb.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererFeb.setLineWidth((float) 0.9d);
                vehicleMileageRendererFeb.setDisplayChartValues(true);
                vehicleMileageRendererFeb.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererFeb.setChartValuesTextSize(15f);

                vehicleMileageRendererMar.setColor(Color.rgb(128, 128, 0));
                vehicleMileageRendererMar.setFillPoints(true);
                // vehicleMileageRendererMar.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererMar.setLineWidth((float) 0.9d);
                vehicleMileageRendererMar.setDisplayChartValues(true);
                vehicleMileageRendererMar.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererMar.setChartValuesTextSize(15f);

                vehicleMileageRendererApr.setColor(Color.rgb(210, 105, 30));
                vehicleMileageRendererApr.setFillPoints(true);
                // vehicleMileageRendererApr.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererApr.setLineWidth((float) 0.9d);
                vehicleMileageRendererApr.setDisplayChartValues(true);
                vehicleMileageRendererApr.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererApr.setChartValuesTextSize(15f);

                vehicleMileageRendererMay.setColor(Color.rgb(154, 205, 50));
                vehicleMileageRendererMay.setFillPoints(true);
                // vehicleMileageRendererMay.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererMay.setLineWidth((float) 0.9d);
                vehicleMileageRendererMay.setDisplayChartValues(true);
                vehicleMileageRendererMay.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererMay.setChartValuesTextSize(15f);

                vehicleMileageRendererJun.setColor(Color.rgb(255, 102, 102));
                vehicleMileageRendererJun.setFillPoints(true);
                // vehicleMileageRendererJun.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererJun.setLineWidth((float) 0.9d);
                vehicleMileageRendererJun.setDisplayChartValues(true);
                vehicleMileageRendererJun.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererJun.setChartValuesTextSize(15f);

                vehicleMileageRendererJul.setColor(Color.rgb(0, 128, 128));
                vehicleMileageRendererJul.setFillPoints(true);
                // vehicleMileageRendererJul.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererJul.setLineWidth((float) 0.9d);
                vehicleMileageRendererJul.setDisplayChartValues(true);
                vehicleMileageRendererJul.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererJul.setChartValuesTextSize(15f);

                vehicleMileageRendererAug.setColor(Color.rgb(255, 153, 153));
                vehicleMileageRendererAug.setFillPoints(true);
                // vehicleMileageRendererAug.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererAug.setLineWidth((float) 0.9d);
                vehicleMileageRendererAug.setDisplayChartValues(true);
                vehicleMileageRendererAug.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererAug.setChartValuesTextSize(15f);

                vehicleMileageRendererSep.setColor(Color.rgb(41, 174, 215));
                vehicleMileageRendererSep.setFillPoints(true);
                // vehicleMileageRendererSep.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererSep.setLineWidth((float) 0.9d);
                vehicleMileageRendererSep.setDisplayChartValues(true);
                vehicleMileageRendererSep.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererSep.setChartValuesTextSize(15f);

                vehicleMileageRendererOct.setColor(Color.rgb(165, 42, 42));
                vehicleMileageRendererOct.setFillPoints(true);
                // vehicleMileageRendererOct.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererOct.setLineWidth((float) 0.9d);
                vehicleMileageRendererOct.setDisplayChartValues(true);
                vehicleMileageRendererOct.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererOct.setChartValuesTextSize(15f);

                vehicleMileageRendererNov.setColor(Color.rgb(244, 164, 96));
                vehicleMileageRendererNov.setFillPoints(true);
                // vehicleMileageRendererNov.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererNov.setLineWidth((float) 0.9d);
                vehicleMileageRendererNov.setDisplayChartValues(true);
                vehicleMileageRendererNov.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererNov.setChartValuesTextSize(15f);

                vehicleMileageRendererDec.setColor(Color.rgb(255, 105, 180));
                vehicleMileageRendererDec.setFillPoints(true);
                // vehicleMileageRendererDec.setChartValuesSpacing((float)
                // 0.5d);
                vehicleMileageRendererDec.setLineWidth((float) 0.9d);
                vehicleMileageRendererDec.setDisplayChartValues(true);
                vehicleMileageRendererDec.setChartValuesTextAlign(Align.CENTER);
                vehicleMileageRendererDec.setChartValuesTextSize(15f);

                // Creating a XYMultipleSeriesRenderer to customize the whole
                // chart
                XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
                multiRenderer.setXLabels(0);
                // multiRenderer.setChartTitle("Vehicle vs Mileage Chart");
                multiRenderer.setXTitle("\n\nVehicle No");
                multiRenderer.setYTitle("Mileage");
                multiRenderer.setBarWidth(20f);
                multiRenderer.setAxisTitleTextSize(20);
                multiRenderer.setLabelsTextSize(11);
                multiRenderer.setLabelsColor(Color.rgb(0, 191, 255));
                multiRenderer.setAxesColor(Color.rgb(200, 200, 200));
                multiRenderer.setXLabelsColor(Color.rgb(72, 72, 72));
                multiRenderer.setYLabelsColor(0, Color.rgb(72, 72, 72));
                multiRenderer.setZoomEnabled(true, true);
                multiRenderer.setMargins(new int[]{50, 40, 50, 40});
                multiRenderer.setMarginsColor(Color
                        .argb(0x00, 0xff, 0x00, 0x00));
                multiRenderer.setYLabelsAlign(Align.RIGHT);
                multiRenderer.setXLabelsAlign(Align.CENTER);
                multiRenderer.setZoomButtonsVisible(false);
                multiRenderer.setExternalZoomEnabled(true);
                multiRenderer.setShowLegend(true);
                multiRenderer.setShowGridX(true);
                multiRenderer.setGridColor(Color.rgb(200, 200, 200));
                multiRenderer.setBarSpacing(.3);
                multiRenderer.setXAxisMin(-1);
                multiRenderer.setYAxisMin(0);
                multiRenderer.setXAxisMax(2.0);
                multiRenderer.setYAxisMax(mMaxY + 1);
                multiRenderer.setScale(5);
                multiRenderer.setFitLegend(true);
                multiRenderer.setPanEnabled(true, false);

                if (VehicleMileage.moveToFirst()) {
                    for (int i = 0; i < VehicleMileage.getCount(); i++) {
                        vehicleNo[i] = VehicleMileage.getString(1);
                        multiRenderer.addXTextLabel(i, vehicleNo[i]);
                        VehicleMileage.moveToNext();
                    }
                }

                multiRenderer.addSeriesRenderer(vehicleMileageRendererJan);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererFeb);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererMar);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererApr);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererMay);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererJun);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererJul);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererAug);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererSep);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererOct);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererNov);
                multiRenderer.addSeriesRenderer(vehicleMileageRendererDec);

                mChartView = ChartFactory.getBarChartView(this, dataset,
                        multiRenderer, Type.DEFAULT);
                vehicleMileageBarGraphContent.removeAllViews();

                vehicleMileageBarGraphContent.addView(mChartView);

            }
        } catch (Exception e) {
            Toast.makeText(DashBoardVehicleChart.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[openVehicleChartRevised]", e.toString());
        }

    }

	/*
	 * Purpose - Parse the data got from the server Method Name -vehicleParser()
	 * Parameters - response Return Type - String JsonData
	 * 
	 * Variable list String jsonData - To get the data with tag d JSONArray
	 * distance - JSon array is Stored String id - Id of driver String name -
	 * String which holds the driver name.
	 */

    public String vehicleParser(String response) {
        String jsonData = null,year=null;
        String statuschk = null;
        int key = 0;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                // GET THE VALUE OF d:
                jsonData = jsonResponse.getString("d");
                JSONArray mileageArray = new JSONArray(jsonData);

                JSONObject status1 = mileageArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authKey")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("data does not exist")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString(), statuschk);
                }  else if (statuschk.equals("OK")) {
                    VehicleMileage = new MatrixCursor(new String[]{"_id",
                            "VehNo", "year", "Jan", "Feb", "Mar", "Apr", "May",
                            "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
                    JSONArray vehicleArray = mileageArray.getJSONArray(1);

                    for (int i = 0; i < vehicleArray.length(); i++) {
                        JSONObject da = vehicleArray.getJSONObject(i);
                        String name = da.getString("id");
                        JSONArray listYearArray=da.getJSONArray("listYear");
                        JSONObject listYearObj=listYearArray.getJSONObject(0);

                        if (!(listYearObj.isNull("year"))) {
                            year = listYearObj.getString("year");
                        }

                        if (!(listYearObj.isNull("jan"))) {
                            Jan = listYearObj.getDouble("jan");
                        }
                        if (!(listYearObj.isNull("feb"))) {
                            Feb = listYearObj.getDouble("feb");
                        }
                        if (!(listYearObj.isNull("mar"))) {
                            Mar = listYearObj.getDouble("mar");
                        }
                        if (!(listYearObj.isNull("apr"))) {
                            Apr = listYearObj.getDouble("apr");
                        }
                        if (!(listYearObj.isNull("may"))) {
                            May = listYearObj.getDouble("may");
                        }
                        if (!(listYearObj.isNull("jun"))) {
                            Jun = listYearObj.getDouble("jun");
                        }
                        if (!(listYearObj.isNull("jul"))) {
                            Jul = listYearObj.getDouble("jul");
                        }
                        if (!(listYearObj.isNull("aug"))) {
                            Aug = listYearObj.getDouble("aug");
                        }
                        if (!(listYearObj.isNull("sep"))) {
                            Sep = listYearObj.getDouble("sep");
                        }
                        if (!(listYearObj.isNull("oct"))) {
                            Oct = listYearObj.getDouble("oct");
                        }
                        if (!(listYearObj.isNull("nov"))) {
                            Nov = listYearObj.getDouble("nov");
                        }
                        if (!(listYearObj.isNull("dec"))) {
                            Dec = listYearObj.getDouble("dec");
                        }

                       VehicleMileage.addRow(new Object[]{key, name,
                                year, Jan, Feb, Mar, Apr, May, Jun, Jul,
                                Aug, Sep, Oct, Nov, Dec});
                        key++;

                    }
                }

            }

            catch (JSONException e) {

                Toast.makeText(this, "Try after sometime...",
                        Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[vehicleParser]", e.toString());
            }

        return statuschk;
    }


//    public String vehicleParser1(String response) {
//        String jsonData = null;
//        String statuschk = null;
//        int key = 0;
//        if (response != null)
//            try {
//                // TO CONVERT THE STRING TO OBJECT
//                JSONObject jsonResponse = new JSONObject(response);
//
//                // GET THE VALUE OF d:
//                jsonData = jsonResponse.getString("d");
//                // INITIALIZING THE ARRAY
//                JSONArray mileageArray = new JSONArray(jsonData);
//
//                JSONObject status1 = mileageArray.getJSONObject(0);
//                statuschk = status1.getString("status").trim();
//
//                if (statuschk.equals("invalid authKey")) {
//                    ExceptionMessage.exceptionLog(this, this.getClass()
//                            .toString(), statuschk);
//                } else if (statuschk.equals("data does not exist")) {
//                    ExceptionMessage.exceptionLog(this, this.getClass()
//                            .toString(), statuschk);
//                }  else if (statuschk.equals("OK")) {
//
//                    VehicleMileage = new MatrixCursor(new String[]{"_id",
//                            "VehNo", "year", "Jan", "Feb", "Mar", "Apr", "May",
//                            "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
//
//                    for (int i = 1; i < mileageArray.length(); i++) {
//                        JSONObject c = mileageArray.getJSONObject(i);
//
//                        String Id = c.getString("Id");
//                        // //GET EMPNAME FROM LOCAL SQLITE DB
//
//                        JSONArray ListYearArray = c.getJSONArray("ListYear");
//
//                        for (int j = 0; j < ListYearArray.length(); j++) {
//                            JSONObject e = ListYearArray.getJSONObject(j);
//                            String year = e.getString("year");
//
//                            if (!(e.isNull("Jan"))) {
//                                Jan = e.getDouble("Jan");
//                            }
//                            if (!(e.isNull("Feb"))) {
//                                Feb = e.getDouble("Feb");
//                            }
//                            if (!(e.isNull("Mar"))) {
//                                Mar = e.getDouble("Mar");
//                            }
//                            if (!(e.isNull("Apr"))) {
//                                Apr = e.getDouble("Apr");
//                            }
//                            if (!(e.isNull("May"))) {
//                                May = e.getDouble("May");
//                            }
//                            if (!(e.isNull("Jun"))) {
//                                Jun = e.getDouble("Jun");
//                            }
//                            if (!(e.isNull("Jul"))) {
//                                Jul = e.getDouble("Jul");
//                            }
//                            if (!(e.isNull("Aug"))) {
//                                Aug = e.getDouble("Aug");
//                            }
//                            if (!(e.isNull("Sep"))) {
//                                Sep = e.getDouble("Sep");
//                            }
//                            if (!(e.isNull("Oct"))) {
//                                Oct = e.getDouble("Oct");
//                            }
//                            if (!(e.isNull("Nov"))) {
//                                Nov = e.getDouble("Nov");
//                            }
//                            if (!(e.isNull("Dec"))) {
//                                Dec = e.getDouble("Dec");
//                            }
//                            // cusor formation
//                            VehicleMileage.addRow(new Object[]{key, Id, year,
//                                    Jan, Feb, Mar, Apr, May, Jun, Jul, Aug,
//                                    Sep, Oct, Nov, Dec});
//                            key++;
//                        }
//
//                    }
//                    // db.close();
//                    // return jsonData;
//                }
//            } catch (JSONException e) {
//
//                Toast.makeText(this, "Try after sometime...",
//                        Toast.LENGTH_SHORT).show();
//                ExceptionMessage.exceptionLog(this, this.getClass().toString()
//                        + " " + "[vehicleParser]", e.toString());
//            }
//
//        return statuschk;
//
//    }

    @Override
    public void onDashBoardVehicleChart(String response) {
        try {
            str = response;
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused") || response.contains("timed out")) {
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
                String stat = vehicleParser(response);
                vehicleMileageBarGraphContent = (LinearLayout) findViewById(R.id.vehicleMileageBarGraphContent);
                if (!stat.equals("data does not exist")) {
                    openVehicleChartRevised();
                } else {
                    vehicleMileageBarGraphContent.removeAllViews();
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(R.drawable.nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    vehicleMileageBarGraphContent.addView(imageView);

                    TextView textView=new TextView(this);
                    textView.setText("NO DATA");
                    textView.setTextSize(14);
                    textView.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.gravity = Gravity.CENTER;
                    layoutParams1.topMargin = 20;
                    textView.setLayoutParams(layoutParams1);
                    vehicleMileageBarGraphContent.addView(textView);

                }

            }
        } catch (Exception e) {
            Toast.makeText(DashBoardVehicleChart.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onDashBoardDriverChart]", e.toString());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicleMileageBarGraphRefresh:
                mChartView.zoomReset();
                break;
            default:
                break;
        }

    }
    class HeaderFooterPageEvent extends PdfPageEventHelper {
        int pageNumber;

        @Override
        public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
            super.onChapter(writer, document, paragraphPosition, title);
            pageNumber = 1;
        }

        public void onStartPage(PdfWriter writer, Document document) {
            Chunk headReport = new Chunk("Vehicle Mileage Report", head_main);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(headReport), 300, 780, 0);
            pageNumber++;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            String currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Fleet Manager"+" / "+currentTime), 130, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()+" of "+page), 550, 30, 0);
        }

    }

    public void createPDF(String response) {
        Document doc = new Document(PageSize.A4, 20, 20, 70, 60);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "VehicleMileage Report.pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter w = PdfWriter.getInstance(doc, fOut);
            Rectangle rect = new Rectangle(30, 30, 550, 780);
            w.setBoxSize("art", rect);
            //HeaderFooterPageEvent is a class for adding header n footer
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            w.setPageEvent(event);

            doc.open();
            doc.add(Chunk.NEWLINE);
            doc.add(new LineSeparator());
            LineSeparator sep1 = new LineSeparator();
            sep1.setOffset(0);
            doc.add(sep1);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            //pdf containing table with 2 column
            PdfPTable table = new PdfPTable(2);
            table.setSpacingBefore(20f);
            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);

            table.getDefaultCell().setBorderWidth(0);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Chunk vehiclename = new Chunk("VEHICLE", font1);
            Chunk mileage = new Chunk("MILEAGE", font1);

            PdfPCell cell = new PdfPCell(new Paragraph(vehiclename));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(mileage));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            try {

                String statuschk = "";
                String jsonData = "";
                // Create the root JSONObject from the JSON string.
                JSONObject jsonResponse = new JSONObject(response);

                //Get the instance of JSONArray that contains JSONObjects
                jsonData = jsonResponse.getString("d");

                JSONArray mileageArray = new JSONArray(jsonData);
                if (mileageArray.length()/40>=0)
                {
                    page=mileageArray.length()/40;
                    page++;
                }

                JSONObject status1 = mileageArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(DashBoardVehicleChart.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("vehicle number does not exist")) {
                    ExceptionMessage.exceptionLog(DashBoardVehicleChart.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("data does not exist")) {
                    Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                } else if (statuschk.equals("OK")) {

                    JSONArray driverArray = mileageArray.getJSONArray(1);
                    for (int i = 0; i < driverArray.length(); i++) {
                        JSONObject da = driverArray.getJSONObject(i);
                        jvehicle = da.getString("id");
                        JSONArray listYearArray = da.getJSONArray("listYear");
                        JSONObject listYearObj = listYearArray.getJSONObject(0);

                        if (!(listYearObj.isNull("jan"))) {
                            Jan = listYearObj.getDouble("jan");
                        }
                        if (!(listYearObj.isNull("feb"))) {
                            Feb = listYearObj.getDouble("feb");
                        }
                        if (!(listYearObj.isNull("mar"))) {
                            Mar = listYearObj.getDouble("mar");
                        }
                        if (!(listYearObj.isNull("apr"))) {
                            Apr = listYearObj.getDouble("apr");
                        }
                        if (!(listYearObj.isNull("may"))) {
                            May = listYearObj.getDouble("may");
                        }
                        if (!(listYearObj.isNull("jun"))) {
                            Jun = listYearObj.getDouble("jun");
                        }
                        if (!(listYearObj.isNull("jul"))) {
                            Jul = listYearObj.getDouble("jul");
                        }
                        if (!(listYearObj.isNull("aug"))) {
                            Aug = listYearObj.getDouble("aug");
                        }
                        if (!(listYearObj.isNull("sep"))) {
                            Sep = listYearObj.getDouble("sep");
                        }
                        if (!(listYearObj.isNull("oct"))) {
                            Oct = listYearObj.getDouble("oct");
                        }
                        if (!(listYearObj.isNull("nov"))) {
                            Nov = listYearObj.getDouble("nov");
                        }
                        if (!(listYearObj.isNull("dec"))) {
                            Dec = listYearObj.getDouble("dec");
                        }
                        total = Jan + Feb + Mar + Apr + May + Jun + Jul + Aug + Sep + Oct + Nov + Dec;
                        jmileage = total + "";
                        table.addCell(jvehicle);
                        table.addCell(jmileage);

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


}
