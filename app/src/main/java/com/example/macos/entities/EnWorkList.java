package com.example.macos.entities;

import com.example.macos.database.Item;

import java.io.Serializable;
import java.util.List;

/**
 * Created by macos on 6/23/16.
 */
public class EnWorkList implements Serializable {
    private List<Item> dataList;

    public EnWorkList(List<Item> dataList) {
        this.dataList = dataList;
    }

    public List<Item> getDataList() {
        return dataList;
    }

    public void setDataList(List<Item> dataList) {
        this.dataList = dataList;
    }
}
