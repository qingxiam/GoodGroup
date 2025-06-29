package com.schedule.util;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 课程提醒服务
 */
public class ReminderService {
    private static ReminderService instance;
    private Timer timer;
    private CourseController courseController;
    private User currentUser;
    private boolean isRunning = false;
    
    private ReminderService() {
        timer = new Timer(true);
        courseController = new CourseController();
    }
    
    public static ReminderService getInstance() {
        if (instance == null) {
            instance = new ReminderService();
        }
        return instance;
    }
    
    /**
     * 启动提醒服务
     */
    public void startReminderService(User user) {
        if (isRunning) {
            return;
        }
        
        this.currentUser = user;
        isRunning = true;
        
        // 每分钟检查一次
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkUpcomingCourses();
            }
        }, 0, 60000); // 60秒 = 1分钟
        
        System.out.println("提醒服务已启动");
    }
    
    /**
     * 停止提醒服务
     */
    public void stopReminderService() {
        if (timer != null) {
            timer.cancel();
            timer = new Timer(true);
        }
        isRunning = false;
        System.out.println("提醒服务已停止");
    }
    
    /**
     * 检查即将开始的课程
     */
    private void checkUpcomingCourses() {
        if (currentUser == null) {
            return;
        }
        
        List<Course> courses = courseController.getCoursesByUserId(currentUser.getId());
        LocalDateTime now = LocalDateTime.now();
        
        for (Course course : courses) {
            if (!course.isReminderEnabled()) {
                continue;
            }
            
            // 检查是否是今天的课程
            if (course.getDayOfWeek() == now.getDayOfWeek()) {
                LocalTime courseStartTime = course.getStartTime();
                LocalTime nowTime = now.toLocalTime();
                
                // 计算距离课程开始的时间（分钟）
                long minutesUntilCourse = ChronoUnit.MINUTES.between(nowTime, courseStartTime);
                
                // 如果课程在30分钟内开始，显示提醒
                if (minutesUntilCourse >= 0 && minutesUntilCourse <= 30) {
                    showReminder(course, minutesUntilCourse);
                }
            }
        }
    }
    
    /**
     * 显示课程提醒
     */
    private void showReminder(Course course, long minutesUntilCourse) {
        // 在事件调度线程中显示提醒
        SwingUtilities.invokeLater(() -> {
            String message = String.format(
                "课程提醒\n\n" +
                "课程：%s\n" +
                "教师：%s\n" +
                "地点：%s\n" +
                "时间：%s - %s\n" +
                "类型：%s\n\n" +
                "距离上课还有 %d 分钟",
                course.getName(),
                course.getTeacher(),
                course.getLocation(),
                course.getStartTime(),
                course.getEndTime(),
                course.getType().getDisplayName(),
                minutesUntilCourse
            );
            
            // 创建自定义提醒窗口
            JDialog reminderDialog = new JDialog((Frame) null, "课程提醒", false);
            reminderDialog.setLayout(new BorderLayout());
            
            // 设置窗口属性
            reminderDialog.setSize(400, 300);
            reminderDialog.setLocationRelativeTo(null);
            reminderDialog.setAlwaysOnTop(true);
            
            // 创建内容面板
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // 图标
            JLabel iconLabel = new JLabel("⏰");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 48));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(iconLabel, BorderLayout.NORTH);
            
            // 消息文本
            JTextArea messageArea = new JTextArea(message);
            messageArea.setEditable(false);
            messageArea.setLineWrap(true);
            messageArea.setWrapStyleWord(true);
            messageArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            messageArea.setBackground(reminderDialog.getBackground());
            
            JScrollPane scrollPane = new JScrollPane(messageArea);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            
            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton okButton = new JButton("知道了");
            JButton snoozeButton = new JButton("稍后提醒");
            
            okButton.addActionListener(e -> reminderDialog.dispose());
            snoozeButton.addActionListener(e -> {
                reminderDialog.dispose();
                // 5分钟后再次提醒
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        showReminder(course, 0);
                    }
                }, 5 * 60 * 1000); // 5分钟
            });
            
            buttonPanel.add(okButton);
            buttonPanel.add(snoozeButton);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            reminderDialog.add(contentPanel);
            
            // 显示提醒
            reminderDialog.setVisible(true);
            
            // 播放提示音（如果支持）
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (Exception e) {
                // 忽略提示音错误
            }
        });
    }
    
    /**
     * 检查提醒服务是否正在运行
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * 手动触发提醒检查
     */
    public void manualCheck() {
        if (isRunning) {
            checkUpcomingCourses();
        }
    }
} 