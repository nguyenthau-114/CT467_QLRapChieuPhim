package main;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TimKiemKhachHangController {

    @FXML private TextField tfMaKH;
    @FXML private TextField tfTenKH;
    @FXML private TextField tfSDT;
    @FXML private TextField tfEmail;

    private KhachhangController mainController;

    public void setMainController(KhachhangController controller) {
        this.mainController = controller;
    }

    @FXML
    private void thucHienTim() {

        mainController.timKiemNangCao(
                tfMaKH.getText().trim(),
                tfTenKH.getText().trim(),
                tfSDT.getText().trim(),
                tfEmail.getText().trim()
        );

        Stage stage = (Stage) tfMaKH.getScene().getWindow();
        stage.close();
    }
}
