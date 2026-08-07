<template>
  <div class="backstage-layout">
    <el-container class="main-container">
      <AsideBar></AsideBar>
      <el-container>
        <el-header>
          <NavBar></NavBar>
        </el-header>
        <el-main class="main-content">
          <router-view class="content-container"></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script setup>
import AsideBar from './components/AsideBar.vue';
import NavBar from './components/NavBar.vue';
</script>
<style lang="scss" scoped>
.backstage-layout {
  height: 100vh;

  .el-header {
    height: 74px !important; //导航栏高度为74px提高优先级（important确保覆盖默认样式）
  }

  .main-container {
    height: 100%;

    .content-container {
      padding: 20px;
      background-color: #fff;
      min-height: calc(100% - 74px); //内容区域高度为100%减去导航栏高度
    }
  }
}
</style>
