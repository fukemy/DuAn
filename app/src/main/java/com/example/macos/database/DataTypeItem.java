package com.example.macos.database;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import com.example.macos.entities.EnLocationItem;

/**
 * Entity mapped to table "DATA_TYPE_ITEM".
 */
public class DataTypeItem {

    private String DataID;
    private Integer DataType;
    private Integer MaDuong;
    private Integer TuyenSo;
    private String MoTaTinhTrang;
    private String KinhDo;
    private String ViDo;
    private String CaoDo;
    private String NguoiNhap;
    private String ThoiGianNhap;
    private String DanhGia;
    private String LyTrinh;
    private String dataUUID;

    public String getLyTrinh() {
        return LyTrinh;
    }

    public void setLyTrinh(String lyTrinh) {
        LyTrinh = lyTrinh;
    }

    @Override
    public String toString() {
        return "\n{" +
                "\n\"DataID\":\"" + DataID + "\"" +
                ", \n\"DataType\":" + DataType +
                ", \n\"MaDuong\":" + MaDuong +
                ", \n\"TuyenSo\":" + TuyenSo +
                ", \n\"MoTaTinhTrang\":\"" + MoTaTinhTrang + "\"" +
                ", \n\"KinhDo\":\"" + KinhDo + "\"" +
                ", \n\"ViDo\":\"" + ViDo + "\"" +
                ", \n\"CaoDo\":\"" + CaoDo + "\"" +
                ", \n\"LyTrinh\":\"" + LyTrinh + "\"" +
                ", \n\"NguoiNhap\":\"" + NguoiNhap + "\"" +
                ", \n\"ThoiGianNhap\":\"" + ThoiGianNhap + "\""+
                ", \n\"DanhGia\":\"" + DanhGia + "\""+
                "\n}";
    }


//    @Override
//    public String toString() {
//        return "DataTypeItem{" +
//                "\nDataID=" + DataID +
//                ",\n DataType=" + DataType +
//                ",\n MaDuong=" + MaDuong +
//                ",\n TuyenSo=" + TuyenSo +
//                ",\n MoTaTinhTrang='" + MoTaTinhTrang + '\'' +
//                ",\n KinhDo='" + KinhDo + '\'' +
//                ",\n ViDo='" + ViDo + '\'' +
//                ",\n CaoDo='" + CaoDo + '\'' +
//                ",\n NguoiNhap='" + NguoiNhap + '\'' +
//                ",\n ThoiGianNhap='" + ThoiGianNhap + '\'' +
//                ",\n DanhGia='" + DanhGia + '\'' +
//                ",\n DataTypeName='" + DataTypeName + '\'' +
//                ",\n Action='" + Action + '\'' +
//                ",\n TenDuong='" + TenDuong + '\'' +
//                ",\n DataName='" + DataName + '\'' +
//                ",\n locationItem=" + locationItem +
//                '}';
//    }

    //sub data
    private String DataTypeName;

    public int getDataTypeID() {
        return DataTypeID;
    }

    public void setDataTypeID(int dataTypeID) {
        DataTypeID = dataTypeID;
    }

    private int DataTypeID;
    private String Action;
    private String TenDuong;

    public String getTenDuong() {
        return TenDuong;
    }

    public void setTenDuong(String tenDuong) {
        TenDuong = tenDuong;
    }

    public String getDataTypeName() {
        return DataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        DataTypeName = dataTypeName;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getDataName() {
        return DataName;
    }

    public void setDataName(String dataName) {
        DataName = dataName;
    }

    public EnLocationItem getLocationItem() {
        return locationItem;
    }

    public void setLocationItem(EnLocationItem locationItem) {
        this.locationItem = locationItem;
    }

    private String DataName;
    private EnLocationItem locationItem;

    public DataTypeItem() {
    }

    public DataTypeItem(String DataID, Integer DataType, Integer MaDuong, Integer TuyenSo, String MoTaTinhTrang, String KinhDo,
                        String ViDo, String CaoDo, String NguoiNhap, String ThoiGianNhap, String DanhGia, String dataUUID) {
        this.DataID = DataID;
        this.DataType = DataType;
        this.MaDuong = MaDuong;
        this.TuyenSo = TuyenSo;
        this.MoTaTinhTrang = MoTaTinhTrang;
        this.KinhDo = KinhDo;
        this.ViDo = ViDo;
        this.CaoDo = CaoDo;
        this.NguoiNhap = NguoiNhap;
        this.ThoiGianNhap = ThoiGianNhap;
        this.DanhGia = DanhGia;
        this.dataUUID = dataUUID;
    }

    public String getDataID() {
        return DataID;
    }

    public void setDataID(String DataID) {
        this.DataID = DataID;
    }

    public Integer getDataType() {
        return DataType;
    }

    public void setDataType(Integer DataType) {
        this.DataType = DataType;
    }

    public Integer getMaDuong() {
        return MaDuong;
    }

    public void setMaDuong(Integer MaDuong) {
        this.MaDuong = MaDuong;
    }

    public Integer getTuyenSo() {
        return TuyenSo;
    }

    public void setTuyenSo(Integer TuyenSo) {
        this.TuyenSo = TuyenSo;
    }

    public String getMoTaTinhTrang() {
        return MoTaTinhTrang;
    }

    public void setMoTaTinhTrang(String MoTaTinhTrang) {
        this.MoTaTinhTrang = MoTaTinhTrang;
    }

    public String getKinhDo() {
        return KinhDo;
    }

    public void setKinhDo(String KinhDo) {
        this.KinhDo = KinhDo;
    }

    public String getViDo() {
        return ViDo;
    }

    public void setViDo(String ViDo) {
        this.ViDo = ViDo;
    }

    public String getCaoDo() {
        return CaoDo;
    }

    public void setCaoDo(String CaoDo) {
        this.CaoDo = CaoDo;
    }

    public String getNguoiNhap() {
        return NguoiNhap;
    }

    public void setNguoiNhap(String NguoiNhap) {
        this.NguoiNhap = NguoiNhap;
    }

    public String getThoiGianNhap() {
        return ThoiGianNhap;
    }

    public void setThoiGianNhap(String ThoiGianNhap) {
        this.ThoiGianNhap = ThoiGianNhap;
    }

    public String getDanhGia() {
        return DanhGia;
    }

    public void setDanhGia(String ThangDanhGia) {
        this.DanhGia = ThangDanhGia;
    }


    public String getDataUUID() {
        return dataUUID;
    }

    public void setDataUUID(String dataUUID) {
        this.dataUUID = dataUUID;
    }

}