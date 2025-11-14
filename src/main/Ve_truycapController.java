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

public class Ve_truycapController {

    @FXML private TextField tfMaVe, tfGiaVe, tfTrangThai, tfMaSuatChieu, tfMaKhachHang, tfMaGhe, tfTimKiem;
    @FXML private DatePicker dpNgayDat;
    @FXML private TableView<ve> tableVe;
    @FXML private TableColumn<ve, String> colMaVe, colTrangThai, colMaSuatChieu, colMaKhachHang, colMaGhe;
    @FXML private TableColumn<ve, Date> colNgayDat;
    @FXML private TableColumn<ve, Double> colGiaVe;

    private ObservableList<ve> danhSachVe = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMaVe.setCellValueFactory(cell -> cell.getValue().maveProperty());
        colNgayDat.setCellValueFactory(cell -> cell.getValue().ngaydatProperty());
        colGiaVe.setCellValueFactory(cell -> cell.getValue().giaveProperty().asObject());
        colTrangThai.setCellValueFactory(cell -> cell.getValue().trangthaiProperty());
        colMaSuatChieu.setCellValueFactory(cell -> cell.getValue().suatchieu_masuatchieuProperty());
        colMaKhachHang.setCellValueFactory(cell -> cell.getValue().khachhang_makhachhangProperty());
        colMaGhe.setCellValueFactory(cell -> cell.getValue().ghe_magheProperty());

        //taiDuLieu();

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
    
    // 🔹 Mẹo thêm — tự động chia đều kích thước cột
    tableVe.widthProperty().addListener((obs, oldWidth, newWidth) -> {
        double width = newWidth.doubleValue() / tableVe.getColumns().size();
        tableVe.getColumns().forEach(col -> col.setPrefWidth(width));
    });
    }

    // ===========================
    // 🔹 TẢI DỮ LIỆU
    // ===========================
    @FXML
    private void taiDuLieu() {
        danhSachVe.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ve")) {

            while (rs.next()) {
                ve ve = new ve(
                        rs.getString("mave"),
                        rs.getDate("ngaydat"),
                        rs.getDouble("giave"),
                        rs.getString("trangthai"),
                        rs.getString("suatchieu_masuatchieu"),
                        rs.getString("khachhang_makhachhang"),
                        rs.getString("ghe_maghe")
                );
                danhSachVe.add(ve);
            }
            tableVe.setItems(danhSachVe);
        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể tải dữ liệu vé:\n" + e.getMessage());
        }
    }

    // ===========================
    // 🔹 THÊM
    // ===========================
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
            //showAlert("Thành công", "Đã thêm vé mới!");
            taiDuLieu();
            clearForm();

        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể thêm vé:\n" + e.getMessage());
        }
    }

    // ===========================
    // 🔹 SỬA
    // ===========================
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
            //showAlert("Thành công", "Đã cập nhật thông tin vé!");
            taiDuLieu();
            clearForm();

        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể sửa vé:\n" + e.getMessage());
        }
    }

    // ===========================
    // 🔹 XÓA
    // ===========================
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
            //showAlert("Thành công", "Đã xóa vé!");
            taiDuLieu();
            clearForm();

        } catch (SQLException e) {
            showAlert("Lỗi", "Không thể xóa vé:\n" + e.getMessage());
        }
    }

    // ===========================
    // 🔹 HÀM TIỆN ÍCH
    // ===========================
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
    private void dangXuat(javafx.event.ActionEvent event) {
        // Hiển thị thông báo đơn giản
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã đăng xuất khỏi hệ thống!");
        alert.showAndWait();

        // (Tùy chọn) Đóng cửa sổ hiện tại
        ((javafx.stage.Stage) ((javafx.scene.Node) event.getSource())
                .getScene().getWindow()).close();

        // (Hoặc mở lại màn hình đăng nhập nếu bạn có file login.fxml)
        /*
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

        private void showAlert(String title, String content) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }

        //tim kiem nang cao
        
        @FXML
private void moTimKiemPopup() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/giaodien/TimKiemVe.fxml"));
        Parent root = loader.load();

        TimKiemVeController popup = loader.getController();
        popup.setMainController(this);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Tìm kiếm vé");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
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

      

}
