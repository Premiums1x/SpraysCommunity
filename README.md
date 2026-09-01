# 校园流浪动物图鉴与动态打卡系统

面向校园师生与志愿者的流浪动物档案、近况记录和协作照护平台。项目采用 Spring Boot 3 + Vue 3 的前后端分离架构，并保持为易维护的模块化单体。

## 主要能力

- 公开浏览和筛选校园猫狗档案，无需登录
- 动物护照：别名、性别、性格标签、绝育情况、健康状态、首次发现日期与活跃时段
- 登录用户发布实名或匿名近况，查看自己的历史记录
- 管理员维护动物档案和封面图片
- 按动物查看近况时间轴，匿名记录在数据查询层完成脱敏
- 响应式界面、系统深色模式、加载/错误/空状态

## 技术栈

后端使用 Java 21、Spring Boot 3.5、MyBatis-Plus、MySQL 8、Flyway、JWT 和 BCrypt。前端使用 Vue 3、Vite、Pinia、Vue Router、Axios 与 Element Plus。

## 项目结构

```text
SpraysCommunity/
├── src/main/java/com/lancer/
│   ├── controller/       # HTTP 边界与权限声明
│   ├── service/          # 业务流程与事务边界
│   ├── mapper/           # 数据访问及关联查询
│   ├── dto/              # 输入、输出和分页契约
│   ├── entity/           # 数据库实体
│   ├── interceptor/      # JWT 认证和管理员授权
│   ├── config/           # 跨域、静态资源和初始化配置
│   └── common/           # 统一响应与异常语义
├── src/main/resources/db/migration/  # Flyway 数据库版本
├── src/test/                         # 权限、文件和查询边界测试
└── frontend/src/
    ├── api/              # 按业务域组织的请求
    ├── components/       # 认证外壳、打卡表单等复用组件
    ├── composables/      # 分页查询等组合逻辑
    ├── views/            # 页面
    ├── stores/           # 会话状态
    └── utils/            # 请求、日期和媒体工具
```

## 本地启动

环境要求：JDK 21、MySQL 8、Node.js 18+。仓库已包含 Maven Wrapper。

### 1. 创建空数据库

```sql
CREATE DATABASE strays_community
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;
```

表结构由 Flyway 在后端启动时自动创建和升级，不再手工执行建表脚本。

### 2. 设置运行环境

必须提供数据库密码和长度至少 32 个字符的 JWT 密钥。PowerShell 示例：

```powershell
$env:DB_PASSWORD = "你的数据库密码"
$env:JWT_SECRET = "请替换为至少32字符的随机高强度密钥"
```

可选配置：

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/strays_community?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai"
$env:DB_USERNAME = "root"
$env:CORS_ALLOWED_ORIGINS = "http://localhost:5173"
$env:UPLOAD_PATH = "./uploads/"
```

项目不再内置默认管理员。首次启动时可以临时提供：

```powershell
$env:BOOTSTRAP_ADMIN_USERNAME = "admin"
$env:BOOTSTRAP_ADMIN_PASSWORD = "请使用独立的强密码"
```

管理员创建后应清除这两个环境变量。若旧数据库仍保留历史示例账号，请在部署前替换或移除该账号。

### 3. 启动后端

```bash
./mvnw spring-boot:run
```

后端默认地址为 `http://localhost:8080`，API 文档位于 `http://localhost:8080/swagger-ui/index.html`。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认地址为 `http://localhost:5173`。

## 质量验证

```bash
./mvnw test
cd frontend
npm run check
```

后端测试覆盖认证/授权路径、数据库角色校验、图片内容识别与清理、关联查询边界。前端 `check` 会执行生产构建，验证 Vue 模板、模块引用和打包过程。

## 安全与部署说明

- `POST/PUT/DELETE /api/animals/**` 必须登录且必须为当前数据库中的管理员角色。
- 公开接口仅限动物列表、动物详情和对应近况的 `GET` 请求。
- 图片扩展名由文件签名确定，上传目录会进行规范化路径校验。
- CORS 默认只允许本地前端；生产环境应显式设置实际域名。
- API 的业务错误会返回相应 HTTP 状态码，服务端异常不会向客户端暴露内部消息。
- 升级已有数据库前请备份数据。若历史数据违反外键或检查约束，Flyway 会停止迁移并要求先修复数据。
