package com.example.tripmodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.ITripManageFragment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MultipleDestinationActivity1 extends Activity implements View.OnClickListener,ITripManageFragment {

    EditText multipleDestinationEdtDistance, multipleDestinationEdtQuantity,
            multipleDestinationEdtProduct, multipleDestinationEdtRouteName, multipleDestinationEdtRent, multipleDestinationEdtPercentOrPayPerKM;
    TextView multipleDestinationTvVehNo, multipleDestinationTvLastDest;
    Spinner multipleDestinationSpinnerDestination, multipleDestinationSpinnerPaymentType;
    Button multipleDestinationBtnSave,multipleDestinationBtnAdd;
    String selDest,formattedDate;
    DBAdapter db;
    List<String> Destination;
    String mTypeOfPayment,mTripOrder,mSubVoucher, mProduct, mQuantity, mDistance, mRouteName, mRent, mPercentOrPayPerKM,
            mDestination, mPayType, mVehNo, mLastDestination,mDriver,mCleaner,mVehicle,mSource;
    int updatePosition,tripOrderNum;
    ArrayList<String> editList;
    NewMultipleDestinationArray mList,finalList;
    public ArrayList<NewMultipleDestinationArray> finalArrayList = new ArrayList<>();
    final ITripManageFragment mTripManageFragment = this;
    ArrayAdapter<String> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_destination);
        bindData();
        // multipleDestinationBtnView.setText("Cancel");
        multipleDestinationBtnSave.setVisibility(View.GONE);
        db = new DBAdapter(this);

        String[] pay = {"Commission", "KM Travelled"};
        selDest = "Select Destination";
        loadDestSpinnerData(null);
        ArrayAdapter<String> payAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pay);
      //  payAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        multipleDestinationSpinnerPaymentType.setAdapter(payAd);
        multipleDestinationSpinnerPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTypeOfPayment = multipleDestinationSpinnerPaymentType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        try {
            updatePosition = getIntent().getIntExtra("editPosition", 0);
            mDriver=getIntent().getStringExtra("drivr");
            mCleaner=getIntent().getStringExtra("clnr");
            mSource=getIntent().getStringExtra("source");
            mVehicle=getIntent().getStringExtra("veh");


            editList = (ArrayList<String>) getIntent().getSerializableExtra("editTripData");
            if (editList != null) {
                int i;
                String s = editList.toString();
                s = s.replaceAll("\\[", "").replaceAll("\\]", "");
                String[] arr = s.split(",");

                //  for (i = 0; i < arr.length; i++) {
                    multipleDestinationTvVehNo.setText(arr[0]);
                    multipleDestinationEdtProduct.setText(arr[1]);
                    multipleDestinationEdtQuantity.setText(arr[2]);
                    multipleDestinationSpinnerDestination.setSelection(Destination.indexOf(arr[3].trim()));
                    multipleDestinationEdtDistance.setText(arr[4]);
                    multipleDestinationEdtRouteName.setText(arr[5]);
                    multipleDestinationEdtRent.setText(arr[6]);
                    multipleDestinationSpinnerPaymentType.setSelection(getIndex(multipleDestinationSpinnerPaymentType, arr[7].trim()));
                    multipleDestinationEdtPercentOrPayPerKM.setText(arr[8]);
                    multipleDestinationTvLastDest.setText(arr[9]);
                    mTripOrder=arr[10];
                    tripOrderNum = Integer.parseInt(arr[10].replaceAll("[\\D]", ""));
                //  tripOrderNum=Integer.parseInt(arr[10]);
                    mSubVoucher=arr[11];
                    multipleDestinationBtnAdd.setText("UPDATE");
                    dataAdapter.remove(arr[3].toString());
                // }
            } else {
               // Toast.makeText(MultipleDestinationActivity.this, "no value", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString() + " " + "[onCreateView]", e.toString());
        }

        multipleDestinationBtnAdd.setOnClickListener(this);

    }

    private void bindData() {
        try {
            multipleDestinationTvVehNo = (TextView) findViewById(R.id.activityMultipleDestinationTvVehicleNum);
            multipleDestinationTvLastDest = (TextView) findViewById(R.id.activityMultipleDestinationTvSrcDest);
            multipleDestinationEdtProduct = (EditText) findViewById(R.id.activityMultipleDestinationEdtProduct);
            multipleDestinationEdtQuantity = (EditText) findViewById(R.id.activityMultipleDestinationEdtQuantity);
            multipleDestinationEdtDistance = (EditText) findViewById(R.id.activityMultipleDestinationEdtDistance);
            multipleDestinationEdtRouteName = (EditText) findViewById(R.id.activityMultipleDestinationEdtRouteName);
            multipleDestinationEdtRent = (EditText) findViewById(R.id.activityMultipleDestinationEdtRent);
            multipleDestinationEdtPercentOrPayPerKM = (EditText) findViewById(R.id.activityMultipleDestinationEdtPercentageOrPerKM);
            multipleDestinationSpinnerDestination = (Spinner) findViewById(R.id.activityMultipleDestinationSpinnerDestination);
            multipleDestinationSpinnerPaymentType = (Spinner) findViewById(R.id.activityMultipleDestinationSpinnerPaymentType);
            multipleDestinationBtnSave = (Button) findViewById(R.id.activityMultipleDestinationBack);
            //    multipleDestinationBtnView = (Button) findViewById(R.id.activityMultipleDestinationBtnView);
            multipleDestinationBtnAdd = (Button) findViewById(R.id.activityMultipleDestinationBtnAdd);
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString() + " " + "[bindData]", e.toString());
        }
    }

    private void loadDestSpinnerData(Object object) {
        try {
            db.open();
            Destination = db.getLocationForSpinner("Destination");
            Destination.add(0, selDest);
            db.close();
            // Creating adapter for spinner
             dataAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item,
                    Destination);
            // Drop down layout style - list view with radio button
         //   dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            multipleDestinationSpinnerDestination.setAdapter(dataAdapter);
            if (Destination.contains(object) && object != null) {
                int position = Destination.indexOf(object);
                multipleDestinationSpinnerDestination.setSelection(position);
            }
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(this, this.getClass()
                    .toString(), e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass()
                    .toString(), e.toString());
        }
    }


    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
