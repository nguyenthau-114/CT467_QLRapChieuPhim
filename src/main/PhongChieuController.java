package main;

import dulieu.PhongChieu;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ketnoi_truyxuat.DBConnection;
import java.sql.*;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Controller: Quản lý Phòng chiếu
 * - Dùng TRIGGER trong DB để tự sinh mã & chặn xóa
 * - Controller chỉ hiển thị thông báo dựa trên lỗi từ DB (SIGNAL 45000 / FK 1451 / trùng mã 1062)
 */
public class PhongChieuController {

    @FXML private TextField txtMaPhong, txtTenPhong, txtSoGhe, txtLoaiPhong;
    @FXML private TableView<PhongChieu> tablePhong;
    @FXML private TableColumn<PhongChieu, String> colMaPhong, colTenPhong, colLoaiPhong;
    @FXML private TableColumn<PhongChieu, Integer> colSoGhe;

    private final ObservableList<PhongChieu> dsPhong = FXCollections.observableArrayList();

    // Lưu dữ liệu gốc khi chọn dòng (để so sánh & khóa mã)
    private String originalMaphong   = "";
    private String originalTenphong  = "";
    private int    originalSoghe     = 0;
    private String originalLoaiphong = "";

    // ================== KHỞI TẠO ==================
    @FXML
    public void initialize() {
        // Cột -> getter của model PhongChieu
        colMaPhong.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaphong()));
        colTenPhong.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenphong()));
        colSoGhe.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSoghe()).asObject());
        colLoaiPhong.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLoaiphong()));

        // Tự co giãn cột
        tablePhong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Click chọn -> đổ ra form + lưu bản gốc + khóa mã (không cho sửa khi đang edit)
        tablePhong.setOnMouseClicked(event -> {
            PhongChieu selected = tablePhong.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtMaPhong.setText(selected.getMaphong());
                txtTenPhong.setText(selected.getTenphong());
                txtSoGhe.setText(String.valueOf(selected.getSoghe()));
                txtLoaiPhong.setText(selected.getLoaiphong());

                originalMaphong   = selected.getMaphong();
                originalTenphong  = selected.getTenphong();
                originalSoghe     = selected.getSoghe();
                originalLoaiphong = selected.getLoaiphong();

                txtMaPhong.setDisable(true); // khóa không cho sửa mã khi đang edit
            }
        });

        // Tải dữ liệu ban đầ
    }

    // ================== TẢI DỮ LIỆU ==================
    @FXML
    public void taiDuLieu() {
        dsPhong.clear();
        String sql = "SELECT maphong, tenphong, soghe, loaiphong FROM phongchieu ORDER BY maphong ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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
            showAlert("Lỗi tải dữ liệu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ================== THÊM (để trống mã -> trigger tự sinh) ==================
    @FXML
    public void themPhong() {
        String maPhongNhap = txtMaPhong.getText().trim(); // có thể để trống để trigger tự sinh
        String tenPhong    = txtTenPhong.getText().trim();
        String soGheStr    = txtSoGhe.getText().trim();
        String loaiPhong   = txtLoaiPhong.getText().trim();

        if (tenPhong.isEmpty() || soGheStr.isEmpty() || loaiPhong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập Tên phòng, Số ghế và Loại phòng!", AlertType.WARNING);
            return;
        }

        int soGhe;
        try {
            soGhe = Integer.parseInt(soGheStr);
            if (soGhe <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert("Số ghế không hợp lệ", "Số ghế phải là số nguyên dương.", AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận thêm phòng");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn thêm phòng chiếu này không?");
        ButtonType dongY = new ButtonType("Có", ButtonData.OK_DONE);
        ButtonType huy   = new ButtonType("Không", ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(res -> {
            if (res == dongY) {
                try (Connection conn = DBConnection.getConnection()) {

                    // Nếu người dùng để trống Mã phòng -> KHÔNG liệt kê cột maphong, để trigger BEFORE INSERT tự gán
                    if (maPhongNhap.isEmpty()) {
                        String sql = "INSERT INTO phongchieu (tenphong, soghe, loaiphong) VALUES (?, ?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, tenPhong);
                            ps.setInt(2, soGhe);
                            ps.setString(3, loaiPhong);
                            ps.executeUpdate();
                        }
                    } else {
                        // Người dùng tự nhập mã -> gửi lên DB (trigger vẫn có thể kiểm tra nếu bạn muốn)
                        String sql = "INSERT INTO phongchieu (maphong, tenphong, soghe, loaiphong) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, maPhongNhap);
                            ps.setString(2, tenPhong);
                            ps.setInt(3, soGhe);
                            ps.setString(4, loaiPhong);
                            ps.executeUpdate();
                        }
                    }

                    taiDuLieu();
                    clearFields();
                    showAlert("Thành công", "Đã thêm phòng mới!" , AlertType.INFORMATION);

                } catch (SQLException e) {
                    e.printStackTrace();
                    String sqlState = e.getSQLState();
                    int err = e.getErrorCode();

                    if ("45000".equals(sqlState)) {
                        // Thông điệp do trigger SIGNAL cung cấp (ví dụ: validate dữ liệu)
                        showAlert("Không thể thêm phòng", e.getMessage(), AlertType.WARNING);
                        return;
                    }
                    if (err == 1062) {
                        showAlert("Trùng mã phòng", "Mã phòng đã tồn tại. Vui lòng nhập mã khác hoặc để trống để DB tự sinh.", AlertType.WARNING);
                        return;
                    }
                    showAlert("Lỗi thêm phòng", e.getMessage(), AlertType.ERROR);
                }
            } else {
                clearFields();
            }
        });
    }

    // ================== SỬA ==================
    @FXML
    public void suaPhong() {
        String maPhong = txtMaPhong.getText().trim();
        String tenPhong = txtTenPhong.getText().trim();
        String soGheStr = txtSoGhe.getText().trim();
        String loaiPhong = txtLoaiPhong.getText().trim();

        if (maPhong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn phòng chiếu cần sửa!", AlertType.WARNING);
            return;
        }
        if (tenPhong.isEmpty() || soGheStr.isEmpty() || loaiPhong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin để sửa!", AlertType.WARNING);
            return;
        }

        int soGhe;
        try {
            soGhe = Integer.parseInt(soGheStr);
            if (soGhe <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert("Số ghế không hợp lệ", "Số ghế phải là số nguyên dương.", AlertType.WARNING);
            return;
        }

        // Không cho đổi mã phòng (đã khóa input khi chọn dòng)
        if (!maPhong.equals(originalMaphong) && !originalMaphong.isEmpty()) {
            showAlert("Không thể thay đổi mã phòng",
                      "Mã phòng là định danh duy nhất và không thể chỉnh sửa.\nHệ thống sẽ giữ nguyên mã cũ.",
                      AlertType.WARNING);
            txtMaPhong.setText(originalMaphong);
            return;
        }

        boolean khongThayDoi =
                tenPhong.equals(originalTenphong) &&
                loaiPhong.equals(originalLoaiphong) &&
                soGhe == originalSoghe;

        if (khongThayDoi) {
            showAlert("Không có thay đổi", "Bạn chưa thay đổi thông tin nào để cập nhật.", AlertType.INFORMATION);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận sửa thông tin");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn cập nhật thông tin phòng chiếu này không?");
        ButtonType dongY = new ButtonType("Có", ButtonData.OK_DONE);
        ButtonType huy   = new ButtonType("Không", ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(res -> {
            if (res == dongY) {
                String sql = "UPDATE phongchieu SET tenphong=?, soghe=?, loaiphong=? WHERE maphong=?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, tenPhong);
                    ps.setInt(2, soGhe);
                    ps.setString(3, loaiPhong);
                    ps.setString(4, maPhong);
                    ps.executeUpdate();

                    taiDuLieu();
                    clearFields();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Lỗi cập nhật", e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    // ================== XÓA (hiển thị thông điệp từ trigger/FK) ==================
    @FXML
    public void xoaPhong() {
        String maPhong = txtMaPhong.getText().trim();
        if (maPhong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn phòng chiếu cần xóa!", AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa phòng");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa phòng chiếu có mã '" + maPhong + "' không?");
        ButtonType dongY = new ButtonType("Có", ButtonData.OK_DONE);
        ButtonType huy   = new ButtonType("Không", ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(dongY, huy);

        confirm.showAndWait().ifPresent(res -> {
            if (res == dongY) {
                String sql = "DELETE FROM phongchieu WHERE maphong = ?";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, maPhong);
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        taiDuLieu();
                        clearFields();
                    } else {
                        showAlert("Không tìm thấy",
                                "Không có phòng chiếu có mã '" + maPhong + "'.",
                                AlertType.WARNING);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    String sqlState = e.getSQLState();
                    int err = e.getErrorCode();

                    // Bị chặn bởi trigger SIGNAL 45000 (trg_before_delete_phong_check_suatchieu)
                    if ("45000".equals(sqlState)) {
                        showAlert("Không thể xóa phòng", e.getMessage(), AlertType.WARNING);
                        return;
                    }
                    // Bị chặn bởi FK RESTRICT
                    if (err == 1451) {
                        showAlert("Không thể xóa phòng",
                                "Phòng đang bị ràng buộc bởi bảng suất chiếu nên không thể xóa.",
                                AlertType.WARNING);
                        return;
                    }

                    showAlert("Lỗi xóa phòng", e.getMessage(), AlertType.ERROR);
                }
            }
        });
    }

    // ================== TIỆN ÍCH ==================
    private void clearFields() {
        txtMaPhong.clear();
        txtTenPhong.clear();
        txtSoGhe.clear();
        txtLoaiPhong.clear();

        // mở lại mã để phục vụ thêm mới
        txtMaPhong.setDisable(false);

        // reset bản gốc
        originalMaphong = "";
        originalTenphong = "";
        originalSoghe = 0;
        originalLoaiphong = "";
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //tim kiem nang cao
    
    @FXML
    private void moTimKiemPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/giaodien/TimKiemPhong.fxml"));
            Parent root = loader.load();

            TimKiemPhongController popup = loader.getController();
            popup.setMainController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tìm kiếm phòng chiếu");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void xuatExcel() {
        try {
            // Hộp thoại chọn nơi lưu file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất danh sách phòng chiếu ra Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );

            File file = fileChooser.showSaveDialog(tablePhong.getScene().getWindow());
            if (file == null) return;   // người dùng bấm Cancel

            // Tạo workbook + sheet
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("PhongChieu");

            // ====== Dòng tiêu đề ======
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Mã phòng");
            header.createCell(1).setCellValue("Tên phòng");
            header.createCell(2).setCellValue("Số ghế");
            header.createCell(3).setCellValue("Loại phòng");

            // ====== Dữ liệu ======
            int rowIndex = 1;
            for (PhongChieu p : tablePhong.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(p.getMaphong());
                row.createCell(1).setCellValue(p.getTenphong());
                row.createCell(2).setCellValue(p.getSoghe());
                row.createCell(3).setCellValue(p.getLoaiphong());
            }

            // Auto size cột cho đẹp
            for (int i = 0; i <= 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file
            try (FileOutputStream out = new FileOutputStream(file)) {
                wb.write(out);
            }
            wb.close();

            showAlert("Thành công",
                      "Xuất Excel danh sách phòng chiếu thành công!",
                      AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi xuất Excel",
                      "Không thể xuất Excel: " + e.getMessage(),
                      AlertType.ERROR);
        }
    }

    
    public void timKiemNangCao(String ma, String ten, String soGheStr, String loai) {

        ObservableList<PhongChieu> ketQua = FXCollections.observableArrayList();

        for (PhongChieu p : dsPhong) {
            boolean ok = true;

            if (!ma.isEmpty() && !p.getMaphong().toLowerCase().contains(ma.toLowerCase()))
                ok = false;

            if (!ten.isEmpty() && !p.getTenphong().toLowerCase().contains(ten.toLowerCase()))
                ok = false;

            if (!loai.isEmpty() && !p.getLoaiphong().toLowerCase().contains(loai.toLowerCase()))
                ok = false;

            // số ghế >= nhập
            if (!soGheStr.isEmpty()) {
                try {
                    int minGhe = Integer.parseInt(soGheStr);
                    if (p.getSoghe() < minGhe) ok = false;
                } catch (NumberFormatException e) {
                    // Nếu người dùng nhập chữ → bỏ qua điều kiện này
                }
            }

            if (ok) ketQua.add(p);
        }

        tablePhong.setItems(ketQua);
    }


}
