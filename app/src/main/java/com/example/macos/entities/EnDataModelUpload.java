package com.example.macos.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by devil2010 on 8/5/16.
 */
public class EnDataModelUpload implements Serializable{
    private EnStatusInformationData DataValue;
    private List<ImageModalUpload> ListImageData;

    public List<ImageModalUpload> getListImageData() {
        return ListImageData;
    }

    public void setListImageData(List<ImageModalUpload> listImageData) {
        ListImageData = listImageData;
    }

    public EnStatusInformationData getDataValue() {

        return DataValue;
    }

    public void setDataValue(EnStatusInformationData dataValue) {
        DataValue = dataValue;
    }

    public EnDataModelUpload(EnDataModel dataModel){
        DataValue = new EnStatusInformationData(dataModel.getDaValue());
        ListImageData = new ArrayList<>();
        for(ImageModel i : dataModel.getListImageData()){
            ListImageData.add(new ImageModalUpload(i.getImageName(), i.getImageDataByte()));
        }
    }
}
