package com.example.macos.database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.Serializable;

/**
 * Entity mapped to table "ITEM".
 */
public class Item implements Serializable {

    private Long ItemID;
    private String ItemName;
    private String Description;

    public Item() {
    }

    @Override
    public String toString() {
        return "Item{" +
                "ItemID=" + ItemID +
                ", ItemName='" + ItemName + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }

    public Item(Long ItemID) {
        this.ItemID = ItemID;
    }

    public Item(Long ItemID, String ItemName, String Description) {
        this.ItemID = ItemID;
        this.ItemName = ItemName;
        this.Description = Description;
    }

    public Long getItemID() {
        return ItemID;
    }

    public void setItemID(Long ItemID) {
        this.ItemID = ItemID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

}
