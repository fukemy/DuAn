package com.example.macos.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macos.duan.R;
import com.example.macos.entities.EnMainCatalogItem;
import com.example.macos.interfaces.iGridClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macos on 6/9/16.
 */
public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.ViewHolder> {

    private List<EnMainCatalogItem> data;
    private Context mContext;
    private DisplayMetrics dm;
    private Typeface custom_font;
    private iGridClick callback;

    public categoryAdapter(Context ctx, List<EnMainCatalogItem> data, iGridClick callback){
        this.data = data;
        this.mContext = ctx;
        dm = mContext.getResources().getDisplayMetrics();
        this.callback = callback;
        custom_font = Typeface.createFromAsset(mContext.getAssets(),  "fonts/thinfont.ttf");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imgView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.imgCategory);
            textView = (TextView) itemView.findViewById(R.id.tvCategory);

            int height = dm.widthPixels / 3 - 16;
            itemView.requestLayout();
            itemView.getLayoutParams().height = height;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            callback.onClickView(getPosition(), view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.category_layout, parent, false);
        ViewHolder holder = new ViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(getItem(position).getItem().getItemName());
//        holder.textView.setTypeface(custom_font);
        holder.imgView.setImageResource(getItem(position).getImg());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public EnMainCatalogItem getItem(int position) {
        return data.get(position);
    }
}
