package com.example.macos.report;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.macos.adapter.RoadStatusReportAdapter;
import com.example.macos.database.Data;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.ImageModel;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.CustomFragment;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class FragmentReportStatus extends CustomFragment {

    private View rootView;
    private ExpandableListView lv;
    private List<EnDataModel> syncData;
    HashMap<String, List<EnDataModel>> hashMap;
    List<String> listHeader ;
    private FloatingActionButton fab;
    private SharedPreferenceManager pref;
    List<EnDataModel> enDataModelList, failUploadData;
    List<Integer> failUploadOrder;
    ProgressDialog dialog;
    Gson gson;
    String USER_TOKEN = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_report_status, container, false);
        pref = new SharedPreferenceManager(getActivity());
        enDataModelList = new ArrayList<>();
        gson = new Gson();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initLayoutAndData();
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          //  lv.setNestedScrollingEnabled(true);
        //}

        return rootView;
    }

    private void initLayoutAndData(){
        lv = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.bringToFront();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });


        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                List<EnDataModel> enList = new ArrayList<>();
                for (Data d : listData) {
                    enList.add(gson.fromJson(d.getInput(), EnDataModel.class));
                }
                summaryData(enList);

                RoadStatusReportAdapter adapter = new RoadStatusReportAdapter(getChildFragmentManager(), listHeader, hashMap, getActivity());
                lv.setAdapter(adapter);

                syncData = new ArrayList<>();
                for (EnDataModel enDataModel : enList) {
                    if(!enDataModel.isUploaded())
                        syncData.add(enDataModel);
                }
                if(syncData.size() == 0){
                    fab.setVisibility(View.GONE);
                }else{
                    fab.setVisibility(View.VISIBLE);
                }

            }
        }
    }


    private void uploadData(){
        if(pref.getString(GlobalParams.USER_ONLY_TYPE, GlobalParams.TYPE_OFFLINE).equals(GlobalParams.TYPE_OFFLINE)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Bạn cần kết nối internet trước khi upload dữ liệu!");
            builder.setPositiveButton("ok", null);
            builder.show();
            return;
        }
        failUploadData = new ArrayList<>();
        syncData = new ArrayList<>();
        failUploadOrder = new ArrayList<>();
        dialog= new ProgressDialog(getActivity());
        dialog.setMessage("Đang chuyển đổi dữ liệu trước khi upload...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                List<EnDataModel> enList = new ArrayList<>();
                for (Data d : listData) {
                    enList.add(gson.fromJson(d.getInput(), EnDataModel.class));
                }
                for (EnDataModel enDataModel : enList) {
                    if(!enDataModel.isUploaded())
                        syncData.add(enDataModel);
                }
            }
        }

        final Thread thread = new Thread(){
            boolean isAcceptUpload = true;
            public void run(){
                Logger.error("size upload : " + syncData.size());
                try{
                    ContentResolver cr = getActivity().getContentResolver();

                    if(syncData.size() == 0){
                        isAcceptUpload = false;
                        return;
                    }else {
                        Logger.error("sync size: " + syncData.size());
                        for (EnDataModel en : syncData) {
                            for (ImageModel imgUri : en.getListImageData()) {
                                Uri uri = Uri.parse(imgUri.getImagePath());
                                try {
                                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);

                                    Bitmap b = FunctionUtils.scaleBitmap(bitmap, 1000, 1000);
                                    String base64value = FunctionUtils.encodeTobase64(b);

                                    imgUri.setImageDataByte(base64value);
                                } catch (Exception e) {
                                    Logger.error("Exception get Image: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                            enDataModelList.add(en);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if(isAcceptUpload) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setMessage("Đang lấy token...");
                            }
                        });
                        getToken();
//                        new UploadData().execute();
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null)
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                Toast.makeText(getActivity(), "Dữ liệu đã được upload rồi!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }
        };
        thread.start();

    }


    private void getToken(){
        order = 0;
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
                new UploadData().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int order;
    boolean IS_UPLOAD_SUSSCESS = true;
    private class UploadData extends AsyncTask<Void, Void, String> {
        String url;

        public UploadData(){
            url = FunctionUtils.encodeUrl(GlobalParams.BASED_POST_URL + USER_TOKEN);
//            url = "http://192.168.1.34:8080/UploadSample/Upload";
            Logger.error("Url: " + url);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("Đang upload dữ liệu thứ " + order + "...");
                }
            });
        }

        @Override
        protected void onPreExecute() {
            Logger.error("Order: " + order + " in size: " + enDataModelList.size());
        }

        @Override
        protected String doInBackground(Void... params) {
            String responseResult = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setHeader("Accept","application/json");
            post.setHeader("Content-Type","application/json");
            HttpResponse response;
            try {
                StringEntity entityData = new StringEntity(enDataModelList.get(order).toString(), HTTP.UTF_8);
                post.setEntity(entityData);
                response = httpclient.execute(post);
                Logger.error("status code:" + response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    responseResult = FunctionUtils.convertStreamToString(instream);
                    instream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.error("exception:  " + e.getMessage());
                return "wrong";
            }
            return responseResult;
        }

        @Override
        protected void onPostExecute(String result) {
            Logger.error("response: " + result);
            if(!result.contains("Successfull")){
                failUploadData.add(enDataModelList.get(order));
                failUploadOrder.add(order);
                IS_UPLOAD_SUSSCESS = false;
            }
            enDataModelList.get(order).setUploaded(true);
            if(order < enDataModelList.size() - 1){
                order = order + 1;
                new UploadData().execute();
            }else if(order == enDataModelList.size() - 1) {
                if (IS_UPLOAD_SUSSCESS) {
                    Toast.makeText(getActivity(), "Upload thành công!", Toast.LENGTH_SHORT).show();
                    enDataModelList.clear();
                    pref.saveBoolean(GlobalParams.IS_SYNC_TODAY, true);
                } else {
                    Toast.makeText(getActivity(), "Có lỗi xảy ra trong quá trình upload!", Toast.LENGTH_SHORT).show();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Đang đồng bộ lại dữ liệu...");
                    }
                });

                final Thread thread = new Thread() {
                    public void run() {
                        try {
                            syncDataOffline();
                        } catch (Exception e) {
                            Logger.error("Error sync data: " + e.toString());
                            e.printStackTrace();
                        } finally {
                            if (dialog != null)
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fab.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide));
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Upload dữ liệu thành công!");
                                    builder.setPositiveButton("ok", null);
                                    builder.show();
                                }
                            });


                        }
                    }
                };
                thread.start();
            }
        }
    }

    private void syncDataOffline(){
        List<Data> listData = DatabaseHelper.getData();
        List<EnDataModel> dataList = new ArrayList<>();
        if(listData!= null) {
            if (listData.size() != 0) {
                for (Data d : listData) {
                    EnDataModel e = gson.fromJson(d.getInput(), EnDataModel.class);
                    e.setUploaded(true);
                    dataList.add(e);
                }
            }
        }

        DatabaseHelper.clearData();
        for(EnDataModel e : dataList){
            DatabaseHelper.insertData(gson.toJson(e));
        }
    }

    private void summaryData(List<EnDataModel> data){
        hashMap = new HashMap<>();
        listHeader=  new ArrayList<>();
        for(EnDataModel en : data){
            if(en.getDaValue().getAction().equals(getResources().getString(R.string.accident)) || en.getDaValue().getAction().equals(getResources().getString(R.string.problem))
                    || en.getDaValue().getAction().equals(getResources().getString(R.string.lastday))) {
            }else{
                if(!hashMap.containsKey(en.getDaValue().getDataName())){
                    List<EnDataModel> statusList = new ArrayList<>();
                    statusList.add(en);
                    listHeader.add(en.getDaValue().getDataName());
                    hashMap.put(en.getDaValue().getDataName(), statusList);
                }else{
                    Iterator myVeryOwnIterator = hashMap.keySet().iterator();
                    while(myVeryOwnIterator.hasNext()) {
                        String key = (String) myVeryOwnIterator.next();
                        if (key.equals(en.getDaValue().getDataName())) {
                            List<EnDataModel> listCurrently = hashMap.get(key);
                                listCurrently.add(en);
                        }
                    }
                }
            }
        }
    }

}
