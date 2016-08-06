package com.example.macos.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnMainCatalogItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macos on 6/9/16.
 */
public class categoryAdapter extends BaseAdapter {

    private List<EnMainCatalogItem> data;
    private Context mContext;
    private DisplayMetrics dm;
    private List<ViewHolder> listHolder;
    private boolean IS_SELECT_MULTI = false;
    private Typeface custom_font;

    public categoryAdapter(Context ctx, List<EnMainCatalogItem> data){
        this.data = data;
        this.mContext = ctx;
        dm = mContext.getResources().getDisplayMetrics();
        listHolder = new ArrayList<>();
        custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/thinfont.ttf");
    }

    public void setMultiSelect(boolean multiSelect)
    {
        IS_SELECT_MULTI = multiSelect;
    }

    public class ViewHolder{
        boolean isCreated = false;
        private View convertView;
        private ImageView imgView;
        private TextView textView;
        private CheckBox checkBox;
    }

    public View getViewAt(int pos){
        return listHolder.get(pos).convertView;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public EnMainCatalogItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.category_layout, parent, false);
            holder = new ViewHolder();
            holder.imgView = (ImageView) convertView.findViewById(R.id.imgCategory);
            holder.textView = (TextView) convertView.findViewById(R.id.tvCategory);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.convertView = convertView;
            convertView.setTag(holder);
            listHolder.add(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(getItem(position).getItem().getItemName());
        //holder.textView.setTypeface(custom_font);
        holder.imgView.setImageResource(getItem(position).getImg());
        holder.checkBox.setChecked(getItem(position).isChecked());

        //System.out.println("position " + position + " check " + getItem(position).isChecked());
        if(!IS_SELECT_MULTI) {
            holder.checkBox.setVisibility(View.INVISIBLE);
            convertView.setAlpha((float)1.0);
        }
        else {
            holder.checkBox.setVisibility(View.VISIBLE);
            if(data.get(position).isChecked())
                convertView.setAlpha((float)1.0);
            else
                convertView.setAlpha((float)0.5);
        }

        int height = dm.widthPixels / 3 - 28;
        convertView.requestLayout();
        convertView.getLayoutParams().height = height;

        if(!holder.isCreated) {
            holder.isCreated = true;
            //AnimationControl.AppearIcon(convertView);
        }

        return convertView;
    }
}
