package com.example.tripmodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IGetParticularTripData;
import com.example.Interface.IGetTripStatus;
import com.example.Interface.ITripListFragment;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;
import com.example.anand_roadwayss.Welcome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TripListDetailsActivity extends Activity implements View.OnClickListener, IGetParticularTripData,ITripListFragment,IGetTripStatus {
    String inputvoucher,inputvehicle;
    TextView txtSource, txtDriver1;//txtDriver2;
    Button btnEdit,btnAdd,btnDelete;
    ListView destinationList;
    ArrayList<String> destList = new ArrayList<>();
    String val,data;
    int pos;
    final IGetParticularTripData mGetParticularTripData=this;
    String sourceName,cleaner,driver,voucher;
    NewMultipleDestinationArray mList;
    public static ArrayList<NewMultipleDestinationArray> multipleArrayList = new ArrayList<>();
    final ITripListFragment mTripListFragment=this;
    final IGetTripStatus mGetTripStatus=this;
    ArrayList<String> editTripData;
    Toolbar toolbar;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        inputvoucher=getIntent().getExtras().getString("voucher");
        inputvehicle=getIntent().getExtras().getString("vehicle");
        bindData();
        multipleArrayList.clear();
        destList.clear();

        toolbar = (Toolbar) findViewById(R.id.tool);
        // toolbar.setTitle("ADD DESTINATION");
        img = (ImageView) findViewById(R.id.arrow_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TripListDetailsActivity.this, Welcome.class);
                startActivity(intent);
                multipleArrayList.clear();
            }
        });
        getParticularTripData();
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);



    }

    private void bindData() {
        txtSource = (TextView)findViewById(R.id.tripDetailsActivityTvSource);
        txtDriver1 = (TextView)findViewById(R.id.tripDetailsActivityTvDriver1);
        //txtDriver2 = (TextView)findViewById(R.id.tripDetailsActivityTvDriver2);
        destinationList=(ListView)findViewById(R.id.tripDetailsActivityLvDestination);
        btnEdit = (Button)findViewById(R.id.tripDetailsActivityDestinationBtnEditDestination);
        //  btnAdd = (Button)findViewById(R.id.tripDetailsActivityDestinationBtnAddDestination);
        btnDelete = (Button)findViewById(R.id.tripDetailsActivityDestinationBtnDeleteDestination);
    }

    public void getParticularTripData(){
        try {
            SendToWebService send = new SendToWebService(this, mGetParticularTripData);
            send.execute("44", "GetSubTrips", inputvoucher, inputvehicle);
        }

        catch(Exception e){
            Toast.makeText(TripListDetailsActivity.this,
                    "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage
                    .exceptionLog(
                            getApplicationContext(),
                            this.getClass().toString()
                                    + " "
                                    + "[regBtnRegister.setOnClickListener]",
                            e.toString());
        }
    }

    @Override
    public void onGetParticularTripData(String response) {
try {
    if (response.equals("No Internet")) {
        ConnectionDetector cd = new ConnectionDetector(TripListDetailsActivity.this);
        cd.ConnectingToInternet();
    } else if (response.contains("refused") || response.contains("timed out")) {
        ImageView image = new ImageView(TripListDetailsActivity.this);
        image.setImageResource(R.drawable.lowconnection3);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                TripListDetailsActivity.this).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                }).setView(image);
        builder.create().show();

    } else if (response.contains("java.net.SocketTimeoutException")) {

        ImageView image = new ImageView(TripListDetailsActivity.this);
        image.setImageResource(R.drawable.lowconnection3);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                TripListDetailsActivity.this).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                }).setView(image);
        builder.create().show();

    } else {
        jsonParsing(response);

    }
}

