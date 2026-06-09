# 🐾 校园流浪动物图鉴与动态打卡系统

基于 Spring Boot 3 + Vue 3 的前后端分离全栈项目，用于校园流浪动物信息管理与近况打卡。

## 📋 项目简介

本系统是一个面向校园师生的流浪动物管理平台，核心功能包括：

- **动物图鉴管理**：管理员录入、更新和维护校园流浪动物电子档案
- **偶遇打卡动态**：用户在校园内偶遇小动物时发布近况打卡
- **时间轴查询**：按动物查看历史打卡动态，了解动物近况

## 🛠️ 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.14 | 核心框架 |
| Java | 21 | 开发语言 |
| MyBatis-Plus | 3.5.12 | ORM 持久层 |
| MySQL | 8.x | 关系型数据库 |
| JWT (jjwt) | 0.12.6 | 用户认证 |
| Knife4j | 4.5.0 | API 文档 |

### 前端
| 技术 | 说明 |
|------|------|
| Vue 3 | 前端框架（Composition API） |
| Vite | 构建工具 |
| Vue Router 4 | 路由管理 |
| Pinia | 状态管理 |
| Axios | HTTP 请求 |
| Element Plus | UI 组件库 |

## 📁 项目结构

```
StraysCommunity/
├── src/main/java/com/lancer/     # 后端 Java 源码
│   ├── config/                   # 配置类（CORS、MyBatis-Plus、WebMvc）
│   ├── common/                   # 通用组件（Result、异常处理）
│   ├── entity/                   # 实体类
│   ├── dto/                      # 数据传输对象
│   ├── mapper/                   # MyBatis-Plus Mapper
│   ├── service/                  # 业务逻辑层
│   ├── controller/               # RESTful 控制器
│   ├── interceptor/              # JWT 拦截器
│   ├── annotation/               # 自定义注解
│   └── utils/                    # 工具类
├── src/main/resources/
│   ├── application.yaml          # 应用配置
│   └── sql/init.sql              # 数据库初始化脚本
├── frontend/                     # Vue 3 前端项目
│   ├── src/views/                # 页面组件
│   ├── src/stores/               # Pinia 状态管理
│   ├── src/router/               # 路由配置
│   ├── src/utils/                # 工具（Axios 封装）
│   └── src/layout/               # 布局组件
└── pom.xml                       # Maven 配置
```

## 🚀 快速开始

### 环境要求
- JDK 21+
- MySQL 8.x
- Node.js 18+
- Maven 3.9+

### 1. 初始化数据库

```sql
-- 在 MySQL 中执行建表脚本
source src/main/resources/sql/init.sql
```

### 2. 修改配置

编辑 `src/main/resources/application.yaml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/strays_community?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 3. 启动后端

```bash
./mvnw spring-boot:run
```

后端启动于 http://localhost:8080

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动于 http://localhost:5173

### 5. 访问系统

- 前端页面：http://localhost:5173
- API 文档：http://localhost:8080/doc.html
- 默认管理员：用户名 `admin`，密码 `admin123`

## 📡 API 接口

### 认证模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| GET | `/api/auth/info` | 获取当前用户信息 |

### 动物图鉴模块
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/animals` | 分页查询动物列表 |
| GET | `/api/animals/{id}` | 获取动物详情 |
| POST | `/api/animals` | 新增动物档案（管理员） |
| PUT | `/api/animals/{id}` | 更新动物信息（管理员） |
| DELETE | `/api/animals/{id}` | 删除动物档案（管理员） |

### 打卡动态模块
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/checkins` | 发布打卡 |
| GET | `/api/animals/{id}/checkins` | 动物打卡时间轴 |
| GET | `/api/checkins/my` | 我的打卡记录 |

## 👥 系统角色

| 角色 | 权限 |
|------|------|
| 普通用户 | 浏览动物信息、发布打卡动态、查看时间轴 |
| 管理员 | 全部普通用户权限 + 动物档案 CRUD 管理 |

## 📸 功能特性

- ✅ JWT 无状态认证，前后端分离
- ✅ 动物档案 CRUD（含封面图片上传）
- ✅ 按名字模糊搜索、按类型筛选、分页查询
- ✅ 偶遇打卡发布与时间轴展示
- ✅ 基于角色的权限控制（@RequireAdmin 注解）
- ✅ RESTful API 设计 + Knife4j 接口文档
- ✅ 全局异常处理 + 参数校验
- ✅ MyBatis-Plus 逻辑删除 + 自动填充
