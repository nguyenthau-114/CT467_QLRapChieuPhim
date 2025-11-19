package main;

import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import ketnoi_truyxuat.DBConnection;
import dulieu.ve;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

import javafx.stage.StageStyle;

public class Ve_truycapController {

    @FXML private TextField tfMaVe, tfGiaVe, tfTrangThai, tfMaSuatChieu, tfMaKhachHang, tfMaGhe, tfTimKiem;
    @FXML private DatePicker dpNgayDat;
    @FXML private TableView<ve> tableVe;
    @FXML private TableColumn<ve, String> colMaVe, colTrangThai, colMaSuatChieu, colMaKhachHang, colMaGhe;
    @FXML private TableColumn<ve, Date> colNgayDat;
    @FXML private TableColumn<ve, Double> colGiaVe;

    private ObservableList<ve> danhSachVe = FXCollections.observableArrayList();
    // ⭐ Lưu dữ liệu gốc khi chọn dòng
    private String originalMaVe = "";
    private Date   originalNgayDat = null;
    private double originalGiaVe = 0;
    private String originalTrangThai = "";
    private String originalMaSuatChieu = "";
    private String originalMaKhachHang = "";
    private String originalMaGhe = "";


    // ================= KHỞI TẠO =================
    @FXML
    public void initialize() {
        colMaVe.setCellValueFactory(cell -> cell.getValue().maveProperty());
        colNgayDat.setCellValueFactory(cell -> cell.getValue().ngaydatProperty());
        colGiaVe.setCellValueFactory(cell -> cell.getValue().giaveProperty().asObject());
        colTrangThai.setCellValueFactory(cell -> cell.getValue().trangthaiProperty());
        colMaSuatChieu.setCellValueFactory(cell -> cell.getValue().suatchieu_masuatchieuProperty());
        colMaKhachHang.setCellValueFactory(cell -> cell.getValue().khachhang_makhachhangProperty());
        colMaGhe.setCellValueFactory(cell -> cell.getValue().ghe_magheProperty());

        // Khi chọn 1 dòng thì đổ lên form
        tableVe.setOnMouseClicked(event -> {
            ve selected = tableVe.getSelectionModel().getSelectedItem();
            if (selected != null) {
                tfMaVe.setText(selected.getMave());
                dpNgayDat.setValue(selected.getNgaydat().toLocalDate());
                tfGiaVe.setText(String.valueOf(selected.getGiave()));
                tfTrangThai.setText(selected.getTrangthai());
                tfMaSuatChieu.setText(selected.getSuatchieu_masuatchieu());
                tfMaKhachHang.setText(selected.getKhachhang_makhachhang());
                tfMaGhe.setText(selected.getGhe_maghe());
                
                originalMaVe        = selected.getMave();
                originalNgayDat     = selected.getNgaydat();
                originalGiaVe       = selected.getGiave();
                originalTrangThai   = selected.getTrangthai();
                originalMaSuatChieu = selected.getSuatchieu_masuatchieu();
                originalMaKhachHang = selected.getKhachhang_makhachhang();
                originalMaGhe       = selected.getGhe_maghe();
            }
        });
        

        tableVe.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Tự chia đều độ rộng cột
        tableVe.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue() / tableVe.getColumns().size();
            tableVe.getColumns().forEach(col -> col.setPrefWidth(width));
        });
    }

    // ================= TẢI DỮ LIỆU =================
    @FXML
    private void taiDuLieu() {
        danhSachVe.clear();
        clearForm();
        tableVe.getSelectionModel().clearSelection();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ve")) {

            while (rs.next()) {
                ve v = new ve(
                        rs.getString("mave"),
                        rs.getDate("ngaydat"),
                        rs.getDouble("giave"),
                        rs.getString("trangthai"),
                        rs.getString("suatchieu_masuatchieu"),
                        rs.getString("khachhang_makhachhang"),
                        rs.getString("ghe_maghe")
                );
                danhSachVe.add(v);
            }
            tableVe.setItems(danhSachVe);
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải dữ liệu vé:\n" + e.getMessage());
        }
    }

    // ================= THÊM =================
    @FXML
    private void onThem() {
        if (tfMaVe.getText().isEmpty()) {
            showAlert("Thông báo", "Vui lòng nhập mã vé!");
            return;
        }

        String sql = "INSERT INTO ve (mave, ngaydat, giave, trangthai, suatchieu_masuatchieu, khachhang_makhachhang, ghe_maghe) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tfMaVe.getText());
            ps.setDate(2, Date.valueOf(dpNgayDat.getValue()));
            ps.setDouble(3, Double.parseDouble(tfGiaVe.getText()));
            ps.setString(4, tfTrangThai.getText());
            ps.setString(5, tfMaSuatChieu.getText());
            ps.setString(6, tfMaKhachHang.getText());
            ps.setString(7, tfMaGhe.getText());

            ps.executeUpdate();
            taiDuLieu();
            clearForm();
            showAlert("Thành công", "Đã thêm vé thành công!");

        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể thêm vé:\n" + e.getMessage());
        }
    }

    // ================= SỬA =================
    @FXML
    private void onSua() {
        ve selected = tableVe.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn vé cần sửa!");
            return;
        }

        // ⭐ KHÔNG CHO SỬA MÃ VÉ
        String maForm = tfMaVe.getText().trim();
        if (!maForm.equals(originalMaVe)) {
            showAlert("Không thể sửa mã vé",
                      "Mã vé là định danh duy nhất, không thể thay đổi");
            tfMaVe.setText(originalMaVe);  // trả lại mã cũ
            return;
        }

        // ⭐ LẤY GIÁ TRỊ TRÊN FORM ĐỂ SO SÁNH
        Date ngayForm;
        double giaForm;
        try {
            ngayForm = Date.valueOf(dpNgayDat.getValue());
            giaForm  = Double.parseDouble(tfGiaVe.getText().trim());
        } catch (Exception ex) {
            showAlert("Dữ liệu không hợp lệ",
                      "Ngày đặt hoặc Giá vé không hợp lệ!");
            return;
        }

        String trangForm = tfTrangThai.getText().trim();
        String maSC      = tfMaSuatChieu.getText().trim();
        String maKH      = tfMaKhachHang.getText().trim();
        String maGhe     = tfMaGhe.getText().trim();

        boolean sameDate = (originalNgayDat == null && ngayForm == null)
                           || (originalNgayDat != null && originalNgayDat.equals(ngayForm));

        boolean khongThayDoi =
                sameDate &&
                giaForm == originalGiaVe &&
                trangForm.equals(originalTrangThai) &&
                maSC.equals(originalMaSuatChieu) &&
                maKH.equals(originalMaKhachHang) &&
                maGhe.equals(originalMaGhe);

        if (khongThayDoi) {
            showAlert("Không có thay đổi",
                      "Bạn chưa thay đổi thông tin nào để cập nhật!");
            return;
        }

        // ⭐ ĐOẠN NÀY GIỮ NGUYÊN LOGIC CŨ
        String sql = "UPDATE ve SET ngaydat=?, giave=?, trangthai=?, suatchieu_masuatchieu=?, khachhang_makhachhang=?, ghe_maghe=? WHERE mave=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dpNgayDat.getValue()));
            ps.setDouble(2, Double.parseDouble(tfGiaVe.getText()));
            ps.setString(3, tfTrangThai.getText());
            ps.setString(4, tfMaSuatChieu.getText());
            ps.setString(5, tfMaKhachHang.getText());
            ps.setString(6, tfMaGhe.getText());
            ps.setString(7, tfMaVe.getText());

            ps.executeUpdate();
            taiDuLieu();
            clearForm();
            showAlert("Thành công", "Đã cập nhật vé thành công!");

        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể sửa vé:\n" + e.getMessage());
        }
    }

    // ================= XÓA =================
    @FXML
    private void onXoa() {
        ve selected = tableVe.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn vé cần xóa!");
            return;
        }

        String sql = "DELETE FROM ve WHERE mave=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, selected.getMave());
            ps.executeUpdate();
            taiDuLieu();
            clearForm();
            showAlert("Thành công", "Đã thêm xóa thành công!");


        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể xóa vé:\n" + e.getMessage());
        }
    }

    // ================= XUẤT EXCEL =================
    @FXML
    private void xuatExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất danh sách vé ra Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );

            File file = fileChooser.showSaveDialog(tableVe.getScene().getWindow());
            if (file == null) return;

            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Ve");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Mã vé");
            header.createCell(1).setCellValue("Ngày đặt");
            header.createCell(2).setCellValue("Giá vé");
            header.createCell(3).setCellValue("Trạng thái");
            header.createCell(4).setCellValue("Mã suất chiếu");
            header.createCell(5).setCellValue("Mã khách hàng");
            header.createCell(6).setCellValue("Mã ghế");

            int rowIndex = 1;
            for (ve v : tableVe.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(v.getMave());
                row.createCell(1).setCellValue(
                        v.getNgaydat() != null ? v.getNgaydat().toString() : ""
                );
                row.createCell(2).setCellValue(v.getGiave());
                row.createCell(3).setCellValue(v.getTrangthai());
                row.createCell(4).setCellValue(v.getSuatchieu_masuatchieu());
                row.createCell(5).setCellValue(v.getKhachhang_makhachhang());
                row.createCell(6).setCellValue(v.getGhe_maghe());
            }

            for (int i = 0; i <= 6; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                wb.write(out);
            }
            wb.close();

            showAlert("Thành công", "Xuất Excel danh sách vé thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi xuất Excel", "Không thể xuất Excel: " + e.getMessage());
        }
    }

    // ================= TÌM KIẾM NÂNG CAO =================
    public void timKiemNangCao(String maVe, String ngayDat, String trangThai,
                               String maSC, String maKH, String maGhe) {

        ObservableList<ve> ketQua = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {

            CallableStatement cs = conn.prepareCall("{CALL sp_timkiem_ve(?, ?, ?, ?, ?, ?)}");

            cs.setString(1, maVe != null ? maVe : "");

            if (ngayDat != null && !ngayDat.isEmpty())
                cs.setDate(2, Date.valueOf(ngayDat));
            else
                cs.setNull(2, Types.DATE);

            cs.setString(3, trangThai != null ? trangThai : "");
            cs.setString(4, maSC != null ? maSC : "");
            cs.setString(5, maKH != null ? maKH : "");
            cs.setString(6, maGhe != null ? maGhe : "");

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                ketQua.add(new ve(
                        rs.getString("mave"),
                        rs.getDate("ngaydat"),
                        rs.getDouble("giave"),
                        rs.getString("trangthai"),
                        rs.getString("suatchieu_masuatchieu"),
                        rs.getString("khachhang_makhachhang"),
                        rs.getString("ghe_maghe")
                ));
            }

            tableVe.setItems(ketQua);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= POPUP TÌM KIẾM =================
    @FXML
    private void moTimKiemPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/giaodien/TimKiemVe.fxml")
            );
            Parent root = loader.load();

            TimKiemVeController popup = loader.getController();
            popup.setMainController(this);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TIỆN ÍCH KHÁC =================
    private void clearForm() {
        tfMaVe.clear();
        tfGiaVe.clear();
        tfTrangThai.clear();
        tfMaSuatChieu.clear();
        tfMaKhachHang.clear();
        tfMaGhe.clear();
        dpNgayDat.setValue(null);
        
        originalMaVe = "";
        originalNgayDat = null;
        originalGiaVe = 0;
        originalTrangThai = "";
        originalMaSuatChieu = "";
        originalMaKhachHang = "";
        originalMaGhe = "";
    }

    @FXML
    private void dangXuat(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã đăng xuất khỏi hệ thống!");
        alert.showAndWait();

        ((Stage) ((javafx.scene.Node) event.getSource())
                .getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}