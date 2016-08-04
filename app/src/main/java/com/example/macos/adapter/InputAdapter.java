package com.example.macos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnInputItem;

import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class InputAdapter extends BaseAdapter {
    private Context mContext;
    private List<EnInputItem> data;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public InputAdapter(List<EnInputItem> data, Context mContext) {
        this.data = data;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    private class ViewHolder{
        private  TextView tvPromptitem;
        private TextView tvStatus;
        private TextView tvInformation;
    }
    @Override
    public EnInputItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.expandable_list_item, parent, false);
            holder = new ViewHolder();
        }


        TextView tvPromptitem = (TextView) convertView.findViewById(R.id.tvPromptItem);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        TextView tvInformation = (TextView) convertView.findViewById(R.id.tvInformation);

        tvPromptitem.setText(getItem(position).getPromptItem().equals("") ? "Chưa có dữ liệu!" : getItem(position).getPromptItem());
        tvStatus.setText(getItem(position).getStatus().equals("") ? "Chưa có dữ liệu!" : getItem(position).getStatus());
        tvInformation.setText(getItem(position).getInformation().equals("") ? "Chưa có dữ liệu!" : getItem(position).getInformation());

        return convertView;
    }
}
