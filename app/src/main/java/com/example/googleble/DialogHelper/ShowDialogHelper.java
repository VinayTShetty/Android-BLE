package com.example.googleble.DialogHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;


import androidx.appcompat.app.AlertDialog;
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
}
