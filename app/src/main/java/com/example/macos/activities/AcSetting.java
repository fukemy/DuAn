package com.example.macos.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;

/**
 * Created by devil2010 on 8/18/16.
 */
public class AcSetting extends AppCompatActivity implements OnCheckedChangeListener{
    private Spinner spnLimitLoginTime;
    private Switch swLimitLoginTime,swAcceptAlertUpload;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_setting);
        initLayout();
    }

    private void initLayout(){
        spnLimitLoginTime  = (Spinner) findViewById(R.id.spnLimitLoginTime);
        spnLimitLoginTime.setEnabled(false);
        swLimitLoginTime  = (Switch) findViewById(R.id.swLimitLoginTime);
        swLimitLoginTime.setOnCheckedChangeListener(this);

        swAcceptAlertUpload  = (Switch) findViewById(R.id.swAcceptAlertUpload);
        swAcceptAlertUpload.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.swLimitLoginTime:
                Logger.error("swLimitLoginTime check: " + isChecked);
                spnLimitLoginTime.setEnabled(isChecked);
                findViewById(R.id.tvAmountLoginLimit).setEnabled(isChecked);
                break;
            case R.id.swAcceptAlertUpload:
                Logger.error("swAcceptAlertUpload check: " + isChecked);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
