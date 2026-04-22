import request from "@/utils/request"

// 登录接口
export const LoginAPI = (data) => {
  return request({
    url: "/user/login",
    method: "post",
    data,
  })
}
// 退出登录接口
export const LogoutAPI = () => {
  return request({
    url: "/user/logout",
    method: "post",
  })
}