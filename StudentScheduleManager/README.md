# 学生个人课表管理系统V1.0

## 📖 项目简介

学生个人课表管理系统是一个基于Java Swing开发的桌面应用程序，旨在解决学生手动记录课程表易错、跨设备同步难的问题。系统支持课程信息录入、课表可视化展示、Excel批量导入和智能提醒功能。

## 🚀 快速运行

### 推荐方法：一键运行
直接双击 `run_cmd.bat` 文件即可编译并运行项目。

### 其他运行方法
```bash
# 方法1：使用Maven命令
mvn clean compile
mvn exec:java

# 方法2：运行测试
mvn test -Dtest=ReminderServiceTest

# 方法3：测试提醒功能
test_reminder.bat
```

## ✨ 功能特性

### 核心功能
- ✅ **用户管理**：学生注册/登录，个人信息管理，密码修改
- ✅ **课程管理**：手动录入课程信息（名称、时间、地点、教师、类型）
- ✅ **课表展示**：周/日视图切换，颜色标签分类（必修/选修/实验/研讨）
- ✅ **智能提醒**：上课前30分钟推送通知（弹窗/声音/邮件/站内消息）
- ✅ **Excel导入导出**：支持批量导入导出课程数据
- ✅ **数据持久化**：SQLite数据库存储，本地数据安全
- ✅ **消息中心**：站内消息管理，支持标记已读、删除等操作

### 界面特性
- 🎨 **现代化UI**：蓝绿色主题，黑色按钮字体，良好对比度
- 📱 **响应式布局**：支持不同屏幕尺寸
- 🎯 **直观操作**：简洁明了的用户界面
- 📊 **可视化展示**：课表按节次和时间段清晰显示
- 🔔 **实时提醒**：桌面弹窗和系统通知

## 🚀 快速开始

### 方法1：一键运行（推荐）
直接双击 `run_cmd.bat` 文件即可编译并运行项目。

