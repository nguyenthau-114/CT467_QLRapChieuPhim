package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FXMLController {

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    @FXML
    public void initialize() {
        // 🔥 Khi mở ứng dụng -> vào ngay trang Thống Kê
        moTrangThongKe();
    }

    private void loadView(String path, String title) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(path));
            contentArea.getChildren().setAll(node);
            statusLabel.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Lỗi tải trang " + path);
        }
    }

    @FXML private void moTrangPhim() {
        loadView("/giaodien/Phim_truycap.fxml", "Đang mở: Quản lý phim");
    }

    @FXML private void moTrangSuatChieu() {
        loadView("/giaodien/SuatChieu.fxml", "Đang mở: Suất chiếu");
    }

    @FXML private void moTrangPhong() {
        loadView("/giaodien/PhongChieu.fxml", "Đang mở: Phòng chiếu");
    }

    @FXML private void moTrangVe() {
        loadView("/giaodien/ve_truycap.fxml", "Đang mở: Vé");
    }

    @FXML private void moTrangNhanVien() {
        loadView("/giaodien/nhanvien.fxml", "Đang mở: Nhân viên");
    }

    @FXML private void moTrangKhachHang() {
        loadView("/giaodien/khachhang.fxml", "Đang mở: Khách hàng");
    }

    @FXML private void moTrangThongKe() {
        loadView("/giaodien/Thongke.fxml", "Đang mở: Thống kê");
    }
    @FXML private VBox menuDuLieu;

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




