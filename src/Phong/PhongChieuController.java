package Phong;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ketnoi_truyxuat.DBConnection;
import java.sql.*;

public class PhongChieuController {

    @FXML private TextField txtMaPhong, txtTenPhong, txtSoGhe, txtLoaiPhong;
    @FXML private TableView<PhongChieu> tablePhong;
    @FXML private TableColumn<PhongChieu, String> colMaPhong, colTenPhong, colLoaiPhong;
    @FXML private TableColumn<PhongChieu, Integer> colSoGhe;

    private ObservableList<PhongChieu> dsPhong = FXCollections.observableArrayList();

    // Biến lưu dữ liệu gốc khi chọn dòng
    private String originalMaphong = "";
    private String originalTenphong = "";
    private int originalSoghe = 0;
    private String originalLoaiphong = "";

    // ---------------- KHỞI TẠO ----------------
    @FXML
    public void initialize() {
        colMaPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphong()));
        colTenPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTenphong()));
        colSoGhe.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getSoghe()).asObject());
        colLoaiPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLoaiphong()));

        // Khi click chọn 1 dòng trong bảng → tự hiển thị lên TextField + lưu bản gốc
        tablePhong.setOnMouseClicked(event -> {
            PhongChieu selected = tablePhong.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtMaPhong.setText(selected.getMaphong());
                txtTenPhong.setText(selected.getTenphong());
                txtSoGhe.setText(String.valueOf(selected.getSoghe()));
                txtLoaiPhong.setText(selected.getLoaiphong());

                // Lưu dữ liệu gốc
                originalMaphong = selected.getMaphong();
                originalTenphong = selected.getTenphong();
                originalSoghe = selected.getSoghe();
                originalLoaiphong = selected.getLoaiphong();
            }
        });
    }

    // ---------------- TẢI DỮ LIỆU ----------------
    @FXML
    public void taiDuLieu() {
        dsPhong.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM phongchieu ORDER BY maphong ASC")) {

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

        // Xác nhận thêm
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận thêm phòng");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn thêm phòng chiếu này không?");
        ButtonType dongY = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType huy = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == dongY) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO phongchieu (maphong, tenphong, soghe, loaiphong) VALUES (?, ?, ?, ?)")) {

                    ps.setString(1, maPhong);
                    ps.setString(2, tenPhong);
                    ps.setInt(3, Integer.parseInt(soGheStr));
                    ps.setString(4, loaiPhong);
                    ps.executeUpdate();

                    taiDuLieu();
                    clearFields();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Lỗi thêm phòng", e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                clearFields(); // Nếu bấm Không → xóa form
            }
        });
    }

    // ---------------- SỬA ----------------
    @FXML
    public void suaPhong() {
        if (txtMaPhong.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn phòng chiếu cần sửa!", Alert.AlertType.WARNING);
            return;
        }

        String maPhong = txtMaPhong.getText().trim();
        String tenPhong = txtTenPhong.getText().trim();
        String soGheStr = txtSoGhe.getText().trim();
        String loaiPhong = txtLoaiPhong.getText().trim();

        // Nếu người dùng chưa nhập đủ dữ liệu
        if (tenPhong.isEmpty() || soGheStr.isEmpty() || loaiPhong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin để sửa!", Alert.AlertType.WARNING);
            return;
        }

        // 🔹 Nếu người dùng cố gắng thay đổi mã phòng
        if (!maPhong.equals(originalMaphong)) {
            showAlert("Không thể thay đổi mã phòng", 
                      "Mã phòng là định danh duy nhất và không thể chỉnh sửa.\nHệ thống sẽ giữ nguyên mã cũ.", 
                      Alert.AlertType.WARNING);

            // Khôi phục lại mã cũ, nhưng vẫn giữ các dữ liệu người dùng đang sửa
            txtMaPhong.setText(originalMaphong);
            return;
        }

        // So sánh xem có thay đổi gì không
        boolean khongThayDoi =
            tenPhong.equals(originalTenphong) &&
            loaiPhong.equals(originalLoaiphong) &&
            Integer.parseInt(soGheStr) == originalSoghe;

        if (khongThayDoi) {
            showAlert("Không có thay đổi", "Bạn chưa thay đổi thông tin nào để cập nhật.", Alert.AlertType.INFORMATION);
            return;
        }

        // 🔹 Xác nhận sửa
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận sửa thông tin");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn cập nhật thông tin phòng chiếu này không?");
        ButtonType dongY = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType huy = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == dongY) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE phongchieu SET tenphong=?, soghe=?, loaiphong=? WHERE maphong=?")) {

                    ps.setString(1, tenPhong);
                    ps.setInt(2, Integer.parseInt(soGheStr));
                    ps.setString(3, loaiPhong);
                    ps.setString(4, maPhong);
                    ps.executeUpdate();

                    taiDuLieu();  // Cập nhật lại bảng
                    clearFields();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Lỗi cập nhật", e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                // Nếu chọn “Không” → khôi phục dữ liệu gốc
                txtMaPhong.setText(originalMaphong);
                txtTenPhong.setText(originalTenphong);
                txtSoGhe.setText(String.valueOf(originalSoghe));
                txtLoaiPhong.setText(originalLoaiphong);
            }
        });
    }


    // ---------------- XÓA ----------------
    @FXML
    public void xoaPhong() {
        String maPhong = txtMaPhong.getText().trim();

        if (maPhong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn phòng chiếu cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa phòng");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa phòng chiếu có mã '" + maPhong + "' không?");
        ButtonType dongY = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType huy = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == dongY) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM phongchieu WHERE maphong=?")) {

                    ps.setString(1, maPhong);
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        taiDuLieu();
                        clearFields();
                    } else {
                        showAlert("Không tìm thấy", "Không có phòng chiếu có mã '" + maPhong + "'.", Alert.AlertType.WARNING);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Lỗi xóa phòng", e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                clearFields(); // Không xóa DB → chỉ làm trắng form
            }
        });
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
