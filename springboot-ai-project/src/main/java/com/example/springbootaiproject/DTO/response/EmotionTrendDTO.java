package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 情绪趋势
 */
@Data
public class EmotionTrendDTO {

    private String date;

    private BigDecimal avgMoodScore;

    private Long recordCount;
}
