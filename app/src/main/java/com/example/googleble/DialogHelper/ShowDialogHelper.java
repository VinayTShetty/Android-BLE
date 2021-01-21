package com.example.googleble.DialogHelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;


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
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
                        builder.setMessage(message).setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
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