### 方法2：命令行运行
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn exec:java
```

### 方法3：测试提醒功能
```bash
# 运行提醒功能测试
test_reminder.bat
```

## 🔧 系统要求

### 必需环境
- **Java Development Kit (JDK)**：版本 24 或更高
- **Maven**：版本 3.6 或更高
- **操作系统**：Windows 10/11、macOS 10.14+、Linux (Ubuntu 18.04+)
- **内存**：至少 4GB RAM
- **磁盘空间**：至少 500MB 可用空间

### 验证安装
```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -version
```

## 📁 项目结构

```
StudentScheduleManager/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── schedule/
│   │               ├── Main.java                    # 主程序入口
│   │               ├── model/                       # 数据模型
│   │               │   ├── Course.java              # 课程模型（包含课程类型枚举）
│   │               │   └── User.java                # 用户模型
│   │               ├── view/                        # 界面组件
│   │               │   ├── LoginFrame.java          # 登录界面
│   │               │   ├── MainFrame.java           # 主界面
│   │               │   ├── SchedulePanel.java       # 课表面板
│   │               │   ├── CourseManagementPanel.java # 课程管理
│   │               │   ├── UserProfilePanel.java    # 用户资料
│   │               │   ├── MessageCenterPanel.java  # 消息中心
│   │               │   ├── CourseDialog.java        # 课程编辑对话框
│   │               │   ├── RegisterDialog.java      # 注册对话框
│   │               │   ├── ChangePasswordDialog.java # 修改密码对话框
│   │               │   └── ReminderSettingsDialog.java # 提醒设置对话框
│   │               ├── controller/                  # 控制器
│   │               │   ├── CourseController.java    # 课程控制器
│   │               │   └── UserController.java      # 用户控制器
│   │               ├── dao/                         # 数据访问层
│   │               │   ├── CourseDAO.java           # 课程数据访问
│   │               │   └── UserDAO.java             # 用户数据访问
│   │               └── util/                        # 工具类
│   │                   ├── DatabaseUtil.java        # 数据库工具
│   │                   ├── ExcelUtil.java           # Excel工具
│   │                   ├── ReminderService.java     # 提醒服务
│   │                   ├── EmailService.java        # 邮件服务
│   │                   ├── MessageService.java      # 消息服务
│   │                   └── TimeSlotUtil.java        # 时间段工具
│   └── test/
│       └── java/
│           └── com/
│               └── schedule/
│                   └── util/
│                       └── ReminderServiceTest.java # 提醒服务测试
├── lib/                                              # 依赖库目录
│   └── sqlite-jdbc-3.36.0.3.jar                     # SQLite驱动
├── target/                                           # 编译输出目录
├── schedule.db                                       # SQLite数据库文件
├── run_cmd.bat                                       # 主要运行脚本
├── test_reminder.bat                                 # 提醒功能测试脚本
├── pom.xml                                           # Maven配置
├── project.properties                                # 项目属性
├── 使用说明.txt                                       # 使用说明
├── 提醒功能说明(点击run_cmd.bat运行！！！).md          # 提醒功能详细说明
├── 邮件设置.md                                        # 邮件服务器配置
└── README.md                                         # 项目说明
```

## 📖 使用指南

### 首次使用
1. 启动程序后，点击"注册"创建新账户
2. 填写用户名、密码和邮箱信息
3. 登录系统

### 添加课程
1. 切换到"课程管理"选项卡
2. 点击"添加课程"按钮
3. 填写课程信息（名称、教师、地点、时间等）
4. 选择课程类型（必修/选修/实验/研讨）
5. 点击"确定"保存

### 查看课表
1. 切换到"课表查看"选项卡
2. 选择视图模式（周视图/日视图）
3. 课程按颜色分类显示

### 设置提醒
1. 点击菜单栏"工具" → "提醒设置"
2. 配置邮件服务器信息（可选）
3. 设置提醒时间和方式
4. 保存设置

### 消息中心
1. 切换到"消息中心"选项卡
2. 查看所有站内消息
3. 使用工具栏管理消息（标记已读、删除等）

## ⏰ 课表时间格式

系统支持以下时间格式：
```
第1节：08:00~08:45
第2节：08:55~09:40
第3节：10:10~10:55
第4节：11:05~11:50
中午：12:00~14:00
第5节：14:30~15:15
第6节：15:25~16:10
第7节：16:40~17:25
第8节：17:35~18:20
第9节：19:10~19:55
第10节：20:05~20:50
第11节：21:00~21:45
```

## 🎨 课程图例

- 🔴 **红色**：必修课程
- 🔵 **蓝色**：选修课程
- 🟢 **绿色**：实验课程
- 🟠 **橙色**：研讨课程

## 🔔 提醒功能

### 提醒方式
- **弹窗提醒**：桌面弹窗通知
- **邮件提醒**：发送邮件到用户邮箱
- **站内消息**：系统内部消息通知

### 邮件配置
支持多种邮箱服务：
- **QQ邮箱**：smtp.qq.com:587
- **Gmail**：smtp.gmail.com:587
- **163邮箱**：smtp.163.com:25
- **126邮箱**：smtp.126.com:25

### 测试提醒功能
```bash
# 运行提醒功能测试
test_reminder.bat

# 或在应用程序中使用"工具" → "测试提醒"
```

## 🛠️ 技术架构

### 设计模式
- **MVC架构**：Model-View-Controller分离
- **单例模式**：服务类使用单例模式
- **DAO模式**：数据访问对象模式
- **观察者模式**：提醒服务使用观察者模式

### 核心技术
- **Java Swing**：图形用户界面
- **SQLite**：轻量级数据库
- **Apache POI**：Excel文件处理
- **JavaMail**：邮件发送功能
- **JUnit 5**：单元测试框架

### 依赖管理
- **Maven**：项目构建和依赖管理
- **SQLite JDBC**：数据库连接驱动
- **Apache POI**：Excel文件读写
- **JavaMail API**：邮件发送功能

## 🔧 故障排除

### 常见问题

#### 1. 编译错误
```bash
# 错误：找不到或无法加载主类
# 解决：确保所有依赖库都在lib目录下
ls lib/*.jar

# 错误：编码问题
# 解决：使用UTF-8编码编译
javac -encoding UTF-8 -cp "lib/*" -d bin src/**/*.java