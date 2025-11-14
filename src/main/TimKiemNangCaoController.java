package main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    @FXML private Button btnClose;

    private SuatChieuController mainController;

    public void setMainController(SuatChieuController controller) {
        this.mainController = controller;
    }

    @FXML
    private void initialize() {
        taiComboBox();
    }

    private void taiComboBox() {
        try (Connection conn = DBConnection.getConnection()) {

            ObservableList<String> phim = FXCollections.observableArrayList();
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT tenphim FROM phim");
            while (rs1.next()) phim.add(rs1.getString(1));
            cbPhim.setItems(phim);

            ObservableList<String> phong = FXCollections.observableArrayList();
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT maphong FROM phongchieu");
            while (rs2.next()) phong.add(rs2.getString(1));
            cbPhong.setItems(phong);

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

        dongPopup();
    }

    @FXML
    private void dongPopup() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
