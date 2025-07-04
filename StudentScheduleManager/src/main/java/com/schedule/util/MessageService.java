package com.schedule.util;

import com.schedule.model.Course;
import com.schedule.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 站内消息服务类
 */
public class MessageService {
    private static MessageService instance;
    private List<Message> messages;
    
    private MessageService() {
        messages = new CopyOnWriteArrayList<>();
    }
    
    public static MessageService getInstance() {
        if (instance == null) {
            instance = new MessageService();
        }
        return instance;
    }
    
    /**
     * 发送课程提醒站内消息
     */
    public void sendCourseReminderMessage(User user, Course course, long minutesUntilCourse) {
        String content = String.format(
            "课程提醒\n\n" +
            "课程：%s\n" +
            "教师：%s\n" +
            "地点：%s\n" +
            "时间：%s - %s\n" +
            "类型：%s\n\n" +
            "距离上课还有 %d 分钟，请提前做好准备！",
            course.getName(),
            course.getTeacher(),
            course.getLocation(),
            course.getStartTime(),
            course.getEndTime(),
            course.getType().getDisplayName(),
            minutesUntilCourse
        );
        
        Message message = new Message(
            user.getId(),
            "课程提醒 - " + course.getName(),
            content,
            MessageType.COURSE_REMINDER,
            LocalDateTime.now()
        );
        
        messages.add(message);
        System.out.println("站内消息已发送给用户：" + user.getName());
    }
    
    /**
     * 发送测试站内消息
     */
    public void sendTestMessage(User user) {
        String content = "这是一条测试消息，用于验证站内消息功能是否正常工作。\n\n如果您看到这条消息，说明站内消息功能配置正确。";
        
        Message message = new Message(
            user.getId(),
            "测试消息",
            content,
            MessageType.TEST,
            LocalDateTime.now()
        );
        
        messages.add(message);
        System.out.println("测试站内消息已发送给用户：" + user.getName());
    }
    
    /**
     * 获取用户的所有消息
     */
    public List<Message> getUserMessages(int userId) {
        List<Message> userMessages = new ArrayList<>();
        for (Message message : messages) {
            if (message.getUserId() == userId) {
                userMessages.add(message);
            }
        }
        return userMessages;
    }
    
    /**
     * 获取用户未读消息数量
     */
    public int getUnreadMessageCount(int userId) {
        int count = 0;
        for (Message message : messages) {
            if (message.getUserId() == userId && !message.isRead()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 标记消息为已读
     */
    public void markMessageAsRead(int messageId) {
        for (Message message : messages) {
            if (message.getId() == messageId) {
                message.setRead(true);
                break;
            }
        }
    }
    
    /**
     * 删除消息
     */
    public void deleteMessage(int messageId) {
        messages.removeIf(message -> message.getId() == messageId);
    }
    
    /**
     * 清空用户所有消息
     */
    public void clearUserMessages(int userId) {
        messages.removeIf(message -> message.getUserId() == userId);
    }
    
    /**
     * 消息类型枚举
     */
    public enum MessageType {
        COURSE_REMINDER("课程提醒"),
        TEST("测试消息"),
        SYSTEM("系统消息");
        
        private final String displayName;
        
        MessageType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 消息实体类
     */
    public static class Message {
        private static int nextId = 1;
        private int id;
        private int userId;
        private String title;
        private String content;
        private MessageType type;
        private LocalDateTime createTime;
        private boolean read;
        
        public Message(int userId, String title, String content, MessageType type, LocalDateTime createTime) {
            this.id = nextId++;
            this.userId = userId;
            this.title = title;
            this.content = content;
            this.type = type;
            this.createTime = createTime;
            this.read = false;
        }
        
        // Getters and Setters
        public int getId() {
            return id;
        }
        
        public int getUserId() {
            return userId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public MessageType getType() {
            return type;
        }
        
        public void setType(MessageType type) {
            this.type = type;
        }
        
        public LocalDateTime getCreateTime() {
            return createTime;
        }
        
        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }
        
        public boolean isRead() {
            return read;
        }
        
        public void setRead(boolean read) {
            this.read = read;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s - %s", 
                type.getDisplayName(), title, createTime.toString());
        }
    }
} 