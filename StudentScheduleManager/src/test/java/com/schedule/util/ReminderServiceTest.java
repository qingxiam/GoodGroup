package com.schedule.util;

import com.schedule.model.Course;
import com.schedule.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 提醒服务测试类
 */
public class ReminderServiceTest {
    
    private ReminderService reminderService;
    private EmailService emailService;
    private MessageService messageService;
    private User testUser;
    private Course testCourse;
    
    @BeforeEach
    void setUp() {
        reminderService = ReminderService.getInstance();
        emailService = EmailService.getInstance();
        messageService = MessageService.getInstance();
        
        // 创建测试用户
        testUser = new User();
        testUser.setId(1);
        testUser.setName("测试用户");
        testUser.setEmail("test@example.com");
        
        // 创建测试课程
        testCourse = new Course();
        testCourse.setId(1);
        testCourse.setUserId(1);
        testCourse.setName("测试课程");
        testCourse.setTeacher("测试教师");
        testCourse.setLocation("测试教室");
        testCourse.setReminderEnabled(true);
    }
    
    @Test
    void testReminderServiceSingleton() {
        ReminderService instance1 = ReminderService.getInstance();
        ReminderService instance2 = ReminderService.getInstance();
        assertSame(instance1, instance2, "ReminderService应该是单例模式");
    }
    
    @Test
    void testEmailServiceSingleton() {
        EmailService instance1 = EmailService.getInstance();
        EmailService instance2 = EmailService.getInstance();
        assertSame(instance1, instance2, "EmailService应该是单例模式");
    }
    
    @Test
    void testMessageServiceSingleton() {
        MessageService instance1 = MessageService.getInstance();
        MessageService instance2 = MessageService.getInstance();
        assertSame(instance1, instance2, "MessageService应该是单例模式");
    }
    
    @Test
    void testEmailServiceConfiguration() {
        // 测试邮件服务配置
        emailService.setSmtpConfig("smtp.test.com", 587, "test@test.com", "password");
        assertTrue(emailService.isEmailConfigured(), "邮件服务应该已配置");
        
        // 测试获取SMTP信息
        String smtpInfo = emailService.getSmtpInfo();
        assertTrue(smtpInfo.contains("smtp.test.com"), "SMTP信息应该包含服务器地址");
        assertTrue(smtpInfo.contains("test@test.com"), "SMTP信息应该包含邮箱地址");
    }
    
    @Test
    void testMessageServiceFunctionality() {
        // 测试发送站内消息
        messageService.sendTestMessage(testUser);
        
        // 验证消息是否已添加
        var messages = messageService.getUserMessages(testUser.getId());
        assertEquals(1, messages.size(), "应该有一条测试消息");
        
        // 验证消息内容
        var message = messages.get(0);
        assertEquals("测试消息", message.getTitle(), "消息标题应该正确");
        assertEquals(MessageService.MessageType.TEST, message.getType(), "消息类型应该正确");
        assertFalse(message.isRead(), "新消息应该是未读状态");
        
        // 测试标记已读
        messageService.markMessageAsRead(message.getId());
        assertTrue(message.isRead(), "消息应该被标记为已读");
        
        // 测试未读消息计数
        assertEquals(0, messageService.getUnreadMessageCount(testUser.getId()), "应该没有未读消息");
    }
    
    @Test
    void testReminderServiceStartStop() {
        // 测试启动提醒服务
        reminderService.startReminderService(testUser);
        assertTrue(reminderService.isRunning(), "提醒服务应该正在运行");
        
        // 测试停止提醒服务
        reminderService.stopReminderService();
        assertFalse(reminderService.isRunning(), "提醒服务应该已停止");
    }
    
    @Test
    void testMessageServiceClear() {
        // 先清空之前的消息
        messageService.clearUserMessages(testUser.getId());
        
        // 添加一些测试消息
        messageService.sendTestMessage(testUser);
        messageService.sendTestMessage(testUser);
        
        // 验证消息数量
        assertEquals(2, messageService.getUserMessages(testUser.getId()).size(), "应该有2条消息");
        
        // 清空消息
        messageService.clearUserMessages(testUser.getId());
        
        // 验证消息已清空
        assertEquals(0, messageService.getUserMessages(testUser.getId()).size(), "消息应该已清空");
    }
} 