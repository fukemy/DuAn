package com.example.macos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.macos.duan.R;

/**
 * Created by devil2010 on 8/8/16.
 */
public class ChooseRoadNameAdapter extends ArrayAdapter {
    private Context mContext;
    private String[] data;


    public ChooseRoadNameAdapter(Context mContext, int textViewResourceId, String[] data) {
        super(mContext, textViewResourceId, data);
        this.mContext = mContext;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public String getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.choose_roadname_layout, parent, false);

        TextView tv = (TextView) convertView.findViewById(R.id.tvRoadName);
        tv.setText(getItem(position));

        return convertView;
    }
}
