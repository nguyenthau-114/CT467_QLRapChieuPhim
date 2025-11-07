package giaodien;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

// 🔹 Thêm các import cần thiết cho phần đăng ký
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import ketnoi_truyxuat.DBConnection;

public class DashboardController {
    @FXML private AnchorPane leftPane; // hộp bên trái

    private static class Delta { double dx, dy; }

    @FXML
    private void initialize() {
        // Gắn kéo cho mọi node có class "draggable-left" trong leftPane
        for (Node n : leftPane.lookupAll(".draggable-left")) {
            makeDraggable((Region) n);
        }

        // 🔹 Nếu có các nút đăng ký trong giao diện đăng ký thì sẽ tự gắn handler
        if (registerButton != null && backToLogin != null) {
            registerButton.setOnAction(this::handleRegister);
            backToLogin.setOnAction(this::openLogin);
        }
    }

    private void makeDraggable(Region node) {
        node.setManaged(false);
        node.setCursor(Cursor.HAND);
        Delta d = new Delta();

        node.setOnMousePressed(e -> {
            d.dx = node.getLayoutX() - e.getSceneX();
            d.dy = node.getLayoutY() - e.getSceneY();
            node.setCursor(Cursor.MOVE);
            e.consume();
        });

        node.setOnMouseDragged(e -> {
            double nx = e.getSceneX() + d.dx;
            double ny = e.getSceneY() + d.dy;

            // giới hạn trong khung trái
            double maxX = Math.max(0, leftPane.getWidth()  - node.getWidth());
            double maxY = Math.max(0, leftPane.getHeight() - node.getHeight());
            node.setLayoutX(Math.max(0, Math.min(nx, maxX)));
            node.setLayoutY(Math.max(0, Math.min(ny, maxY)));
            e.consume();
        });

        node.setOnMouseReleased(e -> node.setCursor(Cursor.HAND));
    }

    // ===============================
    // 🔽 Phần thêm mới: xử lý Đăng ký
    // ===============================

    @FXML private TextField hoTenField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Hyperlink backToLogin;

    private void handleRegister(ActionEvent event) {
        String hoTen = hoTenField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (hoTen.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            show(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!");
            return;
        }
        if (!pass.equals(confirm)) {
            show(Alert.AlertType.ERROR, "Mật khẩu không khớp", "Mật khẩu nhập lại không trùng khớp!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                show(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể kết nối CSDL!");
                return;
            }

            String sql = "INSERT INTO taikhoan (hoTen, email, matKhau) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hoTen);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.executeUpdate();

            show(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký tài khoản thành công!");
            openLogin(event);

        } catch (SQLException e) {
            show(Alert.AlertType.ERROR, "Lỗi SQL", e.getMessage());
        }
    }

    private void openLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/giaodien/DangNhap.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập hệ thống");
        } catch (IOException e) {
            show(Alert.AlertType.ERROR, "Lỗi giao diện", "Không thể mở DangNhap.fxml");
        }
    }

    private void show(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
