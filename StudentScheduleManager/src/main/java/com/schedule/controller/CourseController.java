package com.schedule.controller;

import com.schedule.dao.CourseDAO;
import com.schedule.model.Course;

import java.time.DayOfWeek;
import java.util.List;

/**
 * 课程控制器
 */
public class CourseController {
    private CourseDAO courseDAO;
    
    public CourseController() {
        courseDAO = new CourseDAO();
    }
    
    /**
     * 添加课程
     */
    public boolean addCourse(Course course) {
        return courseDAO.addCourse(course);
    }
    
    /**
     * 更新课程
     */
    public boolean updateCourse(Course course) {
        return courseDAO.updateCourse(course);
    }
    
    /**
     * 删除课程
     */
    public boolean deleteCourse(int courseId, int userId) {
        return courseDAO.deleteCourse(courseId, userId);
    }
    
    /**
     * 获取用户的所有课程
     */
    public List<Course> getCoursesByUserId(int userId) {
        return courseDAO.getCoursesByUserId(userId);
    }
    
    /**
     * 根据ID获取课程
     */
    public Course getCourseById(int courseId, int userId) {
        return courseDAO.getCourseById(courseId, userId);
    }
    
    /**
     * 获取指定星期的课程
     */
    public List<Course> getCoursesByDayOfWeek(int userId, DayOfWeek dayOfWeek) {
        return courseDAO.getCoursesByDayOfWeek(userId, dayOfWeek);
    }
    
    /**
     * 批量添加课程
     */
    public boolean batchAddCourses(List<Course> courses) {
        return courseDAO.batchAddCourses(courses);
    }
    
    /**
     * 验证课程信息
     */
    public String validateCourse(Course course) {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            return "课程名称不能为空";
        }
        
        if (course.getStartTime() == null || course.getEndTime() == null) {
            return "课程时间不能为空";
        }
        
        if (course.getStartTime().isAfter(course.getEndTime())) {
            return "开始时间不能晚于结束时间";
        }
        
        if (course.getDayOfWeek() == null) {
            return "请选择上课星期";
        }
        
        return null; // 验证通过
    }
} 