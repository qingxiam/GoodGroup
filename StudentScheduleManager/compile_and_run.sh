#!/bin/bash

# 学生个人课表管理系统编译和运行脚本

echo "=== 学生个人课表管理系统 ==="
echo "正在编译项目..."

# 创建输出目录
mkdir -p bin

# 编译所有Java文件
javac -d bin -cp "src/main/java" src/main/java/com/schedule/*.java src/main/java/com/schedule/*/*.java

if [ $? -eq 0 ]; then
    echo "编译成功！"
    echo "正在启动应用程序..."
    
    # 运行程序
    java -cp bin com.schedule.Main
else
    echo "编译失败！请检查错误信息。"
    exit 1
fi 