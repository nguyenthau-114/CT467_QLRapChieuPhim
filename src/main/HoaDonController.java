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
        clearFields();
        tableHD.getSelectionModel().clearSelection();
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
                        rs.getString("bapnuoc_macombo")
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
                     "INSERT INTO hoadon VALUES (?,?,?,?,?,?)")) {

            ps.setString(1, txtMaHD.getText());
            ps.setInt(2, Integer.parseInt(txtSoLuong.getText()));
            ps.setDate(3, Date.valueOf(dpNgayMua.getValue()));
            ps.setDouble(4, java.sql.Types.DOUBLE);
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

        Date ngayMuaMoi;
        int soLuongMoi;
        double tongTienMoi;

        try {
            ngayMuaMoi = Date.valueOf(dpNgayMua.getValue());
            soLuongMoi = Integer.parseInt(txtSoLuong.getText().trim());
            tongTienMoi = Double.parseDouble(txtTongTien.getText().trim());
        } catch (Exception ex) {
            showAlert("Dữ liệu không hợp lệ",
                      "Ngày mua, số lượng hoặc tổng tiền không hợp lệ!", AlertType.ERROR);
            return;
        }
        String maKHMoi = txtMaKH.getText().trim();
        String maComboMoi = txtMaCombo.getText().trim();

        boolean sameDate = (originalNgayMua == null && ngayMuaMoi == null)
                           || (originalNgayMua != null && originalNgayMua.equals(ngayMuaMoi));

        boolean khongThayDoi =
                sameDate &&
                soLuongMoi == originalSoLuong &&
                tongTienMoi == originalTongTien &&
                maKHMoi.equals(originalMaKH) &&
                maComboMoi.equals(originalMaCombo);

        if (khongThayDoi) {
            showAlert("Không có thay đổi",
                      "Bạn chưa thay đổi thông tin nào để cập nhật!", AlertType.INFORMATION);
            return;
        }
        // ⭐ Hộp thoại xác nhận như cũ
        if (!showConfirmDialog("Xác nhận", "Bạn muốn cập nhật hóa đơn này?")) {
            return;
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE hoadon SET soluongcombo=?, ngaymua=?, tongtien=?, khachhang_makhachhang=?, bapnuoc_macombo=? WHERE mahoadon=?")) {

            ps.setInt(1, soLuongMoi);
            ps.setDate(2, ngayMuaMoi);
            ps.setDouble(3, tongTienMoi);              // 🔁 dùng đúng tổng tiền mới
            ps.setString(4, maKHMoi);
            ps.setString(5, maComboMoi);
            ps.setString(6, ma);

            ps.executeUpdate();
            showAlert("Thành công", "Đã cập nhật hóa đơn thành công!", AlertType.INFORMATION);
            onTaiDuLieu();
            clearFields();
        } catch (SQLException e) {
            showAlert("Lỗi cập nhật", e.getMessage(), AlertType.ERROR);
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

            int rowIndex = 1;
            for (HoaDon hd : tableHD.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(hd.getMaHD());
                row.createCell(1).setCellValue(hd.getSoLuong());
                row.createCell(2).setCellValue(hd.getNgayMua().toString());
                row.createCell(3).setCellValue(hd.getTongTien());
                row.createCell(4).setCellValue(hd.getMaKH());
                row.createCell(5).setCellValue(hd.getMaCombo());
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
                    )
                kq.add(hd);
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
        
        originalMaHD = "";
        originalMaKH = "";
        originalMaCombo = "";
        originalSoLuong = 0;
        originalTongTien = 0;
        originalNgayMua = null;
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
