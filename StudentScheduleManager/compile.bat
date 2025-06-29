@echo off
setlocal enabledelayedexpansion

rem 设置源文件目录和类路径
set "SOURCE_DIR=src\main\java\com\schedule"
set "CLASS_PATH=lib/*;bin"
set "OUTPUT_DIR=bin"

rem 创建输出目录（如果不存在）
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

rem 编译所有 Java 文件
for /r "%SOURCE_DIR%" %%f in (*.java) do (
    echo 正在编译: %%f
    javac -cp "%CLASS_PATH%" -d "%OUTPUT_DIR%" "%%f"
    if errorlevel 1 (
        echo 编译失败: %%f
        exit /b 1
    )
)

echo 编译完成!