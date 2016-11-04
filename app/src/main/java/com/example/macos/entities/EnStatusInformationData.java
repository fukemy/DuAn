package com.example.macos.entities;

import com.example.macos.database.DataTypeItem;
import com.google.gson.annotations.SerializedName;

/**
 * Created by devil2010 on 7/22/16.
 */
public class EnStatusInformationData {
    @SerializedName("DataID")
    private String DataID;
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
    @SerializedName("DanhGia")
    private String DanhGia;


    public String getDataID() {
        return DataID;
    }

    public void setDataID(String dataID) {
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

    public String getThangDanhGia() {
        return DanhGia;
    }

    public void setThangDanhGia(String thangDanhGia) {
        DanhGia = thangDanhGia;
    }

    public EnStatusInformationData(String dataID, int dataType, int maDuong, Integer tuyenSo, String moTaTinhTrang, String kinhDo, String viDo, String caoDo, String nguoiNhap, String thoiGianNhap, String thangDanhGia) {
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
        DanhGia = thangDanhGia;
    }

    public EnStatusInformationData() {

    }

    public EnStatusInformationData(DataTypeItem item) {
        setViDo(item.getViDo());
        setKinhDo(item.getKinhDo());
        setTuyenSo(item.getTuyenSo());
        setNguoiNhap(item.getNguoiNhap());
        setCaoDo(item.getCaoDo());
        setDataID(item.getDataID());
        setDataType(item.getDataType());
        setMoTaTinhTrang(item.getMoTaTinhTrang());
        setThoiGianNhap(item.getThoiGianNhap());
        setThangDanhGia(item.getDanhGia());
        setNguoiNhap(item.getNguoiNhap());
    }
}
