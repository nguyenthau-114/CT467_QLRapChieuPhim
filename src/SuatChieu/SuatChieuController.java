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
        colTrangThai.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTrangthai()));

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

    // ===================== TẢI DỮ LIỆU (TÍNH 4 TRẠNG THÁI) =====================
    @FXML
    public void taiLaiDuLieu() {
        dsSuatChieu.clear();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                showAlert("Lỗi kết nối", "Không thể kết nối MySQL. Vui lòng kiểm tra DBConnection.", AlertType.ERROR);
                return;
            }

            String sql = "SELECT masuatchieu, ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong FROM suatchieu";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Date ngay = rs.getDate("ngaychieu");
                Time gio = rs.getTime("giochieu");

                // ✅ Xác định trạng thái bằng Java (4 loại)
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

    // 🧠 Hàm xác định trạng thái suất chiếu (logic timeline chính xác)
    private String xacDinhTrangThai(Date ngayChieu) {
        LocalDate ngay = ngayChieu.toLocalDate();
        LocalDate homNay = LocalDate.now();
        long daysDiff = ChronoUnit.DAYS.between(homNay, ngay);

        /*
         * ────────────────────────────────────────────────────────────────
         * Đã chiếu   : < -30 ngày
         * Đang chiếu : -30 → +30 ngày (bao gồm hôm nay)
         * Sắp chiếu  : +31 → +60 ngày
         * Sắp ra mắt : > +60 ngày
         * ────────────────────────────────────────────────────────────────
         */
        if (daysDiff < -30) {
            return "Đã chiếu";
        } else if (daysDiff >= -30 && daysDiff <= 30) {
            return "Đang chiếu";
        } else if (daysDiff > 30 && daysDiff <= 60) {
            return "Sắp chiếu";
        } else {
            return "Sắp ra mắt";
        }
    }

    // ===================== THÊM SUẤT CHIẾU =====================
    @FXML
    public void themSuatChieu() {
        String gio = txtGioChieu.getText().trim();
        String gia = txtGiaVe.getText().trim();
        String phim = txtMaPhim.getText().trim();
        String phong = txtMaPhong.getText().trim();

        if (dpNgayChieu.getValue() == null || gio.isEmpty() || gia.isEmpty() || phim.isEmpty() || phong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", AlertType.WARNING);
            return;
        }

        Alert confirm = taoHopThoai("Xác nhận thêm suất chiếu mới?");
        confirm.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO suatchieu (ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong) VALUES (?, ?, ?, ?, ?)")) {

                    ps.setDate(1, Date.valueOf(dpNgayChieu.getValue()));
                    ps.setTime(2, Time.valueOf(gio));
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

    // ===================== SỬA / XÓA / HỖ TRỢ =====================
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

        Alert confirm = taoHopThoai("Xác nhận cập nhật suất chiếu?");
        confirm.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
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

    @FXML
    public void xoaSuatChieu() {
        String ma = txtMaSuatChieu.getText().trim();
        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn suất chiếu cần xóa!", AlertType.WARNING);
            return;
        }

        Alert confirm = taoHopThoai("Bạn có chắc muốn xóa suất chiếu " + ma + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
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

    // ===================== HỖ TRỢ =====================
    private Alert taoHopThoai(String noiDung) {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText(noiDung);
        ButtonType btnXacNhan = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnXacNhan, btnHuy);
        return confirm;
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

    // ===================== CHUYỂN TRANG & ĐĂNG XUẤT =====================
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

    @FXML
    private void dangXuat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/DangNhap.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Đăng nhập hệ thống");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Lỗi đăng xuất");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
