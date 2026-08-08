# 心屿健康（XinYu-Health）心理健康服务平台

一个前后端分离的全栈心理健康服务平台，整合 AI 对话能力，提供心理测评、情绪日记、AI 心理咨询、知识库阅读等核心功能。

## 技术栈

### 前端（`vue-ai-project/`）
- **框架**：Vue 3 + TypeScript
- **构建工具**：Vite
- **UI 组件库**：Element Plus
- **状态管理**：Pinia（持久化）
- **路由**：Vue Router 4
- **图表**：ECharts
- **富文本编辑器**：wangEditor
- **HTTP 请求**：Axios

### 后端（`springboot-ai-project/`）
- **框架**：Spring Boot 3.5（Java 17）
- **持久层**：MyBatis-Plus + MySQL
- **安全认证**：Spring Security + JWT
- **AI 能力**：Spring AI（OpenAI）
- **文件存储**：阿里云 OSS
- **其他**：Lombok、Hutool、参数校验

## 项目结构

```
XinYu/
├── vue-ai-project/              # 前端项目
│   ├── src/                     # 前端源码
│   │   ├── views/               # 页面视图
│   │   ├── components/          # 组件
│   │   ├── api/                 # 接口请求
│   │   ├── store/               # 状态管理
│   │   └── router/              # 路由配置
│   ├── package.json
│   └── vite.config.js
└── springboot-ai-project/       # 后端项目
    ├── src/main/java/           # 后端源码
    │   └── com/example/springbootaiproject/
    │       ├── controller/      # 控制器
    │       ├── service/         # 业务逻辑
    │       ├── mapper/          # 数据访问
    │       ├── entity/          # 实体类
    │       ├── DTO/             # 数据传输对象
    │       └── config/          # 配置类
    ├── pom.xml
    └── mental_health_assistant.sql   # 数据库脚本
```

## 环境要求

- **Node.js**：18+
- **Java**：17+
- **Maven**：3.6+
- **MySQL**：8.0+
- **包管理器**：pnpm / npm

## 快速启动

### 1. 数据库初始化

导入 `springboot-ai-project/mental_health_assistant.sql` 脚本到本地 MySQL，创建数据库及表结构。

### 2. 启动后端

```bash
cd springboot-ai-project
mvn spring-boot:run
```

> 说明：后端默认端口及 AI、OSS 等配置请在 `application.yml` 中按需修改。

### 3. 启动前端

```bash
cd vue-ai-project
npm install    # 或 pnpm install
npm run dev    # 或 pnpm dev
```

首次启动时，在 Windows PowerShell 中执行 `Copy-Item .env.example .env` 创建本地配置。如果本地已经存在 `.env`，请保留原文件，只需确认包含以下配置：

```dotenv
DEV_PORT=5173
API_PROXY_TARGET=http://localhost:1920
```

启动后，浏览器访问前端开发地址（默认 `http://localhost:5173`）即可。开发环境中的 `/api` 请求会由 Vite 转发至 `http://localhost:1920`。

## 主要功能

- 👤 用户注册 / 登录（JWT 认证）
- 🧠 AI 心理咨询对话
- 📝 情绪日记记录与趋势分析
- 📚 心理健康知识文章阅读
- 📊 管理后台数据统计与看板
- 🎯 用户心理状态评估

## 说明

- `.gitignore` 已忽略学习笔记（`.md` / `.txt`）、编译产物、依赖包等文件。
