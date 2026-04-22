import request from "@/utils/request"

// 获取综合数据分析
export const getOverviewAPI = () => {
  return request({
    url: "/data-analytics/overview",
    method: "get",
  })
}
