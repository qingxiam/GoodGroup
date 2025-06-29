# 学生个人课表管理系统

## 项目简介
这是一个基于Java Swing开发的学生个人课表管理系统，旨在解决学生手动记录课程表易错、跨设备同步难的问题。

## 功能特性
- **用户管理**：学生注册/登录功能
- **课程管理**：手动录入课程信息（名称、时间、地点、教师），支持Excel批量导入
- **课表展示**：按周/日视图展示课程，支持颜色标签分类（如必修/选修）
- **提醒功能**：上课前30分钟推送通知（邮件/站内消息）

## 技术栈
- Java 8+
- Java Swing (GUI界面)
- Apache POI (Excel文件处理)
- SQLite (数据存储)
- JavaMail (邮件通知)

## 项目结构
```
StudentScheduleManager/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   └── schedule/
│   │   │   │       ├── model/          # 数据模型
│   │   │   │       ├── view/           # 界面组件
│   │   │   │       ├── controller/     # 控制器
│   │   │   │       ├── dao/            # 数据访问层
│   │   │   │       ├── util/           # 工具类
│   │   │   │       └── Main.java       # 主程序入口
│   │   │   └── resources/              # 资源文件
│   │   └── lib/                        # 依赖库
└── README.md
```

## 运行说明
1. 确保已安装Java 8或更高版本
2. 下载项目依赖库到lib目录
3. 编译项目：`javac -cp "lib/*" src/main/java/com/schedule/*.java`
4. 运行程序：`java -cp "lib/*:src/main/java" com.schedule.Main`

## 使用说明
1. 首次运行需要注册新用户
2. 登录后可以添加课程信息
3. 支持Excel批量导入课程
4. 课表支持周视图和日视图切换
5. 可以设置课程提醒功能

## 依赖库
- poi-5.2.3.jar (Excel处理)
- poi-ooxml-5.2.3.jar (Excel处理)
- sqlite-jdbc-3.36.0.3.jar (数据库)
- javax.mail-1.6.2.jar (邮件发送) 