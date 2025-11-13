/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dulieu;

public class NhanVien {
    private String maNhanVien;
    private String tenNhanVien;
    private String chucVu;
    private String sdt;
    private String email;

    public NhanVien(String maNhanVien, String tenNhanVien, String chucVu, String sdt, String email) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.chucVu = chucVu;
        this.sdt = sdt;
        this.email = email;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public String getTenNhanVien() { return tenNhanVien; }
    public String getChucVu() { return chucVu; }
    public String getSdt() { return sdt; }
    public String getEmail() { return email; }
}

