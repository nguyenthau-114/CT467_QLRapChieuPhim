package thongke;

import ketnoi_truyxuat.DBConnection;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.sql.*;

/**
 * Controller cho giao diện thống kê doanh thu và dashboard tổng quan
 */
public class ThongkeController {

    // ---------------- PHẦN DASHBOARD ----------------
    @FXML private Label lblTongPhim;
    @FXML private Label lblTongVe;
    @FXML private Label lblDoanhThu;
    @FXML private Label lblSuatChieu;
    @FXML private TableView<?> tableTopPhim;
    @FXML private StackPane chartContainer;

    // ---------------- PHẦN THỐNG KÊ DOANH THU ----------------
    @FXML private ComboBox<String> cboPhim;
    @FXML private DatePicker dpTuNgay;
    @FXML private DatePicker dpDenNgay;
    @FXML private TableView<Thongke> tableThongKe;
    @FXML private TableColumn<Thongke, String> colTenPhim;
    @FXML private TableColumn<Thongke, String> colNgayChieu;
    @FXML private TableColumn<Thongke, String> colGioChieu;
    @FXML private TableColumn<Thongke, Integer> colSoVe;
    @FXML private TableColumn<Thongke, Double> colDoanhThu;
    @FXML private Label lblTongDoanhThu;

    // ---------------- KHỞI TẠO ----------------
    @FXML
    public void initialize() {
        try (Connection conn = DBConnection.getConnection()) {
            // Thông tin tổng quan
            lblTongPhim.setText(getCount(conn, "SELECT COUNT(*) FROM phim"));
            lblTongVe.setText(getCount(conn, "SELECT COUNT(*) FROM ve"));
            lblDoanhThu.setText(getSum(conn, "SELECT SUM(giave) FROM ve") + " VNĐ");
            lblSuatChieu.setText(getCount(conn, "SELECT COUNT(*) FROM suatchieu"));

            // Load danh sách phim vào combobox
            ResultSet rs = conn.createStatement().executeQuery("SELECT tenphim FROM phim");
            while (rs.next()) cboPhim.getItems().add(rs.getString("tenphim"));

        } catch (Exception e) {
            e.printStackTrace();
            lblDoanhThu.setText("Lỗi dữ liệu!");
        }
    }

    // ---------------- HÀM HỖ TRỢ ----------------
    private String getCount(Connection conn, String query) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        }
        return "0";
    }

    private String getSum(Connection conn, String query) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return String.format("%,d", rs.getInt(1));
        }
        return "0";
    }

    // ---------------- XỬ LÝ NÚT THỐNG KÊ ----------------
    @FXML
    private void onThongKe(ActionEvent event) {
        if (cboPhim.getValue() == null || dpTuNgay.getValue() == null || dpDenNgay.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng chọn phim và khoảng thời gian!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        String phim = cboPhim.getValue();
        java.sql.Date tuNgay = java.sql.Date.valueOf(dpTuNgay.getValue());
        java.sql.Date denNgay = java.sql.Date.valueOf(dpDenNgay.getValue());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT p.tenphim, s.ngaychieu, s.giochieu,
                       COUNT(v.id) AS so_ve,
                       SUM(v.giave) AS tong_tien
                FROM ve v
                JOIN suatchieu s ON v.idsuatchieu = s.id
                JOIN phim p ON s.idphim = p.id
                WHERE p.tenphim = ? AND s.ngaychieu BETWEEN ? AND ?
                GROUP BY p.tenphim, s.ngaychieu, s.giochieu
                ORDER BY s.ngaychieu ASC
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, phim);
            ps.setDate(2, tuNgay);
            ps.setDate(3, denNgay);
            ResultSet rs = ps.executeQuery();

            ObservableList<Thongke> list = FXCollections.observableArrayList();
            double tongDoanhThu = 0;

            while (rs.next()) {
                list.add(new Thongke(
                        rs.getString("tenphim"),
                        rs.getString("ngaychieu"),
                        rs.getString("giochieu"),
                        rs.getInt("so_ve"),
                        rs.getDouble("tong_tien")
                ));
                tongDoanhThu += rs.getDouble("tong_tien");
            }

            // Gắn dữ liệu vào bảng
            colTenPhim.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenPhim()));
            colNgayChieu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNgayChieu()));
            colGioChieu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGioChieu()));
            colSoVe.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getSoVe()).asObject());
            colDoanhThu.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getDoanhThu()).asObject());

            tableThongKe.setItems(list);
            lblTongDoanhThu.setText(String.format("%,.0f VNĐ", tongDoanhThu));

        } catch (Exception e) {
            e.printStackTrace();
            lblTongDoanhThu.setText("Lỗi truy vấn!");
        }
    }
}
