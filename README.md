# XMU Java 项目集合

这是厦门大学软件工程专业的Java编程实践课程项目集合。

## 项目列表

### 1. 学生个人课表管理系统 (student-schedule-system)

一个基于Spring Boot的学生个人课表管理系统，具有以下功能：

- 用户注册和登录
- 课程管理（手动录入和Excel批量导入）
- 按周/日查看课表，支持颜色分类
- 可选的课程提醒（邮件或站内消息）

**技术栈：**
- 后端：Spring Boot, MySQL, JWT, JavaMail, Apache POI
- 前端：HTML, CSS, JavaScript, Bootstrap

### 2. 校园活动报名平台 (campus-activity-platform)

一个简化的校园活动报名平台，用于替代纸质报名表：

- 在线发布活动
- 活动报名（带容量检查）
- 导出报名名单
- 批量通知（可选）
- 按活动类型统计参与情况

**技术栈：**
- 后端：Spring Boot, MySQL, JWT
- 前端：HTML, CSS, JavaScript, Bootstrap, Chart.js

## 快速开始

### 环境要求
- Java 8+
- Maven 3.6+
- MySQL 5.7+

### 运行步骤

1. 克隆仓库
```bash
git clone git@github.com:qingxiam/XMU-Java-Project.git
cd XMU-Java-Project
```

2. 配置数据库
   - 创建MySQL数据库
   - 修改各项目的`application.yml`配置文件

3. 运行项目
```bash
# 运行学生课表管理系统
cd student-schedule-system
mvn spring-boot:run

# 运行校园活动报名平台
cd ../campus-activity-platform
mvn spring-boot:run
```

## 项目结构

```
Java/
├── student-schedule-system/     # 学生个人课表管理系统
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── campus-activity-platform/    # 校园活动报名平台
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── HelloWorld.java             # 简单的Hello World示例
└── README.md                   # 本文件
```

## 贡献

欢迎提交Issue和Pull Request来改进这些项目。

## 许可证

本项目仅供学习和教学使用。 