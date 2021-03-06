/*
 * Purpose - CleanerPayParticulars implements cleaner payment calculations
 * @author - Pravitha 
 * Created on May 22, 2014
 * Modified on June 10, 2014
 */
package com.example.PaymentModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Interface.IDriverCleanerPayment;
import com.example.ManageResources.CleanerEntryActivity;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.CustomAlertDialog;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CleanerPayParticulars extends Fragment implements IDriverCleanerPayment {
    View view;
    LinearLayout mContent;
    static Bitmap receiptImg;
    byte[] imgArr, signDb, voucherDb;
    Cursor cursor;
    int a = 0;
    EditText et1;
    String paymentDate, mEmployeeId, mFromDate, mToDate;
    final IDriverCleanerPayment mDriverCleaner = this;
    EditText cleanerPayETCommisionPercent, cleanerPayETConstSalary,
            cleanerPayETCleanerAdvance;
    TextView cleanerPayETSlidingTotalAmount;
    String mSCommission = "0", mSTotalPay = "0",
            cleanerPayParticularsCommission = "0.0",
            cleanerPayParticularsCleanerAdvance = "0.0",
            cleanerPayParticularsSalary = "0.0";
    static String sCleanerTotalPay, sCleanerBata;
    DBAdapter db;
    double mSalary = 0.0, mCommission = 0.0, mCleanerAdv = 0.0,
            mTotalpay = 0.0;

    Button payBtn, camera, viewImg;
    String mVoucherNum, voucherImg;
    ImageView camIntf, captImg;
    Button ok, clear;
    Signature mSignature;
    public static int REQUEST_CODE = 1;
    Calendar c = Calendar.getInstance();
    Bitmap thumbnail;
    Bitmap bp1, bp2;
    CustomAlertDialog ald;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cleaner_pay_particulars,
                container, false);
        db = new DBAdapter(getActivity());
        ald=new CustomAlertDialog();
       bindData();


		/*
         * Purpose - Sets the current page to 1 i.e. CleanerPay Event name -
		 * OnClickListener Parameters - No parameter Return Type - No return
		 * type
		 */
//        viewImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//                    dialogBuilder.setTitle("Receipt View");
//                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    LayoutInflater inflater = getActivity().getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.capturedimageview, null);
//                    dialogBuilder.setView(dialogView);
//
//                    ImageView iv = (ImageView) dialogView.findViewById(R.id.showimage);
//                    iv.setImageBitmap(thumbnail);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    voucherDb = stream.toByteArray();
//
//                    AlertDialog alertDialog = dialogBuilder.create();
//                    alertDialog.show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


//        camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    if(cleanerPayETSlidingTotalAmount.getText().toString()!=null
//                            && cleanerPayETSlidingTotalAmount.getText().toString()!="") {
//                        Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cam, REQUEST_CODE);
//                    }
//                    else
//                    {
//                        Toast.makeText(getActivity(),"please enter date and cleaner name",Toast.LENGTH_LONG).show();
//                    }
//                }catch (Exception e)
//                {
//                    //Toast.makeText(getActivity(),"please enter date and cleaner name",Toast.LENGTH_LONG).show();
//                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
//                            .toString() + " " + "[setUserVisibleHint]", e.toString());sCleanerName
//                }
//            }
//        });
        payBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (CleanerList.sCleanerName != null) {
                    final Dialog dialog=new Dialog(getActivity());
                    dialog.setContentView(R.layout.payment_dialog);
                    dialog.setTitle("Voucher Number");
                    et1=(EditText)dialog.findViewById(R.id.editText);
                    TextView t1=(TextView)dialog.findViewById(R.id.cancle);
                    TextView t2=(TextView)dialog.findViewById(R.id.ok);
                    t1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            dialog.dismiss();
                        }
                    });
                    t2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            mVoucherNum = et1.getText().toString();
                            if(mVoucherNum.equals("") || mVoucherNum.equalsIgnoreCase("null") || mVoucherNum.length()<3 ) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(),"Enter proper voucher number",Toast.LENGTH_LONG).show();
                            }
                                else
                            {
                                CleanerPayment();
                                dialog.dismiss();
                            }

                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(getActivity(), "enter name date first!!", Toast.LENGTH_LONG).show();
                }
            }
        });


        //               try {
