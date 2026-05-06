# 智慧图书馆管理系统（Library_AI）

基于 Spring Boot + Vue.js 的图书馆管理系统，集成 AI 智能助手，支持图书管理、借阅管理、用户权限管理及 AI 图书推荐与咨询等功能。

## 项目简介

本项目为毕业设计项目，旨在构建一个现代化的智慧图书馆管理平台。系统采用前后端分离架构，后端基于 Spring Boot 提供 RESTful API，前端基于 Vue.js + Element UI 构建用户界面，并接入阿里云通义千问大模型实现 AI 智能图书推荐与咨询服务。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 前端 | Vue.js | 2.6 |
| 前端 UI | Element UI | 2.15 |
| 前端路由 | Vue Router | 3.5 |
| 前端 HTTP | Axios | 1.13 |
| 后端框架 | Spring Boot | 2.7.18 |
| ORM | MyBatis-Plus | 3.5.3 |
| 数据库 | MySQL | 8.0 |
| 认证 | JWT | - |
| AI 模型 | 阿里云通义千问（Qwen） | qwen-turbo |
| AI 框架 | LangChain4j | 0.35.0 |
| 对象存储 | 阿里云 OSS | - |
| Java | JDK | 17 |

## 功能模块

### 用户与权限管理
- **三种角色**：系统管理员（admin）、图书管理员（librarian）、读者（reader）
- **RBAC 权限模型**：基于角色-权限关联表实现细粒度权限控制
- **用户注册/登录**：支持 JWT Token 认证，路由守卫实现前端权限拦截
- **个人中心**：修改个人信息、修改密码、上传头像（阿里云 OSS）

### 图书管理（管理员/图书管理员）
- 图书的增删改查（支持按书名、作者、分类搜索）
- 图书分类管理（计算机、文学、历史、科学等）
- 库存管理与状态跟踪

### 借阅管理
- **管理员端**：查看所有借阅记录、办理借书/还书、逾期管理
- **读者端**：查看个人借阅记录、借阅状态统计（借阅中/已归还/逾期）
- 借阅天数与数量限制（可在系统设置中配置）

### AI 智能助手
- **智能推荐**：根据用户阅读偏好，结合馆藏数据推荐图书
- **图书咨询**：自然语言问答，解答馆藏相关问题
- **多轮对话**：支持会话上下文记忆，对话超过 10 条自动生成摘要
- **会话管理**：创建、切换、查看历史会话

### 系统公告
- 公告的发布、编辑、删除（管理员权限）
- 公告优先级设置（普通/重要/紧急）
- 读者端公告浏览

### 数据统计
- 首页仪表盘展示：馆藏总量、在借数量、用户总数、逾期数量
- 快捷操作入口

## 项目结构

```
Library_AI/
├── sql/                              # 数据库脚本
│   └── library.sql                   # 完整数据库建表及初始数据
├── test-library/                     # 前端项目（Vue 2）
│   ├── src/
│   │   ├── components/               # 页面组件
│   │   │   ├── Login.vue             # 登录/注册页
│   │   │   ├── Layout.vue            # 整体布局（侧边栏+顶栏）
│   │   │   ├── Home.vue              # 首页仪表盘
│   │   │   ├── Books.vue             # 图书管理页
│   │   │   ├── Borrow.vue            # 借阅管理页（管理员）
│   │   │   ├── ReaderBorrow.vue      # 我的借阅页（读者）
│   │   │   ├── Users.vue             # 用户管理页
│   │   │   ├── AiRobot.vue           # AI 智能助手页
│   │   │   ├── Announcements.vue     # 系统公告页
│   │   │   ├── Profile.vue           # 个人中心页
│   │   │   └── common/               # 公共组件
│   │   │       ├── PageHeader.vue
│   │   │       ├── EmptyState.vue
│   │   │       └── StatCard.vue
│   │   ├── router/index.js           # 路由配置与权限守卫
│   │   ├── styles/                   # 全局样式
│   │   │   ├── global.css
│   │   │   └── variables.css
│   │   ├── App.vue
│   │   └── main.js
│   └── package.json
└── test-library-backend/             # 后端项目（Spring Boot）
    ├── src/main/java/com/library/
    │   ├── LibraryApplication.java   # 启动类
    │   ├── controller/               # 控制器层
    │   │   ├── AuthController.java   # 认证（登录/注册）
    │   │   ├── BookController.java   # 图书管理
    │   │   ├── BorrowController.java # 借阅管理
    │   │   ├── UserController.java   # 用户管理 & 个人中心
    │   │   ├── AiController.java     # AI 智能助手
    │   │   ├── AnnouncementController.java  # 系统公告
    │   │   └── StatsController.java  # 数据统计
    │   ├── entity/                   # 实体类
    │   │   ├── Book.java
    │   │   ├── Borrow.java
    │   │   ├── SysUser.java
    │   │   ├── SysRole.java
    │   │   ├── SysPermission.java
    │   │   ├── Announcement.java
    │   │   ├── AiConversation.java
    │   │   └── AiMessage.java
    │   ├── service/                  # 业务逻辑层
    │   ├── mapper/                   # MyBatis-Plus Mapper
    │   └── utils/                    # 工具类
    │       ├── JwtUtils.java         # JWT 工具
    │       ├── AiService.java        # AI 服务（调用通义千问 API）
    │       └── OssUtils.java         # 阿里云 OSS 工具
    └── src/main/resources/
        └── application.yml           # 应用配置
```

