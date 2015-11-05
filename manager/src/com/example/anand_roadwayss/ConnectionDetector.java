package com.example.anand_roadwayss;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;

public class ConnectionDetector {

    private Activity _context;

    public ConnectionDetector(Activity context) {
        this._context = context;
    }

    public void ConnectingToInternet() {
        alertdialog();

        // ConnectivityManager connectivity = (ConnectivityManager)
        // _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // if (connectivity != null)
        // {
        // NetworkInfo[] info = connectivity.getAllNetworkInfo();
        // if (info != null)
        // for (int i = 0; i < info.length; i++)
        // if (info[i].getState() == NetworkInfo.State.CONNECTED)
        // {
        // return true;
        // }
        //
        // }
        // return false;
    }

    private void alertdialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(_context);
        alert.setTitle("Internet"); // Set Alert dialog title
        // here
        alert.setMessage("Please Connect Internet ...."); // Message
        // here
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                    } // End of onClick(DialogInterface dialog, int
                    // whichButton)
                }); // End of alert.setPositiveButton
        alert.setNeutralButton("Go TO Settings", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                _context.startActivityForResult(new Intent(
                        android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        }); // End of alert.setNegativeButton

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}
