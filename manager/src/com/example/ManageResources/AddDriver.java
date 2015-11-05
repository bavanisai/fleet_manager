
/**
 *
 */
package com.example.ManageResources;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.example.Interface.IManageResources;
import com.example.anand_roadwayss.ConnectionDetector;
import com.example.anand_roadwayss.DBAdapter;
import com.example.anand_roadwayss.ExceptionMessage;
import com.example.anand_roadwayss.IpAddress;
import com.example.anand_roadwayss.R;
import com.example.anand_roadwayss.SendToWebService;

public class AddDriver extends Fragment implements IManageResources {
    DBAdapter db;
    ListView driverList;
    EditText eTNewEntryEmpName, eTNewEntryPhNo, eTNewEntryCommission,
            eTNewEntrySalary, eTNewEntryAddress, eTNewEntryLicenseNo;
    ImageView iVempPhoto;
    final int CAMERA_CAPTURE = 1, REQUEST_CODE_PICK_CONTACTS = 2;
    byte[] byteArray;
    ConnectionDetector cd = new ConnectionDetector(getActivity());
    Button manVBtnSave;
    ImageButton contactImage;
    String srvrStatus;
    ContentValues cv = new ContentValues();

    String drvName, drvPhNo, drvCommission,
            drvSalary, drvLicense, drvAddress, LocalEmpId, srvrEmpId;
    public static View view;
    IManageResources mInterfaceManageResources = this;
    // SendToWebService send;
    private Uri uriContact;
    private String contactID;
    TextView message,ok,cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // send = new SendToWebService(getActivity(),
        // mInterfaceManageResources);

