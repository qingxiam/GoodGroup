package com.schedule.dao;

import com.schedule.model.Course;
import com.schedule.util.DatabaseUtil;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程数据访问对象
 */
public class CourseDAO {
    
    /**
     * 添加课程
     */
    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (user_id, name, teacher, location, day_of_week, start_time, end_time, type, description, reminder_enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, course.getUserId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getTeacher());
            pstmt.setString(4, course.getLocation());
            pstmt.setString(5, course.getDayOfWeek().toString());
            pstmt.setString(6, course.getStartTime().toString());
            pstmt.setString(7, course.getEndTime().toString());
            pstmt.setString(8, course.getType().toString());
            pstmt.setString(9, course.getDescription());
            pstmt.setBoolean(10, course.isReminderEnabled());
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    course.setId(rs.getInt(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库错误: " + e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * 更新课程
     */
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET name = ?, teacher = ?, location = ?, day_of_week = ?, start_time = ?, end_time = ?, type = ?, description = ?, reminder_enabled = ? WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getName());
            pstmt.setString(2, course.getTeacher());
            pstmt.setString(3, course.getLocation());
            pstmt.setString(4, course.getDayOfWeek().toString());
            pstmt.setString(5, course.getStartTime().toString());
            pstmt.setString(6, course.getEndTime().toString());
            pstmt.setString(7, course.getType().toString());
            pstmt.setString(8, course.getDescription());
            pstmt.setBoolean(9, course.isReminderEnabled());
            pstmt.setInt(10, course.getId());
            pstmt.setInt(11, course.getUserId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 删除课程
     */
    public boolean deleteCourse(int courseId, int userId) {
        String sql = "DELETE FROM courses WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取用户的所有课程
     */
    public List<Course> getCoursesByUserId(int userId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE user_id = ? ORDER BY day_of_week, start_time";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Course course = mapResultSetToCourse(rs);
                courses.add(course);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courses;
    }
    
    /**
     * 根据ID获取课程
     */
    public Course getCourseById(int courseId, int userId) {
        String sql = "SELECT * FROM courses WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 获取指定星期的课程
     */
    public List<Course> getCoursesByDayOfWeek(int userId, DayOfWeek dayOfWeek) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE user_id = ? AND day_of_week = ? ORDER BY start_time";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, dayOfWeek.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Course course = mapResultSetToCourse(rs);
                courses.add(course);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return courses;
    }
    
    /**
     * 将ResultSet映射为Course对象
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setUserId(rs.getInt("user_id"));
        course.setName(rs.getString("name"));
        course.setTeacher(rs.getString("teacher"));
        course.setLocation(rs.getString("location"));
        course.setDayOfWeek(DayOfWeek.valueOf(rs.getString("day_of_week")));
        course.setStartTime(LocalTime.parse(rs.getString("start_time")));
        course.setEndTime(LocalTime.parse(rs.getString("end_time")));
        course.setType(Course.CourseType.valueOf(rs.getString("type")));
        course.setDescription(rs.getString("description"));
        course.setReminderEnabled(rs.getBoolean("reminder_enabled"));
        return course;
    }
    
    /**
     * 批量添加课程
     */
    public boolean batchAddCourses(List<Course> courses) {
        String sql = "INSERT INTO courses (user_id, name, teacher, location, day_of_week, start_time, end_time, type, description, reminder_enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (Course course : courses) {
                pstmt.setInt(1, course.getUserId());
                pstmt.setString(2, course.getName());
                pstmt.setString(3, course.getTeacher());
                pstmt.setString(4, course.getLocation());
                pstmt.setString(5, course.getDayOfWeek().toString());
                pstmt.setString(6, course.getStartTime().toString());
                pstmt.setString(7, course.getEndTime().toString());
                pstmt.setString(8, course.getType().toString());
                pstmt.setString(9, course.getDescription());
                pstmt.setBoolean(10, course.isReminderEnabled());
                
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            conn.commit();
            
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 