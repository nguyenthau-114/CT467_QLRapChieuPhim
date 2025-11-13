package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import ketnoi_truyxuat.DBConnection;
import java.sql.*;
import javafx.scene.layout.VBox;
import dulieu.NhanVien;

public class NhanVienController {

    @FXML private TextField txtMaNV, txtTenNV, txtChucVu, txtSDT, txtEmail, txtTimKiem;
    @FXML private TableView<NhanVien> tableNV;
    @FXML private TableColumn<NhanVien, String> colMaNV, colTenNV, colChucVu, colSDT, colEmail;
    @FXML private Button btnDangXuat;

    private final ObservableList<NhanVien> dsNV = FXCollections.observableArrayList();

    // Lưu dữ liệu gốc để so sánh khi sửa
    private String originalMaNV = "", originalTenNV = "", originalChucVu = "", originalSDT = "", originalEmail = "";

    // ---------------- KHỞI TẠO ----------------
    @FXML
    public void initialize() {
        colMaNV.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaNhanVien()));
        colTenNV.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenNhanVien()));
        colChucVu.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getChucVu()));
        colSDT.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSdt()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        tableNV.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Khi chọn dòng trong bảng
        tableNV.setOnMouseClicked(event -> {
            NhanVien nv = tableNV.getSelectionModel().getSelectedItem();
            if (nv != null) {
                txtMaNV.setText(nv.getMaNhanVien());
                txtTenNV.setText(nv.getTenNhanVien());
                txtChucVu.setText(nv.getChucVu());
                txtSDT.setText(nv.getSdt());
                txtEmail.setText(nv.getEmail());

                originalMaNV = nv.getMaNhanVien();
                originalTenNV = nv.getTenNhanVien();
                originalChucVu = nv.getChucVu();
                originalSDT = nv.getSdt();
                originalEmail = nv.getEmail();
            }
        });

        onTaiDuLieu();
    }

    // ---------------- TẢI DỮ LIỆU ----------------
    @FXML
    public void onTaiDuLieu() {
        dsNV.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM nhanvien ORDER BY manhanvien ASC")) {

            while (rs.next()) {
                dsNV.add(new NhanVien(
                        rs.getString("manhanvien"),
                        rs.getString("tennhanvien"),
                        rs.getString("chucvu"),
                        rs.getString("sdt"),
                        rs.getString("email")
                ));
            }

            /*tableNV.setItems(dsNV);
            System.out.println("✅ Đã tải " + dsNV.size() + " nhân viên từ CSDL.");*/

        } catch (SQLException e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ---------------- THÊM ----------------
    @FXML
    public void onThem() {
        String ma = txtMaNV.getText().trim();
        String ten = txtTenNV.getText().trim();
        String chucVu = txtChucVu.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || chucVu.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", AlertType.WARNING);
            return;
        }

        if (!showConfirmDialog("Xác nhận thêm mới", "Bạn có chắc muốn thêm nhân viên này không?")) {
            clearFields();
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO nhanvien VALUES (?, ?, ?, ?, ?)")) {

            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, chucVu);
            ps.setString(4, sdt);
            ps.setString(5, email);

            /*int rows = ps.executeUpdate();
            if (rows > 0) {
                showAlert("Thành công", "Đã thêm nhân viên mới!", AlertType.INFORMATION);
                onTaiDuLieu();
                clearFields();
            }*/

        } catch (SQLException e) {
            showAlert("Lỗi thêm nhân viên", e.getMessage(), AlertType.ERROR);
        }
    }

    // ---------------- SỬA ----------------
    @FXML
    public void onSua() {
        if (txtMaNV.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn nhân viên cần sửa!", AlertType.WARNING);
            return;
        }

        String ma = txtMaNV.getText().trim();
        String ten = txtTenNV.getText().trim();
        String chucVu = txtChucVu.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        // Không cho phép đổi mã
        if (!ma.equals(originalMaNV)) {
            showAlert("Không thể sửa mã nhân viên",
                    "Mã nhân viên là định danh duy nhất, hệ thống sẽ khôi phục lại mã cũ.",
                    AlertType.WARNING);
            txtMaNV.setText(originalMaNV);
            return;
        }

        boolean khongThayDoi =
                ten.equals(originalTenNV) &&
                chucVu.equals(originalChucVu) &&
                sdt.equals(originalSDT) &&
                email.equals(originalEmail);

        if (khongThayDoi) {
            showAlert("Không có thay đổi", "Bạn chưa thay đổi thông tin nào để cập nhật.", AlertType.INFORMATION);
            return;
        }

        if (!showConfirmDialog("Xác nhận sửa thông tin",
                "Bạn có chắc muốn cập nhật thông tin nhân viên này không?")) {
            clearFields();
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE nhanvien SET tennhanvien=?, chucvu=?, sdt=?, email=? WHERE manhanvien=?")) {

            ps.setString(1, ten);
            ps.setString(2, chucVu);
            ps.setString(3, sdt);
            ps.setString(4, email);
            ps.setString(5, ma);

            /*int rows = ps.executeUpdate();
            if (rows > 0) {
                showAlert("Thành công", "Cập nhật thông tin nhân viên thành công!", AlertType.INFORMATION);
                onTaiDuLieu();
                clearFields();
            }*/

        } catch (SQLException e) {
            showAlert("Lỗi cập nhật nhân viên", e.getMessage(), AlertType.ERROR);
        }
    }

    // ---------------- XÓA ----------------
    @FXML
    public void onXoa() {
        String ma = txtMaNV.getText().trim();

        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn nhân viên cần xóa!", AlertType.WARNING);
            return;
        }

        if (!showConfirmDialog("Xác nhận xóa", "Bạn có chắc muốn xóa nhân viên có mã '" + ma + "' không?")) {
            clearFields();
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nhanvien WHERE manhanvien=?")) {

            ps.setString(1, ma);
            int rows = ps.executeUpdate();

            /*if (rows > 0) {
                showAlert("Thành công", "Đã xóa nhân viên!", AlertType.INFORMATION);
                onTaiDuLieu();
                clearFields();
            } else {
                showAlert("Không tìm thấy", "Không có nhân viên có mã '" + ma + "'.", AlertType.WARNING);
            }*/

        } catch (SQLException e) {
            showAlert("Lỗi xóa nhân viên", e.getMessage(), AlertType.ERROR);
        }
    }

    // ---------------- TÌM KIẾM ----------------
    @FXML
    public void onTimKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            tableNV.setItems(dsNV);
            return;
        }

        ObservableList<NhanVien> ketQua = FXCollections.observableArrayList();
        for (NhanVien nv : dsNV) {
            if (nv.getMaNhanVien().toLowerCase().contains(keyword)
                    || nv.getTenNhanVien().toLowerCase().contains(keyword)
                    || nv.getChucVu().toLowerCase().contains(keyword)) {
                ketQua.add(nv);
            }
        }
        tableNV.setItems(ketQua);
    }

    // ---------------- ĐĂNG XUẤT ----------------
    @FXML
    private void dangXuat(ActionEvent event) {
        showAlert("Đăng xuất", "Bạn đã đăng xuất khỏi hệ thống!", AlertType.INFORMATION);
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    // ---------------- HÀM HỖ TRỢ ----------------
    private void clearFields() {
        txtMaNV.clear();
        txtTenNV.clear();
        txtChucVu.clear();
        txtSDT.clear();
        txtEmail.clear();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmDialog(String title, String message) {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(null);
        confirm.setContentText(message);

        ButtonType btnXacNhan = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnHuy = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(btnXacNhan, btnHuy);

        confirm.showAndWait();
        return confirm.getResult() == btnXacNhan;
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

}
