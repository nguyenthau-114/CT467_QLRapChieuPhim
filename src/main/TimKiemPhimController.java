package main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class TimKiemPhimController {

    @FXML private TextField tfMaPhim;
    @FXML private TextField tfTenPhim;
    @FXML private TextField tfTheLoai;
    @FXML private TextField tfDaoDien;
    @FXML private DatePicker dpNgayKC;

    private Phim_truycapController mainController;

    public void setMainController(Phim_truycapController controller) {
        this.mainController = controller;
    }

    @FXML
    private void thucHienTim() {
        mainController.timKiemNangCao(
                tfMaPhim.getText().trim(),
                tfTenPhim.getText().trim(),
                tfTheLoai.getText().trim(),
                tfDaoDien.getText().trim(),
                dpNgayKC.getValue()
        );

        Stage stage = (Stage) tfMaPhim.getScene().getWindow();
        stage.close();
    }
    @FXML private Button btnClose;
    @FXML
private void dongPopup() {
 
   Stage stage = (Stage) btnClose.getScene().getWindow();
    stage.close();
}

}
