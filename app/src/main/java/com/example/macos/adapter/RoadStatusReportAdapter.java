package com.example.macos.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.fragment.FragmentViewFullReport;

import java.util.HashMap;
import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class RoadStatusReportAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private HashMap<String, List<EnDataModel>> listChild;
    List<String> listHeader;
    LayoutInflater inflater;
    FragmentManager manager;

    //HashMap<String, List<EnMainInputItem>>
    public RoadStatusReportAdapter(FragmentManager manager, List<String> listHeader, HashMap<String, List<EnDataModel>>data, Context mContext) {
        this.listChild = data;
        this.listHeader = listHeader;
        this.mContext = mContext;
        this.manager = manager;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getGroupCount() {
        return listHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listHeader.get(groupPosition)).size();

    }

    @Override
    public String getGroup(int groupPosition) {
        return listHeader.get(groupPosition);
    }

    @Override
    public EnDataModel getChild(int groupPosition, int childPosition) {
        return listChild.get(listHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.expandable_list_group, parent, false);

        TextView tvCatalog= (TextView) convertView.findViewById(R.id.tvCatalog);
        TextView tvRoadname= (TextView) convertView.findViewById(R.id.tvRoadName);
        TextView tvSummary= (TextView) convertView.findViewById(R.id.tvSummary);

        tvCatalog.setText(getGroup(groupPosition).equals("") ? "Chưa có dữ liệu!" : getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.expandable_list_item, parent, false);

        TextView tvPromptitem = (TextView) convertView.findViewById(R.id.tvPromptItem);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        TextView tvInformation = (TextView) convertView.findViewById(R.id.tvInformation);
        TextView tvRoadName = (TextView) convertView.findViewById(R.id.tvRoadName);


        try {
            tvPromptitem.setText(mContext.getResources().getString(R.string.dahhmuc) + ":\n" + (getChild(groupPosition, childPosition).getDaValue().getDataTypeName().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getDataTypeName()));
        }catch(Exception e){
            tvPromptitem.setText(mContext.getResources().getString(R.string.dahhmuc) + ":\n" + "Chưa có dữ liệu!");
        }

        try {
            tvStatus.setText(mContext.getResources().getString(R.string.Tinhtrang) + ":\n" + (getChild(groupPosition, childPosition).getDaValue().getThangDanhGia().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getThangDanhGia()));
        }catch(Exception e){
            tvStatus.setText(mContext.getResources().getString(R.string.Tinhtrang) + ":\n" + "Chưa có dữ liệu!");
        }

        try {
            tvInformation.setText(mContext.getResources().getString(R.string.motachitet) + ":\n" + (getChild(groupPosition, childPosition).getDaValue().getMoTaTinhTrang().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getMoTaTinhTrang()));
        }catch(Exception e){
            tvInformation.setText(mContext.getResources().getString(R.string.motachitet) + ":\n" + "Chưa có dữ liệu!");
        }

        try {
            tvRoadName.setText(mContext.getResources().getString(R.string.road_name) + ":\n" + (getChild(groupPosition, childPosition).getDaValue().getTenDuong().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getTenDuong()));
        }catch(Exception e){
            tvRoadName.setText(mContext.getResources().getString(R.string.road_name) + ":\n" + "Chưa có dữ liệu!");
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentViewFullReport reportInformation = new FragmentViewFullReport();
                reportInformation.setData(getChild(groupPosition, childPosition));
                reportInformation.show(manager, "test");
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
