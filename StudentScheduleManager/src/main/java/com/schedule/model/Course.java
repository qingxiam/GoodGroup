package com.schedule.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * 课程数据模型
 */
public class Course {
    private int id;
    private int userId;
    private String name;
    private String teacher;
    private String location;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private CourseType type;
    private String description;
    private boolean reminderEnabled;
    
    public enum CourseType {
        REQUIRED("必修", java.awt.Color.RED),
        ELECTIVE("选修", java.awt.Color.BLUE),
        PRACTICAL("实验", java.awt.Color.GREEN),
        SEMINAR("研讨", java.awt.Color.ORANGE);
        
        private final String displayName;
        private final java.awt.Color color;
        
        CourseType(String displayName, java.awt.Color color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public java.awt.Color getColor() {
            return color;
        }
        
        public java.awt.Color getMacBgColor() {
            switch (this) {
                case REQUIRED: return new java.awt.Color(0xFFCDD2);
                case ELECTIVE: return new java.awt.Color(0xBBDEFB);
                case PRACTICAL: return new java.awt.Color(0xC8E6C9);
                case SEMINAR: return new java.awt.Color(0xFFE0B2);
                default: return java.awt.Color.WHITE;
            }
        }
    }
    
    public Course() {}
    
    public Course(int userId, String name, String teacher, String location, 
                  DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, 
                  CourseType type, String description) {
        this.userId = userId;
        this.name = name;
        this.teacher = teacher;
        this.location = location;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.description = description;
        this.reminderEnabled = true;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTeacher() {
        return teacher;
    }
    
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public CourseType getType() {
        return type;
    }
    
    public void setType(CourseType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isReminderEnabled() {
        return reminderEnabled;
    }
    
    public void setReminderEnabled(boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                ", location='" + location + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", type=" + type +
                '}';
    }
} 