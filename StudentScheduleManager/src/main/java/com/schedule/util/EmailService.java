package com.schedule.util;

import com.schedule.model.Course;
import com.schedule.model.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件服务类
 */
public class EmailService {
    private static EmailService instance;
    private Properties emailProperties;
    private String smtpHost = "smtp.qq.com"; // 默认使用QQ邮箱SMTP
    private int smtpPort = 587;
    private String username = ""; // 发件人邮箱
    private String password = ""; // 发件人密码或授权码
    
    private EmailService() {
        initEmailProperties();
    }
    
    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    /**
     * 初始化邮件配置
     */
    private void initEmailProperties() {
        emailProperties = new Properties();
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        emailProperties.put("mail.smtp.host", smtpHost);
        emailProperties.put("mail.smtp.port", smtpPort);
        emailProperties.put("mail.smtp.ssl.trust", smtpHost);
    }
    
    /**
     * 设置SMTP配置
     */
    public void setSmtpConfig(String host, int port, String username, String password) {
        this.smtpHost = host;
        this.smtpPort = port;
        this.username = username;
        this.password = password;
        initEmailProperties();
    }
    
    /**
     * 发送课程提醒邮件
     */
    public boolean sendCourseReminder(User user, Course course, long minutesUntilCourse) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.out.println("用户邮箱为空，无法发送邮件提醒");
            return false;
        }
        
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("邮件服务器配置未完成，无法发送邮件");
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
            
            String emailContent = String.format(
                "亲爱的 %s，\n\n" +
                "您有一门课程即将开始：\n\n" +
                "课程名称：%s\n" +
                "授课教师：%s\n" +
                "上课地点：%s\n" +
                "上课时间：%s - %s\n" +
                "课程类型：%s\n" +
                "距离上课还有：%d 分钟\n\n" +
                "请提前做好准备，按时到达教室。\n\n" +
                "祝学习愉快！\n" +
                "学生个人课表管理系统",
                user.getName(),
                course.getName(),
                course.getTeacher(),
                course.getLocation(),
                course.getStartTime(),
                course.getEndTime(),
                course.getType().getDisplayName(),
                minutesUntilCourse
            );
            
            message.setText(emailContent);
            
            Transport.send(message);
            System.out.println("课程提醒邮件已发送到：" + user.getEmail());
            return true;
            
        } catch (MessagingException e) {
            System.err.println("发送邮件失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 发送测试邮件
     */
    public boolean sendTestEmail(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.out.println("用户邮箱为空，无法发送测试邮件");
            return false;
        }
        
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("邮件服务器配置未完成，无法发送测试邮件");
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
            message.setSubject("测试邮件 - 学生个人课表管理系统");
            
            String emailContent = String.format(
                "亲爱的 %s，\n\n" +
                "这是一封测试邮件，用于验证邮件提醒功能是否正常工作。\n\n" +
                "如果您收到这封邮件，说明邮件提醒功能配置正确。\n\n" +
                "祝学习愉快！\n" +
                "学生个人课表管理系统",
                user.getName()
            );
            
            message.setText(emailContent);
            
            Transport.send(message);
            System.out.println("测试邮件已发送到：" + user.getEmail());
            return true;
            
        } catch (MessagingException e) {
            System.err.println("发送测试邮件失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 检查邮件配置是否完整
     */
    public boolean isEmailConfigured() {
        return !username.isEmpty() && !password.isEmpty();
    }
    
    /**
     * 获取当前SMTP配置信息
     */
    public String getSmtpInfo() {
        return String.format("SMTP服务器：%s:%d，发件人：%s", smtpHost, smtpPort, username);
    }
} 