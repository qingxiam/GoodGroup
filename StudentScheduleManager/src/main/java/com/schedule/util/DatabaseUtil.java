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
            
            // 检查courses表是否为空，若为空则插入预制课程（以初始用户为userId=1）
            String checkCourseSql = "SELECT COUNT(*) FROM courses";
            ResultSet crs = stmt.executeQuery(checkCourseSql);
            int courseCount = 0;
            if (crs.next()) {
                courseCount = crs.getInt(1);
            }
            if (courseCount == 0) {
                // 预制课程数据
                String insertCourseSql = "INSERT INTO courses (user_id, name, teacher, location, day_of_week, start_time, end_time, type, description, reminder_enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertCourseSql)) {
                    // 以截图为准，以下为示例，您可根据实际补充/调整
                    // 星期一 第2节（必修）
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "计算机应用基础(03)");
                    pstmt.setString(3, "李慧琪");
                    pstmt.setString(4, "");
                    pstmt.setString(5, "MONDAY");
                    pstmt.setString(6, "08:55");
                    pstmt.setString(7, "09:40");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "6-9周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期一 第3节（选修）
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "微积分V-1(04)");
                    pstmt.setString(3, "张文");
                    pstmt.setString(4, "庄汉水楼（南强二）401");
                    pstmt.setString(5, "MONDAY");
                    pstmt.setString(6, "10:10");
                    pstmt.setString(7, "10:55");
                    pstmt.setString(8, "ELECTIVE");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期一 第5节（实验）
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "管理学原理B(04)");
                    pstmt.setString(3, "许雯翔");
                    pstmt.setString(4, "庄汉水楼（南强二）203");
                    pstmt.setString(5, "MONDAY");
                    pstmt.setString(6, "14:30");
                    pstmt.setString(7, "15:15");
                    pstmt.setString(8, "PRACTICAL");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期一 第7节（研讨）
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "形势与政策（1）(38)");
                    pstmt.setString(3, "赵忍杰,郑楚桥,吴荣梅,梅超杰");
                    pstmt.setString(4, "庄汉水楼（南强二）106");
                    pstmt.setString(5, "MONDAY");
                    pstmt.setString(6, "16:40");
                    pstmt.setString(7, "17:25");
                    pstmt.setString(8, "SEMINAR");
                    pstmt.setString(9, "9-15单周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期二 第2节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "大学英语3级(05SQ2)");
                    pstmt.setString(3, "冯型");
                    pstmt.setString(4, "囯武楼105");
                    pstmt.setString(5, "TUESDAY");
                    pstmt.setString(6, "08:55");
                    pstmt.setString(7, "09:40");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16双周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期二 第3节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "微积分V-1(04)");
                    pstmt.setString(3, "张文");
                    pstmt.setString(4, "庄汉水楼（南强二）401");
                    pstmt.setString(5, "TUESDAY");
                    pstmt.setString(6, "10:10");
                    pstmt.setString(7, "10:55");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期二 第5节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "会计学原理(08)");
                    pstmt.setString(3, "郑斌");
                    pstmt.setString(4, "庄汉水楼（南强二）504");
                    pstmt.setString(5, "TUESDAY");
                    pstmt.setString(6, "14:30");
                    pstmt.setString(7, "15:15");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期三 第3节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "微积分V-1(04)");
                    pstmt.setString(3, "张文");
                    pstmt.setString(4, "庄汉水楼（南强二）401");
                    pstmt.setString(5, "WEDNESDAY");
                    pstmt.setString(6, "10:10");
                    pstmt.setString(7, "10:55");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期三 第5节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "大学语文(02)");
                    pstmt.setString(3, "洪迎华");
                    pstmt.setString(4, "庄汉水楼（南强二）502");
                    pstmt.setString(5, "WEDNESDAY");
                    pstmt.setString(6, "14:30");
                    pstmt.setString(7, "15:15");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期三 第7节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "管理学原理B(04)");
                    pstmt.setString(3, "许雯翔");
                    pstmt.setString(4, "庄汉水楼（南强二）203");
                    pstmt.setString(5, "WEDNESDAY");
                    pstmt.setString(6, "16:40");
                    pstmt.setString(7, "17:25");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期四 第2节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "体育舞蹈-摩登舞（基础2）(01)");
                    pstmt.setString(3, "邹红");
                    pstmt.setString(4, "");
                    pstmt.setString(5, "THURSDAY");
                    pstmt.setString(6, "08:55");
                    pstmt.setString(7, "09:40");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期四 第5节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "会计学原理(08)");
                    pstmt.setString(3, "郑斌");
                    pstmt.setString(4, "庄汉水楼（南强二）504");
                    pstmt.setString(5, "THURSDAY");
                    pstmt.setString(6, "14:30");
                    pstmt.setString(7, "15:15");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期四 第6节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "中国近现代史纲要(24)");
                    pstmt.setString(3, "董兴艳");
                    pstmt.setString(4, "祖营楼（嘉庚四）103");
                    pstmt.setString(5, "THURSDAY");
                    pstmt.setString(6, "15:25");
                    pstmt.setString(7, "16:10");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期五 第5节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "大学生心理健康(13)");
                    pstmt.setString(3, "陈梅香");
                    pstmt.setString(4, "集美二104");
                    pstmt.setString(5, "FRIDAY");
                    pstmt.setString(6, "14:30");
                    pstmt.setString(7, "15:15");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16双周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    // 星期五 第7节
                    pstmt.setInt(1, 1);
                    pstmt.setString(2, "大学英语3级(05)");
                    pstmt.setString(3, "江柱英,刘青,毛跃,张坤坤");
                    pstmt.setString(4, "庄汉水楼（南强二）307");
                    pstmt.setString(5, "FRIDAY");
                    pstmt.setString(6, "16:40");
                    pstmt.setString(7, "17:25");
                    pstmt.setString(8, "REQUIRED");
                    pstmt.setString(9, "4-16双周");
                    pstmt.setBoolean(10, true);
                    pstmt.addBatch();
                    pstmt.executeBatch();
                    System.out.println("已自动插入预制课程表");
                }
            }
            
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