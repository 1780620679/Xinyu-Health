import { createRouter, createWebHistory } from 'vue-router'
import BackStageLayout from '@/components/BackStageLayout.vue'
// 后台路由
const BackRoutes=[
  {
    path:'/back',
    component:BackStageLayout,
    children:[
      {
        //控制台路由
        path:'dashboard',
        component:()=>import('@/views/dashboard.vue'),
        meta:{
          title:'数据分析',
          icon:'PieChart'
        }
      },
      {
        //知识文章路由
        path:'knowledge',
        component:()=>import('@/views/knowledge.vue'),
        meta:{
          title:'知识文章',
          icon:'ChatLineSquare'
        }
      },
      {
        //咨询记录路由
        path:'consultation',
        component:()=>import('@/views/consultation.vue'),
        meta:{
          title:'咨询记录',
          icon:'Message'
        }
      },
      {
        //情绪日志路由
        path:'emotional',
        component:()=>import('@/views/emotional.vue'),
        meta:{
          title:'情绪日志',
          icon:'User'
        }
      }
    ]
  }
]
// 创建路由实例
const router = createRouter({
  history:createWebHistory(),
  routes:BackRoutes
})

// 导出路由实例
export default router
