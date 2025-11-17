package main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

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

        // ===== 2️⃣ CHỈ CHO PHÉP ADMIN ĐĂNG NHẬP =====
        if (email.equals("admin@gmail.com") && password.equals("12345678")) {

            // 🔔 THÔNG BÁO TRƯỚC
            showAlert("Thành công", "Chào mừng Quản trị viên!");

            // ➜ CHUYỂN TRANG
            chuyenTrangThongKe(event, "Quản trị viên");
            return;
        }

        // ❌ Nếu không phải admin → Sai tài khoản
        showAlert("Lỗi", "Sai email hoặc mật khẩu!");
    }

    // ==================================================
    //  HÀM CHUYỂN TRANG THỐNG KÊ (DÙNG CHUNG)
    // ==================================================
    private void chuyenTrangThongKe(ActionEvent event, String tenNguoiDung) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/giaodien/FXML.fxml"));
            Parent root = loader.load();

            FXMLController controller = loader.getController();
            controller.moTrangThongKe(); // Load Thống kê vào contentArea

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("📊 Hệ thống quản lý rạp chiếu phim");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
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
        alert.showAndWait(); 
    }
}