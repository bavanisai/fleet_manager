/*
 * Purpose - DriverPayParticulars implements driver payment calculations
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

public class DriverPayParticulars extends Fragment implements IDriverCleanerPayment {

    LinearLayout mContent;
    int a = 0;
    View view;
    EditText et1;
    EditText driverPayETCommisionPercent, driverPayETConstSalary,
            driverPayETDriverAdvance, driverPayETVehicleAdvance,
            driverPayETMileageDeduction, driverPayETMileageDeductionAmount,
            driverPayETMileageDeductionTotal, driverPayETExpense;
    TextView driverPayETSlidingTotalAmount;

    String mEmployeeId, voucherImg, paymentDate;
    DBAdapter db;
    Button payBtn, camera, viewImg;
    public static int REQUEST_CODE = 1;
    Calendar c = Calendar.getInstance();
    Bitmap thumbnail;
    int click = 0;
    static Bitmap receiptImg;
    Cursor cursor;
    double mSalary = 0.0, mCommission = 0.0, mMileageDeductionTotal = 0.0,
            mDriverAdvanceP = 0.0, mVehicleAdvanceP = 0.0, mTotalpay = 0.0,
            mMileageDeductionLtr = 0.0, mMileageDeductionAmount = 0.0,
            mExpenseP;

    String mSCommission = "0", mSMileageDeductionTotal = "0",
            mSDriverAdvance = "0", mSVehicleAdvance = "0", mSTotalPay = "0",
            mSExpense = "0", driverPayParticularsCommission = "0.0",
            driverPayParticularsDriAdvance = "0.0",
            driverPayParticularsVehAdvance = "0.0",
            driverPayParticularsSalary = "0.0",
            driverPayParticularsMileageDeductionLtr = "0.0",
            driverPayParticularsExpense="0.0";

    static String mFromDate, mToDate, pa;
    Bitmap bp1, bp2;
    String mVoucherNum;
    ImageView camIntf, captImg;
    Button ok, clear;
    Signature mSignature;
    byte[] imgArr, signDb, voucherDb;
    final IDriverCleanerPayment mDriverCleaner = this;
    static String sDriverTotalPay, sMileageDeductionTotal, sBata;
    CustomAlertDialog ald;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_driver_pay_particulars,
                container, false);
        db = new DBAdapter(getActivity());
        ald=new CustomAlertDialog();
//        camIntf = (ImageView) view.findViewById(R.id.checkmark);
//        camIntf.setImageResource(R.drawable.img_saved);
//        camIntf.setVisibility(View.INVISIBLE);

        bindData();

        //  viewImg.setVisibility(View.INVISIBLE);
        // clearData();

		/*
         * Purpose - Sets the current page to 1 i.e. DriverPay Event name -
		 * OnClickListener Parameters - No parameter Return Type - No return
		 * type
		 */
//        viewImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
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
//            public void onClick(View v)
//            {
//                if(driverPayETSlidingTotalAmount.getText().toString()!=null
//                        && driverPayETSlidingTotalAmount.getText().toString()!="") {
//                    Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cam, REQUEST_CODE);
//                }else
//                {
//                    Toast.makeText(getActivity(),"enter date and driver name",Toast.LENGTH_LONG).show();
//                }
//            }
//        });

//        payBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int a;
//            }
//        });

        payBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (DriverList.sDriverName != null) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.payment_dialog);
                    dialog.setTitle("Voucher Number");
                    et1 = (EditText) dialog.findViewById(R.id.editText);
                    TextView t1 = (TextView) dialog.findViewById(R.id.cancle);
                    TextView t2 = (TextView) dialog.findViewById(R.id.ok);
                    t1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    t2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mVoucherNum = et1.getText().toString();
                            receiptAlertDialog();
                        }
                    });

