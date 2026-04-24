<template>
  <div class="frontend-layout">
    <!-- 头部导航栏 -->
    <div class="navbar-container">
      <div class="brand-section">
        <el-image :src="logo" alt="logo" class="brand-logo" style="height: 50px; width: 50px;"></el-image>
        <h1 class="brand-name">智能AI助手</h1>
      </div>
      <div class="nav-section">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/consultation" class="nav-link" v-if="isLogin">AI助手</router-link>
        <router-link to="/emotion-diary" class="nav-link" v-if="isLogin">情绪日记</router-link>
        <router-link to="/knowledge" class="nav-link">知识库</router-link>
        <el-button v-if="isLogin" @click="handleLogout" class="logout-btn">退出登录</el-button>
        <template v-else>
          <router-link to="/auth/login" class="nav-link">登录</router-link>
          <router-link to="/auth/register" class="nav-link">
            <el-button type="primary">注册</el-button>
          </router-link>
        </template>
      </div>
    </div>
    <!-- 主要内容区域 -->
    <div class="main-content">
      <router-view></router-view>
    </div>
    <!-- 页脚 -->
    <div class="footer-container">
      <div class="footer-bottom">
        <p>智能AI助手 &copy; 2026</p>
      </div>
    </div>
  </div>
</template>
<script setup>
import { LogoutAPI } from "@/apis/admin";
import { useAdminStore } from "@/stores/admin"
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router"

const router = useRouter()
const adminStore = useAdminStore()
import { ElMessage } from "element-plus"
const isLogin = ref(false)
const logo = new URL('@/assets/images/机器人.png', import.meta.url).href


//登出
const handleLogout = async () => {
  // 登出接口
  await LogoutAPI()
  // 清除token,userInfo
  adminStore.clearUser()
  // 登出成功后，跳转到登录页
  ElMessage.success("退出登录成功")
  router.push("/auth/login")
}

onMounted(() => {
  // 检查是否有token，如果有则设置isLogin为true
  isLogin.value = !!adminStore.token
  //   - !!"" → false （空字符串）
  // - !!"hello" → true （非空字符串）
})
</script>

<style scoped lang="scss">
.frontend-layout {
  background-color: #fff;

  .navbar-container {
    max-width: 1200px;
    height: 100%;
    margin: 0 auto;
    padding: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .brand-section {
      display: flex;
      align-items: center;

      .brand-name {
        margin-left: 10px;
        font-size: 24px;
        font-weight: 600;
        color: #333;
      }
    }

    .nav-section {
      display: flex;
      align-items: center;
      gap: 40px;

      .nav-link {
        color: #4b5563;
        font-size: 16px;
        font-weight: 500;

        &:hover {
          color: #4A90E2;
        }
      }
    }
  }

  .footer-container {
    background: #1f2937;
    color: white;
    padding: 15px 0;
    margin-top: auto;

    .footer-bottom {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 10px;
      text-align: center;
    }
  }
}
</style>
