@echo off
echo 正在测试提醒功能...
echo.

echo 1. 编译项目...
call mvn clean compile
if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo.
echo 2. 运行测试...
call mvn test -Dtest=ReminderServiceTest
if %errorlevel% neq 0 (
    echo 测试失败！
    pause
    exit /b 1
)

echo.
echo 3. 启动应用程序...
echo 在应用程序中，您可以：
echo - 使用"工具"菜单中的"提醒设置"来配置邮件服务器
echo - 使用"工具"菜单中的"测试提醒"来测试各种提醒功能
echo - 在"消息中心"选项卡中查看站内消息
echo.
call mvn exec:java

pause 