catch (Exception e){
    ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
            .toString() + " " + "[onGetParticularTripData]", e.toString());
}

    }

    public void jsonParsing(String response){
        try {
        JSONObject jsonResponse = new JSONObject(response);
            String statusCheck=null;
        // getting the data with tag d

            String jsonData = jsonResponse.getString("d");
            JSONArray tripDetailsArray = new JSONArray(jsonData);

            JSONObject c = tripDetailsArray.getJSONObject(0);
            statusCheck=c.getString("status");

            if (statusCheck.equals("invalid authkey")) {
                ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                        .toString(), statusCheck);
            } else if (statusCheck.equals("vehicle number does not exist")) {
                ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                        .toString(), statusCheck);
            } else if (statusCheck.equals("OK")) {

                JSONObject sourceDetails = tripDetailsArray.getJSONObject(1);
                sourceName = sourceDetails.getString("sourceName");
                cleaner = sourceDetails.getString("cleaner");
                driver=sourceDetails.getString("driver");
                voucher = sourceDetails.getString("voucherNumber");

                JSONArray destDetails = tripDetailsArray.getJSONArray(2);

                int key = 1;

                for (int i = 0; i < destDetails.length(); i++) {
                    JSONObject trip = destDetails.getJSONObject(i);

                    String vehicleNo=trip.getString("vehicleNumber");
                    String tripOrder=trip.getString("tripOrder");
                    String sourceOrLastDest=trip.getString("sourceName");
                    String destination = trip.getString("destinationName");
                    String distance=trip.getString("distance");
                    String product=trip.getString("product");
                    String quantity=trip.getString("quantity");
                    String tripPaymentType=trip.getString("paymentType");
                    String payment_km = trip.getString("paymentKm");
                    String amount=trip.getString("amount");
                    String subVoucher=trip.getString("subVoucher");
                    String route=trip.getString("route");
                    String tripStatus=trip.getString("tripStatus");
//                    String lastDest=destination;

//                    if (i == 0) {
//                         lastDest = sourceName;
//                    }
//
//                    else{
//                         lastDest=lastDest;
//                    }

                    mList = new NewMultipleDestinationArray(vehicleNo, product, quantity, destination, distance, route, amount, tripPaymentType, payment_km, sourceOrLastDest,tripOrder,subVoucher);
                    int pos=multipleArrayList.size();
                    multipleArrayList.add(pos, mList);
                }


                viewData();
                setAllData();



            }

        } catch (JSONException e) {
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                    .toString() + " " + "[jsonParsing]", e.toString());
        }

    }

    public  void viewData(){
        try {
            int i, j;
            String temp = "";
            // destList = new ArrayList<String>();
            for (i = 0; i < multipleArrayList.size(); i++) {

                String s = multipleArrayList.get(i).toString();
                String[] arr = s.split(",");
                //for (j = 0; j < arr.length; j++) {
                temp = arr[3];
                // }
                destList.add(temp);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                    .toString() + " " + "[viewData]", e.toString());
        }
    }

    private void setAllData(){

        try {
            txtSource.setText(sourceName);
            txtDriver1.setText(driver);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, destList);
            destinationList.setAdapter(adapter);
            destinationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pos = position;
                    // val = MultipleDestinationActivity.arrayList.get(position).toString();
                    val = multipleArrayList.get(position).toString();
                    // Toast.makeText(DestinationActivity.this, "position" + position + " mylist[" + val + "]", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.toString();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tripDetailsActivityDestinationBtnEditDestination:
                try {
                    editTripData = new ArrayList<String>();
                    String[] arr = val.split(",");
                    String voucher = arr[11];
                    for (int i = 0; i < arr.length; i++) {
                        data = arr[i];
                        editTripData.add(data);
                    }
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this
                                    .getClass().toString() + " " + "[tripDetailsActivityDestinationBtnEditDestination]",
                            e.toString());
                }
                try {
                    SendToWebService send = new SendToWebService(TripListDetailsActivity.this,
                            mGetTripStatus);
                    send.execute("45", "GetTripStatus", voucher);
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this
                                    .getClass().toString() + " " + "[deleteSubTrip]",
                            e.toString());
                }
                break;

