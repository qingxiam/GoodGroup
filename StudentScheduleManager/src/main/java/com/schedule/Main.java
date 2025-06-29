package com.schedule;

import com.schedule.view.LoginFrame;
import javax.swing.*;

/**
 * 学生个人课表管理系统主程序入口
 */
public class Main {
    public static void main(String[] args) {
        // 设置界面外观为系统默认外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                // 如果获取系统外观失败，使用默认外观
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // 在事件调度线程中启动应用程序
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
} 