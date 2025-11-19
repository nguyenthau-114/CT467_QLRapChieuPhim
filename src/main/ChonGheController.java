package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;
import java.util.*;
import javafx.scene.control.Alert;

public class ChonGheController {

    @FXML private GridPane gridGhe;
    @FXML private Button btnXacNhan, btnHuy;

    // =============================
    // 🔹 BIẾN TOÀN CỤC (KHÔNG TRÙNG LẶP)
    // =============================
    private String gheDangChon = null;
    private Button nutDangChon = null;
    private Set<String> gheDaDat = new HashSet<>();
    private Set<String> gheVIP = new HashSet<>();

    private String maSuatChieu;
    private Ve_truycapController mainController;

    public void setData(String maSC, Ve_truycapController controller) {
        this.maSuatChieu = maSC;
        this.mainController = controller;
        taiSoDoGhe();
    }

    private void taiSoDoGhe() {
        try {
            Connection conn = ketnoi_truyxuat.DBConnection.getConnection();

            // 1. LẤY MÃ PHÒNG
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT phongchieu_maphong FROM suatchieu WHERE masuatchieu = ?");
            ps.setString(1, maSuatChieu);
            ResultSet rs = ps.executeQuery();

            String maPhong = rs.next() ? rs.getString(1) : null;
            if (maPhong == null) return;

            // 2. LẤY SỐ GHẾ
            ps = conn.prepareStatement("SELECT soghe FROM phongchieu WHERE maphong = ?");
            ps.setString(1, maPhong);
            rs = ps.executeQuery();
            int soGhe = rs.next() ? rs.getInt(1) : 0;

            // 3. GHẾ ĐÃ ĐẶT (TRỪ GHẾ ĐÃ HỦY)
            ps = conn.prepareStatement(
                "SELECT ghe_maghe FROM ve WHERE suatchieu_masuatchieu = ? AND trangthai <> 'Đã hủy'"
            );
            ps.setString(1, maSuatChieu);
            rs = ps.executeQuery();
            while (rs.next()) gheDaDat.add(rs.getString(1));

            // 4. VẼ SƠ ĐỒ GHẾ
        int gheMoiHang = 10;
int soHang = (int) Math.ceil((double) soGhe / gheMoiHang);

// lối đi rộng 2 cột
int loDiRong = 8;
int cotLoDi = gheMoiHang / 2;

int gheIndex = 0;

// 🔥 TẠO GHẾ VIP (chỉ phòng 2D - 3D)
xacDinhGheVIP(soHang, gheMoiHang, maPhong);

for (int i = 0; i < soHang; i++) {
    char hang = (char) ('A' + i);

    for (int j = 0; j < gheMoiHang; j++) {

        gheIndex++;
        if (gheIndex > soGhe) break;

        int soThuTu = j + 1;

        String maGhe = String.format("%s_%s%02d", maPhong, hang, soThuTu);
        Button btn = new Button(hang + "" + soThuTu);

        // NÚT NHỎ GỌN
        btn.setPrefSize(35, 32);

        // --- KIỂM TRA GHẾ ĐÃ ĐẶT BẰNG FUNCTION ---
        CallableStatement cs = conn.prepareCall("SELECT fn_ghe_trong(?, ?) AS status");
        cs.setString(1, maSuatChieu);
        cs.setString(2, maGhe);
        ResultSet rsCheck = cs.executeQuery();

        boolean daDat = false;
        if (rsCheck.next()) {
            daDat = rsCheck.getInt("status") == 1;
        }

        // --- SET MÀU GHẾ ---
        if (daDat) {
            btn.setStyle("-fx-background-color:#94a3b8; -fx-text-fill:white;");
            btn.setDisable(true);
        }
        else if (gheVIP.contains(maGhe)) {
            btn.setStyle("-fx-background-color:#b91c1c; -fx-text-fill:white;");
        }
        else {
            btn.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white;");
        }


        String gheChonTmp = maGhe;
        btn.setOnAction(e -> {
            if (nutDangChon != null && !nutDangChon.isDisable()) {
                if (gheVIP.contains(nutDangChon.getText()))
                    nutDangChon.setStyle("-fx-background-color:#b91c1c; -fx-text-fill:white;");
                else
                    nutDangChon.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white;");
            }
            btn.setStyle("-fx-background-color:#22c55e; -fx-text-fill:white;");
            nutDangChon = btn;
            gheDangChon = gheChonTmp;
        });

        // LỐI ĐI RỘNG 2 CỘT
        int col = j;
        if (j >= cotLoDi) col += loDiRong;

        gridGhe.add(btn, col, i);
    }
}

            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    //Ghe VIP
    //
    private void xacDinhGheVIP(int soHang, int gheMoiHang, String maPhong) {

    // VIP cho phòng 2D hoặc 3D
    try (Connection conn = ketnoi_truyxuat.DBConnection.getConnection()) {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT loaiphong FROM phongchieu WHERE maphong=?"
        );
        ps.setString(1, maPhong);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return;

        String loaiPhong = rs.getString(1);

        if (loaiPhong.equalsIgnoreCase("IMAX"))
            return; // IMAX không có VIP

        // Nếu là 2D hoặc 3D → tạo vùng VIP như hình

        for (int hang = 1; hang <= 3; hang++) { // B, C, D
            char hangChar = (char) ('A' + hang);

            // VIP bên trái
            for (int col = 3; col <= 5; col++) {
                gheVIP.add(String.format("%s_%s%02d", maPhong, hangChar, col));
            }

            // VIP bên phải
            for (int col = 6; col <= 8; col++) {
                gheVIP.add(String.format("%s_%s%02d", maPhong, hangChar, col));
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // ============================
    // 🔹 NÚT XÁC NHẬN
    // ============================
    @FXML
    private void xacNhan() {
        if (gheDangChon == null) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Bạn chưa chọn ghế nào!");
            alert.showAndWait();
            return;
        }

        // Trả ghế về form chính
        boolean isVIP = gheVIP.contains(gheDangChon);
        mainController.setGheDuocChon(gheDangChon, isVIP);


        // Tô xám vì đã đặt
        if (nutDangChon != null) {
            nutDangChon.setStyle("-fx-background-color:#94a3b8; -fx-text-fill:white;");
            nutDangChon.setDisable(true);
        }
        
        // ⭐ THÔNG BÁO THÀNH CÔNG ⭐
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Chọn ghế thành công!");
        alert.showAndWait();

        ((Stage) gridGhe.getScene().getWindow()).close();
    }

    // ============================
    // 🔹 NÚT HỦY
    // ============================
    @FXML
    private void huyChon() {
        if (nutDangChon != null && !nutDangChon.isDisable()) {
            nutDangChon.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white;");
        }
        gheDangChon = null;
    }
}
