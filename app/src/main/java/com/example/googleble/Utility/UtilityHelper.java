package com.example.googleble.Utility;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

public class UtilityHelper {
    public static   boolean ble_on_off(){
        boolean ble_on_off=false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            ble_on_off=false;
        } else if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled :)
            ble_on_off=false;
        } else {
            // Bluetooth is enabled
            ble_on_off=true;
        }
        return ble_on_off;
    }

    public static void showPermissionDialog(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Permission Denied For Scanning");
        alert.show();
    }

}
