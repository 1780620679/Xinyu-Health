import { createApp } from "vue";
import "./style.css";
// 引入暗黑模式样式
import "./assets/css/dark-mode.css";
import App from "./App.vue";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
// 引入路由
import router from "./router/index.js";
// 引入element-plus图标
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
//引入pinia
import { createPinia } from "pinia";
// 引入图片懒加载插件
import { lazyPlugin } from "./directives/lazy.js";

const app = createApp(App);
// 注册图片懒加载插件
app.use(lazyPlugin);
// 注册pinia插件
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate); // 使用持久化插件
app.use(pinia);
app.use(ElementPlus);
app.use(router);

app.mount("#app");
// 注册element-plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}