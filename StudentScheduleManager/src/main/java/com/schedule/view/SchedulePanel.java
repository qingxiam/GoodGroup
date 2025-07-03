package com.schedule.view;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;
import com.schedule.util.TimeSlotUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * 课表查看面板（支持日视图日期切换）
 */
public class SchedulePanel extends JPanel {
    private User currentUser;
    private CourseController courseController;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> viewModeComboBox;
    private JComboBox<String> daySelector;
    private JButton refreshButton;
    private JButton exportButton;
    private DayOfWeek currentDisplayDay = DayOfWeek.MONDAY;

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
        daySelector = new JComboBox<>(new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"});
        daySelector.setEnabled(false);

        refreshButton = new JButton("刷新");
        exportButton = new JButton("导出");

        // 创建表格模型
        String[] columnNames = {"时间", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? String.class : Course.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setForeground(Color.BLACK);

                if (column == 0) {
                    c.setBackground(new Color(0xF7F7F7));
                    if (isCellSelected(row, column)) {
                        c.setBackground(new Color(0xD0E3FF));
                    }
                    return c;
                }

                Course course = (Course) getValueAt(row, column);
                if (course != null) {
                    Color bgColor = course.getType().getMacBgColor();
                    if (isCellSelected(row, column)) {
                        bgColor = new Color(
                                Math.min(bgColor.getRed() + 15, 255),
                                Math.min(bgColor.getGreen() + 15, 255),
                                Math.min(bgColor.getBlue() + 15, 255)
                        );
                    }
                    c.setBackground(bgColor);
                    ((JLabel)c).setText(String.format("<html><b>%s</b><br>%s<br>%s</html>",
                            course.getName(), course.getTeacher(), course.getLocation()));
                } else {
                    c.setBackground(isCellSelected(row, column) ? new Color(0xD0E3FF) : Color.WHITE);
                    ((JLabel)c).setText("");
                }
                return c;
            }
        };

        // 保持原有样式设置
        scheduleTable.setRowHeight(64);
        scheduleTable.setShowGrid(false);
        scheduleTable.setIntercellSpacing(new Dimension(0, 0));
        scheduleTable.setSelectionBackground(new Color(0xD0E3FF));
        scheduleTable.getTableHeader().setBackground(new Color(0xF7F7F7));
        scheduleTable.getTableHeader().setFont(new Font("PingFang SC", Font.BOLD, 15));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.add(new JLabel("视图模式:"));
        toolbarPanel.add(viewModeComboBox);
        toolbarPanel.add(new JLabel("选择星期:"));
        toolbarPanel.add(daySelector);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(refreshButton);
        toolbarPanel.add(exportButton);
        add(toolbarPanel, BorderLayout.NORTH);

        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        add(createLegendPanel(), BorderLayout.SOUTH);
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setBorder(BorderFactory.createTitledBorder("课程类型图例"));

        for (Course.CourseType type : Course.CourseType.values()) {
            JLabel label = new JLabel(type.getDisplayName());
            label.setOpaque(true);
            label.setBackground(type.getColor());
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            label.setPreferredSize(new Dimension(80, 20));
            panel.add(label);
        }
        return panel;
    }

    private void setupListeners() {
        viewModeComboBox.addActionListener(e -> {
            boolean isDayView = "日视图".equals(viewModeComboBox.getSelectedItem());
            daySelector.setEnabled(isDayView);
            loadSchedule();
        });

        daySelector.addActionListener(e -> {
            if (daySelector.isEnabled()) {
                currentDisplayDay = parseDayOfWeek((String)daySelector.getSelectedItem());
                loadSchedule();
            }
        });

        refreshButton.addActionListener(e -> loadSchedule());
        exportButton.addActionListener(e -> exportSchedule());
    }

    private void loadSchedule() {
        tableModel.setRowCount(0);
        List<Course> courses = courseController.getCoursesByUserId(currentUser.getId());

        if ("周视图".equals(viewModeComboBox.getSelectedItem())) {
            loadWeekView(courses);
        } else {
            loadDayView(courses);
        }
    }

    private void loadWeekView(List<Course> courses) {
        tableModel.setColumnIdentifiers(new String[]{"时间", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"});

        String[] timeSlots = TimeSlotUtil.getTimeSlotStrings();
        for (String timeSlot : timeSlots) {
            Object[] row = new Object[8];
            row[0] = timeSlot;
            for (int i = 1; i <= 7; i++) {
                row[i] = getCourseForDayAndTime(courses, DayOfWeek.of(i), timeSlot);
            }
            tableModel.addRow(row);
        }
    }

    private void loadDayView(List<Course> courses) {
        tableModel.setColumnIdentifiers(new String[]{"时间", currentDisplayDay.getDisplayName(TextStyle.FULL, Locale.CHINESE)});

        String[] timeSlots = TimeSlotUtil.getTimeSlotStrings();
        for (String timeSlot : timeSlots) {
            Object[] row = new Object[2];
            row[0] = timeSlot;
            row[1] = getCourseForDayAndTime(courses, currentDisplayDay, timeSlot);
            tableModel.addRow(row);
        }
    }

    private Course getCourseForDayAndTime(List<Course> courses, DayOfWeek day, String timeSlot) {
        String timeRange = timeSlot.substring(timeSlot.indexOf(" ") + 1);
        return courses.stream()
                .filter(c -> c.getDayOfWeek() == day &&
                        (c.getStartTime() + "-" + c.getEndTime()).equals(timeRange))
                .findFirst()
                .orElse(null);
    }

    private DayOfWeek parseDayOfWeek(String dayName) {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.getDisplayName(TextStyle.FULL, Locale.CHINESE).equals(dayName)) {
                return day;
            }
        }
        return DayOfWeek.MONDAY;
    }

    private void exportSchedule() {
        JOptionPane.showMessageDialog(this, "导出功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}