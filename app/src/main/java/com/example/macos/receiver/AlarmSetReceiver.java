package com.example.macos.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.example.macos.activities.MainScreen;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.GlobalParams;

/**
 * Created by devil2010 on 7/23/16.
 */
public class AlarmSetReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
//
//        //now code:
//        final SharedPreferenceManager pref = new SharedPreferenceManager(context);
//        boolean worked_today = pref.getBoolean(GlobalParams.IS_WORKED_TODAY, false);
//        if(!worked_today) {
//        }
//
//        createNotification(context, "Thông báo","Upload dữ liệu tuần đường hôm nay!");
//        boolean sync_today = pref.getBoolean(GlobalParams.IS_SYNC_TODAY, false);
//        if(!sync_today) {
//        }
//
//        pref.saveBoolean(GlobalParams.IS_SYNC_TODAY, false);
        wl.release();


        Logger.error("Show notification");
        Intent alarmIntent = new Intent(context, MainScreen.class);
        alarmIntent.putExtra(GlobalParams.IS_ALARM, true);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, GlobalParams.NOTIFICATION_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification n  = new Notification.Builder(context)
                .setContentTitle("Thông báo")
                .setContentText("Upload dữ liệu tuần đường hôm nay!")
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.logo_thehegeo)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        notificationManager.notify(GlobalParams.NOTIFICATION_ID, n);
    }

    /*

    private void createNotification(Context mContext, String notificationTitle, String notificationMessage){
        Intent intent = new Intent(mContext, MainScreen.class);
        intent.putExtra(GlobalParams.IS_ALARM, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_NO_CREATE);

        Notification n  = new Notification.Builder(mContext)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.logo_thehegeo)
                .setAutoCancel(true).build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.contentIntent = pIntent;

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, n);
    }

    public PendingIntent isNotificationVisible(Context mContext) {
        Intent notificationIntent = new Intent(mContext, MainScreen.class);
        PendingIntent test = PendingIntent.getActivity(mContext, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test;
    }

    public void SetAlarm(Context context) {
            Logger.error("alarm set in " + System.currentTimeMillis());
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmSetReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, NOTIFICATION_ID, i, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            calendar.set(Calendar.HOUR_OF_DAY, 22);  //HOUR
            calendar.set(Calendar.MINUTE, 0);       //MIN
//            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi); // Millisec * Second * Minute
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 4000, pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmSetReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
    */
}
