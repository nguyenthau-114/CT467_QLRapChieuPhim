package SuatChieu;

import java.sql.Date;
import java.sql.Time;

public class SuatChieu {

    private String masuatchieu;
    private Date ngaychieu;
    private Time giochieu;
    private float giave;
    private String maphim;
    private String maphong;

    // ----------- CONSTRUCTOR -----------
    public SuatChieu(String masuatchieu, Date ngaychieu, Time giochieu, float giave, String maphim, String maphong) {
        this.masuatchieu = masuatchieu;
        this.ngaychieu = ngaychieu;
        this.giochieu = giochieu;
        this.giave = giave;
        this.maphim = maphim;
        this.maphong = maphong;
    }

    // ----------- GETTER & SETTER -----------
    public String getMasuatchieu() {
        return masuatchieu;
    }

    public void setMasuatchieu(String masuatchieu) {
        this.masuatchieu = masuatchieu;
    }

    public Date getNgaychieu() {
        return ngaychieu;
    }

    public void setNgaychieu(Date ngaychieu) {
        this.ngaychieu = ngaychieu;
    }

    public Time getGiochieu() {
        return giochieu;
    }

    public void setGiochieu(Time giochieu) {
        this.giochieu = giochieu;
    }

    public float getGiave() {
        return giave;
    }

    public void setGiave(float giave) {
        this.giave = giave;
    }

    public String getMaphim() {
        return maphim;
    }

    public void setMaphim(String maphim) {
        this.maphim = maphim;
    }

    public String getMaphong() {
        return maphong;
    }

    public void setMaphong(String maphong) {
        this.maphong = maphong;
    }

    // ----------- TO STRING (TÙY CHỌN) -----------
    @Override
    public String toString() {
        return "SuatChieu{" +
                "masuatchieu='" + masuatchieu + '\'' +
                ", ngaychieu=" + ngaychieu +
                ", giochieu=" + giochieu +
                ", giave=" + giave +
                ", maphim='" + maphim + '\'' +
                ", maphong='" + maphong + '\'' +
                '}';
    }
}
