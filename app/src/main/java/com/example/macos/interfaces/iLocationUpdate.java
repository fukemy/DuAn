package com.example.macos.interfaces;

import android.location.Location;

import com.example.macos.entities.EnLocationItem;

/**
 * Created by macos on 6/29/16.
 */
public interface iLocationUpdate {
    public void updateLocation(EnLocationItem lo);
    public void onFailGetLocation();
}
