/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package nhanvien;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ketnoi_truyxuat.DBConnection;
import java.sql.*;

public class NhanVienController {

    @FXML private TextField txtMaNV, txtTenNV, txtChucVu, txtSDT, txtEmail;
    @FXML private TableView<NhanVien> tableNV;
    @FXML private TableColumn<NhanVien, String> colMaNV, colTenNV, colChucVu, colSDT, colEmail;

    private ObservableList<NhanVien> dsNV = FXCollections.observableArrayList();

    // Biến lưu dữ liệu gốc khi chọn dòng
    private String originalMaNV = "", originalTenNV = "", originalChucVu = "", originalSDT = "", originalEmail = "";

    // ---------------- KHỞI TẠO ----------------
    @FXML
    public void initialize() {
        colMaNV.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaNhanVien()));
        colTenNV.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenNhanVien()));
        colChucVu.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getChucVu()));
        colSDT.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSdt()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));

        // Khi click chọn dòng → hiển thị + lưu gốc
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

        tableNV.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
            tableNV.setItems(dsNV);

        } catch (SQLException e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage(), Alert.AlertType.ERROR);
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
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO nhanvien VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, ma);
            ps.setString(2, ten);
            ps.setString(3, chucVu);
            ps.setString(4, sdt);
            ps.setString(5, email);
            ps.executeUpdate();
            onTaiDuLieu();
            clearFields();
        } catch (SQLException e) {
            showAlert("Lỗi thêm nhân viên", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- SỬA ----------------
    @FXML
    public void onSua() {
        if (txtMaNV.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn nhân viên cần sửa!", Alert.AlertType.WARNING);
            return;
        }

        String ma = txtMaNV.getText().trim();
        String ten = txtTenNV.getText().trim();
        String chucVu = txtChucVu.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (!ma.equals(originalMaNV)) {
            showAlert("Không thể sửa mã nhân viên", "Mã nhân viên là định danh duy nhất!", Alert.AlertType.WARNING);
            txtMaNV.setText(originalMaNV);
            return;
        }

        if (ten.equals(originalTenNV) && chucVu.equals(originalChucVu) &&
            sdt.equals(originalSDT) && email.equals(originalEmail)) {
            showAlert("Không có thay đổi", "Bạn chưa thay đổi thông tin nào để cập nhật!", Alert.AlertType.INFORMATION);
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
            ps.executeUpdate();

            onTaiDuLieu();
            clearFields();
        } catch (SQLException e) {
            showAlert("Lỗi cập nhật nhân viên", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- XÓA ----------------
    @FXML
    public void onXoa() {
        String ma = txtMaNV.getText().trim();

        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn nhân viên cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nhanvien WHERE manhanvien=?")) {
            ps.setString(1, ma);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                onTaiDuLieu();
                clearFields();
            } else {
                showAlert("Không tìm thấy", "Không có nhân viên có mã '" + ma + "'.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            showAlert("Lỗi xóa nhân viên", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- HÀM TIỆN ÍCH ----------------
    private void clearFields() {
        txtMaNV.clear();
        txtTenNV.clear();
        txtChucVu.clear();
        txtSDT.clear();
        txtEmail.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void dangXuat(javafx.event.ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã đăng xuất khỏi hệ thống!");
        alert.showAndWait();
        ((javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow()).close();
    }
}

