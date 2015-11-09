package com.example.AbsenceReport;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.Interface.ILeaveEntryDriver;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverList extends Fragment implements ILeaveEntryDriver {
    Button btnSend;
    TextView btnDate;
    ListView lvEmp;
    DBAdapter db;
    StringBuilder str;
    String itemsSelected;
    CheckedTextView ck;
    int year, month, day;
    final ILeaveEntryDriver mLeaveEntryDriver = this;
    ArrayList<String> driverList;
    LinearLayout noDataLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driverlist, container,
                false);
        btnDate = (TextView) view.findViewById(R.id.btnFgmntLeaveDate);
        btnSend = (Button) view.findViewById(R.id.btnSendAbsentees);
        noDataLayout = (LinearLayout)view.findViewById(R.id.inboxLinearL);
        lvEmp = (ListView) view.findViewById(R.id.lvEmpList);


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Show current date

        btnDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("-").append(month + 1).append("-")
                .append(day).append(" "));

        listViewEvents();

        return view;
    }

    private void listViewEvents() {
        btnDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker();

            }
        });
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    DBAdapter db = new DBAdapter(getActivity());
                    db.open();
                    itemsSelected = "Selected items: \n";
                    String LocalEmpId = "";
                    for (int i = 0; i < lvEmp.getCount(); i++) {
                        if (lvEmp.isItemChecked(i)) {
                            itemsSelected += lvEmp.getItemAtPosition(i) + "\n";
                            String driverName = (String) lvEmp.getItemAtPosition(i);
                            LocalEmpId += db.checkDrvierTableforDataExist(driverName) + ",";
                        }
                        if (i == lvEmp.getCount() - 1) {
                            str = new StringBuilder(LocalEmpId);
                            str.deleteCharAt(str.length() - 1);
                        }
                    }

                    SendToWebService send = new SendToWebService(getActivity(),
                            mLeaveEntryDriver);
                    try {
                        send.execute("22", "Absence", str.toString().trim(),
                                btnDate.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(getActivity().getBaseContext(),
                                "try after sometime....", Toast.LENGTH_SHORT)
                                .show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                .getClass().toString()
                                + " "
                                + "[btnSend.setOnClickListener]", e.toString());
                    }
                    db.close();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Please select the Driver", Toast.LENGTH_LONG).show();
                }
            }
        });

        populateListView();

    }

    private void populateListView() {
        db = new DBAdapter(getActivity());
        db.open();
        driverList = new ArrayList<String>();

        Cursor driverCursor = db.getAllEmp(DBAdapter.getEmployeeDetails(),
                "Driver");
        if (driverCursor.getCount() > 0) {
            noDataLayout.setVisibility(View.GONE);
            lvEmp.setVisibility(View.VISIBLE);
            if (driverCursor.moveToFirst()) {
                do {
                    try {
                        driverList.add(driverCursor.getString(driverCursor
                                .getColumnIndex("Name")));
                    } catch (Exception e) {
                        ExceptionMessage.exceptionLog(getActivity(),
                                this.getClass().toString() + " "
                                        + "[populateListView]", e.toString());
                    }
                } while (driverCursor.moveToNext());
            }

            lvEmp.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.account5, R.id.checktv_title, driverList));

            lvEmp.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            lvEmp.setTextFilterEnabled(true);
            lvEmp.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View selectedView,
                                        int position, long arg3) {
                    ck = (CheckedTextView) selectedView.findViewById(R.id.checktv_title);
                    ck.toggle();

                }
            });
            db.close();
        }else {
            //if listview is empty at first time
            noDataLayout.setVisibility(View.VISIBLE);
            lvEmp.setVisibility(View.GONE);
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.nodata);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.topMargin = 100;
            imageView.setLayoutParams(layoutParams);
            noDataLayout.addView(imageView);

            TextView textView=new TextView(getActivity());
            textView.setText("NO DRIVER LIST");
            textView.setTextSize(14);
            textView.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.gravity = Gravity.CENTER;
            layoutParams1.topMargin = 20;
            textView.setLayoutParams(layoutParams1);
            noDataLayout.addView(textView);
        }
    }
    private void showDatePicker() {
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
        date.show(getFragmentManager(), "Date Picker");
    }

    OnDateSetListener ondate = new OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            btnDate.setText(String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-"
                    + String.valueOf(dayOfMonth));

        }
    };

    @Override
    public void onTaskCompleteDriverLeave(String result) {
        if (result.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            cd.ConnectingToInternet();
        }

        if (result.contains("refused") || result.contains("timed out")) {
            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
            builder.create().show();

        } else if (result.contains("java.net.SocketTimeoutException")) {

            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.lowconnection3);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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
		
		String response = jsonParsing1(result);
            if(response.equals("updated")){
                Toast.makeText(getActivity(), "Driver leave updated to server",
                        Toast.LENGTH_LONG).show();
						}
            else{
                Toast.makeText(getActivity(), "Try after sometime.....",
                        Toast.LENGTH_LONG).show();
						}
        }

    }
	
	 public String jsonParsing1(String response) {
        String jsonData = null;
        String status = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);
                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                status = d.getString("status").trim();
                return status;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing1]", e.toString());
            }
        return status;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            LeaveMainActivity.pos=1;
        }

    }

}
