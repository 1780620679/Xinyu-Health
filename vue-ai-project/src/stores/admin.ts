import { defineStore } from "pinia";
import { ref } from "vue";

// 用户信息类型
interface UserInfo {
  id?: string;
  username?: string;
  avatar?: string;
  [key: string]: any;
}

export const useAdminStore = defineStore("ai-health-admin", () => {
  // 侧边栏是否折叠
  const isCollapse = ref<boolean>(false);
  // 切换侧边栏折叠状态
  const toggleCollapse = (): void => {
    isCollapse.value = !isCollapse.value;
  };

  // 主题状态 (false: 浅色模式, true: 暗黑模式)
  const isDark = ref<boolean>(false);

  const token = ref<string>("");
  const userInfo = ref<UserInfo>({});
  
  // 设置token,userInfo
  const setToken = (newToken: string): void => {
    token.value = newToken;
  };
  
  const setUserInfo = (newUserInfo: UserInfo): void => {
    userInfo.value = newUserInfo;
  };
  
  // 清除token,userInfo
  const clearUser = (): void => {
    token.value = "";
    userInfo.value = {};
  };

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
},{  persist: true,});
