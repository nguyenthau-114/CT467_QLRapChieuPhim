package doituong;

import ketnoi_truyxuat.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Phim_truycap {

    public List<phim> getAllPhim() {
        List<phim> dsPhim = new ArrayList<>();
        String sql = "SELECT * FROM phim";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                phim p = new phim(
                    rs.getString("maPhim"),
                    rs.getString("tenPhim"),
                    rs.getString("theLoai"),
                    rs.getString("daoDien"),
                    rs.getInt("thoiLuong"),
                    rs.getString("ngayKhoiChieu"),
                    rs.getInt("doTuoiChoPhep")
                );
                dsPhim.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsPhim;
    }
}
