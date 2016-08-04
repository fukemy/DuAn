package com.example.macos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.macos.duan.R;

import java.util.List;

/**
 * Created by macos on 6/18/16.
 */
public class ImageAdapter extends BaseAdapter {

    List<Bitmap> data;
    Context mContext;
    LayoutInflater inflater;

    public ImageAdapter(List<Bitmap> data, Context mContext){
        this.data = data;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.image_data, parent, false);
        }

        ImageView img = (ImageView) convertView.findViewById(R.id.img);
        img.setImageBitmap(getItem(position));
        return convertView;
    }
}
