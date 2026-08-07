package com.example.springbootaiproject.DTO.response;

import lombok.Data;

/**
 * 每日趋势
 */
@Data
public class DailyTrendDTO {

    private String date;

    private Long sessionCount;

    private Long userCount;
}
