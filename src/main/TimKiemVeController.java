package main;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TimKiemVeController {

    @FXML private TextField tfMaVe;
    @FXML private TextField tfTrangThai;
    @FXML private TextField tfMaSuatChieu;
    @FXML private TextField tfMaKhachHang;
    @FXML private TextField tfMaGhe;
    @FXML private DatePicker dpNgayDat;

    private Ve_truycapController mainController;

    public void setMainController(Ve_truycapController controller) {
        this.mainController = controller;
    }

    @FXML
    private void thucHienTim() {

        String ngay = dpNgayDat.getValue() == null ? "" : dpNgayDat.getValue().toString();

        mainController.timKiemNangCao(
                tfMaVe.getText().trim(),
                ngay,
                tfTrangThai.getText().trim(),
                tfMaSuatChieu.getText().trim(),
                tfMaKhachHang.getText().trim(),
                tfMaGhe.getText().trim()
        );

        Stage s = (Stage) tfMaVe.getScene().getWindow();
        s.close();
    }
}
