package com.example.macos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.macos.duan.R;
import com.example.macos.entities.EnVideoItem;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Microsoft on 11/28/16.
 */

public class VideoListAdapter extends BaseAdapter {

    List<EnVideoItem> videoList;
    private Context mContext;

    public VideoListAdapter(List<EnVideoItem> videoList, Context mContext){
        this.videoList = videoList;
        this.mContext = mContext;
    }

    private static class ViewHolder {
        private TextView videoName;
        private YouTubeThumbnailView thumb;
        private ProgressBar prLoadImgThumb;
        private ImageView iconYoutube;

        public ViewHolder(View v) {
            videoName = (TextView) v.findViewById(R.id.videoName);
            thumb = (YouTubeThumbnailView) v.findViewById(R.id.videoThumb);
            prLoadImgThumb = (ProgressBar) v.findViewById(R.id.prLoadImgThumb);
            iconYoutube = (ImageView) v.findViewById(R.id.iconYoutube);
        }
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.video_thumb_listview, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.videoName.setText(getItem(position).getTitle());
        final ProgressBar dialog = holder.prLoadImgThumb;
        final ImageView youtube = holder.iconYoutube;
        Picasso.with(mContext).load(getItem(position).getThumbnailURL())
                .into(holder.thumb, new Callback() {
                    @Override
                    public void onSuccess() {
                        dialog.setVisibility(View.GONE);
                        youtube.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        dialog.setVisibility(View.GONE);
                        youtube.setVisibility(View.VISIBLE);
                    }
                });

//        holder.thumb.initialize(GlobalParams.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
//                youTubeThumbnailLoader.setVideo(getItem(position).getId());
//                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
//                    @Override
//                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
//                        try {
//                            youTubeThumbnailLoader.release();
//                        }catch (Exception e){}
//                        dialog.setVisibility(View.GONE);
//                        youtube.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
//                        dialog.setVisibility(View.GONE);
//                        youtube.setVisibility(View.VISIBLE);
//                    }
//                });
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//                dialog.setVisibility(View.GONE);
//                youtube.setVisibility(View.VISIBLE);
//            }
//        });
        return convertView;
    }

    @Override
    public EnVideoItem getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
