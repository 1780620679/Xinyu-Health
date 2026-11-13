# 心屿 AI 项目

## 项目简介

心屿是一款基于 Vue 3 的人工智能应用平台，提供情绪管理、知识咨询等功能。项目分为前台用户端和后台管理系统，旨在为用户提供智能的情绪分析和知识服务。
在线预览：https://vue-ai-project.netlify.app/
## 技术栈

- **前端框架**：Vue 3
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **UI 组件库**：Element Plus
- **构建工具**：Vite
- **TypeScript**：支持类型检查
- **图表库**：ECharts
- **富文本编辑器**：WangEditor
- **HTTP 客户端**：Axios
- **样式预处理器**：Sass

## 项目结构

```
├── public/            # 静态资源
├── src/
│   ├── apis/          # API 调用
│   │   ├── backend/   # 后台 API
│   │   └── frontend/  # 前台 API
│   ├── assets/        # 资源文件
│   │   ├── css/       # 样式文件
│   │   └── images/    # 图片资源
│   ├── components/    # 通用组件
│   ├── config/        # 配置文件
│   ├── directives/    # 自定义指令
│   ├── router/        # 路由配置
│   ├── stores/        # 状态管理
│   ├── types/         # TypeScript 类型定义
│   ├── utils/         # 工具函数
│   ├── views/         # 页面组件
│   │   ├── backstage/ # 后台页面
│   │   ├── frontstage/# 前台页面
│   │   └── login/     # 登录注册页面
│   ├── App.vue        # 应用根组件
│   ├── main.js        # 应用入口
│   └── style.css      # 全局样式
├── .gitignore
├── package.json
├── tsconfig.json
└── vite.config.js
```

## 功能模块

### 前台功能

1. **首页**：项目介绍和功能导航
2. **咨询**：智能咨询服务
3. **情绪日记**：记录和分析用户情绪
4. **知识文章**：浏览知识文章列表
5. **知识文章详情**：查看文章详细内容

### 后台功能

1. **数据分析**：查看系统数据统计和分析
2. **知识文章管理**：管理知识文章的发布和编辑
3. **咨询记录**：查看用户咨询记录
4. **情绪日志**：查看和分析用户情绪日志

### 用户认证

- **登录**：用户登录系统
- **注册**：新用户注册

## 安装和运行

### 安装依赖

```bash
# 使用 npm
npm install

# 使用 pnpm
pnpm install
```

### 开发环境运行

```bash
# 使用 npm
npm run dev

# 使用 pnpm
pnpm dev
```

### 构建生产版本

```bash
# 使用 npm
npm run build

# 使用 pnpm
pnpm build
```

### 预览生产构建

```bash
# 使用 npm
npm run preview

# 使用 pnpm
pnpm preview
```

## 路由配置

### 前台路由

- `/` - 首页
- `/consultation` - 咨询页面
- `/emotion-diary` - 情绪日记页面
- `/knowledge` - 知识文章列表
- `/knowledge/article/:id` - 知识文章详情

### 后台路由

- `/back/dashboard` - 数据分析
- `/back/knowledge` - 知识文章管理
- `/back/consultation` - 咨询记录
- `/back/emotional` - 情绪日志管理

### 认证路由

- `/auth/login` - 登录页面
- `/auth/register` - 注册页面

## 项目特点

1. **前后台分离**：清晰的前后台功能划分
2. **响应式设计**：适配不同设备屏幕
3. **TypeScript 支持**：提供类型安全
4. **模块化结构**：代码组织清晰，易于维护
5. **智能情绪分析**：基于 AI 的情绪识别和分析
6. **丰富的可视化**：使用 ECharts 提供数据可视化

## 注意事项

- 项目使用 Vite 构建工具，需要 Node.js 14.18+ 版本
- 开发环境下，API 请求可能需要配置代理
- 生产环境部署时，需要配置正确的后端 API 地址

## 许可证

MIT License
