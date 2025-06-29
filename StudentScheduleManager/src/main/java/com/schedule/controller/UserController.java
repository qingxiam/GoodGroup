package com.schedule.controller;

import com.schedule.dao.UserDAO;
import com.schedule.model.User;

/**
 * 用户控制器
 */
public class UserController {
    private UserDAO userDAO;
    
    public UserController() {
        userDAO = new UserDAO();
    }
    
    /**
     * 用户登录
     */
    public User login(String username, String password) {
        // 直接返回一个虚拟用户对象，无论输入什么
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail("test@example.com");
        user.setStudentId("20240001");
        user.setName("测试用户");
        return user;
    }
    
    /**
     * 用户注册
     */
    public boolean register(User user) {
        // 验证用户名是否已存在
        if (userDAO.isUsernameExists(user.getUsername())) {
            return false;
        }
        
        return userDAO.register(user);
    }
    
    /**
     * 检查用户名是否已存在
     */
    public boolean isUsernameExists(String username) {
        return userDAO.isUsernameExists(username);
    }
    
    /**
     * 根据ID获取用户信息
     */
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
    
    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
} 