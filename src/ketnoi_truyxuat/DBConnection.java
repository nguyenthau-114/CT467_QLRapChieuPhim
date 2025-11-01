package ketnoi_truyxuat;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/htql_rap_phim";
    private static final String USER = "root";
    private static final String PASSWORD = "NTDiemMy221105@";

    // 🔹 Hàm tạo kết nối, gọi ở bất kỳ đâu
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to database successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }
        return conn;
    }
}
