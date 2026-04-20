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
  //解决跨域问题
  // 配置代理服务器
  server: {
    proxy: {
      "/api": {
        target: "http://159.75.169.224:1235",
        changeOrigin: true,
      },
    },
  },
})
