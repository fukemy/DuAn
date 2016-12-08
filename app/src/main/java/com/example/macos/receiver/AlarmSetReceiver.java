package com.example.macos.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;

import com.example.macos.activities.MainScreen;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.PositionData;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.util.List;

/**
 * Created by devil2010 on 7/23/16.
 */
public class AlarmSetReceiver extends BroadcastReceiver
{
    private String USER_TOKEN = "";
    SharedPreferenceManager pref;
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        pref = new SharedPreferenceManager(context);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

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

        boolean IS_ACCEPT_NOTIFICATION = pref.getBoolean(GlobalParams.IS_ACCEPT_NOTIFICATION, true);
        if(IS_ACCEPT_NOTIFICATION) {
            Logger.error("Show notification");
            Intent alarmIntent = new Intent(context, MainScreen.class);
            alarmIntent.putExtra(GlobalParams.IS_ALARM, true);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(context, GlobalParams.NOTIFICATION_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Notification n = new Notification.Builder(context)
                    .setContentTitle("Thông báo")
                    .setContentText("Upload dữ liệu tuần đường hôm nay!")
                    .setContentIntent(pIntent)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true).build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            notificationManager.notify(GlobalParams.NOTIFICATION_ID, n);
        }

        uploadPoitionsInSilent();
    }

    private void uploadPoitionsInSilent(){
        getToken();
    }

    private void getToken(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(GlobalParams.BASED_LOGIN_URL);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = FunctionUtils.convertStreamToString(instream).replace("\"","");
                Logger.error("login token:" + result);
                USER_TOKEN = result;

                pref.saveString(GlobalParams.USER_TOKEN, USER_TOKEN);
                instream.close();

                new UploadUserPositionTrackingData().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
          UPLOAD POSITION DATA
     */
    private class UploadUserPositionTrackingData extends AsyncTask<Void, Void, String> {
        private String result,USER;
        private List<PositionData> positionDatas;
        String url;
        Gson gson;

        public UploadUserPositionTrackingData(){
            USER = pref.getString(GlobalParams.USERNAME, "dungdv");
            result = "";
            gson = new Gson();
            url = FunctionUtils.encodeUrl(GlobalParams.BASED_UPLOAD_POSITION_URL + USER_TOKEN);
            Logger.error("Url position to upload: " + url);
        }
        @Override
        protected void onPreExecute() {
            positionDatas = DatabaseHelper.getPositionDataByUser(USER);
            Logger.error("uploading position data: " + gson.toJson(positionDatas));
        }

        @Override
        protected String doInBackground(Void... voids) {
            if(positionDatas.size() == 0){
                return "error positionDatas.size() == 0";
            }

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json; charset=utf-8");
            post.setHeader("Accept","application/json");
            HttpResponse response;
            try {
                StringEntity entityData = new StringEntity(gson.toJson(positionDatas), HTTP.UTF_8);
                post.setEntity(entityData);
                response = httpclient.execute(post);
                Logger.error("status code:" + response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = FunctionUtils.convertStreamToString(instream);
                    instream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error("exception:  " + e.getMessage());
                return "wrong";
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            Logger.error("result upload position : " + result);
            if (result.contains("successful")) {
                DatabaseHelper.deletePositionForUser(USER);
            } else {
                Logger.error("upload position for user : " + USER + " fail!");
            }
        }
    }
}
