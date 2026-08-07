import request from '@/utils/request'
import type { StartSessionParams, StartSessionResponse, SessionListParams, SessionListResponse, SessionDetailResponse, SessionEmotionResponse } from '@/types/frontstage/consultation'


// 创建新会话
export const startSessionAPI = (data: StartSessionParams): Promise<StartSessionResponse> => {
  return request({
    url: '/psychological-chat/session/start',
    method: 'POST',
    data
  })
}
//获取会话历史列表
export const getSessionListAPI = (params: SessionListParams): Promise<SessionListResponse> => {
  return request({
    url: '/psychological-chat/sessions',
    method: 'GET',  
    params
  })
}
//删除会话记录
export const deleteSessionAPI = (sessionId: string): Promise<void> => {
  return request({
    url: `/psychological-chat/sessions/${sessionId}`,
    method: 'DELETE'
  })
}
//获取会话记录详情
export const getSessionDetailAPI = (sessionId: string): Promise<SessionDetailResponse> => {
  return request({
    url: `/psychological-chat/sessions/${sessionId}/messages`,
    method: 'GET'
  })
}
// 获取会话情绪分析结果
export const getSessionEmotionAPI = (sessionId: string): Promise<SessionEmotionResponse> => {
  return request({
    url: `/psychological-chat/session/${sessionId}/emotion`,
    method: 'GET'
  })
}
