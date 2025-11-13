package java;

public class khachhang {
    private String maKhachHang;
    private String tenKhachHang;
    private String sdt;
    private String email;

    public khachhang(String maKhachHang, String tenKhachHang, String sdt, String email) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.sdt = sdt;
        this.email = email;
    }

    public String getMaKhachHang() { return maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public String getSdt() { return sdt; }
    public String getEmail() { return email; }
}
