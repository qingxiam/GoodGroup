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
    // 修复登录逻辑：调用 UserDAO 校验数据库
    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    /**
     * 用户注册
     */
    public boolean register(User user) {
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