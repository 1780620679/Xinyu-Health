package com.example.springbootaiproject.DTO.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationMessageResponseDTO {
    // 消息ID
    private Long id;

    // 会话ID
    private Long sessionId;

    // 发送者类型 1:用户 2:AI助手
    private Integer senderType;

    // 发送者类型描述
    private String senderTypeDesc;

    // 消息类型 1:文本
    private Integer messageType;

    // 消息类型描述
    private String messageTypeDesc;

    // 消息内容
    private String content;

    // 情绪标签
    private String emotionTag;

    // 使用的AI模型
    private String aiModel;

    // 创建时间
    private LocalDateTime createdAt;

    // 消息长度
    private Integer contentLength;

    // 内容预览
    private String contentPreview;

    // 前端格式：sender = 'user' | 'ai'
    private String sender;

    // 前端格式：status
    private String status;

    /**
     * 计算消息长度
     */
    public void calculateContentLength() {
        this.contentLength = content != null ? content.length() : 0;
        this.contentPreview = content != null && content.length() > 100
                ? content.substring(0, 100) + "..."
                : content;
    }

}
