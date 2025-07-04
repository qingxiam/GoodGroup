@echo off
title Student Schedule Manager
color 0A

echo ========================================
echo    Student Schedule Manager
echo ========================================
echo.

echo Step 1: Compiling project...
call mvn clean compile
if errorlevel 1 (
    echo.
    echo COMPILATION FAILED!
    echo Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Step 2: Starting GUI application...
echo Note: A login window should appear. If not visible, check your taskbar.
echo.

call mvn exec:java

echo.
echo Application has closed.
pause 