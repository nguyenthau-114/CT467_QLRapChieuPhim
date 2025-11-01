package ketnoi_truyxuat;

import java.sql.Connection;

public class Test {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Đã kết nối thành công tới cơ sở dữ liệu MySQL!");
        } else {
            System.out.println("❌ Kết nối thất bại. Vui lòng kiểm tra lại cấu hình!");
        }
    }
}

