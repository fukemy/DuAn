package com.example.macos.entities;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by macos on 6/18/16.
 */
public class EnLocationItem implements Serializable{
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String knownName;
    private Location location;

    @Override
    public String toString() {
        return "EnLocationItem{" +
                "address:'" + address + '\'' +
                ", city:'" + city + '\'' +
                ", state:'" + state + '\'' +
                ", country:'" + country + '\'' +
                ", postalCode:'" + postalCode + '\'' +
                ", knownName:'" + knownName + '\'' +
                ", location:" + location +
                '}';
    }

    public EnLocationItem(String address, String city, String country, String knownName, Location location, String postalCode, String state) {
        this.address = address;
        this.city = city;
        this.country = country;
        this.knownName = knownName;
        this.location = location;
        this.postalCode = postalCode;
        this.state = state;
    }

    public Location getLocation() {

        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public EnLocationItem() {
    }

    public EnLocationItem(String address, String city, String country, String knownName, String postalCode, String state) {
        this.address = address;
        this.city = city;
        this.country = country;
        this.knownName = knownName;
        this.postalCode = postalCode;
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getKnownName() {
        return knownName;
    }

    public void setKnownName(String knownName) {
        this.knownName = knownName;
    }
}
