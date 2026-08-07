package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 情绪日记管理端列表响应
 */
@Data
public class EmotionDiaryAdminDTO {

    private Long id;

    private Long userId;

    private String username;

    private String nickname;

    private LocalDate diaryDate;

    private String diaryContent;

    private String diaryContentPreview;

    private Integer contentLength;

    private Integer moodScore;

    private String dominantEmotion;

    private String emotionTriggers;

    private Integer sleepQuality;

    private Integer stressLevel;

    private String aiAnalysisStatus;

    private LocalDateTime aiAnalysisUpdatedAt;

    private String aiEmotionAnalysis;

    private Boolean hasAiEmotionAnalysis;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
