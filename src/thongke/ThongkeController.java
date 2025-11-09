package thongke;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.property.*;
import java.sql.*;

import ketnoi_truyxuat.DBConnection;

public class ThongkeController {

    @FXML private Label lblTongPhim, lblTongVe, lblDoanhThu, lblSuatChieu, lblTongDoanhThu;
    @FXML private ComboBox<String> cboPhim;
    @FXML private DatePicker dpTuNgay, dpDenNgay;
    @FXML private TableView<Thongke> tableThongKe;
    @FXML private TableColumn<Thongke, String> colTenPhim, colNgayChieu, colGioChieu;
    @FXML private TableColumn<Thongke, Integer> colSoVe;
    @FXML private TableColumn<Thongke, Double> colDoanhThu;
    @FXML private TableView<?> tableTopPhim; // ✅ thêm dòng này

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

        // 🔹 Cấu hình bảng Top 10 phim
        tableTopPhim.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableTopPhim.setPlaceholder(new Label("Chưa có dữ liệu thống kê"));

        // 🔹 Cấu hình bảng Thống kê doanh thu
        tableThongKe.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableThongKe.setPlaceholder(new Label("Chưa có dữ liệu thống kê"));

         // Thiết lập kích thước tối thiểu để không bị che chữ

        dpTuNgay.setPrefWidth(150);
        dpDenNgay.setPrefWidth(150);
        cboPhim.setPrefWidth(220);
        
        // 🔹 Căn giữa header
        tableTopPhim.widthProperty().addListener((obs, oldVal, newVal) ->
            tableTopPhim.lookupAll(".column-header .label")
                    .forEach(node -> node.setStyle("-fx-alignment: CENTER;"))
        );

        tableThongKe.widthProperty().addListener((obs, oldVal, newVal) ->
            tableThongKe.lookupAll(".column-header .label")
                    .forEach(node -> node.setStyle("-fx-alignment: CENTER;"))
        );

        // 🔹 Căn giữa dữ liệu trong cell (tuỳ chọn)
        colTenPhim.setStyle("-fx-alignment: CENTER;");
        colNgayChieu.setStyle("-fx-alignment: CENTER;");
        colGioChieu.setStyle("-fx-alignment: CENTER;");
        colSoVe.setStyle("-fx-alignment: CENTER;");
        colDoanhThu.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        
    }

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

    @FXML
    private void onThongKe(ActionEvent event) {
        if (cboPhim.getValue() == null || dpTuNgay.getValue() == null || dpDenNgay.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn phim và khoảng thời gian!", ButtonType.OK).showAndWait();
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

    @FXML
    private void dangXuat(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
