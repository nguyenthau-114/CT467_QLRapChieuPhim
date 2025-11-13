package java;

import javafx.beans.property.*;
import java.sql.Date;

public class ve {
    private final StringProperty mave;
    private final ObjectProperty<Date> ngaydat;
    private final DoubleProperty giave;
    private final StringProperty trangthai;
    private final StringProperty suatchieu_masuatchieu;
    private final StringProperty khachhang_makhachhang;
    private final StringProperty ghe_maghe;

    public ve(String mave, Date ngaydat, double giave, String trangthai,
              String suatchieu_masuatchieu, String khachhang_makhachhang, String ghe_maghe) {
        this.mave = new SimpleStringProperty(mave);
        this.ngaydat = new SimpleObjectProperty<>(ngaydat);
        this.giave = new SimpleDoubleProperty(giave);
        this.trangthai = new SimpleStringProperty(trangthai);
        this.suatchieu_masuatchieu = new SimpleStringProperty(suatchieu_masuatchieu);
        this.khachhang_makhachhang = new SimpleStringProperty(khachhang_makhachhang);
        this.ghe_maghe = new SimpleStringProperty(ghe_maghe);
    }

    // ========= GETTERS =========
    public String getMave() { return mave.get(); }
    public Date getNgaydat() { return ngaydat.get(); }
    public double getGiave() { return giave.get(); }
    public String getTrangthai() { return trangthai.get(); }
    public String getSuatchieu_masuatchieu() { return suatchieu_masuatchieu.get(); }
    public String getKhachhang_makhachhang() { return khachhang_makhachhang.get(); }
    public String getGhe_maghe() { return ghe_maghe.get(); }

    // ========= PROPERTY METHODS =========
    public StringProperty maveProperty() { return mave; }
    public ObjectProperty<Date> ngaydatProperty() { return ngaydat; }
    public DoubleProperty giaveProperty() { return giave; }
    public StringProperty trangthaiProperty() { return trangthai; }
    public StringProperty suatchieu_masuatchieuProperty() { return suatchieu_masuatchieu; }
    public StringProperty khachhang_makhachhangProperty() { return khachhang_makhachhang; }
    public StringProperty ghe_magheProperty() { return ghe_maghe; }

    // ========= SETTERS =========
    public void setMave(String value) { mave.set(value); }
    public void setNgaydat(Date value) { ngaydat.set(value); }
    public void setGiave(double value) { giave.set(value); }
    public void setTrangthai(String value) { trangthai.set(value); }
    public void setSuatchieu_masuatchieu(String value) { suatchieu_masuatchieu.set(value); }
    public void setKhachhang_makhachhang(String value) { khachhang_makhachhang.set(value); }
    public void setGhe_maghe(String value) { ghe_maghe.set(value); }
}
