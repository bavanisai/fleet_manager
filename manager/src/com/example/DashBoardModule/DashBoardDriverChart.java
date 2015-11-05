/*
 * Purpose - DriverMileageBarGraph implements Driver Mileage details in Bar Graph
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IDashBoardDriverChart;
import com.example.anand_roadwayss.AboutUs;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
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

public class
        DashBoardDriverChart extends ActionBarActivity implements
        IDashBoardDriverChart, OnClickListener {

    MatrixCursor DriverMileage;
    double Jan = 0.0, Feb = 0.0, Mar = 0.0, Apr = 0.0, May = 0.0, Jun = 0.0,
            Jul = 0.0, Aug = 0.0, Sep = 0.0, Oct = 0.0, Nov = 0.0, Dec = 0.0;
    double total = 0.0;
    double mMaxY = 0;
    String str;
    private GraphicalView mChartView = null;
    LinearLayout driverMileageBarGraphContent;
    final IDashBoardDriverChart mDashBoardDriverChart = this;
    DBAdapter db;
    ImageView driverMileageBarGraphRefresh;
    String adress = new IpAddress().getIpAddress();
    Toolbar toolbar;
    String jdriver, jmileage;
    Font head_main = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    int page;
    CustomAlertDialog ald;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_mileage_bargraph);
        db = new DBAdapter(this);
        ald=new CustomAlertDialog();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("DASHBOARD");
        setSupportActionBar(toolbar);

        ImageView img = (ImageView) toolbar.findViewById(R.id.driverDistancePieChartPdfReport1);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (str != null && str.contains("OK"))
                    {
                        //if data is prasent create pdf n open it.
                        createPDF(str);
                        Toast toast = Toast.makeText(DashBoardDriverChart.this, "Report generating..", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

                        File file = new File(path, "DriverMileage Report.pdf");

                        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                        startActivity(intent);
                    } else {
                        ald.alertDialog(DashBoardDriverChart.this, "No record found !");
                    }
                } catch (Exception e) {
                    Toast.makeText(DashBoardDriverChart.this, "Try after sometime...", Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString()
                            + " "
                            + "[calenderAlertDialog]", e.toString());

                }
            }
        });

        bindData();
        driverMileageBarGraphRefresh.setVisibility(View.INVISIBLE);
        driverMileageBarGraphRefresh.setOnClickListener(this);

        SendToWebService send = new SendToWebService(DashBoardDriverChart.this,
                mDashBoardDriverChart);
        // if (send.isConnectingToInternet()) {
        try {
            send.execute("13", "GetDriverMileage");
        } catch (Exception e) {
            Toast.makeText(DashBoardDriverChart.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getApplicationContext(), this
                    .getClass().toString() + " " + "[onCreate]", e.toString());

        }

    }

	/*
     * Purpose - Binds XMl Id reference to java Method Name - bindData()
	 * Parameters - No parameters Return Type - No Return Type
	 */

    private void bindData() {
        driverMileageBarGraphContent = (LinearLayout) findViewById(R.id.driverMileageBarGraphContent);
        driverMileageBarGraphRefresh = (ImageView) findViewById(R.id.driverMileageBarGraphRefresh);
    }

	/*
	 * Purpose - Get the data from the server Method Name -
	 * openDriverChartRevised( Parameters - No parameter Return Type - No Return
	 * Type
	 */

    public void openDriverChartRevised() {
        try {

            String[] driverNo = null;

            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

            XYSeriesRenderer driverMileageRendererJan = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererFeb = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererMar = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererApr = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererMay = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererJun = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererJul = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererAug = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererSep = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererOct = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererNov = new XYSeriesRenderer();
            XYSeriesRenderer driverMileageRendererDec = new XYSeriesRenderer();

            if (DriverMileage.getCount() == 0)
            {
                driverMileageBarGraphRefresh.setVisibility(View.INVISIBLE);
                driverMileageBarGraphContent.removeAllViews();
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.alert_nodata);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.topMargin = 100;
                imageView.setLayoutParams(layoutParams);
                driverMileageBarGraphContent.addView(imageView);

                TextView textView=new TextView(this);
                textView.setText("NO DATA");
                textView.setTextSize(14);
                textView.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.gravity = Gravity.CENTER;
                layoutParams1.topMargin = 20;
                textView.setLayoutParams(layoutParams1);
                driverMileageBarGraphContent.addView(textView);
            } else {
                driverMileageBarGraphRefresh.setVisibility(View.VISIBLE);
                driverNo = new String[DriverMileage.getCount()];
                double[] January = new double[DriverMileage.getCount()];
                double[] February = new double[DriverMileage.getCount()];
                double[] March = new double[DriverMileage.getCount()];
                double[] April = new double[DriverMileage.getCount()];
                double[] May = new double[DriverMileage.getCount()];
                double[] June = new double[DriverMileage.getCount()];
                double[] July = new double[DriverMileage.getCount()];
                double[] August = new double[DriverMileage.getCount()];
                double[] September = new double[DriverMileage.getCount()];
                double[] October = new double[DriverMileage.getCount()];
                double[] November = new double[DriverMileage.getCount()];
                double[] December = new double[DriverMileage.getCount()];

                // XYSeries driverNoSeries = new XYSeries("driverNo");
                XYSeries driverMileageSeriesJan = new XYSeries("Jan");
                XYSeries driverMileageSeriesFeb = new XYSeries("Feb");
                XYSeries driverMileageSeriesMar = new XYSeries("Mar");
                XYSeries driverMileageSeriesApr = new XYSeries("Apr");
                XYSeries driverMileageSeriesMay = new XYSeries("May");
                XYSeries driverMileageSeriesJun = new XYSeries("Jun");
                XYSeries driverMileageSeriesJul = new XYSeries("Jul");
                XYSeries driverMileageSeriesAug = new XYSeries("Aug");
                XYSeries driverMileageSeriesSep = new XYSeries("Sep");
                XYSeries driverMileageSeriesOct = new XYSeries("Oct");
                XYSeries driverMileageSeriesNov = new XYSeries("Nov");
                XYSeries driverMileageSeriesDec = new XYSeries("Dec");

                if (DriverMileage.moveToFirst()) {
                    for (int i = 0; i < DriverMileage.getCount(); i++) {
                        DecimalFormat numberFormat = new DecimalFormat("#.0");

                        January[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(3)));

                        if (mMaxY < January[i])
                            mMaxY = January[i];

                        February[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(4)));
                        if (mMaxY < February[i])
                            mMaxY = February[i];

                        March[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(5)));
                        if (mMaxY < March[i])
                            mMaxY = March[i];

                        April[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(6)));
                        if (mMaxY < April[i])
                            mMaxY = April[i];

                        May[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(7)));
                        if (mMaxY < May[i])
                            mMaxY = May[i];

                        June[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(8)));
                        if (mMaxY < June[i])
                            mMaxY = June[i];

                        July[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(9)));
                        if (mMaxY < July[i])
                            mMaxY = July[i];

                        August[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(10)));
                        if (mMaxY < August[i])
                            mMaxY = August[i];

                        September[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(11)));
                        if (mMaxY < September[i])
                            mMaxY = September[i];

                        October[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(12)));
                        if (mMaxY < October[i])
                            mMaxY = October[i];

                        November[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(13)));
                        if (mMaxY < November[i])
                            mMaxY = November[i];

                        December[i] = Double.parseDouble(numberFormat
                                .format(DriverMileage.getDouble(14)));
                        if (mMaxY < December[i])
                            mMaxY = December[i];

                        if (January[i] != 0) {
                            driverMileageSeriesJan.add(i, January[i]);
                        }
                        if (February[i] != 0) {
                            driverMileageSeriesFeb.add(i, February[i]);
                        }
                        if (March[i] != 0) {
                            driverMileageSeriesMar.add(i, March[i]);
                        }
                        if (April[i] != 0) {
                            driverMileageSeriesApr.add(i, April[i]);
                        }
                        if (May[i] != 0) {
                            driverMileageSeriesMay.add(i, May[i]);
                        }
                        if (June[i] != 0) {
                            driverMileageSeriesJun.add(i, June[i]);
                        }
                        if (July[i] != 0) {
                            driverMileageSeriesJul.add(i, July[i]);
                        }
                        if (August[i] != 0) {
                            driverMileageSeriesAug.add(i, August[i]);
                        }
                        if (September[i] != 0) {
                            driverMileageSeriesSep.add(i, September[i]);
                        }
                        if (October[i] != 0) {
                            driverMileageSeriesOct.add(i, October[i]);
                        }
                        if (November[i] != 0) {
                            driverMileageSeriesNov.add(i, November[i]);
                        }
                        if (December[i] != 0) {
                            driverMileageSeriesDec.add(i, December[i]);
                        }

                        DriverMileage.moveToNext();
                    }
                }
                DriverMileage.close();

                dataset.addSeries(driverMileageSeriesJan);
                dataset.addSeries(driverMileageSeriesFeb);
                dataset.addSeries(driverMileageSeriesMar);
                dataset.addSeries(driverMileageSeriesApr);
                dataset.addSeries(driverMileageSeriesMay);
                dataset.addSeries(driverMileageSeriesJun);
                dataset.addSeries(driverMileageSeriesJul);
                dataset.addSeries(driverMileageSeriesAug);
                dataset.addSeries(driverMileageSeriesSep);
                dataset.addSeries(driverMileageSeriesOct);
                dataset.addSeries(driverMileageSeriesNov);
                dataset.addSeries(driverMileageSeriesDec);

                driverMileageRendererJan.setColor(Color.rgb(205, 133, 63));
                driverMileageRendererJan.setFillPoints(true);
                // vehicleMileageRendererJan.setChartValuesSpacing((float)
                // 0.5d);
                driverMileageRendererJan.setLineWidth((float) 0.9d);
                driverMileageRendererJan.setDisplayChartValues(true);
                driverMileageRendererJan.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererJan.setChartValuesTextSize(15f);

                driverMileageRendererFeb.setColor(Color.rgb(0, 255, 255));
                driverMileageRendererFeb.setFillPoints(true);
                // driverMileageRendererFeb.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererFeb.setLineWidth((float) 0.9d);
                driverMileageRendererFeb.setDisplayChartValues(true);
                driverMileageRendererFeb.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererFeb.setChartValuesTextSize(15f);

                driverMileageRendererMar.setColor(Color.rgb(128, 128, 0));
                driverMileageRendererMar.setFillPoints(true);
                // driverMileageRendererMar.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererMar.setLineWidth((float) 0.9d);
                driverMileageRendererMar.setDisplayChartValues(true);
                driverMileageRendererMar.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererMar.setChartValuesTextSize(15f);

                driverMileageRendererApr.setColor(Color.rgb(210, 105, 30));
                driverMileageRendererApr.setFillPoints(true);
                // driverMileageRendererApr.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererApr.setLineWidth((float) 0.9d);
                driverMileageRendererApr.setDisplayChartValues(true);
                driverMileageRendererApr.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererApr.setChartValuesTextSize(15f);

                driverMileageRendererMay.setColor(Color.rgb(154, 205, 50));
                driverMileageRendererMay.setFillPoints(true);
                // driverMileageRendererMay.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererMay.setLineWidth((float) 0.9d);
                driverMileageRendererMay.setDisplayChartValues(true);
                driverMileageRendererMay.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererMay.setChartValuesTextSize(15f);

                driverMileageRendererJun.setColor(Color.rgb(255, 102, 102));
                driverMileageRendererJun.setFillPoints(true);
                // driverMileageRendererJun.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererJun.setLineWidth((float) 0.9d);
                driverMileageRendererJun.setDisplayChartValues(true);
                driverMileageRendererJun.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererJun.setChartValuesTextSize(15f);

                driverMileageRendererJul.setColor(Color.rgb(0, 128, 128));
                driverMileageRendererJul.setFillPoints(true);
                // driverMileageRendererJul.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererJul.setLineWidth((float) 0.9d);
                driverMileageRendererJul.setDisplayChartValues(true);
                driverMileageRendererJul.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererJul.setChartValuesTextSize(15f);

                driverMileageRendererAug.setColor(Color.rgb(255, 153, 153));
                driverMileageRendererAug.setFillPoints(true);
                // driverMileageRendererAug.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererAug.setLineWidth((float) 0.9d);
                driverMileageRendererAug.setDisplayChartValues(true);
                driverMileageRendererAug.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererAug.setChartValuesTextSize(15f);

                driverMileageRendererSep.setColor(Color.rgb(41, 174, 215));
                driverMileageRendererSep.setFillPoints(true);
                // driverMileageRendererSep.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererSep.setLineWidth((float) 0.9d);
                driverMileageRendererSep.setDisplayChartValues(true);
                driverMileageRendererSep.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererSep.setChartValuesTextSize(15f);

                driverMileageRendererOct.setColor(Color.rgb(165, 42, 42));
                driverMileageRendererOct.setFillPoints(true);
                // driverMileageRendererOct.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererOct.setLineWidth((float) 0.9d);
                driverMileageRendererOct.setDisplayChartValues(true);
                driverMileageRendererOct.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererOct.setChartValuesTextSize(15f);

                driverMileageRendererNov.setColor(Color.rgb(244, 164, 96));
                driverMileageRendererNov.setFillPoints(true);
                // driverMileageRendererNov.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererNov.setLineWidth((float) 0.9d);
                driverMileageRendererNov.setDisplayChartValues(true);
                driverMileageRendererNov.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererNov.setChartValuesTextSize(15f);

                driverMileageRendererDec.setColor(Color.rgb(255, 105, 180));
                driverMileageRendererDec.setFillPoints(true);
                // driverMileageRendererDec.setChartValuesSpacing((float) 0.5d);
                driverMileageRendererDec.setLineWidth((float) 0.9d);
                driverMileageRendererDec.setDisplayChartValues(true);
                driverMileageRendererDec.setChartValuesTextAlign(Align.CENTER);
                driverMileageRendererDec.setChartValuesTextSize(15f);

                // Creating a XYMultipleSeriesRenderer to customize the whole
                // chart
                XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
                multiRenderer.setXLabels(0);
                multiRenderer.setXTitle("\n\nDriver Name");
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
                // multiRenderer.setPanEnabled(false);
                // multiRenderer.setChartTitleTextSize(47);
                // multiRenderer.setFitLegend(true);
                // multiRenderer.setOrientation(Orientation.VERTICAL);
                // multiRenderer.setChartValuesTextSize(25f);
                // multiRenderer.setLegendTextSize(13);
                // multiRenderer.setBarWidth((float) 5.0);
                // multiRenderer.setPanEnabled(true, false);
                // multiRenderer.setFitLegend(true);
                // multiRenderer.setShowGridY(true);
                // multiRenderer.setApplyBackgroundColor(true);
                // multiRenderer.setBackgroundColor(Color.BLACK);
                // multiRenderer.setXAxisMax(DriverMileage.getCount());
                if (DriverMileage.moveToFirst()) {
                    for (int i = 0; i < DriverMileage.getCount(); i++) {
                        driverNo[i] = DriverMileage.getString(1);
                        multiRenderer.addXTextLabel(i, driverNo[i]);
                        DriverMileage.moveToNext();
                    }
                }

                multiRenderer.addSeriesRenderer(driverMileageRendererJan);
                multiRenderer.addSeriesRenderer(driverMileageRendererFeb);
                multiRenderer.addSeriesRenderer(driverMileageRendererMar);
                multiRenderer.addSeriesRenderer(driverMileageRendererApr);
                multiRenderer.addSeriesRenderer(driverMileageRendererMay);
                multiRenderer.addSeriesRenderer(driverMileageRendererJun);
                multiRenderer.addSeriesRenderer(driverMileageRendererJul);
                multiRenderer.addSeriesRenderer(driverMileageRendererAug);
                multiRenderer.addSeriesRenderer(driverMileageRendererSep);
                multiRenderer.addSeriesRenderer(driverMileageRendererOct);
                multiRenderer.addSeriesRenderer(driverMileageRendererNov);
                multiRenderer.addSeriesRenderer(driverMileageRendererDec);

                mChartView = ChartFactory.getBarChartView(this, dataset, multiRenderer, Type.DEFAULT);
                driverMileageBarGraphContent.removeAllViews();
                driverMileageBarGraphContent.addView(mChartView);

            }

        } catch (Exception e) {
            Toast.makeText(DashBoardDriverChart.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[openDriverChartRevised]", e.toString());

        }
    }

	/*
	 * Purpose - Parse the data got from the server Method Name -driverParser()
	 * Parameters - response Return Type - String JsonData
	 * 
	 * Variable list String jsonData - To get the data with tag d JSONArray
	 * distance - JSon array is Stored String id - Id of driver String name -
	 * String which holds the driver name.
	 */

    public String driverParser(String response) {
        String jsonData = null,year = null;
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

                    DriverMileage = new MatrixCursor(new String[]{"_id",
                            "DriverName", "year", "Jan", "Feb", "Mar", "Apr",
                            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                            "Dec"});

                    JSONArray driverArray = mileageArray.getJSONArray(1);
                    for (int i = 0; i < driverArray.length(); i++) {
                        JSONObject da = driverArray.getJSONObject(i);
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

                        DriverMileage.addRow(new Object[]{key, name,
                                year, Jan, Feb, Mar, Apr, May, Jun, Jul,
                                Aug, Sep, Oct, Nov, Dec});
                        key++;

                    }


                }


            }

            catch (JSONException e) {

                Toast.makeText(DashBoardDriverChart.this,
                        "Try after sometime...", Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[driverParser]", e.toString());
            }
        return statuschk;

    }

    public String driverParser1(String response) {
        String jsonData = null;
        String statuschk = null;
        int key = 0;
        if (response != null)
            try {
                // TO CONVERT THE STRING TO OBJECT
                JSONObject jsonResponse = new JSONObject(response);

                // GET THE VALUE OF d:
                jsonData = jsonResponse.getString("d");
                // INITIALIZING THE ARRAY
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

                    DriverMileage = new MatrixCursor(new String[]{"_id",
                            "DriverName", "year", "Jan", "Feb", "Mar", "Apr",
                            "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                            "Dec"});

                    db.open();
                    for (int i = 1; i < mileageArray.length(); i++) {
                        JSONObject c = mileageArray.getJSONObject(i);

                        String Id = c.getString("Id");
                        // GET EMPNAME FROM LOCAL SQLITE DB
                        String name = db.getEmployeeName(Id,
                                DBAdapter.getEmployeeDetails(),
                                DBAdapter.getKeyName());
                        JSONArray ListYearArray = c.getJSONArray("ListYear");

                        for (int j = 0; j < ListYearArray.length(); j++) {
                            JSONObject e = ListYearArray.getJSONObject(j);
                            String year = e.getString("year");

                            if (!(e.isNull("jan"))) {
                                Jan = e.getDouble("jan");
                            }
                            if (!(e.isNull("feb"))) {
                                Feb = e.getDouble("feb");
                            }
                            if (!(e.isNull("mar"))) {
                                Mar = e.getDouble("mar");
                            }
                            if (!(e.isNull("apr"))) {
                                Apr = e.getDouble("apr");
                            }
                            if (!(e.isNull("may"))) {
                                May = e.getDouble("may");
                            }
                            if (!(e.isNull("jun"))) {
                                Jun = e.getDouble("jun");
                            }
                            if (!(e.isNull("jul"))) {
                                Jul = e.getDouble("jul");
                            }
                            if (!(e.isNull("aug"))) {
                                Aug = e.getDouble("aug");
                            }
                            if (!(e.isNull("sep"))) {
                                Sep = e.getDouble("sep");
                            }
                            if (!(e.isNull("oct"))) {
                                Oct = e.getDouble("oct");
                            }
                            if (!(e.isNull("nov"))) {
                                Nov = e.getDouble("nov");
                            }
                            if (!(e.isNull("dec"))) {
                                Dec = e.getDouble("dec");
                            }
                            // cusor formation
                            DriverMileage.addRow(new Object[]{key, name,
                                    year, Jan, Feb, Mar, Apr, May, Jun, Jul,
                                    Aug, Sep, Oct, Nov, Dec});
                            key++;
                        }

                    }
                    db.close();
                    // return jsonData;
                }
            } catch (JSONException e) {

                Toast.makeText(DashBoardDriverChart.this,
                        "Try after sometime...", Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[driverParser]", e.toString());
            }

        return statuschk;

    }

    @Override
    public void onDashBoardDriverChart(String response) {
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
                // int i=1/0;
                String stat = driverParser(response);
                driverMileageBarGraphContent = (LinearLayout) findViewById(R.id.driverMileageBarGraphContent);
                if (!stat.equals("data does not exist")) {
                    openDriverChartRevised();
                } else {

                    driverMileageBarGraphContent.removeAllViews();
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(R.drawable.alert_nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    driverMileageBarGraphContent.addView(imageView);

                    TextView textView=new TextView(this);
                    textView.setText("NO DATA");
                    textView.setTextSize(14);
                    textView.setTypeface(null, Typeface.BOLD);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.gravity = Gravity.CENTER;
                    layoutParams1.topMargin = 20;
                    textView.setLayoutParams(layoutParams1);
                    driverMileageBarGraphContent.addView(textView);
                }

            }
        } catch (Exception e) {
            Toast.makeText(DashBoardDriverChart.this, "Try after sometime ...",
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
            case R.id.driverMileageBarGraphRefresh:
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
            Chunk headReport = new Chunk("Driver Mileage Report", head_main);
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
            //pdf path
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "DriverMileage Report.pdf");
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

            Chunk drivername = new Chunk("DRIVER", font1);
            Chunk mileage = new Chunk("MILEAGE", font1);

            PdfPCell cell = new PdfPCell(new Paragraph(drivername));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(mileage));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            try {

                String statuschk = null;
                String jsonData = null;
                // Create the root JSONObject from the JSON string.
                JSONObject jsonResponse = new JSONObject(response);

                //Get the instance of JSONArray that contains JSONObjects
                jsonData = jsonResponse.getString("d");

                JSONArray mileageArray = new JSONArray(jsonData);
                if(mileageArray.length()/40>=0)
                {
                    page=mileageArray.length()/40;
                    page++;
                }

                JSONObject status1 = mileageArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(DashBoardDriverChart.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("vehicle number does not exist")) {
                    ExceptionMessage.exceptionLog(DashBoardDriverChart.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("data does not exist")) {
                    Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                } else if (statuschk.equals("OK")) {

//                    for (int i = 1; i < fuelArray.length(); i++) {
//                        JSONObject jsonObject = fuelArray.getJSONObject(i);
//                        JSONArray names = jsonObject.names();
//
//                        for (int k = 0; k < names.length(); k++) {
//                            jdriver = jsonObject.optString("DriverName").toString();
//                           // jmileage = jsonObject.optString("distance").toString();
//
//                        }
                    JSONArray driverArray = mileageArray.getJSONArray(1);
                    for (int i = 0; i < driverArray.length(); i++) {
                        JSONObject da = driverArray.getJSONObject(i);
                        jdriver = da.getString("id");
                        JSONArray listYearArray = da.getJSONArray("listYear");
                        JSONObject listYearObj = listYearArray.getJSONObject(0);

//                        if (!(listYearObj.isNull("year"))) {
//                            year = listYearObj.getString("year");
//                        }

                        if (!(listYearObj.isNull("Jan"))) {
                            Jan = listYearObj.getDouble("Jan");
                        }
                        if (!(listYearObj.isNull("Feb"))) {
                            Feb = listYearObj.getDouble("Feb");
                        }
                        if (!(listYearObj.isNull("Mar"))) {
                            Mar = listYearObj.getDouble("Mar");
                        }
                        if (!(listYearObj.isNull("Apr"))) {
                            Apr = listYearObj.getDouble("Apr");
                        }
                        if (!(listYearObj.isNull("May"))) {
                            May = listYearObj.getDouble("May");
                        }
                        if (!(listYearObj.isNull("Jun"))) {
                            Jun = listYearObj.getDouble("Jun");
                        }
                        if (!(listYearObj.isNull("Jul"))) {
                            Jul = listYearObj.getDouble("Jul");
                        }
                        if (!(listYearObj.isNull("Aug"))) {
                            Aug = listYearObj.getDouble("Aug");
                        }
                        if (!(listYearObj.isNull("Sep"))) {
                            Sep = listYearObj.getDouble("Sep");
                        }
                        if (!(listYearObj.isNull("Oct"))) {
                            Oct = listYearObj.getDouble("Oct");
                        }
                        if (!(listYearObj.isNull("Nov"))) {
                            Nov = listYearObj.getDouble("Nov");
                        }
                        if (!(listYearObj.isNull("Dec"))) {
                            Dec = listYearObj.getDouble("Dec");
                        }
                        total = Jan + Feb + Mar + Apr + May + Jun + Jul + Aug + Sep + Oct + Nov + Dec;
                        jmileage = total + "";
                        table.addCell(jdriver);
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

        } catch (DocumentException de) {
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