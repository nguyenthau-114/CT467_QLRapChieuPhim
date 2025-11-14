package main;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TimKiemPhongController {

    @FXML private TextField tfMaPhong;
    @FXML private TextField tfTenPhong;
    @FXML private TextField tfSoGhe;
    @FXML private TextField tfLoaiPhong;

    private PhongChieuController mainController;

    public void setMainController(PhongChieuController controller) {
        this.mainController = controller;
    }

    @FXML
    private void thucHienTim() {

        mainController.timKiemNangCao(
                tfMaPhong.getText().trim(),
                tfTenPhong.getText().trim(),
                tfSoGhe.getText().trim(),
                tfLoaiPhong.getText().trim()
        );

        Stage s = (Stage) tfMaPhong.getScene().getWindow();
        s.close();
    }
}
