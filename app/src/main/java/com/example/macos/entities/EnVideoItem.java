package com.example.macos.entities;

import com.google.api.client.util.DateTime;

/**
 * Created by Microsoft on 12/5/16.
 */

public class EnVideoItem {
    private String title;
    private String description;
    private String thumbnailURL;
    private String id;
    private String kind;
    private String snippet;
    private DateTime publistAt;
    private String channelTitle;


    public DateTime getPublistAt() {
        return publistAt;
    }

    public void setPublistAt(DateTime publistAt) {
        this.publistAt = publistAt;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    @Override
    public String toString() {
        return "EnVideoItem{" +
                "title='" + title + '\'' +
                ", \ndescription='" + description + '\'' +
                ", \nthumbnailURL='" + thumbnailURL + '\'' +
                ", \nid='" + id + '\'' +
                ", \nkind='" + kind + '\'' +
                ", \nsnippet='" + snippet + '\'' +
                ", \npublistAt='" + publistAt + '\'' +
                ", channelTitle='" + channelTitle + '\'' +
                '}';
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }


    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }
}