//            case R.id.tripDetailsActivityDestinationBtnAddDestination:
//                Intent intent=new Intent(TripListDetailsActivity.this,MultipleDestinationActivity1.class);
//                intent.putExtra("editPosition",pos);
//                startActivity(intent);
//                break;

            case R.id.tripDetailsActivityDestinationBtnDeleteDestination:

                deleteSubTrip();
               // Toast.makeText(TripListDetailsActivity.this, "Destination" + pos + " completeList[" + val + "]" +" Removed", Toast.LENGTH_LONG).show();
                break;
        }
    }


    public void deleteSubTrip(){
        try {
        String[] arr=val.split(",");
        String voucher=arr[11];
        String sflag="0";


            SendToWebService send = new SendToWebService(TripListDetailsActivity.this,
                    mTripListFragment);
            send.execute("21", "CloseVehicleOrDeleteTrip", voucher, sflag);
        }

        catch (Exception e) {
            Toast.makeText(TripListDetailsActivity.this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this
                            .getClass().toString() + " " + "[deleteSubTrip]",
                    e.toString());
        }
    }

    @Override
    public void onDeleteTrip(String response) {

        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(TripListDetailsActivity.this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused")) {
                ImageView image = new ImageView(TripListDetailsActivity.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        TripListDetailsActivity.this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else if (response.contains("java.net.SocketTimeoutException")) {

                ImageView image = new ImageView(TripListDetailsActivity.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        TripListDetailsActivity.this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else {
                // OneJSONValue one = new OneJSONValue();
                String result = jsonParsing1(response);
                deleteTripData(result);
            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                    .toString() + " " + "[onDeleteTrip]", e.toString());
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
                return status;

            } catch (JSONException e) {
                if (e.toString().contains("refused")) {
                    ImageView image = new ImageView(TripListDetailsActivity.this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            TripListDetailsActivity.this).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
                    builder.create().show();

                } else if (e.toString().contains(
                        "java.net.SocketTimeoutException")) {

                    ImageView image = new ImageView(TripListDetailsActivity.this);
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            TripListDetailsActivity.this).setPositiveButton("OK",
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

    public void deleteTripData(String result) {
        try {
            if (result.equals("deleted")||result.equals("closed") ) {
                //MultipleDestinationActivity.arrayList.remove(pos);
                Toast.makeText(TripListDetailsActivity.this, "Trip Deleted",
                        Toast.LENGTH_SHORT).show();
                Intent i=new Intent(this,TripActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(TripListDetailsActivity.this, "Try After Sometime",
                        Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                        .toString(), "Try after sometime...");

            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                    .toString() + " " + "[deleteTripData]", e.toString());
        }
    }

    @Override
    public void onGetTripStatus(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(TripListDetailsActivity.this);
                cd.ConnectingToInternet();
            } else if (response.contains("refused")) {
                ImageView image = new ImageView(TripListDetailsActivity.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        TripListDetailsActivity.this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else if (response.contains("java.net.SocketTimeoutException")) {

                ImageView image = new ImageView(TripListDetailsActivity.this);
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        TripListDetailsActivity.this).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else {
                // OneJSONValue one = new OneJSONValue();
                String result = jsonParsing1(response);
                processData(result);

            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                    .toString() + " " + "[onGetTripStatus]", e.toString());
        }
    }

    public void processData(String result){
        try {
            if (result.equals("on trip")) {

                Toast.makeText(TripListDetailsActivity.this, "Vehicle is in trip Cannot be changed",
                        Toast.LENGTH_SHORT).show();
            } else if (result.equals("off trip")) {
                Intent in = new Intent(TripListDetailsActivity.this, MultipleDestinationActivity1.class);
                in.putExtra("editPosition",pos);
                in.putExtra("source",sourceName);
                in.putExtra("clnr",cleaner);
                in.putExtra("drivr",driver);
                in.putExtra("veh",inputvehicle);
                in.putExtra("editTripData",editTripData);
                startActivity(in);
            }
            else {
                Toast.makeText(TripListDetailsActivity.this, "Try After Sometime",
                        Toast.LENGTH_SHORT).show();
                ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                        .toString(), "Try after sometime...");

            }

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                    .toString() + " " + "[deleteTripData]", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        try {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }
        }

        catch (Exception e){
            ExceptionMessage.exceptionLog(TripListDetailsActivity.this, this.getClass()
                    .toString() + " " + "[onOptionsItemSelected]", e.toString());
        }
        return super.onOptionsItemSelected(item);
    }



}
