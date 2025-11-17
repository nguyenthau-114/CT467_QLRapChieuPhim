package main;

import dulieu.SuatChieu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import ketnoi_truyxuat.DBConnection;
import javafx.stage.Modality;
import java.sql.*;
import java.time.LocalDate;
import javafx.scene.Parent;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import javafx.stage.FileChooser;
import java.io.File;

public class SuatChieuController {

    @FXML private TextField txtMaSuatChieu, txtGioChieu, txtGiaVe, txtMaPhim, txtMaPhong;
    @FXML private DatePicker dpNgayChieu;

    @FXML private TableView<SuatChieu> tableSuatChieu;
    @FXML private TableColumn<SuatChieu, String> colMaSuatChieu, colMaPhim, colMaPhong;
    @FXML private TableColumn<SuatChieu, Date> colNgayChieu;
    @FXML private TableColumn<SuatChieu, Time> colGioChieu;
    @FXML private TableColumn<SuatChieu, Float> colGiaVe;

    private ObservableList<SuatChieu> dsSuatChieu = FXCollections.observableArrayList();
        // Lưu bản gốc để kiểm tra sửa đổi
    private String originalMaSuatChieu = "", originalMaPhim = "", originalMaPhong = "";
    private Date originalNgayChieu = null;
    private Time originalGioChieu = null;
    private float originalGiaVe = 0f;



