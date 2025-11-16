package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import ketnoi_truyxuat.DBConnection;

import java.sql.*;

import javafx.scene.layout.VBox;

import dulieu.HoaDon;   // ⭐ MODEL MỚI

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.FileChooser;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

import javafx.stage.StageStyle;

public class HoaDonController {

    // ===================== FXML ======================
    @FXML private TextField txtMaHD, txtSoLuong, txtTongTien, txtMaKH, txtMaCombo;
    @FXML private DatePicker dpNgayMua;

    @FXML private TableView<HoaDon> tableHD;
    @FXML private TableColumn<HoaDon,String> colMaHD, colMaKH, colMaCombo;
    @FXML private TableColumn<HoaDon,Integer> colSoLuong;
    @FXML private TableColumn<HoaDon,Double> colTongTien;
    @FXML private TableColumn<HoaDon,Date> colNgayMua;

    @FXML private TextField txtTimKiem;
    @FXML private Button btnDangXuat;

    private final ObservableList<HoaDon> dsHD = FXCollections.observableArrayList();

    // Lưu dữ liệu gốc
    private String originalMaHD = "", originalMaKH = "", originalMaCombo = "";
    private int originalSoLuong = 0;
    private double originalTongTien = 0;
    private Date originalNgayMua = null;

    // ===================== KHỞI TẠO ======================
    @FXML
    public void initialize() {

        colMaHD.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaHD()));
        colSoLuong.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getSoLuong()).asObject());
        colNgayMua.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getNgayMua()));
        colTongTien.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTongTien()).asObject());
        colMaKH.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaKH()));
        colMaCombo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaCombo()));
        

        tableHD.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Khi click chọn hàng → đổ lên form
        tableHD.setOnMouseClicked(event -> {
            HoaDon hd = tableHD.getSelectionModel().getSelectedItem();
            if (hd != null) {
                txtMaHD.setText(hd.getMaHD());
                txtSoLuong.setText(String.valueOf(hd.getSoLuong()));
                dpNgayMua.setValue(hd.getNgayMua().toLocalDate());
                txtTongTien.setText(String.valueOf(hd.getTongTien()));
                txtMaKH.setText(hd.getMaKH());
                txtMaCombo.setText(hd.getMaCombo());


                // Lưu bản gốc
                originalMaHD = hd.getMaHD();
                originalSoLuong = hd.getSoLuong();
                originalNgayMua = hd.getNgayMua();
                originalTongTien = hd.getTongTien();
                originalMaKH = hd.getMaKH();
                originalMaCombo = hd.getMaCombo();
  
            }
        });
    }

    // ===================== TẢI DỮ LIỆU ======================
    @FXML
    public void onTaiDuLieu() {
        dsHD.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM hoadon ORDER BY mahoadon ASC")) {

            while (rs.next()) {
                dsHD.add(new HoaDon(
                        rs.getString("mahoadon"),
                        rs.getInt("soluongcombo"),
                        rs.getDate("ngaymua"),
                        rs.getDouble("tongtien"),
                        rs.getString("khachhang_makhachhang"),
                        rs.getString("bapnuoc_macombo"),
                        rs.getString("Nhanvien_manhanvien")
                ));
            }

            tableHD.setItems(dsHD);
            System.out.println("Đã tải " + dsHD.size() + " hóa đơn.");

        } catch (SQLException e) {
            showAlert("Lỗi tải dữ liệu", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== THÊM ======================
    @FXML
    public void onThem() {
        if (txtMaHD.getText().isEmpty()
            || txtSoLuong.getText().isEmpty()
            || dpNgayMua.getValue() == null
            || txtTongTien.getText().isEmpty()
            || txtMaKH.getText().isEmpty()
            || txtMaCombo.getText().isEmpty()
            ) {

            showAlert("Thiếu thông tin", "Vui lòng nhập đầy đủ các trường!", AlertType.WARNING);
            return;
        }

        if (!showConfirmDialog("Xác nhận thêm hóa đơn", "Bạn có chắc muốn thêm không?")) {
            clearFields();
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO hoadon VALUES (?,?,?,?,?,?,?)")) {

            ps.setString(1, txtMaHD.getText());
            ps.setInt(2, Integer.parseInt(txtSoLuong.getText()));
            ps.setDate(3, Date.valueOf(dpNgayMua.getValue()));
            ps.setDouble(4, Double.parseDouble(txtTongTien.getText()));
            ps.setString(5, txtMaKH.getText());
            ps.setString(6, txtMaCombo.getText());
          

            ps.executeUpdate();

            showAlert("Thành công", "Đã thêm hóa đơn thành công!", AlertType.INFORMATION);
            onTaiDuLieu();
            clearFields();

        } catch (SQLException e) {
            showAlert("Lỗi thêm hóa đơn", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== SỬA ======================
    @FXML
    public void onSua() {
        if (txtMaHD.getText().isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn hóa đơn cần sửa!", AlertType.WARNING);
            return;
        }

        String ma = txtMaHD.getText().trim();

        if (!ma.equals(originalMaHD)) {
            showAlert("Không thể sửa mã", "Mã hóa đơn là định danh duy nhất!", AlertType.WARNING);
            txtMaHD.setText(originalMaHD);
            return;
        }

        if (!showConfirmDialog("Xác nhận", "Bạn muốn cập nhật hóa đơn này?")) {
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE hoadon SET soluongcombo=?, ngaymua=?, tongtien=?, khachhang_makhachhang=?, bapnuoc_macombo=?, Nhanvien_manhanvien=? WHERE mahoadon=?")) {

            ps.setInt(1, Integer.parseInt(txtSoLuong.getText()));
            ps.setDate(2, Date.valueOf(dpNgayMua.getValue()));
            ps.setDouble(3, Double.parseDouble(txtTongTien.getText()));
            ps.setString(4, txtMaKH.getText());
            ps.setString(5, txtMaCombo.getText());
      
            ps.setString(7, ma);

            ps.executeUpdate();
            showAlert("Thành công", "Đã cập nhật hóa đơn thành công!", AlertType.INFORMATION);

            onTaiDuLieu();
            clearFields();

        } catch (SQLException e) {
            showAlert("Lỗi cập nhật", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== XÓA ======================
    @FXML
    public void onXoa() {
        String ma = txtMaHD.getText().trim();

        if (ma.isEmpty()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn hóa đơn cần xóa!", AlertType.WARNING);
            return;
        }

        if (!showConfirmDialog("Xóa hóa đơn", "Bạn có chắc chắn muốn xóa hóa đơn này?"))
            return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM hoadon WHERE mahoadon=?")) {

            ps.setString(1, ma);
            ps.executeUpdate();

            showAlert("Thành công", "Đã xóa hóa đơn thành công!", AlertType.INFORMATION);

            onTaiDuLieu();
            clearFields();

        } catch (SQLException e) {
            showAlert("Lỗi xóa hóa đơn", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== EXPORT EXCEL ======================
    @FXML
    private void xuatExcel() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Xuất Excel hóa đơn");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));

            File file = chooser.showSaveDialog(tableHD.getScene().getWindow());
            if (file == null) return;

            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("HoaDon");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Mã HĐ");
            header.createCell(1).setCellValue("SL Combo");
            header.createCell(2).setCellValue("Ngày mua");
            header.createCell(3).setCellValue("Tổng tiền");
            header.createCell(4).setCellValue("Mã KH");
            header.createCell(5).setCellValue("Mã combo");
            header.createCell(6).setCellValue("Mã NV");

            int rowIndex = 1;
            for (HoaDon hd : tableHD.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(hd.getMaHD());
                row.createCell(1).setCellValue(hd.getSoLuong());
                row.createCell(2).setCellValue(hd.getNgayMua().toString());
                row.createCell(3).setCellValue(hd.getTongTien());
                row.createCell(4).setCellValue(hd.getMaKH());
                row.createCell(5).setCellValue(hd.getMaCombo());
                row.createCell(6).setCellValue(hd.getMaNV());
            }

            for (int i = 0; i < 7; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream out = new FileOutputStream(file)) {
                wb.write(out);
            }

            showAlert("Thành công", "Đã xuất Excel!", AlertType.INFORMATION);
            wb.close();

        } catch (Exception e) {
            showAlert("Lỗi xuất Excel", e.getMessage(), AlertType.ERROR);
        }
    }

    // ===================== TÌM KIẾM CƠ BẢN ======================
    @FXML
    public void onTimKiem() {
        String key = txtTimKiem.getText().trim().toLowerCase();
        if (key.isEmpty()) {
            tableHD.setItems(dsHD);
            return;
        }

        ObservableList<HoaDon> kq = FXCollections.observableArrayList();
        for (HoaDon hd : dsHD) {
            if (hd.getMaHD().toLowerCase().contains(key)
                    || hd.getMaKH().toLowerCase().contains(key)
                    || hd.getMaNV().toLowerCase().contains(key))
                kq.add(hd);
        }

        tableHD.setItems(kq);
    }

    // ===================== POPUP SEARCH ======================
    @FXML
    private void moTimKiemPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/giaodien/TimKiemHoaDon.fxml")
            );
            Parent root = loader.load();

            TimKiemHoaDonController popup = loader.getController();
            popup.setMainController(this);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== TÌM KIẾM NÂNG CAO ======================
    public void timKiemNangCao(String maHD, String ngay, String sl, String tong,
                               String maKH, String maCombo, String maNV) {

        ObservableList<HoaDon> kq = FXCollections.observableArrayList();

        for (HoaDon hd : dsHD) {
            boolean ok = true;

            if (!maHD.isEmpty() && !hd.getMaHD().toLowerCase().contains(maHD.toLowerCase()))
                ok = false;

            if (!ngay.isEmpty() && !hd.getNgayMua().toString().equals(ngay))
                ok = false;

            if (!sl.isEmpty() && hd.getSoLuong() != Integer.parseInt(sl))
                ok = false;

            if (!tong.isEmpty() && hd.getTongTien() != Double.parseDouble(tong))
                ok = false;

            if (!maKH.isEmpty() && !hd.getMaKH().toLowerCase().contains(maKH.toLowerCase()))
                ok = false;

            if (!maCombo.isEmpty() && !hd.getMaCombo().toLowerCase().contains(maCombo.toLowerCase()))
                ok = false;

            if (!maNV.isEmpty() && !hd.getMaNV().toLowerCase().contains(maNV.toLowerCase()))
                ok = false;

            if (ok) kq.add(hd);
        }

        tableHD.setItems(kq);
    }

    // ===================== TOOL ======================
    private void clearFields() {
        txtMaHD.clear();
        txtSoLuong.clear();
        dpNgayMua.setValue(null);
        txtTongTien.clear();
        txtMaKH.clear();
        txtMaCombo.clear();
    }

    private void showAlert(String title, String msg, AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean showConfirmDialog(String title, String msg) {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setHeaderText(null);
        confirm.setContentText(msg);

        ButtonType ok = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait();
        return confirm.getResult() == ok;
    }
}
