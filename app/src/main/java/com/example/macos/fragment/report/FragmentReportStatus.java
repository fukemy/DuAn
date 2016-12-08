package com.example.macos.fragment.report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.macos.adapter.RoadStatusReportAdapter;
import com.example.macos.database.BlueToothData;
import com.example.macos.database.Data;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.database.PositionData;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.entities.ImageModel;
import com.example.macos.interfaces.iLongClickInterace;
import com.example.macos.interfaces.iRippleControl;
import com.example.macos.libraries.AnimatedExpandableListview;
import com.example.macos.libraries.Logger;
import com.example.macos.report.DiaryReportContent;
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
    List<String> failBlueToothData;
    List<String> failPositonData;
    List<Uri> failImageData;
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

        initLayout();
        initData();
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

    iRippleControl rippleInterface = new iRippleControl() {
        @Override
        public void disableView(View v) {
            v.setEnabled(false);
        }
    };

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

    private void initLayout() {
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

    }

    private void initData(){
        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                List<EnDataModel> enList = new ArrayList<>();
                syncData = new ArrayList<>();

                for (Data d : listData) {
                    EnDataModel en = gson.fromJson(d.getInput(), EnDataModel.class);
                    enList.add(en);
                    if(en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.accident_report).toLowerCase()) ||
                            en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.problem_report).toLowerCase()) ||
                            en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.lastday).toLowerCase())) {
                    }else {
                        syncData.add(en);
//                            Logger.error("add en:  " + en.getDaValue().getAction());
                    }
                }

                summaryData(enList);

                RoadStatusReportAdapter adapter = new RoadStatusReportAdapter(getChildFragmentManager(), listHeader, hashMap, getActivity());
                adapter.setInterface(longClickInterace);
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

    public iLongClickInterace longClickInterace = new iLongClickInterace() {
        @Override
        public void onLongClick(final EnDataModel dataModel,final View v) {
            PopupMenu popup = new PopupMenu(getActivity(), v, Gravity.CENTER_HORIZONTAL);
            popup.getMenuInflater().inflate(R.menu.road_status_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {

                    if(item.getItemId() == R.id.information){
                        final Intent in = new Intent(getActivity(), DiaryReportContent.class);
                        Gson gson = new Gson();
                        in.putExtra("data",  gson.toJson(dataModel));
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            v.setEnabled(false);
                            ActivityOptionsCompat options =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v,
                                            getActivity().getResources().getString(R.string.show_map));
                            getActivity().startActivity(in, options.toBundle());
                            v.setEnabled(true);
                        }else{
                            getActivity().startActivity(in);
                        }

//                    }else if(item.getItemId() == R.id.delete){
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        builder.setMessage("Bạn có chắc chắn muốn xoá bản ghi này?");
//                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                DatabaseHelper.deleteDataTypeItem(dataModel);
//                                initData();
//                            }
//                        });
//                        builder.setNegativeButton("Huỷ", null);
//                        builder.show();

                    }else if(item.getItemId() == R.id.upload) {
                        Toast.makeText(getActivity(),"Chưa hỗ trợ upload từng bản ghi một!",Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
            });

            popup.show();
        }
    };

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
        failBlueToothData = new ArrayList<>();
        failImageData = new ArrayList<>();
        failPositonData = new ArrayList<>();

        syncData = new ArrayList<>();
        dialog= new ProgressDialog(getActivity());
        dialog.setMessage("Đang chuyển đổi dữ liệu trước khi upload...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                for (Data d : listData) {
                    EnDataModel en = gson.fromJson(d.getInput(), EnDataModel.class);
                    if(!d.getIsUploaded()) {
                        if(en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.accident_report).toLowerCase()) ||
                                en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.problem_report).toLowerCase()) ||
                                en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.lastday).toLowerCase())) {
                        }else {
                            syncData.add(en);
//                            Logger.error("add en:  " + en.getDaValue().getAction());
                        }
                    }
                }

            }
        }

        final Thread thread = new Thread(){
            boolean isAcceptUpload = true;
            public void run(){
                Logger.error("size upload : " + syncData.size());
                try{
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

                failUploadData.clear();
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
        new UploadData(uploadData).execute();

    }

    int order, order_upload_image;
    private class UploadData extends AsyncTask<Void, Void, String> {
        String url;
        EnDataModel uploadData;

        public UploadData(EnDataModel uploadData) {
            url = FunctionUtils.encodeUrl(GlobalParams.BASED_POST_DATA_URL + USER_TOKEN);
            Logger.error("Url to upload: " + url);
            Logger.error("data to upload: " + "[\n" + uploadData.toString() + "\n]");
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
            post.setHeader("Accept", "application/json");
            HttpResponse response;
            try {
                StringEntity entityData = new StringEntity("[\n" + uploadData.toString() + "\n]", HTTP.UTF_8);
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
            Logger.error("response upload road data: " + result);
            if (result.contains("Successfull") || result.contains("Image List")) {
                syncDataOffline(uploadData);
            } else {
                failUploadData.add(uploadData);
            }
            order_upload_image = 0;
            List<Uri> listImgUri = new ArrayList<>();
            if(uploadData.getListImageData().size() > 0) {
                for (ImageModel imgUri : uploadData.getListImageData()) {
                    listImgUri.add(Uri.parse(imgUri.getImagePath()));
                }
                new UploadImageData(listImgUri, uploadData).execute();

            }else{
                order_upload_image = 0;
                if (order < syncData.size() - 1) {
                    order = order + 1;
                    syncAndUploadData();

                } else{
                    dialog.dismiss();
                    pref.saveBoolean(GlobalParams.IS_SYNC_TODAY, true);

                    if (dialog != null)
                        if (dialog.isShowing())
                            dialog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(failUploadData.size() == 0) {
                                fab.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide));
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Đã upload xong dữ liệu!");
                                builder.setMessage("Kết quả trả về: Tất cả dữ liệu upload thành công!");
                                builder.setPositiveButton("ok", null);
                                builder.show();
                            }else{
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Thông tin upload dữ liệu.");
                                builder.setMessage("Có " + failUploadData.size() + " trên " + syncData.size() + " dữ liệu upload không thành công."
                                        + "\nKết quả cuối cùng trả về: " + result.replace(" ","").replace(".","").replace("\"","").replace(".","")
                                        .replace("\n","") + "." + "\nBạn có muốn upload lại không?");

                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        uploadData();
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.setCancelable(false);
                                builder.show();
                            }
                        }
                    });
                }
            }
        }
    }
    /*
        UPLOAD IMAGE DATA
    */
    private class UploadImageData extends AsyncTask<Void, Void, String>{
        List<Uri> listUri;
        String url;
        ImageModel imageModel;
        EnDataModel uploadData;

        public UploadImageData(List<Uri> listImgUri, EnDataModel uploadData) {
            this.uploadData = uploadData;
            this.listUri = listImgUri;
            url = FunctionUtils.encodeUrl(GlobalParams.BASED_UPLOAD_IMAGE_URL + USER_TOKEN);
            Logger.error("Url image to upload: " + url);
        }

        @Override
        protected void onPreExecute() {
            Logger.error("Order: " + order + " in size: " + syncData.size());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("Đang upload dữ liệu ảnh!");
                }
            });

            try {
                Bitmap bitmap = FunctionUtils.decodeSampledBitmap(getActivity(), listUri.get(order_upload_image));
//                Bitmap b = FunctionUtils.decodeSampledBitmapFromFile(listUri.get(order_upload_image).getPath(), 800, 1000);
                String base64value = FunctionUtils.convertBitMapToString(bitmap);
                imageModel = new ImageModel();
                imageModel.setImageDataByte(base64value);
                imageModel.setDataID(uploadData.getDaValue().getDataID());
                imageModel.setImageName(System.currentTimeMillis() + ".png");
                Logger.error("image to upload: " + "[\n" + gson.toJson(imageModel) + "\n]");
            } catch (Exception e) {
                Logger.error("Exception get Image: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            if(imageModel == null)
                return "imageModel == null";

            String responseResult = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json; charset=utf-8");
            post.setHeader("Accept", "application/json");
            HttpResponse response;
            try {
                StringEntity entityData = new StringEntity("[\n" + gson.toJson(imageModel) + "\n]", HTTP.UTF_8);
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
            Logger.error("response upload image: " + result);
            if (result.contains("Image List null") || result.contains("imageModel == null")) {
                //store uri that upload fail
//                failImageData.add(listUri.get(order_upload_image));
            } else {
            }

            if(order_upload_image < listUri.size() - 1){
                order_upload_image++;
                new UploadImageData(listUri, uploadData).execute();
            }else{
                order_upload_image = 0;
                if (order < syncData.size() - 1) {
                    order = order + 1;
                    syncAndUploadData();
                } else{
                    dialog.dismiss();
                    pref.saveBoolean(GlobalParams.IS_SYNC_TODAY, true);
                    if (dialog != null)
                        if (dialog.isShowing())
                            dialog.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(failUploadData.size() == 0) {
                                fab.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide));
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Đã upload xong dữ liệu!");
                                builder.setMessage("Kết quả trả về: Tất cả dữ liệu upload thành công!");
                                builder.setPositiveButton("ok", null);
                                builder.show();
                            }else{
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Thông tin upload dữ liệu.");
                                builder.setMessage("Có " + failUploadData.size() + " trên " + syncData.size() + " dữ liệu upload không thành công."
                                        + "\nKết quả cuối cùng trả về: " + result.replace(" ","").replace(".","").replace("\"","").replace(".","")
                                        .replace("\n","") + "." + "\nBạn có muốn upload lại không?");

                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        uploadData();
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.setCancelable(false);
                                builder.show();
                            }
                        }
                    });
                }
            }
        }
    }

    private void syncDataOffline(EnDataModel dataModel){
        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                for (Data d : listData) {

                    EnDataModel e = gson.fromJson(d.getInput(), EnDataModel.class);
                    if(e.getDaValue().getDataID().equals(dataModel.getDaValue().getDataID())
                            && e.getDaValue().getMaDuong().equals(dataModel.getDaValue().getMaDuong())
                            && e.getDaValue().getDanhGia().equals(dataModel.getDaValue().getDanhGia())
                            && e.getDaValue().getTuyenSo().equals(dataModel.getDaValue().getTuyenSo())
                            && e.getDaValue().getDataType().equals(dataModel.getDaValue().getDataType())) {
                        d.setIsUploaded(true);
                        DatabaseHelper.updateData(d);
                    }
                }
            }
        }
    }

    private void summaryData(List<EnDataModel> data){
        hashMap = new HashMap<>();
        listHeader=  new ArrayList<>();
        for(EnDataModel en : data){
            if(en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.accident_report).toLowerCase()) ||
                    en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.problem_report).toLowerCase()) ||
                    en.getDaValue().getAction().toLowerCase().equals(getResources().getString(R.string.lastday).toLowerCase())) {
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
