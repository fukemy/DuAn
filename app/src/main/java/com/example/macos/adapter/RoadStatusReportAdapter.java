package com.example.macos.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.libraries.AnimatedExpandableListview;
import com.example.macos.report.DiaryReportContent;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class RoadStatusReportAdapter extends AnimatedExpandableListview.AnimatedExpandableListAdapter {
    private Activity mContext;
    private HashMap<String, List<EnDataModel>> listChild;
    List<String> listHeader;
    LayoutInflater inflater;
    FragmentManager manager;

    public RoadStatusReportAdapter(FragmentManager manager, List<String> listHeader, HashMap<String, List<EnDataModel>>data, Activity mContext) {
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
    public int getRealChildrenCount(int groupPosition) {
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.expandable_list_group, parent, false);

        TextView tvCatalog= (TextView) convertView.findViewById(R.id.tvCatalog);

        tvCatalog.setText((getGroup(groupPosition) == null || getGroup(groupPosition).equals(""))
                ? "Chưa có dữ liệu!" : getGroup(groupPosition));
        return convertView;
    }


    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.expandable_list_item, parent, false);

        LinearLayout lnlHeader = (LinearLayout) convertView.findViewById(R.id.lnlHeader);
        TextView tvPromptitem = (TextView) convertView.findViewById(R.id.tvPromptItem);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        TextView tvInformation = (TextView) convertView.findViewById(R.id.tvInformation);
        TextView tvRoadName = (TextView) convertView.findViewById(R.id.tvRoadName);

        if(childPosition == 0)
            lnlHeader.setVisibility(View.VISIBLE);
        else
            lnlHeader.setVisibility(View.GONE);

        try {
            tvPromptitem.setText((getChild(groupPosition, childPosition).getDaValue().getDataTypeName().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getDataTypeName()));
        }catch(Exception e){
            tvPromptitem.setText("Chưa có dữ liệu!");
        }

        try {
            tvStatus.setText((getChild(groupPosition, childPosition).getDaValue().getThangDanhGia().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getThangDanhGia()));
        }catch(Exception e){
            tvStatus.setText("Chưa có dữ liệu!");
        }

        try {
            tvInformation.setText((getChild(groupPosition, childPosition).getDaValue().getMoTaTinhTrang().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getMoTaTinhTrang()));
        }catch(Exception e){
            tvInformation.setText("Chưa có dữ liệu!");
        }

        try {
            tvRoadName.setText((getChild(groupPosition, childPosition).getDaValue().getTenDuong().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getTenDuong()));
        }catch(Exception e){
            tvRoadName.setText("Chưa có dữ liệu!");
        }

        final LinearLayout animatedView = (LinearLayout) convertView.findViewById(R.id.lnlData);
        animatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent in = new Intent(mContext, DiaryReportContent.class);
                Gson gson = new Gson();
                in.putExtra("data",  gson.toJson(getChild(groupPosition, childPosition)));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ActivityOptionsCompat options =
                                    ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, animatedView,
                                            mContext.getResources().getString(R.string.show_map));
                            mContext.startActivity(in, options.toBundle());
                        }
                    }, 100);

                }else{
                    mContext.startActivity(in);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
