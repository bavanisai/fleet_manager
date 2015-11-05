package com.example.TrackingModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.ITrackingReport;

import com.example.PaymentModule.PaymentDriver;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class TrackReportActivity extends ActionBarActivity implements View.OnClickListener,ITrackingReport {
    private TextView fromDatetxt;
    private TextView toDatetxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    LinearLayout noDataLayout;
    private SimpleDateFormat dateFormatter;
    String date,status,speed,time,address;
    String selectedFromList,fromDate,toDate,currentTime;
    String parsedString = null;
    Calendar fdate,tdate;
    Toolbar toolbar;
    Font head_main = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    final ITrackingReport mTrackingReport = this;
    ListView vehnum ;
    int page;
    CustomAlertDialog ald;
    Date dateToday, nextday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_report);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("TRACKING REPORT");
        setSupportActionBar(toolbar);

        currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        DBAdapter db = new DBAdapter(this);
        ald=new CustomAlertDialog();
        vehnum = (ListView)findViewById(R.id.TrackinglistViewVehicleNum);
        noDataLayout=(LinearLayout)findViewById(R.id.inboxLinearL);
        db.open();

        Cursor vehicleCursor = db.getAllData(DBAdapter.getVehicleDetails());
        if(vehicleCursor.getCount()>0)
        {
            noDataLayout.setVisibility(View.GONE);
            vehnum.setVisibility(View.VISIBLE);
        // LIST OF VEHICLES
        String from[] = {DBAdapter.getKeyVehicleNo()};
        int to[] = {R.id.vehNumbersListViewTrackVehicle};
        SimpleCursorAdapter num = new SimpleCursorAdapter(this, R.layout.account3, vehicleCursor, from, to, 0);

        vehnum.setAdapter(num);
        db.close();
        }else{

            noDataLayout.setVisibility(View.VISIBLE);
            vehnum.setVisibility(View.GONE);
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.alert_nodata);
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
               // all type date validation
                if (fromDatetxt.getText().toString().equals("click here") && toDatetxt.getText().toString().equals("click here"))
                {
                    ald.alertDialog(TrackReportActivity.this,"Please select From date and To date !");
                }
                else if(toDatetxt.getText().toString().equals("click here"))
                {
                    ald.alertDialog(TrackReportActivity.this, "Please select To date !");
                }
                else
                {try {
                        trackReport(fromDate, toDate, selectedFromList);
                    }
                    catch (Exception e)
                    {
                        ExceptionMessage.exceptionLog(getApplicationContext(),this
                                .getClass().toString() + " "
                                + "[vehnum.setOnItemClickListener-Exception]", e.toString());
                        Toast.makeText(getApplicationContext(), "Please Install Adobe Reader", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        findViewsById();
        setDateTimeField();

    }

    private void findViewsById() {
        fromDatetxt = (TextView) findViewById(R.id.textClick1);
        fromDatetxt.setInputType(InputType.TYPE_NULL);
        fromDatetxt.requestFocus();

        toDatetxt = (TextView) findViewById(R.id.textclick2);
        toDatetxt.setInputType(InputType.TYPE_NULL);
    }

    private void setDateTimeField() {
        fromDatetxt.setOnClickListener(this);
        toDatetxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDatetxt.setText(dateFormatter.format(newDate.getTime()));
                fromDate=fromDatetxt.getText().toString();
                fdate=newDate;
                fromDateValidation();
                System.out.println(fromDate);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDatetxt.setText(dateFormatter.format(newDate.getTime()));
                toDate=toDatetxt.getText().toString();
                tdate=newDate;
                toDateValidation();
                System.out.println(toDate);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


    @Override
    public void onClick(View view) {
      if(view==fromDatetxt)
      {
          fromDatePickerDialog.show();
      }
        if(view==toDatetxt) {
            if (fromDatetxt.getText().toString().equals("click here") && toDatetxt.getText().toString().equals("click here")) {
                ald.alertDialog(TrackReportActivity.this, "Please select From date !");
            }
            else
                toDatePickerDialog.show();
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
            header[0] = new Chunk("From : " + fromDate, font1);
            header[1]= new Chunk("To      : " + toDate, font1);

        }

        public void onStartPage(PdfWriter writer, Document document)
        {
            pageNumber++;
            try {
                Rectangle rect = writer.getBoxSize("art");
                int a=writer.getPageNumber();
                switch(a/a) {
                    case 1:
                        Chunk headReport = new Chunk("Tracking Report", head_main);
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
                ExceptionMessage.exceptionLog(getApplicationContext(),this
                        .getClass().toString() + " "
                        + "onStartPage()-DocumentException]", e.toString());
                e.printStackTrace();
            }

        }



        public void onEndPage(PdfWriter writer, Document document)
        {

            String currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Fleet Manager" + " / " + currentTime), 130, document.bottom()-20, 0);
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("page " + document.getPageNumber()+" of "+page), 550, document.bottom()-20, 0);
        }
    }
    public void createPDF(String response) {
        Document doc = new Document(PageSize.A4,20, 20, 120, 60);
        //doc.setMargins(2,2,2,2);

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            Log.d("PDFCreator", "PDF Path: " + path);

            File file = new File(dir, "TrackingReport.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter w = PdfWriter.getInstance(doc, fOut);
            Rectangle rect = new Rectangle(30, 30, 550, 780);
            w.setBoxSize("art", rect);
            //initializing class which containing header footer
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            w.setPageEvent(event);

            doc.open();
            doc.add(Chunk.NEWLINE);

            //pdf table with 5 column
            PdfPTable table = new PdfPTable(5);
            table.setSpacingBefore(30f);
            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            table.getDefaultCell().setBorderWidth(0);
            table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Chunk date1 = new Chunk("DATE", font1);
            Chunk time1 = new Chunk("TIME", font1);
            Chunk address1 = new Chunk("ADDRESS", font1);
            Chunk speed1 = new Chunk("SPEED", font1);
            Chunk status1 = new Chunk("STATUS", font1);

            PdfPCell cell = new PdfPCell(new Paragraph(date1));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(time1));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(address1));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(speed1));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph(status1));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);


            try {
			String statuschk=null;
			String jsonData =null;
               // Create the root JSONObject from the JSON string.
               // JSONObject jsonRootObject = new JSONObject(response);
				JSONObject jsonResponse = new JSONObject(response);

                //Get the instance of JSONArray that contains JSONObjects
                //JSONArray jsonArray = jsonRootObject.optJSONArray("d");
				 jsonData = jsonResponse.getString("d");
				 
				 JSONArray trackArray = new JSONArray(jsonData);
                if(trackArray.length()/40>=0)
                {
                    page=trackArray.length()/40;
                    page++;
                }
				 
				 JSONObject status2 = trackArray.getJSONObject(0);
				 statuschk = status2.getString("status").trim();
				 
				  if (statuschk.equals("invalid authkey")) {
                ExceptionMessage.exceptionLog(this, this.getClass()
                        .toString(), statuschk);
				} else if (statuschk.equals("vehicle number does not exist")) {
					ExceptionMessage.exceptionLog(this, this.getClass()
							.toString(), statuschk);
				}
				 else if (statuschk.equals("data does not exist")) {
					Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
				}
				else if (statuschk.equals("OK")){

                //Iterate the jsonArray and print the info of JSONObjects
                for (int i = 1; i < trackArray.length(); i++) {
                    JSONObject jsonObject = trackArray.getJSONObject(i);
                    JSONArray names = jsonObject.names();
                    DecimalFormat numberFormat = new DecimalFormat("#.00");

                    for (int k = 0; k < names.length(); k++) {
                        date = jsonObject.optString("deviceDate").toString();
                        time = jsonObject.optString("deviceTime").toString();
                        address = jsonObject.optString("address").toString();
                       // mAmount = numberFormat.format(number);
                         speed = jsonObject.optString("speed").toString();
                       // speed=numberFormat.format(Double.valueOf(sspeed));
                        status = jsonObject.optString("vehicleStatus").toString();
						if(status.equals("1"))
						{
						status="ON";
						}
						else{
						status="OFF";
						}
                    }
                    String[] parts =date.split("T");
                    table.addCell(parts[0]);
                    table.addCell(time);
                    table.addCell(address);
                    table.addCell(speed);
                    table.addCell(status);
                }
				}

            } catch (JSONException e)
            {
                ExceptionMessage.exceptionLog(this,this
                        .getClass().toString() + " "
                        + "[createPdf()-JSONException]", e.toString());
                        e.printStackTrace();
            }
            doc.add(table);
            Toast.makeText(getApplicationContext(), "Created...", Toast.LENGTH_LONG).show();

        } catch (DocumentException de)
        {
            ExceptionMessage.exceptionLog(this,this
                    .getClass().toString() + " "
                    + "[createPdf()-DocumentException]", de.toString());
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e)
        {
            ExceptionMessage.exceptionLog(this,this
                    .getClass().toString() + " "
                    + "[createPdf()-IOException]", e.toString());
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }
    }

    public void trackReport(String fromdate, String todate, String vehicleNum)
{
    SendToWebService send=new SendToWebService(this, mTrackingReport);
    try {
        send.execute("40", "GetTrackingReport", fromdate, todate, vehicleNum);

    } catch (Exception e) {
        Toast.makeText(this, "Try after sometime...",
                Toast.LENGTH_SHORT).show();
        ExceptionMessage.exceptionLog(this, this.getClass().toString() + " " + "[trackReport]", e.toString());
    }
}

    @Override
    public void onTrackingReport(String response)
    {
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
            }
            else
            {
                if (response != null && response.contains("OK")) {
                    createPDF(response);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF01";

                    File file = new File(path, "TrackingReport.pdf");

                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    startActivity(intent);
                } else {
                    ald.alertDialog(TrackReportActivity.this,"Tracking record not found from " + fromDate + " to " + toDate + " !");
                }
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[onManageAdvanceDetailsTable]",
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

            if(dateToday.after(nextday) || dateToday.equals(nextday))
            {
                ald.alertDialog(this,"To Date cannot be greater than Today's Date !");
                toDatetxt.setText("click here");
            }

            else if(dateToday.before(preFromDate)){
                ald.alertDialog(TrackReportActivity.this,"To Date cannot be less than From Date !");
                toDatetxt.setText("click here");
            }
        }

        catch (Exception e)
        {
            ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[toDateValidation()]",
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
            if(dateToday.after(nextday) || dateToday.equals(nextday))
            {
                ald.alertDialog(this,"From Date cannot be greater than Today's Date !");
                fromDatetxt.setText("click here");

            }
        }

        catch (Exception e)
        {
            ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[fromDateValidation()]",
                    e.toString());
            e.printStackTrace();
        }
    }



}




