// 这是告诉 TypeScript "如何理解 .vue 文件" 的声明文件。

// 为什么需要这个文件？

// - TypeScript 默认不知道 .vue 文件是什么
// - 这个文件声明了 .vue 文件的类型
// - 使得 TypeScript 能够识别 Vue 组件

/// <reference types="vite/client" />

// 声明所有 .vue 文件的类型
declare module '*.vue' {
  // 从 vue 包中导入 DefineComponent 类型
  import type { DefineComponent } from 'vue'
  
  // 定义 .vue 文件是一个组件，有三个泛型参数
  // 第一个 {}：props 的类型
  // 第二个 {}：setup 返回的对象类型
  // 第三个 any：组件内部 data 的类型
  const component: DefineComponent<{}, {}, any>
  
  // 导出组件
  export default component
}