try {
    for (int i = 0; i < spinner.getCount(); i++) {
        if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
            index = i;
            break;
        }
    }
}
catch (Exception e){
    ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
            .toString() + " " + "[getIndex]", e.toString());
}
        return index;
    }


    public  void SelectSpinnerItemByValue(Spinner spnr, long value)
    {
        try {
            SimpleCursorAdapter adapter = (SimpleCursorAdapter) spnr.getAdapter();
            for (int position = 0; position < adapter.getCount(); position++) {
                if (adapter.getItemId(position) == value) {
                    spnr.setSelection(position);
                    return;
                }
            }
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString() + " " + "[getIndex]", e.toString());
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activityMultipleDestinationBack:
                finish();
                break;
//            case R.id.activityMultipleDestinationBtnView:
//                viewData();
//                break;
            case R.id.activityMultipleDestinationBtnAdd:
                if (multipleDestinationBtnAdd.getText().toString().equals("ADD")) {
                    addDataToArrayList();
                } else
                if (multipleDestinationBtnAdd.getText().toString().equals("UPDATE")) {
                    updateDataToArrayList();
                }
                break;
        }
    }

    public void addDataToArrayList() {
        try {
            String data;
            getData();
            tripOrderNum = tripOrderNum + 1;
            mTripOrder = String.valueOf(tripOrderNum);
            DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd hhmmss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            mSubVoucher = "SubTrip" + dateFormatter.format(today);

            mList = new NewMultipleDestinationArray(mVehNo, mProduct, mQuantity, mDestination, mDistance, mRouteName, mRent, mPayType, mPercentOrPayPerKM, mLastDestination, mTripOrder, mSubVoucher);
            int pos = TripListDetailsActivity.multipleArrayList.size();
            if (updatePosition != 0) {
                TripListDetailsActivity.multipleArrayList.add(updatePosition, mList);
                pos = updatePosition;
                updatePosition += 1;
            } else {
                TripListDetailsActivity.multipleArrayList.add(pos, mList);
            }
            clearViewData();

            int rowCount = TripListDetailsActivity.multipleArrayList.size();

            for (int i = 0; i < rowCount; i++) {

                String rowValue = TripListDetailsActivity.multipleArrayList.get(i).toString();
                String[] arr = rowValue.split(",");
                mVehNo = arr[0];
                mProduct = arr[1];
                mQuantity = arr[2];
                mDestination = arr[3];
                mDistance = arr[4];
                mRouteName = arr[5];
                mRent = arr[6];
                mPayType = arr[7];
                mPercentOrPayPerKM = arr[8];
                mLastDestination = arr[9];
                if (i > pos) {
                    tripOrderNum = tripOrderNum + 1;
                    mTripOrder = String.valueOf(tripOrderNum);
                } else {
                    mTripOrder = arr[10];
                }
                mSubVoucher = arr[11];

                finalList = new NewMultipleDestinationArray(mVehNo, mProduct, mQuantity, mDestination, mDistance, mRouteName, mRent, mPayType, mPercentOrPayPerKM, mLastDestination, mTripOrder, mSubVoucher);

                finalArrayList.add(i, finalList);

            }
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString() + " " + "[addDataToArrayList]", e.toString());
        }

    }


    public void updateDataToArrayList() {
        try {
            getData();
            mList = new NewMultipleDestinationArray(mVehNo, mProduct, mQuantity, mDestination, mDistance, mRouteName, mRent, mPayType, mPercentOrPayPerKM, mLastDestination, mTripOrder, mSubVoucher);
            TripListDetailsActivity.multipleArrayList.set(updatePosition, mList);
            updatePosition =updatePosition+1;
            clearViewData();

            //String rowValue = TripListDetailsActivity.multipleArrayList.get(updatePosition).toString();

           JSONObject data= getJsonObjectData();
            String updateData=data.toString();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            formattedDate = df.format(date);
            db=new DBAdapter(getApplicationContext());
            db.open();
            mDriver = db.checkDrvierTableforDataExist(mDriver.toUpperCase());
            mCleaner = db.checkCleanerTableforDataExist(mCleaner.toUpperCase());
            db.close();

            SendToWebService send = new SendToWebService(MultipleDestinationActivity1.this,
                    mTripManageFragment);

            try {
                send.execute("48", "UpdateSubTripDetails", updateData);

            } catch (Exception e) {
                ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this
                        .getClass().toString(), e.toString());
            }

        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString() + " " + "[updateDataToArrayList]", e.toString());
        }

