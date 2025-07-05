package com.schedule.view;

import com.schedule.controller.CourseController;
import com.schedule.model.Course;
import com.schedule.model.User;
import com.schedule.util.TimeSlotUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * 课表查看面板（支持日视图日期切换 + 孤独摇滚风格）
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
    private JButton themeButton;
    private DayOfWeek currentDisplayDay = DayOfWeek.MONDAY;
    
    // 孤独摇滚风格相关
    private boolean bocchiThemeEnabled = false;
    private BufferedImage backgroundImage;
    private Map<Course.CourseType, BufferedImage> courseTypeBackgrounds;
    private Color bocchiTextColor = new Color(255, 255, 255);
    private Color bocchiPanelColor = new Color(45, 45, 45, 200);
    private Color bocchiAccentColor = new Color(255, 105, 180); // 粉色
    private Color bocchiSecondaryColor = new Color(138, 43, 226); // 紫色
    private static final Color bocchiWhiteBg = new Color(255, 255, 255, 100);
    private static final Color bocchiHeaderBg = new Color(255, 255, 255, 100);

    public SchedulePanel(User user) {
        this.currentUser = user;
        this.courseController = new CourseController();
        loadBocchiResources();
        initComponents();
        setupLayout();
        setupListeners();
        loadSchedule();
    }
    
    /**
     * 加载孤独摇滚资源
     */
    private void loadBocchiResources() {
        courseTypeBackgrounds = new HashMap<>();
        
        try {
            // 加载主背景图片
            File bgFile = new File("resources/backgrounds/bocchi_main_bg.jpg");
            if (bgFile.exists()) {
                backgroundImage = ImageIO.read(bgFile);
            }
            
            // 加载各课程类型背景
            loadCourseTypeBackground(Course.CourseType.REQUIRED, "resources/backgrounds/bocchi_required_bg.jpg");
            loadCourseTypeBackground(Course.CourseType.ELECTIVE, "resources/backgrounds/bocchi_elective_bg.jpg");
            loadCourseTypeBackground(Course.CourseType.PRACTICAL, "resources/backgrounds/bocchi_practical_bg.jpg");
            loadCourseTypeBackground(Course.CourseType.SEMINAR, "resources/backgrounds/bocchi_seminar_bg.jpg");
            
        } catch (IOException e) {
            System.out.println("无法加载孤独摇滚背景图片: " + e.getMessage());
            // 使用默认颜色背景
        }
    }
    
    /**
     * 加载课程类型背景
     */
    private void loadCourseTypeBackground(Course.CourseType type, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                courseTypeBackgrounds.put(type, ImageIO.read(file));
            }
        } catch (IOException e) {
            System.out.println("无法加载" + type.getDisplayName() + "背景: " + e.getMessage());
        }
    }

    private void initComponents() {
        viewModeComboBox = new JComboBox<>(new String[]{"周视图", "日视图"});
        
        daySelector = new JComboBox<>(new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"});
        daySelector.setEnabled(false);

        refreshButton = new JButton("🔄 刷新");
        
        exportButton = new JButton("📤 导出");
        
        themeButton = new JButton("🎸 孤独摇滚");

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
                
                // 设置所有单元格文字居中
                if (c instanceof JLabel) {
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel)c).setVerticalAlignment(SwingConstants.CENTER);
                }
                
                if (column == 0) {
                    if (bocchiThemeEnabled) {
                        c.setForeground(Color.BLACK);
                        c.setBackground(bocchiWhiteBg);
                    } else {
                        c.setForeground(Color.BLACK);
                        c.setBackground(new Color(0xF7F7F7));
                    }
                    if (isCellSelected(row, column)) {
                        c.setBackground(new Color(0xD0E3FF));
                    }
                    return c;
                }

                Course course = (Course) getValueAt(row, column);
                if (course != null) {
                    if (bocchiThemeEnabled) {
                        // 孤独摇滚风格渲染
                        renderBocchiCourseCell(c, course, isCellSelected(row, column));
                    } else {
                        // 默认主题渲染
                        Color bgColor = course.getType().getMacBgColor();
                        if (isCellSelected(row, column)) {
                            bgColor = new Color(
                                    Math.min(bgColor.getRed() + 15, 255),
                                    Math.min(bgColor.getGreen() + 15, 255),
                                    Math.min(bgColor.getBlue() + 15, 255)
                            );
                        }
                        c.setBackground(bgColor);
                        c.setForeground(getContrastColor(bgColor));
                        ((JLabel)c).setText(String.format("<html><div style='text-align: center;'><b>%s</b><br>%s<br>%s</div></html>",
                                course.getName(), course.getTeacher(), course.getLocation()));
                    }
                } else {
                    if (bocchiThemeEnabled) {
                        c.setBackground(bocchiWhiteBg);
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(isCellSelected(row, column) ? new Color(0xD0E3FF) : Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                    ((JLabel)c).setText("");
                }
                return c;
            }
        };
        
        // 设置自定义单元格渲染器
        scheduleTable.setDefaultRenderer(Course.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof Course) {
                    Course course = (Course) value;
                    if (bocchiThemeEnabled) {
                        return createBocchiCourseRenderer(course, isSelected);
                    } else {
                        // 默认主题渲染
                        Color bgColor = course.getType().getMacBgColor();
                        if (isSelected) {
                            bgColor = new Color(
                                    Math.min(bgColor.getRed() + 15, 255),
                                    Math.min(bgColor.getGreen() + 15, 255),
                                    Math.min(bgColor.getBlue() + 15, 255)
                            );
                        }
                        c.setBackground(bgColor);
                        c.setForeground(getContrastColor(bgColor));
                        ((JLabel)c).setText(String.format("<html><div style='text-align: center;'><b>%s</b><br>%s<br>%s</div></html>",
                                course.getName(), course.getTeacher(), course.getLocation()));
                    }
                }
                return c;
            }
        });

        // 保持原有样式设置
        scheduleTable.setRowHeight(80);
        scheduleTable.setShowGrid(false);
        scheduleTable.setIntercellSpacing(new Dimension(0, 0));
        scheduleTable.setSelectionBackground(new Color(0xD0E3FF));
        scheduleTable.getTableHeader().setBackground(new Color(0xF7F7F7));
        
        // 设置表头居中
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        scheduleTable.getTableHeader().setDefaultRenderer(headerRenderer);
        
        // 设置透明背景
        scheduleTable.setOpaque(false);
        scheduleTable.getTableHeader().setOpaque(false);
        
        // 设置孤独摇滚风格的表头
        if (bocchiThemeEnabled) {
            scheduleTable.getTableHeader().setBackground(new Color(45, 45, 45, 180));
            scheduleTable.getTableHeader().setForeground(bocchiTextColor);
        }
        
        // 设置表头渲染器为透明
        scheduleTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                    if (bocchiThemeEnabled) {
                        c.setBackground(bocchiHeaderBg);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });
    }
    
    /**
     * 创建孤独摇滚风格课程渲染器
     */
    private Component createBocchiCourseRenderer(Course course, boolean isSelected) {
        BufferedImage bgImage = courseTypeBackgrounds.get(course.getType());
        
        if (bgImage != null) {
            // 创建带背景图片的面板
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    
                    // 启用抗锯齿
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // 绘制背景图片
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                    
                    // 添加孤独摇滚风格遮罩
                    g2d.setColor(new Color(0, 0, 0, isSelected ? 60 : 100));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // 添加粉色边框效果
                    g2d.setColor(bocchiAccentColor);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(1, 1, getWidth()-2, getHeight()-2);
                    
                    g2d.dispose();
                }
            };
            panel.setLayout(new BorderLayout());
            panel.setOpaque(false);
            
            // 创建课程信息标签
            JLabel label = new JLabel(String.format("<html><div style='text-align: center;'><b style='color: black; font-size: 12px;'>%s</b><br><span style='color: black; font-size: 10px;'>%s</span><br><span style='color: black; font-size: 10px;'>%s</span></div></html>",
                    course.getName(), course.getTeacher(), course.getLocation()));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);
            label.setForeground(Color.BLACK);
            panel.add(label, BorderLayout.CENTER);
            
            return panel;
        } else {
            // 使用孤独摇滚配色
            Color baseColor = getBocchiColorForCourseType(course.getType());
            Color bocchiColor = new Color(
                    baseColor.getRed(),
                    baseColor.getGreen(),
                    baseColor.getBlue(),
                    220
            );
            
            JLabel label = new JLabel(String.format("<html><div style='text-align: center;'><b>%s</b><br><span style='color: black;'>%s</span><br><span style='color: black;'>%s</span></div></html>",
                    course.getName(), course.getTeacher(), course.getLocation()));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setBackground(bocchiColor);
            label.setForeground(Color.BLACK);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            
            return label;
        }
    }
    
    /**
     * 渲染孤独摇滚风格课程单元格
     */
    private void renderBocchiCourseCell(Component c, Course course, boolean isSelected) {
        BufferedImage bgImage = courseTypeBackgrounds.get(course.getType());
        
        if (bgImage != null) {
            // 设置背景图片和样式
            if (c instanceof JComponent) {
                ((JComponent)c).setOpaque(false);
            }
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                label.setText(String.format("<html><div style='text-align: center;'><b style='color: black; font-size: 12px;'>%s</b><br><span style='color: black; font-size: 10px;'>%s</span><br><span style='color: black; font-size: 10px;'>%s</span></div></html>",
                        course.getName(), course.getTeacher(), course.getLocation()));
                
                // 创建自定义背景
                label.setOpaque(false);
                label.setBackground(new Color(0, 0, 0, 0));
                label.setForeground(Color.BLACK);
                
                // 设置边框
                if (isSelected) {
                    label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bocchiAccentColor, 3),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)
                    ));
                } else {
                    label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bocchiAccentColor, 2),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)
                    ));
                }
            }
        } else {
            // 使用孤独摇滚配色
            Color baseColor = getBocchiColorForCourseType(course.getType());
            Color bocchiColor = new Color(
                    baseColor.getRed(),
                    baseColor.getGreen(),
                    baseColor.getBlue(),
                    220
            );
            c.setBackground(bocchiColor);
            c.setForeground(Color.BLACK);
            if (c instanceof JComponent) {
                ((JComponent)c).setOpaque(true);
            }
            if (c instanceof JLabel) {
                ((JLabel)c).setText(String.format("<html><div style='text-align: center;'><b>%s</b><br><span style='color: black;'>%s</span><br><span style='color: black;'>%s</span></div></html>",
                        course.getName(), course.getTeacher(), course.getLocation()));
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                ((JLabel)c).setForeground(Color.BLACK);
            }
        }
    }
    
    /**
     * 获取孤独摇滚风格的课程类型颜色
     */
    private Color getBocchiColorForCourseType(Course.CourseType type) {
        switch (type) {
            case REQUIRED:
                return new Color(255, 180, 0); // 虹夏 #ffb400
            case ELECTIVE:
                return new Color(255, 34, 145); // 波奇 #ff2291
            case PRACTICAL:
                return new Color(2, 209, 224); // 凉 #02d1e0
            case SEMINAR:
                return new Color(255, 70, 55); // 喜多 #ff4637
            default:
                return bocchiAccentColor;
        }
    }
    
    /**
     * 获取孤独摇滚风格的课程类型显示名称
     */
    private String getBocchiDisplayName(Course.CourseType type) {
        switch (type) {
            case REQUIRED:
                return "📚 必修";
            case ELECTIVE:
                return "🎵 选修";
            case PRACTICAL:
                return "🔬 实验";
            case SEMINAR:
                return "💬 研讨";
            default:
                return type.getDisplayName();
        }
    }
    
    /**
     * 根据背景颜色计算对比色（黑色或白色）
     */
    private Color getContrastColor(Color backgroundColor) {
        // 计算背景色的亮度
        double luminance = (0.299 * backgroundColor.getRed() + 
                           0.587 * backgroundColor.getGreen() + 
                           0.114 * backgroundColor.getBlue()) / 255.0;
        
        // 如果背景色较亮，返回黑色文字；如果较暗，返回白色文字
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 设置背景
        if (bocchiThemeEnabled && backgroundImage != null) {
            setOpaque(false);
        }

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        if (bocchiThemeEnabled) {
            toolbarPanel.setOpaque(true);
            toolbarPanel.setBackground(bocchiWhiteBg);
        }
        
        // 设置所有按钮和组件的孤独摇滚风格
        if (bocchiThemeEnabled) {
            Color bocchiBg = new Color(45, 45, 45, 180);
            Color bocchiBorder = bocchiAccentColor;
            Color bocchiHover = new Color(70, 70, 70, 200);
            // 下拉框统一风格
            JComboBox[] combos = {viewModeComboBox, daySelector};
            for (JComboBox combo : combos) {
                combo.setOpaque(true);
                combo.setBackground(bocchiWhiteBg);
                combo.setForeground(Color.BLACK);
                combo.setBorder(BorderFactory.createLineBorder(bocchiBorder, 2, true));
                combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
            }
            // 按钮统一风格
            JButton[] buttons = {refreshButton, exportButton, themeButton};
            for (JButton btn : buttons) {
                btn.setOpaque(true);
                btn.setBackground(bocchiWhiteBg);
                btn.setForeground(Color.BLACK);
                btn.setBorder(BorderFactory.createLineBorder(bocchiBorder, 2, true));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(true);
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        btn.setBackground(bocchiHover);
                        btn.setOpaque(true);
                        btn.repaint();
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        btn.setBackground(bocchiBg);
                        btn.setOpaque(false);
                        btn.repaint();
                    }
                });
            }
        }
        
        JLabel viewModeLabel = new JLabel("👁️ 视图模式:");
        if (bocchiThemeEnabled) {
            viewModeLabel.setForeground(Color.BLACK);
        }
        toolbarPanel.add(viewModeLabel);
        toolbarPanel.add(viewModeComboBox);
        
        JLabel dayLabel = new JLabel("📅 选择星期:");
        if (bocchiThemeEnabled) {
            dayLabel.setForeground(Color.BLACK);
        }
        toolbarPanel.add(dayLabel);
        toolbarPanel.add(daySelector);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(refreshButton);
        toolbarPanel.add(exportButton);
        toolbarPanel.add(themeButton);
        add(toolbarPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        if (bocchiThemeEnabled) {
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
        }
        add(scrollPane, BorderLayout.CENTER);
        add(createLegendPanel(), BorderLayout.SOUTH);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (bocchiThemeEnabled && backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2d.dispose();
        } else {
            super.paintComponent(g);
        }
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🎨 课程类型图例"));
        
        // 设置图例面板标题字体
        TitledBorder border = (TitledBorder) panel.getBorder();
        
        if (bocchiThemeEnabled) {
            panel.setOpaque(true);
            panel.setBackground(bocchiWhiteBg);
            border.setTitleColor(Color.BLACK);
            panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                "🎨 课程类型图例",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                null,
                Color.BLACK
            ));
        }

        for (Course.CourseType type : Course.CourseType.values()) {
            String displayText = bocchiThemeEnabled ? getBocchiDisplayName(type) : type.getDisplayName();
            JLabel label = new JLabel(displayText);
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            
            if (bocchiThemeEnabled) {
                BufferedImage bgImage = courseTypeBackgrounds.get(type);
                if (bgImage != null) {
                    // 创建带背景的图例
                    label = new JLabel(displayText) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2d = (Graphics2D) g.create();
                            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                            g2d.setColor(new Color(0, 0, 0, 120));
                            g2d.fillRect(0, 0, getWidth(), getHeight());
                            super.paintComponent(g);
                        }
                    };
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setForeground(bocchiTextColor);
                } else {
                    Color bgColor = getBocchiColorForCourseType(type);
                    label.setBackground(bgColor);
                    label.setForeground(getContrastColor(bgColor));
                }
            } else {
                Color bgColor = type.getColor();
                label.setBackground(bgColor);
                label.setForeground(getContrastColor(bgColor));
            }
            
            if (bocchiThemeEnabled) {
                // 孤独摇滚风格的图例标签
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bocchiAccentColor, 2),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
                ));
                label.setPreferredSize(new Dimension(120, 30));
            } else {
                label.setBorder(BorderFactory.createLineBorder(bocchiAccentColor));
                label.setPreferredSize(new Dimension(100, 25));
            }
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
        themeButton.addActionListener(e -> toggleBocchiTheme());
    }
    
    /**
     * 切换孤独摇滚主题
     */
    private void toggleBocchiTheme() {
        bocchiThemeEnabled = !bocchiThemeEnabled;
        themeButton.setText(bocchiThemeEnabled ? "🎨 切换默认" : "🎸 孤独摇滚");
        
        // 重新加载界面
        loadSchedule();
        
        // 重新创建图例面板
        removeAll();
        setupLayout();
        
        // 重新设置组件样式
        if (bocchiThemeEnabled) {
            Color bocchiBg = new Color(45, 45, 45, 180);
            Color bocchiBorder = bocchiAccentColor;
            Color bocchiHover = new Color(70, 70, 70, 200);
            // 下拉框统一风格
            JComboBox[] combos = {viewModeComboBox, daySelector};
            for (JComboBox combo : combos) {
                combo.setOpaque(true);
                combo.setBackground(bocchiWhiteBg);
                combo.setForeground(Color.BLACK);
                combo.setBorder(BorderFactory.createLineBorder(bocchiBorder, 2, true));
                combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
            }
            // 按钮统一风格
            JButton[] buttons = {refreshButton, exportButton, themeButton};
            for (JButton btn : buttons) {
                btn.setOpaque(true);
                btn.setBackground(bocchiWhiteBg);
                btn.setForeground(Color.BLACK);
                btn.setBorder(BorderFactory.createLineBorder(bocchiBorder, 2, true));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(true);
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        btn.setBackground(bocchiHover);
                        btn.setOpaque(true);
                        btn.repaint();
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        btn.setBackground(bocchiBg);
                        btn.setOpaque(false);
                        btn.repaint();
                    }
                });
            }
            
            scheduleTable.getTableHeader().setBackground(new Color(45, 45, 45, 180));
            scheduleTable.getTableHeader().setForeground(bocchiTextColor);
        } else {
            // 恢复默认样式
            viewModeComboBox.setOpaque(true);
            viewModeComboBox.setBackground(UIManager.getColor("ComboBox.background"));
            viewModeComboBox.setForeground(UIManager.getColor("ComboBox.foreground"));
            
            daySelector.setOpaque(true);
            daySelector.setBackground(UIManager.getColor("ComboBox.background"));
            daySelector.setForeground(UIManager.getColor("ComboBox.foreground"));
            
            refreshButton.setOpaque(true);
            refreshButton.setBackground(UIManager.getColor("Button.background"));
            refreshButton.setForeground(UIManager.getColor("Button.foreground"));
            refreshButton.setBorder(UIManager.getBorder("Button.border"));
            
            exportButton.setOpaque(true);
            exportButton.setBackground(UIManager.getColor("Button.background"));
            exportButton.setForeground(UIManager.getColor("Button.foreground"));
            exportButton.setBorder(UIManager.getBorder("Button.border"));
            
            themeButton.setOpaque(true);
            themeButton.setBackground(UIManager.getColor("Button.background"));
            themeButton.setForeground(UIManager.getColor("Button.foreground"));
            themeButton.setBorder(UIManager.getBorder("Button.border"));
            
            scheduleTable.getTableHeader().setBackground(new Color(0xF7F7F7));
            scheduleTable.getTableHeader().setForeground(Color.BLACK);
        }
        
        revalidate();
        repaint();
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
        // 解析格子的时间段
        String timeRange = timeSlot.substring(timeSlot.indexOf(" ") + 1);
        String[] times = timeRange.split("-");
        java.time.LocalTime slotStart = java.time.LocalTime.parse(times[0]);
        java.time.LocalTime slotEnd = java.time.LocalTime.parse(times[1]);
        // 查找覆盖该时间段的课程
        return courses.stream()
                .filter(c -> c.getDayOfWeek() == day &&
                        !c.getStartTime().isAfter(slotStart) &&
                        !c.getEndTime().isBefore(slotEnd))
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