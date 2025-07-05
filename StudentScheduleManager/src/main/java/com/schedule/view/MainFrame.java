package com.schedule.view;

import com.schedule.model.User;
import com.schedule.util.ReminderService;
import com.schedule.util.MessageService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 主窗口 - 孤独摇滚风格
 */
public class MainFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private SchedulePanel schedulePanel;
    private CourseManagementPanel courseManagementPanel;
    private UserProfilePanel userProfilePanel;
    private MessageCenterPanel messageCenterPanel;
    private ReminderService reminderService;
    private MessageService messageService;
    
    // 孤独摇滚主题相关
    private boolean bocchiThemeEnabled = false;
    private BufferedImage backgroundImage;
    private Color bocchiAccentColor = new Color(255, 105, 180); // 粉色
    private Color bocchiSecondaryColor = new Color(138, 43, 226); // 紫色
    private Color bocchiDarkColor = new Color(45, 45, 45); // 深色

    public MainFrame(User user) {
        this.currentUser = user;
        this.reminderService = ReminderService.getInstance();
        this.messageService = MessageService.getInstance();
        
        loadBocchiResources();
        initComponents();
        setupLayout();
        setupMenuBar();
        setupListeners();
        
        // 启动提醒服务
        reminderService.startReminderService(currentUser);
        
        setTitle("学生课程管理系统 - " + currentUser.getUsername());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * 加载孤独摇滚资源
     */
    private void loadBocchiResources() {
        try {
            File bgFile = new File("resources/backgrounds/bocchi_main_bg.jpg");
            if (bgFile.exists()) {
                backgroundImage = ImageIO.read(bgFile);
            }
        } catch (IOException e) {
            System.out.println("无法加载主窗口背景图片: " + e.getMessage());
        }
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        schedulePanel = new SchedulePanel(currentUser);
        courseManagementPanel = new CourseManagementPanel(currentUser);
        userProfilePanel = new UserProfilePanel(currentUser);
        messageCenterPanel = new MessageCenterPanel(currentUser);
    }
    


    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 设置背景
        if (bocchiThemeEnabled && backgroundImage != null) {
            setContentPane(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                    g2d.dispose();
                }
            });
        }

        // 添加标签页
        tabbedPane.addTab("课表", new ImageIcon("resources/icons/bocchi_schedule.png"), schedulePanel);
        tabbedPane.addTab("课程管理", new ImageIcon("resources/icons/bocchi_course.png"), courseManagementPanel);
        tabbedPane.addTab("个人资料", new ImageIcon("resources/icons/bocchi_profile.png"), userProfilePanel);
        tabbedPane.addTab("消息中心", new ImageIcon("resources/icons/bocchi_message.png"), messageCenterPanel);
        
        // 设置标签页字体
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        // 设置标签页样式
        if (bocchiThemeEnabled) {
            tabbedPane.setOpaque(false);
            tabbedPane.setBackground(new Color(255, 255, 255, 180));
            tabbedPane.setForeground(bocchiDarkColor);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        if (bocchiThemeEnabled) {
            menuBar.setBackground(new Color(45, 45, 45, 200));
            menuBar.setForeground(Color.WHITE);
        }

        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JMenuItem importItem = new JMenuItem("导入Excel");
        importItem.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JMenuItem exportItem = new JMenuItem("导出Excel");
        exportItem.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        importItem.addActionListener(e -> importFromExcel());
        exportItem.addActionListener(e -> exportToExcel());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 工具菜单
        JMenu toolsMenu = new JMenu("工具");
        toolsMenu.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JMenuItem reminderItem = new JMenuItem("提醒设置");
        reminderItem.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JMenuItem themeItem = new JMenuItem("主题设置");
        themeItem.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        reminderItem.addActionListener(e -> openReminderSettings());
        themeItem.addActionListener(e -> openThemeSettings());
        
        toolsMenu.add(reminderItem);
        toolsMenu.add(themeItem);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JMenuItem helpItem = new JMenuItem("使用帮助");
        helpItem.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        aboutItem.addActionListener(e -> showAboutDialog());
        helpItem.addActionListener(e -> showHelpDialog());
        
        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }

    private void setupListeners() {
        // 主题切换功能已移至课表面板
    }

    private void importFromExcel() {
        JOptionPane.showMessageDialog(this, "Excel导入功能将在课程管理面板中实现", "提示", JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(1); // 切换到课程管理选项卡
    }

    private void exportToExcel() {
        JOptionPane.showMessageDialog(this, "导出功能将在课表查看面板中实现", "提示", JOptionPane.INFORMATION_MESSAGE);
        tabbedPane.setSelectedIndex(0); // 切换到课表查看选项卡
    }

    private void openReminderSettings() {
        ReminderSettingsDialog dialog = new ReminderSettingsDialog(this, currentUser);
        dialog.setVisible(true);
    }

    private void openThemeSettings() {
        JOptionPane.showMessageDialog(this, 
            "孤独摇滚主题设置\n\n" +
            "当前支持的功能：\n" +
            "• 点击孤独摇滚按钮启用主题\n" +
            "• 在课表面板中查看孤独摇滚风格的课程显示\n" +
            "• 自定义背景图片请参考 resources/backgrounds/README.md\n\n" +
            "孤独摇滚配色方案：\n" +
            "• 主色调：粉黄蓝红\n" +
            "• 浅色背景：白色", 
            "主题设置", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "学生课程管理系统 v2.1.0\n\n" +
            "功能特色：\n" +
            "• 支持连堂课导入和显示\n" +
            "• 孤独摇滚风格主题\n" +
            "• 课程提醒功能\n" +
            "• Excel导入\n\n" +
            "开发者：GoodGroup团队\n" +
            "主题：孤独摇滚风格\n" +
            "© 厦门大学信息学院软工2023级学生",
            "关于",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelpDialog() {
        JOptionPane.showMessageDialog(this,
            "使用帮助\n\n" +
            "1. 课表查看：\n" +
            "   • 支持周视图和日视图切换\n" +
            "   • 连堂课会在所有覆盖时间段显示\n\n" +
            "2. 课程管理：\n" +
            "   • 手动添加课程\n" +
            "   • Excel批量导入（支持连堂课）\n" +
            "   • 课程编辑和删除\n\n" +
            "3. 孤独摇滚主题：\n" +
            "   • 点击孤独摇滚启用主题\n" +
            "   • 自定义背景图片请查看resources/backgrounds/README.md\n" +
            "   • 粉黄蓝红配色方案\n\n" +
            "4. 提醒功能：\n" +
            "   • 邮件提醒设置\n" +
            "   • 消息中心查看提醒记录\n\n" +
            "享受你的课表体验！",
            "使用帮助",
            JOptionPane.INFORMATION_MESSAGE);
    }
} 