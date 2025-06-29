@echo off
chcp 65001 >nul

echo === 学生个人课表管理系统 ===
echo 正在编译项目...

REM 创建输出目录
if not exist bin mkdir bin

REM 编译所有Java文件
javac -d bin -cp "src\main\java" src\main\java\com\schedule\*.java src\main\java\com\schedule\*\*.java

if %errorlevel% equ 0 (
    echo 编译成功！
    echo 正在启动应用程序...
    
    REM 运行程序
    java -cp bin com.schedule.Main
) else (
    echo 编译失败！请检查错误信息。
    pause
    exit /b 1
)

pause 