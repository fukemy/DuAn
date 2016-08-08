package com.example.macos.fragment.report;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.macos.adapter.RoadTestReportAdapter;
import com.example.macos.database.Data;
import com.example.macos.database.DatabaseHelper;
import com.example.macos.duan.R;
import com.example.macos.utilities.CustomFragment;

import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class FragmentReportDiary extends CustomFragment {
    private View rootView;
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ac_report, container, false);
        lv = (ListView) rootView.findViewById(R.id.lv);

        List<Data> list = DatabaseHelper.getData();
        if(list != null){
            if(list.size() != 0){
                RoadTestReportAdapter adapter = new RoadTestReportAdapter(getChildFragmentManager(), list, getActivity());
                lv.setAdapter(adapter);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lv.setNestedScrollingEnabled(true);
        }

        return rootView;
    }
}
