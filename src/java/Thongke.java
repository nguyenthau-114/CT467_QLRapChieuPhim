package java;

public class Thongke {
    private final String tenPhim;
    private final int soVe;
    private final double doanhThu;

    public Thongke(String tenPhim, int soVe, double doanhThu) {
        this.tenPhim = tenPhim;
        this.soVe = soVe;
        this.doanhThu = doanhThu;
    }

    public String getTenPhim() { return tenPhim; }
    public int getSoVe() { return soVe; }
    public double getDoanhThu() { return doanhThu; }
}
