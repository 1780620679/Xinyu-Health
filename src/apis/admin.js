import request from "@/utils/request"

export const LoginAPI = (data) => {
  return request({
    url: "/user/login",
    method: "post",
    data,
  })
}
