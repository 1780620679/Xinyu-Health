import request from '@/utils/request'
import type { CreateOrUpdateEmotionDiaryParams, EmotionDiaryResponse } from '@/types/frontstage/emotionDiary'

// 创建或更新情绪日记
export const createOrUpdateEmotionDiaryAPI = (data: CreateOrUpdateEmotionDiaryParams): Promise<EmotionDiaryResponse> => {
  return request({
    url: '/emotion-diary',
    method: 'POST',
    data
  })
}
