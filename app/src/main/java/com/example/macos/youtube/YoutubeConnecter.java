package com.example.macos.youtube;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.example.macos.duan.R;
import com.example.macos.entities.EnVideoItem;
import com.example.macos.interfaces.iYoutubeQuery;
import com.example.macos.libraries.Logger;
import com.example.macos.utilities.GlobalParams;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Microsoft on 12/5/16.
 */

public class YoutubeConnecter {

    private YouTube youtube;
    private YouTube.Search.List query;

    public YoutubeConnecter(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {

                Logger.error("initialize success");
                hr.setConnectTimeout(15000);
            }
        }).setApplicationName(context.getResources().getString(R.string.app_name)).build();

        try{
            query = youtube.search().list("snippet");
            query.setKey(GlobalParams.YOUTUBE_BROWNSER_KEY);
            query.setType("video");
            query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        }catch(IOException e){
            Logger.error("Could not initialize: "+e);
        }
    }

    public void searchVideoByChannel(String CHANNEL_ID, iYoutubeQuery delegate){
//        query.setQ(keywords);
        new GetYoutubeVideo(CHANNEL_ID, delegate).execute();
    }

    private class GetYoutubeVideo extends AsyncTask<Void, Void, List<EnVideoItem>>{
        List<EnVideoItem> items;
        String CHANNEL_ID;
        iYoutubeQuery delegate;

        public GetYoutubeVideo(String CHANNEL_ID, iYoutubeQuery delegate){
            this.CHANNEL_ID = CHANNEL_ID;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            items = new ArrayList<>();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        @Override
        protected List<EnVideoItem> doInBackground(Void... params) {
            query.setChannelId(CHANNEL_ID);
            try{

                SearchListResponse response = query.execute();
                List<SearchResult> results = response.getItems();

                for(SearchResult result:results){
                    EnVideoItem item = new EnVideoItem();
                    item.setTitle(result.getSnippet().getTitle());
                    item.setDescription(result.getSnippet().getDescription());
                    item.setThumbnailURL(changeImageSizeToHQ(result.getSnippet().getThumbnails().getDefault().getUrl()));
                    item.setId(result.getId().getVideoId());
                    item.setKind(result.getKind());
                    item.setPublistAt(result.getSnippet().getPublishedAt());
                    item.setChannelTitle(result.getSnippet().getChannelTitle());
                    item.setSnippet(result.getSnippet().toPrettyString());
                    items.add(item);

                    Logger.error("found video: " + item.toString());
                }
            }catch(Exception e){
                e.printStackTrace();
                Logger.error("Search youtube video error: "+ e.getMessage());
            }
            return items;
        }

        @Override
        protected void onPostExecute(List<EnVideoItem> enVideoItems) {
            delegate.onSuccess(enVideoItems);
        }
    }

    private String changeImageSizeToHQ(String url){
        return url.replace("default","hqdefault");
    }
}
