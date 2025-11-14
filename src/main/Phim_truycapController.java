package main;

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
import dulieu.TheLoaiItem;
import dulieu.phim;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;


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
                //showAlert("Đã thêm phim thành công!");
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
                //showAlert("Đã cập nhật phim thành công!");
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
                    //showAlert("Đã xóa phim thành công!");
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
private void moTimKiemPopup() {
    try {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/giaodien/TimKiemPhim.fxml")
        );
        Parent root = loader.load();

        TimKiemPhimController popup = loader.getController();
        popup.setMainController(this);

        Stage stage = new Stage();

        // ⭐ Giúp bỏ màu nền mặc định của Stage
        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(root);

        // ⭐ Giúp bỏ nền trắng của Scene
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}


   public void timKiemNangCao(String ma, String ten, String theloai, String daodien, LocalDate ngayKC) {

    ObservableList<phim> ketQua = FXCollections.observableArrayList();

    for (phim p : dao.getAllPhim()) {
        boolean ok = true;

        if (!ma.isEmpty() && !p.getMaPhim().toLowerCase().contains(ma.toLowerCase()))
            ok = false;

        if (!ten.isEmpty() && !p.getTenPhim().toLowerCase().contains(ten.toLowerCase()))
            ok = false;

        if (!theloai.isEmpty() && !p.getTheLoai().toLowerCase().contains(theloai.toLowerCase()))
            ok = false;

        if (!daodien.isEmpty() && !p.getDaoDien().toLowerCase().contains(daodien.toLowerCase()))
            ok = false;

        if (ngayKC != null && (p.getNgayKhoiChieu() == null ||
                !p.getNgayKhoiChieu().equals(ngayKC.toString())))
            ok = false;

        if (ok) ketQua.add(p);
    }

    tablePhim.setItems(ketQua);
}
 

}