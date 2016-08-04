package com.example.macos.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.macos.database.Data;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.fragment.FragmentViewFullReportDiary;
import com.example.macos.utilities.FunctionUtils;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class RoadTestReportAdapter extends BaseAdapter {

    private List<Data> listData;
    private Context mContext;
    private Gson gson;
    LayoutInflater inflater;
    FragmentManager manager;

    public RoadTestReportAdapter(FragmentManager manager,List<Data> listData, Context mContext){
        this.manager = manager;
        this.listData = listData;
        this.mContext = mContext;
        gson = new Gson();
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Data getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.road_test_report_adapter, parent, false);
        TextView tvAction = (TextView)convertView.findViewById(R.id.tvAction);
        TextView tvCategory = (TextView)convertView.findViewById(R.id.tvCategory);
        TextView tvTime = (TextView)convertView.findViewById(R.id.tvTime);
        TextView tvRoadName = (TextView)convertView.findViewById(R.id.tvRoadName);

        final EnDataModel en = gson.fromJson(getItem(position).getInput(), EnDataModel.class);
        tvAction.setText(en.getDaValue().getAction());
        tvRoadName.setText(en.getDaValue().getTenDuong());
        tvCategory.setText(en.getDaValue().getDataName());
        tvTime.setText(FunctionUtils.timeStampToTime(Long.parseLong(en.getDaValue().getThoiGianNhap())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentViewFullReportDiary reportInformation = new FragmentViewFullReportDiary();
                reportInformation.setData(en);
                reportInformation.show(manager, "test");

            }
        });
        return convertView;
    }
}
