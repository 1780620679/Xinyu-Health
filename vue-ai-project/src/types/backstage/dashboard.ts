// 系统概览数据
export interface SystemOverview {
  totalUsers: number; // 总用户数
  activeUsers: number; // 活跃用户数
  totalDiaries: number; // 总情绪日志数
  todayNewDiaries: number; // 今日新增情绪日志数
  totalSessions: number; // 总咨询会话数
  todayNewSessions: number; // 今日新增咨询会话数
  avgMoodScore: number; // 平均情绪评分
}

// 咨询统计数据
export interface ConsultationStats {
  totalSessions: number; // 总会话数
  avgDurationMinutes: number; // 平均时长(分钟)
  dailyTrend: DailyTrend[]; // 每日趋势
}

// 每日趋势数据
export interface DailyTrend {
  date: string; // 日期
  sessionCount: number; // 会话数量
  userCount: number; // 参与用户数
}

// 情绪趋势数据
export interface EmotionTrend {
  date: string; // 日期
  avgMoodScore: number; // 平均情绪评分
  recordCount: number; // 记录数量
}

// 用户活跃度数据
export interface UserActivity {
  date: string; // 日期
  activeUsers: number; // 活跃用户数
  newUsers: number; // 新增用户数
  diaryUsers: number; // 日记用户数
  consultationUsers: number; // 咨询用户数
}

// 综合数据分析响应
export interface OverviewResponse {
  systemOverview: SystemOverview; // 系统概览
  consultationStats: ConsultationStats; // 咨询统计
  emotionTrend: EmotionTrend[]; // 情绪趋势
  userActivity: UserActivity[]; // 用户活跃度
}