//        multipleDestinationTvLastDest.setText(mDestination);
//        multipleDestinationBtnAdd.setText("ADD");
    }


    public JSONObject getJsonObjectData() throws JSONException {
        JSONObject obj = null;
        JSONArray jsonArray = new JSONArray();


            String rowValue = TripListDetailsActivity.multipleArrayList.get(updatePosition-1).toString();
            String[] arr = rowValue.split(",");
            obj = new JSONObject();
            try {
                obj.put("vehicleNumber", arr[0].trim());
                obj.put("product", arr[1].trim());
                obj.put("quantity", arr[2].trim());
                obj.put("destination", arr[3].trim());
                obj.put("distance", arr[4]);
                obj.put("route", arr[5].trim());
                obj.put("amount", arr[6].trim());
                obj.put("paymentType", arr[7].trim());
                obj.put("paymentKm", arr[8].trim());
                obj.put("sourceName", arr[9].trim());
                obj.put("tripOrder", arr[10].trim());
                obj.put("subVoucher", arr[11].trim());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(obj);


        JSONObject finalobject = new JSONObject();
        finalobject.put("subTrip", jsonArray);
        return finalobject;
    }
    public void getData() {
        try {
            mProduct = multipleDestinationEdtProduct.getText().toString();
            mQuantity = multipleDestinationEdtQuantity.getText().toString();
            mDestination = multipleDestinationSpinnerDestination.getSelectedItem().toString();
            mDistance = multipleDestinationEdtDistance.getText().toString();
            mRouteName = multipleDestinationEdtRouteName.getText().toString();
            mRent = multipleDestinationEdtRent.getText().toString();
            mPayType = multipleDestinationSpinnerPaymentType.getSelectedItem().toString();
            mPercentOrPayPerKM = multipleDestinationEdtPercentOrPayPerKM.getText().toString();
            mVehNo = multipleDestinationTvVehNo.getText().toString();
            mLastDestination = multipleDestinationTvLastDest.getText().toString();
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString() + " " + "[getData]", e.toString());
        }
    }

    public void clearViewData() {
        try {
            multipleDestinationEdtProduct.setText("");
            multipleDestinationEdtQuantity.setText("");
            multipleDestinationSpinnerDestination.setSelection(0);
            multipleDestinationEdtDistance.setText("");
            multipleDestinationEdtRouteName.setText("");
            multipleDestinationEdtRent.setText("");
            multipleDestinationSpinnerPaymentType.setSelection(0);
            multipleDestinationEdtPercentOrPayPerKM.setText("");
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString() + " " + "[clearViewData]", e.toString());
        }
    }

    @Override
    public void onSaveSourceDestinationDetails(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(MultipleDestinationActivity1.this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused")) {
                ImageView image = new ImageView(MultipleDestinationActivity1.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MultipleDestinationActivity1.this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else if (response.contains("java.net.SocketTimeoutException")) {

                ImageView image = new ImageView(MultipleDestinationActivity1.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MultipleDestinationActivity1.this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else {

                String parsedValue = jsonParsing1(response);
                saveTripData(parsedValue);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString(), e.toString());
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
                status = d.getString("status");
                //TripId=d.getString("tripId");
                return status;

            } catch (JSONException e) {
                if (e.toString().contains("refused")) {
                    ImageView image = new ImageView( MultipleDestinationActivity1.this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MultipleDestinationActivity1.this).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
                    builder.create().show();

                } else if (e.toString().contains("java.net.SocketTimeoutException")) {

                    ImageView image = new ImageView(MultipleDestinationActivity1.this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MultipleDestinationActivity1.this).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
                    builder.create().show();

                }
            }
        return status;
    }


    private void saveTripData(String parsedValue) {
        try {

            if (parsedValue.equals("updated")) {

                Toast.makeText(MultipleDestinationActivity1.this,
                        "Data Updated Successfully", Toast.LENGTH_SHORT)
                        .show();

                Intent i=new Intent(MultipleDestinationActivity1.this,TripActivity.class);
                startActivity(i);
                finish();

            } else if (parsedValue.equals("vehicle already in trip")) {
                Toast.makeText(MultipleDestinationActivity1.this,
                        "Vehicle Already in trip...",
                        Toast.LENGTH_SHORT).show();
            }
            else if (parsedValue.equals("driver already in trip")) {
                Toast.makeText(MultipleDestinationActivity1.this,
                        "Driver Already in trip...",
                        Toast.LENGTH_SHORT).show();
            }
            else if (parsedValue.equals("invalid input")) {
                Toast.makeText(MultipleDestinationActivity1.this,
                        "invalid inputs...",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                        .toString(), parsedValue);
                Toast.makeText(MultipleDestinationActivity1.this,
                        "Sorry Trip Not Added... Try After SomeTimes",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString(), e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(MultipleDestinationActivity1.this, this.getClass()
                    .toString(), e.toString());
        }
        //refreshActivity();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
