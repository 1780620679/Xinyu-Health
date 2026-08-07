package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.util.List;

/**
 * 数据分析总览响应
 */
@Data
public class OverviewResponseDTO {

    private SystemOverviewDTO systemOverview;

    private ConsultationStatsDTO consultationStats;

    private List<EmotionTrendDTO> emotionTrend;

    private List<UserActivityDTO> userActivity;
}
