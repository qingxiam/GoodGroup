package com.schedule.view;

import com.schedule.controller.UserController;
import com.schedule.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * 修改密码对话框
 */
public class ChangePasswordDialog extends JDialog {
    private UserController userController;
    private User currentUser;
    
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton confirmButton;
    private JButton cancelButton;
    
    public ChangePasswordDialog(JFrame parent, UserController userController, User currentUser) {
        super(parent, "修改密码", true);
        this.userController = userController;
        this.currentUser = currentUser;
        initComponents();
        setupLayout();
        setupListeners();
    }
    
    private void initComponents() {
        setSize(350, 250);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        oldPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        confirmButton = new JButton("确认");
        cancelButton = new JButton("取消");
        
        // 设置按钮样式
        confirmButton.setBackground(new Color(46, 139, 87));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 标题
        JLabel titleLabel = new JLabel("修改密码");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        // 当前密码
        addFormField(mainPanel, "当前密码:", oldPasswordField, gbc, 1);
        
        // 新密码
        addFormField(mainPanel, "新密码:", newPasswordField, gbc, 2);
        
        // 确认新密码
        addFormField(mainPanel, "确认新密码:", confirmPasswordField, gbc, 3);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
    
    private void setupListeners() {
        confirmButton.addActionListener(e -> handleChangePassword());
        cancelButton.addActionListener(e -> dispose());
        
        // 回车键确认
        getRootPane().setDefaultButton(confirmButton);
    }
    
    private void handleChangePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // 验证输入
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有密码字段", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 验证当前密码
        if (!oldPassword.equals(currentUser.getPassword())) {
            JOptionPane.showMessageDialog(this, "当前密码错误", "错误", JOptionPane.ERROR_MESSAGE);
            oldPasswordField.setText("");
            return;
        }
        
        // 验证新密码
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的新密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            return;
        }
        
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "新密码长度不能少于6位", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 更新密码
        currentUser.setPassword(newPassword);
        
        if (userController.updateUser(currentUser)) {
            JOptionPane.showMessageDialog(this, "密码修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "密码修改失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 