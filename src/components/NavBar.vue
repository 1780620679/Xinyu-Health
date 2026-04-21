<template>
  <div class="nav-bar">
    <div class="flex-box">
      <el-button @click="handleCollapse">
        <el-icon><Expand /></el-icon>
      </el-button>
      <p class="page-title">导航栏</p>
    </div>
    <div class="flex-box">
      <el-dropdown @command="handleCommand">
        <div class="flex-box">
          <el-avatar
            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
            size="default"
          ></el-avatar>
          <p class="user-name">用户</p>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="user-center">用户中心</el-dropdown-item>
            <el-dropdown-item command="setting">设置</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>
<script setup>
import { useAdminStore } from "@/stores/admin"
const handleCommand = (command) => {
  console.log(command)
}
// 引入admin store中的isCollapse状态,控制是否折叠侧边栏
const AdminStore = useAdminStore()
const handleCollapse = () => {
  AdminStore.toggleCollapse()
}
</script>
<style lang="scss" scoped>
.nav-bar {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  border-bottom: 1px solid #e5e7eb;
  .flex-box {
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .page-title {
    margin-left: 20px;
    font-size: 26px;
    font-weight: bold;
    color: #1f2937;
  }
  .user-name {
    margin: 0 5px;
    font-weight: bold;
  }
}
</style>
