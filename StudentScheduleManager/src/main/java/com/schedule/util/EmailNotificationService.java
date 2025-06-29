package com.schedule.util;

import com.schedule.model.Course;
import com.schedule.model.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件通知服务
 * 用于发送课程提醒邮件
 */
public class EmailNotificationService {
    private static EmailNotificationService instance;
    private Properties emailProperties;
    private String smtpHost;
    private String smtpPort;
    private String username;
    private String password;
    private boolean isConfigured = false;
    
    private EmailNotificationService() {
        emailProperties = new Properties();
    }
    
    public static EmailNotificationService getInstance() {
        if (instance == null) {
            instance = new EmailNotificationService();
        }
        return instance;
    }
    
    /**
     * 配置邮件服务器
     */
    public void configureEmailServer(String smtpHost, String smtpPort, String username, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        
        // 配置邮件属性
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        emailProperties.put("mail.smtp.host", smtpHost);
        emailProperties.put("mail.smtp.port", smtpPort);
        
        isConfigured = true;
        System.out.println("邮件服务器配置完成: " + smtpHost + ":" + smtpPort);
    }
    
    /**
     * 发送课程提醒邮件
     */
    public boolean sendCourseReminder(User user, Course course, int minutesUntilCourse) {
        if (!isConfigured || user.getEmail() == null || user.getEmail().isEmpty()) {
            System.out.println("邮件服务未配置或用户邮箱为空");
            return false;
        }
        
        try {
            Session session = Session.getInstance(emailProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject("课程提醒 - " + course.getName());
            
            String emailContent = buildEmailContent(user, course, minutesUntilCourse);
            message.setText(emailContent, "UTF-8");
            
            Transport.send(message);
            System.out.println("课程提醒邮件已发送到: " + user.getEmail());
            return true;
            
        } catch (MessagingException e) {
            System.err.println("发送邮件失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 构建邮件内容
     */
    private String buildEmailContent(User user, Course course, int minutesUntilCourse) {
        StringBuilder content = new StringBuilder();
        content.append("亲爱的 ").append(user.getUsername()).append("：\n\n");
        content.append("您有一门课程即将开始：\n\n");
        content.append("课程名称：").append(course.getName()).append("\n");
        content.append("授课教师：").append(course.getTeacher()).append("\n");
        content.append("上课地点：").append(course.getLocation()).append("\n");
        content.append("上课时间：").append(course.getStartTime()).append(" - ").append(course.getEndTime()).append("\n");
        content.append("课程类型：").append(course.getType().getDisplayName()).append("\n");
        content.append("星期：").append(course.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.CHINESE)).append("\n\n");
        
        if (minutesUntilCourse > 0) {
            content.append("距离上课还有 ").append(minutesUntilCourse).append(" 分钟\n\n");
        } else {
            content.append("课程即将开始，请尽快到达教室！\n\n");
        }
        
        if (course.getDescription() != null && !course.getDescription().isEmpty()) {
            content.append("课程描述：").append(course.getDescription()).append("\n\n");
        }
        
        content.append("祝学习愉快！\n");
        content.append("学生课表管理系统");
        
        return content.toString();
    }
    
    /**
     * 检查邮件服务是否已配置
     */
    public boolean isConfigured() {
        return isConfigured;
    }
    
    /**
     * 测试邮件连接
     */
    public boolean testConnection() {
        if (!isConfigured) {
            return false;
        }
        
        try {
            Session session = Session.getInstance(emailProperties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, username, password);
            transport.close();
            
            System.out.println("邮件服务器连接测试成功");
            return true;
            
        } catch (MessagingException e) {
            System.err.println("邮件服务器连接测试失败: " + e.getMessage());
            return false;
        }
    }
} 