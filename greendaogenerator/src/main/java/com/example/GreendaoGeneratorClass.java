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
        Schema schema = new Schema(1, "com.example.macos.database.new");
        addPositionDb(schema);
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


    public static void addPositionDb(Schema schema)
    {
        Entity en = schema.addEntity("PositionData");
        en.addStringProperty("Id");
        en.addStringProperty("UserName");
        en.addStringProperty("LogTime");
        en.addStringProperty("Lattitude");
        en.addStringProperty("Longitude");
    }

    public static void addBlueToothData(Schema schema){
        Entity en = schema.addEntity("BlueToothData");
        en.addStringProperty("Id");
        en.addIntProperty("RoadId");
        en.addStringProperty("DateTimeLoging");
        en.addStringProperty("Latitude");
        en.addStringProperty("Longitude");
        en.addDoubleProperty("ZaxisValue");
        en.addStringProperty("UserLoging");
    }

    public static void addInputDb(Schema schema)
    {
        Entity en = schema.addEntity("Data");
        en.addLongProperty("ID").primaryKey().autoincrement();
        en.addStringProperty("input");
        en.addBooleanProperty("isUploaded");
    }

    public static void addItemDb(Schema schema)
    {
        Entity en = schema.addEntity("Item");
        en.addLongProperty("ItemID").primaryKey();
        en.addStringProperty("ItemName");
        en.addStringProperty("Description");
    }


    public static void addDataTypeItemDb(Schema schema)
    {
        Entity en = schema.addEntity("DataTypeItem");
        en.addStringProperty("DataID");
        en.addIntProperty("DataType");
        en.addIntProperty("MaDuong");
        en.addIntProperty("TuyenSo");
        en.addStringProperty("MoTaTinhTrang");
        en.addStringProperty("KinhDo");
        en.addStringProperty("ViDo");
        en.addStringProperty("CaoDo");
        en.addStringProperty("NguoiNhap");
        en.addStringProperty("ThoiGianNhap");
        en.addStringProperty("DanhGia");
        en.addStringProperty("LyTrinh");
    }

    public static void addRoadInformationDb(Schema schema)
    {
        Entity en = schema.addEntity("RoadInformation");
        en.addLongProperty("ID").primaryKey().autoincrement();
        en.addStringProperty("MaDuong");
        en.addStringProperty("TenDuong");
        en.addStringProperty("TuyenSo");
        en.addStringProperty("TuyenNhanhSo");
        en.addStringProperty("CapDuong");
        en.addIntProperty("MaDonViQuanLy");
        en.addIntProperty("TuCotKm");
        en.addIntProperty("DenCotKm");
        en.addStringProperty("ViDo");
        en.addStringProperty("KinhDo");
        en.addIntProperty("ChieuDai");
        en.addStringProperty("NgayDieuChinhCotKm");
        en.addStringProperty("ThoiDiemCapNhap");
        en.addIntProperty("MaTinh");
        en.addIntProperty("MaThanhPho");
        en.addStringProperty("LoadLanDuong");
        en.addIntProperty("NamXayDung");
        en.addDoubleProperty("HanhLangDuongBo");
        en.addStringProperty("LoaiKetCauDuong");
        en.addIntProperty("SoLanXeCoGioi");
        en.addDoubleProperty("ChieuRongLanXeCoGioi");
        en.addStringProperty("LoaiMatDuongLanXeCoGioi");
        en.addIntProperty("SoLanxeThoSo");
        en.addStringProperty("LoaiMatDuongLanXeThoSo");
        en.addDoubleProperty("ChieuRongLanXeThoSo");
        en.addDoubleProperty("ChieuRongDuongXeChay");
        en.addDoubleProperty("ChieuRongMatDuong");
        en.addStringProperty("LeDuong");
        en.addDoubleProperty("ChieuRongLeDuong");
        en.addStringProperty("ViaHe");
        en.addDoubleProperty("ChieuRongViaHe");
        en.addStringProperty("LoaiViaHe");
        en.addDoubleProperty("ChieuRongThoatNuoc");
        en.addDoubleProperty("TocDoThietKe");
        en.addStringProperty("LoaiDiaHinh");
        en.addStringProperty("LoaiNenDuong");
    }

//    public static void addUserLocationDb(Schema schema)
//    {
//        Entity en = schema.addEntity("UserLocation");
//        en.addLongProperty("ID").primaryKey().autoincrement();
//        en.addStringProperty("userLocation");
//    }


}
