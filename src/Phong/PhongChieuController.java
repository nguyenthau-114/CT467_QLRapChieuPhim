package Phong;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class PhongChieuController {

    @FXML private TextField txtMaPhong, txtTenPhong, txtSoGhe, txtLoaiPhong;
    @FXML private TableView<PhongChieu> tablePhong;
    @FXML private TableColumn<PhongChieu, Integer> colSoGhe;
    @FXML private TableColumn<PhongChieu, String> colTenPhong, colMaPhong, colLoaiPhong;

    private ObservableList<PhongChieu> dsPhong = FXCollections.observableArrayList();

    // ---------------- KHỞI TẠO ----------------
    @FXML
    public void initialize() {
        colMaPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphong()));
        colTenPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenphong()));
        colSoGhe.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getSoghe()).asObject());
        colLoaiPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLoaiphong()));

        taiDuLieu();
    }

    // ---------------- TẢI DỮ LIỆU ----------------
    @FXML
    public void taiDuLieu() {
        dsPhong.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/htql_rap_phim", "root", "NTDiemMy221105@");
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM phongchieu")) {

            while (rs.next()) {
                dsPhong.add(new PhongChieu(
                        rs.getString("maphong"),
                        rs.getString("tenphong"),
                        rs.getInt("soghe"),
                        rs.getString("loaiphong")
                ));
            }
            tablePhong.setItems(dsPhong);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- THÊM ----------------
    @FXML
    public void themPhong() {
        String maPhong = txtMaPhong.getText().trim();
        String tenPhong = txtTenPhong.getText().trim();
        String soGheStr = txtSoGhe.getText().trim();
        String loaiPhong = txtLoaiPhong.getText().trim();

        if (maPhong.isEmpty() || tenPhong.isEmpty() || soGheStr.isEmpty() || loaiPhong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/htql_rap_phim", "root", "NTDiemMy221105@");
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO phongchieu (maphong, tenphong, soghe, loaiphong) VALUES (?, ?, ?, ?)")) {

            ps.setString(1, maPhong);
            ps.setString(2, tenPhong);
            ps.setInt(3, Integer.parseInt(soGheStr));
            ps.setString(4, loaiPhong);

            ps.executeUpdate();
            showAlert("Thành công", "Đã thêm phòng chiếu mới!", Alert.AlertType.INFORMATION);
            taiDuLieu();
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi thêm phòng", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    // ---------------- SỬA ----------------
    @FXML
    public void suaPhong() {
        if (txtMaPhong.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn phòng chiếu cần sửa!", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/htql_rap_phim", "root", "NTDiemMy221105@");
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE phongchieu SET tenphong=?, soghe=?, loaiphong=? WHERE maphong=?")) {

            ps.setString(1, txtTenPhong.getText());
            ps.setInt(2, Integer.parseInt(txtSoGhe.getText()));
            ps.setString(3, txtLoaiPhong.getText());
            ps.setString(4, txtMaPhong.getText());
            ps.executeUpdate();

            showAlert("Thành công", "Đã cập nhật thông tin phòng chiếu!", Alert.AlertType.INFORMATION);
            taiDuLieu();
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi cập nhật", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- XÓA ----------------
    @FXML
    public void xoaPhong() {
        if (txtMaPhong.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn phòng chiếu cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/htql_rap_phim", "root", "NTDiemMy221105@");
             PreparedStatement ps = conn.prepareStatement("DELETE FROM phongchieu WHERE maphong=?")) {

            ps.setString(1, txtMaPhong.getText());
            ps.executeUpdate();

            showAlert("Thành công", "Đã xóa phòng chiếu!", Alert.AlertType.INFORMATION);
            taiDuLieu();
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi xóa phòng", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- HÀM TIỆN ÍCH ----------------
    private void clearFields() {
        txtMaPhong.clear();
        txtTenPhong.clear();
        txtSoGhe.clear();
        txtLoaiPhong.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
