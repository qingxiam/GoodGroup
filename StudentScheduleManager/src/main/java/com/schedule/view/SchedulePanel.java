package com.schedule.view;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * 课表查看面板
 */
public class SchedulePanel extends JPanel {
    private User currentUser;
    private CourseController courseController;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> viewModeComboBox;
    private JButton refreshButton;
    private JButton exportButton;
    
    public SchedulePanel(User user) {
        this.currentUser = user;
        this.courseController = new CourseController();
        initComponents();
        setupLayout();
        setupListeners();
        loadSchedule();
    }
    
    private void initComponents() {
        viewModeComboBox = new JComboBox<>(new String[]{"周视图", "日视图"});
        refreshButton = new JButton("刷新");
        exportButton = new JButton("导出");
        
        // 优化按钮样式
        refreshButton.setBackground(new Color(0, 123, 167));
        refreshButton.setForeground(new Color(30, 30, 30));
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        refreshButton.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 167), 1, true));
        
        exportButton.setBackground(new Color(46, 204, 113));
        exportButton.setForeground(new Color(30, 30, 30));
        exportButton.setFocusPainted(false);
        exportButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        exportButton.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 1, true));
        
        // 优化下拉框样式
        viewModeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        viewModeComboBox.setBackground(Color.WHITE);
        viewModeComboBox.setForeground(new Color(30, 30, 30));
        
        // 创建表格模型 - 更新为新的课表结构
        String[] columnNames = {"节次/星期", "时间", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // 优化表格样式
        scheduleTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 250, 255) : Color.WHITE);
                    c.setForeground(new Color(50, 50, 50));
                } else {
                    c.setBackground(new Color(0, 123, 167));
                    c.setForeground(Color.WHITE);
                }
                c.setFont(new Font("微软雅黑", Font.PLAIN, 13));
                return c;
            }
        };
        scheduleTable.setRowHeight(80);
        scheduleTable.setGridColor(new Color(220, 220, 220));
        scheduleTable.setShowGrid(true);
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.setSelectionBackground(new Color(0, 123, 167));
        scheduleTable.setSelectionForeground(Color.WHITE);
        
        // 设置表格表头样式 - 深蓝色背景，白色字体
        scheduleTable.getTableHeader().setBackground(new Color(0, 123, 167));
        scheduleTable.getTableHeader().setForeground(Color.WHITE);
        scheduleTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 15));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 工具栏 - 浅蓝色背景
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        toolbarPanel.setBackground(new Color(240, 248, 255));
        
        // 标签 - 深蓝色字体
        JLabel viewLabel = new JLabel("视图模式:");
        viewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        viewLabel.setForeground(new Color(0, 123, 167));
        
        toolbarPanel.add(viewLabel);
        toolbarPanel.add(viewModeComboBox);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(refreshButton);
        toolbarPanel.add(exportButton);
        
        add(toolbarPanel, BorderLayout.NORTH);
        
        // 课表区域
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // 图例面板 - 浅蓝色背景
        JPanel legendPanel = createLegendPanel();
        legendPanel.setBackground(new Color(240, 248, 255));
        add(legendPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBorder(BorderFactory.createTitledBorder("课程类型图例"));
        legendPanel.setBackground(new Color(240, 248, 255));
        
        for (Course.CourseType type : Course.CourseType.values()) {
            JPanel colorPanel = new JPanel();
            colorPanel.setPreferredSize(new Dimension(20, 20));
            colorPanel.setBackground(type.getColor());
            colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            // 图例标签 - 深灰色字体
            JLabel label = new JLabel(type.getDisplayName());
            label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            label.setForeground(new Color(60, 60, 60));
            
            legendPanel.add(colorPanel);
            legendPanel.add(label);
            legendPanel.add(Box.createHorizontalStrut(10));
        }
        
        return legendPanel;
    }
    
    private void setupListeners() {
        refreshButton.addActionListener(e -> loadSchedule());
        exportButton.addActionListener(e -> exportSchedule());
        viewModeComboBox.addActionListener(e -> loadSchedule());
    }
    
    private void loadSchedule() {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 获取课程数据
        List<Course> courses = courseController.getCoursesByUserId(currentUser.getId());
        
        if (viewModeComboBox.getSelectedItem().equals("周视图")) {
            loadWeekView(courses);
        } else {
            loadDayView(courses);
        }
    }
    
    private void loadWeekView(List<Course> courses) {
        // 定义时间段 - 根据用户提供的课程时间表
        String[] timeSlots = {
            "第1节", "第2节", "第3节", "第4节", "中午", "第5节", "第6节", "第7节", "第8节", "第9节", "第10节", "第11节"
        };
        
        String[] timeRanges = {
            "08:00~08:45", "08:55~09:40", "10:10~10:55", "11:05~11:50", "12:00~14:00", 
            "14:30~15:15", "15:25~16:10", "16:40~17:25", "17:35~18:20", "19:10~19:55", 
            "20:05~20:50", "21:00~21:45"
        };
        
        for (int i = 0; i < timeSlots.length; i++) {
            Object[] row = new Object[9]; // 9列：节次/星期、时间、星期日到星期六
            row[0] = timeSlots[i];
            row[1] = timeRanges[i];
            
            // 为每个时间段填充课程
            for (int j = 2; j <= 8; j++) {
                DayOfWeek dayOfWeek = DayOfWeek.of(j == 2 ? 7 : j - 1); // 星期日是第7天
                String courseInfo = getCourseInfoForTimeSlot(courses, dayOfWeek, timeRanges[i]);
                row[j] = courseInfo;
            }
            
            tableModel.addRow(row);
        }
    }
    
    private void loadDayView(List<Course> courses) {
        // 获取当前选中的星期
        DayOfWeek selectedDay = DayOfWeek.MONDAY; // 默认显示星期一
        
        String[] timeSlots = {
            "08:00-09:40", "10:00-11:40", "14:00-15:40", "16:00-17:40", "19:00-20:40"
        };
        
        for (String timeSlot : timeSlots) {
            Object[] row = new Object[2];
            row[0] = timeSlot;
            row[1] = getCourseInfoForTimeSlot(courses, selectedDay, timeSlot);
            tableModel.addRow(row);
        }
        
        // 更新列标题
        String dayName = selectedDay.getDisplayName(TextStyle.FULL, Locale.CHINESE);
        tableModel.setColumnIdentifiers(new String[]{"时间", dayName});
    }
    
    private String getCourseInfoForTimeSlot(List<Course> courses, DayOfWeek dayOfWeek, String timeSlot) {
        for (Course course : courses) {
            if (course.getDayOfWeek() == dayOfWeek) {
                String courseTime = course.getStartTime() + "~" + course.getEndTime();
                if (courseTime.equals(timeSlot)) {
                    return String.format("<html><b>%s</b><br>%s<br>%s</html>", 
                        course.getName(), course.getTeacher(), course.getLocation());
                }
            }
        }
        return "";
    }
    
    private void exportSchedule() {
        JOptionPane.showMessageDialog(this, "导出功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
} 