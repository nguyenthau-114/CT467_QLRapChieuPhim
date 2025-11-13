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

public class ThongkeController {

    @FXML private Label lblTongPhim, lblTongVe, lblDoanhThu, lblSuatChieu, lblTongDoanhThu;

    @FXML private TableView<Thongke> tableThongKe;
    @FXML private TableColumn<Thongke, String> colTenPhim;
    @FXML private TableColumn<Thongke, Integer> colSoVe;
    @FXML private TableColumn<Thongke, Double> colDoanhThu;

    @FXML private TableView<?> tableTopPhim;
    @FXML private VBox menuDuLieu;


    @FXML
    public void initialize() {
        try (Connection conn = DBConnection.getConnection()) {

            lblTongPhim.setText(getCount(conn, "SELECT COUNT(*) FROM phim"));
            lblTongVe.setText(getCount(conn, "SELECT COUNT(*) FROM ve"));
            lblDoanhThu.setText(getSum(conn, "SELECT SUM(giave) FROM ve") + " VNĐ");
            lblSuatChieu.setText(getCount(conn, "SELECT COUNT(*) FROM suatchieu"));

        } catch (Exception e) {
            e.printStackTrace();
            lblDoanhThu.setText("Lỗi dữ liệu!");
        }

        tableThongKe.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableTopPhim.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        tableThongKe.setPlaceholder(new Label("Chưa có dữ liệu"));
        tableTopPhim.setPlaceholder(new Label("Chưa có dữ liệu"));

        colTenPhim.setStyle("-fx-alignment: CENTER;");
        colSoVe.setStyle("-fx-alignment: CENTER;");
        colDoanhThu.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    private String getCount(Connection conn, String query) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(query);
        return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
    }

    private String getSum(Connection conn, String query) throws SQLException {
        ResultSet rs = conn.createStatement().executeQuery(query);
        return rs.next() ? String.format("%,d", rs.getInt(1)) : "0";
    }

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
            new Alert(Alert.AlertType.ERROR, "Lỗi khi truy vấn view_doanhthu_phim!").show();
        }
    }

    @FXML
    private void dangXuat(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    // ===============================
    // 📂 MENU DỮ LIỆU (hiện/ẩn + điều hướng)
    // ===============================

    @FXML
    private void hienMenuDuLieu() {
        menuDuLieu.setVisible(true);
        menuDuLieu.setManaged(true); // cho phép layout nhận diện khi hiển thị
    }

    @FXML
    private void anMenuDuLieu() {
        // Trì hoãn 150ms để tránh mất menu khi rê chuột xuống quá nhanh
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
    // ===============================
    // 📂 CHUYỂN TRANG (nút trong menu Dữ liệu)
    // ===============================

    @FXML
    private void moTrangPhim(ActionEvent event) {
        chuyenTrang(event, "/phim/Phim_truycap.fxml");
    }

    @FXML
    private void moTrangSuatChieu(ActionEvent event) {
        chuyenTrang(event, "/SuatChieu/SuatChieu.fxml");
    }

    @FXML
    private void moTrangPhongChieu(ActionEvent event) {
        chuyenTrang(event, "/Phong/PhongChieu.fxml");
    }

    @FXML
    private void moTrangVe(ActionEvent event) {
        chuyenTrang(event, "/ve/ve_truycap.fxml");
    }

    /** 🔹 Hàm dùng chung để chuyển trang */
    private void chuyenTrang(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không thể mở trang: " + fxmlPath).show();
        }
    }
    //hàm chuyển trang Nhân Viên
    @FXML
    private void moTrangNhanVien(ActionEvent e) {
        chuyenTrang(e, "/nhanvien/NhanVien.fxml");
    }
    @FXML
    private void moTrangKhachHang(ActionEvent e) {
        chuyenTrang(e, "/khachhang/khachhang.fxml");
    }

    }