// 情绪日志列表请求参数
export interface EmotionalPageParams {
  currentPage: number;
  size: number;
  total: number;
}

// 情绪日志列表响应
export interface EmotionalPageResponse {
  current: number;
  pages: number;
  records: EmotionalDiary[];
  size: number;
  total: number;
}

// 情绪日志信息
export interface EmotionalDiary {
  id: number;
  userId: number;
  username: string;
  nickname: string;
  diaryDate: string;
  diaryContent: string;
  diaryContentPreview: string;
  moodScore: number;
  dominantEmotion: string;
  emotionTriggers: string;
  sleepQuality: number;
  stressLevel: number;
  contentLength: number;
  aiAnalysisStatus: string;
  aiAnalysisUpdatedAt: string;
  aiEmotionAnalysis: string;
  hasAiEmotionAnalysis: boolean;
  createdAt: string;
  updatedAt: string;
}

// AI情绪分析结果
export interface AIEmotionAnalysis {
  primaryEmotion: string;
  emotionScore: number;
  isNegative: boolean;
  riskLevel: number;
  keywords: string[];
  suggestion: string;
  riskDescription: string;
  improvementSuggestions: string[];
}
