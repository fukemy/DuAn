package com.example.macos.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by devil2010 on 8/5/16.
 */
public class ImageModalUpload implements Serializable{
    @SerializedName("ImageName")
    private String ImageName;
    @SerializedName("ImageDataByte")
    private String ImageDataByte;

    public ImageModalUpload(String imageName, String imageDataByte) {
        ImageName = imageName;
        ImageDataByte = imageDataByte;
    }


    public String getImageName() {

        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageDataByte() {
        return ImageDataByte;
    }

    public void setImageDataByte(String imageDataByte) {
        ImageDataByte = imageDataByte;
    }
}