//                                              AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//
//                                              alert.setTitle("Voucher Number"); // Set Alert dialog
//                                              // title here
//                                              alert.setMessage(""); // Message
//                                              // here
//                                              final EditText input = new EditText(getActivity());
//                                              input.setPadding(3,1,3,1);
//                                              alert.setView(input);
//                                              alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                  public void onClick(
//                                                          DialogInterface dialog,
//                                                          int whichButton) {
//                                                      mVoucherNum = input.getEditableText().toString();
//                                                      receiptAlertDialog();
//
//                                                  } // End of onClick(DialogInterface
//                                                  // dialog,
//                                                  // int
//                                                  // whichButton)
//                                              }); // End of alert.setPositiveButton
//
//                                              alert.setNegativeButton("CANCEL",
//                                                      new DialogInterface.OnClickListener() {
//                                                          public void onClick(
//                                                                  DialogInterface dialog,
//                                                                  int whichButton) {
//                                                              // Canceled.
//                                                              dialog.cancel();
//                                                          }
//                                                      }); // End of alert.setNegativeButton
//                                              AlertDialog alertDialog = alert.create();
//                                              alertDialog.show();
                    dialog.show();

                    // /* Alert Dialog Code End */
                } else {
                    Toast.makeText(getActivity(), "SELECT DATE & EMPLOYEE FROM THE LIST!!", Toast.LENGTH_LONG).show();
                    ((PaymentDriver) getActivity()).setCurrentItem(0, true);
                }
            }

        });



		/*
         * Purpose - Updates TotalpayAmount calculation when salary edittext
		 * changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */
        driverPayETConstSalary.addTextChangedListener(new TextWatcher() {

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
                String amount = driverPayETConstSalary.getText().toString();
                if (!amount.equals("")) {
                    TotalPayAmount();

                } else {
                    extraMethod();
                }
            }

        });

        driverPayETExpense.addTextChangedListener(new TextWatcher() {

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

                for (int i = 0; i < driverPayETExpense.getText().toString().length(); i++) {
                    if (!Character.isDigit(driverPayETExpense.getText().toString().charAt(i)))
                        driverPayETExpense.setError("Enter Numeric Value");
                }

                String amount = driverPayETExpense.getText().toString();
                if (!amount.equals("")) {

                    TotalPayAmount();

                } else {
                    amount = "0.0";
                    // extraMethod();
                    TotalPayAmount();
                }
            }

        });

		/*
		 * Purpose - Updates TotalpayAmount calculation when Commission percent
		 * edittext changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */
        driverPayETCommisionPercent.addTextChangedListener(new TextWatcher() {

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
                String amount = driverPayETCommisionPercent.getText()
                        .toString();
                if (!amount.equals("")) {
                    TotalPayAmount();

                } else {
                    extraMethod();
                }
            }

        });

		/*
		 * Purpose - Updates TotalpayAmount calculation when mileage deduction
		 * edittext changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */
        driverPayETMileageDeduction.addTextChangedListener(new TextWatcher() {

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
                String amount = driverPayETMileageDeduction.getText()
                        .toString();
                if (!amount.equals("")) {
                    TotalMileageAmount();

                } else {
                    extraMethod();
                }
            }

        });

		/*
		 * Purpose - Updates TotalpayAmount calculation when mileage deduction
		 * amount edittext changes. Event name - addTextChangedListener
		 * Parameters - No parameter Return Type - No return type
		 */
        driverPayETMileageDeductionAmount
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                        String amount = driverPayETMileageDeductionAmount
                                .getText().toString();
                        if (!amount.equals("")) {
                            TotalMileageAmount();

                        } else {
                            extraMethod();
                        }
                    }

                });

		/*
		 * Purpose - Updates TotalpayAmount calculation when mileage deduction
		 * total edittext changes. Event name - addTextChangedListener
		 * Parameters - No parameter Return Type - No return type
		 */

        driverPayETMileageDeductionTotal
                .addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                        String amount = driverPayETMileageDeductionTotal
                                .getText().toString();
                        if (!amount.equals("")) {
                            TotalPayAmount();

                        } else {
                            extraMethod();
                        }
                    }

                });

		/*
		 * Purpose - Updates TotalpayAmount calculation when vehicle advance
		 * edittext changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */
        driverPayETVehicleAdvance.addTextChangedListener(new TextWatcher() {

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
                String amount = driverPayETVehicleAdvance.getText().toString();
                if (!amount.equals("")) {

                    TotalPayAmount();

                } else {
                    extraMethod();
                }
            }

        });

		/*
		 * Purpose - Updates TotalpayAmount calculation when driver advance
		 * edittext changes. Event name - addTextChangedListener Parameters - No
		 * parameter Return Type - No return type
		 */

        driverPayETDriverAdvance.addTextChangedListener(new TextWatcher() {

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
                String amount = driverPayETDriverAdvance.getText().toString();
                if (!amount.equals("")) {

                    TotalPayAmount();

                } else {
                    extraMethod();
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

        driverPayETCommisionPercent = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETCommisionPercent);
        driverPayETConstSalary = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETConstSalary);
        driverPayETDriverAdvance = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETDriverAdvance);
        driverPayETVehicleAdvance = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETVehicleAdvance);
        driverPayETMileageDeduction = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETMileageDeduction);
        driverPayETMileageDeductionAmount = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETMileageDeductionAmount);
        driverPayETMileageDeductionTotal = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETMileageDeductionTotal);
        driverPayETExpense = (EditText) view
                .findViewById(R.id.fragmentDriverPaymentETExpense);
        driverPayETSlidingTotalAmount = (TextView) view
                .findViewById(R.id.fragmentDriverPaymentETSlidingTotalAmount);
        payBtn = (Button) view
                .findViewById(R.id.fragmentDriverPaymentETbtnCloseSlider);
