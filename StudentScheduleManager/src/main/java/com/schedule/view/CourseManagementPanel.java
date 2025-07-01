package com.schedule.view;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
        
        // 设置按钮样式
        addButton.setBackground(new Color(46, 139, 87));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        
        editButton.setBackground(new Color(70, 130, 180));
        editButton.setForeground(Color.BLACK);
        editButton.setFocusPainted(false);
        
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFocusPainted(false);
        
        importButton.setBackground(new Color(255, 140, 0));
        importButton.setForeground(Color.BLACK);
        importButton.setFocusPainted(false);
        
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
                if (column == 7) { // 类型列
                    Object value = getValueAt(row, column);
                    if (value != null) {
                        String typeName = value.toString();
                        Course.CourseType type = null;
                        if (typeName.contains("必修")) type = Course.CourseType.REQUIRED;
                        else if (typeName.contains("选修")) type = Course.CourseType.ELECTIVE;
                        else if (typeName.contains("实验")) type = Course.CourseType.PRACTICAL;
                        else if (typeName.contains("研讨")) type = Course.CourseType.SEMINAR;
                        if (type != null) {
                            c.setBackground(type.getMacBgColor());
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(Color.WHITE);
                }
                c.setFont(new Font("PingFang SC", Font.PLAIN, 14));
                if (isCellSelected(row, column)) {
                    c.setBackground(new Color(0xD0E3FF));
                }
                return c;
            }
        };
        courseTable.setRowHeight(36);
        courseTable.setShowGrid(false);
        courseTable.setIntercellSpacing(new Dimension(0, 0));
        courseTable.setSelectionBackground(new Color(0xD0E3FF));
        courseTable.setSelectionForeground(Color.BLACK);
        courseTable.setFont(new Font("PingFang SC", Font.PLAIN, 14));
        courseTable.getTableHeader().setBackground(new Color(0xF7F7F7));
        courseTable.getTableHeader().setFont(new Font("PingFang SC", Font.BOLD, 15));
        courseTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        courseTable.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 工具栏
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        toolbarPanel.add(addButton);
        toolbarPanel.add(editButton);
        toolbarPanel.add(deleteButton);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(importButton);
        
        add(toolbarPanel, BorderLayout.NORTH);
        
        // 课程列表
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
        JOptionPane.showMessageDialog(this, "Excel导入功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
} 