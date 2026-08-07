package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 咨询统计
 */
@Data
public class ConsultationStatsDTO {

    private Long totalSessions;

    private BigDecimal avgDurationMinutes;

    private List<DailyTrendDTO> dailyTrend;
}
