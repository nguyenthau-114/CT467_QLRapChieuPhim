package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ketnoi_truyxuat.DBConnection;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import dulieu.khachhang;

public class KhachhangController {

    @FXML private TextField tfMaKH, tfTenKH, tfSDT, tfEmail, tfTimKiem;
    @FXML private TableView<khachhang> tableKH;
    @FXML private TableColumn<khachhang, String> colMaKH, colTenKH, colSDT, colEmail;

    private ObservableList<khachhang> dsKH = FXCollections.observableArrayList();

    // Biến lưu dữ liệu gốc khi chọn dòng
    private String originalMaKH = "";
    private String originalTenKH = "";
    private String originalSDT = "";
    private String originalEmail = "";

    // ---------------- KHỞI TẠO ----------------
    @FXML
    public void initialize() {
        colMaKH.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaKhachHang()));
        colTenKH.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenKhachHang()));
        colSDT.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSdt()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        tableKH.setOnMouseClicked(event -> {
            khachhang selected = tableKH.getSelectionModel().getSelectedItem();
            if (selected != null) {
                tfMaKH.setText(selected.getMaKhachHang());
                tfTenKH.setText(selected.getTenKhachHang());
                tfSDT.setText(selected.getSdt());
                tfEmail.setText(selected.getEmail());

                originalMaKH = selected.getMaKhachHang();
                originalTenKH = selected.getTenKhachHang();
                originalSDT = selected.getSdt();
                originalEmail = selected.getEmail();
            }
        });

        tableKH.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ---------------- TẢI DỮ LIỆU ----------------
    @FXML
    public void onTaiDuLieu() {
        dsKH.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM khachhang ORDER BY makhachhang ASC")) {

            while (rs.next()) {
                dsKH.add(new khachhang(
                        rs.getString("makhachhang"),
                        rs.getString("tenkhachhang"),
                        rs.getString("sdt"),
                        rs.getString("email")
                ));
            }
            tableKH.setItems(dsKH);

        } catch (SQLException e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- THÊM ----------------
    @FXML
    public void onThem() {
        String maKH = tfMaKH.getText().trim();
        String tenKH = tfTenKH.getText().trim();
        String sdt = tfSDT.getText().trim();
        String email = tfEmail.getText().trim();

        if (maKH.isEmpty() || tenKH.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Thêm khách hàng mới?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO khachhang (makhachhang, tenkhachhang, sdt, email) VALUES (?, ?, ?, ?)")) {

                    ps.setString(1, maKH);
                    ps.setString(2, tenKH);
                    ps.setString(3, sdt);
                    ps.setString(4, email);
                    ps.executeUpdate();

                    onTaiDuLieu();
                    clearFields();

                } catch (SQLException e) {
                    showAlert("Lỗi thêm khách hàng", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // ---------------- SỬA ----------------
    @FXML
    public void onSua() {
        if (tfMaKH.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn khách hàng cần sửa!", Alert.AlertType.WARNING);
            return;
        }

        String maKH = tfMaKH.getText().trim();
        String tenKH = tfTenKH.getText().trim();
        String sdt = tfSDT.getText().trim();
        String email = tfEmail.getText().trim();

        if (tenKH.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
            showAlert("Thiếu dữ liệu", "Vui lòng nhập đầy đủ thông tin!", Alert.AlertType.WARNING);
            return;
        }

        if (!maKH.equals(originalMaKH)) {
            showAlert("Không thể sửa mã khách hàng",
                    "Mã khách hàng là định danh duy nhất, không thể thay đổi.",
                    Alert.AlertType.WARNING);
            tfMaKH.setText(originalMaKH);
            return;
        }

        boolean khongThayDoi =
                tenKH.equals(originalTenKH) &&
                sdt.equals(originalSDT) &&
                email.equals(originalEmail);

        if (khongThayDoi) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cập nhật thông tin khách hàng?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE khachhang SET tenkhachhang=?, sdt=?, email=? WHERE makhachhang=?")) {

                    ps.setString(1, tenKH);
                    ps.setString(2, sdt);
                    ps.setString(3, email);
                    ps.setString(4, maKH);
                    ps.executeUpdate();

                    onTaiDuLieu();
                    clearFields();

                } catch (SQLException e) {
                    showAlert("Lỗi cập nhật", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // ---------------- XÓA ----------------
    @FXML
    public void onXoa() {
        String maKH = tfMaKH.getText().trim();

        if (maKH.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn khách hàng cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa khách hàng có mã '" + maKH + "'?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM khachhang WHERE makhachhang=?")) {

                    ps.setString(1, maKH);
                    ps.executeUpdate();

                    onTaiDuLieu();
                    clearFields();

                } catch (SQLException e) {
                    showAlert("Lỗi xóa khách hàng", e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    // ---------------- HÀM TIỆN ÍCH ----------------
    private void clearFields() {
        tfMaKH.clear();
        tfTenKH.clear();
        tfSDT.clear();
        tfEmail.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ---------------- ĐĂNG XUẤT ----------------
    @FXML
    private void dangXuat(javafx.event.ActionEvent event) {
        ((javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow()).close();
    }
// ===============================
// 📂 MENU DỮ LIỆU (hiện/ẩn + điều hướng)
// ===============================
@FXML private VBox menuDuLieu;

@FXML
private void hienMenuDuLieu() {
    menuDuLieu.setVisible(true);
    menuDuLieu.setManaged(true);
}

@FXML
private void anMenuDuLieu() {
    new Thread(() -> {
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        javafx.application.Platform.runLater(() -> {
            if (!menuDuLieu.isHover()) {
                menuDuLieu.setVisible(false);
                menuDuLieu.setManaged(false);
            }
        });
    }).start();
}

@FXML
private void giuMenuKhiHover() {
    menuDuLieu.setVisible(true);
    menuDuLieu.setManaged(true);
}

@FXML
private void anMenuKhiRoi() {
    menuDuLieu.setVisible(false);
    menuDuLieu.setManaged(false);
}

// ===============================
// 🔄 CHUYỂN TRANG TRONG MENU
// ===============================
@FXML private void moTrangPhim(ActionEvent e) { chuyenTrang(e, "/phim/Phim_truycap.fxml"); }
@FXML private void moTrangSuatChieu(ActionEvent e) { chuyenTrang(e, "/SuatChieu/SuatChieu.fxml"); }
@FXML private void moTrangPhongChieu(ActionEvent e) { chuyenTrang(e, "/Phong/PhongChieu.fxml"); }
@FXML private void moTrangVe(ActionEvent e) { chuyenTrang(e, "/ve/ve_truycap.fxml"); }

private void chuyenTrang(ActionEvent e, String path) {
    try {
        javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(path));
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    } catch (Exception ex) {
        ex.printStackTrace();
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                "Không thể mở trang: " + path).show();
    }
}

@FXML
private void moTrangThongKe(ActionEvent e) {
    chuyenTrang(e, "/thongke/Thongke.fxml");
}
@FXML
private void moTrangNhanVien(ActionEvent e) {
    chuyenTrang(e, "/nhanvien/NhanVien.fxml");
}

}

