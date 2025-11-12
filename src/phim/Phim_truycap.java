package phim;

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
    public boolean insertPhim(phim p) {
        String sql = "INSERT INTO phim(tenPhim, theLoai, daoDien, thoiLuong, ngayKhoiChieu, doTuoiChoPhep) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTenPhim());
            ps.setString(2, p.getTheLoai());
            ps.setString(3, p.getDaoDien());
            ps.setInt(4, p.getThoiLuong());
            ps.setString(5, p.getNgayKhoiChieu());
            ps.setInt(6, p.getDoTuoiChoPhep());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updatePhim(phim p) {
        String sql = "UPDATE phim SET tenPhim=?, theLoai=?, daoDien=?, thoiLuong=?, ngayKhoiChieu=?, doTuoiChoPhep=? " +
                     "WHERE maPhim=?";
        try (Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTenPhim());
            ps.setString(2, p.getTheLoai());
            ps.setString(3, p.getDaoDien());
            ps.setInt(4, p.getThoiLuong());
            ps.setString(5, p.getNgayKhoiChieu());
            ps.setInt(6, p.getDoTuoiChoPhep());
            ps.setString(7, p.getMaPhim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void deletePhim(String maPhim) throws Exception {
        String sql = "DELETE FROM phim WHERE maPhim=?";
        try (Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhim);
            ps.executeUpdate(); // nếu trigger báo lỗi, exception sẽ bị ném ra
        }
    }
}