//        camera = (Button) view.findViewById(R.id.cameraInterface);
//        viewImg = (Button) view.findViewById(R.id.viewReceipt);

    }

	/*
	 * Purpose - Calculates total payment amount Method Name - TotalPayAmount()
	 * Parameters - No parameter Return Type - String Total payment amount
	 */

    public void TotalPayAmount() {
        try {

            if (!driverPayETConstSalary.getText().toString().equals("")) {

                mSalary = Double.valueOf(driverPayETConstSalary.getText().toString());

            }

            if (!driverPayETCommisionPercent.getText().toString().equals("")) {
                mSCommission = driverPayETCommisionPercent.getText().toString();
                mCommission = Double.valueOf(mSCommission);

            }

            if (!driverPayETMileageDeductionTotal.getText().toString()
                    .equals("")) {
                mSMileageDeductionTotal = driverPayETMileageDeductionTotal
                        .getText().toString();
                mMileageDeductionTotal = Double
                        .valueOf(mSMileageDeductionTotal);

            }

            if (!driverPayETDriverAdvance.getText().toString().equals("")) {
                mSDriverAdvance = driverPayETDriverAdvance.getText().toString();
                mDriverAdvanceP = Double.valueOf(mSDriverAdvance);
            }

            if (!driverPayETVehicleAdvance.getText().toString().equals("")) {
                mSVehicleAdvance = driverPayETVehicleAdvance.getText()
                        .toString();
                mVehicleAdvanceP = Double.valueOf(mSVehicleAdvance);
            }
            if (!driverPayETExpense.getText().toString().equals("")) {
                mSExpense = driverPayETExpense.getText().toString();
                mExpenseP = Double.valueOf(mSExpense);
            } else {
                mSExpense = "0.0";
                mExpenseP = 0.0;
            }

            mTotalpay = mSalary + mCommission - mMileageDeductionTotal
                    - mVehicleAdvanceP - mDriverAdvanceP + mExpenseP;
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            mSTotalPay = numberFormat.format(mTotalpay);
            driverPayETSlidingTotalAmount.setText(mSTotalPay);
            double c = Double.valueOf(driverPayETSlidingTotalAmount.getText()
                    .toString());
            sDriverTotalPay = numberFormat.format(c);
            sDriverTotalPay = driverPayETSlidingTotalAmount.getText().toString();
            sMileageDeductionTotal = mSMileageDeductionTotal;
            sBata = mSCommission;

        } catch (Exception e) {

            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[TotalPayAmount]", e.toString());
        }

        // return mSTotalPay;
    }

    public void extraMethod() {
        try {

            if (driverPayETConstSalary.getText().toString().equals("")
                    && driverPayETCommisionPercent.getText().toString()
                    .equals("")
                    && driverPayETMileageDeductionTotal.getText().toString()
                    .equals("0.0")
                    && driverPayETDriverAdvance.getText().toString().equals("")
                    && driverPayETVehicleAdvance.getText().toString()
                    .equals("")
                    && driverPayETExpense.getText().toString().equals("")) {
                mSalary = 0;
                mCommission = 0;
                mMileageDeductionTotal = 0;
                mDriverAdvanceP = 0;
                mVehicleAdvanceP = 0;
                mExpenseP = 0;

                mTotalpay = mSalary + mCommission - mMileageDeductionTotal
                        - mVehicleAdvanceP - mDriverAdvanceP - mExpenseP;
                mSTotalPay = String.valueOf(mTotalpay);
                driverPayETSlidingTotalAmount.setText(mSTotalPay);
            }
            // sDriverTotalPay = mSTotalPay;
            // sDriverTotalPay=driverPayETSlidingTotalAmount.getText().toString();
            if (driverPayETSlidingTotalAmount.getText().toString() != "0"
                    && driverPayETSlidingTotalAmount.getText().toString() != "0.0") {
                sDriverTotalPay = driverPayETSlidingTotalAmount.getText()
                        .toString();
                // sDriverTotalPay = "NOT EXIST";
            } else {
                sDriverTotalPay = "NOT EXIST";
                // sDriverTotalPay =
                // driverPayETSlidingTotalAmount.getText().toString();
            }
            sMileageDeductionTotal = mSMileageDeductionTotal;
            sBata = mSCommission;

        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[extraMethod]", e.toString());
        }

        // return mSTotalPay;
    }

	/*
	 * Purpose - Calculates total mileage amount Method Name -
	 * TotalMileageAmount() Parameters - No parameter Return Type - String Total
	 * mileage deduction total
	 */

    public String TotalMileageAmount() {
        try {
            if (!driverPayETMileageDeduction.getText().toString().equals("")) {
                mMileageDeductionLtr = Double
                        .valueOf(driverPayETMileageDeduction.getText()
                                .toString());
            }

            if (!driverPayETMileageDeductionAmount.getText().toString()
                    .equals("")) {
                mMileageDeductionAmount = Double
                        .valueOf(driverPayETMileageDeductionAmount.getText()
                                .toString());
            }

            mMileageDeductionTotal = mMileageDeductionLtr
                    * mMileageDeductionAmount;
            mSMileageDeductionTotal = Double.toString(mMileageDeductionTotal);
            driverPayETMileageDeductionTotal.setText(mSMileageDeductionTotal);
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[TotalMileageAmount]", e.toString());
        }
        return mSMileageDeductionTotal;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            PaymentDriver.pos=2;
            try {
                if (DriverList.fDate == ""
                        && DriverList.fDate == null
                        && DriverList.tDate == null
                        && DriverList.tDate == ""
                        && DriverList.fDate == "From Date"
                        && DriverList.tDate == "To Date")

                {

                }
                if (DriverList.sCommissionString != null
                        && DriverList.sDriverSalary != null
                        && DriverList.sDriAdvance != null
                        && DriverList.sVehAdvance != null
                        && DriverList.sMileage != null
                        && DriverList.sDriverName != null) {

                    driverPayParticularsCommission = DriverList.sCommissionString;
                    driverPayETCommisionPercent.setText(driverPayParticularsCommission);

                    driverPayParticularsSalary = DriverList.sDriverSalary;
                    driverPayETConstSalary.setText(driverPayParticularsSalary);

                    driverPayParticularsDriAdvance = DriverList.sDriAdvance;
                    driverPayETDriverAdvance
                            .setText(driverPayParticularsDriAdvance);

                    driverPayParticularsVehAdvance = DriverList.sVehAdvance;
                    driverPayETVehicleAdvance
                            .setText(driverPayParticularsVehAdvance);

                    driverPayParticularsMileageDeductionLtr = DriverList.sMileage;
                    driverPayETMileageDeduction
                            .setText(driverPayParticularsMileageDeductionLtr);

                    driverPayParticularsExpense=DriverList.sExpense;

                    driverPayETExpense.setText(driverPayParticularsExpense);

                    DriverList.sCommissionString = null;
                    DriverList.sDriverSalary = null;
                    DriverList.sDriAdvance = null;
                    DriverList.sVehAdvance = null;
                    DriverList.sMileage = null;
                    DriverList.sExpense=null;
                }


            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[setUserVisibleHint]", e.toString());
            }
        }
    }

    public void clearData() {
        driverPayETCommisionPercent.setText("");
        driverPayETConstSalary.setText("");
        driverPayETDriverAdvance.setText("");
        driverPayETVehicleAdvance.setText("");
        driverPayETMileageDeduction.setText("");
        driverPayETMileageDeductionAmount.setText("0");
        driverPayETMileageDeductionTotal.setText("");
        driverPayETExpense.setText("0");
        driverPayETSlidingTotalAmount.setText("");
    }

    //    @Override
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
//
//    }
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
                bp2 = BitmapFactory.decodeByteArray(imgArr,
                        0, imgArr.length);
                mSignature.clear();

                a = ((signDb.length - imgArr.length) * 100 / imgArr.length);

                if (a > 0 && a <= 30 || a < 0) {
                    if (!mVoucherNum.equals("0")
                            && !mVoucherNum.equals("")) {
                        DriverPayment();
                        db.close();
                    } else {
                        Toast.makeText(getActivity(), "Please enter the Voucher Number...",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    ald.alertDialog(getActivity(),"Signature mismatch !");
                }
                  } catch (Exception e) {
                e.printStackTrace();
            }
        }



    public void receiptAlertDialog() {

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
                String ParsedString = driverJParser(response);
                driverSavePayment(ParsedString);

            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                            .toString() + " " + "[onManagePaymentDetails]",
                    e.toString());
        }
    }

    private void driverSavePayment(String ParsedString) {
        try {

            switch (ParsedString) {

                case "inserted":
                    bindData();
                    String name = DriverList.sDriverName;
                    db.open();
                    ContentValues cv = new ContentValues();
                    cv.put(DBAdapter.getKeyDate(), paymentDate);
                    cv.put(DBAdapter.getKeyPaymentType(), "Driver");
                    cv.put(DBAdapter.getKeyName(), name);
                    cv.put(DBAdapter.getKeyVoucherNo(), mVoucherNum);
                    cv.put(DBAdapter.getKeyCommission(), sBata);
                    cv.put(DBAdapter.getKeyMileagededuction(), sMileageDeductionTotal);
                    cv.put(DBAdapter.getKeyAmount(), sDriverTotalPay);
                    cv.put(DBAdapter.getKeyReceipt(), imgArr);

                    long id = db.insertContactWithDelete(DBAdapter.getPaymentDetails(), cv);
                    if (id != -1) {
                        // refreshActivity();

                        pa = "0";
                        Intent in = new Intent(getActivity(), DriverPaidList1.class);
                        startActivity(in);
                        getActivity().finish();

                    }
                    db.close();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Payment Saved Successful", Toast.LENGTH_LONG).show();
                    break;

                case "invalid authkey":
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[driverSavePayment]", ParsedString);
                    break;

                case "employee id does not exist":
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[driverSavePayment]", ParsedString);
                    break;

                case "voucher number already exist":
                    Toast.makeText(getActivity().getApplicationContext(),
                            "VoucherNumber Already Exists", Toast.LENGTH_LONG)
                            .show();
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[driverSavePayment]", ParsedString);
                    break;

                case "unknown error":
                    ExceptionMessage
                            .exceptionLog(getActivity(), this.getClass().toString()
                                    + " " + "[driverSavePayment]", ParsedString);
                    break;

                default:
                    Toast.makeText(getActivity(), "Try after sometime...",
                            Toast.LENGTH_SHORT).show();
                    ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                                    .toString() + " " + "[driverSavePayment]",
                            "Try after sometime...");
                    break;

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[driverSavePayment]", e.toString());
        }

    }

    public String driverJParser(String response) {

        String jsonData = null;
        try {
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
                    ExceptionMessage.exceptionLog(getActivity(), this
                                    .getClass().toString() + " " + "[driverJParser]",
                            e.toString());
                }

            else {

            }
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[driverJParser]", e.toString());
        }

        return jsonData;

    }

    public void DriverPayment() {

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        paymentDate = df.format(c.getTime());
        db = new DBAdapter(getActivity());
        db.open();
        // mEmployeeId = "218";
        mEmployeeId = db.checkDrvierTableforDataExist(DriverList.sDriverName);
        db.close();
        mFromDate = DriverList.fromDate;
        mToDate = DriverList.toDate;
        voucherImg = "";
        SendToWebService send = new SendToWebService(getActivity(), mDriverCleaner);

        // if (send.isConnectingToInternet()) {
        try {
            send.execute("18", "ManagePayment", mEmployeeId,
                    mVoucherNum, mFromDate, mToDate, sBata, sDriverTotalPay,
                    paymentDate, "0.0", sMileageDeductionTotal, voucherImg, "4");

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Try after sometime...",
                    Toast.LENGTH_SHORT).show();

            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[DriverPayment]", e.toString());
        }
    }
}
