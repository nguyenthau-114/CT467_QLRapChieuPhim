package ketnoi_truyxuat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/htql_rap_phim";
    private static final String USER = "root";
    private static final String PASSWORD = "NTDiemMy221105@";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối MySQL thành công!");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối MySQL: " + e.getMessage());
            return null;
        }
    }
}