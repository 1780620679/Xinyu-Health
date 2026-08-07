import { useAdminStore } from "@/stores/admin"
import axios from "axios"
import { ElMessage } from "element-plus"

// 创建axios实例
const request = axios.create({
  baseURL: "/api",
  timeout: 5000,
})

// 添加请求拦截器
request.interceptors.request.use(
  function (config) {
    // 在发送请求之前做些什么
    // 例如：添加token到请求头
    const adminStore = useAdminStore()
    const token = adminStore.token
    if (token) {
      config.headers = config.headers || {}
      config.headers['token'] = token
    }
    return config
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error)
  },
)

// 添加响应拦截器
request.interceptors.response.use(
  function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    // 对响应数据做点什么
    // 例如：判断响应状态码是否为200
    const { data } = response
    //注意：data.code是字符串
    if (data.code === "200") {
      //返回的数据在res的data.data中这里直接处理一下去掉一层data
      return data.data
    }
    else {
      if (data.code === '-1') {
        // 处理401错误，例如跳转到登录页
        ElMessage.error(data.msg || "登录过期，请重新登录")
        // 清除token,userInfo
        const adminStore = useAdminStore()
        adminStore.clearUser()
        window.location.href = "/auth/login"
        return Promise.reject(data)
      } else {
        ElMessage.error(data.msg || "请求失败")
        return Promise.reject(data)
      }
    }
  },
  function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    ElMessage.error(error.message || "请求失败")
    return Promise.reject(error)
  },
)

// 导出axios实例
export default request
