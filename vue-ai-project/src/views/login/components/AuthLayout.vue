<template>
  <div class="auth-layout">
    <div class="left-section">
      <div class="content">
        <h2 class="title">心屿</h2>
        <p class="text">
          每个深夜，每个焦虑的时刻，我们都在这里。不必独自承受，让心与心的连接温暖您的每一天
        </p>
        <div class="robot">
          <el-image style="width: 164px; height: 164px; border-radius: 50%" :src="iconUrl" alt="心理AI助手" />
        </div>
      </div>
    </div>
    <div class="right-section">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
//获取图标路径(如果直接src="@/assets/images/robot-fill.png" 会报错 所以要使用new URL()方法)
const iconUrl = new URL("@/assets/images/心屿.png", import.meta.url).href
</script>

<style scoped lang="scss">
.auth-layout {
  display: flex;

  .left-section {
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    background: linear-gradient(180deg, #FFF9F5 0%, #FFF5F0 100%);
    height: 100vh;

    .content {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .title {
        margin-bottom: 20px;
        font-size: 40px;
        font-weight: bold;
        color: #ff9a56;
      }

      .text {
        margin-bottom: 20px;
        width: 460px;
        font-size: 20px;
        color: #666;
        text-align: center;
      }

      .robot {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 160px;
        height: 160px;
        border-radius: 50%;
        border: 2px solid rgba(255, 255, 255, 0.2);
        background: linear-gradient(135deg,
            rgba(255, 255, 255, 0.15) 0%,
            rgba(255, 255, 255, 0.05) 100%);
        box-shadow:
          0 15px 35px rgba(0, 0, 0, 0.1),
          inset 0 1px 0 rgba(255, 255, 255, 0.3);
      }
    }
  }

  .right-section {
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    height: 100vh;
    background-color: #fff;
  }
}
</style>
