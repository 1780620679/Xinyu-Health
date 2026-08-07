import { defineConfig } from "vite"
import vue from "@vitejs/plugin-vue"
import path from "path"

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  // 配置路径别名
  resolve: {
    //__dirname 当前根目录
    alias: {
      "@": path.resolve(__dirname, "src"),
    },
  },
  // 配置基础路径
  // �置后，所有路由都添加 / 前缀
  // 解决部署到服务器后，路由跳转问题
  base: '/',
  //解决跨域问题
  // 配置代理服务器
  server: {
    port: 1920,
    proxy: {
      "/api": {
        // target: "http://159.75.169.224:1235",
        target: "http://localhost:1920",
        changeOrigin: true,
      },
    },
  },
})
