package dulieu;

import java.sql.Date;
import java.sql.Time;

public class SuatChieu {
    private String masuatchieu;
    private Date ngaychieu;
    private Time giochieu;
    private float giave;
    private String maphim;
    private String maphong;
    private String trangthai; // ✅ Thêm thuộc tính trạng thái

    // ===================== Constructor mặc định (cho TableView, FXML) =====================
    public SuatChieu() {}

    // ===================== Constructor 6 tham số (mặc định trạng thái "Sắp chiếu") =====================
    public SuatChieu(String masuatchieu, Date ngaychieu, Time giochieu,
                     float giave, String maphim, String maphong) {
        this(masuatchieu, ngaychieu, giochieu, giave, maphim, maphong, "Sắp chiếu");
    }

    // ===================== Constructor 7 tham số (đầy đủ có trạng thái) =====================
    public SuatChieu(String masuatchieu, Date ngaychieu, Time giochieu,
                     float giave, String maphim, String maphong, String trangthai) {
        this.masuatchieu = masuatchieu;
        this.ngaychieu = ngaychieu;
        this.giochieu = giochieu;
        this.giave = giave;
        this.maphim = maphim;
        this.maphong = maphong;
        this.trangthai = trangthai;
    }

    // ===================== Getter & Setter =====================
    public String getMasuatchieu() { return masuatchieu; }
    public void setMasuatchieu(String masuatchieu) { this.masuatchieu = masuatchieu; }

    public Date getNgaychieu() { return ngaychieu; }
    public void setNgaychieu(Date ngaychieu) { this.ngaychieu = ngaychieu; }

    public Time getGiochieu() { return giochieu; }
    public void setGiochieu(Time giochieu) { this.giochieu = giochieu; }

    public float getGiave() { return giave; }
    public void setGiave(float giave) { this.giave = giave; }

    public String getMaphim() { return maphim; }
    public void setMaphim(String maphim) { this.maphim = maphim; }

    public String getMaphong() { return maphong; }
    public void setMaphong(String maphong) { this.maphong = maphong; }

    public String getTrangthai() { return trangthai; }
    public void setTrangthai(String trangthai) { this.trangthai = trangthai; }

    // ===================== Hỗ trợ debug (in thông tin nhanh) =====================
    @Override
    public String toString() {
        return masuatchieu + " | " + ngaychieu + " " + giochieu + " | " + trangthai;
    }
}
