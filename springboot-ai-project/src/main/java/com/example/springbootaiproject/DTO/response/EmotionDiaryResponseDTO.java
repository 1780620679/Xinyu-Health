package com.example.springbootaiproject.DTO.response;

import lombok.Builder;
import lombok.Data;

/**
 * 情绪日记响应（前台创建/更新后返回）
 */
@Data
@Builder
public class EmotionDiaryResponseDTO {

    private Long id;

    private String content;

    private String emotion;

    private Integer emotionScore;

    private String tags;

    private String createdAt;

    private String updatedAt;
}
