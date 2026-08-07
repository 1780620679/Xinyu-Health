import { createRouter, createWebHashHistory } from "vue-router"
import AuthLayout from "@/views/login/components/AuthLayout.vue"
import { useAdminStore } from "@/stores/admin"
import FrontStageLayout from "@/views/frontstage/FrontStageLayout.vue"
import BackStageLayout from "@/views/backstage/BackStageLayout.vue"

// 后台路由
const BackRoutes = [
  {
    path: "/back",
    redirect: "/back/dashboard",
    component: BackStageLayout,
    children: [
      {
        // 数据分析路由
        path: "dashboard",
        component: () => import("@/views/backstage/dashboard.vue"),
        //meta对象，用于配置路由的元数据，如标题、图标等，可以在AsideBar.vue中使用meta.title和meta.icon显示
        meta: {
          title: "数据分析",
          icon: "PieChart",
        },
      },
      {
        //知识文章路由
        path: "knowledge",
        component: () => import("@/views/backstage/knowledge.vue"),
        meta: {
          title: "知识文章",
          icon: "ChatLineSquare",
        },
      },
      {
        //咨询记录路由
        path: "consultation",
        component: () => import("@/views/backstage/consultation.vue"),
        meta: {
          title: "咨询记录",
          icon: "Message",
        },
      },
      {
        //情绪日志路由
        path: "emotional",
        component: () => import("@/views/backstage/emotional.vue"),
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
        component: () => import("@/views/login/login.vue"),
        meta: {
          title: "登录",
        },
      },
      {
        path: "register",
        component: () => import("@/views/login/register.vue"),
        meta: {
          title: "注册",
        },
      },
    ],
  },
]
// 前台路由
const FrontRoutes = [
  {
    path: "/",
    component: FrontStageLayout,
    children: [
      {
        path: "",
        component: () => import("@/views/frontstage/home.vue"),
      },
      {
        path: "consultation",
        component: () => import("@/views/frontstage/consultation.vue"),
      },
      {
        path: "emotion-diary",
        component: () => import("@/views/frontstage/emotionDiary.vue"),
      },
      {
        path: "knowledge",
        component: () => import("@/views/frontstage/knowledge.vue"),
      },
      {
        path: "knowledge/article/:id",
        component: () => import("@/views/frontstage/knowledgeArticleDetail.vue"),
      }
    ],
  },
]


// 创建路由实例
const router = createRouter({
  history: createWebHashHistory(),
  routes: [...BackRoutes, ...FrontRoutes],
})


// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 检查用户是否已登录
  const adminStore = useAdminStore()
  const token = adminStore.token
  if (token) {
    const userInfo = adminStore.userInfo
    if (userInfo.userType == 2) {//如果是后台路由
      if (to.path.startsWith("/back")) {
        next()
      } else {
        next('/back/dashboard')
      }
    } else if(userInfo.userType == 1) {//如果是前台
      //用户端账号只能访问前台路由
      if (to.path.startsWith("/back")||to.path.startsWith("/auth")) {
        next('/')
      } else {
        next()
      }
    }
   
  } else {
    // 如果用户未登录,判断是否是后台路由
    if (to.path.startsWith("/back")) {
      //如果是后台路由，重定向到登录页
      next('/auth/login')
    } else {
      next()
    }
  }
})

// 导出路由实例
export default router

