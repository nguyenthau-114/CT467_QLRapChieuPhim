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
            showAlert("Thành công", "Đã thêm vé thành công!" + Alert.AlertType.INFORMATION);

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
            showAlert("Thành công", "Đã cập nhật vé thành công!" + Alert.AlertType.INFORMATION);

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
            showAlert("Thành công", "Đã xóa vé thành công!" + Alert.AlertType.INFORMATION);

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

        for (ve v : danhSachVe) {
            boolean ok = true;

            if (!maVe.isEmpty() && !v.getMave().toLowerCase().contains(maVe.toLowerCase()))
                ok = false;

            if (!trangThai.isEmpty() && !v.getTrangthai().toLowerCase().contains(trangThai.toLowerCase()))
                ok = false;

            if (!maSC.isEmpty() && !v.getSuatchieu_masuatchieu().toLowerCase().contains(maSC.toLowerCase()))
                ok = false;

            if (!maKH.isEmpty() && !v.getKhachhang_makhachhang().toLowerCase().contains(maKH.toLowerCase()))
                ok = false;

            if (!maGhe.isEmpty() && !v.getGhe_maghe().toLowerCase().contains(maGhe.toLowerCase()))
                ok = false;

            if (!ngayDat.isEmpty() && !v.getNgaydat().toString().equals(ngayDat))
                ok = false;

            if (ok) ketQua.add(v);
        }

        tableVe.setItems(ketQua);
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