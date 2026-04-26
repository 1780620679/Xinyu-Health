<template>
  <div class="frontend-layout">
    <div class="navbar-container">
      <div class="navbar-content">
        <div class="brand-section">
          <el-image :src="logo" alt="logo" class="brand-logo" />
          <h1 class="brand-name">心屿</h1>
        </div>
        <div class="nav-section">
          <router-link to="/" class="nav-item" :class="{ 'active-nav': $route.path === '/' }">首页</router-link>
          <router-link to="/consultation" class="nav-item" v-if="isLogin"
            :class="{ 'active-nav': $route.path === '/consultation' }">AI助手</router-link>
          <router-link to="/emotion-diary" class="nav-item" v-if="isLogin"
            :class="{ 'active-nav': $route.path === '/emotion-diary' }">心情日志</router-link>
          <router-link to="/knowledge" class="nav-item"
            :class="{ 'active-nav': $route.path === '/knowledge' || $route.path.startsWith('/knowledge/article/') }">知识库</router-link>
          <div class="nav-divider"></div>
          <el-switch v-model="isDark" @change="handleThemeChange" inline-prompt active-icon="Moon"
            inactive-icon="Sunny" />
          <el-button v-if="isLogin" @click="handleLogout" class="logout-btn">退出</el-button>
          <template v-else>
            <router-link to="/auth/login" class="nav-item">登录</router-link>
            <router-link to="/auth/register" class="nav-item nav-highlight">注册</router-link>
          </template>
        </div>
      </div>
    </div>
    <div class="main-content">
      <router-view></router-view>
    </div>
    <div class="footer-container">
      <div class="footer-content">
        <div class="footer-brand">
          <span class="footer-brand-name">心屿</span>
          <span class="footer-tagline">—— 您的专属心理健康伙伴</span>
        </div>
        <p class="footer-copyright">© 2026 心屿心理健康助手</p>
      </div>
    </div>
  </div>
</template>
<script setup>
import { LogoutAPI } from "@/apis/admin";
import { useAdminStore } from "@/stores/admin"
import { onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router"

const router = useRouter()
const adminStore = useAdminStore()
import { ElMessage } from "element-plus"
const isLogin = ref(false)
const logo = new URL('@/assets/images/心屿.png', import.meta.url).href

const isDark = ref(adminStore.isDark)
watch(() => adminStore.isDark, (newVal) => {
  isDark.value = newVal
})

const handleThemeChange = (value) => {
  adminStore.isDark = value
  if (value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

const handleLogout = async () => {
  await LogoutAPI()
  adminStore.clearUser()
  ElMessage.success("退出登录成功")
  router.push("/auth/login")
}

onMounted(() => {
  isLogin.value = !!adminStore.token
  isDark.value = adminStore.isDark
  if (adminStore.isDark) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
})
</script>

<style scoped lang="scss">
.frontend-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #FFF9F5;

  html.dark & {
    background-color: #1a1a1a;
  }

  .navbar-container {
    position: sticky;
    top: 0;
    z-index: 100;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    box-shadow: 0 2px 20px rgba(255, 107, 107, 0.08);

    html.dark & {
      background: rgba(45, 45, 45, 0.95);
      box-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
    }

    .navbar-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 40px;
      height: 70px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .brand-section {
        display: flex;
        align-items: center;
        gap: 12px;

        .brand-logo {
          width: 42px;
          height: 42px;
          border-radius: 12px;
          box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
        }

        .brand-name {
          font-size: 26px;
          font-weight: 800;
          background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          font-family: 'Microsoft YaHei', sans-serif;
          letter-spacing: 3px;
          margin: 0;

          html.dark & {
            background: linear-gradient(135deg, #FFB4B4 0%, #FF9A56 100%);
            -webkit-background-clip: text;
            background-clip: text;
          }
        }
      }

      .nav-section {
        display: flex;
        align-items: center;
        gap: 32px;

        .nav-item {
          position: relative;
          color: #666;
          font-size: 15px;
          font-weight: 500;
          font-family: 'Microsoft YaHei', sans-serif;
          text-decoration: none;
          padding: 8px 0;
          transition: all 0.3s ease;

          &::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 50%;
            width: 0;
            height: 2px;
            background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
            transition: all 0.3s ease;
            transform: translateX(-50%);
          }

          &:hover {
            color: #FF6B6B;

            &::after {
              width: 100%;
            }
          }

          &.active-nav {
            color: #FF6B6B;

            &::after {
              width: 100%;
            }
          }

          html.dark & {
            color: #999;

            &:hover {
              color: #FF9A56;
            }

            &.active-nav {
              color: #FF9A56;

              &::after {
                width: 100%;
              }
            }
          }
        }

        .nav-divider {
          width: 1px;
          height: 24px;
          background: #eee;

          html.dark & {
            background: #444;
          }
        }

        .logout-btn {
          color: #999;
          font-size: 14px;
          border: none;
          background: transparent;

          &:hover {
            color: #FF6B6B;
          }

          html.dark & {
            color: #999;

            &:hover {
              color: #FF9A56;
            }
          }
        }

        .nav-highlight {
          padding: 8px 20px !important;
          background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
          color: white !important;
          border-radius: 20px;
          margin-left: 8px;

          &::after {
            display: none;
          }

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
          }

          html.dark & {
            background: linear-gradient(135deg, #FFB4B4 0%, #FF9A56 100%);
          }
        }
      }
    }
  }

  .main-content {
    flex: 1;
  }

  .footer-container {
    background: linear-gradient(135deg, #FFF5F0 0%, #FFF0EA 100%);
    padding: 40px 0;
    margin-top: 60px;

    html.dark & {
      background: linear-gradient(135deg, #2d2d2d 0%, #252525 100%);
    }

    .footer-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 40px;
      text-align: center;

      .footer-brand {
        margin-bottom: 16px;

        .footer-brand-name {
          font-size: 24px;
          font-weight: 700;
          background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          font-family: 'Microsoft YaHei', sans-serif;
          letter-spacing: 2px;
        }

        .footer-tagline {
          color: #999;
          font-size: 14px;
          margin-left: 12px;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #777;
          }
        }
      }

      .footer-copyright {
        color: #bbb;
        font-size: 13px;
        margin: 0;
        font-family: 'Microsoft YaHei', sans-serif;

        html.dark & {
          color: #666;
        }
      }
    }
  }
}
</style>
