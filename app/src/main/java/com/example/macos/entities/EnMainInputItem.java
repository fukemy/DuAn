package com.example.macos.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by macos on 6/24/16.
 */
public class EnMainInputItem implements Serializable{


    private String catalog;
    private String action;
    private String time;

    public EnMainInputItem(String action, String catalog, List<EnInputItem> input, EnLocationItem location, String roadName, String summary, String time) {
        this.action = action;
        this.catalog = catalog;
        this.input = input;
        this.location = location;
        this.roadName = roadName;
        this.summary = summary;
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String roadName;
    private EnLocationItem location;

    public EnLocationItem getLocation() {

        return location;
    }

    @Override
    public String toString() {
        return "EnMainInputItem{" +
                "action:'" + action + '\'' +
                ", catalog:'" + catalog + '\'' +
                ", time:'" + time + '\'' +
                ", roadName:'" + roadName + '\'' +
                //", location:'" + location +
                ", input:'" + input +
                ", summary:'" + summary + '\'' +
                '}';
    }

    public void setLocation(EnLocationItem location) {
        this.location = location;
    }

    public List<EnInputItem> getInput() {
        return input;
    }

    public void setInput(List<EnInputItem> input) {
        this.input = input;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    private List<EnInputItem> input;
    private String summary;

    public EnMainInputItem() {
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}
