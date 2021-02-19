package vip.testops.apitest.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class JdbcUtil {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    public static String getDriverClassFromURL(String url){
        if(url.startsWith("jdbc:mysql")){
            return "com.mysql.cj.jdbc.Driver";
        } else if(url.startsWith("jdbc:oracle")){
            return "oracle.jdbc.driver.OracleDriver";
        } else {
            logger.error("unsupported jdbc url");
            return null;
        }
    }

    public static Connection getConnection(String url, String username, String password){
        Connection conn = null;
        try {
            Class.forName(getDriverClassFromURL(url));
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("DB Connection initial failed");
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeAll(ResultSet rs, Statement stmt, Connection conn){
        try{
            if(rs != null) {
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
