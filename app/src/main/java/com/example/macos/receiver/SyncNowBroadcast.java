package com.example.macos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.macos.libraries.Logger;

/**
 * Created by devil2010 on 7/25/16.
 */
public class SyncNowBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.error("SYnc now");
    }
}
