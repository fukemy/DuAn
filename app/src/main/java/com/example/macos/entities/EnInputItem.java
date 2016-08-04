package com.example.macos.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by macos on 6/17/16.
 */
public class EnInputItem implements Serializable {
    private String promptItem;
    private String status;
    private String information;
    private List<String> imgUri;
    private boolean isUpload = false;

    @Override
    public String toString() {
        return "EnInputItem{" +
                "imgUri:" + imgUri +
                ", promptItem:'" + promptItem + '\'' +
                ", status:'" + status + '\'' +
                ", information:'" + information + '\'' +
                '}';
    }

    public EnInputItem() {
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public EnInputItem(List<String> imgUri) {
        this.imgUri = imgUri;
    }

    public String getInformation() {

        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public List<String> getImgUri() {
        return imgUri;
    }

    public void setImgUri(List<String> imgUri) {
        this.imgUri = imgUri;
    }

    public String getPromptItem() {
        return promptItem;
    }

    public void setPromptItem(String promptItem) {
        this.promptItem = promptItem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
