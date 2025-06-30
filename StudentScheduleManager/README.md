# 学生个人课表管理系统

## 项目简介

学生个人课表管理系统是一个基于Java Swing开发的桌面应用程序，旨在解决学生手动记录课程表易错、跨设备同步难的问题。系统支持课程信息录入、课表可视化展示、Excel批量导入和智能提醒功能。

## 功能特性

### 核心功能
- ✅ **用户管理**：学生注册/登录，个人信息管理
- ✅ **课程管理**：手动录入课程信息（名称、时间、地点、教师）
- ✅ **课表展示**：周/日视图切换，颜色标签分类（必修/选修/实验/研讨）
- ✅ **提醒功能**：上课前30分钟推送通知（弹窗/声音/邮件）
- ✅ **Excel导入**：支持批量导入课程数据
- ✅ **数据持久化**：SQLite数据库存储

### 界面特性
- 🎨 **现代化UI**：蓝绿色主题，黑色按钮字体，良好对比度
- 📱 **响应式布局**：支持不同屏幕尺寸
- 🎯 **直观操作**：简洁明了的用户界面
- 📊 **可视化展示**：课表按节次和时间段清晰显示

## 开发环境要求

### 必需环境
- **Java Development Kit (JDK)**：版本 8 或更高
  - 推荐：OpenJDK 11 或 Oracle JDK 11
  - 最低要求：Java 8 (1.8.0)
- **操作系统**：Windows 10/11、macOS 10.14+、Linux (Ubuntu 18.04+)
- **内存**：至少 4GB RAM
- **磁盘空间**：至少 500MB 可用空间

### 开发工具（推荐）
- **IDE**：
  - IntelliJ IDEA (推荐)
  - Eclipse IDE
  - Visual Studio Code + Java Extension Pack
- **版本控制**：Git 2.20+
- **构建工具**：Maven 3.6+ (可选)

### 依赖库
项目需要以下Java库文件，请下载到 `lib/` 目录：

#### 核心依赖
- **sqlite-jdbc-3.36.0.3.jar** - SQLite数据库驱动
  - 下载：[Maven Central](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc/3.36.0.3)

#### Excel处理（可选功能）
- **poi-5.2.3.jar** - Apache POI核心库
- **poi-ooxml-5.2.3.jar** - POI OOXML支持
- **poi-ooxml-schemas-4.1.2.jar** - POI OOXML模式
- **commons-collections4-4.4.jar** - Apache Commons Collections
- **commons-math3-3.6.1.jar** - Apache Commons Math
- **SparseBitSet-1.2.jar** - SparseBitSet库
- **log4j-api-2.18.0.jar** - Log4j API
- **commons-codec-1.15.jar** - Apache Commons Codec
- **commons-io-2.11.0.jar** - Apache Commons IO

