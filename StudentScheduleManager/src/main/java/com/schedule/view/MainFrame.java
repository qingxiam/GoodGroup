package com.schedule.view;

import com.schedule.model.User;
import com.schedule.util.ReminderService;
import com.schedule.util.MessageService;

import javax.swing.*;
import java.awt.*;

/**
 * 主界面
 */
public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private SchedulePanel schedulePanel;
    private CourseManagementPanel courseManagementPanel;
    private UserProfilePanel userProfilePanel;
    private MessageCenterPanel messageCenterPanel;
    private ReminderService reminderService;
    private int userId;
    
    public MainFrame(User user) {
        this.currentUser = user;
        this.reminderService = ReminderService.getInstance();
        initComponents();
        setupLayout();
        setupMenuBar();
        startReminderService();
    }
    
    private void initComponents() {
        setTitle("学生个人课表管理系统 - " + currentUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        tabbedPane = new JTabbedPane();
        schedulePanel = new SchedulePanel(currentUser);
        courseManagementPanel = new CourseManagementPanel(currentUser);
        userProfilePanel = new UserProfilePanel(currentUser);
        messageCenterPanel = new MessageCenterPanel(currentUser);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 添加选项卡
        tabbedPane.addTab("课表查看", new ImageIcon(), schedulePanel, "查看和管理课程表");
        tabbedPane.addTab("课程管理", new ImageIcon(), courseManagementPanel, "添加、编辑和删除课程");
        tabbedPane.addTab("个人资料", new ImageIcon(), userProfilePanel, "管理个人信息");
        tabbedPane.addTab("消息中心", new ImageIcon(), messageCenterPanel, "查看和管理站内消息");
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // 添加状态栏
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel(" 欢迎使用学生个人课表管理系统！当前用户：" + currentUser.getName());
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // 添加提醒状态指示器
        JLabel reminderLabel = new JLabel(" 提醒服务：运行中 ");
        reminderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        reminderLabel.setForeground(new Color(46, 139, 87));
        statusBar.add(reminderLabel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        JMenuItem importItem = new JMenuItem("导入Excel");
        JMenuItem exportItem = new JMenuItem("导出课表");
        JMenuItem exitItem = new JMenuItem("退出");
        
        importItem.addActionListener(e -> importFromExcel());
        exportItem.addActionListener(e -> exportSchedule());
        exitItem.addActionListener(e -> exitApplication());
        
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 工具菜单
        JMenu toolsMenu = new JMenu("工具");
        JMenuItem settingsItem = new JMenuItem("设置");
        JMenuItem reminderItem = new JMenuItem("提醒设置");
        JMenuItem testReminderItem = new JMenuItem("测试提醒");
        
        settingsItem.addActionListener(e -> openSettings());
        reminderItem.addActionListener(e -> openReminderSettings());
        testReminderItem.addActionListener(e -> testReminder());
        
        toolsMenu.add(settingsItem);
        toolsMenu.add(reminderItem);
        toolsMenu.addSeparator();
        toolsMenu.add(testReminderItem);
        
        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        
        aboutItem.addActionListener(e -> showAbout());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void startReminderService() {
        reminderService.startReminderService(currentUser);
    }
    
    private void importFromExcel() {
        JOptionPane.showMessageDialog(this, "Excel导入功能将在课程管理面板中实现", "提示", JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(1); // 切换到课程管理选项卡
    }
    
    private void exportSchedule() {
        JOptionPane.showMessageDialog(this, "导出功能将在课表查看面板中实现", "提示", JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(0); // 切换到课表查看选项卡
    }
    
    private void openSettings() {
        JOptionPane.showMessageDialog(this, "设置功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openReminderSettings() {
        ReminderSettingsDialog dialog = new ReminderSettingsDialog(this, currentUser);
        dialog.setVisible(true);
    }
    
    private void testReminder() {
        String[] options = {"弹窗提醒", "邮件提醒", "站内消息", "全部测试"};
        int choice = JOptionPane.showOptionDialog(this,
            "请选择要测试的提醒方式：",
            "测试提醒功能",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[3]);
        
        if (choice == 0) {
            // 弹窗提醒
            reminderService.manualCheck();
        } else if (choice == 1) {
            // 邮件提醒
            ReminderSettingsDialog dialog = new ReminderSettingsDialog(this, currentUser);
            dialog.setVisible(true);
        } else if (choice == 2) {
            // 站内消息
            MessageService.getInstance().sendTestMessage(currentUser);
            JOptionPane.showMessageDialog(this,
                "测试站内消息已发送！请切换到消息中心查看。",
                "测试成功", JOptionPane.INFORMATION_MESSAGE);
            tabbedPane.setSelectedIndex(3); // 切换到消息中心
        } else if (choice == 3) {
            // 全部测试
            reminderService.manualCheck();
            MessageService.getInstance().sendTestMessage(currentUser);
            JOptionPane.showMessageDialog(this,
                "已发送弹窗提醒和站内消息！\n如需测试邮件，请使用提醒设置功能。",
                "测试完成", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要退出应用程序吗？", 
            "确认退出", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // 停止提醒服务
            reminderService.stopReminderService();
            System.exit(0);
        }
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this, 
            "学生个人课表管理系统 v1.0\n\n" +
            "功能特性：\n" +
            "• 用户注册和登录\n" +
            "• 课程信息管理\n" +
            "• 课表可视化展示\n" +
            "• Excel批量导入\n" +
            "• 课程提醒功能\n\n" +
            "开发者：学生个人课表管理系统开发团队", 
            "关于", JOptionPane.INFORMATION_MESSAGE);
    }
} 