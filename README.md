# 心屿健康（XinYu-Health）

一个前后端分离的心理健康服务平台。以 Spring AI 驱动的流式 AI 心理咨询为核心，配合情绪日记、心理健康知识库与后台运营看板，覆盖「咨询 → 记录 → 学习 → 分析」的完整闭环。


在线访问地址：http://47.117.119.70

## 核心功能

**前台（用户端）**

- 账号注册 / 登录，JWT 无状态认证，路由按用户类型分流
- AI 心理咨询：SSE 流式输出、多会话管理、历史记录与记忆窗口、中断即取消上游请求
- AI 情绪分析：每轮对话结束后对回复做结构化情绪分析，输出情绪标签与四级风险分级
- 情绪日记：记录每日心情、情绪趋势可视化
- 心理健康知识库：分类树浏览、文章详情、富文本正文与图片懒加载
- 个人中心与偏好设置（含暗色主题）

**后台（管理端）**

- 数据看板：平台概览、咨询统计、用户活跃度、情绪趋势（ECharts）
- 知识库管理：分类维护、文章增删改查、富文本编辑、上下架
- 咨询记录管理：分页查询会话与消息
- 情绪日记管理：分页筛选、删除

**通用能力**

- 文件上传：阿里云 OSS 图床，用于头像、文章封面与富文本内图，上传记录落库 `sys_file_info`
- 统一响应体 `Result` + 全局异常处理，业务异常码集中管理

## 界面预览

<table>
  <tr>
    <td align="center"><img src="./docs/images/home.png" width="420" /><br /><sub>首页</sub></td>
    <td align="center"><img src="./docs/images/consultation.png" width="420" /><br /><sub>AI 心理咨询（流式输出 + 情绪分析）</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="./docs/images/emotion-diary.png" width="420" /><br /><sub>情绪日记与趋势</sub></td>
    <td align="center"><img src="./docs/images/knowledge.png" width="420" /><br /><sub>心理健康知识库</sub></td>
  </tr>
  <tr>
    <td align="center"><img src="./docs/images/dashboard.png" width="420" /><br /><sub>后台数据看板</sub></td>
    <td align="center"><img src="./docs/images/admin-knowledge.png" width="420" /><br /><sub>后台知识库管理</sub></td>
  </tr>
</table>

## 技术栈

### 前端（`vue-ai-project/`）

| 类别 | 选型 |
| --- | --- |
| 框架 | Vue 3.5 + TypeScript |
| 构建 | Vite 8 |
| UI | Element Plus 2.13 |
| 状态管理 | Pinia 3 + persistedstate |
| 路由 | Vue Router 4（hash 模式） |
| 图表 | ECharts 6 |
| 富文本 | wangEditor 5 |
| 请求 | Axios；SSE 使用 `@microsoft/fetch-event-source` |
| 其他 | VueUse、highlight.js、Sass |

### 后端（`springboot-ai-project/`）

| 类别 | 选型 |
| --- | --- |
| 运行时 | Java 17 + Spring Boot 3.5 |
| 持久层 | MyBatis-Plus 3.5.7 + MySQL 8（无 XML，注解 + Wrapper） |
| 安全 | Spring Security + `java-jwt` 4.4 |
| AI | Spring AI 1.0.0-SNAPSHOT（OpenAI 协议接入 DeepSeek） |
| 对象存储 | 阿里云 OSS SDK 3.17 |
| 工具 | Lombok、Hutool 5.8、Bean Validation |

> AI 侧通过 Spring AI 的 OpenAI Starter 接入兼容 OpenAI 协议的 DeepSeek，模型与 base-url 在 `application.yml` 中配置。

## 项目结构

