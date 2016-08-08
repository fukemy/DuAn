package com.example.macos.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.macos.activities.MainScreen;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.GlobalParams;

/**
 * Created by devil2010 on 7/23/16.
 */
public class AlarmService extends Service {
    private NotificationManager mManager;
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Logger.error("alarm set in " + System.currentTimeMillis());
        Intent alarmIntent = new Intent(getApplicationContext(), MainScreen.class);
        alarmIntent.putExtra(GlobalParams.IS_ALARM, true);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), GlobalParams.NOTIFICATION_ID, alarmIntent, PendingIntent.FLAG_NO_CREATE);

        Notification n  = new Notification.Builder(getApplicationContext())
                .setContentTitle("title")
                .setContentText("message")
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.logo_thehegeo)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(GlobalParams.NOTIFICATION_ID, n);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
