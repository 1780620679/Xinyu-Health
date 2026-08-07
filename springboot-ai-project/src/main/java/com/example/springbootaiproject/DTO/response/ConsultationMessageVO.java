package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 咨询消息响应
 */
@Data
public class ConsultationMessageVO {

    private Long id;

    private Long sessionId;

    private Integer senderType;

    private String senderTypeDesc;

    private Integer messageType;

    private String messageTypeDesc;

    private String content;

    private Integer contentLength;

    private String contentPreview;

    private String emotionTag;

    private String aiModel;

    private LocalDateTime createdAt;
}
