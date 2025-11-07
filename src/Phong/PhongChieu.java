package Phong;

public class PhongChieu {

    private String maphong;
    private String tenphong;
    private int soghe;
    private String loaiphong;

    // ----------- CONSTRUCTOR -----------
    public PhongChieu(String maphong, String tenphong, int soghe, String loaiphong) {
        this.maphong = maphong;
        this.tenphong = tenphong;
        this.soghe = soghe;
        this.loaiphong = loaiphong;
    }

    // ----------- GETTER & SETTER -----------
    public String getMaphong() {
        return maphong;
    }

    public void setMaphong(String maphong) {
        this.maphong = maphong;
    }

    public String getTenphong() {
        return tenphong;
    }

    public void setTenphong(String tenphong) {
        this.tenphong = tenphong;
    }

    public int getSoghe() {
        return soghe;
    }

    public void setSoghe(int soghe) {
        this.soghe = soghe;
    }

    public String getLoaiphong() {
        return loaiphong;
    }

    public void setLoaiphong(String loaiphong) {
        this.loaiphong = loaiphong;
    }

    // ----------- TO STRING (TÙY CHỌN) -----------
    @Override
    public String toString() {
        return "PhongChieu{" +
                "maphong='" + maphong + '\'' +
                ", tenphong='" + tenphong + '\'' +
                ", soghe=" + soghe +
                ", loaiphong='" + loaiphong + '\'' +
                '}';
    }
}
