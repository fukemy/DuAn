package com.example.macos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("NetworkChangeReceiver triggered");
        SharedPreferenceManager pref = new SharedPreferenceManager(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfor = cm.getActiveNetworkInfo();
        if(netInfor != null && netInfor.isConnected()) {
            System.out.println("connected to internet");
            pref.saveString(GlobalParams.USER_ONLY_TYPE, GlobalParams.TYPE_WIFI);
        }else {
            System.out.println("disconnect from internet");
            pref.saveString(GlobalParams.USER_ONLY_TYPE, GlobalParams.TYPE_OFFLINE);
        }
    }
}
