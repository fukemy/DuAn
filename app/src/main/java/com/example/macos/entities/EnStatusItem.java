package com.example.macos.entities;

import java.util.List;

/**
 * Created by devil2010 on 7/7/16.
 */
public class EnStatusItem {
    private String promptItem;
    private String status;
    private String information;
    private List<String> imgUri;
    private String catalog;
    private String action;
    private String time;
    private String roadName;
    private EnLocationItem location;
    private String summary;
    private boolean isUploaded = false;

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public EnStatusItem(){};

    public void setData(EnMainInputItem mainInputItem, EnInputItem inputItem){
        setLocation(mainInputItem.getLocation());
        setSummary(mainInputItem.getSummary());
        setAction(mainInputItem.getAction());
        setCatalog(mainInputItem.getCatalog());
        setTime(mainInputItem.getTime());
        setRoadName(mainInputItem.getRoadName());
        setPromptItem(inputItem.getPromptItem());
        setStatus(inputItem.getStatus());
        setInformation(inputItem.getInformation());
        setImgUri(inputItem.getImgUri());
        setUploaded(inputItem.isUpload());
    }

    @Override
    public String toString() {
        return "EnStatusItem{" +
                "action='" + action + '\'' +
                ", promptItem='" + promptItem + '\'' +
                ", status='" + status + '\'' +
                ", information='" + information + '\'' +
                ", imgUri=" + imgUri +
                ", catalog='" + catalog + '\'' +
                ", time='" + time + '\'' +
                ", roadName='" + roadName + '\'' +
                //", location=" + location +
                ", summary='" + summary + '\'' +
                ", isUploaded='" + isUploaded + '\'' +
                '}';
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public List<String> getImgUri() {
        return imgUri;
    }

    public void setImgUri(List<String> imgUri) {
        this.imgUri = imgUri;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public EnLocationItem getLocation() {
        return location;
    }

    public void setLocation(EnLocationItem location) {
        this.location = location;
    }

    public String getPromptItem() {
        return promptItem;
    }

    public void setPromptItem(String promptItem) {
        this.promptItem = promptItem;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
