package com.example.tripmodule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anand_roadwayss.R;

import java.util.ArrayList;

public class TripDetailsActivity extends Activity implements View.OnClickListener {

    TextView txtSource,txtDriver1;
    Button btnEdit,btnDelete;
    ListView destinationList;
    ArrayList<String> destList;
    String val,data,src,dri,veh;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        Bundle bundleObject = getIntent().getExtras();
        destList = (ArrayList<String>) getIntent().getSerializableExtra("destList");
        src=getIntent().getExtras().getString("src");
        dri=getIntent().getExtras().getString("dri");
        veh=getIntent().getExtras().getString(veh);
        bindData();
        setAllData();
    }

    private void bindData() {
        txtSource = (TextView)findViewById(R.id.tripDetailsActivityTvSource);
        txtDriver1 = (TextView)findViewById(R.id.tripDetailsActivityTvDriver1);
        destinationList=(ListView)findViewById(R.id.tripDetailsActivityLvDestination);
        btnEdit = (Button)findViewById(R.id.tripDetailsActivityDestinationBtnEditDestination);
        btnDelete = (Button)findViewById(R.id.tripDetailsActivityDestinationBtnDeleteDestination);
    }

   private void setAllData(){

       txtSource.setText(src);
       txtDriver1.setText(dri);
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, destList);
       destinationList.setAdapter(adapter);
       destinationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               pos=position;
               val = MultipleDestinationActivity.arrayList.get(position).toString();

           }
       });
   }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tripDetailsActivityDestinationBtnEditDestination:
                ArrayList<String> editTripData=new ArrayList<String>();
                String[] arr=val.split(",");
                for(int i=0;i<arr.length;i++)
                {
                    data=arr[i];
                    editTripData.add(data);
                }
                Intent in=new Intent(TripDetailsActivity.this,MultipleDestinationActivity.class);
                in.putExtra("editPosition",pos);
                in.putExtra("editTripData",editTripData);
                startActivity(in);
                break;

            case R.id.tripDetailsActivityDestinationBtnDeleteDestination:
                MultipleDestinationActivity.arrayList.remove(pos);
                Toast.makeText(TripDetailsActivity.this, "Destination" + pos + " completeList[" + val + "]" +" Removed", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
