package com.schedule.view;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * 课程管理面板
 */
public class CourseManagementPanel extends JPanel {
    private User currentUser;
    private CourseController courseController;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton importButton;
    
    public CourseManagementPanel(User user) {
        this.currentUser = user;
        this.courseController = new CourseController();
        initComponents();
        setupLayout();
        setupListeners();
        loadCourses();
    }
    
    private void initComponents() {
        addButton = new JButton("添加课程");
        editButton = new JButton("编辑课程");
        deleteButton = new JButton("删除课程");
        importButton = new JButton("导入Excel");
        
        // 优化按钮样式
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(new Color(30, 30, 30));
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addButton.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 1, true));
        
        editButton.setBackground(new Color(0, 123, 167));
        editButton.setForeground(new Color(30, 30, 30));
        editButton.setFocusPainted(false);
        editButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        editButton.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 167), 1, true));
        
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(new Color(30, 30, 30));
        deleteButton.setFocusPainted(false);
        deleteButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        deleteButton.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 1, true));
        
        importButton.setBackground(new Color(255, 140, 0));
        importButton.setForeground(new Color(30, 30, 30));
        importButton.setFocusPainted(false);
        importButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        importButton.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 1, true));
        
        // 创建表格模型
        String[] columnNames = {"ID", "课程名称", "教师", "地点", "星期", "开始时间", "结束时间", "类型", "描述"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        courseTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 250, 255) : Color.WHITE);
                } else {
                    c.setBackground(new Color(204, 232, 255));
                }
                c.setFont(new Font("微软雅黑", Font.PLAIN, 13));
                return c;
            }
        };
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.setSelectionBackground(new Color(0, 123, 167));
        courseTable.setSelectionForeground(Color.WHITE);
        courseTable.getTableHeader().setBackground(new Color(0, 123, 167));
        courseTable.getTableHeader().setForeground(Color.WHITE);
        courseTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 15));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        toolbarPanel.setBackground(new Color(240, 248, 255));
        
        toolbarPanel.add(addButton);
        toolbarPanel.add(editButton);
        toolbarPanel.add(deleteButton);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(importButton);
        
        add(toolbarPanel, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupListeners() {
        addButton.addActionListener(e -> addCourse());
        editButton.addActionListener(e -> editCourse());
        deleteButton.addActionListener(e -> deleteCourse());
        importButton.addActionListener(e -> importFromExcel());
    }
    
    private void loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = courseController.getCoursesByUserId(currentUser.getId());
        
        for (Course course : courses) {
            Object[] row = {
                course.getId(),
                course.getName(),
                course.getTeacher(),
                course.getLocation(),
                course.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE),
                course.getStartTime().toString(),
                course.getEndTime().toString(),
                course.getType().getDisplayName(),
                course.getDescription()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addCourse() {
        CourseDialog dialog = new CourseDialog((JFrame) SwingUtilities.getWindowAncestor(this), courseController, currentUser, null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadCourses();
        }
    }
    
    private void editCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Course course = courseController.getCourseById(courseId, currentUser.getId());
        
        if (course != null) {
            CourseDialog dialog = new CourseDialog((JFrame) SwingUtilities.getWindowAncestor(this), courseController, currentUser, course);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                loadCourses();
            }
        }
    }
    
    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要删除课程 \"" + courseName + "\" 吗？", 
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (courseController.deleteCourse(courseId, currentUser.getId())) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importFromExcel() {
        ExcelImportDialog dialog = new ExcelImportDialog((JFrame) SwingUtilities.getWindowAncestor(this), currentUser);
        dialog.setVisible(true);
        
        // 导入完成后刷新课程列表
        loadCourses();
    }
} 