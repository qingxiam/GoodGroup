package com.schedule.view;

import com.schedule.model.User;
import com.schedule.util.EmailService;
import com.schedule.util.MessageService;

import javax.swing.*;
import java.awt.*;

/**
 * 提醒设置对话框
 */
public class ReminderSettingsDialog extends JDialog {
    private User currentUser;
    private EmailService emailService;
    private MessageService messageService;
    
    // UI组件
    private JTextField smtpHostField;
    private JTextField smtpPortField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox emailEnabledCheckBox;
    private JCheckBox messageEnabledCheckBox;
    private JButton testEmailButton;
    private JButton testMessageButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    public ReminderSettingsDialog(JFrame parent, User user) {
        super(parent, "提醒设置", true);
        this.currentUser = user;
        this.emailService = EmailService.getInstance();
        this.messageService = MessageService.getInstance();
        
        initComponents();
        setupLayout();
        setupListeners();
        loadCurrentSettings();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        smtpHostField = new JTextField("smtp.qq.com", 20);
        smtpPortField = new JTextField("587", 10);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        emailEnabledCheckBox = new JCheckBox("启用邮件提醒", true);
        messageEnabledCheckBox = new JCheckBox("启用站内消息提醒", true);
        
        testEmailButton = new JButton("测试邮件发送");
        testMessageButton = new JButton("测试站内消息");
        saveButton = new JButton("保存设置");
        cancelButton = new JButton("取消");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 邮件服务器设置
        JPanel emailPanel = new JPanel(new GridBagLayout());
        emailPanel.setBorder(BorderFactory.createTitledBorder("邮件服务器设置"));
        
        gbc.gridx = 0; gbc.gridy = 0;
        emailPanel.add(new JLabel("SMTP服务器:"), gbc);
        gbc.gridx = 1;
        emailPanel.add(smtpHostField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        emailPanel.add(new JLabel("SMTP端口:"), gbc);
        gbc.gridx = 1;
        emailPanel.add(smtpPortField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        emailPanel.add(new JLabel("发件人邮箱:"), gbc);
        gbc.gridx = 1;
        emailPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        emailPanel.add(new JLabel("邮箱密码/授权码:"), gbc);
        gbc.gridx = 1;
        emailPanel.add(passwordField, gbc);
        
        // 提醒设置
        JPanel reminderPanel = new JPanel(new GridBagLayout());
        reminderPanel.setBorder(BorderFactory.createTitledBorder("提醒设置"));
        
        gbc.gridx = 0; gbc.gridy = 0;
        reminderPanel.add(emailEnabledCheckBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        reminderPanel.add(messageEnabledCheckBox, gbc);
        
        // 测试按钮
        JPanel testPanel = new JPanel(new GridBagLayout());
        testPanel.setBorder(BorderFactory.createTitledBorder("测试功能"));
        
        gbc.gridx = 0; gbc.gridy = 0;
        testPanel.add(testEmailButton, gbc);
        
        gbc.gridx = 1;
        testPanel.add(testMessageButton, gbc);
        
        // 添加到主面板
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(emailPanel, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(reminderPanel, gbc);
        
        gbc.gridy = 2;
        mainPanel.add(testPanel, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        testEmailButton.addActionListener(e -> testEmail());
        testMessageButton.addActionListener(e -> testMessage());
        saveButton.addActionListener(e -> saveSettings());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void loadCurrentSettings() {
        // 这里可以从配置文件或数据库加载当前设置
        // 暂时使用默认值
        emailField.setText(currentUser.getEmail());
    }
    
    private void testEmail() {
        if (!validateEmailSettings()) {
            return;
        }
        
        // 临时设置邮件配置
        String host = smtpHostField.getText().trim();
        int port = Integer.parseInt(smtpPortField.getText().trim());
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        emailService.setSmtpConfig(host, port, email, password);
        
        // 发送测试邮件
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return emailService.sendTestEmail(currentUser);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(ReminderSettingsDialog.this,
                            "测试邮件发送成功！请检查您的邮箱。",
                            "测试成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ReminderSettingsDialog.this,
                            "测试邮件发送失败，请检查邮箱配置。",
                            "测试失败", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReminderSettingsDialog.this,
                        "测试邮件发送失败：" + ex.getMessage(),
                        "测试失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void testMessage() {
        // 发送测试站内消息
        messageService.sendTestMessage(currentUser);
        
        JOptionPane.showMessageDialog(this,
            "测试站内消息已发送！\n\n" +
            "您可以在主界面的消息中心查看这条测试消息。",
            "测试成功", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean validateEmailSettings() {
        String host = smtpHostField.getText().trim();
        String portStr = smtpPortField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入SMTP服务器地址", "验证失败", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的SMTP端口号", "验证失败", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入发件人邮箱", "验证失败", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入邮箱密码或授权码", "验证失败", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void saveSettings() {
        if (!validateEmailSettings()) {
            return;
        }
        
        // 保存邮件配置
        String host = smtpHostField.getText().trim();
        int port = Integer.parseInt(smtpPortField.getText().trim());
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        emailService.setSmtpConfig(host, port, email, password);
        
        // 这里可以将设置保存到配置文件或数据库
        
        JOptionPane.showMessageDialog(this,
            "设置保存成功！\n\n" +
            "邮件服务器：" + host + ":" + port + "\n" +
            "发件人邮箱：" + email + "\n" +
            "邮件提醒：" + (emailEnabledCheckBox.isSelected() ? "启用" : "禁用") + "\n" +
            "站内消息：" + (messageEnabledCheckBox.isSelected() ? "启用" : "禁用"),
            "保存成功", JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
} 