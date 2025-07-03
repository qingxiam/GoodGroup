package com.schedule.view;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;
import com.schedule.util.TimeSlotUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
        
        // 设置按钮样式
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        
        exportButton.setBackground(new Color(46, 139, 87));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFocusPainted(false);
        
        // 创建表格模型
        String[] columnNames = {"时间", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        scheduleTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == 0) {
                    c.setBackground(new Color(0xF7F7F7));
                    c.setFont(new Font("PingFang SC", Font.PLAIN, 14));
                } else {
                    Object value = getValueAt(row, column);
                    if (value != null && value.toString().contains("<b>")) {
                        // 解析课程类型
                        String html = value.toString();
                        String courseName = html.replaceAll("<[^>]*>", "").split("\n")[0];
                        Course.CourseType type = null;
                        if (courseName.contains("必修")) type = Course.CourseType.REQUIRED;
                        else if (courseName.contains("选修")) type = Course.CourseType.ELECTIVE;
                        else if (courseName.contains("实验")) type = Course.CourseType.PRACTICAL;
                        else if (courseName.contains("研讨")) type = Course.CourseType.SEMINAR;
                        if (type != null) {
                            c.setBackground(type.getMacBgColor());
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                    c.setFont(new Font("PingFang SC", Font.PLAIN, 14));
                }
                if (isCellSelected(row, column)) {
                    c.setBackground(new Color(0xD0E3FF));
                }
                return c;
            }
        };
        scheduleTable.setRowHeight(64);
        scheduleTable.setShowGrid(false);
        scheduleTable.setIntercellSpacing(new Dimension(0, 0));
        scheduleTable.setSelectionBackground(new Color(0xD0E3FF));
        scheduleTable.setSelectionForeground(Color.BLACK);
        scheduleTable.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        scheduleTable.getTableHeader().setBackground(new Color(0xF7F7F7));
        scheduleTable.getTableHeader().setFont(new Font("PingFang SC", Font.BOLD, 15));
        scheduleTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        scheduleTable.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 工具栏
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel viewLabel = new JLabel("视图模式:");
        viewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
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
        
        // 图例面板
        JPanel legendPanel = createLegendPanel();
        add(legendPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBorder(BorderFactory.createTitledBorder("课程类型图例"));
        
        for (Course.CourseType type : Course.CourseType.values()) {
            JPanel colorPanel = new JPanel();
            colorPanel.setPreferredSize(new Dimension(20, 20));
            colorPanel.setBackground(type.getColor());
            colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            
            JLabel label = new JLabel(type.getDisplayName());
            label.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            
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
        // 使用新的时间段设置
        String[] timeSlots = TimeSlotUtil.getTimeSlotStrings();
        
        for (String timeSlot : timeSlots) {
            Object[] row = new Object[8];
            row[0] = timeSlot;
            
            // 为每个时间段填充课程
            for (int i = 1; i <= 7; i++) {
                DayOfWeek dayOfWeek = DayOfWeek.of(i);
                String courseInfo = getCourseInfoForTimeSlot(courses, dayOfWeek, timeSlot);
                row[i] = courseInfo;
            }
            
            tableModel.addRow(row);
        }
    }
    
    private void loadDayView(List<Course> courses) {
        // 获取当前选中的星期
        DayOfWeek selectedDay = DayOfWeek.MONDAY; // 默认显示星期一
        
        String[] timeSlots = TimeSlotUtil.getTimeSlotStrings();
        
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
                String courseTime = course.getStartTime() + "-" + course.getEndTime();
                // 从时间段字符串中提取时间范围进行匹配
                String timeRange = timeSlot.substring(timeSlot.indexOf(" ") + 1);
                if (courseTime.equals(timeRange)) {
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