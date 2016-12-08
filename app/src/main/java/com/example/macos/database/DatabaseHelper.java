package com.example.macos.database;

import android.content.Context;

import com.example.macos.duan.R;
import com.example.macos.entities.EnDataModel;
import com.example.macos.main.Application;
import com.google.gson.Gson;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by macos on 6/27/16.
 */
public class DatabaseHelper {
    public static void insertData(String str){
        Data data = new Data();
        data.setInput(str);

        DataDao dao = Application.getInstance().daoSession.getDataDao();
        dao.insertOrReplaceInTx(data);
    }

    public static void insertBlueToothData(BlueToothData blData){
        BlueToothDataDao dao = Application.getInstance().daoSession.getBlueToothDataDao();
        dao.insertOrReplaceInTx(blData);
    }

    public static void insertPositionData(PositionData pxData){
        PositionDataDao dao = Application.getInstance().daoSession.getPositionDataDao();
        dao.insertOrReplaceInTx(pxData);
    }

    public static List<Data> getData(){
        DataDao dao = Application.getInstance().daoSession.getDataDao();
        return dao.queryBuilder().list();
    }

    public static void deleteDataTypeItem(EnDataModel dataModel){
        DataDao dao = Application.getInstance().daoSession.getDataDao();
        List<Data> listData = DatabaseHelper.getData();
        if(listData!= null) {
            if (listData.size() != 0) {
                Gson gson = new Gson();
                for (Data d : listData) {
                    EnDataModel en = gson.fromJson(d.getInput(), EnDataModel.class);
                    if(dataModel.getDaValue().getDataID().equalsIgnoreCase(en.getDaValue().getDataID())) {
                        dao.deleteInTx(d);
                        break;
                    }
                }

            }
        }
    }

    public static List<BlueToothData> getBlueToothDataByID(String dataUUID){
        BlueToothDataDao dao = Application.getInstance().daoSession.getBlueToothDataDao();

        QueryBuilder queryBuilder = dao.queryBuilder()
                .where(BlueToothDataDao.Properties.Id.eq(dataUUID));
        return queryBuilder.list();
    }

    public static List<PositionData> getPositionDataByID(String dataUUID){
        PositionDataDao dao = Application.getInstance().daoSession.getPositionDataDao();

        QueryBuilder queryBuilder = dao.queryBuilder()
                .where(PositionDataDao.Properties.Id.eq(dataUUID));
        return queryBuilder.list();
    }

    public static List<PositionData> getPositionDataByUser(String user){
        PositionDataDao dao = Application.getInstance().daoSession.getPositionDataDao();

        QueryBuilder queryBuilder = dao.queryBuilder()
                .where(PositionDataDao.Properties.UserName.eq(user));
        return queryBuilder.list();
    }


    public static void deletePositionForUser(String user){
        PositionDataDao dao = Application.getInstance().daoSession.getPositionDataDao();
        QueryBuilder queryBuilder = dao.queryBuilder()
                .where(PositionDataDao.Properties.UserName.eq(user));
        dao.deleteInTx(queryBuilder.list());
    }

    public static void updateData(Data d){
        DataDao dao = Application.getInstance().daoSession.getDataDao();
        dao.updateInTx(d);
    }

    public static void clearData(){
        DataDao dao = Application.getInstance().daoSession.getDataDao();
        dao.deleteAll();
    }

    public static void clearBlueToothData(){
        BlueToothDataDao dao = Application.getInstance().daoSession.getBlueToothDataDao();
        dao.deleteAll();
    }

    public static void clearPositionData(){
        PositionDataDao dao = Application.getInstance().daoSession.getPositionDataDao();
        dao.deleteAll();
    }


    public static void insertListRoadInformation(List<RoadInformation> informations){
        RoadInformationDao dao = Application.getInstance().daoSession.getRoadInformationDao();
        dao.insertOrReplaceInTx(informations);
    }

    public static List<RoadInformation> getRoadInformationList(){
        RoadInformationDao dao = Application.getInstance().daoSession.getRoadInformationDao();
        return dao.queryBuilder().list();
    }

    public static void insertListItem(List<Item> itemList){
        ItemDao dao = Application.getInstance().daoSession.getItemDao();
        dao.deleteAll();
        dao.insertOrReplaceInTx(itemList);
    }

    public static List<Item> getItemList(){
        ItemDao dao = Application.getInstance().daoSession.getItemDao();
        return dao.queryBuilder().list();
    }

}
