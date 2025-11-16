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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import javafx.stage.FileChooser;



public class Phim_truycapController {

    // Form fields
    @FXML private TextField tfMaPhim, tfTenPhim, tfDaoDien, tfThoiLuong, tfDoTuoi;
    @FXML private DatePicker dpNgayKC;
    @FXML private ListView<TheLoaiItem> lvTheLoai;

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
            showAlert("Thành công",
                      "Đã thêm phim thành công!",
                      Alert.AlertType.INFORMATION);
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
                showAlert("Thành công",
                          "Đã cập nhật phim thành công!",
                          Alert.AlertType.INFORMATION);
            }

        });
    }


    @FXML
    public void onXoa() {
        phim selected = tablePhim.getSelectionModel().getSelectedItem();
        if (selected == null) {
        showAlert("Thiếu thông tin",
                  "Hãy chọn 1 dòng cần xóa.",
                  Alert.AlertType.WARNING);
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
                    showAlert("Thành công",
                              "Đã xóa phim thành công!",
                              Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    // MySQL trigger SIGNAL sẽ truyền message qua e.getMessage()
                    if (e.getMessage() != null && e.getMessage().contains("suatchieu")) {
                        showAlert("Không thể xóa phim",
                                  "Không thể xóa phim vì đang có suất chiếu!",
                                  Alert.AlertType.WARNING);
                    } else {
                        showAlert("Lỗi xóa phim",
                                  e.getMessage(),
                                  Alert.AlertType.ERROR);
                    }
                }
            }

        });
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
            showAlert("Thiếu thông tin",
                      "Tên phim bắt buộc nhập!",
                      Alert.AlertType.WARNING);
            return null;
            }



            return new phim(ma, ten, theloai, daoDien, thoiLuong, ngayKC, doTuoi);
            } catch (NumberFormatException e) {
            showAlert("Sai dữ liệu",
                      "Thời lượng và Độ tuổi phải là số nguyên.",
                      Alert.AlertType.ERROR);
            return null;
            }

    }

    private void clearForm() {
        tfMaPhim.clear(); tfTenPhim.clear(); tfDaoDien.clear(); tfThoiLuong.clear(); tfDoTuoi.clear();
        dpNgayKC.setValue(null); dsTheLoai.forEach(t -> t.setSelected(false));
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
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
    @FXML
    private void xuatExcel() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Xuất danh sách phim ra Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );

            File file = fileChooser.showSaveDialog(tablePhim.getScene().getWindow());
            if (file == null) return; // người dùng bấm Cancel

            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Phim");

            // ====== Dòng tiêu đề ======
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Mã phim");
            header.createCell(1).setCellValue("Tên phim");
            header.createCell(2).setCellValue("Thể loại");
            header.createCell(3).setCellValue("Đạo diễn");
            header.createCell(4).setCellValue("Thời lượng (phút)");
            header.createCell(5).setCellValue("Ngày khởi chiếu");
            header.createCell(6).setCellValue("Độ tuổi");

            // ====== Dữ liệu ======
            int rowIndex = 1;
            for (phim p : tablePhim.getItems()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(p.getMaPhim());
                row.createCell(1).setCellValue(p.getTenPhim());
                row.createCell(2).setCellValue(p.getTheLoai());
                row.createCell(3).setCellValue(p.getDaoDien());
                row.createCell(4).setCellValue(p.getThoiLuong());

                // Ngày khởi chiếu (trong model bạn đang lưu dạng String)
                if (p.getNgayKhoiChieu() != null) {
                    row.createCell(5).setCellValue(p.getNgayKhoiChieu());
                } else {
                    row.createCell(5).setCellValue("");
                }

                row.createCell(6).setCellValue(p.getDoTuoiChoPhep());
            }

            // Tự động chỉnh độ rộng cột cho đẹp
            for (int i = 0; i <= 6; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file
            try (FileOutputStream out = new FileOutputStream(file)) {
                wb.write(out);
            }
            wb.close();

            showAlert("Thành công", "Xuất Excel danh sách phim thành công!", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể xuất Excel: " + e.getMessage(), Alert.AlertType.ERROR);
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