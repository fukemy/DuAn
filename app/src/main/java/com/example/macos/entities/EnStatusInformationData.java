package com.example.macos.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by devil2010 on 7/22/16.
 */
public class EnStatusInformationData {
    @SerializedName("DataID")
    private int DataID;
    @SerializedName("DataType")
    private int DataType;
    @SerializedName("MaDuong")
    private int MaDuong;
    @SerializedName("TuyenSo")
    private Integer TuyenSo;
    @SerializedName("MoTaTinhTrang")
    private String MoTaTinhTrang;
    @SerializedName("KinhDo")
    private String KinhDo;
    @SerializedName("ViDo")
    private String ViDo;
    @SerializedName("CaoDo")
    private String CaoDo;
    @SerializedName("NguoiNhap")
    private String NguoiNhap;
    @SerializedName("ThoiGianNhap")
    private String ThoiGianNhap;
    @SerializedName("ThangDanhGia")
    private Integer ThangDanhGia;

    @Override
    public String toString() {
        return "\n{" +
                "\n\"DataID\":" + DataID +
                ", \n\"DataType\":" + DataType +
                ", \n\"MaDuong\":" + MaDuong +
                ", \n\"TuyenSo\":" + TuyenSo +
                ", \n\"MoTaTinhTrang\":\"" + MoTaTinhTrang + "\"" +
                ", \n\"KinhDo\":\"" + KinhDo + "\"" +
                ", \n\"ViDo\":\"" + ViDo + "\"" +
                ", \n\"CaoDo\":\"" + CaoDo + "\"" +
                ", \n\"NguoiNhap\":\"" + NguoiNhap + "\"" +
                ", \n\"ThoiGianNhap\":\"" + ThoiGianNhap + "\""+
                ", \n\"ThangDanhGia\":" + ThangDanhGia +
                "\n}";
    }

    public int getDataID() {
        return DataID;
    }

    public void setDataID(int dataID) {
        DataID = dataID;
    }

    public int getDataType() {
        return DataType;
    }

    public void setDataType(int dataType) {
        DataType = dataType;
    }

    public int getMaDuong() {
        return MaDuong;
    }

    public void setMaDuong(int maDuong) {
        MaDuong = maDuong;
    }

    public Integer getTuyenSo() {
        return TuyenSo;
    }

    public void setTuyenSo(Integer tuyenSo) {
        TuyenSo = tuyenSo;
    }

    public String getMoTaTinhTrang() {
        return MoTaTinhTrang;
    }

    public void setMoTaTinhTrang(String moTaTinhTrang) {
        MoTaTinhTrang = moTaTinhTrang;
    }

    public String getKinhDo() {
        return KinhDo;
    }

    public void setKinhDo(String kinhDo) {
        KinhDo = kinhDo;
    }

    public String getViDo() {
        return ViDo;
    }

    public void setViDo(String viDo) {
        ViDo = viDo;
    }

    public String getCaoDo() {
        return CaoDo;
    }

    public void setCaoDo(String caoDo) {
        CaoDo = caoDo;
    }

    public String getNguoiNhap() {
        return NguoiNhap;
    }

    public void setNguoiNhap(String nguoiNhap) {
        NguoiNhap = nguoiNhap;
    }

    public String getThoiGianNhap() {
        return ThoiGianNhap;
    }

    public void setThoiGianNhap(String thoiGianNhap) {
        ThoiGianNhap = thoiGianNhap;
    }

    public Integer getThangDanhGia() {
        return ThangDanhGia;
    }

    public void setThangDanhGia(Integer thangDanhGia) {
        ThangDanhGia = thangDanhGia;
    }

    public EnStatusInformationData(int dataID, int dataType, int maDuong, Integer tuyenSo, String moTaTinhTrang, String kinhDo, String viDo, String caoDo, String nguoiNhap, String thoiGianNhap, Integer thangDanhGia) {
        DataID = dataID;
        DataType = dataType;
        MaDuong = maDuong;
        TuyenSo = tuyenSo;
        MoTaTinhTrang = moTaTinhTrang;
        KinhDo = kinhDo;
        ViDo = viDo;
        CaoDo = caoDo;
        NguoiNhap = nguoiNhap;
        ThoiGianNhap = thoiGianNhap;
        ThangDanhGia = thangDanhGia;
    }

    public EnStatusInformationData() {

    }
}
