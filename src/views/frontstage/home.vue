<template>
  <div class="home-container">
    <div class="content">
      <div class="text">
        <h2 class="title">
          智能AI助手<br />
          <span class="highlight-text">来一次温暖的对话</span>
        </h2>
        <p class="description">智能AI助手是一个基于Vue3的智能助手助手，它可以帮助用户进行对话，提供知识库查询，以及情绪日记功能。</p>
        <div class="hero-actions">
          <el-button size="large" @click="$router.push('/consultation')">开始对话</el-button>
          <el-button size="large" @click="$router.push('/emotion-diary')" style="border-color: #fff;"
            color="transparent">记下此刻心情，宣泄您的情绪</el-button>
        </div>
      </div>
      <div class="robot">
        <el-image :src="iconUrl" alt="智能AI助手" style="width: 150px; height: 150px;" class="robot-image" />
      </div>
    </div>
  </div>
</template>
<script setup>

const iconUrl = new URL('@/assets/images/robot-fill.png', import.meta.url).href

</script>
<style scoped lang="scss">
.home-container {
  background: linear-gradient(90deg, rgb(74, 156, 140) 0%, rgb(61, 138, 122) 100%) rgba(74, 156, 140, 0.95);
  color: white;
  padding: 5rem 0;
  height: calc(100vh - 285px);
  display: flex;
  align-items: center;
  justify-content: center;

  .content {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 30px;

    .text {
      width: 500px;

      .title {
        font-size: 45px;
        font-weight: bold;
        // line-height: 1.2;
        margin-bottom: 15px;

        .highlight-text {
          color: #ffd700;
        }
      }

      .hero-actions {
        margin-top: 30px;
      }
    }

    .robot {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 260px;
      height: 260px;
      border-radius: 50%;
      border: 2px solid rgba(255, 255, 255, 0.2);
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.05) 100%);
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.3);
    }
  }
}
</style>
