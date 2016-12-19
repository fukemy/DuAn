package com.example.macos.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.interfaces.iLongClickInterace;
import com.example.macos.interfaces.iRippleControl;
import com.example.macos.libraries.AnimatedExpandableListview;
import com.example.macos.libraries.Logger;
import com.example.macos.report.DiaryReportContent;
import com.example.macos.utilities.FunctionUtils;
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
    private iLongClickInterace longClickInterace;

    public RoadStatusReportAdapter(FragmentManager manager, List<String> listHeader, HashMap<String, List<EnDataModel>>data, Activity mContext) {
        this.listChild = data;
        this.listHeader = listHeader;
        this.mContext = mContext;
        this.manager = manager;
        inflater = LayoutInflater.from(mContext);
    }

    public void setInterface(iLongClickInterace longClickInterace){
        this.longClickInterace = longClickInterace;
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
        HeaderHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.header_report_status, parent, false);
            holder = new HeaderHolder();
            holder.tvHeaderName= (TextView) convertView.findViewById(R.id.tvHeaderName);
            holder.tvItemCount = (TextView) convertView.findViewById(R.id.tvItemCount);
            holder.imgTitleHeader= (ImageView) convertView.findViewById(R.id.imgTitleHeader);

            convertView.setTag(holder);
        }else{
            holder = (HeaderHolder) convertView.getTag();
        }

        holder.tvItemCount.setText("" + listChild.get(listHeader.get(groupPosition)).size());
        holder.tvHeaderName.setText((getGroup(groupPosition) == null || getGroup(groupPosition).equals(""))
                ? "Chưa có dữ liệu!" : getGroup(groupPosition));

        holder.imgTitleHeader.setImageResource(0);
        FunctionUtils.setImageForResource(mContext, holder.imgTitleHeader, getGroup(groupPosition));
        return convertView;
    }


    @Override
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MainHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.expandable_list_item, parent, false);
            holder = new MainHolder();
            holder.tvPromptitem = (TextView) convertView.findViewById(R.id.tvPromptItem);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
            holder.tvRoadName = (TextView) convertView.findViewById(R.id.tvRoadName);

            convertView.setTag(holder);
        }else{
            holder = (MainHolder) convertView.getTag();
        }


        try {
            holder.tvPromptitem.setText((getChild(groupPosition, childPosition).getDaValue().getDataTypeName().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getDataTypeName()));
        }catch(Exception e){
            holder.tvPromptitem.setText("Chưa có dữ liệu!");
        }

        try {
            holder.tvStatus.setText((getChild(groupPosition, childPosition).getDaValue().getDanhGia().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getDanhGia()));
        }catch(Exception e){
            holder.tvStatus.setText("Chưa có dữ liệu!");
        }

        try {
            holder.tvRoadName.setText((getChild(groupPosition, childPosition).getDaValue().getTenDuong().equals("")?
                    "Chưa có dữ liệu!"  : getChild(groupPosition, childPosition).getDaValue().getTenDuong()));
        }catch(Exception e){
            holder.tvRoadName.setText("Chưa có dữ liệu!");
        }

        final View animatedView = convertView;
        animatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent in = new Intent(mContext, DiaryReportContent.class);
                Gson gson = new Gson();
                in.putExtra("data",  gson.toJson(getChild(groupPosition, childPosition)));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animatedView.setEnabled(false);
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, animatedView,
                                    mContext.getResources().getString(R.string.show_map));
                    mContext.startActivity(in, options.toBundle());
                    animatedView.setEnabled(true);
                }else{
                    mContext.startActivity(in);
                }
            }
        });

        animatedView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                longClickInterace.onLongClick(getChild(groupPosition, childPosition), animatedView);
                return false;
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public class HeaderHolder{
        TextView tvHeaderName, tvItemCount;
        ImageView imgTitleHeader;
        public HeaderHolder(){

        }
    }

    public class MainHolder{
        TextView tvPromptitem,tvStatus ,tvRoadName;
        public MainHolder(){
        }
    }

}
