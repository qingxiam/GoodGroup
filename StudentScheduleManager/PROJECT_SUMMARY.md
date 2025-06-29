# 学生个人课表管理系统 - 项目总结

## 项目概述
本项目是一个基于Java Swing开发的学生个人课表管理系统，旨在解决学生手动记录课程表易错、跨设备同步难的问题。

## 技术架构

### 开发环境
- **编程语言**: Java 8+
- **GUI框架**: Java Swing
- **数据库**: SQLite
- **开发工具**: 任何支持Java的IDE

### 项目结构
```
StudentScheduleManager/
├── src/main/java/com/schedule/
│   ├── Main.java                    # 主程序入口
│   ├── model/                       # 数据模型层
│   │   ├── User.java               # 用户模型
│   │   └── Course.java             # 课程模型
│   ├── dao/                        # 数据访问层
│   │   ├── UserDAO.java            # 用户数据访问
│   │   └── CourseDAO.java          # 课程数据访问
│   ├── controller/                 # 控制器层
│   │   ├── UserController.java     # 用户控制器
│   │   └── CourseController.java   # 课程控制器
│   ├── view/                       # 视图层
│   │   ├── LoginFrame.java         # 登录界面
│   │   ├── RegisterDialog.java     # 注册对话框
│   │   ├── MainFrame.java          # 主界面
│   │   ├── SchedulePanel.java      # 课表查看面板
│   │   ├── CourseManagementPanel.java # 课程管理面板
│   │   ├── UserProfilePanel.java   # 用户资料面板
│   │   ├── CourseDialog.java       # 课程编辑对话框
│   │   └── ChangePasswordDialog.java # 修改密码对话框
│   └── util/                       # 工具类
│       ├── DatabaseUtil.java       # 数据库工具
│       ├── ExcelUtil.java          # Excel工具（框架）
│       └── ReminderService.java    # 提醒服务
├── README.md                       # 项目说明
├── PROJECT_SUMMARY.md              # 项目总结
├── project.properties              # 项目配置
├── compile_and_run.sh              # Linux/Mac编译运行脚本
└── compile_and_run.bat             # Windows编译运行脚本
```

## 功能特性

### ✅ 已实现功能

#### 1. 用户管理
- **用户注册**: 支持新用户注册，包含用户名、密码、邮箱、学号、姓名等信息
- **用户登录**: 用户名密码验证登录
- **个人信息管理**: 修改个人资料和密码
- **数据验证**: 输入验证和重复用户名检查

#### 2. 课程管理
- **添加课程**: 手动添加课程信息（名称、教师、地点、时间、类型等）
- **编辑课程**: 修改现有课程信息
- **删除课程**: 删除不需要的课程
- **课程分类**: 支持必修、选修、实验、研讨等课程类型
- **数据验证**: 课程信息完整性验证

#### 3. 课表展示
- **周视图**: 按周显示所有课程，支持颜色分类
- **日视图**: 按天显示课程安排
- **课程图例**: 不同课程类型用不同颜色标识
- **实时刷新**: 支持课表数据实时更新

#### 4. 提醒功能
- **自动提醒**: 上课前30分钟自动弹出提醒窗口
- **提醒服务**: 后台定时检查即将开始的课程
- **提醒设置**: 支持启用/禁用课程提醒
- **测试功能**: 提供提醒功能测试

#### 5. 数据库管理
- **SQLite数据库**: 轻量级本地数据库
- **自动初始化**: 程序启动时自动创建数据表
- **数据持久化**: 用户和课程数据本地存储

### 🔄 待实现功能

#### 1. Excel导入导出
- **Excel导入**: 批量导入课程数据（需要Apache POI库）
- **Excel导出**: 导出课表到Excel文件
- **模板生成**: 生成Excel导入模板

#### 2. 高级提醒功能
- **邮件提醒**: 发送邮件通知（需要JavaMail库）
- **自定义提醒时间**: 设置不同的提醒提前时间
- **提醒历史**: 记录提醒历史

#### 3. 数据备份恢复
- **数据备份**: 定期备份数据库
- **数据恢复**: 从备份文件恢复数据
- **数据导入导出**: 支持数据迁移

## 设计模式

### 1. MVC架构
- **Model**: User、Course数据模型
- **View**: 各种Swing界面组件
- **Controller**: UserController、CourseController业务逻辑控制

### 2. DAO模式
- **数据访问对象**: UserDAO、CourseDAO封装数据库操作
- **数据抽象**: 隐藏数据库实现细节

### 3. 单例模式
- **ReminderService**: 确保提醒服务全局唯一实例

## 数据库设计

### 用户表 (users)
```sql
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    email TEXT,
    student_id TEXT,
    name TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 课程表 (courses)
```sql
CREATE TABLE courses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    teacher TEXT,
    location TEXT,
    day_of_week TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    type TEXT NOT NULL,
    description TEXT,
    reminder_enabled BOOLEAN DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
```

### 提醒表 (reminders)
```sql
CREATE TABLE reminders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_id INTEGER NOT NULL,
    reminder_time TIMESTAMP NOT NULL,
    sent BOOLEAN DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses (id)
);
```

## 运行说明

### 环境要求
- Java 8或更高版本
- 支持Java的操作系统（Windows、macOS、Linux）

### 编译运行
1. **Linux/Mac**: 执行 `./compile_and_run.sh`
2. **Windows**: 双击 `compile_and_run.bat`

### 首次使用
1. 启动程序后点击"注册"按钮
2. 填写用户信息完成注册
3. 使用注册的用户名密码登录
4. 在"课程管理"选项卡中添加课程
5. 在"课表查看"选项卡中查看课表

## 扩展建议

### 1. 功能扩展
- **多用户支持**: 支持管理员和普通用户角色
- **课程冲突检测**: 检测时间冲突的课程
- **课程统计**: 统计各类型课程数量
- **学期管理**: 支持不同学期的课程管理

### 2. 技术改进
- **网络同步**: 支持云端数据同步
- **移动端支持**: 开发Android/iOS客户端
- **Web版本**: 开发Web版本支持跨平台访问
- **数据加密**: 对敏感数据进行加密存储

### 3. 用户体验
- **主题切换**: 支持深色/浅色主题
- **快捷键**: 添加常用操作的快捷键
- **拖拽操作**: 支持拖拽调整课程时间
- **搜索功能**: 支持课程搜索和过滤

## 项目亮点

1. **完整的MVC架构**: 代码结构清晰，易于维护和扩展
2. **用户友好的界面**: 现代化的GUI设计，操作简单直观
3. **实时提醒功能**: 智能的课程提醒系统
4. **数据持久化**: 可靠的本地数据存储
5. **跨平台支持**: 基于Java Swing，支持多平台运行
6. **模块化设计**: 功能模块独立，便于功能扩展

## 总结

本项目成功实现了一个功能完整的学生个人课表管理系统，包含了用户管理、课程管理、课表展示、提醒功能等核心特性。项目采用标准的MVC架构和DAO模式，代码结构清晰，具有良好的可维护性和扩展性。

虽然某些高级功能（如Excel导入导出）需要额外的依赖库支持，但核心功能已经完全实现并可以正常运行。项目为后续的功能扩展和技术改进提供了良好的基础。 