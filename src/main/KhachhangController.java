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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.stage.StageStyle;


public class KhachhangController {
    

    @FXML private TextField tfMaKH, tfTenKH, tfSDT, tfEmail, tfTimKiem;
    @FXML private TableView<khachhang> tableKH;
    @FXML private TableColumn<khachhang, String> colMaKH, colTenKH, colSDT, colEmail;
    @FXML private TableColumn<khachhang, Integer> colTongVe;

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

    // ⭐ CỘT TỔNG VÉ
    colTongVe.setCellValueFactory(c ->
            new javafx.beans.property.SimpleIntegerProperty(c.getValue().getTongVe()).asObject()
    );

    tableKH.setOnMouseClicked(event -> {
        khachhang selected = tableKH.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tfMaKH.setText(selected.getMaKhachHang());
            tfTenKH.setText(selected.getTenKhachHang());
            tfSDT.setText(selected.getSdt());
            tfEmail.setText(selected.getEmail());
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
            khachhang kh = new khachhang(
                    rs.getString("makhachhang"),
                    rs.getString("tenkhachhang"),
                    rs.getString("sdt"),
                    rs.getString("email")
            );

            // ⭐ LẤY TỔNG VÉ THẬT TỪ BẢNG VE
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) AS tongve FROM ve WHERE khachhang_makhachhang = ?")) {

                ps.setString(1, kh.getMaKhachHang());
                ResultSet rs2 = ps.executeQuery();
                if (rs2.next()) {
                    kh.setTongVe(rs2.getInt("tongve"));
                }
            }

            dsKH.add(kh);
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
                    showAlert("Thành công", "Đã thêm khách hàng thành công!", Alert.AlertType.INFORMATION);

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
                    showAlert("Thành công", "Thông tin khách hàng đã được cập nhật!", Alert.AlertType.INFORMATION);

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
                    showAlert("Thành công", "Đã xóa khách hàng thành công!", Alert.AlertType.INFORMATION);

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

public void timKiemNangCao(String ma, String ten, String sdt, String email) {

    ObservableList<khachhang> ketQua = FXCollections.observableArrayList();

    for (khachhang kh : dsKH) {
        boolean ok = true;

        if (!ma.isEmpty() && !kh.getMaKhachHang().toLowerCase().contains(ma.toLowerCase()))
            ok = false;

        if (!ten.isEmpty() && !kh.getTenKhachHang().toLowerCase().contains(ten.toLowerCase()))
            ok = false;

        if (!sdt.isEmpty() && !kh.getSdt().toLowerCase().contains(sdt.toLowerCase()))
            ok = false;

        if (!email.isEmpty() && !kh.getEmail().toLowerCase().contains(email.toLowerCase()))
            ok = false;

        if (ok) ketQua.add(kh);
    }

    tableKH.setItems(ketQua);
}

@FXML
private void xuatExcel() {
    try {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất Excel - Khách hàng");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return;

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("KhachHang");

        // ===== TẠO HEADER =====
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Mã khách hàng");
        header.createCell(1).setCellValue("Tên khách hàng");
        header.createCell(2).setCellValue("Số điện thoại");
        header.createCell(3).setCellValue("Email");

        // ===== GHI DỮ LIỆU =====
        int rowIndex = 1;
        for (khachhang kh : tableKH.getItems()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(kh.getMaKhachHang());
            row.createCell(1).setCellValue(kh.getTenKhachHang());
            row.createCell(2).setCellValue(kh.getSdt());
            row.createCell(3).setCellValue(kh.getEmail());
        }

        // ===== LƯU FILE =====
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);
        out.close();
        wb.close();

        showAlert("Thành công", "Xuất Excel thành công!", Alert.AlertType.INFORMATION);

    } catch (Exception e) {
        showAlert("Lỗi", "Không thể xuất Excel: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}


//tìm kiếm nâng cao
 @FXML
private void moTimKiemPopup() {
    try {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/giaodien/TimKiemKhachHang.fxml")
        );
        Parent root = loader.load();

        TimKiemKhachHangController popup = loader.getController();
        popup.setMainController(this);

        Stage stage = new Stage();

        // ⭐ Giúp bỏ màu nền mặc định của Stage
        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(root);

        // ⭐ Giúp bỏ nền trắng của Scene
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}