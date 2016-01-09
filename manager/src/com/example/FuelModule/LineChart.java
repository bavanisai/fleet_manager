package com.example.FuelModule;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IFuelGraph;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class LineChart extends AppCompatActivity implements IFuelGraph {

    final IFuelGraph mFuelGraph = this;
    String fromDate, toDate, vehicleNo;
    ArrayList<String> dateList;
    ArrayList<Double> fuelList;
    String date;
    double fuel;
    ImageView fuelChartRefresh;
    private GraphicalView mChart = null;
    Toolbar toolbar;
    LinearLayout noDataLayout;
    LinearLayout chartContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        noDataLayout=(LinearLayout)findViewById(R.id.inboxLinearL);
        chartContainer = (LinearLayout) findViewById(R.id.chart);
        fuelChartRefresh = (ImageView) findViewById(R.id.fuelChartRefresh);
      //refresh chart
        fuelChartRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    mChart.zoomReset();
                }catch (Exception e)
                {
                    ExceptionMessage.exceptionLog(LineChart.this, this
                                    .getClass().toString() + " " + "[onCreate()]",
                            e.toString());
                    e.printStackTrace();
                }
            }
        });
        dateList = new ArrayList();
        fuelList = new ArrayList();

        Intent intent = getIntent();
        fromDate = intent.getStringExtra("fromDate");
        toDate = intent.getStringExtra("toDate");
        vehicleNo = intent.getStringExtra("vehicleNo");
        FuelGraph(fromDate, toDate, vehicleNo);

    }

    public void FuelGraph( String fromdate, String todate, String vehicleNum) {
        SendToWebService send = new SendToWebService(this, mFuelGraph);
        try {
            send.execute("49", "GetFuelData", fromdate, todate, vehicleNum);

        } catch (Exception e) {
            Toast.makeText(this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString() + " " + "[deletePayment]",
                    e.toString());
        }
    }

    private void openChart(String response) {
        try {
            String statuschk = null;
            String jsonData = null;
            // Create the root JSONObject from the JSON string.
            JSONObject jsonResponse = new JSONObject(response);

            //Get the instance of JSONArray that contains JSONObjects
            jsonData = jsonResponse.getString("d");

            JSONArray fuelArray = new JSONArray(jsonData);

            JSONObject status1 = fuelArray.getJSONObject(0);
            statuschk = status1.getString("status").trim();

            if (statuschk.equals("invalid authkey")) {
                ExceptionMessage.exceptionLog(LineChart.this, this.getClass()
                        .toString(), statuschk);
            } else if (statuschk.equals("vehicle number does not exist")) {
                ExceptionMessage.exceptionLog(LineChart.this, this.getClass()
                        .toString(), statuschk);
            } else if (statuschk.contains("data does not exist"))
            {
                noDataLayout.setVisibility(View.VISIBLE);
                chartContainer.setVisibility(View.GONE);
                fuelChartRefresh.setVisibility(View.GONE);
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.nodata);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.topMargin = 100;
                layoutParams.bottomMargin=10;
                imageView.setLayoutParams(layoutParams);
                noDataLayout.addView(imageView);

                TextView textView=new TextView(this);
                textView.setText("NO DATA");
                textView.setTextSize(14);
                textView.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.gravity = Gravity.CENTER;
                layoutParams1.topMargin = 20;
                textView.setLayoutParams(layoutParams1);
               noDataLayout.addView(textView);
               // Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
            }
            else if (statuschk.equals("OK")) {
                noDataLayout.setVisibility(View.GONE);
                chartContainer.setVisibility(View.VISIBLE);
                for (int i = 1; i < fuelArray.length(); i++) {
                    JSONObject jsonObject = fuelArray.getJSONObject(i);
                    JSONArray names = jsonObject.names();

                    for (int k = 0; k < names.length(); k++) {
                        date = jsonObject.optString("dateTime").toString();
                        fuel = jsonObject.optDouble("fuel");
                    }

                    dateList.add(date);
                    fuelList.add(fuel);
                }

                // Creating an  XYSeries for Fuel
                XYSeries incomeSeries = new XYSeries("");

                for (int i = 0; i < fuelList.size(); i++) {
                    incomeSeries.add(i, fuelList.get(i));
                }

                // Creating a dataset to hold each series
                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                // Adding Income Series to the dataset
                dataset.addSeries(incomeSeries);

                XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
                incomeRenderer.setColor(Color.parseColor("#009688"));
                incomeRenderer.setPointStyle(PointStyle.CIRCLE);
                incomeRenderer.setFillPoints(true);
                incomeRenderer.setLineWidth(2);
                incomeRenderer.setChartValuesTextSize(20);
                incomeRenderer.setDisplayChartValues(true);

                XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
                multiRenderer.setXLabels(0);
                multiRenderer.setYAxisMin(0);

                multiRenderer.setChartTitle("Fuel Report");
                multiRenderer.setXTitle("\n\n\n\n\n" + fromDate + " To " + toDate);
                multiRenderer.setYTitle("Fuel in Ltrs");
                multiRenderer.setZoomButtonsVisible(false);
                multiRenderer.setAxisTitleTextSize(20);
                multiRenderer.setChartTitleTextSize(25);
                multiRenderer.setTextTypeface(Typeface.DEFAULT_BOLD);
                multiRenderer.setLabelsColor(Color.parseColor("#2196F3"));
                multiRenderer.setXLabelsColor(Color.BLACK);
                multiRenderer.setYLabelsColor(0, Color.BLACK);
                multiRenderer.setLabelsTextSize(15);
                multiRenderer.setLegendTextSize(11);
                multiRenderer.setShowGridX(true);
                multiRenderer.setApplyBackgroundColor(true);
                multiRenderer.setBackgroundColor(Color.WHITE);
                multiRenderer.setMarginsColor(Color.WHITE);
                multiRenderer.setXAxisMax(4.0);
                multiRenderer.setExternalZoomEnabled(true);
                multiRenderer.setFitLegend(true);
                multiRenderer.setMargins(new int[]{80, 40, 30, 25});
                multiRenderer.setMarginsColor(Color
                        .argb(0x00, 0xff, 0xff, 0x00));

                for (int i = 0; i < fuelList.size(); i++) {
                    String dt=dateList.get(i);
                    dt=dt.replace("T","\n");
                    multiRenderer.addXTextLabel(i + 1, dt);

                }

                // Adding incomeRenderer and expenseRenderer to multipleRenderer
                // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
                // should be same
                multiRenderer.addSeriesRenderer(incomeRenderer);
                //       multiRenderer.addSeriesRenderer(expenseRenderer);

                // Getting a reference to LinearLayout of the MainActivity Layout


                // Creating a Line Chart
                mChart = ChartFactory.getLineChartView(this, dataset, multiRenderer);
                chartContainer.removeAllViews();
                // Adding the Line Chart to the LinearLayout
                chartContainer.addView(mChart);
            }

        } catch (JSONException e)
        {
            ExceptionMessage.exceptionLog(LineChart.this, this
                            .getClass().toString() + " " + "[openChart()-jsonException]",
                    e.toString());
            e.printStackTrace();
        }

    }


    @Override
    public void onFuelGraph(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused") || response.contains("timed out")) {
                ImageView image = new ImageView(this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(this).setPositiveButton("OK",
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

                AlertDialog.Builder builder = new AlertDialog.Builder(this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();
            } else {
                openChart(response);
            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[onFuelReport]",
                    e.toString());
        }

    }

}

