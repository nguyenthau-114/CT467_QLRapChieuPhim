package khachhang;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ketnoi_truyxuat.DBConnection;

import java.sql.*;

public class KhachhangController {

    @FXML private TextField txtMaKH, txtTenKH, txtSDT, txtEmail;
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

        // Khi click chọn 1 dòng → hiển thị lên TextField + lưu bản gốc
        tableKH.setOnMouseClicked(event -> {
            khachhang selected = tableKH.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtMaKH.setText(selected.getMaKhachHang());
                txtTenKH.setText(selected.getTenKhachHang());
                txtSDT.setText(selected.getSdt());
                txtEmail.setText(selected.getEmail());

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
            e.printStackTrace();
            showAlert("Lỗi tải dữ liệu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ---------------- THÊM ----------------
    @FXML
    public void onThem() {
        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (maKH.isEmpty() || tenKH.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận thêm khách hàng");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn thêm khách hàng này không?");
        ButtonType dongY = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType huy = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == dongY) {
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
                    e.printStackTrace();
                    showAlert("Lỗi thêm khách hàng", e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                clearFields();
            }
        });
    }

    // ---------------- SỬA ----------------
    @FXML
    public void onSua() {
        if (txtMaKH.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn khách hàng cần sửa!", Alert.AlertType.WARNING);
            return;
        }

        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();

        if (tenKH.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ dữ liệu!", Alert.AlertType.WARNING);
            return;
        }

        // Không cho sửa mã khách hàng
        if (!maKH.equals(originalMaKH)) {
            showAlert("Không thể thay đổi mã khách hàng",
                    "Mã khách hàng là định danh duy nhất, không thể chỉnh sửa.",
                    Alert.AlertType.WARNING);
            txtMaKH.setText(originalMaKH);
            return;
        }

        // Nếu không thay đổi gì
        boolean khongThayDoi =
                tenKH.equals(originalTenKH) &&
                sdt.equals(originalSDT) &&
                email.equals(originalEmail);

        if (khongThayDoi) {
            showAlert("Không có thay đổi", "Bạn chưa thay đổi thông tin nào để cập nhật.", Alert.AlertType.INFORMATION);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận sửa thông tin");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn cập nhật thông tin khách hàng này?");
        ButtonType dongY = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType huy = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == dongY) {
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
                    e.printStackTrace();
                    showAlert("Lỗi cập nhật", e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                txtMaKH.setText(originalMaKH);
                txtTenKH.setText(originalTenKH);
                txtSDT.setText(originalSDT);
                txtEmail.setText(originalEmail);
            }
        });
    }

    // ---------------- XÓA ----------------
    @FXML
    public void onXoa() {
        String maKH = txtMaKH.getText().trim();

        if (maKH.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn khách hàng cần xóa!", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa khách hàng");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa khách hàng có mã '" + maKH + "' không?");
        ButtonType dongY = new ButtonType("Có", ButtonBar.ButtonData.OK_DONE);
        ButtonType huy = new ButtonType("Không", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(response -> {
            if (response == dongY) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM khachhang WHERE makhachhang=?")) {

                    ps.setString(1, maKH);
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        onTaiDuLieu();
                        clearFields();
                    } else {
                        showAlert("Không tìm thấy", "Không có khách hàng có mã '" + maKH + "'.", Alert.AlertType.WARNING);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Lỗi xóa khách hàng", e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                clearFields();
            }
        });
    }

    // ---------------- HÀM TIỆN ÍCH ----------------
    private void clearFields() {
        txtMaKH.clear();
        txtTenKH.clear();
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

    // ---------------- ĐĂNG XUẤT ----------------
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
