package doituong;

public class phim {
    private String maPhim;
    private String tenPhim;
    private String theLoai;
    private String daoDien;
    private int thoiLuong;
    private String ngayKhoiChieu;
    private int doTuoiChoPhep;

    public phim(String maPhim, String tenPhim, String theLoai, String daoDien,
                int thoiLuong, String ngayKhoiChieu, int doTuoiChoPhep) {
        this.maPhim = maPhim;
        this.tenPhim = tenPhim;
        this.theLoai = theLoai;
        this.daoDien = daoDien;
        this.thoiLuong = thoiLuong;
        this.ngayKhoiChieu = ngayKhoiChieu;
        this.doTuoiChoPhep = doTuoiChoPhep;
    }

    public String getMaPhim() { return maPhim; }
    public String getTenPhim() { return tenPhim; }
    public String getTheLoai() { return theLoai; }
    public String getDaoDien() { return daoDien; }
    public int getThoiLuong() { return thoiLuong; }
    public String getNgayKhoiChieu() { return ngayKhoiChieu; }
    public int getDoTuoiChoPhep() { return doTuoiChoPhep; }
}
