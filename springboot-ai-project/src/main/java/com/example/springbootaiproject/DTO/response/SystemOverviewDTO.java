package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 系统概览数据
 */
@Data
public class SystemOverviewDTO {

    private Long totalUsers;

    private Long activeUsers;

    private Long totalDiaries;

    private Long todayNewDiaries;

    private Long totalSessions;

    private Long todayNewSessions;

    private BigDecimal avgMoodScore;
}
