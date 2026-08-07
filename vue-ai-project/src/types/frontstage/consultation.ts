// 创建会话请求参数
export interface StartSessionParams {
  initialMessage: string;
  sessionTitle?: string;
}

// 创建会话响应
export interface StartSessionResponse {
  sessionId: string;
  sessionTitle: string;
  status: string;
  startedAt: string;
}

// 会话列表请求参数
export interface SessionListParams {
  pageNum?: number;
  pageSize?: number;
}

// 会话信息
export interface SessionInfo {
  id: string;
  sessionTitle: string;
  startedAt: string;
  endedAt?: string;
  messageCount: number;
  durationMinutes: number;
  lastMessageContent: string;
  status: string;
}

// 会话列表响应
export interface SessionListResponse {
  records: SessionInfo[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// 会话消息
export interface SessionMessage {
  id: string;
  sessionId: string;
  content: string;
  senderType: number; // 1: 用户, 2: AI助手
  createdAt: string;
  isError?: boolean;
}

// 会话详情响应
export type SessionDetailResponse = SessionMessage[];
// 情绪分析响应
export interface SessionEmotionResponse {
  primaryEmotion: string;
  emotionScore: number;
  isNegative: boolean;
  riskLevel: number;
  suggestion: string;
  improvementSuggestions: string[];
  riskDescription?: string;
  keywords?: string[];
  icon?: string;
  label?: string;
  timestamp?: number;
}