package com.example.googleble.DialogHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;


import androidx.appcompat.app.AlertDialog;

import com.example.googleble.R;


public class ShowDialogHelper {
    Activity mActivity;
    Dialog mAlertDialog;
    public ShowDialogHelper(Activity loc_mActivity) {
        this.mActivity = loc_mActivity;
    }
    public void errorDialog(final String message) {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialog != null && mAlertDialog.isShowing()) {

                    } else {
                      /*  android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                        builder.setMessage(message).setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        mAlertDialog = builder.create();
                        mAlertDialog.show();*/


                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        mAlertDialog = builder.create();
                        mAlertDialog.show();

                    }




                }
            });
        }
    }

    public void e(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("")
                .setPositiveButton("s", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("a", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
                builder.create();
                builder.show();

    }
}
