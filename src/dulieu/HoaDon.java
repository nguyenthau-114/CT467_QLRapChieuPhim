package dulieu;

import java.sql.Date;

public class HoaDon {

    private String maHD;
    private int soLuong;
    private Date ngayMua;
    private double tongTien;

    private String maKH;
    private String maCombo;
    private String maNV;

    // ===== Constructor =====
    public HoaDon(String maHD, int soLuong, Date ngayMua, double tongTien,
                  String maKH, String maCombo, String maNV) {
        this.maHD = maHD;
        this.soLuong = soLuong;
        this.ngayMua = ngayMua;
        this.tongTien = tongTien;
        this.maKH = maKH;
        this.maCombo = maCombo;
        this.maNV = maNV;
    }

    // ===== Getter =====
    public String getMaHD() {
        return maHD;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public Date getNgayMua() {
        return ngayMua;
    }

    public double getTongTien() {
        return tongTien;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaCombo() {
        return maCombo;
    }

    public String getMaNV() {
        return maNV;
    }
}
