package dulieu;

public class TLapDay {
    private final String maSuat, tenPhim, phong, ngay, gio;
    private final int tongGhe, daBan;
    private final double tyLe;

    public TLapDay(String maSuat, String tenPhim, String phong,
                   String ngay, String gio, int tongGhe, int daBan, double tyLe) {

        this.maSuat = maSuat;
        this.tenPhim = tenPhim;
        this.phong = phong;
        this.ngay = ngay;
        this.gio = gio;
        this.tongGhe = tongGhe;
        this.daBan = daBan;
        this.tyLe = tyLe;
    }

    public String getMaSuat() { return maSuat; }
    public String getTenPhim() { return tenPhim; }
    public String getPhong() { return phong; }
    public String getNgay() { return ngay; }
    public String getGio() { return gio; }
    public int getTongGhe() { return tongGhe; }
    public int getDaBan() { return daBan; }
    public double getTyLe() { return tyLe; }
}
