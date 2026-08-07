import request from '@/utils/request';
import type { EmotionalPageParams, EmotionalPageResponse } from "@/types/backstage/emotional"


// 获取情绪日志分页列表
export const getEmotionalPageAPI = (params: EmotionalPageParams): Promise<EmotionalPageResponse> => {
  return request({
    url: '/emotion-diary/admin/page',
    method: 'get',
    params
  });
}
// 删除情绪日志
export const deleteEmotionalAPI = (id: string): Promise<void> => {
  return request({
    url: `/emotion-diary/admin/${id}`,
    method: 'delete'
  });
}
