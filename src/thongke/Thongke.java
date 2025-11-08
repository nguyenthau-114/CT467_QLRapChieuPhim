package thongke;

public class Thongke {
    private final String tenPhim, ngayChieu, gioChieu;
    private final int soVe;
    private final double doanhThu;

    public Thongke(String tenPhim, String ngayChieu, String gioChieu, int soVe, double doanhThu) {
        this.tenPhim = tenPhim;
        this.ngayChieu = ngayChieu;
        this.gioChieu = gioChieu;
        this.soVe = soVe;
        this.doanhThu = doanhThu;
    }

    public String getTenPhim() { return tenPhim; }
    public String getNgayChieu() { return ngayChieu; }
    public String getGioChieu() { return gioChieu; }
    public int getSoVe() { return soVe; }
    public double getDoanhThu() { return doanhThu; }
}
