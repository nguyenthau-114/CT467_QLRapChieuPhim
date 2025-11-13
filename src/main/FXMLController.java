package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class FXMLController {

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    @FXML private VBox menuDuLieu;

    @FXML
    public void initialize() {
        // Khi mở FXML chính, load luôn Thống kê
        moTrangThongKe();
    }

    // Load nội dung các trang con vào contentArea
    private void loadView(String path, String title) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(node);
            statusLabel.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Lỗi tải trang: " + path);
        }
    }

    @FXML public void moTrangThongKe() { loadView("/giaodien/Thongke.fxml", "Đang mở: Thống kê"); }
    @FXML private void moTrangPhim()      { loadView("/giaodien/Phim_truycap.fxml", "Đang mở: Quản lý Phim"); }
    @FXML private void moTrangSuatChieu(){ loadView("/giaodien/SuatChieu.fxml", "Đang mở: Suất Chiếu"); }
    @FXML private void moTrangPhong()     { loadView("/giaodien/PhongChieu.fxml", "Đang mở: Phòng Chiếu"); }
    @FXML private void moTrangVe()        { loadView("/giaodien/ve_truycap.fxml", "Đang mở: Vé"); }
    @FXML private void moTrangNhanVien()  { loadView("/giaodien/nhanvien.fxml", "Đang mở: Nhân Viên"); }
    @FXML private void moTrangKhachHang() { loadView("/giaodien/khachhang.fxml", "Đang mở: Khách Hàng"); }


    // ================= Đăng xuất =================
    @FXML
    private void dangXuat(ActionEvent event) {
        try {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/giaodien/DangNhap.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Đăng nhập");
            loginStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void hienMenuDuLieu() {
        menuDuLieu.setVisible(true);
        menuDuLieu.setManaged(true);
    }

    @FXML
    private void anMenuDuLieu() {
        new Thread(() -> {
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                if (!menuDuLieu.isHover()) {
                    menuDuLieu.setVisible(false);
                    menuDuLieu.setManaged(false);
                }
            });
        }).start();
    }

    @FXML
    private void giuMenuKhiHover() {
        menuDuLieu.setVisible(true);
        menuDuLieu.setManaged(true);
    }

    @FXML
    private void anMenuKhiRoi() {
        menuDuLieu.setVisible(false);
        menuDuLieu.setManaged(false);
    }
    @FXML
    private void dangXuat() {
        statusLabel.setText("📌 Bạn đã đăng xuất.");
    }
}

    






