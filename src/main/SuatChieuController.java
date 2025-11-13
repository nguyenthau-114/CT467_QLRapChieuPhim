package main;

import dulieu.SuatChieu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import ketnoi_truyxuat.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SuatChieuController {

    @FXML private TextField txtMaSuatChieu, txtGioChieu, txtGiaVe, txtMaPhim, txtMaPhong, txtTimKiem;
    @FXML private DatePicker dpNgayChieu;
    @FXML private TableView<SuatChieu> tableSuatChieu;
    @FXML private TableColumn<SuatChieu, String> colMaSuatChieu, colMaPhim, colMaPhong, colTrangThai;
    @FXML private TableColumn<SuatChieu, Date> colNgayChieu;
    @FXML private TableColumn<SuatChieu, Time> colGioChieu;
    @FXML private TableColumn<SuatChieu, Float> colGiaVe;
    @FXML private Button btnDangXuat;

    private ObservableList<SuatChieu> dsSuatChieu = FXCollections.observableArrayList();

    // ======================== KHỞI TẠO ========================
    @FXML
    public void initialize() {

        colMaSuatChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMasuatchieu()));
        colNgayChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNgaychieu()));
        colGioChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiochieu()));
        colGiaVe.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiave()));
        colMaPhim.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphim()));
        colMaPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphong()));
        colTrangThai.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTrangthai()));

        tableSuatChieu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // ❌ KHÔNG load dữ liệu khi mở giao diện
        // taiLaiDuLieu();

        // ✔ Listener chọn dòng
        tableSuatChieu.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, sc) -> {
            if (sc != null) {
                txtMaSuatChieu.setText(sc.getMasuatchieu());
                dpNgayChieu.setValue(sc.getNgaychieu().toLocalDate());
                txtGioChieu.setText(sc.getGiochieu().toString());
                txtGiaVe.setText(String.valueOf(sc.getGiave()));
                txtMaPhim.setText(sc.getMaphim());
                txtMaPhong.setText(sc.getMaphong());
            }
        });
    }

    // ===================== TẢI DỮ LIỆU =====================
    @FXML
    public void taiLaiDuLieu() {
        dsSuatChieu.clear();

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM suatchieu";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Date ngay = rs.getDate("ngaychieu");
                Time gio = rs.getTime("giochieu");

                String trangThai = xacDinhTrangThai(ngay);

                dsSuatChieu.add(new SuatChieu(
                        rs.getString("masuatchieu"),
                        ngay,
                        gio,
                        rs.getFloat("giave"),
                        rs.getString("phim_maphim"),
                        rs.getString("phongchieu_maphong"),
                        trangThai
                ));
            }

            tableSuatChieu.setItems(dsSuatChieu);

        } catch (SQLException e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== TÍNH TRẠNG THÁI =====================
    private String xacDinhTrangThai(Date ngayChieu) {

        LocalDate ngay = ngayChieu.toLocalDate();
        LocalDate homNay = LocalDate.now();

        long daysDiff = ChronoUnit.DAYS.between(homNay, ngay);

        if (daysDiff < -30) return "Đã chiếu";
        if (daysDiff <= 30) return "Đang chiếu";
        if (daysDiff <= 60) return "Sắp chiếu";
        return "Sắp ra mắt";
    }


    // ===================== THÊM SUẤT CHIẾU =====================
    @FXML
    public void themSuatChieu() {
        String gio = txtGioChieu.getText().trim();
        String gia = txtGiaVe.getText().trim();
        String phim = txtMaPhim.getText().trim();
        String phong = txtMaPhong.getText().trim();
        LocalDate ngay = dpNgayChieu.getValue();

        if (ngay == null || gio.isEmpty() || gia.isEmpty() || phim.isEmpty() || phong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", AlertType.WARNING);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String sqlCheck = "SELECT fn_kiemtra_lichtrung(?, ?, ?, ?) AS trung";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);

            psCheck.setString(1, phong);
            psCheck.setDate(2, Date.valueOf(ngay));
            psCheck.setTime(3, Time.valueOf(gio.length() == 5 ? gio + ":00" : gio));
            psCheck.setString(4, phim);

            ResultSet rs = psCheck.executeQuery();
            rs.next();

            if (rs.getInt("trung") == 1) {
                showAlert("⛔ Lịch trùng", "Phòng '" + phong + "' đã có suất chiếu trùng!", AlertType.WARNING);
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO suatchieu (ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong) VALUES (?, ?, ?, ?, ?)"
            );

            ps.setDate(1, Date.valueOf(ngay));
            ps.setTime(2, Time.valueOf(gio.length() == 5 ? gio + ":00" : gio));
            ps.setFloat(3, Float.parseFloat(gia));
            ps.setString(4, phim);
            ps.setString(5, phong);

            ps.executeUpdate();

            taiLaiDuLieu();
            clearFields();
            showAlert("🎉 Thành công", "Thêm suất chiếu thành công!", AlertType.INFORMATION);

        } catch (SQLException e) {
            showAlert("Lỗi thêm suất chiếu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== SỬA SUẤT CHIẾU =====================
    @FXML
    public void suaSuatChieu() {

        String ma = txtMaSuatChieu.getText().trim();
        String gio = txtGioChieu.getText().trim();
        String gia = txtGiaVe.getText().trim();
        String phim = txtMaPhim.getText().trim();
        String phong = txtMaPhong.getText().trim();
        LocalDate ngay = dpNgayChieu.getValue();

        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn suất chiếu cần sửa!", AlertType.WARNING);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE suatchieu SET ngaychieu=?, giochieu=?, giave=?, phim_maphim=?, phongchieu_maphong=? WHERE masuatchieu=?"
            );

            ps.setDate(1, Date.valueOf(ngay));
            ps.setTime(2, Time.valueOf(gio.length() == 5 ? gio + ":00" : gio));
            ps.setFloat(3, Float.parseFloat(gia));
            ps.setString(4, phim);
            ps.setString(5, phong);
            ps.setString(6, ma);

            ps.executeUpdate();

            taiLaiDuLieu();
            clearFields();
            showAlert("🎉 Thành công", "Đã sửa suất chiếu!", AlertType.INFORMATION);

        } catch (SQLException e) {
            showAlert("Lỗi sửa suất chiếu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== XÓA SUẤT CHIẾU =====================
    @FXML
    public void xoaSuatChieu() {

        String ma = txtMaSuatChieu.getText().trim();

        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn suất chiếu cần xóa!", AlertType.WARNING);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("DELETE FROM suatchieu WHERE masuatchieu=?");
            ps.setString(1, ma);
            ps.executeUpdate();

            taiLaiDuLieu();
            clearFields();
            showAlert("🗑️ Đã xóa", "Xóa suất chiếu thành công!", AlertType.INFORMATION);

        } catch (SQLException e) {
            showAlert("Lỗi xóa suất chiếu", e.getMessage(), AlertType.ERROR);
        }
    }


    // ===================== HỖ TRỢ =====================
    private void clearFields() {
        txtMaSuatChieu.clear();
        dpNgayChieu.setValue(null);
        txtGioChieu.clear();
        txtGiaVe.clear();
        txtMaPhim.clear();
        txtMaPhong.clear();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void dangXuat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/DangNhap.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Đăng nhập hệ thống");
            stage.show();
        } catch (Exception e) {
            showAlert("Lỗi đăng xuất", e.getMessage(), AlertType.ERROR);
        }
    }
}
