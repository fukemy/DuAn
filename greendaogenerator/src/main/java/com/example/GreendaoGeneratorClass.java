package com.example;

import javax.swing.JOptionPane;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreendaoGeneratorClass {
    public static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args){
        createDatabase();
    }

    public static void createDatabase(){
        Schema schema = new Schema(1, "com.example.macos.database");
        addUserLocationDb(schema);
        addInputDb(schema);
        try{
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "//app//src//main//java");
            System.out.println(PROJECT_DIR + "//app//src//main//java");
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void addDb(Schema schema)
    {
        Entity en = schema.addEntity("MaDuong");
        en.addLongProperty("ID").primaryKey().autoincrement();
        en.addStringProperty("TenDuong");
    }

    public static void addInputDb(Schema schema)
    {
        Entity en = schema.addEntity("Data");
        en.addLongProperty("ID").primaryKey().autoincrement();
        en.addStringProperty("input");
    }

    public static void addUserLocationDb(Schema schema)
    {
        Entity en = schema.addEntity("UserLocation");
        en.addLongProperty("ID").primaryKey().autoincrement();
        en.addStringProperty("userLocation");
    }

//    public static void addUserLocationDb(Schema schema)
//    {
//        Entity en = schema.addEntity("UserLocation");
//        en.addLongProperty("ID").primaryKey().autoincrement();
//        en.addStringProperty("userLocation");
//    }


}
