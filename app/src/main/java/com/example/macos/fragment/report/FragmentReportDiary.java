package com.example.macos.fragment.report;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.macos.adapter.RoadTestReportAdapter;
import com.example.macos.database.Data;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.CustomFragment;

import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class FragmentReportDiary extends CustomFragment {
    private View rootView;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ac_report, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);


        List<Data> list = DatabaseHelper.getData();
        Logger.error("list diary datasize: " + list.size());
        if(list != null){
            if(list.size() != 0){
                RoadTestReportAdapter adapter = new RoadTestReportAdapter(list, getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
//                recyclerView.setHasFixedSize(true);
            }
        }

        return rootView;
    }
}
