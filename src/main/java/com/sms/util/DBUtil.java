package com.sms.util;

import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * 数据库工具类：加载配置、获取/关闭连接。
 */
public class DBUtil {

    private static String url;
    private static String username;
    private static String password;

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties p = new Properties();
            p.load(in);
            String driver = p.getProperty("driver");
            url = p.getProperty("url");
            username = p.getProperty("username");
            password = p.getProperty("password");
            Class.forName(driver);
        } catch (Exception e) {
            throw new RuntimeException("数据库配置加载失败: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(Connection c, Statement s, ResultSet r) {
        if (r != null) { try { r.close(); } catch (Exception ignored) {} }
        if (s != null) { try { s.close(); } catch (Exception ignored) {} }
        if (c != null) { try { c.close(); } catch (Exception ignored) {} }
    }

    /** 将 Timestamp 格式化为 yyyy-MM-dd HH:mm:ss 字符串，空值返回 null */
    public static String fmt(Timestamp t) {
        if (t == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t);
    }
}
