package main;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class TimKiemNhanVienController {

    @FXML private TextField txtMaNV;
    @FXML private TextField txtTenNV;
    @FXML private TextField txtChucVu;
    @FXML private TextField txtSDT;
    @FXML private TextField txtEmail;

    private NhanVienController mainController;

    public void setMainController(NhanVienController controller) {
        this.mainController = controller;
    }

    @FXML
    private void thucHienTim() {

        mainController.timKiemNangCao(
                txtMaNV.getText().trim(),
                txtTenNV.getText().trim(),
                txtChucVu.getText().trim(),
                txtSDT.getText().trim(),
                txtEmail.getText().trim()
        );

        Stage stage = (Stage) txtMaNV.getScene().getWindow();
        stage.close();
    }
}
