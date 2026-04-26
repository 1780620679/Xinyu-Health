import { useIntersectionObserver } from '@vueuse/core'
// 图片懒加载插件
export const lazyPlugin = {
  // 创建一个 Vue 插件对象，包含 install 方法，用于在 Vue 应用中注册全局功能
  install(app) {
//- 在 install 方法中注册一个名为 img-lazy 的全局指令
// - 指令有一个 mounted 钩子，当指令绑定的元素被挂载到 DOM 时执行
    app.directive('img-lazy', {
      mounted(el, binding) {
        // el: 指令绑定的元素 (img)
        // binding: 指令值 (图片URL)
        const { stop } = useIntersectionObserver(
          el, // 监听的元素
          ([{ isIntersecting }]) => {
            if (isIntersecting) {
              // 进入视口，加载图片
              // 检查是否是 el-image 组件
              if (el.tagName === 'IMG') {
                // 普通 img 标签
                el.src = binding.value
              } else if (el.querySelector('img')) {
                // el-image 组件内部的 img 元素
                const imgElement = el.querySelector('img')
                if (imgElement) {
                  imgElement.src = binding.value
                }
              }
              // 停止监听
              stop()
            }
          },
          {
            rootMargin: '50px' // 提前50px开始加载
          }
        )
      }
    })
  }
}