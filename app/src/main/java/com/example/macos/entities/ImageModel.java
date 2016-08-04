package com.example.macos.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by devil2010 on 7/22/16.
 */
public class ImageModel {
    @SerializedName("ImageName")
    private String ImageName;
    @SerializedName("ImageDataByte")
    private String ImageDataByte;
    private String ImagePath;

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getImageDataByte() {
        return ImageDataByte;
    }

    public void setImageDataByte(String imageDataByte) {
        ImageDataByte = imageDataByte;
    }


    public ImageModel() {
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    @Override
    public String toString() {
        return "\n{" +
                " \n\"ImageName\":\"" + ImageName + "\"," +
                "\n\"ImageDataByte\":\"" + ImageDataByte.trim() + "\"" +
                "\n}\n";
    }

    public ImageModel(String ImageDataByte, String imageName) {
        this.ImageDataByte = ImageDataByte;
        ImageName = imageName;
    }
}
