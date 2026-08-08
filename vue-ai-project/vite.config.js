import { defineConfig, loadEnv } from "vite"
import vue from "@vitejs/plugin-vue"
import path from "path"

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // 加载环境变量：mode=当前模式(development/production等)，process.cwd()=项目根目录，""=加载所有环境变量(不仅限于VITE_前缀)
  const env = loadEnv(mode, process.cwd(), "")
  const devPort = Number(env.DEV_PORT || 5173)
  const apiProxyTarget = env.API_PROXY_TARGET || "http://localhost:1920"
  // target: "http://159.75.169.224:1235"
  return {
    plugins: [vue()],
    //配置路径别名
    resolve: {
      //__dirname 当前根目录
      alias: {
        "@": path.resolve(__dirname, "src"),
      },
    },
    // 配置基础路径
    // 配置后，所有路由都添加 / 前缀
    // 解决部署到服务器后，路由跳转问题
    base: '/',
    //解决跨域问题
    // 配置代理服务器
    server: {
      port: devPort,
      proxy: {
        "/api": {
          target: apiProxyTarget,
          changeOrigin: true,
        },
      },
    },
  }
})