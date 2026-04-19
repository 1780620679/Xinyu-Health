import { defineStore } from "pinia";
import { ref } from "vue";

export const useAdminStore = defineStore("admin", () => {
  // 侧边栏是否折叠
  const isCollapse = ref(false);
  // 切换侧边栏折叠状态
  const toggleCollapse = () => {
    isCollapse.value = !isCollapse.value;
  };
  return {
    isCollapse,
    toggleCollapse,
  };
});
