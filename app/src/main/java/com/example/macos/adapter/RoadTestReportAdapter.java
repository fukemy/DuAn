package com.example.macos.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.macos.database.Data;
import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.libraries.Logger;
import com.example.macos.report.DiaryReportContent;
import com.example.macos.utilities.FunctionUtils;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by macos on 6/27/16.
 */
public class RoadTestReportAdapter extends RecyclerView.Adapter<RoadTestReportAdapter.ViewHolder> {

    private List<Data> listData;
    private Activity mContext;
    private Gson gson;
    LayoutInflater inflater;

    public RoadTestReportAdapter(List<Data> listData, Activity mContext){
        this.listData = listData;
        this.mContext = mContext;
        gson = new Gson();
        inflater = LayoutInflater.from(mContext);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvAction, tvCategory, tvTime, tvRoadName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAction = (TextView)itemView.findViewById(R.id.tvAction);
            tvCategory = (TextView)itemView.findViewById(R.id.tvCategory);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvRoadName = (TextView)itemView.findViewById(R.id.tvRoadName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Intent in = new Intent(mContext, DiaryReportContent.class);

            EnDataModel en = gson.fromJson(getItem(getPosition()).getInput(), EnDataModel.class);
            in.putExtra("data",  gson.toJson(en));
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, view,
                                mContext.getResources().getString(R.string.show_map));
                mContext.startActivity(in, options.toBundle());
            }else{
                mContext.startActivity(in);
            }
        }
    }

    public Data getItem(int position) {
        return listData.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.road_test_report_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EnDataModel en = gson.fromJson(getItem(position).getInput(), EnDataModel.class);
        Logger.error("en: " + en.toString());
        holder.tvAction.setText(en.getDaValue().getAction());
        holder.tvRoadName.setText(en.getDaValue().getTenDuong());
        holder.tvCategory.setText(en.getDaValue().getDataName());
        holder.tvTime.setText(FunctionUtils.timeStampToTime(Long.parseLong(en.getDaValue().getThoiGianNhap())));
    }

    @Override
    public int getItemCount() {
        Logger.error("list diary data: " + listData.toString());
        return listData.size();
    }

}
