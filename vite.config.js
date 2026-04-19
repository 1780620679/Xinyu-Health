import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  // 配置路径别名
  resolve: {
    //__dirname 当前根目录
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
})
