package com.example.macos.database;

import com.example.macos.main.Application;

import java.util.List;

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

    public static List<Data> getData(){
        DataDao dao = Application.getInstance().daoSession.getDataDao();
        return dao.queryBuilder().list();
    }

    public static void clearData(){
        DataDao dao = Application.getInstance().daoSession.getDataDao();
        dao.deleteAll();
    }

    public static void insertListRoadInformation(List<RoadInformation> informations){
        RoadInformationDao dao = Application.getInstance().daoSession.getRoadInformationDao();
        dao.deleteAll();
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