//                     if(cleanerPayETSlidingTotalAmount.getText().toString()!=null
//                             && cleanerPayETSlidingTotalAmount.getText().toString()!="") {
//                         if (click == 0 && receiptImg == null) {
//                             AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                             builder.setMessage("Are you sure to proceed without Receipt?");
//                             builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
//                                 @Override
//                                 public void onClick(DialogInterface dialog, int which) {
//
//                                     AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                                     alert.setTitle("Voucher Number"); // Set Alert dialog
//                                     // title here
//                                     alert.setMessage("ENTER THE VOUCHER NUMBER HERE"); // Message
//                                     // here
//                                     final EditText input = new EditText(getActivity());
//                                     alert.setView(input);
//                                     alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                         public void onClick(
//                                                 DialogInterface dialog,
//                                                 int whichButton) {
//                                             mVoucherNum = input.getEditableText().toString();
//                                             receiptAlertDialog();
//
//                                         } // End of onClick(DialogInterface
//                                         // dialog,
//                                         // int
//                                         // whichButton)
//                                     }); // End of alert.setPositiveButton
//
//                                     alert.setNegativeButton("CANCEL",
//                                             new DialogInterface.OnClickListener() {
//                                                 public void onClick(
//                                                         DialogInterface dialog,
//                                                         int whichButton) {
//                                                     // Canceled.
//                                                     dialog.cancel();
//                                                 }
//                                             }); // End of alert.setNegativeButton
//                                     AlertDialog alertDialog = alert.create();
//                                     alertDialog.show();
//                                     receiptImg = null;
//                                     // /* Alert Dialog Code End */
//
//                                 }// /////
//
//                             }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                                 @Override
//                                 public void onClick(DialogInterface dialog, int which) {
//                                     dialog.dismiss();
//                                 }
//                             }).show();
//                         } else {
//                             Toast.makeText(getActivity(), "receipt attached", Toast.LENGTH_LONG).show();
//                             AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//                             alert.setTitle("Voucher Number"); // Set Alert dialog
//                             // title here
//                             alert.setMessage("ENTER THE VOUCHER NUMBER HERE"); // Message
//                             // here
//                             final EditText input = new EditText(getActivity());
//                             alert.setView(input);
//                             alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                 public void onClick(
//                                         DialogInterface dialog,
//                                         int whichButton) {
//                                     mVoucherNum = input.getEditableText().toString();
//                                     receiptAlertDialog();
//
//                                 }
//                             }); // End of alert.setPositiveButton
//
//                             alert.setNegativeButton("CANCEL",
//                                     new DialogInterface.OnClickListener() {
//                                         public void onClick(
//                                                 DialogInterface dialog,
//                                                 int whichButton) {
//                                             // Canceled.
//                                             dialog.cancel();
//                                         }
//                                     }); // End of alert.setNegativeButton
//                             AlertDialog alertDialog = alert.create();
//                             alertDialog.show();
//                             receiptImg = null;
//                             // /* Alert Dialog Code End */
//                         }
//                     }else
//                     {
//                         Toast.makeText(getActivity(),"please enter date and cleaner name",Toast.LENGTH_LONG).show();
//                     }
//                } catch (Exception e) {
//                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
//                            .toString()
//                            + " "
//                            + "[case : fragmentDriverPaymentBtnPay]", e.toString());
//                }
//
//            }
//        });


		/*
		 * Purpose - Updates TotalpayAmount calculation when salary edittext
		 * changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */
        cleanerPayETConstSalary.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                String amount = cleanerPayETConstSalary.getText().toString();
                if (!amount.equals("")) {
                    TotalPayAmount();

                }
            }

        });

		/*
		 * Purpose - Updates TotalpayAmount calculation when Commission percent
		 * edittext changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */
        cleanerPayETCommisionPercent.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                String amount = cleanerPayETCommisionPercent.getText()
                        .toString();
                if (!amount.equals("")) {
                    TotalPayAmount();

                }
            }

        });

		/*
		 * Purpose - Updates TotalpayAmount calculation when cleaner advance
		 * edittext changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */
        cleanerPayETCleanerAdvance.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                String amount = cleanerPayETCleanerAdvance.getText().toString();
                if (!amount.equals("")) {
                    TotalPayAmount();

                }
            }

        });

        return view;

    }

	/*
	 * Purpose - Binds XMl Id reference to java Method Name - bindData()
	 * Parameters - No parameters Return Type - No Return Type
	 */

    private void bindData() {

        cleanerPayETCommisionPercent = (EditText) view
                .findViewById(R.id.fragmentCleanerPaymentETCommisionPercent);
        cleanerPayETConstSalary = (EditText) view
                .findViewById(R.id.fragmentCleanerPaymentETConstSalary);
        cleanerPayETCleanerAdvance = (EditText) view
                .findViewById(R.id.fragmentCleanerPaymentETCleanerAdvance);
        cleanerPayETSlidingTotalAmount = (TextView) view
                .findViewById(R.id.fragmentCleanerPaymentETSlidingTotalAmount);
        payBtn = (Button) view
                .findViewById(R.id.fragmentCleanerPaymentETbtnCloseSlider);
//        camera = (Button) view.findViewById(R.id.cameraInterface);
//        viewImg = (Button) view.findViewById(R.id.viewReceipt);

    }

	/*
	 * Purpose - Calculates total payment amount Method Name - TotalPayAmount()
	 * Parameters - No parameter Return Type - String Total payment amount
	 */

    public String TotalPayAmount() {
        try {

            if (!cleanerPayETConstSalary.getText().toString().equals("")) {
                mSalary = Double.valueOf(cleanerPayETConstSalary.getText()
                        .toString());
            }

            if (!cleanerPayETCommisionPercent.getText().toString().equals("")) {
                mSCommission = cleanerPayETCommisionPercent.getText()
                        .toString();
                mCommission = Double.valueOf(mSCommission);
            }

            if (!cleanerPayETCleanerAdvance.getText().toString().equals("")) {
                mCleanerAdv = Double.valueOf(cleanerPayETCleanerAdvance
                        .getText().toString());
            }

            mTotalpay = mSalary + mCommission - mCleanerAdv;
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            mSTotalPay = numberFormat.format(mTotalpay);
            cleanerPayETSlidingTotalAmount.setText(mSTotalPay);
            sCleanerTotalPay = mSTotalPay;
            sCleanerBata = mSCommission;
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[TotalPayAmount]", e.toString());
        }

        return mSTotalPay;

    }

    public void extraMethod() {
        try {

            if (cleanerPayETConstSalary.getText().toString().equals("")
                    && cleanerPayETCommisionPercent.getText().toString()
                    .equals("")
                    && cleanerPayETCleanerAdvance.getText().toString()
                    .equals(""))

            {
                mSalary = 0;
                mCommission = 0;
                mCleanerAdv = 0;

                mTotalpay = mSalary + mCommission - mCleanerAdv;
                mSTotalPay = Double.toString(mTotalpay);
                cleanerPayETSlidingTotalAmount.setText(mSTotalPay);
            }
            if (cleanerPayETSlidingTotalAmount.getText().toString() != "0"
                    && cleanerPayETSlidingTotalAmount.getText().toString() != "0.0") {
                sCleanerTotalPay = cleanerPayETSlidingTotalAmount.getText()
                        .toString();

            } else {
                sCleanerTotalPay = "NOT EXIST";
            }
            sCleanerBata = mSCommission;

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[extraMethod]", e.toString());
        }

        // return mSTotalPay;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if(isVisibleToUser)
            {
                PaymentCleaner.pos=2;
            }

            if (CleanerList.fDate == ""
                    && CleanerList.fDate == null
                    && CleanerList.tDate == null
                    && CleanerList.tDate == ""
                    && CleanerList.fDate == "From Date"
                    && CleanerList.tDate == "To Date")

            {

            }

            if (CleanerList.sCleanerCommissionString != null
                    && CleanerList.sCleanerSalary != null
                    && CleanerList.sCleanerAdvance != null) {
                cleanerPayParticularsCommission = CleanerList.sCleanerCommissionString;
                cleanerPayETCommisionPercent.setText(cleanerPayParticularsCommission);

                cleanerPayParticularsSalary = CleanerList.sCleanerSalary;
                cleanerPayETConstSalary.setText(cleanerPayParticularsSalary);

                cleanerPayParticularsCleanerAdvance = CleanerList.sCleanerAdvance;
                cleanerPayETCleanerAdvance
                        .setText(cleanerPayParticularsCleanerAdvance);

                CleanerList.sCleanerCommissionString = null;
                CleanerList.sCleanerSalary = null;
                CleanerList.sCleanerAdvance = null;

            }


        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[setUserVisibleHint]", e.toString());
        }

    }

    private void cleanerSavePayment(String ParsedString) {
        try {

            switch (ParsedString) {

                case "inserted":
                    bindData();
                    String name = CleanerList.sCleanerName;
                    db.open();
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyDate(), paymentDate);
                    cv.put(DBAdapter.getKeyPaymentType(), "Cleaner");
                    cv.put(DBAdapter.getKeyName(), name);
                    cv.put(DBAdapter.getKeyVoucherNo(), mVoucherNum);
                    cv.put(DBAdapter.getKeyCommission(), sCleanerBata);
                    cv.put(DBAdapter.getKeyAmount(), sCleanerTotalPay);
                    cv.put(DBAdapter.getKeyReceipt(), imgArr);
                    long id = db.insertContactWithDelete(DBAdapter.getPaymentDetails(), cv);
                    if (id != -1) {
                        //pa = "0";
                        Intent in = new Intent(getActivity(), CleanerPaidList.class);
                        startActivity(in);
                        getActivity().finish();
                    }
                    db.close();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Payment Saved Successful", Toast.LENGTH_LONG).show();

                    break;

                case "invalid authkey":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[cleanerSavePayment]",
                            ParsedString);
                    break;

                case "employee id does not exist":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[cleanerSavePayment]",
                            ParsedString);
                    break;

                case "voucher number already exist":
                    Toast.makeText(getActivity().getApplicationContext(),
                            "VoucherNumber Already Exists", Toast.LENGTH_LONG)
                            .show();
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[cleanerSavePayment]",
                            ParsedString);
                    break;

                case "unknown error":
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[cleanerSavePayment]",
                            ParsedString);
                    break;

                default:
                    Toast.makeText(getActivity(), "Try after sometime...",
                            Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[cleanerSavePayment]",
                            "Try after sometime...");
                    break;

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[cleanerSavePayment]", e.toString());
        }

    }

    @Override
    public void ongetCleanerPaymentDetails(String response) {

    }

    @Override
    public void ongetDriverPaymentDetails(String response) {

    }

    @Override
    public void onManagePaymentDetails(String response) {
        try {
            if (response.equals("No Internet")) {
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                cd.ConnectingToInternet();
            } else if (response.contains("refused") || response.contains("timed out")) {
                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity()).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else if (response.contains("java.net.SocketTimeoutException")) {

                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.lowconnection3);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity()).setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setView(image);
                builder.create().show();

            } else {

                bindData();
                String ParsedString = cleanerJParser(response);
                cleanerSavePayment(ParsedString);

            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[onManagePaymentDetails]",
                    e.toString());
        }
    }

    public String cleanerJParser(String response) {
        String jsonData = null;
        if (response != null)
            try {
                // TO CONVERT THE STRING TO OBJECT
                JSONObject jsonResponse = new JSONObject(response);
                // GET THE VALUE OF d:
                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                jsonData = d.getString("status").trim();

                return jsonData;

            } catch (JSONException e) {
                if (e.toString().contains("refused")) {
                    ImageView image = new ImageView(getActivity());
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity()).setPositiveButton("OK",
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

                    ImageView image = new ImageView(getActivity());
                    image.setImageResource(R.drawable.lowconnection3);

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity()).setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }).setView(image);
                    builder.create().show();

                } else {
                    Toast.makeText(getActivity(), "Try after sometime...",
                            Toast.LENGTH_SHORT).show();
                }
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[cleanerJParser]", e.toString());
            }

        else {

        }

        return jsonData;

    }

    private void CleanerPayment() {

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",
                Locale.getDefault());
        paymentDate = df.format(c.getTime());
        db = new DBAdapter(getActivity());
        db.open();

        mEmployeeId = db.checkCleanerTableforDataExist(CleanerList.sCleanerName);
        db.close();
        mFromDate = CleanerList.fromDate;
        mToDate = CleanerList.toDate;
        voucherImg = "";
        SendToWebService send = new SendToWebService(getActivity(), mDriverCleaner);

        try {
            send.execute("18", "ManagePayment", mEmployeeId,
                    mVoucherNum, mFromDate, mToDate, sCleanerBata,
                    sCleanerTotalPay, paymentDate, "0.0", "0.0", voucherImg, "4");
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();

            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[CleanerPayment]", e.toString());
        }

    }

    public void signMatch() {
        Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),mContent.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        mContent.draw(canvas);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);

            try {
                imgArr = bs.toByteArray();
                db.open();
                cursor = db.getSignature();
                signDb = cursor.getBlob(cursor.getColumnIndex("signature"));
                //matching current sign with database sign
                bp1 = BitmapFactory.decodeByteArray(signDb, 0, signDb.length);
                bp2 = BitmapFactory.decodeByteArray(imgArr, 0, imgArr.length);
                mSignature.clear();

                a = ((signDb.length - imgArr.length) * 100 / imgArr.length);

                if (a > 0 && a <= 30 || a < 0) {
                    if (!mVoucherNum.equals("0")
                            && !mVoucherNum.equals("")) {
                        CleanerPayment();
                        db.close();
                    } else {
                        Toast.makeText(getActivity(), "Please enter the Voucher Number...",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    ald.alertDialog(getActivity(), "Signature mismatch !");
                }
            } catch (Exception e)
            {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                .toString() + " " + "[jsonParser-commSalaryArray]",
                        e.toString());
                e.printStackTrace();
            }
        }


    public void receiptAlertDialog() {
     //alert dialog for taking signature
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("SIGN HERE");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_layout, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog1 = dialogBuilder.create();

        mContent = (LinearLayout) dialogView.findViewById(R.id.mysignature);
        ok = (Button) dialogView.findViewById(R.id.btnReg);
        clear = (Button) dialogView.findViewById(R.id.btnClear);
        mSignature = new Signature(getActivity(), null);
        mContent.addView(mSignature);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.clear();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
                signMatch();
            }
        });
        alertDialog1.show();
    }

}
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
//            thumbnail = (Bitmap) data.getExtras().get("data");
//            captImg = new ImageView(getActivity());
//            captImg.setImageBitmap(thumbnail);
//
//            //sending receipt image to next fragment
//            receiptImg = thumbnail;
//
//            viewImg.setEnabled(true);
//            camIntf.setVisibility(View.VISIBLE);
//            viewImg.setVisibility(View.VISIBLE);
//            click++;
//        }





