package com.schedule.view;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.schedule.util.TimeSlotUtil;

/**
 * 课程编辑对话框
 */
public class CourseDialog extends JDialog {
    private CourseController courseController;
    private User currentUser;
    private Course course;
    private boolean confirmed = false;
    
    // 表单组件
    private JTextField nameField;
    private JTextField teacherField;
    private JTextField locationField;
    private JComboBox<String> dayOfWeekComboBox;
    private JComboBox<String> startTimeSpinner;
    private JComboBox<String> endTimeSpinner;
    private JComboBox<Course.CourseType> typeComboBox;
    private JTextArea descriptionArea;
    private JCheckBox reminderCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    
    public CourseDialog(JFrame parent, CourseController courseController, User currentUser, Course course) {
        super(parent, course == null ? "添加课程" : "编辑课程", true);
        this.courseController = courseController;
        this.currentUser = currentUser;
        this.course = course;
        initComponents();
        setupLayout();
        setupListeners();
        loadCourseData();
    }
    
    private void initComponents() {
        setSize(420, 520);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(new Color(0xF7F7F7));
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0), 2, true));
        
        nameField = new JTextField(20);
        teacherField = new JTextField(20);
        locationField = new JTextField(20);
        
        // 星期选择
        String[] days = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        dayOfWeekComboBox = new JComboBox<>(days);
        
        // 时间段选择器
        String[] timeSlotOptions = TimeSlotUtil.getTimeSlotStrings();
        startTimeSpinner = new JComboBox<>(timeSlotOptions);
        endTimeSpinner = new JComboBox<>(timeSlotOptions);
        
        // 课程类型选择
        typeComboBox = new JComboBox<>(Course.CourseType.values());
        
        // 描述文本区域
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // 提醒复选框
        reminderCheckBox = new JCheckBox("启用提醒", true);
        
        // 按钮
        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");
        
        // 设置按钮样式
        saveButton.setBackground(new Color(0xE0E0E0));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("PingFang SC", Font.BOLD, 15));
        saveButton.setBorder(BorderFactory.createLineBorder(new Color(0xB0B0B0), 1, true));
        saveButton.setPreferredSize(new Dimension(90, 36));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelButton.setBackground(new Color(0xE0E0E0));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("PingFang SC", Font.BOLD, 15));
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(0xB0B0B0), 1, true));
        cancelButton.setPreferredSize(new Dimension(90, 36));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        nameField.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        teacherField.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        locationField.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        dayOfWeekComboBox.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        startTimeSpinner.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        endTimeSpinner.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        typeComboBox.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        descriptionArea.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        reminderCheckBox.setFont(new Font("PingFang SC", Font.PLAIN, 14));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 课程名称
        addFormField(mainPanel, "课程名称:", nameField, gbc, 0);
        
        // 教师
        addFormField(mainPanel, "教师:", teacherField, gbc, 1);
        
        // 地点
        addFormField(mainPanel, "地点:", locationField, gbc, 2);
        
        // 星期
        addFormField(mainPanel, "星期:", dayOfWeekComboBox, gbc, 3);
        
        // 开始节次
        addFormField(mainPanel, "开始节次:", startTimeSpinner, gbc, 4);
        
        // 结束节次
        addFormField(mainPanel, "结束节次:", endTimeSpinner, gbc, 5);
        
        // 课程类型
        addFormField(mainPanel, "课程类型:", typeComboBox, gbc, 6);
        
        // 描述
        JLabel descLabel = new JLabel("描述:");
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        mainPanel.add(descLabel, gbc);
        
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(250, 80));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(descScrollPane, gbc);
        
        // 提醒设置
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(reminderCheckBox, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        label.setForeground(new Color(0x333333));
        
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
        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> dispose());
        
        // 回车键保存
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void loadCourseData() {
        if (course != null) {
            // 编辑模式，加载现有数据
            nameField.setText(course.getName());
            teacherField.setText(course.getTeacher());
            locationField.setText(course.getLocation());
            
            // 设置星期
            String dayName = course.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE);
            dayOfWeekComboBox.setSelectedItem(dayName);
            
            // 设置时间（这里需要转换，简化处理）
            // 实际项目中需要更复杂的时间处理
            
            typeComboBox.setSelectedItem(course.getType());
            descriptionArea.setText(course.getDescription());
            reminderCheckBox.setSelected(course.isReminderEnabled());
        }
    }
    
    private void handleSave() {
        String name = nameField.getText().trim();
        String teacher = teacherField.getText().trim();
        String location = locationField.getText().trim();
        String dayName = (String) dayOfWeekComboBox.getSelectedItem();
        Course.CourseType type = (Course.CourseType) typeComboBox.getSelectedItem();
        String description = descriptionArea.getText().trim();
        boolean reminderEnabled = reminderCheckBox.isSelected();
        
        // 验证输入
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入课程名称", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 转换星期
        DayOfWeek dayOfWeek = null;
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getDisplayName(TextStyle.FULL, Locale.CHINESE).equals(dayName)) {
                dayOfWeek = day;
                break;
            }
        }
        
        // 获取时间段
        String startTimeSlot = (String) startTimeSpinner.getSelectedItem();
        String endTimeSlot = (String) endTimeSpinner.getSelectedItem();
        
        if (startTimeSlot == null || endTimeSlot == null) {
            JOptionPane.showMessageDialog(this, "请选择上课时间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 从时间段字符串中提取时间范围
        String startTimeRange = startTimeSlot.substring(startTimeSlot.indexOf(" ") + 1);
        String endTimeRange = endTimeSlot.substring(endTimeSlot.indexOf(" ") + 1);
        
        // 解析时间
        LocalTime startTime = null;
        LocalTime endTime = null;
        try {
            String[] startParts = startTimeRange.split("-");
            String[] endParts = endTimeRange.split("-");
            startTime = LocalTime.parse(startParts[0]);
            endTime = LocalTime.parse(endParts[1]);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "时间格式错误", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 创建或更新课程对象
        if (course == null) {
            course = new Course();
            course.setUserId(currentUser.getId());
        }
        
        course.setName(name);
        course.setTeacher(teacher);
        course.setLocation(location);
        course.setDayOfWeek(dayOfWeek);
        course.setStartTime(startTime);
        course.setEndTime(endTime);
        course.setType(type);
        course.setDescription(description);
        course.setReminderEnabled(reminderEnabled);
        
        // 验证课程信息
        String validationError = courseController.validateCourse(course);
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, validationError, "验证错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 保存课程
        boolean success = false;
        String errorMsg = null;
        try {
            if (course.getId() == 0) {
                success = courseController.addCourse(course);
            } else {
                success = courseController.updateCourse(course);
            }
        } catch (Exception ex) {
            errorMsg = ex.getMessage();
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, "保存成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            confirmed = true;
            dispose();
        } else {
            String msg = "保存失败";
            if (errorMsg != null) msg += ("\n" + errorMsg);
            JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
} 