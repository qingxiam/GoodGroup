package com.schedule.view;

import com.schedule.controller.UserController;
import com.schedule.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * 用户资料面板
 */
public class UserProfilePanel extends JPanel {
    private User currentUser;
    private UserController userController;
    
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField studentIdField;
    private JTextField nameField;
    private JButton saveButton;
    private JButton changePasswordButton;
    
    public UserProfilePanel(User user) {
        this.currentUser = user;
        this.userController = new UserController();
        initComponents();
        setupLayout();
        setupListeners();
        loadUserData();
    }
    
    private void initComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        studentIdField = new JTextField(20);
        nameField = new JTextField(20);
        saveButton = new JButton("保存修改");
        changePasswordButton = new JButton("修改密码");
        
        // 设置字段为只读或可编辑
        usernameField.setEditable(false);
        usernameField.setBackground(new Color(240, 240, 240));
        
        // 设置按钮样式
        saveButton.setBackground(new Color(46, 139, 87));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        
        changePasswordButton.setBackground(new Color(70, 130, 180));
        changePasswordButton.setForeground(Color.BLACK);
        changePasswordButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 标题
        JLabel titleLabel = new JLabel("个人资料");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        // 用户名
        addFormField(mainPanel, "用户名:", usernameField, gbc, 1);
        
        // 姓名
        addFormField(mainPanel, "姓名:", nameField, gbc, 2);
        
        // 学号
        addFormField(mainPanel, "学号:", studentIdField, gbc, 3);
        
        // 邮箱
        addFormField(mainPanel, "邮箱:", emailField, gbc, 4);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(changePasswordButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 添加信息面板
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("系统信息"));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(248, 248, 248));
        infoArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        infoArea.setText(
            "学生个人课表管理系统 v1.0\n\n" +
            "功能说明：\n" +
            "• 课程管理：添加、编辑、删除课程信息\n" +
            "• 课表查看：支持周视图和日视图切换\n" +
            "• 数据导入：支持Excel批量导入课程\n" +
            "• 提醒功能：上课前30分钟自动提醒\n\n" +
            "使用提示：\n" +
            "• 首次使用请先添加课程信息\n" +
            "• 可以通过Excel文件批量导入课程\n" +
            "• 课表支持按课程类型进行颜色分类\n" +
            "• 建议定期备份重要数据"
        );
        
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        
        return infoPanel;
    }
    
    private void setupListeners() {
        saveButton.addActionListener(e -> handleSave());
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
    }
    
    private void loadUserData() {
        // 重新获取最新的用户信息
        User updatedUser = userController.getUserById(currentUser.getId());
        if (updatedUser != null) {
            currentUser = updatedUser;
        }
        
        usernameField.setText(currentUser.getUsername());
        nameField.setText(currentUser.getName());
        studentIdField.setText(currentUser.getStudentId());
        emailField.setText(currentUser.getEmail());
    }
    
    private void handleSave() {
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String email = emailField.getText().trim();
        
        // 验证输入
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入姓名", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入学号", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 更新用户信息
        currentUser.setName(name);
        currentUser.setStudentId(studentId);
        currentUser.setEmail(email);
        
        if (userController.updateUser(currentUser)) {
            JOptionPane.showMessageDialog(this, "保存成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "保存失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog((JFrame) SwingUtilities.getWindowAncestor(this), userController, currentUser);
        dialog.setVisible(true);
    }
} 