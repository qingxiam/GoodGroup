package com.schedule.util;

import java.sql.*;

/**
 * 数据库工具类
 */
public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:schedule.db";
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    /**
     * 初始化数据库和表
     */
    private static void initDatabase() {
        try (Connection conn = getConnection()) {
            // 创建用户表
            String createUserTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    email TEXT,
                    student_id TEXT,
                    name TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // 创建课程表
            String createCourseTable = """
                CREATE TABLE IF NOT EXISTS courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    teacher TEXT,
                    location TEXT,
                    day_of_week TEXT NOT NULL,
                    start_time TEXT NOT NULL,
                    end_time TEXT NOT NULL,
                    type TEXT NOT NULL,
                    description TEXT,
                    reminder_enabled BOOLEAN DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                )
            """;
            
            // 创建提醒表
            String createReminderTable = """
                CREATE TABLE IF NOT EXISTS reminders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    reminder_time TIMESTAMP NOT NULL,
                    sent BOOLEAN DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (course_id) REFERENCES courses (id)
                )
            """;
            
            Statement stmt = conn.createStatement();
            stmt.execute(createUserTable);
            stmt.execute(createCourseTable);
            stmt.execute(createReminderTable);
            
            // 检查users表是否为空，若为空则插入初始用户
            String checkUserSql = "SELECT COUNT(*) FROM users";
            ResultSet rs = stmt.executeQuery(checkUserSql);
            int userCount = 0;
            if (rs.next()) {
                userCount = rs.getInt(1);
            }
            if (userCount == 0) {
                String insertUserSql = "INSERT INTO users (username, password, email, student_id, name) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertUserSql)) {
                    pstmt.setString(1, "liuhaotian");
                    pstmt.setString(2, "123456");
                    pstmt.setString(3, "liuhaotian@example.com");
                    pstmt.setString(4, "20240001");
                    pstmt.setString(5, "刘浩天");
                    pstmt.executeUpdate();
                    System.out.println("已自动插入初始用户：liuhaotian / 123456");
                }
            }
            
            System.out.println("数据库初始化完成");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 关闭数据库连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 关闭Statement
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 关闭ResultSet
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 