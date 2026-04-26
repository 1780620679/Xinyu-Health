import { defineStore } from "pinia";
import { ref } from "vue";

export const useAdminStore = defineStore("ai-health-admin", () => {
  // 侧边栏是否折叠
  const isCollapse = ref(false);
  // 切换侧边栏折叠状态
  const toggleCollapse = () => {
    isCollapse.value = !isCollapse.value;
  };

  // 主题状态 (false: 浅色模式, true: 暗黑模式)
  const isDark = ref(false)

  const token = ref("")
  const userInfo = ref({})
  // 设置token,userInfo
  const setToken = (newToken) => {
    token.value = newToken
  }
  const setUserInfo = (newUserInfo) => {
    userInfo.value = newUserInfo
  }
  // 清除token,userInfo
  const clearUser = () => {
    token.value = ""
    userInfo.value = {}
  }

  return {
    isCollapse,
    toggleCollapse,
    token,
    userInfo,
    setToken,
    setUserInfo,
    clearUser,
    isDark,
  };
},{
  persist: true,
});