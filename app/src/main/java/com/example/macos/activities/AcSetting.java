package com.example.macos.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.macos.database.Data;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.main.Application;
import com.example.macos.main.SprashScreen;
import com.example.macos.utilities.FunctionUtils;
import com.example.macos.utilities.GlobalParams;
import com.example.macos.utilities.SharedPreferenceManager;

import java.util.List;

/**
 * Created by devil2010 on 8/18/16.
 */
public class AcSetting extends AppCompatActivity implements OnCheckedChangeListener, View.OnClickListener{
    private SharedPreferenceManager pref;
    private Spinner spnLimitLoginTime;
    private Switch swLimitLoginTime,swAcceptAlertUpload;
    private FrameLayout lnlLogout;
    private Button btnBack,btnDeleteData;
    int MAX_LOGIN_TIME;
    boolean IS_ACCEPT_NOTIFICATION;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.ac_setting);

        pref = new SharedPreferenceManager(this);
        initLayout();
        initData();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            getWindow().setEnterTransition(new Slide(Gravity.BOTTOM).setDuration(500));
            getWindow().setEnterTransition(new Fade().setDuration(400));
            getWindow().setExitTransition(new Explode().setDuration(400));
        }
    }

    private void initLayout(){
        spnLimitLoginTime  = (Spinner) findViewById(R.id.spnLimitLoginTime);
        swLimitLoginTime  = (Switch) findViewById(R.id.swLimitLoginTime);
        swLimitLoginTime.setOnCheckedChangeListener(this);

        swAcceptAlertUpload  = (Switch) findViewById(R.id.swAcceptAlertUpload);
        swAcceptAlertUpload.setOnCheckedChangeListener(this);

        lnlLogout  = (FrameLayout) findViewById(R.id.lnlLogout);
        lnlLogout.setOnClickListener(this);

        btnBack  = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        btnDeleteData  = (Button) findViewById(R.id.btnDeleteData);
        btnDeleteData.setOnClickListener(this);
    }

    private void initData(){
        spnLimitLoginTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int time = Integer.parseInt(spnLimitLoginTime.getSelectedItem().toString().replace(" ", "").replace("ngày", ""));
                pref.saveInt(GlobalParams.MAX_LOGIN_TIME, time);
                Logger.error("spnLimitLoginTime select: " + time);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        MAX_LOGIN_TIME = pref.getInt(GlobalParams.MAX_LOGIN_TIME, 9999);
        if(MAX_LOGIN_TIME == 9999){
            spnLimitLoginTime.setEnabled(false);
            swLimitLoginTime.setChecked(false);
        }else{
            spnLimitLoginTime.setEnabled(true);
            swLimitLoginTime.setChecked(true);
            setSpinText(spnLimitLoginTime, MAX_LOGIN_TIME);
        }

        IS_ACCEPT_NOTIFICATION = pref.getBoolean(GlobalParams.IS_ACCEPT_NOTIFICATION, true);
        swAcceptAlertUpload.setChecked(IS_ACCEPT_NOTIFICATION);
        if(IS_ACCEPT_NOTIFICATION){
            FunctionUtils.setAlarm(this);
        }else{
            FunctionUtils.calcelAlarm(this);
        }
    }

    public void setSpinText(Spinner spin, int text)
    {
        for(int i= 0; i < spin.getAdapter().getCount(); i++)
        {
            if(spin.getAdapter().getItem(i).toString().contains("" + text))
            {
                spin.setSelection(i);
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.swLimitLoginTime:
                Logger.error("swLimitLoginTime check: " + isChecked);
                spnLimitLoginTime.setEnabled(isChecked);
                findViewById(R.id.tvAmountLoginLimit).setEnabled(isChecked);
                if(!isChecked){
                    pref.saveInt(GlobalParams.MAX_LOGIN_TIME, 9999);
                }else{
                    pref.saveInt(GlobalParams.MAX_LOGIN_TIME, 3); //default
                }
                break;
            case R.id.swAcceptAlertUpload:
                Logger.error("swAcceptAlertUpload check: " + isChecked);
                pref.saveBoolean(GlobalParams.IS_ACCEPT_NOTIFICATION, isChecked);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder;
        switch (view.getId()){
            case R.id.lnlLogout:
                builder = new AlertDialog.Builder(AcSetting.this);
                builder.setTitle("Warning");
                builder.setMessage(getResources().getString(R.string.bancochacchanmuondangxuat));

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pref.saveBoolean(GlobalParams.IS_LOGGED_ON, false);
                        pref.saveString(GlobalParams.USERNAME, "");
                        pref.saveLong(GlobalParams.LAST_LOGIN, 0);

                        startActivity(new Intent(AcSetting.this, SprashScreen.class));
                        finish();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;

            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnDeleteData:
//                List<Data> dataList = DatabaseHelper.getData();
//                if(dataList.size() > 0) {
                    builder = new AlertDialog.Builder(AcSetting.this);
                    builder.setTitle("Warning");
                    builder.setMessage("Bạn có chắc chắn muốn xoá mọi dữ liệu đã nhập?");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseHelper.clearData();
                            DatabaseHelper.clearBlueToothData();
                            DatabaseHelper.clearPositionData();
                            Intent in = new Intent(AcSetting.this, MainScreen.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            in.putExtra("isDeleteData", true);
                            startActivity(in);
                            finish();
                        }
                    });

                    builder.setNegativeButton("Cancel", null);
                    builder.show();
//                }else{
//                    Toast.makeText(AcSetting.this, "Chưa có dữ liệu để xoá!", Toast.LENGTH_SHORT).show();
//                }
                break;
        }
    }


}
