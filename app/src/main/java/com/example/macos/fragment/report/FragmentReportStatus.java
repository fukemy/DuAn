package com.example.macos.fragment.report;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.macos.adapter.RoadStatusReportAdapter;
import com.example.macos.database.Data;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.ImageModel;
import com.example.macos.libraries.AnimatedExpandableListview;
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
    private AnimatedExpandableListview lv;
    private List<EnDataModel> syncData;
    HashMap<String, List<EnDataModel>> hashMap;
    List<String> listHeader ;
    private FloatingActionButton fab;
    private SharedPreferenceManager pref;
    List<EnDataModel>  failUploadData;
    private LinearLayout lnlOptions;
    boolean isShowFab = true;
    ProgressDialog dialog;
    Gson gson;
    String USER_TOKEN = "";
    private int previousGroup=-1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fr_report_status, container, false);
        pref = new SharedPreferenceManager(getActivity());
        gson = new Gson();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initLayoutAndData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          //  lv.setNestedScrollingEnabled(true);
        }

        lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (lv.isGroupExpanded(groupPosition)) {
                    lv.collapseGroupWithAnimation(groupPosition);
                    previousGroup=-1;
                } else {
                    lv.expandGroupWithAnimation(groupPosition);
                    if(previousGroup!=-1){
                        lv.collapseGroupWithAnimation(previousGroup);
                    }
                    previousGroup=groupPosition;
                }
                return true;
            }

        });

        return rootView;
    }

    private void showFabButton(final boolean isShow){
        TranslateAnimation tran;
        if(isShow)
            tran = new TranslateAnimation(0, 0, lnlOptions.getHeight() * 2, 0);
        else
            tran = new TranslateAnimation(0, 0, 0, lnlOptions.getHeight() * 2);
        tran.setDuration(400);
        tran.setInterpolator(new LinearInterpolator());
        tran.setStartOffset(150);
        tran.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(isShow)
                    fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!isShow)
                    fab.setVisibility(View.GONE);
                showOption(isShow);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fab.startAnimation(tran);
    }
    private void showOption(final boolean isShow){
        TranslateAnimation tran;
        if(!isShow)
            tran = new TranslateAnimation(0, 0, lnlOptions.getHeight(), 0);
        else
            tran = new TranslateAnimation(0, 0, 0, lnlOptions.getHeight());
        tran.setDuration(400);
        tran.setInterpolator(new LinearInterpolator());
        tran.setStartOffset(150);
        tran.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!isShow)
                    lnlOptions.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isShow)
                    lnlOptions.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        lnlOptions.startAnimation(tran);
    }

    private void initLayoutAndData(){
        lnlOptions = (LinearLayout) rootView.findViewById(R.id.lnlOption);
        lv = (AnimatedExpandableListview) rootView.findViewById(R.id.lvExp);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.bringToFront();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isShowFab = !isShowFab;
//                showFabButton(isShowFab);
                uploadData();
            }
        });


        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                List<EnDataModel> enList = new ArrayList<>();
                syncData = new ArrayList<>();

                for (Data d : listData) {
                    EnDataModel en = gson.fromJson(d.getInput(), EnDataModel.class);
                    enList.add(en);
                    if(!d.getIsUploaded())
                        syncData.add(en);
                }

                summaryData(enList);

                RoadStatusReportAdapter adapter = new RoadStatusReportAdapter(getChildFragmentManager(), listHeader, hashMap, getActivity());
                lv.setAdapter(adapter);

                if(syncData.size() == 0){
                    fab.setVisibility(View.GONE);
                }else{
                    fab.setVisibility(View.VISIBLE);
                }

            }else{
                fab.setVisibility(View.GONE);
            }
        }
    }


    private void uploadData(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Bạn cần kết nối internet trước khi upload dữ liệu!");
            builder.setPositiveButton("ok", null);
            builder.show();
            return;
        }
        failUploadData = new ArrayList<>();
        syncData = new ArrayList<>();
        dialog= new ProgressDialog(getActivity());
        dialog.setMessage("Đang chuyển đổi dữ liệu trước khi upload...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                List<EnDataModel> enList = new ArrayList<>();
                for (Data d : listData) {
                    EnDataModel en = gson.fromJson(d.getInput(), EnDataModel.class);
                    enList.add(en);
                    if(!d.getIsUploaded())
                        syncData.add(en);
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
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if(isAcceptUpload) {
                        getToken();
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

//                new UploadData().execute();
                syncAndUploadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncAndUploadData(){
        if(order > syncData.size() - 1)
            return;

        final EnDataModel uploadData = syncData.get(order);

        final Thread thread = new Thread() {
            public void run() {
                try {
//                    ContentResolver cr = getActivity().getContentResolver();
                    for (ImageModel imgUri : uploadData.getListImageData()) {
                        Uri uri = Uri.parse(imgUri.getImagePath());
                        try {
//                            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
//                            Bitmap b = FunctionUtils.scaleBitmap(bitmap, 200, 200);
//                            String base64value = FunctionUtils.convertBitMapToString(b);
//                            Bitmap bitmap = FunctionUtils.decodeSampledBitmapFromFile(uri.getPath(), dm.widthPixels, dm.widthPixels);
                            Bitmap bitmap = FunctionUtils.decodeSampledBitmapFromFile(uri.getPath(), 800, 1100);
                            Logger.error("decode size: " + FunctionUtils.sizeOf(bitmap));
                            String base64value = FunctionUtils.convertBitMapToString(bitmap);
                            imgUri.setImageDataByte(base64value);
                            bitmap = null;
                        } catch (Exception e) {
                            Logger.error("Exception get Image: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Logger.error("Error sync data: " + e.toString());
                    e.printStackTrace();
                } finally {
                    new UploadData(uploadData).execute();
                }
            }
        };

        final Thread threadSleep = new Thread() {
            public void run() {
                try {
                    Thread.sleep(5000);
                }catch(Exception e){
                }finally {
                    thread.start();
                }
            }
        };
        threadSleep.start();
    }

    int order;
    boolean IS_UPLOAD_SUSSCESS = true;
    private class UploadData extends AsyncTask<Void, Void, String> {
        String url;
        EnDataModel uploadData;

        public UploadData(EnDataModel uploadData){
            url = FunctionUtils.encodeUrl(GlobalParams.BASED_POST_URL + USER_TOKEN);
//            url = "http://192.168.1.142:8080/UploadSample/Upload";
//            url = "http://10.0.2.2:8080/UploadSample/Upload";
            Logger.error("Url: " + url);
            this.uploadData = uploadData;
        }

        @Override
        protected void onPreExecute() {
            Logger.error("Order: " + order + " in size: " + syncData.size());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("Đang upload dữ liệu thứ " + (order + 1) + "...");
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            String responseResult = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json; charset=utf-8");
            post.setHeader("Accept","application/json");
            HttpResponse response;
            try {
                StringEntity entityData = new StringEntity(uploadData.toString(), HTTP.UTF_8);
//                EnDataModelUpload enDataModelUpload = new EnDataModelUpload(uploadData);
//                StringEntity entityData = new StringEntity(gson.toJson(enDataModelUpload), HTTP.UTF_8);
//                Logger.error("push: " + gson.toJson(enDataModelUpload));
//                StringEntity entityData = new StringEntity(uploadData.toString(), HTTP.UTF_8);
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
        protected void onPostExecute(final String result) {
            Logger.error("response: " + result);
            if(!result.contains("Successfull")){
                uploadData = null;
                IS_UPLOAD_SUSSCESS = false;
            }else{
                syncDataOffline(uploadData);
            }
            if (order < syncData.size() - 1) {
                order = order + 1;
                syncAndUploadData();
            } else{
//                    Toast.makeText(getActivity(), "Đã upload xong dữ liệu cuối cùng mới nhất!", Toast.LENGTH_SHORT).show();
                pref.saveBoolean(GlobalParams.IS_SYNC_TODAY, true);

//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.setMessage("Đang đồng bộ lại dữ liệu...");
//                    }
//                });


                final Thread thread = new Thread() {
                    public void run() {
                        try {
                            //syncDataOffline();
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
                                    builder.setTitle("Đã upload xong dữ liệu!");
                                    builder.setMessage("Kết quả trả về: \n \"" + result.replace("\"","").replace(",","").trim() + "\"");
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

    private void syncDataOffline(EnDataModel dataModel){
        Logger.error("syncDataOffline");
        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                Logger.error("syncDataOffline");
                for (Data d : listData) {

                    EnDataModel e = gson.fromJson(d.getInput(), EnDataModel.class);
                    if(e.getDaValue().getDataID().equals(dataModel.getDaValue().getDataID())
                            && e.getDaValue().getMaDuong().equals(dataModel.getDaValue().getMaDuong())
                            && e.getDaValue().getThangDanhGia().equals(dataModel.getDaValue().getThangDanhGia())
                            && e.getDaValue().getTuyenSo().equals(dataModel.getDaValue().getTuyenSo())
                            && e.getDaValue().getDataType().equals(dataModel.getDaValue().getDataType())) {
                        Logger.error("found data id:" + d.getID());
                        d.setIsUploaded(true);
//                        dataList.add(e);
                        DatabaseHelper.updateData(d);
                    }
                }
            }
        }

//        DatabaseHelper.clearData();
//        for(EnDataModel e : dataList){
//            DatabaseHelper.insertData(gson.toJson(e));
//        }
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
