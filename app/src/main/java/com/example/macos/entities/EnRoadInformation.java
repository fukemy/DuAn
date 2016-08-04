package com.example.macos.entities;

import java.io.Serializable;

/**
 * Created by devil2010 on 7/22/16.
 */
public class EnRoadInformation implements Serializable{
    public int MaDuong ;
    public String TenDuong ;
    public String TuyenSo ;
    public String TuyenNhanhSo ;
    public String CapDuong ;
    public int MaDonViQuanLy ;
    public int TuCotKm ;
    public int DenCotKm ;
    public String ViDo ;
    public String KinhDo ;
    public int ChieuDai ;
    public String NgayDieuChinhCotKm ;
    public String ThoiDiemCapNhat ;
    public int MaTinh ;
    public Integer MaThanhPho ;
    public String LoaiLanDuong ;
    public Integer NamXayDung ;
    public Double HanhLangDuongBo ;
    public String LoaiKetCauDuong ;
    public Integer SoLanXeCoGioi ;
    public Double ChieuRongLanXeCoGioi ;
    public String LoaiMatDuongLanXeCoGioi ;
    public Integer SoLanxeThoSo ;
    public String LoaiMatDuongLanXeThoSo ;
    public Double ChieuRongLanXeThoSo ;
    public Double ChieuRongDuongXeChay ;
    public Double ChieuRongMatDuong ;
    public String LeDuong ;
    public Double ChieuRongLeDuong ;
    public String ViaHe ;
    public Double ChieuRongViaHe ;
    public String LoaiViaHe ;
    public Double ChieuRongThoatNuoc ;
    public Double TocDoThietKe ;
    public String LoaiDiaHinh ;
    public String LoaiNenDuong ;

    @Override
    public String toString() {
        return "EnRoadInformation{" +
                "CapDuong='" + CapDuong + '\'' +
                ", MaDuong=" + MaDuong +
                ", TenDuong='" + TenDuong + '\'' +
                ", TuyenSo='" + TuyenSo + '\'' +
                ", TuyenNhanhSo='" + TuyenNhanhSo + '\'' +
                ", MaDonViQuanLy=" + MaDonViQuanLy +
                ", TuCotKm=" + TuCotKm +
                ", DenCotKm=" + DenCotKm +
                ", ViDo='" + ViDo + '\'' +
                ", KinhDo='" + KinhDo + '\'' +
                ", ChieuDai=" + ChieuDai +
                ", NgayDieuChinhCotKm=" + NgayDieuChinhCotKm +
                ", ThoiDiemCapNhat=" + ThoiDiemCapNhat +
                ", MaTinh=" + MaTinh +
                ", MaThanhPho=" + MaThanhPho +
                ", LoaiLanDuong='" + LoaiLanDuong + '\'' +
                ", NamXayDung=" + NamXayDung +
                ", HanhLangDuongBo=" + HanhLangDuongBo +
                ", LoaiKetCauDuong='" + LoaiKetCauDuong + '\'' +
                ", SoLanXeCoGioi=" + SoLanXeCoGioi +
                ", ChieuRongLanXeCoGioi=" + ChieuRongLanXeCoGioi +
                ", LoaiMatDuongLanXeCoGioi='" + LoaiMatDuongLanXeCoGioi + '\'' +
                ", SoLanxeThoSo=" + SoLanxeThoSo +
                ", LoaiMatDuongLanXeThoSo='" + LoaiMatDuongLanXeThoSo + '\'' +
                ", ChieuRongLanXeThoSo=" + ChieuRongLanXeThoSo +
                ", ChieuRongDuongXeChay=" + ChieuRongDuongXeChay +
                ", ChieuRongMatDuong=" + ChieuRongMatDuong +
                ", LeDuong='" + LeDuong + '\'' +
                ", ChieuRongLeDuong=" + ChieuRongLeDuong +
                ", ViaHe='" + ViaHe + '\'' +
                ", ChieuRongViaHe=" + ChieuRongViaHe +
                ", LoaiViaHe='" + LoaiViaHe + '\'' +
                ", ChieuRongThoatNuoc=" + ChieuRongThoatNuoc +
                ", TocDoThietKe=" + TocDoThietKe +
                ", LoaiDiaHinh='" + LoaiDiaHinh + '\'' +
                ", LoaiNenDuong='" + LoaiNenDuong + '\'' +
                '}';
    }
}
