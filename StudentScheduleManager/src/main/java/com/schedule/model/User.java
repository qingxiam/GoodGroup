package com.schedule.model;

/**
 * 用户数据模型
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String studentId;
    private String name;
    
    public User() {}
    
    public User(String username, String password, String email, String studentId, String name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.studentId = studentId;
        this.name = name;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
} 