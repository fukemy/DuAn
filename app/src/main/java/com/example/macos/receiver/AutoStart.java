package com.example.macos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.macos.service.AlarmService;
import com.example.macos.libraries.Logger;

/**
 * Created by devil2010 on 7/23/16.
 */
public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Logger.error("ON BOOT COMPLETE");
            Intent service = new Intent(context, AlarmService.class);
            context.startService(service);
        }
    }
}
