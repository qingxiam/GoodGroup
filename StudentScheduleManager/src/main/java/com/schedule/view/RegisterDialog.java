package com.schedule.view;

import com.schedule.controller.UserController;
import com.schedule.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * 注册对话框
 */
public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField studentIdField;
    private JTextField nameField;
    private JButton registerButton;
    private JButton cancelButton;
    private UserController userController;

    public RegisterDialog(JFrame parent, UserController userController) {
        super(parent, "用户注册", true);
        this.userController = userController;
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        setSize(350, 400);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        emailField = new JTextField(20);
        studentIdField = new JTextField(20);
        nameField = new JTextField(20);
        registerButton = new JButton("注册");
        cancelButton = new JButton("取消");

        // 设置按钮样式
        registerButton.setBackground(new Color(46, 139, 87));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);

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

        // 用户名
        addFormField(mainPanel, "用户名:", usernameField, gbc, 0);

        // 密码
        addFormField(mainPanel, "密码:", passwordField, gbc, 1);

        // 确认密码
        addFormField(mainPanel, "确认密码:", confirmPasswordField, gbc, 2);

        // 邮箱
        addFormField(mainPanel, "邮箱:", emailField, gbc, 3);

        // 学号
        addFormField(mainPanel, "学号:", studentIdField, gbc, 4);

        // 姓名
        addFormField(mainPanel, "姓名:", nameField, gbc, 5);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
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
        registerButton.addActionListener(e -> handleRegister());
        cancelButton.addActionListener(e -> dispose());

        // 回车键注册
        getRootPane().setDefaultButton(registerButton);
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String name = nameField.getText().trim();

        // 验证输入
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                email.isEmpty() || studentId.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            return;
        }

        if (userController.isUsernameExists(username)) {
            JOptionPane.showMessageDialog(this, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 邮箱格式验证
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "请输入有效的邮箱地址", "格式错误", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return;
        }
        // 创建用户对象
        User user = new User(username, password, email, studentId, name);

        // 注册用户
        if (userController.register(user)) {
            JOptionPane.showMessageDialog(this, "注册成功！请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "注册失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    // 邮箱格式验证方法
    private boolean isValidEmail(String email) {
        // 使用正则表达式验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailRegex);
        java.util.regex.Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