## 数据库设计

系统共包含以下核心数据表：

| 表名 | 说明 |
|------|------|
| `sys_user` | 系统用户表 |
| `sys_role` | 角色表（admin / librarian / reader） |
| `sys_permission` | 权限表 |
| `sys_user_role` | 用户-角色关联表 |
| `sys_role_permission` | 角色-权限关联表 |
| `sys_settings` | 系统设置表（借阅天数、罚款等） |
| `book` | 图书表 |
| `borrow` | 借阅记录表 |
| `announcement` | 系统公告表 |
| `ai_conversation` | AI 对话会话表 |
| `ai_message` | AI 对话消息表 |

## 快速开始

### 环境要求

- JDK 17+
- Node.js 14+
- MySQL 8.0+
- Maven 3.6+

### 1. 数据库初始化

```bash
# 登录 MySQL，创建数据库并导入
mysql -u root -p < sql/library.sql
```

### 2. 后端启动

```bash
cd test-library-backend

# 修改配置文件 src/main/resources/application.yml 中的数据库连接信息
# spring.datasource.username / password

# 启动后端服务（默认端口 8081）
mvn spring-boot:run
```

### 3. 前端启动

```bash
cd test-library

# 安装依赖
npm install

# 启动开发服务器（默认端口 8080）
npm run serve
```

### 4. 访问系统

打开浏览器访问 `http://localhost:8080`

#### 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 系统管理员 | sysadmin | admin123 |
| 图书管理员 | libadmin | admin123 |
| 读者 | zhangsan | 123456 |

### 5. AI 功能配置（可选）

AI 智能助手需要配置阿里云通义千问 API Key，在 `application.yml` 中设置：

```yaml
ai:
  api-key: ${AI_API_KEY:你的API密钥}
  model: qwen-turbo
  endpoint: https://dashscope.aliyuncs.com/compatible-mode/v1
```

或通过环境变量 `AI_API_KEY` 传入。

### 6. 头像上传配置（可选）

头像上传使用阿里云 OSS，需配置以下环境变量：

```bash
export OSS_ENDPOINT=oss-cn-beijing.aliyuncs.com
export OSS_ACCESS_KEY_ID=你的AccessKeyId
export OSS_ACCESS_KEY_SECRET=你的AccessKeySecret
export OSS_BUCKET_NAME=你的Bucket名称
```

## API 接口概览

| 模块 | 路径 | 说明 |
|------|------|------|
| 认证 | `POST /api/auth/login` | 用户登录 |
| 认证 | `POST /api/auth/register` | 用户注册 |
| 图书 | `GET /api/books` | 图书列表（分页、搜索） |
| 图书 | `POST /api/books` | 新增图书 |
| 图书 | `PUT /api/books/{id}` | 编辑图书 |
| 图书 | `DELETE /api/books/{id}` | 删除图书 |
| 借阅 | `GET /api/borrows` | 借阅记录列表 |
| 借阅 | `POST /api/borrows` | 办理借书 |
| 借阅 | `PUT /api/borrows/{id}/return` | 办理还书 |
| 借阅 | `GET /api/borrows/my` | 我的借阅记录 |
| 用户 | `GET /api/users` | 用户列表 |
| 用户 | `GET /api/profile` | 个人信息 |
| 用户 | `PUT /api/profile` | 更新个人信息 |
| 用户 | `POST /api/avatar/upload` | 上传头像 |
| AI | `POST /api/ai/session` | 创建 AI 会话 |
| AI | `POST /api/ai/chat` | AI 对话 |
| AI | `POST /api/ai/recommend` | 智能推荐 |
| 公告 | `GET /api/announcements` | 公告列表 |
| 统计 | `GET /api/stats` | 首页统计数据 |

## 系统截图

> 可根据实际情况补充系统截图

## 许可证

本项目仅供学习交流使用。
