package main;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import ketnoi_truyxuat.DBConnection;

public class TimKiemNangCaoController {

    @FXML private TextField txtMaSuat;
    @FXML private DatePicker dpNgay;
    @FXML private ComboBox<String> cbPhim;
    @FXML private ComboBox<String> cbPhong;
    @FXML private ComboBox<String> cbTrangThai;

    private SuatChieuController mainController; // tham chiếu ngược

    public void setMainController(SuatChieuController controller) {
        this.mainController = controller;
    }

    // Khi popup mở ra sẽ tự load dữ liệu cho combobox
    @FXML
    private void initialize() {
        taiComboBoxTimKiem();
    }

    private void taiComboBoxTimKiem() {
        try (Connection conn = DBConnection.getConnection()) {

            // Load phim
            ObservableList<String> dsPhim = FXCollections.observableArrayList();
            ResultSet rsPhim = conn.createStatement()
                    .executeQuery("SELECT tenphim FROM phim");
            while (rsPhim.next()) {
                dsPhim.add(rsPhim.getString("tenphim"));
            }
            cbPhim.setItems(dsPhim);

            // Load phòng
            ObservableList<String> dsPhong = FXCollections.observableArrayList();
            ResultSet rsPhong = conn.createStatement()
                    .executeQuery("SELECT maphong FROM phongchieu");
            while (rsPhong.next()) {
                dsPhong.add(rsPhong.getString("maphong"));
            }
            cbPhong.setItems(dsPhong);

            // Load trạng thái (cố định)
            cbTrangThai.setItems(FXCollections.observableArrayList(
                    "Sắp ra mắt", "Sắp chiếu", "Đang chiếu", "Đã chiếu"
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void thucHienTim() {

        mainController.timKiemNangCao(
                txtMaSuat.getText().trim(),
                dpNgay.getValue(),
                cbPhim.getValue(),
                cbPhong.getValue(),
                cbTrangThai.getValue()
        );

        // đóng popup
        Stage stage = (Stage) txtMaSuat.getScene().getWindow();
        stage.close();
    }
}
