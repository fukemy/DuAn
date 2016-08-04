package com.example.macos.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by macos on 6/17/16.
 */
public abstract class EnImageItem implements Parcelable
{
    private String image;

    public EnImageItem(String image){
        this.image = image;
    }

    protected EnImageItem(Parcel in){
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image.toString());
    }
}
