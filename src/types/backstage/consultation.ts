// 咨询会话列表请求参数
export interface ConsultationDataParams {
  total: number;
  size: number;
  currentPage: number;
}

// 咨询会话列表响应
export interface ConsultationDataResponse {
  records: Records[];
  total: number;
}

// 咨询会话信息
export interface Records {
  id: string;
  userId: string;
  userNickname: string;
  sessionTitle: string;
  lastMessageContent: string;
  messageCount: number;
  lastMessageTime: string;
  startdAt: string;
  messages?: SessionMessage[];
}

// 会话消息列表响应
// export interface SessionMessagesResponse {
//   messages?: SessionMessage[];
// }

// 会话消息信息
export interface SessionMessage {
  id: string;
  sessionId: string;
  content: string;
  contentLength: number;
  contentPreview: string;
  senderType: number; // 1: 用户, 2: AI助手
  senderTypeDesc: string; // 发送者类型描述
  createdAt: string;
  messageType: number; // 1: 文本消息, 2: 图片消息, 3: 视频消息
  messageTypeDesc: string; // 消息类型描述
}
