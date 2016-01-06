/*
 * Purpose - DriverDistancePiechart implements Driver distance details in Pie chart
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
import com.example.Interface.IDriverDistancePieChart;
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


public class DriverDistancePiechart extends ActionBarActivity implements
        OnClickListener, IDriverDistancePieChart {

    String mdateType, mfrom_date, mto_date, currentTime;
    String str;
    static int count = 0;
    private static int sKey = 0;
    MatrixCursor driverDistanceCursor;
    TextView driverDistancePieChartTvTotalDistance, driverDistancePieChartTv;
    double mTotalKm = 0.0;
    private GraphicalView mChartView = null;
    Button driverDistancePieChartBtnFromDate, driverDistancePieChartBtnToDate;
    LinearLayout layout;
    final IDriverDistancePieChart mDriverDistancePieChart = this;
    ImageView driverDistancePieChartRefresh;
    String adress = new IpAddress().getIpAddress();
    String fDate, tDate;
    ImageView pdfReport;
    Toolbar toolbar;
    String jdriver, jdistance;
    Font head_main = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    int page;
    CustomAlertDialog ald;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_distance_piechart);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("DASHBOARD");
        setSupportActionBar(toolbar);
        ald=new CustomAlertDialog();
        ImageView img = (ImageView) toolbar.findViewById(R.id.driverDistancePieChartPdfReport1);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (driverDistancePieChartBtnFromDate.getText().toString().equals("FROM DATE")
                        && driverDistancePieChartBtnToDate.getText().toString().equals("TO DATE"))
                {
                    ald.alertDialog(DriverDistancePiechart.this,"Please select from date and to date !");
                }

                else if(driverDistancePieChartBtnToDate.getText().toString().equals("TO DATE"))
                {
                    ald.alertDialog(DriverDistancePiechart.this, "Please select To date !");
                }
                else
                {
                    count++;
                    try {

                        if (str != null && str.contains("OK")) {
                            createPDF(str);
                            Toast toast = Toast.makeText(DriverDistancePiechart.this, "Report generating..", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

                            File file = new File(path, "DriverDistance Report.pdf");

                            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                            startActivity(intent);
                        } else {
                            ald.alertDialog(DriverDistancePiechart.this,"Record not found from " + fDate + " to " + tDate + " !");
                        }

                    } catch (Exception e) {
                        Toast.makeText(DriverDistancePiechart.this,
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
        driverDistancePieChartRefresh.setVisibility(View.INVISIBLE);
        layout.removeAllViews();
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.selectdate);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.topMargin = 100;
        imageView.setLayoutParams(layoutParams);
        layout.addView(imageView);
        driverDistancePieChartTv.setVisibility(View.INVISIBLE);
        driverDistancePieChartBtnFromDate.setOnClickListener(this);
        driverDistancePieChartBtnToDate.setOnClickListener(this);
        driverDistancePieChartRefresh.setOnClickListener(this);


    }

    /*
     * Purpose - Binds XMl Id reference to java Method Name - bindData()
     * Parameters - No parameters Return Type - No Return Type
     */
    private void bindData() {

        driverDistancePieChartRefresh = (ImageView) findViewById(R.id.driverDistancePieChartRefresh);
        driverDistancePieChartTv = (TextView) findViewById(R.id.driverDistancePieChartTv);
        driverDistancePieChartTvTotalDistance = (TextView) findViewById(R.id.driverDistancePieChartTvTotalDistance);
        driverDistancePieChartBtnFromDate = (Button) findViewById(R.id.driverDistancePieChartBtnFromDate);
        driverDistancePieChartBtnToDate = (Button) findViewById(R.id.driverDistancePieChartBtnToDate);
        layout = (LinearLayout) findViewById(R.id.driverDistancePieChartContent);
//        pdfReport=(ImageView)findViewById(R.id.driverDistancePieChartPdfReport);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.driverDistancePieChartBtnFromDate:
                driverDistancePieChartBtnToDate.setText("TO DATE");
                mdateType = "from";
                calenderAlertDialog(mdateType);
                break;

            case R.id.driverDistancePieChartBtnToDate:
                if (driverDistancePieChartBtnFromDate.getText().toString() != ""
                        && driverDistancePieChartBtnFromDate.getText().toString() != null
                        && !driverDistancePieChartBtnFromDate.getText().toString()
                        .contains("FROM DATE")) {
                    mdateType = "to";
                    calenderAlertDialog(mdateType);
                } else {
                    ald.alertDialog(DriverDistancePiechart.this,"Please select from date !");
                }
                break;

            case R.id.driverDistancePieChartRefresh:
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
                    driverDistancePieChartBtnFromDate.setText(String.valueOf(year)
                            + "-" + String.valueOf(monthOfYear + 1) + "-"
                            + String.valueOf(dayOfMonth));
                }
            } else if (mdateType == "to") {
                tDate = String.valueOf(year) + "-"
                        + String.valueOf(monthOfYear + 1) + "-"
                        + String.valueOf(dayOfMonth);
                String status=toDateValidation();
                if(status.equals("success")) {
                    driverDistancePieChartBtnToDate.setText(String.valueOf(year)
                            + "-" + String.valueOf(monthOfYear + 1) + "-"
                            + String.valueOf(dayOfMonth));
                }


            }

        }
    };

	/*
	 * Purpose - Get the data from the server and display pie chart Method Name
	 * - DriverDistance() Parameters - mfrom_date and mto_date Return Type - No
	 * Return Type
	 * 
	 * Variable list String response - To fetch the response from the server
	 * double[] driverDistanceKM - Double Array which holds all the distance
	 * traveled by the driver. String[] driverName - String array which holds
	 * all the driver name. Int[] colors - Integer Array which holds the total
	 * No of colors.
	 */

    public void driverDistance(String mfrom_date, String mto_date) {
        try {
            // If the cursor i.e server data is null then just display sample
            // pie chart in the activity
            if (driverDistanceCursor.getCount() == 0) {
                driverDistancePieChartRefresh.setVisibility(View.INVISIBLE);
                layout.removeAllViews();
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.nodata);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.topMargin = 100;
                imageView.setLayoutParams(layoutParams);
                layout.addView(imageView);
                driverDistancePieChartTv.setVisibility(View.INVISIBLE);

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


            }

            // If we get the data from server display it in pie chart
            else {
                driverDistancePieChartRefresh.setVisibility(View.VISIBLE);
                double[] driverDistanceKM = new double[driverDistanceCursor
                        .getCount()];
                CategorySeries driverSeries = new CategorySeries("pie");
                String[] driverName = new String[driverDistanceCursor
                        .getCount()];

                int[] colors = new int[driverDistanceCursor.getCount()];

                if (driverDistanceCursor.moveToFirst()) {
                    for (int i = 0; i < driverDistanceCursor.getCount(); i++) {
                        driverName[i] = driverDistanceCursor.getString(1);
                        driverDistanceKM[i] = driverDistanceCursor.getDouble(2);
                        driverSeries.add(driverName[i], driverDistanceKM[i]);
                        Random rnd = new Random();
                        colors[i] = Color.argb(255, rnd.nextInt(256),
                                rnd.nextInt(256), rnd.nextInt(256));

                        driverDistanceCursor.moveToNext();
                    }
                }
                driverDistanceCursor.close();

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

                mChartView = ChartFactory.getPieChartView(this, driverSeries, renderer);
                layout.removeAllViews();

                layout.addView(mChartView);
                driverDistancePieChartTv.setVisibility(View.VISIBLE);
                driverDistancePieChartTvTotalDistance.setText(Double.toString(mTotalKm));

            }

        } catch (Exception e) {
            Toast.makeText(DriverDistancePiechart.this,
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[driverDistance]", e.toString());

        }
    }

	/*
	 * Purpose - Parse the data got from the server Method Name
	 * -DriverdistanceParser() Parameters - response Return Type - No Return
	 * Type
	 * 
	 * Variable list String jsonData - To get the data with tag d JSONArray
	 * distance - JSon array is Stored String[] driverName - String array which
	 * holds all the driver name. Int[] colors - Integer Array which holds the
	 * total No of colors.
	 */

    public String driverDistanceParser(String response) {
        String statuschk = null;

        if (response != null)

            try {
                // String Name = null,Distance ="0";

                JSONObject jsonResponse = new JSONObject(response);
                String jsondata=jsonResponse.getString("d");
                JSONArray distance=new JSONArray(jsondata);
                JSONObject jsonResponse1 = distance.getJSONObject(0);
                statuschk = jsonResponse1.getString("status").trim();

                if (statuschk.equals("invalid authKey")) {
                    ExceptionMessage.exceptionLog(this, this.getClass()
                                    .toString() + " " + "[driverDistanceParser]",
                            statuschk);

                } else if (statuschk.equals("data does not exist")) {
//                    ExceptionMessage.exceptionLog(this, this.getClass()
//                                    .toString() + " " + "[driverDistanceParser]",
//                            statuschk);
                }  else if (statuschk.equals("OK")) {
                    String[] columnNames = {"_id", "Name", "Distance"};
                    driverDistanceCursor = new MatrixCursor(columnNames);
                    for (int i = 1; i < distance.length(); i++) {
                         JSONObject c = distance.getJSONObject(i);

                       //JSONObject Performance = d.getJSONObject(i);
                        // if (!(Performance.isNull("Name"))) {
                        String Name = c.getString("driverName");
                        // }
                        // if (!(Performance.isNull("Distance"))) {
                        String Distance = c.getString("distance");
                        // }
                        driverDistanceCursor.addRow(new Object[]{sKey, Name,
                                Distance});
                        sKey++;
                        mTotalKm = mTotalKm + Double.parseDouble(Distance);

                    }
                }

            } catch (JSONException e) {

//                Toast.makeText(this, "Try after sometime...",
//                        Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(this, this.getClass().toString()
                        + " " + "[driverDistanceParser]", e.toString());

            }
        return statuschk;

    }

    @Override
    public void onDriverDistancePieChart(String response) {
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
                String stat = driverDistanceParser(response);
                layout = (LinearLayout) findViewById(R.id.driverDistancePieChartContent);
                driverDistancePieChartTvTotalDistance = (TextView) findViewById(R.id.driverDistancePieChartTvTotalDistance);
                if (!stat.equals("data does not exist")) {
                    driverDistance(mfrom_date, mto_date);
                } else {
                    layout.removeAllViews();
                    ImageView imageView = new ImageView(this);
                    imageView.setImageResource(R.drawable.nodata);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.topMargin = 100;
                    imageView.setLayoutParams(layoutParams);
                    layout.addView(imageView);
                    driverDistancePieChartTv.setVisibility(View.INVISIBLE);
                    driverDistancePieChartTvTotalDistance.setText("");
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
                }
            }
        } catch (Exception e) {
            Toast.makeText(DriverDistancePiechart.this,
                    "Try after sometime...", Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString()
                    + " " + "[onDriverDistancePieChart]", e.toString());
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
    class HeaderFooterPageEvent extends PdfPageEventHelper
    {
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
                int a=writer.getPageNumber();
                switch(a/a) {
                    case 1:
                        Chunk headReport = new Chunk("DriverDistance Report", head_main);
                        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(headReport), 300,780, 0);

                        document.add(new LineSeparator());
                        LineSeparator sep1 = new LineSeparator();
                        sep1.setOffset(0);
                        document.add(sep1);

                        ColumnText.showTextAligned(writer.getDirectContent(),
                                Element.ALIGN_LEFT,new Phrase( header[0]),
                                rect.getLeft(), rect.getTop()-30, 0);
                        ColumnText.showTextAligned(writer.getDirectContent(),
                                Element.ALIGN_LEFT, new Phrase(header[1]),
                                rect.getLeft(), rect.getTop()-50, 0);
                        document.add(Chunk.NEWLINE);

                        document.add(new LineSeparator());
                        LineSeparator sep2 = new LineSeparator();
                        sep2.setOffset(0);
                        document.add(sep2);
                        document.add(Chunk.NEWLINE);
                        break;
                }

            }catch (DocumentException e)
            {
                ExceptionMessage.exceptionLog(DriverDistancePiechart.this, this
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
        Document doc = new Document(PageSize.A4, 20, 20, 120, 60);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "DriverDistance Report.pdf");
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

            Chunk driver = new Chunk("DRIVER", font1);
            Chunk distance = new Chunk("DISTANCE", font1);

            PdfPCell cell = new PdfPCell(new Paragraph(driver));
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
                    ExceptionMessage.exceptionLog(DriverDistancePiechart.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("vehicle number does not exist")) {
                    ExceptionMessage.exceptionLog(DriverDistancePiechart.this, this.getClass()
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
//from date validation
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
            if(dateToday.after(nextday) ||dateToday.equals(nextday))
            {
                ald.alertDialog(DriverDistancePiechart.this,"From Date cannot be greater than Today's Date !");
                msg= "failed";
                driverDistancePieChartBtnFromDate.setText("FROM DATE");
            }
            else{
                msg= "success";
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
        return msg;
    }
// To date validation
    public String  toDateValidation()
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
                ald.alertDialog(DriverDistancePiechart.this,"To Date cannot be greater than Today's Date !");
                msg= "failed";
                driverDistancePieChartBtnToDate.setText("TO DATE");
            }

            else if(dateToday.before(preFromDate)){
                ald.alertDialog(DriverDistancePiechart.this,"To Date cannot be less than From Date !");
                msg= "failed";
                driverDistancePieChartBtnToDate.setText("TO DATE");
            }

            else{
                SendToWebService send = new SendToWebService(
                        DriverDistancePiechart.this, mDriverDistancePieChart);
                // if (send.isConnectingToInternet()) {
                try {
                    send.execute("19", "GetDriverDistance", fDate, tDate);
                } catch (Exception e) {
//                    Toast.makeText(DriverDistancePiechart.this,
//                            "Try after sometime...", Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getApplicationContext(), this
                            .getClass().toString()
                            + " "
                            + "[calenderAlertDialog]", e.toString());

                }
                msg= "success";
            }

        }

        catch (Exception e){
            e.printStackTrace();
        }
        return msg;
    }


}
