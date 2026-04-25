import request from '@/utils/request'

// 创建或更新情绪日记
export const createOrUpdateEmotionDiaryAPI = (data) => {
  return request({
    url: '/emotion-diary',
    method: 'POST',
    data
  })
}
