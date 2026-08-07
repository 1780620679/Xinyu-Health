import request from "@/utils/request"
import type { OverviewResponse } from "@/types/backstage/dashboard"

// 获取综合数据分析
export const getOverviewAPI = (): Promise<OverviewResponse> => {
  return request({
    url: "/data-analytics/overview",
    method: "get",
  })
}
