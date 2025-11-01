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
        String sql = "SELECT * FROM Phim";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                phim p = new phim(
                    rs.getString("maphim"),
                    rs.getString("tenphim"),
                    rs.getString("theloai"),
                    rs.getString("daodien"),
                    rs.getInt("thoiluong"),
                    rs.getString("ngaykhoichieu"),
                    rs.getInt("dotuoichophep")
                );
                dsPhim.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dsPhim;
    }
}
