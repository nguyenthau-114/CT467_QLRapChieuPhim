package SuatChieu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import ketnoi_truyxuat.DBConnection;
import java.sql.*;
import java.time.LocalDate;

public class SuatChieuController {

    @FXML private TextField txtMaSuatChieu, txtGioChieu, txtGiaVe, txtMaPhim, txtMaPhong, txtTimKiem;
    @FXML private DatePicker dpNgayChieu;
    @FXML private TableView<SuatChieu> tableSuatChieu;
    @FXML private TableColumn<SuatChieu, String> colMaSuatChieu, colMaPhim, colMaPhong;
    @FXML private TableColumn<SuatChieu, Date> colNgayChieu;
    @FXML private TableColumn<SuatChieu, Time> colGioChieu;
    @FXML private TableColumn<SuatChieu, Float> colGiaVe;
    @FXML private Button btnDangXuat;

    private ObservableList<SuatChieu> dsSuatChieu = FXCollections.observableArrayList();

    private String originalMaSuatChieu = "";
    private Date originalNgayChieu;
    private Time originalGioChieu;
    private float originalGiaVe = 0;
    private String originalMaPhim = "";
    private String originalMaPhong = "";

    @FXML
    public void initialize() {
        colMaSuatChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMasuatchieu()));
        colNgayChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNgaychieu()));
        colGioChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiochieu()));
        colGiaVe.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiave()));
        colMaPhim.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphim()));
        colMaPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphong()));

        tableSuatChieu.setOnMouseClicked(event -> {
            SuatChieu sc = tableSuatChieu.getSelectionModel().getSelectedItem();
            if (sc != null) {
                txtMaSuatChieu.setText(sc.getMasuatchieu());
                dpNgayChieu.setValue(sc.getNgaychieu().toLocalDate());
                txtGioChieu.setText(sc.getGiochieu().toString());
                txtGiaVe.setText(String.valueOf(sc.getGiave()));
                txtMaPhim.setText(sc.getMaphim());
                txtMaPhong.setText(sc.getMaphong());

                originalMaSuatChieu = sc.getMasuatchieu();
                originalNgayChieu = sc.getNgaychieu();
                originalGioChieu = sc.getGiochieu();
                originalGiaVe = sc.getGiave();
                originalMaPhim = sc.getMaphim();
                originalMaPhong = sc.getMaphong();
            }
        });

        tableSuatChieu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    // ===================== TẢI DỮ LIỆU =====================
    @FXML
    public void taiLaiDuLieu() {
        dsSuatChieu.clear();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                showAlert("Lỗi kết nối", "Không thể kết nối MySQL. Vui lòng kiểm tra DBConnection.", AlertType.ERROR);
                return;
            }

            String sql = "SELECT masuatchieu, ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong " +
                         "FROM suatchieu ORDER BY CAST(SUBSTRING(masuatchieu,3) AS UNSIGNED)";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                dsSuatChieu.add(new SuatChieu(
                        rs.getString("masuatchieu"),
                        rs.getDate("ngaychieu"),
                        rs.getTime("giochieu"),
                        rs.getFloat("giave"),
                        rs.getString("phim_maphim"),
                        rs.getString("phongchieu_maphong")
                ));
            }
            tableSuatChieu.setItems(dsSuatChieu);
        } catch (SQLException e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage(), AlertType.ERROR);
        }
    }

    
    // ===================== THÊM SUẤT CHIẾU (KIỂM TRA LỊCH TRÙNG) =====================
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

            // 🔹 1. GỌI FUNCTION KIỂM TRA LỊCH TRÙNG
            String sqlCheck = "SELECT fn_kiemtra_lichtrung(?, ?, ?, ?) AS trung";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setString(1, phong);
                psCheck.setDate(2, Date.valueOf(ngay));
                psCheck.setTime(3, Time.valueOf(gio.length() == 5 ? gio + ":00" : gio));
                psCheck.setString(4, phim);

                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        int trung = rs.getInt("trung");

                        if (trung == 1) {
                            showAlert("⛔ Lịch chiếu bị trùng",
                                    "Phòng '" + phong + "' Không thể thêm suất chiếu mới!",
                                    AlertType.WARNING);
                            return;
                        } else {
                            // 🔹 2. Nếu KHÔNG trùng → hỏi xác nhận thêm
                            Alert confirm = new Alert(AlertType.CONFIRMATION);
                            confirm.setTitle("✅ Lịch hợp lệ! Xác nhận thêm suất chiếu");
                            confirm.setHeaderText(null);
                            confirm.setContentText("Phòng '" + phong + "' hiện trống.\nBạn có muốn thêm suất chiếu này không?");
                            ButtonType btnThem = new ButtonType("Thêm", ButtonBar.ButtonData.OK_DONE);
                            ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
                            confirm.getButtonTypes().setAll(btnThem, btnHuy);

                            confirm.showAndWait().ifPresent(response -> {
                                if (response == btnThem) {
                                    try (PreparedStatement ps = conn.prepareStatement(
                                            "INSERT INTO suatchieu (ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong) VALUES (?, ?, ?, ?, ?)")) {

                                        ps.setDate(1, Date.valueOf(ngay));
                                        ps.setTime(2, Time.valueOf(gio.length() == 5 ? gio + ":00" : gio));
                                        ps.setFloat(3, Float.parseFloat(gia));
                                        ps.setString(4, phim);
                                        ps.setString(5, phong);

                                        int rows = ps.executeUpdate();
                                        if (rows > 0) {
                                            taiLaiDuLieu();
                                            showAlert("Thành công", "Đã thêm suất chiếu mới!", AlertType.INFORMATION);
                                            clearFields();
                                        }

                                    } catch (SQLException e) {
                                        showAlert("Lỗi thêm suất chiếu", e.getMessage(), AlertType.ERROR);
                                    }
                                }
                            });
                        }
                    }
                }
            }

        } catch (SQLException e) {
            showAlert("Lỗi kiểm tra lịch chiếu", e.getMessage(), AlertType.ERROR);
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

        if (ma.isEmpty() || dpNgayChieu.getValue() == null || gio.isEmpty() || gia.isEmpty() || phim.isEmpty() || phong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin!", AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Xác nhận cập nhật suất chiếu?");
        ButtonType btnXacNhan = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnXacNhan, btnHuy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == btnXacNhan) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE suatchieu SET ngaychieu=?, giochieu=?, giave=?, phim_maphim=?, phongchieu_maphong=? WHERE masuatchieu=?")) {

                    ps.setDate(1, Date.valueOf(dpNgayChieu.getValue()));
                    ps.setTime(2, Time.valueOf(gio));
                    ps.setFloat(3, Float.parseFloat(gia));
                    ps.setString(4, phim);
                    ps.setString(5, phong);
                    ps.setString(6, ma);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        taiLaiDuLieu();
                        showAlert("Thành công", "Cập nhật suất chiếu thành công!", AlertType.INFORMATION);
                        clearFields();
                    }

                } catch (SQLException e) {
                    showAlert("Lỗi cập nhật", e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    // ===================== XÓA SUẤT CHIẾU =====================
    @FXML
    public void xoaSuatChieu() {
        String ma = txtMaSuatChieu.getText().trim();
        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn suất chiếu cần xóa!", AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa suất chiếu " + ma + "?");
        ButtonType btnXacNhan = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnXacNhan, btnHuy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == btnXacNhan) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM suatchieu WHERE masuatchieu=?")) {
                    ps.setString(1, ma);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        taiLaiDuLieu();
                        showAlert("Thành công", "Đã xóa suất chiếu!", AlertType.INFORMATION);
                        clearFields();
                    }
                } catch (SQLException e) {
                    showAlert("Lỗi xóa suất chiếu", e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    // ===================== TÌM KIẾM =====================
    @FXML
    private void timKiem(KeyEvent event) {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        ObservableList<SuatChieu> ketQua = FXCollections.observableArrayList();
        for (SuatChieu sc : dsSuatChieu) {
            if (sc.getMasuatchieu().toLowerCase().contains(keyword)
                    || sc.getMaphim().toLowerCase().contains(keyword)
                    || sc.getMaphong().toLowerCase().contains(keyword)) {
                ketQua.add(sc);
            }
        }
        tableSuatChieu.setItems(ketQua);
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

    private void chuyenTrang(ActionEvent e, String fxmlPath) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlPath));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Không thể mở trang: " + fxmlPath).show();
        }
    }

    @FXML private void moTrangPhim(ActionEvent e) { chuyenTrang(e, "/phim/Phim_truycap.fxml"); }
    @FXML private void moTrangPhongChieu(ActionEvent e) { chuyenTrang(e, "/Phong/PhongChieu.fxml"); }
    @FXML private void moTrangVe(ActionEvent e) { chuyenTrang(e, "/ve/ve_truycap.fxml"); }
    @FXML private void moTrangThongKe(ActionEvent e) { chuyenTrang(e, "/thongke/Thongke.fxml"); }
    @FXML private void moTrangNhanVien(ActionEvent e) { chuyenTrang(e, "/nhanvien/NhanVien.fxml"); }
    @FXML private void moTrangKhachHang(ActionEvent e) { chuyenTrang(e, "/khachhang/khachhang.fxml"); }
}
