package doituong;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Phim_truycapController {

    @FXML private TableView<phim> tablePhim;
    @FXML private TableColumn<phim, String> colMaPhim;
    @FXML private TableColumn<phim, String> colTenPhim;
    @FXML private TableColumn<phim, String> colTheLoai;
    @FXML private TableColumn<phim, String> colDaoDien;
    @FXML private TableColumn<phim, Integer> colThoiLuong;
    @FXML private TableColumn<phim, String> colNgayKhoiChieu;
    @FXML private TableColumn<phim, Integer> colDoTuoi;
    @FXML private Button btnTaiDuLieu;

    @FXML
    public void initialize() {
        colMaPhim.setCellValueFactory(new PropertyValueFactory<>("maPhim"));
        colTenPhim.setCellValueFactory(new PropertyValueFactory<>("tenPhim"));
        colTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        colDaoDien.setCellValueFactory(new PropertyValueFactory<>("daoDien"));
        colThoiLuong.setCellValueFactory(new PropertyValueFactory<>("thoiLuong"));
        colNgayKhoiChieu.setCellValueFactory(new PropertyValueFactory<>("ngayKhoiChieu"));
        colDoTuoi.setCellValueFactory(new PropertyValueFactory<>("doTuoiChoPhep"));
    }

    @FXML
    public void taiDuLieu() {
        Phim_truycap dao = new Phim_truycap();
        ObservableList<phim> dsPhim =
            FXCollections.observableArrayList(dao.getAllPhim());
        tablePhim.setItems(dsPhim);
    }
    // gọi lại hàm hiện có để nạp dữ liệu vào TableView

}
