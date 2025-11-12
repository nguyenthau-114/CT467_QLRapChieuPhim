package phim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;


public class Phim_truycapController {

    // Form fields
    @FXML private TextField tfMaPhim, tfTenPhim, tfDaoDien, tfThoiLuong, tfDoTuoi;
    @FXML private DatePicker dpNgayKC;
    @FXML private ListView<TheLoaiItem> lvTheLoai;
    @FXML private ImageView imgPoster;

    // Table
    @FXML private TableView<phim> tablePhim;
    @FXML private TableColumn<phim, String> colMaPhim, colTenPhim, colTheLoai, colDaoDien, colNgayKhoiChieu;
    @FXML private TableColumn<phim, Integer> colThoiLuong, colDoTuoi;

    private final Phim_truycap dao = new Phim_truycap();
    private final ObservableList<TheLoaiItem> dsTheLoai = FXCollections.observableArrayList(
            new TheLoaiItem("Hành động"), new TheLoaiItem("Hài"),
            new TheLoaiItem("Tình cảm"), new TheLoaiItem("Gia đình"),
            new TheLoaiItem("Viễn tưởng"), new TheLoaiItem("Phiêu lưu"),
            new TheLoaiItem("Tâm lý")
    );

    @FXML
    public void initialize() {
        // Table columns
        colMaPhim.setCellValueFactory(new PropertyValueFactory<>("maPhim"));
        colTenPhim.setCellValueFactory(new PropertyValueFactory<>("tenPhim"));
        colTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        colDaoDien.setCellValueFactory(new PropertyValueFactory<>("daoDien"));
        colThoiLuong.setCellValueFactory(new PropertyValueFactory<>("thoiLuong"));
        colNgayKhoiChieu.setCellValueFactory(new PropertyValueFactory<>("ngayKhoiChieu"));
        colDoTuoi.setCellValueFactory(new PropertyValueFactory<>("doTuoiChoPhep"));

        // ListView thể loại dạng checkbox
        lvTheLoai.setItems(dsTheLoai);
        lvTheLoai.setCellFactory(CheckBoxListCell.forListView(TheLoaiItem::selectedProperty));

        // Chọn dòng trong bảng ⇒ đổ dữ liệu lên form
        tablePhim.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p == null) return;
            tfMaPhim.setText(p.getMaPhim());
            tfTenPhim.setText(p.getTenPhim());
            tfDaoDien.setText(p.getDaoDien());
            tfThoiLuong.setText(String.valueOf(p.getThoiLuong()));
            tfDoTuoi.setText(String.valueOf(p.getDoTuoiChoPhep()));
            try { dpNgayKC.setValue(LocalDate.parse(p.getNgayKhoiChieu())); } catch (Exception ignored) {}
            // set checkbox theo chuỗi theLoai (ngăn cách bởi ,)
            String[] picked = p.getTheLoai() == null ? new String[0] : p.getTheLoai().split("\\s*,\\s*");
            dsTheLoai.forEach(t -> t.setSelected(false));
            for (String s : picked) {
                dsTheLoai.stream().filter(t -> t.getName().equalsIgnoreCase(s)).findFirst().ifPresent(t -> t.setSelected(true));
            }
        });
         // --- Cấu hình dữ liệu cột ---
    colMaPhim.setCellValueFactory(new PropertyValueFactory<>("maPhim"));
    colTenPhim.setCellValueFactory(new PropertyValueFactory<>("tenPhim"));
    colTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
    colDaoDien.setCellValueFactory(new PropertyValueFactory<>("daoDien"));
    colThoiLuong.setCellValueFactory(new PropertyValueFactory<>("thoiLuong"));
    colNgayKhoiChieu.setCellValueFactory(new PropertyValueFactory<>("ngayKhoiChieu"));
    colDoTuoi.setCellValueFactory(new PropertyValueFactory<>("doTuoiChoPhep"));

    // --- Cho phép bảng co giãn đầy đủ ---
    tablePhim.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        // ListView checkbox
        lvTheLoai.setItems(dsTheLoai);
        lvTheLoai.setCellFactory(CheckBoxListCell.forListView(TheLoaiItem::selectedProperty));
    }

    @FXML
    public void onTaiDuLieu() {
        tablePhim.setItems(FXCollections.observableArrayList(dao.getAllPhim()));
    }

    @FXML
    public void onThem() {
        phim p = buildPhimFromForm();
        if (p == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận thêm");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn thêm phim này không?");
        ButtonType ok = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ok) {
                dao.insertPhim(p);
                onTaiDuLieu();
                tablePhim.getSelectionModel().selectLast(); // ✅ chọn dòng phim vừa thêm
                showAlert("Đã thêm phim thành công!");
            }
        });

    }


    @FXML
    public void onSua() {
        phim p = buildPhimFromForm();
        if (p == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận sửa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn cập nhật thông tin phim này không?");
        ButtonType ok = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ok) {
                dao.updatePhim(p);
                onTaiDuLieu();
                showAlert("Đã cập nhật phim thành công!");
            }
        });
    }


    @FXML
    public void onXoa() {
        phim selected = tablePhim.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Hãy chọn 1 dòng cần xóa");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa phim này không?");
        ButtonType ok = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ok) {
                try {
                    dao.deletePhim(selected.getMaPhim());
                    onTaiDuLieu();
                    clearForm();
                    showAlert("Đã xóa phim thành công!");
                } catch (Exception e) {
                    // Nếu trigger báo lỗi, dòng này sẽ bắt được và hiển thị thông báo SQL
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi xóa phim");
                    alert.setHeaderText(null);
                    // MySQL trigger SIGNAL sẽ truyền message qua e.getMessage()
                    alert.setContentText(
                        e.getMessage().contains("suatchieu") 
                            ? "Không thể xóa phim vì đang có suất chiếu!" 
                            : e.getMessage()
                    );
                    alert.showAndWait();
                }
            }
        });
    }



    @FXML
    public void onChonAnh() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ảnh", "*.png", "*.jpg", "*.jpeg"));
        File f = fc.showOpenDialog(imgPoster.getScene().getWindow());
        if (f != null) imgPoster.setImage(new Image(f.toURI().toString()));
        // Giai đoạn này chỉ hiển thị; lưu DB sẽ làm sau (blob/path).
    }

    // Helpers
    private phim buildPhimFromForm() {
        try {
            String ma = tfMaPhim.getText().trim(); // vẫn lấy, nhưng không bắt buộc
            String ten = tfTenPhim.getText().trim();
            String theloai = dsTheLoai.stream().filter(TheLoaiItem::isSelected)
                    .map(TheLoaiItem::getName).collect(Collectors.joining(", "));
            String daoDien = tfDaoDien.getText().trim();
            int thoiLuong = Integer.parseInt(tfThoiLuong.getText().trim());
            String ngayKC = dpNgayKC.getValue() == null ? null : dpNgayKC.getValue().toString();
            int doTuoi = Integer.parseInt(tfDoTuoi.getText().trim());

            if (ten.isEmpty()) {
                showAlert("Tên phim bắt buộc nhập!");
                return null;
            }

            return new phim(ma, ten, theloai, daoDien, thoiLuong, ngayKC, doTuoi);
        } catch (NumberFormatException e) {
            showAlert("Thời lượng/Độ tuổi phải là số nguyên.");
            return null;
        }
    }

    private void clearForm() {
        tfMaPhim.clear(); tfTenPhim.clear(); tfDaoDien.clear(); tfThoiLuong.clear(); tfDoTuoi.clear();
        dpNgayKC.setValue(null); dsTheLoai.forEach(t -> t.setSelected(false)); imgPoster.setImage(null);
    }
    private void showAlert(String m) { new Alert(Alert.AlertType.INFORMATION, m).showAndWait(); 
    }
    @FXML
    private void dangXuat(javafx.event.ActionEvent event) {
        // Hiển thị thông báo đơn giản
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã đăng xuất khỏi hệ thống!");
        alert.showAndWait();

        // (Tùy chọn) Đóng cửa sổ hiện tại
        ((javafx.stage.Stage) ((javafx.scene.Node) event.getSource())
                .getScene().getWindow()).close();

        // (Hoặc mở lại màn hình đăng nhập nếu bạn có file login.fxml)
        /*
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Đăng nhập");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
    //hàm chuyển trang chính
 private void chuyenTrang(ActionEvent e, String fxmlPath) {
    try {
        javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlPath));
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    } catch (Exception ex) {
        ex.printStackTrace();
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                "Không thể mở trang: " + fxmlPath).show();
    }
}

 //hàm chuyển trang qua lại
    @FXML
private void moTrangSuatChieu(ActionEvent e) {
    chuyenTrang(e, "/suatchieu/SuatChieu.fxml");
}
@FXML
private void moTrangPhongChieu(ActionEvent e) {
    chuyenTrang(e, "/Phong/PhongChieu.fxml");
}
@FXML
private void moTrangVe(ActionEvent e) {
    chuyenTrang(e, "/ve/ve_truycap.fxml");
}
@FXML
private void moTrangThongKe(ActionEvent e) {
    chuyenTrang(e, "/thongke/Thongke.fxml");
}
@FXML
private void moTrangNhanVien(ActionEvent e) {
    chuyenTrang(e, "/nhanvien/NhanVien.fxml");
}

@FXML
private void moTrangKhachHang(ActionEvent e) {
    chuyenTrang(e, "/khachhang/khachhang.fxml");
}
}
