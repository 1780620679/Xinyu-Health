package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.util.List;

/**
 * 会话情绪分析响应（字段匹配前端 SessionEmotionResponse）
 */
@Data
public class SessionEmotionDTO {

    private String primaryEmotion;

    private Integer emotionScore;

    private Boolean isNegative;

    private Integer riskLevel;

    private String suggestion;

    private List<String> improvementSuggestions;

    private String riskDescription;

    private List<String> keywords;

    private String icon;

    private String label;

    private Long timestamp;
}