#### 邮件通知（可选功能）
- **javax.mail-1.6.2.jar** - JavaMail API
  - 下载：[Maven Central](https://mvnrepository.com/artifact/javax.mail/javax.mail/1.6.2)

## 安装和配置

### 1. 环境准备

#### Windows 环境
```bash
# 1. 安装JDK
# 下载并安装 OpenJDK 11 或 Oracle JDK 11
# 设置环境变量 JAVA_HOME 和 PATH

# 2. 验证安装
java -version
javac -version
```

#### macOS 环境
```bash
# 使用 Homebrew 安装
brew install openjdk@11

# 设置环境变量
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@11' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

#### Linux 环境
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-11-jdk

# CentOS/RHEL
sudo yum install java-11-openjdk-devel
```

### 2. 项目设置

```bash
# 1. 克隆项目
git clone https://github.com/qingxiam/XMU-Java-Project.git
cd XMU-Java-Project/StudentScheduleManager

# 2. 创建lib目录
mkdir -p lib

# 3. 下载依赖库到lib目录
# 手动下载上述jar文件到lib/目录
```

### 3. 编译和运行

#### 方法一：使用批处理文件（推荐）
```bash
# Windows
compile_and_run.bat

# Linux/macOS
chmod +x compile_and_run.sh
./compile_and_run.sh
```

#### 方法二：手动编译
```bash
# 1. 编译所有Java文件
javac -cp "lib/*" -d bin src/main/java/com/schedule/*.java
javac -cp "lib/*" -d bin src/main/java/com/schedule/controller/*.java
javac -cp "lib/*" -d bin src/main/java/com/schedule/dao/*.java
javac -cp "lib/*" -d bin src/main/java/com/schedule/model/*.java
javac -cp "lib/*" -d bin src/main/java/com/schedule/util/*.java
javac -cp "lib/*" -d bin src/main/java/com/schedule/view/*.java

# 2. 运行程序
java -cp "bin;lib/*" com.schedule.Main
```

#### 方法三：使用IDE
1. 在IDE中导入项目
2. 添加lib目录下的所有jar文件到classpath
3. 运行 `com.schedule.Main` 类

## 项目结构

```
StudentScheduleManager/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── schedule/
│                   ├── Main.java                    # 主程序入口
│                   ├── model/                       # 数据模型
│                   │   ├── Course.java              # 课程模型
│                   │   └── User.java                # 用户模型
│                   ├── view/                        # 界面组件
│                   │   ├── LoginFrame.java          # 登录界面
│                   │   ├── MainFrame.java           # 主界面
│                   │   ├── SchedulePanel.java       # 课表面板
│                   │   ├── CourseManagementPanel.java # 课程管理
│                   │   └── UserProfilePanel.java    # 用户资料
│                   ├── controller/                  # 控制器
│                   │   ├── CourseController.java    # 课程控制器
│                   │   └── UserController.java      # 用户控制器
│                   ├── dao/                         # 数据访问层
│                   │   ├── CourseDAO.java           # 课程数据访问
│                   │   └── UserDAO.java             # 用户数据访问
│                   └── util/                        # 工具类
│                       ├── DatabaseUtil.java        # 数据库工具
│                       ├── ExcelUtil.java           # Excel工具
│                       └── ReminderService.java     # 提醒服务
├── lib/                                              # 依赖库目录
├── bin/                                              # 编译输出目录
├── schedule.db                                       # SQLite数据库文件
├── compile_and_run.bat                               # Windows运行脚本
├── compile_and_run.sh                                # Linux/macOS运行脚本
├── pom.xml                                           # Maven配置（可选）
└── README.md                                         # 项目说明
```

## 使用指南

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

## 课表时间格式

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

## 故障排除

### 常见问题

#### 1. 编译错误
```bash
# 错误：找不到或无法加载主类
# 解决：确保所有依赖库都在lib目录下
ls lib/*.jar

# 错误：编码问题
# 解决：使用UTF-8编码编译
javac -encoding UTF-8 -cp "lib/*" -d bin src/main/java/com/schedule/**/*.java
```

#### 2. 运行错误
```bash
# 错误：ClassNotFoundException
# 解决：检查classpath设置
java -cp "bin;lib/*" com.schedule.Main

# 错误：数据库连接失败
# 解决：检查schedule.db文件权限
chmod 644 schedule.db
```

#### 3. 界面显示问题
- 确保Java版本兼容（推荐Java 11）
- 检查系统字体支持
- 尝试设置系统外观：`UIManager.setLookAndFeel()`

### 调试模式
```bash
# 启用详细日志
java -Djava.util.logging.config.file=logging.properties -cp "bin;lib/*" com.schedule.Main
```

## 开发指南

### 代码规范
- 使用Java命名规范
- 添加适当的注释
- 遵循MVC架构模式
- 异常处理要完善

### 扩展功能
1. 添加新的课程类型
2. 实现数据导出功能
3. 增加统计分析功能
4. 支持多语言界面

### 测试
```bash
# 单元测试
mvn test

# 集成测试
mvn verify
```

## 版本历史

### v1.1.0 (当前版本)
- 优化UI界面，按钮字体改为黑色
- 更新课表结构，支持节次显示
- 完善Excel导入功能
- 增强提醒系统

### v1.0.0
- 基础用户管理功能
- 课程信息管理
- 基础课表展示
- 简单提醒功能

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目维护者：[qingxiam](https://github.com/qingxiam)
- 项目地址：[https://github.com/qingxiam/XMU-Java-Project](https://github.com/qingxiam/XMU-Java-Project)
- 问题反馈：[Issues](https://github.com/qingxiam/XMU-Java-Project/issues)

## 致谢

感谢所有为这个项目做出贡献的开发者和用户！ 