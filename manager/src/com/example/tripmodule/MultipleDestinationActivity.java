package com.example.tripmodule;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.example.FuelModule.FuelActivity;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.Welcome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MultipleDestinationActivity extends Activity implements View.OnClickListener {
    EditText multipleDestinationEdtDistance, multipleDestinationEdtQuantity,
            multipleDestinationEdtProduct, multipleDestinationEdtRouteName, multipleDestinationEdtRent, multipleDestinationEdtPercentOrPayPerKM;
    TextView multipleDestinationTvVehNo, multipleDestinationTvLastDest;
    Spinner multipleDestinationSpinnerDestination, multipleDestinationSpinnerPaymentType;
    Button multipleDestinationBtnBack,  multipleDestinationBtnAdd;
    String mTypeOfPayment, mProduct, mQuantity, mDistance, mRouteName, mRent, mPercentOrPayPerKM, mDestination, mPayType, mVehNo,
            mLastDestination,mTripOrder,mSubVoucher;
    DBAdapter db;
    List<String> Destination=new ArrayList<>();
    String selDest;
    public static ArrayList<MultipleDestinationArrayList> arrayList=new ArrayList<>();
    MultipleDestinationArrayList mList;
    ArrayList<String> editList;
    int updatePosition;
    String veh,dri,src;
    public static int order;
    int tripOrderNum;
    Toolbar toolbar;
    ImageView img;

    ArrayAdapter<String> dataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_destination);
        bindData();
        toolbar = (Toolbar) findViewById(R.id.tool);
       // toolbar.setTitle("ADD DESTINATION");
        img=(ImageView)findViewById(R.id.arrow_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MultipleDestinationActivity.this, Welcome.class);
                startActivity(intent);
            }
        });
        db = new DBAdapter(this);
        String[] pay={"Select Payment Mode","Commission","KM Travelled"};
        selDest = "Select Destination";

        multipleDestinationBtnBack.setOnClickListener(this);
        multipleDestinationBtnAdd.setOnClickListener(this);

        SharedPreferences UserType = getSharedPreferences(
                "RegisterName", 0);
        String UserTyp = UserType.getString("Name", "");

        if (UserTyp.equals("DEMO")) {
            try {
                Destination.add(0, selDest);
                Destination.add(1, "Mysore");
                Destination.add(2,"Mangalore");
                Destination.add(3,"Udupi");
                Destination.add(4,"Goa");

                dataAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item,
                        Destination);
                // Drop down layout style - list view with radio button
                //     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // attaching data adapter to spinner
                multipleDestinationSpinnerDestination.setAdapter(dataAdapter);

            } catch (Exception e)
            {
                ExceptionMessage.exceptionLog(this, this
                        .getClass().toString()+ " "
                        + "[onCreate()]", e.toString());
            }
        }

        else {

            loadDestSpinnerData(null);
        }
        ArrayAdapter<String> payAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pay);
     //   payAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
            veh = getIntent().getStringExtra("vehNo");
            if (!veh.equals("")) {
                multipleDestinationTvVehNo.setText(veh);
            }
             dri = getIntent().getStringExtra("driv");
            src = getIntent().getStringExtra("src");
            if (!src.equals("")) {
                multipleDestinationTvLastDest.setText(src);
            }
            String clnr = getIntent().getStringExtra("clnr");
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
                    .toString() + " " + "[onGetParticularTripData]", e.toString());
        }

        try {
            updatePosition = getIntent().getIntExtra("editPosition", 0);
            editList = (ArrayList<String>) getIntent().getSerializableExtra("editTripData");
            if (editList != null) {
                int i;
                String s = editList.toString();
                s = s.replaceAll("\\[", "").replaceAll("\\]", "");
                String[] arr = s.split(",");

                for (i = 0; i < arr.length; i++) {
                    multipleDestinationTvVehNo.setText(arr[0]);
                    multipleDestinationEdtProduct.setText(arr[1]);
                    multipleDestinationEdtQuantity.setText(arr[2]);
                    multipleDestinationSpinnerDestination.setSelection(getIndex(multipleDestinationSpinnerDestination, arr[3]));
                    multipleDestinationEdtDistance.setText(arr[4]);
                    multipleDestinationEdtRouteName.setText(arr[5]);
                    multipleDestinationEdtRent.setText(arr[6]);
                    multipleDestinationSpinnerPaymentType.setSelection(getIndex(multipleDestinationSpinnerPaymentType, arr[7]));
                    multipleDestinationEdtPercentOrPayPerKM.setText(arr[8]);
                    multipleDestinationTvLastDest.setText(arr[9]);
                    mTripOrder=arr[10];
                    tripOrderNum=Integer.valueOf(mTripOrder);
                    mSubVoucher=arr[11];
                    multipleDestinationBtnAdd.setText("UPDATE");
                }
            } else {
                //Toast.makeText(MultipleDestinationActivity.this, "no value", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
                    .toString() + " " + "[onCreate]", e.toString());
        }
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
            multipleDestinationBtnBack = (Button) findViewById(R.id.activityMultipleDestinationBack);
            multipleDestinationBtnAdd = (Button) findViewById(R.id.activityMultipleDestinationBtnAdd);
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
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
       //     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activityMultipleDestinationBack:
                try {
                    finish();

//                        getData();
//                    if (mProduct.equals("") && mQuantity.equals("")  &&
//                            mDestination.equals("Select Destination")
//                            && mDistance.equals("")&& mRouteName .equals("")
//                            && mRent.equals("") && mPayType.equals("Select Payment Mode")
//                            && mPercentOrPayPerKM.equals(""))
//
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please Select all Values", Toast.LENGTH_LONG).show();
//                    else if(mProduct.equals(""))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please add product name", Toast.LENGTH_LONG).show();}
//                    else if(mQuantity.equals(""))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please add quantity of product", Toast.LENGTH_LONG).show();
//                    }
//                    else if(mDestination.equals("Select Destination"))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please select destination ", Toast.LENGTH_LONG).show();
//                    }
//                    else if(mRouteName.equals(""))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please add route name", Toast.LENGTH_LONG).show();
//                    }
//                    else if(mDistance.equals(""))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please add distance", Toast.LENGTH_LONG).show();
//                    }
//                    else if(mRent.equals(""))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please add Rent", Toast.LENGTH_LONG).show();
//                    }
//                    else if(mPayType.equals("Select Payment Mode"))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please select payment mode", Toast.LENGTH_LONG).show();
//                    }
//                    else if(mPercentOrPayPerKM.equals(""))
//                    {
//                        Toast.makeText(MultipleDestinationActivity.this,
//                                "Please add Commission or KM travelled ", Toast.LENGTH_LONG).show();
//                    }
//                        else
//                        {
//                            addDataToArrayList();
//                            finish();
//                        }
                }
                catch (Exception e){
                            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
                                    .toString() + " " + "[activityMultipleDestinationBtnSave]", e.toString());
                        }
                break;

            case R.id.activityMultipleDestinationBtnAdd:
                try {
                    getData();
                    if (mProduct.equals("") && mQuantity.equals("")  &&
                            mDestination.equals("Select Destination")
                            && mDistance.equals("")&& mRouteName .equals("")
                            && mRent.equals("") && mPayType.equals("Select Payment Mode")
                            && mPercentOrPayPerKM.equals(""))

                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please Select all Values", Toast.LENGTH_LONG).show();
                    else if(mProduct.equals(""))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please add product name", Toast.LENGTH_LONG).show();}
                    else if(mQuantity.equals(""))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please add quantity of product", Toast.LENGTH_LONG).show();
                    }
                    else if(mDestination.equals("Select Destination"))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please select destination ", Toast.LENGTH_LONG).show();
                    }
                    else if(mRouteName.equals(""))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please add route name", Toast.LENGTH_LONG).show();
                    }
                    else if(mRent.equals(""))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please add Rent", Toast.LENGTH_LONG).show();
                    }
                    else if(mDistance.equals(""))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please add distance", Toast.LENGTH_LONG).show();
                    }
                    else if(mPayType.equals("Select Payment Mode"))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please select payment mode", Toast.LENGTH_LONG).show();
                    }
                    else if(mPercentOrPayPerKM.equals(""))
                    {
                        Toast.makeText(MultipleDestinationActivity.this,
                                "Please add Commission or KM travelled ", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (multipleDestinationBtnAdd.getText().toString().equals("ADD")) {
                            addDataToArrayList();
                        } else if (multipleDestinationBtnAdd.getText().toString().equals("UPDATE")) {
                            updateDataToArrayList();
                        }
                    }
                }
                catch (Exception e){
                    ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
                            .toString() + " " + "[activityMultipleDestinationBtnAdd]", e.toString());
                }
                break;
        }
    }

    public void saveData(){
        Intent i=new Intent(MultipleDestinationActivity.this,TripManageFragment.class);
        startActivity(i);
    }

    public  void viewData(){
        try {
            int i, j;
            String temp = "";
            ArrayList<String> destList = new ArrayList<String>();
            for (i = 0; i < arrayList.size(); i++) {

                String s = arrayList.get(i).toString();
                String[] arr = s.split(",");
                for (j = 0; j < arr.length; j++) {
                    temp = arr[3];
                }
                destList.add(temp);
            }

            Intent intent = new Intent(MultipleDestinationActivity.this, TripDetailsActivity.class);
            intent.putExtra("destList", destList);
            intent.putExtra("source", src);
            intent.putExtra("driver", dri);
            intent.putExtra("vehicle", veh);
            startActivity(intent);
        }
        catch (Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
                    .toString() + " " + "[viewData]", e.toString());
        }
    }

    public void addDataToArrayList() {
        try {
            getData();

            DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd hhmmss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            mSubVoucher = "SubTrip" + dateFormatter.format(today);

            int pos = arrayList.size();
           // int pos=0;
            if (pos == 0) {
                order = 1;
                mList = new MultipleDestinationArrayList(mVehNo, mProduct, mQuantity, mDestination, mDistance, mRouteName, mRent, mPayType, mPercentOrPayPerKM, mLastDestination, "1", mSubVoucher);
            } else {
                order = order + 1;
                mTripOrder=String.valueOf(order);
                mList = new MultipleDestinationArrayList(mVehNo, mProduct, mQuantity, mDestination, mDistance, mRouteName, mRent, mPayType, mPercentOrPayPerKM, mLastDestination, mTripOrder, mSubVoucher);
            }
            pos = arrayList.size();
            if (updatePosition != 0) {
                arrayList.add(updatePosition, mList);
                updatePosition += 1;
            } else {
                arrayList.add(pos, mList);
            }
            clearViewData();
            dataAdapter.remove(mDestination);
            dataAdapter.notifyDataSetChanged();
            multipleDestinationTvLastDest.setText(mDestination);
            Toast.makeText(MultipleDestinationActivity.this, "Destination Added", Toast.LENGTH_LONG).show();
        }

catch(Exception e){
    clearViewData();
    multipleDestinationTvLastDest.setText(mDestination);
    Toast.makeText(MultipleDestinationActivity.this, "Destination Added", Toast.LENGTH_LONG).show();


}
    }

    public void updateDataToArrayList() {
        try {
            getData();

            mList = new MultipleDestinationArrayList(mVehNo, mProduct, mQuantity, mDestination, mDistance,
                    mRouteName, mRent, mPayType, mPercentOrPayPerKM, mLastDestination, mTripOrder, mSubVoucher);
            arrayList.set(updatePosition, mList);
            updatePosition += 1;
            clearViewData();
            multipleDestinationTvLastDest.setText(mDestination);
            multipleDestinationBtnAdd.setText("ADD");
        }

        catch(Exception e){
            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
                    .toString() + " " + "[updateDataToArrayList]", e.toString());
        }
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
            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
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
            ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
                    .toString() + " " + "[clearViewData]", e.toString());
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
    ExceptionMessage.exceptionLog(MultipleDestinationActivity.this, this.getClass()
            .toString() + " " + "[getndex]", e.toString());
}
        return index;
    }

}
