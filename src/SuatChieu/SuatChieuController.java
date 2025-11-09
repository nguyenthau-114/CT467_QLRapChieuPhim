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

public class SuatChieuController {

    // ====== KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN ======
    @FXML private TextField txtMaSuatChieu, txtGioChieu, txtGiaVe, txtMaPhim, txtMaPhong, txtTimKiem;
    @FXML private DatePicker dpNgayChieu;
    @FXML private TableView<SuatChieu> tableSuatChieu;
    @FXML private TableColumn<SuatChieu, String> colMaSuatChieu, colMaPhim, colMaPhong;
    @FXML private TableColumn<SuatChieu, Date> colNgayChieu;
    @FXML private TableColumn<SuatChieu, Time> colGioChieu;
    @FXML private TableColumn<SuatChieu, Float> colGiaVe;
    @FXML private Button btnDangXuat;

    private ObservableList<SuatChieu> dsSuatChieu = FXCollections.observableArrayList();

    // Biến lưu dữ liệu gốc (phục vụ kiểm tra sửa)
    private String originalMaSuatChieu = "";
    private Date originalNgayChieu;
    private Time originalGioChieu;
    private float originalGiaVe = 0;
    private String originalMaPhim = "";
    private String originalMaPhong = "";

    // ====== KHỞI TẠO BAN ĐẦU ======
    @FXML
    public void initialize() {
        colMaSuatChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMasuatchieu()));
        colNgayChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNgaychieu()));
        colGioChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiochieu()));
        colGiaVe.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiave()));
        colMaPhim.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphim()));
        colMaPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphong()));

        // Khi chọn dòng trong bảng
        tableSuatChieu.setOnMouseClicked(event -> {
            SuatChieu sc = tableSuatChieu.getSelectionModel().getSelectedItem();
            if (sc != null) {
                txtMaSuatChieu.setText(sc.getMasuatchieu());
                dpNgayChieu.setValue(sc.getNgaychieu().toLocalDate());
                txtGioChieu.setText(sc.getGiochieu().toString());
                txtGiaVe.setText(String.valueOf(sc.getGiave()));
                txtMaPhim.setText(sc.getMaphim());
                txtMaPhong.setText(sc.getMaphong());

                // Lưu dữ liệu gốc
                originalMaSuatChieu = sc.getMasuatchieu();
                originalNgayChieu = sc.getNgaychieu();
                originalGioChieu = sc.getGiochieu();
                originalGiaVe = sc.getGiave();
                originalMaPhim = sc.getMaphim();
                originalMaPhong = sc.getMaphong();
            }
        });

        taiLaiDuLieu();
        // ✅ Giúp bảng tự co giãn đầy vùng hiển thị khi phóng to/thu nhỏ cửa sổ
        tableSuatChieu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Lắng nghe thay đổi kích thước cửa sổ cha (stage)
        tableSuatChieu.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) {
                        Stage stage = (Stage) newWin;

                        // Lắng nghe thay đổi chiều cao và chiều rộng của cửa sổ
                        stage.widthProperty().addListener((o, oldW, newW) -> {
                            tableSuatChieu.setPrefWidth(newW.doubleValue() - 250); // chừa khoảng sidebar
                        });
                        stage.heightProperty().addListener((o, oldH, newH) -> {
                            tableSuatChieu.setPrefHeight(newH.doubleValue() - 250); // chừa khoảng header + form
                        });
                    }
                });
            }
        });

    }

    // ====== TẢI DỮ LIỆU ======
    @FXML
    public void taiLaiDuLieu() {
        dsSuatChieu.clear();
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                showAlert("Lỗi kết nối", "Không thể kết nối MySQL. Vui lòng kiểm tra DBConnection.", AlertType.ERROR);
                return;
            }

            String sql = "SELECT masuatchieu, ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong " +
                         "FROM suatchieu ORDER BY CAST(SUBSTRING(masuatchieu, 3) AS UNSIGNED)";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            int count = 0;
            while (rs.next()) {
                dsSuatChieu.add(new SuatChieu(
                        rs.getString("masuatchieu"),
                        rs.getDate("ngaychieu"),
                        rs.getTime("giochieu"),
                        rs.getFloat("giave"),
                        rs.getString("phim_maphim"),
                        rs.getString("phongchieu_maphong")
                ));
                count++;
            }

            tableSuatChieu.setItems(dsSuatChieu);
            System.out.println("✅ Đã tải " + count + " suất chiếu từ CSDL.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ====== THÊM SUẤT CHIẾU ======
    @FXML
    public void themSuatChieu() {
        String ma = txtMaSuatChieu.getText().trim();
        String gio = txtGioChieu.getText().trim();
        String gia = txtGiaVe.getText().trim();
        String phim = txtMaPhim.getText().trim();
        String phong = txtMaPhong.getText().trim();

        if (ma.isEmpty() || dpNgayChieu.getValue() == null || gio.isEmpty() || gia.isEmpty() || phim.isEmpty() || phong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION, "Xác nhận thêm suất chiếu mới?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO suatchieu (masuatchieu, ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong) VALUES (?, ?, ?, ?, ?, ?)")) {

                    ps.setString(1, ma);
                    ps.setDate(2, Date.valueOf(dpNgayChieu.getValue()));
                    ps.setTime(3, Time.valueOf(gio));
                    ps.setFloat(4, Float.parseFloat(gia));
                    ps.setString(5, phim);
                    ps.setString(6, phong);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        taiLaiDuLieu();
                        showAlert("Thành công", "Đã thêm suất chiếu mới!", AlertType.INFORMATION);
                        clearFields(); // ✅ chỉ xóa sau khi thêm thành công
                    }

                } catch (SQLException e) {
                    showAlert("Lỗi thêm suất chiếu", e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    // ====== SỬA SUẤT CHIẾU ======
    @FXML
    public void suaSuatChieu() {
        if (txtMaSuatChieu.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn suất chiếu cần sửa!", AlertType.WARNING);
            return;
        }

        String ma = txtMaSuatChieu.getText().trim();
        String gio = txtGioChieu.getText().trim();
        String gia = txtGiaVe.getText().trim();
        String phim = txtMaPhim.getText().trim();
        String phong = txtMaPhong.getText().trim();

        if (dpNgayChieu.getValue() == null || gio.isEmpty() || gia.isEmpty() || phim.isEmpty() || phong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin!", AlertType.WARNING);
            return;
        }

        if (!ma.equals(originalMaSuatChieu)) {
            showAlert("Không thể đổi mã suất chiếu", "Mã suất chiếu là định danh duy nhất, hệ thống sẽ khôi phục lại mã cũ.", AlertType.WARNING);
            txtMaSuatChieu.setText(originalMaSuatChieu);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION, "Xác nhận cập nhật suất chiếu?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
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
                        clearFields(); // ✅ xóa sau khi cập nhật thành công
                    }

                } catch (SQLException e) {
                    showAlert("Lỗi cập nhật", e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    // ====== XÓA SUẤT CHIẾU ======
    @FXML
    public void xoaSuatChieu() {
        String ma = txtMaSuatChieu.getText().trim();
        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn suất chiếu cần xóa!", AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION, "Bạn có chắc muốn xóa suất chiếu " + ma + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
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

    // ====== TÌM KIẾM ======
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

    // ====== ĐĂNG XUẤT ======
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

    // ====== TIỆN ÍCH ======
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
}
