package com.example.macos.entities;

import com.example.macos.database.Data;
import com.example.macos.database.DataTypeItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by devil2010 on 7/22/16.
 */
public class EnDataModel implements Serializable{
    private DataTypeItem DataValue;
    private List<ImageModel> ListImageData;
    private boolean isUploaded = false;

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    @Override
    public String toString() {
//        return "\n{" +
//                "\"DataValue\":" + DataValue +
//                "\"ListImageData\":" + ListImageData +
//                "\n}";
        return DataValue.toString();
    }

    public DataTypeItem getDaValue() {
        return DataValue;
    }

    public void setDaValue(DataTypeItem daValue) {
        DataValue = daValue;
    }

    public List<ImageModel> getListImageData() {
        return ListImageData;
    }

    public void setListImageData(List<ImageModel> listImageData) {
        ListImageData = listImageData;
    }

    public EnDataModel() {

    }

    public EnDataModel(DataTypeItem daValue, List<ImageModel> listImageData) {
        DataValue = daValue;
        ListImageData = listImageData;
    }
}
