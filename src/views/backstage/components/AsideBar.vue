<template>
  <el-aside :width="isCollapse ? '64px' : '264px'">
    <el-menu :collapse="isCollapse" :collapse-transition="false" default-active="2" class="menu-style">
      <div class="brand">
        <el-image :src="iconUrl" alt="logo" style="width: 50px; height: 50px; margin-right: 10px">
        </el-image>
        <div class="info-card" v-show="!isCollapse">
          <h1 class="brand-title">心理健康AI助手</h1>
          <p class="brand-subtitle">管理后台</p>
        </div>
      </div>
      <el-menu-item v-for="item in router.options.routes[0].children" :key="item.path" :index="item.path"
        @click="handleClick">
        <el-icon>
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta.title }}</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { useRouter } from "vue-router"
import { useAdminStore } from "@/stores/admin"
import { computed } from "vue"
const router = useRouter()
// 引入admin store中的isCollapse状态,控制是否折叠侧边栏
const AdminStore = useAdminStore()
const isCollapse = computed(() => AdminStore.isCollapse)

// 图标URL
const iconUrl = new URL("@/assets/images/心屿.png", import.meta.url).href

// 点击菜单项时跳转路由
const handleClick = (key) => {
  // console.log(key)
  // 获取当前路由路径（定义路由时的path）
  const currentPath = router.options.routes[0]
  // 点击菜单项时跳转路由
  router.push(`${currentPath.path}/${key.index}`)
}
</script>
<style lang="scss" scoped>
.menu-style {
  height: 100%;

  .brand {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    background-color: #fff;
    border-bottom: 1px solid #e5e7eb;

    .info-card {
      .brand-title {
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 5px;
        color: #1f2937;
      }

      .brand-subtitle {
        font-size: 14px;
        color: #6b7280;
      }
    }
  }
}
</style>
