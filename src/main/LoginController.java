package main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import ketnoi_truyxuat.DBConnection;
import main.ThongkeController;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    // ============================================
    //  XỬ LÝ KHI NHẤN NÚT ĐĂNG NHẬP
    // ============================================
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // ===== 1️⃣ Kiểm tra hợp lệ =====
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ email và mật khẩu!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Lỗi", "Email không hợp lệ!");
            return;
        }

        if (password.length() < 8) {
            showAlert("Lỗi", "Mật khẩu phải có ít nhất 8 ký tự!");
            return;
        }

        // ===== 2️⃣ Đăng nhập ADMIN (Fix cứng) =====
        if (email.equals("admin@gmail.com") && password.equals("12345678")) {

            // 🔔 THÔNG BÁO TRƯỚC
            showAlert("Thành công", "Chào mừng Quản trị viên!");

            // ➜ CHUYỂN TRANG
            chuyenTrangThongKe(event, "Quản trị viên");

            return;
        }

        // ===== 3️⃣ Kiểm tra tài khoản trong database =====
        String sql = "SELECT tennhanvien FROM nhanvien WHERE email = ? AND matkhau = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String tenNV = rs.getString("tennhanvien");

                // 🔔 Thông báo trước
                showAlert("Thành công", "Chào mừng " + tenNV + "!");

                // ➜ CHUYỂN TRANG
                chuyenTrangThongKe(event, tenNV);

            } else {
                showAlert("Lỗi", "Sai email hoặc mật khẩu!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể kết nối CSDL!");
        }
    }

    // ==================================================
    //  HÀM CHUYỂN TRANG THỐNG KÊ (DÙNG CHUNG)
    // ==================================================
    private void chuyenTrangThongKe(ActionEvent event, String tenNguoiDung) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/giaodien/Thongke.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("📊 Thống kê - Rạp Chiếu Phim");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở trang thống kê!");
        }
    }

    // ==================================================
    //  HÀM HIỆN ALERT
    // ==================================================
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Đợi bấm OK xong mới chạy tiếp chương trình
    }
}
