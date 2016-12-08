package com.example.macos.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.PositionData;
import com.example.macos.interfaces.iRequestNetwork;
import com.example.macos.libraries.Logger;
import com.example.macos.receiver.AlarmSetReceiver;
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
 * Created by Microsoft on 12/8/16.
 */

public class NetworkHelper {

    public void getToken(Context context, iRequestNetwork callback) {
        new GetTokenAsynctask(context, callback).execute();
    }

    /*
          UPLOAD POSITION DATA
     */
    public class GetTokenAsynctask extends AsyncTask<Void, Void, String> {
        private SharedPreferenceManager pref;
        iRequestNetwork callback;

        public GetTokenAsynctask(Context context, iRequestNetwork callback) {
            this.callback = callback;
            pref = new SharedPreferenceManager(context);
        }

        @Override
        protected void onPreExecute() {
            Logger.error("start get token");
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(GlobalParams.BASED_LOGIN_URL);
            HttpResponse response;
            try {
                Logger.error("getToken");
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String result = FunctionUtils.convertStreamToString(instream).replace("\"", "");
                    Logger.error("login token:" + result);
                    pref.saveString(GlobalParams.USER_TOKEN, result);
                    instream.close();
                    return result;
                } else {
                    return GlobalParams.GET_TOKEN_FAIL;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error("get token for upload position fail: " + e.toString());
                return GlobalParams.GET_TOKEN_FAIL;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals(GlobalParams.GET_TOKEN_FAIL)) {
                callback.onFailRequest();
            } else {
                callback.onSuccess(result);
            }
        }
    }
}