        view = inflater.inflate(R.layout.fragment_new_entry_addemp, container,
                false);
        setTextValue(view);
        manVBtnSave.setText("ADD");
        iVempPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    // use standard intent to capture an image
                    Intent captureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    // we will handle the returned data in onActivityResult
                    startActivityForResult(captureIntent, CAMERA_CAPTURE);
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString()
                            + " "
                            + "[iVempPhoto.setOnClickListener]", e.toString());
                }
            }
        });

        manVBtnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ((VehicleEntryActivity) getActivity()).refresh(0);
                try {

                    getTextValue();
                    if (drvName.equals("")) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE DRIVER NAME",
                                Toast.LENGTH_LONG).show();
                    } else if ((drvPhNo.equals(""))) {
                        Toast.makeText(getActivity(),
                                "PLEASE SELECT THE PHONE NUMBER",
                                Toast.LENGTH_LONG).show();
                    } else if ((drvCommission.equals(""))) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE COMMISSION",
                                Toast.LENGTH_LONG).show();
                    } else if ((drvSalary.equals(""))) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE SALARY", Toast.LENGTH_LONG)
                                .show();
                    }else if(drvAddress.equals(""))
                    {Toast.makeText(getActivity(),
                                "PLEASE ENTER THE ADDRESS", Toast.LENGTH_LONG)
                                .show();}
                    else if (drvLicense.equals("")) {
                        Toast.makeText(getActivity(),
                                "PLEASE ENTER THE LICENSE", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        DBAdapter db = new DBAdapter(getActivity());
                        db.open();
                        LocalEmpId = db.checkDrvierTableforDataExist(drvName);
                        if (!LocalEmpId.equals("NOT EXIST")) {// get EMp ID for

                            alertUpdate();
                        } else {
                            SendToWebService send = new SendToWebService(
                                    getActivity(), mInterfaceManageResources);
                            // if (send.isConnectingToInternet()) {
                            try {
                                send.execute("10", "ManageEmployee",
                                        "Driver", drvName, "null", "null",
                                        drvLicense, drvPhNo, drvAddress,
                                        drvCommission, drvSalary, "4", "00");
                            } catch (Exception e) {
                                Toast.makeText(getActivity(),
                                        "Try after sometime...",
                                        Toast.LENGTH_SHORT).show();
                                ExceptionMessage
                                        .exceptionLog(
                                                getActivity(),
                                                this.getClass().toString()
                                                        + " "
                                                        + "[manVBtnSave.setOnClickListener]",
                                                e.toString());
                            }

                        }

                        db.close();

                    }

                } catch (SQLiteException e) {
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString()
                            + " "
                            + "[manVBtnSave.setOnClickListener]", e.toString());
                } catch (Exception e) {
                    ExceptionMessage.exceptionLog(getActivity(), this
                            .getClass().toString()
                            + " "
                            + "[manVBtnSave.setOnClickListener]", e.toString());
                }

            }

        });

        contactImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
                ;

            }
        });

        return view;
    }

    public void setTextValue(View view) {
        eTNewEntryEmpName = (EditText) view
                .findViewById(R.id.eTNewEntryEmpName);
        eTNewEntryPhNo = (EditText) view.findViewById(R.id.eTNewEntryPhNo);
        eTNewEntryCommission = (EditText) view
                .findViewById(R.id.eTNewEntryCommission);
        eTNewEntrySalary = (EditText) view.findViewById(R.id.eTNewEntrySalary);
        eTNewEntryAddress = (EditText) view
                .findViewById(R.id.eTNewEntryAddress);
        eTNewEntryLicenseNo = (EditText) view
                .findViewById(R.id.eTNewEntryLicenseNo);
        iVempPhoto = (ImageView) view.findViewById(R.id.iVempPhoto);
        manVBtnSave = (Button) view
                .findViewById(R.id.btnFragmentNewEntryEmployeeListSave);
        contactImage = (ImageButton) view.findViewById(R.id.contactImage);

    }

    public void getTextValue() {
        drvName = eTNewEntryEmpName.getText().toString().trim()
                .toUpperCase(Locale.getDefault());
        drvCommission = eTNewEntryCommission.getText().toString().trim();
        drvSalary = eTNewEntrySalary.getText().toString().trim();
        drvLicense = eTNewEntryLicenseNo.getText().toString().trim();
        drvAddress = eTNewEntryAddress.getText().toString().trim();
        drvPhNo = eTNewEntryPhNo.getText().toString().trim();

    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (DriverList.DriverName != null) {
                switch (DriverList.DriverName) {
                    case "refresh":
                        refreshActivity();
                        break;

                    default:

                        populate(DriverList.DriverName);
                        break;
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == CAMERA_CAPTURE && resultCode == getActivity().RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                iVempPhoto.setImageBitmap(thumbnail);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//				thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            } else if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == getActivity().RESULT_OK) {
                uriContact = data.getData();

                retrieveContactName();
                retrieveContactNumber();
                //	retrieveContactPhoto();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Sorry Unable to get Data",
                    Toast.LENGTH_LONG).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[onActivityResult]", e.toString());
        }

    }

    //Contact Info

    private void retrieveContactPhoto() {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.
                    openContactPhotoInputStream(getActivity().getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                                    new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                iVempPhoto.setImageBitmap(photo);
//		                ImageView imageView = (ImageView) findViewById(R.id.img_contact);
//		                imageView.setImageBitmap(photo);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass().toString()
                    + " " + "[ retrieveContactPhoto() ]", e.toString());
            e.printStackTrace();
        }

    }

    private void retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getActivity().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        // Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();
        eTNewEntryPhNo.setText(contactNumber);

    }

    private void retrieveContactName() {

        String contactName = null;

        // querying contact data store
        Cursor cursor = getActivity().getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();
        eTNewEntryEmpName.setText(contactName);
//		TextView t1 = (TextView)getActivity().findViewById(R.id.textView1);
//		t1.setText(contactName);


        //Log.d(TAG, "Contact Name: " + contactName);

    }

    //End
    public byte[] getBytesFromBitmap(ImageView DriverPhoto2) {
        try {
            DriverPhoto2.buildDrawingCache();
            Bitmap bmap = DriverPhoto2.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error while Uploading Photo",
                    Toast.LENGTH_SHORT).show();
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString(), e.toString());
        }
        return byteArray;
    }

    public String jsonParsing(String response) {
        String jsonData = null;
        if (response != null)
            try {
                JSONObject jsonResponse = new JSONObject(response);

                jsonData = jsonResponse.getString("d");
                JSONObject d = new JSONObject(jsonData);
                srvrStatus = d.getString("status").trim();
                srvrEmpId = d.getString("employeeId").trim();
                return jsonData;

            } catch (JSONException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[jsonParsing]", e.toString());
            }

        return jsonData;

    }

    private void alertUpdate()
    {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_two_btn, null);
        builder.setView(dialogView);
        final AlertDialog alertDialog1 = builder.create();
        message = (TextView) dialogView.findViewById(R.id.textmsg);
        ok = (TextView) dialogView.findViewById(R.id.textOkBtn);
        cancel=(TextView)dialogView.findViewById(R.id.textCancelBtn);
        message.setText("Update "+DriverList.DriverName+" ?");
        View v1= inflater.inflate(R.layout.title_dialog_layout, null);
        alertDialog1.setCustomTitle(v1);

        ok.setText("UPDATE");
        cancel.setText("CANCEL");
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendToWebService send = new SendToWebService(getActivity(), mInterfaceManageResources);
                send.execute("10", "ManageEmployee", "Driver", drvName,
                        "null", "null", drvLicense, drvPhNo, drvAddress,
                        drvCommission, drvSalary, "1", LocalEmpId);
                alertDialog1.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(getActivity(), "Update Cancelled", Toast.LENGTH_LONG).show();
                alertDialog1.dismiss();
            }
        });
        Resources resources =alertDialog1.getContext().getResources();
        int color = resources.getColor(R.color.white);
        alertDialog1.show();
        int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
        View titleDivider = alertDialog1.getWindow().getDecorView().findViewById(titleDividerId);
        titleDivider.setBackgroundColor(color);

    }

    public void refreshActivity() {

        Intent intent = new Intent(getActivity(), DriverEntryActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void populate(String driName) {
        try {
            manVBtnSave.setText("UPDATE");
            DBAdapter db = new DBAdapter(getActivity());
            db.open();

            Cursor cursor = db.getDriverEntry(driName);

            if (cursor.moveToFirst()) {
                // update view
                eTNewEntryEmpName.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyName())));

                eTNewEntryPhNo.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyPhNo())));

                eTNewEntryCommission.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyCommission())));
                eTNewEntrySalary.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeySalary())));
                eTNewEntryAddress.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyAddr())));
                eTNewEntryLicenseNo.setText(cursor.getString(cursor
                        .getColumnIndex(DBAdapter.getKeyLicNo())));
                byte[] bitmapData = cursor.getBlob(cursor
                        .getColumnIndex(DBAdapter.getKeyPhoto()));
                ByteArrayInputStream imageStream = new ByteArrayInputStream(
                        bitmapData);
                Bitmap theImage = BitmapFactory.decodeStream(imageStream);

                if (theImage != null) {
                    iVempPhoto.setImageBitmap(theImage);
                }
                cursor.close();
                db.close();

            }
        } catch (SQLiteException e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[populate]", e.toString());
        } catch (Exception e) {
            ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                    .toString() + " " + "[populate]", e.toString());
        }
    }

    @Override
    public void onTaskComplete(String result) {
        if (result.equals("No Internet")) {
            ConnectionDetector cd = new ConnectionDetector(getActivity());
            cd.ConnectingToInternet();
        } else if (result.contains("refused") || result.contains("timed out")) {
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

            jsonParsing(result);

            DBAdapter db = new DBAdapter(getActivity());
            try {
                db.open();
                setTextValue(view);
                getTextValue();
                cv.put(DBAdapter.getKeyName(), drvName);
                cv.put(DBAdapter.getKeyPhNo(), drvPhNo);
                cv.put(DBAdapter.getKeyAddr(), drvAddress);
                cv.put(DBAdapter.getKeyLicNo(), drvLicense);
                cv.put(DBAdapter.getKeySalary(), drvSalary);
                cv.put(DBAdapter.getKeyCommission(), drvCommission);
                getBytesFromBitmap(iVempPhoto);
                cv.put(DBAdapter.getKeyPhoto(), byteArray);
                cv.put(DBAdapter.getKeyManagetype(), "Driver");

                switch (srvrStatus) {

                    case "inserted":
                        cv.put(DBAdapter.getKeyEmployeeId(), srvrEmpId);
                        long id = db.insertContact(DBAdapter.getEmployeeDetails(),
                                cv);
                        if (id != -1) {

                            Toast.makeText(getActivity(), "Data Saved Successfull",
                                    Toast.LENGTH_LONG).show();
                        }
                        refreshActivity();
                        break;

                    case "phonenumber already exist":
                        String name = db.getEmployeeName(srvrEmpId,
                                DBAdapter.getEmployeeDetails(),
                                DBAdapter.getKeyName());
                        Toast.makeText(getActivity(),
                                name + "  is having same mobile number",
                                Toast.LENGTH_LONG).show();
                        break;

                    case "licensenumber already exist":
                        String name1 = db.getEmployeeName(srvrEmpId,
                                DBAdapter.getEmployeeDetails(),
                                DBAdapter.getKeyName());
                        Toast.makeText(getActivity(),
                                name1 + "  is having same License number",
                                Toast.LENGTH_LONG).show();
                        break;
						
					 case "emailid already exist":
                        String name2 = db.getEmployeeName(srvrEmpId,
                                DBAdapter.getEmployeeDetails(),
                                DBAdapter.getKeyName());
                        Toast.makeText(getActivity(),
                                name2 + "  is having same Email id",
                                Toast.LENGTH_LONG).show();
                        break;

                    case "updated":
                        cv.put(DBAdapter.getKeyEmployeeId(), srvrEmpId);
                        long rowsaffected = db.updateContact(
                                DBAdapter.getEmployeeDetails(), cv, drvName,
                                "Driver");
                        if (rowsaffected != -1) {
                            Toast.makeText(getActivity(), "Data Updated",
                                    Toast.LENGTH_LONG).show();
                        }
                        refreshActivity();
                        break;

                    case "invalid authkey":
                        Toast.makeText(getActivity(), "Failed Try Again",
                                Toast.LENGTH_LONG).show();
                        ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString() + " " + "[onTaskComplete]",
                                srvrStatus);
                        break;

                    case "unknown error":
                        Toast.makeText(getActivity(), "Failed Try Again",
                                Toast.LENGTH_LONG).show();
                        // ExceptionMessage.exceptionLog(getActivity(), this
                        // .getClass().toString() + " " + "[onTaskComplete]",
                        // srvrStatus);

                        break;

                    default:
                        ExceptionMessage.exceptionLog(getActivity(), this
                                        .getClass().toString() + " " + "[onTaskComplete]",
                                srvrStatus);
                        break;

                }

            } catch (SQLiteException e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onTaskComplete]", e.toString());
            } catch (Exception e) {
                ExceptionMessage.exceptionLog(getActivity(), this.getClass()
                        .toString() + " " + "[onTaskComplete]", e.toString());
            }
            db.close();
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            DriverEntryActivity.pos=2;
        }
    }
}
