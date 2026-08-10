package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 咨询会话列表项响应（前台 + 后台共用）
 */
@Data
public class ConsultationSessionVO {

    private Long id;

    private Long userId;

    private String userNickname;

    private String sessionTitle;

    private String lastMessageContent;

    private Integer messageCount;

    private LocalDateTime lastMessageTime;

    private LocalDateTime startedAt;

    /** 前台：会话状态 */
    private String status;

    /** 前台：持续分钟数 */
    private Long durationMinutes;

    /** 该会话最后一次情绪分析摘要，用于绘制会话情绪趋势 */
    private String primaryEmotion;

    private Integer emotionScore;

    private LocalDateTime emotionUpdatedAt;
}
