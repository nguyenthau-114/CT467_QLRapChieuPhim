package main;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class TimKiemHoaDonController {

    @FXML private TextField tfMaHD;
    @FXML private TextField tfSoLuong;
    @FXML private DatePicker dpNgayMua;
    @FXML private TextField tfTongTien;
    @FXML private TextField tfMaKH;
    @FXML private TextField tfMaCombo;

    @FXML private Button btnClose;

    private HoaDonController mainController;

    public void setMainController(HoaDonController controller) {
        this.mainController = controller;
    }

    @FXML
    private void thucHienTim() {

        String ngay = (dpNgayMua.getValue() == null)
                ? ""
                : dpNgayMua.getValue().toString();

        mainController.timKiemNangCao(
                tfMaHD.getText().trim(),
                tfSoLuong.getText().trim(),
                ngay,
                tfTongTien.getText().trim(),
                tfMaKH.getText().trim(),
                tfMaCombo.getText().trim()
        );

        Stage stage = (Stage) tfMaHD.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void dongPopup() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
