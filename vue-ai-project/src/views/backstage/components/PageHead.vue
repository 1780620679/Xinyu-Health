<template>
  <div class="page-head">
    <p class="page-title">{{ title }}</p>
    <div class="action-btn">
      <slot name="buttons"></slot>
    </div>
  </div>
</template>
<script setup lang="ts">
const props = defineProps<{
  title: string;
}>();

// 定义插槽类型
// defineSlots<{
//   buttons: () => void;
// }>();
</script>
<style lang="scss" scoped>
.page-head {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .page-title {
    font-size: 24px;
    color: #2d3748;
  }
}
</style>
