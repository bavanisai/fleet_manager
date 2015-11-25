package com.example.FuelModule;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IFuelReport;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.R;

import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
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
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

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

public class FuelReport extends ActionBarActivity implements IFuelReport {
    private TextView fromDatetxt;
    private TextView toDatetxt;
    TextView message,ok;
    LinearLayout noDataLayout;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String jdate, jvehNum, jdriver, jfuel, jspeedometer, jmileage;
    String selectedFromList, fromDate, toDate, currentTime;
    String parsedString = null;
    Calendar fdate, tdate;
	String avgMileage;
    int noPages;
    final IFuelReport mFuelReport = this;
    Toolbar toolbar;
    Font head_main = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    ListView vehnum;
    int page;
    CustomAlertDialog ald;
    Date dateToday, nextday;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fuel_report);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("FUEL REPORT");
        setSupportActionBar(toolbar);
        currentTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        ald=new CustomAlertDialog();
        DBAdapter db = new DBAdapter(this);
        vehnum = (ListView) findViewById(R.id.listViewVehicleNum);
       noDataLayout=(LinearLayout)findViewById(R.id.inboxLinearL);
        db.open();

        Cursor vehicleCursor = db.getAllData(DBAdapter.getVehicleDetails());
        // LIST OF VEHICLES
        if(vehicleCursor.getCount()>0) {
            noDataLayout.setVisibility(View.GONE);
            vehnum.setVisibility(View.VISIBLE);
            String from[] = {DBAdapter.getKeyVehicleNo()};
            int to[] = {R.id.vehNumbersListViewTrackVehicle};
            SimpleCursorAdapter num = new SimpleCursorAdapter(this, R.layout.account3, vehicleCursor, from, to, 0);

            vehnum.setAdapter(num);
            db.close();
        }
            else{
          //if listview is empty at 1st time
            noDataLayout.setVisibility(View.VISIBLE);
            vehnum.setVisibility(View.GONE);
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.nodata);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.topMargin = 100;
            imageView.setLayoutParams(layoutParams);
            noDataLayout.addView(imageView);

            TextView textView=new TextView(this);
            textView.setText("NO VEHICLE LIST");
            textView.setTextSize(14);
            textView.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER;
            layoutParams1.topMargin = 20;
            textView.setLayoutParams(layoutParams1);
            noDataLayout.addView(textView);
        }
            vehnum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedFromList = ((TextView) view.findViewById(R.id.vehNumbersListViewTrackVehicle)).getText().toString();
                    if ((fromDatetxt.getText().toString().equals("click here") && toDatetxt.getText().toString().equals("click here")))
                    {
                        ald.alertDialog(FuelReport.this,"Please select From date and To date !");
                    }
                    else if(toDatetxt.getText().toString().equals("click here"))
                    {
                        ald.alertDialog(FuelReport.this, "Please select To date !");
                    }
                    else {
                        try {
                            fuelAlertDialog();
                        } catch (Exception e) {
                            ExceptionMessage.exceptionLog(FuelReport.this, this
                                            .getClass().toString() + " " + "[ vehnum.setOnItemClickListener()]",
                                    e.toString());
                            Toast.makeText(getApplicationContext(), "Please Install Adobe Reader", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            findViewsById();
            setDateTimeField();
        }




    private void findViewsById()
    {
        fromDatetxt = (TextView) findViewById(R.id.textClick1);
        fromDatetxt.setInputType(InputType.TYPE_NULL);
        fromDatetxt.requestFocus();

        toDatetxt = (TextView) findViewById(R.id.textclick2);
        toDatetxt.setInputType(InputType.TYPE_NULL);
    }

    private void setDateTimeField() {
        fromDatetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();

            }
        });
        toDatetxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromDatetxt.getText().toString().equals("click here")
                        && toDatetxt.getText().toString().equals("click here")) {
                    ald.alertDialog(FuelReport.this, "Please select From date !");
                } else
                    toDatePickerDialog.show();

            }
        });


        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDatetxt.setText(dateFormatter.format(newDate.getTime()));
                fromDate = fromDatetxt.getText().toString();
                fdate = newDate;
                fromDateValidation();
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDatetxt.setText(dateFormatter.format(newDate.getTime()));
                toDate = toDatetxt.getText().toString();
                tdate = newDate;
                toDateValidation();
                System.out.println(toDate);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }
    class HeaderFooterPageEvent extends PdfPageEventHelper {
        int pageNumber;
        PdfTemplate total;

        Chunk[] header = new Chunk[2];
        @Override
        public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
            super.onChapter(writer, document, paragraphPosition, title);
            pageNumber = 1;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            super.onOpenDocument(writer, document);
            header[0] = new Chunk("From : " + fromDate, font1);
            header[1]= new Chunk("To      : " + toDate, font1);
            total=writer.getDirectContent().createTemplate(30,16);


        }

        public void onStartPage(PdfWriter writer, Document document) {
            pageNumber++;

            try {
                Rectangle rect = writer.getBoxSize("art");
                int a=writer.getPageNumber();
                switch(a/a) {
                    case 1:
                        Chunk headReport = new Chunk("Fuel Report", head_main);
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
                ExceptionMessage.exceptionLog(FuelReport.this, this
                                .getClass().toString() + " " + "[onStartPage()]",
                        e.toString());
                e.printStackTrace();
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document doc)
        {
            ColumnText.showTextAligned(total,Element.ALIGN_LEFT,new Phrase(String.valueOf(writer.getPageNumber()-1)),2,2,0);
        }

        public void onEndPage(PdfWriter writer, Document document)
        {

            String currentTime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Fleet Manager"+" / "+currentTime), 130, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()+" of "+page), 550, 30, 0);
        }

    }
    public void createPDF(String response) {
        Document doc = new Document(PageSize.A4, 20, 20, 120, 60);

        try {
            //pdf path
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "FuelReport.pdf");
            FileOutputStream fOut = new FileOutputStream(file);
            PdfWriter w = PdfWriter.getInstance(doc, fOut);

            Rectangle rect = new Rectangle(30, 30, 550, 780);
            w.setBoxSize("art", rect);

            //class containing header n footer details
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            w.setPageEvent(event);

            doc.open();
             //pdf table with 5 columns
            PdfPTable table = new PdfPTable(5);
            table.setSpacingBefore(20f);
            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorderWidth(0);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Chunk date = new Chunk("DATE", font1);
            Chunk vehicle = new Chunk("VEHICLE NO", font1);
            Chunk driver = new Chunk("DRIVER", font1);
            Chunk fuel = new Chunk("FUEL", font1);
            Chunk speedometer = new Chunk("SPEEDOMETER", font1);

            PdfPCell cell = new PdfPCell(new Paragraph(date));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(vehicle));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(driver));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(fuel));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(speedometer));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            try {
			String statuschk=null;
			String jsonData =null;
                // Create the root JSONObject from the JSON string.
                JSONObject jsonResponse = new JSONObject(response);

                //Get the instance of JSONArray that contains JSONObjects
                jsonData = jsonResponse.getString("d");

                JSONArray fuelArray = new JSONArray(jsonData);
                //total page count
                if(fuelArray.length()/40>=0)
                {
                    page=fuelArray.length()/40;
                    page++;
                }

                JSONObject status1 = fuelArray.getJSONObject(0);
                statuschk = status1.getString("status").trim();

                if (statuschk.equals("invalid authkey")) {
                    ExceptionMessage.exceptionLog(FuelReport.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("vehicle number does not exist")) {
                    ExceptionMessage.exceptionLog(FuelReport.this, this.getClass()
                            .toString(), statuschk);
                } else if (statuschk.equals("data does not exist")) {
                    Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                } else if (statuschk.equals("OK")) {

                    JSONObject mileage = fuelArray.getJSONObject(1);
                    avgMileage = mileage.getString("averageMileage").trim();

                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 2; i < fuelArray.length(); i++) {
                        JSONObject jsonObject = fuelArray.getJSONObject(i);
                        JSONArray names = jsonObject.names();

                        for (int k = 0; k < names.length(); k++) {
                            jdate = jsonObject.optString("fuelFilledDate").toString();
                            jvehNum = jsonObject.optString("vehicleNumber").toString();
                            jdriver = jsonObject.optString("employeeName").toString();
                            jfuel = jsonObject.optString("fuelVolume").toString();
                            jspeedometer = jsonObject.optString("speedoMeter").toString();

                        }
                        String[] parts = jdate.split("T");
                        table.addCell(parts[0]);
                        table.addCell(jvehNum);
                        table.addCell(jdriver);
                        table.addCell(jfuel);
                        table.addCell(jspeedometer);
                        table.setSpacingAfter(10);

                    }

                    }
            } catch (JSONException e)
            {
                ExceptionMessage.exceptionLog(FuelReport.this, this
                                .getClass().toString() + " " + "[createPdf()-jsonException]",
                        e.toString());
                e.printStackTrace();
            }
            doc.add(table);
            doc.add(Chunk.NEWLINE);

            Toast.makeText(getApplicationContext(), "Report Generated...", Toast.LENGTH_LONG).show();
            doc.add(new LineSeparator());
            LineSeparator sep3 = new LineSeparator();
            sep3.setOffset(0);
            doc.add(sep3);

            doc.add(Chunk.NEWLINE);

            Chunk mileage = new Chunk("Mileage:  " + avgMileage+ " KM", font1);
            doc.add(new Paragraph(mileage));


        } catch (DocumentException de)
        {
            ExceptionMessage.exceptionLog(FuelReport.this, this
                            .getClass().toString() + " " + "[ createPdf()-DocumentException]",
                    de.toString());
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e)
        {
            ExceptionMessage.exceptionLog(FuelReport.this, this
                            .getClass().toString() + " " + "[ createPdf()-IOException]",
                    e.toString());
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }
    }

    public void FuelReport( String fromdate, String todate, String vehicleNum) {
        SendToWebService send = new SendToWebService(this, mFuelReport);
        try {
            send.execute("36", "GetFuelReport", fromdate, todate, vehicleNum);

        } catch (Exception e) {
            Toast.makeText(this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString() + " " + "[deletePayment]",
                    e.toString());
        }
    }

    @Override
    public void onFuelReport(String response) {
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
                if (response != null && response.contains("OK")) {
                    createPDF(response);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

                    File file = new File(path, "FuelReport.pdf");

                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    startActivity(intent);
                } else
                {
                    ald.alertDialog(FuelReport.this,"Fuel record not found from " + fromDate + " to " + toDate + " !");
                }
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[onFuelReport]",
                    e.toString());
        }

    }


    // To-Date validation | to-date should not be greater than today's date
    public void toDateValidation()
    {
        try{
            String today = toDate;
            DateFormat format = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
            dateToday = format.parse(today);

            Calendar calendar = Calendar.getInstance();
            Date tomo = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            DateFormat format2 = new SimpleDateFormat("yyyy-M-dd");
            String tomDate = format2.format(tomorrow);
            nextday = format.parse(tomDate);

            Date preFromDate = format.parse(fromDate);

            if(dateToday.after(nextday)||dateToday.equals(nextday))
            {
                ald.alertDialog(FuelReport.this,"To Date cannot be greater than Today's Date !");
                toDatetxt.setText("click here");
            }

            else if(dateToday.before(preFromDate)){
                ald.alertDialog(FuelReport.this,"To Date cannot be less than From Date !");
                toDatetxt.setText("click here");
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(FuelReport.this, this
                            .getClass().toString() + " " + "[toDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
    }
    // From-Date validation | to-date should not be greater than today's date
    public void fromDateValidation()
    {
        try{
            String today = fromDate;
            DateFormat format = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
            dateToday = format.parse(today);

            Calendar calendar = Calendar.getInstance();
            Date tomo = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            DateFormat format2 = new SimpleDateFormat("yyyy-M-dd");
            String tomDate = format2.format(tomorrow);
            nextday = format.parse(tomDate);
            if(dateToday.after(nextday)||dateToday.equals(nextday))
            {
                ald.alertDialog(FuelReport.this,"From Date cannot be greater than Today's Date !");
                fromDatetxt.setText("click here");


            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(FuelReport.this, this
                            .getClass().toString() + " " + "[fromDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
    }


    private void fuelAlertDialog() {
        final Dialog dialog = new Dialog(this);
        //   dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dilog_graph_report_layout);
        //    dialog.setTitle("SELECT OPTIONS");
        LinearLayout graph = (LinearLayout) dialog.findViewById(R.id.dialogGraph);
        LinearLayout report = (LinearLayout) dialog.findViewById(R.id.dialogReport);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent graphIntent = new Intent(FuelReport.this, LineChart.class);
                graphIntent.putExtra("fromDate", fromDate);
                graphIntent.putExtra("toDate", toDate);
                graphIntent.putExtra("vehicleNo", selectedFromList);
                startActivity(graphIntent);
                dialog.cancel();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent reportIntent = new Intent(FuelReport.this, TrackReportActivity.class);
//                startActivity(reportIntent);
                FuelReport(fromDate, toDate, selectedFromList);
                dialog.cancel();
            }
        });
        dialog.show();
    }

}
