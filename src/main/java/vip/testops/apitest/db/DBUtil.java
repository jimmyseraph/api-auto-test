package vip.testops.apitest.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {

    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

    public static Map<String, Object> getOne(Connection conn, String sql, Object...params){
        if(conn == null){
            logger.error("Connection cannot be null.");
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for(int i = 0; i < params.length; i++){
                ps.setObject(i+1, params[i]);
            }
            rs = ps.executeQuery();
            if(rs.next()){
                ResultSetMetaData rsmd = rs.getMetaData();
                for(int j = 0; j < rsmd.getColumnCount(); j++){
                    map.put(rsmd.getColumnLabel(j+1), rs.getObject(j+1));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<Map<String, Object>> getList(Connection conn, String sql, Object...params){
        if(conn == null){
            logger.error("Connection cannot be null.");
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for(int i = 0; i < params.length; i++){
                ps.setObject(i+1, params[i]);
            }
            rs = ps.executeQuery();
            while(rs.next()){
                ResultSetMetaData rsmd = rs.getMetaData();
                Map<String, Object> map = new HashMap<>();
                for(int j = 0; j < rsmd.getColumnCount(); j++){
                    map.put(rsmd.getColumnLabel(j+1), rs.getObject(j+1));
                }
                list.add(map);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean doDML(Connection conn, String sql, Object...params){
        if(conn == null){
            logger.error("Connection cannot be null.");
            return false;
        }
        PreparedStatement ps = null;
        int rs = 0;
        try {
            ps = conn.prepareStatement(sql);
            for(int i = 0; i < params.length; i++){
                ps.setObject(i+1, params[i]);
            }
            rs = ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs != 0;
    }
}