    // ======================== KHỞI TẠO ========================
    @FXML
    public void initialize() {

        colMaSuatChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMasuatchieu()));
        colNgayChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNgaychieu()));
        colGioChieu.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiochieu()));
        colGiaVe.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getGiave()));
        colMaPhim.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphim()));
        colMaPhong.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaphong()));

        tableSuatChieu.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        tableSuatChieu.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, sc) -> {
            if (sc != null) {
                txtMaSuatChieu.setText(sc.getMasuatchieu());
                dpNgayChieu.setValue(sc.getNgaychieu().toLocalDate());
                txtGioChieu.setText(sc.getGiochieu().toString());
                txtGiaVe.setText(String.valueOf(sc.getGiave()));
                txtMaPhim.setText(sc.getMaphim());
                txtMaPhong.setText(sc.getMaphong());
                
                originalMaSuatChieu = sc.getMasuatchieu();
                originalNgayChieu   = sc.getNgaychieu();
                originalGioChieu    = sc.getGiochieu();
                originalGiaVe       = sc.getGiave();
                originalMaPhim      = sc.getMaphim();
                originalMaPhong     = sc.getMaphong();
            }
        });
    }

    // ===================== TẢI DỮ LIỆU =====================
    @FXML
    public void taiLaiDuLieu() {
        dsSuatChieu.clear();
        clearFields();
        tableSuatChieu.getSelectionModel().clearSelection();

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM suatchieu";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                dsSuatChieu.add(new SuatChieu(
                        rs.getString("masuatchieu"),
                        rs.getDate("ngaychieu"),
                        rs.getTime("giochieu"),
                        rs.getFloat("giave"),
                        rs.getString("phim_maphim"),
                        rs.getString("phongchieu_maphong")
                ));
            }

            tableSuatChieu.setItems(dsSuatChieu);
        } catch (SQLException e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== THÊM SUẤT CHIẾU =====================
    @FXML
    public void themSuatChieu() {

        String gio = txtGioChieu.getText().trim();
        String gia = txtGiaVe.getText().trim();
        String phim = txtMaPhim.getText().trim();
        String phong = txtMaPhong.getText().trim();
        LocalDate ngay = dpNgayChieu.getValue();

        if (ngay == null || gio.isEmpty() || gia.isEmpty() || phim.isEmpty() || phong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ!", AlertType.WARNING);
            return;
        }

        Time gioNhap;
        try {
            gioNhap = Time.valueOf(gio.length() == 5 ? gio + ":00" : gio);
        } catch (Exception ex) {
            showAlert("Lỗi giờ chiếu", "Giờ phải dạng HH:MM hoặc HH:MM:SS", AlertType.ERROR);
            return;
        }

        // ⭐ KIỂM TRA GIỜ CHIẾU (giống Trigger SQL)
        Time gioMin = Time.valueOf("08:00:00");
        Time gioMax = Time.valueOf("23:30:00");

        if (gioNhap.before(gioMin) || gioNhap.after(gioMax)) {
            showAlert("Giờ chiếu không hợp lệ!", "Chỉ cho phép 08:00 - 23:30", AlertType.ERROR);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String sqlCheck = "SELECT fn_kiemtra_lichtrung(?, ?, ?, ?) AS trung";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setString(1, phong);
            psCheck.setDate(2, Date.valueOf(ngay));
            psCheck.setTime(3, gioNhap);
            psCheck.setString(4, phim);

            ResultSet rs = psCheck.executeQuery();
            rs.next();
            if (rs.getInt("trung") == 1) {
                showAlert("⛔ Lịch trùng", "Phòng này đã có suất chiếu!", AlertType.WARNING);
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO suatchieu (ngaychieu, giochieu, giave, phim_maphim, phongchieu_maphong) VALUES (?, ?, ?, ?, ?)"
            );
            ps.setDate(1, Date.valueOf(ngay));
            ps.setTime(2, gioNhap);
            ps.setFloat(3, Float.parseFloat(gia));
            ps.setString(4, phim);
            ps.setString(5, phong);

            ps.executeUpdate();

            taiLaiDuLieu();
            clearFields();
            showAlert("🎉 Thành công", "Thêm suất chiếu thành công!", AlertType.INFORMATION);

        } catch (SQLException e) {
            showAlert("Lỗi thêm suất chiếu", e.getMessage(), AlertType.ERROR);
        }
    }
    // ===================== SỬA =====================
    @FXML
    public void suaSuatChieu() {

        String ma = txtMaSuatChieu.getText().trim();
        String gio = txtGioChieu.getText().trim();
        String gia = txtGiaVe.getText().trim();
        String phim = txtMaPhim.getText().trim();
        String phong = txtMaPhong.getText().trim();
        LocalDate ngay = dpNgayChieu.getValue();

        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Chọn suất chiếu cần sửa!", AlertType.WARNING);
            return;
        }
        
        if (!ma.equals(originalMaSuatChieu)) {
            showAlert("Không thể sửa mã suất chiếu",
                      "Mã suất chiếu là định danh duy nhất, không thể thay đổi!",
                      AlertType.WARNING);
            txtMaSuatChieu.setText(originalMaSuatChieu);
            return;
        }
        
        if (ngay == null || gio.isEmpty() || gia.isEmpty() || phim.isEmpty() || phong.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ!", AlertType.WARNING);
            return;
        }

        Time gioNhap;
        try {
            gioNhap = Time.valueOf(gio.length() == 5 ? gio + ":00" : gio);
        } catch (Exception e) {
            showAlert("Lỗi giờ chiếu", "Giờ phải đúng định dạng!", AlertType.ERROR);
            return;
        }

        
        // ⭐ KIỂM TRA GIỜ CHIẾU (08:00 - 23:30)
        Time gioMin = Time.valueOf("08:00:00");
        Time gioMax = Time.valueOf("23:30:00");
        
        if (gioNhap.before(gioMin) || gioNhap.after(gioMax)) {
            showAlert("Giờ chiếu không hợp lệ!", "Chỉ cho phép 08:00 - 23:30", AlertType.ERROR);
            return;
        }
        float giaFloat;
        try {
            giaFloat = Float.parseFloat(gia);
        } catch (NumberFormatException ex) {
            showAlert("Lỗi giá vé", "Giá vé phải là số!", AlertType.ERROR);
            return;
        }
        boolean sameDate = (originalNgayChieu == null && ngay == null)
                       || (originalNgayChieu != null && originalNgayChieu.equals(Date.valueOf(ngay)));
        boolean sameTime = (originalGioChieu == null && gioNhap == null)
                           || (originalGioChieu != null && originalGioChieu.equals(gioNhap));

        boolean khongThayDoi =
                sameDate &&
                sameTime &&
                originalGiaVe == giaFloat &&
                originalMaPhim.equals(phim) &&
                originalMaPhong.equals(phong);

        if (khongThayDoi) {
            showAlert("Không có thay đổi",
                      "Bạn chưa thay đổi thông tin nào để cập nhật!",
                      AlertType.INFORMATION);
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE suatchieu SET ngaychieu=?, giochieu=?, giave=?, phim_maphim=?, phongchieu_maphong=? WHERE masuatchieu=?"
            );
            ps.setDate(1, Date.valueOf(ngay));
            ps.setTime(2, gioNhap);
            ps.setFloat(3, Float.parseFloat(gia));
            ps.setString(4, phim);
            ps.setString(5, phong);
            ps.setString(6, ma);
            ps.executeUpdate();
            taiLaiDuLieu();
            clearFields();
            showAlert("🎉 Thành công", "Đã sửa suất chiếu!", AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Lỗi sửa suất chiếu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== XÓA =====================
    @FXML
    public void xoaSuatChieu() {
        String ma = txtMaSuatChieu.getText().trim();
        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Chọn suất chiếu cần xóa!", AlertType.WARNING);
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM suatchieu WHERE masuatchieu=?");
            ps.setString(1, ma);
            ps.executeUpdate();
            taiLaiDuLieu();
            clearFields();
            showAlert("🗑️ Đã xóa", "Xóa suất chiếu thành công!", AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Lỗi xóa suất chiếu", e.getMessage(), AlertType.ERROR);
        }
    }
    // ===================== TÌM KIẾM NÂNG CAO =====================
    @FXML
    private void moTimKiemPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/giaodien/TimKiemNangCao.fxml"));
            Parent root = loader.load();
            TimKiemNangCaoController popupController = loader.getController();
            popupController.setMainController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tìm kiếm suất chiếu");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void timKiemNangCao(String ma, LocalDate ngay, String phim, String phong, String trangthai) {
        dsSuatChieu.clear();
        try (Connection conn = DBConnection.getConnection()) {
            CallableStatement cs = conn.prepareCall("{CALL sp_timkiem_suatchieu(?, ?, ?, ?)}");
            cs.setString(1, ma != null ? ma : "");
            cs.setDate(2, ngay != null ? Date.valueOf(ngay) : null);
            cs.setString(3, phim != null ? phim : "");
            cs.setString(4, phong != null ? phong : "");
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                dsSuatChieu.add(new SuatChieu(
                        rs.getString("masuatchieu"),
                        rs.getDate("ngaychieu"),
                        rs.getTime("giochieu"),
                        rs.getFloat("giave"),
                        rs.getString("phim_maphim"),
                        rs.getString("phongchieu_maphong")
                ));
            }
            tableSuatChieu.setItems(dsSuatChieu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== XUẤT EXCEL =====================
    @FXML
    private void xuatExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(null);
            if (file == null) return;
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("SuatChieu");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Mã suất chiếu");
            header.createCell(1).setCellValue("Ngày chiếu");
            header.createCell(2).setCellValue("Giờ chiếu");
            header.createCell(3).setCellValue("Giá vé");
            header.createCell(4).setCellValue("Mã phim");
            header.createCell(5).setCellValue("Mã phòng");
            int rowIndex = 1;
            for (SuatChieu sc : tableSuatChieu.getItems()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(sc.getMasuatchieu());
                row.createCell(1).setCellValue(sc.getNgaychieu().toString());
                row.createCell(2).setCellValue(sc.getGiochieu().toString());
                row.createCell(3).setCellValue(sc.getGiave());
                row.createCell(4).setCellValue(sc.getMaphim());
                row.createCell(5).setCellValue(sc.getMaphong());
            }
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
            wb.close();
            showAlert("Thành công", "Xuất Excel thành công!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể xuất Excel: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    // ===================== HỖ TRỢ =====================
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
