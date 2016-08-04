package com.example.macos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.entities.EnMainInputItem;
import com.example.macos.utilities.GlobalParams;
import com.google.gson.Gson;

public class AcSummary extends AppCompatActivity {

    EnMainInputItem inputItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_summary);

        Gson gson = new Gson();
        inputItem = (EnMainInputItem) getIntent().getExtras().getSerializable(GlobalParams.DATA_SUMMARY);

        String temp = gson.toJson(inputItem);
        System.out.println("" + temp );
        ((TextView) findViewById(R.id.tvSummary)).setText(temp);
        DatabaseHelper.insertData(temp);
    }

}
