import { createRouter, createWebHistory } from "vue-router"
import BackStageLayout from "@/components/BackStageLayout.vue"
import AuthLayout from "@/components/AuthLayout.vue"
// 后台路由
const BackRoutes = [
  {
    path: "/back",
    component: BackStageLayout,
    children: [
      {
        // 数据分析路由
        path: "dashboard",
        component: () => import("@/views/dashboard.vue"),
        //meta对象，用于配置路由的元数据，如标题、图标等，可以在AsideBar.vue中使用meta.title和meta.icon显示
        meta: {
          title: "数据分析",
          icon: "PieChart",
        },
      },
      {
        //知识文章路由
        path: "knowledge",
        component: () => import("@/views/knowledge.vue"),
        meta: {
          title: "知识文章",
          icon: "ChatLineSquare",
        },
      },
      {
        //咨询记录路由
        path: "consultation",
        component: () => import("@/views/consultation.vue"),
        meta: {
          title: "咨询记录",
          icon: "Message",
        },
      },
      {
        //情绪日志路由
        path: "emotional",
        component: () => import("@/views/emotional.vue"),
        meta: {
          title: "情绪日志",
          icon: "User",
        },
      },
    ],
  },
  {
    path: "/auth",
    component: AuthLayout,
    children: [
      {
        path: "login",
        component: () => import("@/views/login.vue"),
        meta: {
          title: "登录",
        },
      },
      {
        path: "register",
        component: () => import("@/views/register.vue"),
        meta: {
          title: "注册",
        },
      },
    ],
  },
]
// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes: BackRoutes,
})

// 导出路由实例
export default router
