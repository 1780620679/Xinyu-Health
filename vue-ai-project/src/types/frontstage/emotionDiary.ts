// 创建或更新情绪日记请求参数
export interface CreateOrUpdateEmotionDiaryParams {
  diaryDate: string;
  diaryContent: string;
  moodScore: number;
  dominantEmotion: string;
  emotionTriggers: string;
  sleepQuality: number;
  stressLevel: number;
  id?: string; // 用于更新操作
}

// 情绪日记响应
export interface EmotionDiaryResponse {
  id: string;
  content: string;
  emotion: string;
  emotionScore: number;
  tags: string[];
  createdAt: string;
  updatedAt: string;
}