```
XinYu/
├── vue-ai-project/                     # 前端
│   ├── src/
│   │   ├── views/
│   │   │   ├── frontstage/             # 前台：首页、咨询、情绪日记、知识库
│   │   │   ├── backstage/              # 后台：看板、知识库、咨询记录、情绪日志
│   │   │   └── login/                  # 登录 / 注册
│   │   ├── apis/                       # 接口封装（frontend / backend 分目录）
│   │   ├── components/                 # 富文本、Markdown、用户中心、骨架屏等通用组件
│   │   ├── composables/                # useChatStream（SSE 封装）
│   │   ├── stores/                     # Pinia
│   │   ├── router/                     # 路由与登录守卫
│   │   ├── directives/                 # 图片懒加载
│   │   ├── utils/                      # Axios 实例与拦截器
│   │   ├── types/                      # 前后台类型定义
│   │   └── assets/                     # 图片、暗色主题样式
│   ├── .env.example
│   └── vite.config.js
└── springboot-ai-project/              # 后端
    ├── src/main/java/com/example/springbootaiproject/
    │   ├── controller/                 # 用户、AI 咨询、情绪日记、知识库、数据统计、文件
    │   ├── service/                    # 业务逻辑 + convert（DTO ↔ VO）
    │   ├── mapper/                     # MyBatis-Plus Mapper
    │   ├── entity/                     # 数据库实体
    │   ├── DTO/                        # command（入参）/ response（出参）
    │   ├── AiService/                  # Prompt 管理、情绪分析、结构化输出
    │   ├── config/                     # Security、JWT、ChatClient、MyBatis-Plus
    │   ├── common/                     # 统一响应、异常处理
    │   ├── enumClass/                  # 用户类型、状态枚举
    │   └── utils/                      # JWT 工具、认证过滤器
    ├── src/main/resources/application.yml
    ├── mental_health_assistant.sql     # 建表 + 初始数据
    └── Dockerfile
```

## 环境要求

- Node.js 18+，pnpm 或 npm
- JDK 17+，Maven 3.6+
- MySQL 8.0+

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -p -e "CREATE DATABASE mental_health_assistant DEFAULT CHARSET utf8mb4;"
mysql -u root -p mental_health_assistant < springboot-ai-project/mental_health_assistant.sql
```

### 2. 配置并启动后端

后端配置从环境变量读取，敏感信息不落库。启动前需设置：

| 变量 | 说明 |
| --- | --- |
| `DB_USERNAME` / `DB_PASSWORD` | MySQL 账号密码（`DB_URL` 可选，默认本地 `mental_health_assistant`） |
| `DEEPSEEK_API_KEY` | DeepSeek API Key |
| `JWT_SECRET` | JWT 签名密钥 |
| `OSS_ACCESS_KEY_ID` / `OSS_ACCESS_KEY_SECRET` | 阿里云 OSS 凭证 |
| `FRONTEND_ORIGIN` | 跨域白名单，默认 `http://localhost:5174` |
| `PORT` | 服务端口，默认 `1920` |

```bash
cd springboot-ai-project
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd vue-ai-project
pnpm install          # 或 npm install
pnpm dev              # 或 npm run dev
```

首次启动前复制环境变量文件（Windows PowerShell）：

```powershell
Copy-Item .env.example .env
```

`.env` 内容：

```dotenv
DEV_PORT=5174                            # 前端开发端口
API_PROXY_TARGET=http://localhost:1920   # 本地后端地址，/api 由 Vite 代理
VITE_API_BASE_URL=                       # 生产环境后端地址，本地留空
```

访问 `http://localhost:5174` 即可。

## 接口概览

所有接口统一以 `/api` 为前缀，除登录注册外均需在 `Authorization` 头携带 `Bearer <token>`。

| 模块 | 主要接口 |
| --- | --- |
| 用户 | `POST /user/login`、`POST /user/add`、`GET /user/current`、`POST /user/logout` |
| AI 咨询 | `POST /psychological-chat/session/start`、`POST /psychological-chat/stream`（SSE） |
| 会话 | `GET /psychological-chat/sessions`、`GET /sessions/{id}/messages`、`DELETE /sessions/{id}`、`GET /session/{id}/emotion` |
| 情绪日记 | `POST /emotion-diary`、`GET /emotion-diary/admin/page`、`DELETE /emotion-diary/admin/{id}` |
| 知识库 | `GET /knowledge/category/tree`、`GET /knowledge/article/page`、`GET /knowledge/article/{id}`、文章增删改与上下架 |
| 数据统计 | `GET /data-analytics/overview` |
| 文件 | `POST /file/upload`、`DELETE /file/delete` |

