package com.example.anand_roadwayss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.Interface.ISignature;

import java.io.ByteArrayOutputStream;


public class SignRegisterActivity extends ActionBarActivity implements ISignature {
    LinearLayout mContent;
    Button save,clear;
    Signature mSignature;
    byte[] img;
    DBAdapter db;
    String status="four";
    final ISignature mSignature1 = this;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sign);

        toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle("SIGNATURE");
        //comment
        setSupportActionBar(toolbar);

        db=new DBAdapter(this);

        mContent=(LinearLayout)findViewById(R.id.mysignature);
        save=(Button)findViewById(R.id.btnReg);
        clear=(Button)findViewById(R.id.btnClear);
        mSignature = new Signature(this, null);
        // mContent.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearing sign n making canvas blank
                mSignature.clear();
                //once mark anything on canvas lastTouchX and lastTouchY contains value so
                //once we clear the canvas we should make lastTouchX and lastTouchY zero
                mSignature.lastTouchY=0;
                mSignature.lastTouchX=0;

            }
        });

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Checking canvas containing sign or not
                if (mSignature.lastTouchX != 0 && mSignature.lastTouchY != 0) {
                    Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(returnedBitmap);
//
//                    canvas.drawColor(Color.WHITE);
                    mContent.draw(canvas);

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    // Calling addSignature Method
                    try {
                        img = bs.toByteArray();
                        // signImage=new String(img);

                        db.open();
                        db.addSignature(img);
                        mSignature.clear();
                        db.close();



                    } catch (Exception e) {
                        ExceptionMessage.exceptionLog(SignRegisterActivity.this, this
                                        .getClass().toString() + " " + "[save.setOnClickListener]",
                                e.toString());
                        e.printStackTrace();
                    }

                    Intent imgIntent = new Intent(SignRegisterActivity.this, Welcome.class);
                    startActivity(imgIntent);
                    finish();
                }
                else {
                    Toast.makeText(SignRegisterActivity.this,"Please sign here",Toast.LENGTH_LONG).show();
                }
            }


        });

    }


    public void saveSignature(String signImg, String status)
    {
        SendToWebService send = new SendToWebService(this, mSignature1);
        try {
            //saving data status flag=four;
            send.execute("33", "saveSignature", signImg, status);

        } catch (Exception e) {
            Toast.makeText(this, "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(this, this.getClass().toString() + " " + "[addSignature]", e.toString());
        }
    }


    @Override
    public void onAddSign(String response) {
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

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(this, this.getClass()
                            .toString() + " " + "[onAddSign]",
                    e.toString());
        }

    }

    @Override
    public void onBackPressed() {
        //finish();
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
