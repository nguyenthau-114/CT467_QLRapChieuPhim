package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.beans.property.*;
import java.sql.*;
import ketnoi_truyxuat.DBConnection;
import dulieu.Thongke;
import dulieu.TLapDay;   // ⭐ THÊM IMPORT QUAN TRỌNG

public class ThongkeController {

    @FXML private Label lblTongPhim, lblTongVe, lblDoanhThu, lblSuatChieu, lblTongDoanhThu;

    // 🔹 BẢNG DOANH THU THEO PHIM
    @FXML private TableView<Thongke> tableThongKe;
    @FXML private TableColumn<Thongke, String> colTenPhim;
    @FXML private TableColumn<Thongke, Integer> colSoVe;
    @FXML private TableColumn<Thongke, Double> colDoanhThu;

    // 🔹 BẢNG TỶ LỆ LẤP ĐẦY
    @FXML private TableView<TLapDay> tableTLapDay;
    @FXML private TableColumn<TLapDay, String> colMaSuat, colPhim, colPhong, colNgay, colGio;
    @FXML private TableColumn<TLapDay, Integer> colTongGhe, colDaBan;
    @FXML private TableColumn<TLapDay, Double> colTyLe;

    @FXML private VBox menuDuLieu;

    @FXML
    public void initialize() {
        // 🧮 Load thống kê tổng quan
        try (Connection conn = DBConnection.getConnection()) {

            lblTongPhim.setText(getCount(conn, "SELECT COUNT(*) FROM phim"));
            lblTongVe.setText(getCount(conn, "SELECT COUNT(*) FROM ve"));
            lblDoanhThu.setText(getSum(conn, "SELECT SUM(giave) FROM ve") + " VNĐ");
            lblSuatChieu.setText(getCount(conn, "SELECT COUNT(*) FROM suatchieu"));

        } catch (Exception e) {
            e.printStackTrace();
            lblDoanhThu.setText("Lỗi dữ liệu!");
        }

        // 🟦 Setup bảng doanh thu
        tableThongKe.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableThongKe.setPlaceholder(new Label("Chưa có dữ liệu"));
        colTenPhim.setStyle("-fx-alignment: CENTER;");
        colSoVe.setStyle("-fx-alignment: CENTER;");
        colDoanhThu.setStyle("-fx-alignment: CENTER-RIGHT;");

        // ⭐⭐⭐ LOAD BẢNG TỶ LỆ LẤP ĐẦY (bị thiếu)
        loadTLapDay();
        tableTLapDay.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    }

    private String getCount(Connection conn, String query) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(query);
        return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
    }

    private String getSum(Connection conn, String query) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(query);
        return rs.next() ? String.format("%,d", rs.getInt(1)) : "0";
    }

    // =================================================
    // 📊 NÚT THỐNG KÊ DOANH THU THEO PHIM
    // =================================================
    @FXML
    private void onThongKePhim(ActionEvent e) {

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT tenphim, so_ve_ban, tong_doanhthu FROM view_doanhthu_phim";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            ObservableList<Thongke> list = FXCollections.observableArrayList();
            double tong = 0;

            while (rs.next()) {
                list.add(new Thongke(
                        rs.getString("tenphim"),
                        rs.getInt("so_ve_ban"),
                        rs.getDouble("tong_doanhthu")
                ));
                tong += rs.getDouble("tong_doanhthu");
            }

            colTenPhim.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenPhim()));
            colSoVe.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSoVe()).asObject());
            colDoanhThu.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getDoanhThu()).asObject());

            tableThongKe.setItems(list);
            lblTongDoanhThu.setText(String.format("%,.0f VNĐ", tong));

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi truy vấn: view_doanhthu_phim").show();
        }
    }

    // =================================================
    // 📊 LOAD TỶ LỆ LẤP ĐẦY (bảng dưới)
    // =================================================
    private void loadTLapDay() {
        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM view_tyle_suatchieu";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            ObservableList<TLapDay> list = FXCollections.observableArrayList();

            while (rs.next()) {
                list.add(new TLapDay(
                        rs.getString("masuatchieu"),
                        rs.getString("tenphim"),
                        rs.getString("tenphong"),
                        rs.getString("ngaychieu"),
                        rs.getString("giochieu"),
                        rs.getInt("tong_ghe"),
                        rs.getInt("da_ban"),
                        rs.getDouble("tyle")
                ));
            }

            colMaSuat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaSuat()));
            colPhim.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenPhim()));
            colPhong.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhong()));
            colNgay.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNgay()));
            colGio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGio()));
            colTongGhe.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTongGhe()).asObject());
            colDaBan.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getDaBan()).asObject());
            colTyLe.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getTyLe()).asObject());

            tableTLapDay.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =================================================
    // 🔐 ĐĂNG XUẤT
    // =================================================
    @FXML
    private void dangXuat(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // =================================================
    // 📁 MENU DỮ LIỆU
    // =================================================
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

    // =================================================
    // 🔄 CHUYỂN TRANG
    // =================================================
    private void chuyenTrang(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể mở trang: " + fxmlPath).show();
        }
    }

    @FXML private void moTrangPhim(ActionEvent e) { chuyenTrang(e, "/phim/Phim_truycap.fxml"); }
    @FXML private void moTrangSuatChieu(ActionEvent e) { chuyenTrang(e, "/SuatChieu/SuatChieu.fxml"); }
    @FXML private void moTrangPhongChieu(ActionEvent e) { chuyenTrang(e, "/Phong/PhongChieu.fxml"); }
    @FXML private void moTrangVe(ActionEvent e) { chuyenTrang(e, "/ve/ve_truycap.fxml"); }
    @FXML private void moTrangNhanVien(ActionEvent e) { chuyenTrang(e, "/nhanvien/NhanVien.fxml"); }
    @FXML private void moTrangKhachHang(ActionEvent e) { chuyenTrang(e, "/khachhang/khachhang.fxml"); }

}
