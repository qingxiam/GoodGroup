package com.schedule.view;

import com.schedule.model.User;
import com.schedule.util.MessageService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 消息中心面板
 */
public class MessageCenterPanel extends JPanel {
    private User currentUser;
    private MessageService messageService;
    
    // UI组件
    private JTable messageTable;
    private DefaultTableModel tableModel;
    private JTextArea messageContentArea;
    private JButton refreshButton;
    private JButton markAsReadButton;
    private JButton deleteButton;
    private JButton clearAllButton;
    private JLabel statusLabel;
    
    public MessageCenterPanel(User user) {
        this.currentUser = user;
        this.messageService = MessageService.getInstance();
        
        initComponents();
        setupLayout();
        setupListeners();
        refreshMessages();
    }
    
    private void initComponents() {
        // 创建表格模型
        String[] columnNames = {"ID", "类型", "标题", "时间", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        
        messageTable = new JTable(tableModel);
        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.getTableHeader().setReorderingAllowed(false);
        
        // 设置列宽
        messageTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        messageTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // 类型
        messageTable.getColumnModel().getColumn(2).setPreferredWidth(200); // 标题
        messageTable.getColumnModel().getColumn(3).setPreferredWidth(150); // 时间
        messageTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 状态
        
        messageContentArea = new JTextArea();
        messageContentArea.setEditable(false);
        messageContentArea.setLineWrap(true);
        messageContentArea.setWrapStyleWord(true);
        messageContentArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        refreshButton = new JButton("刷新");
        markAsReadButton = new JButton("标记已读");
        deleteButton = new JButton("删除");
        clearAllButton = new JButton("清空所有");
        
        statusLabel = new JLabel("共 0 条消息，未读 0 条");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部工具栏
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(markAsReadButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearAllButton);
        
        toolbarPanel.add(buttonPanel, BorderLayout.WEST);
        toolbarPanel.add(statusLabel, BorderLayout.EAST);
        
        // 消息列表和内容区域
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // 左侧消息列表
        JScrollPane tableScrollPane = new JScrollPane(messageTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 300));
        
        // 右侧消息内容
        JScrollPane contentScrollPane = new JScrollPane(messageContentArea);
        contentScrollPane.setPreferredSize(new Dimension(300, 300));
        
        splitPane.setLeftComponent(tableScrollPane);
        splitPane.setRightComponent(contentScrollPane);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(3);
        
        add(toolbarPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void setupListeners() {
        refreshButton.addActionListener(e -> refreshMessages());
        markAsReadButton.addActionListener(e -> markSelectedAsRead());
        deleteButton.addActionListener(e -> deleteSelectedMessage());
        clearAllButton.addActionListener(e -> clearAllMessages());
        
        // 表格选择监听器
        messageTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedMessage();
            }
        });
    }
    
    private void refreshMessages() {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 获取用户消息
        List<MessageService.Message> messages = messageService.getUserMessages(currentUser.getId());
        
        // 添加到表格
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (MessageService.Message message : messages) {
            Object[] row = {
                message.getId(),
                message.getType().getDisplayName(),
                message.getTitle(),
                message.getCreateTime().format(formatter),
                message.isRead() ? "已读" : "未读"
            };
            tableModel.addRow(row);
        }
        
        // 更新状态
        updateStatus();
        
        // 清空内容区域
        messageContentArea.setText("");
    }
    
    private void showSelectedMessage() {
        int selectedRow = messageTable.getSelectedRow();
        if (selectedRow >= 0) {
            int messageId = (Integer) tableModel.getValueAt(selectedRow, 0);
            List<MessageService.Message> messages = messageService.getUserMessages(currentUser.getId());
            
            for (MessageService.Message message : messages) {
                if (message.getId() == messageId) {
                    messageContentArea.setText(message.getContent());
                    break;
                }
            }
        }
    }
    
    private void markSelectedAsRead() {
        int selectedRow = messageTable.getSelectedRow();
        if (selectedRow >= 0) {
            int messageId = (Integer) tableModel.getValueAt(selectedRow, 0);
            messageService.markMessageAsRead(messageId);
            
            // 更新表格显示
            tableModel.setValueAt("已读", selectedRow, 4);
            updateStatus();
        } else {
            JOptionPane.showMessageDialog(this, "请选择要标记的消息", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void deleteSelectedMessage() {
        int selectedRow = messageTable.getSelectedRow();
        if (selectedRow >= 0) {
            int messageId = (Integer) tableModel.getValueAt(selectedRow, 0);
            
            int result = JOptionPane.showConfirmDialog(this, 
                "确定要删除这条消息吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                messageService.deleteMessage(messageId);
                refreshMessages();
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要删除的消息", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearAllMessages() {
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要清空所有消息吗？此操作不可恢复！", "确认清空", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            messageService.clearUserMessages(currentUser.getId());
            refreshMessages();
        }
    }
    
    private void updateStatus() {
        List<MessageService.Message> messages = messageService.getUserMessages(currentUser.getId());
        int totalCount = messages.size();
        int unreadCount = messageService.getUnreadMessageCount(currentUser.getId());
        
        statusLabel.setText(String.format("共 %d 条消息，未读 %d 条", totalCount, unreadCount));
    }
    
    /**
     * 获取未读消息数量（供外部调用）
     */
    public int getUnreadCount() {
        return messageService.getUnreadMessageCount(currentUser.getId());
    }
} 