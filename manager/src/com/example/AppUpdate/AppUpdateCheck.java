package com.example.AppUpdate;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;

import java.io.File;

public class AppUpdateCheck extends ActionBarActivity {
    Button updateBtnApp;
    String location;
    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update_check);
       getActionBar().hide();
       packageName="package:com.example.AppUpdate".toString();
       location= getIntent().getStringExtra("loc");
        updateBtnApp=(Button)findViewById(R.id.activityAppUpdateBtnUpdate);
        updateBtnApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.fromFile(new File(location));
//                Intent.setDataAndType(uri, "application/vnd.android.package-archive");
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                update();
;            }
        });


    }

public void update(){
    try {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Uri packageURI = Uri.parse(packageName);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, packageURI);
                        intent.setDataAndType
                                (Uri.fromFile(new File(location)),
                                        "application/vnd.android.package-archive");
                        startActivity(intent);


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked

                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("New Apk Available..").setPositiveButton("Yes Proceed", dialogClickListener)
                .setNegativeButton("No.", dialogClickListener);
//    AlertDialog dialog = builder.create();
//    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
        builder.show();
    }
    catch(Exception e){
        ExceptionMessage.exceptionLog(this, this
                        .getClass().toString() + " " + "[update()]",
                e.toString());

    }
}

}
