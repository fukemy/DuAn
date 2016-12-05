package com.example.macos.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.example.macos.activities.AcICIChecking;
import com.example.macos.adapter.DeviceListActivity;

/**
 * Created by Microsoft on 12/5/16.
 */

public class AlertUtil {
    public static AlertDialog showSimpleAlertWithMessage(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false);
        final AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }
}
