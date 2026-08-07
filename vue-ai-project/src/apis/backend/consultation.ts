import request from "@/utils/request"
import type { ConsultationDataParams, ConsultationDataResponse, SessionMessagesResponse } from "@/types/backstage/consultation"

// 咨询数据
export const getConsultationDataAPI = (params: ConsultationDataParams): Promise<ConsultationDataResponse> => {
  return request({
    url: "/psychological-chat/sessions",
    method: "get",
    params,
  })
}
//会话消息详情列表
export const getSessionMessagesAPI = (sessionId: string): Promise<SessionMessagesResponse> => {
  return request({
    url: `/psychological-chat/sessions/${sessionId}/messages`,
    method: "get",
  })
}
