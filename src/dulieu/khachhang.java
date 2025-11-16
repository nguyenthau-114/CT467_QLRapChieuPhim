package dulieu;

public class khachhang {
    private String maKhachHang;
    private String tenKhachHang;
    private String sdt;
    private String email;

    private int tongVe; // ⭐ tổng vé tự tính, không lưu DB

    public khachhang(String maKhachHang, String tenKhachHang, String sdt, String email) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.sdt = sdt;
        this.email = email;
        this.tongVe = 0; // mặc định
    }

    public String getMaKhachHang() { return maKhachHang; }
    public String getTenKhachHang() { return tenKhachHang; }
    public String getSdt() { return sdt; }
    public String getEmail() { return email; }

    public int getTongVe() { return tongVe; }
    public void setTongVe(int tongVe) { this.tongVe = tongVe; }
}
