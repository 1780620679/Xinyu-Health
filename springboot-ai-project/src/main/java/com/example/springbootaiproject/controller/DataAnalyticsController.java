package com.example.springbootaiproject.controller;

import com.example.springbootaiproject.DTO.response.OverviewResponseDTO;
import com.example.springbootaiproject.common.Result;
import com.example.springbootaiproject.service.DataAnalyticsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据分析控制器
 */
@RestController
@RequestMapping("/api/data-analytics")
public class DataAnalyticsController {

    @Resource
    private DataAnalyticsService dataAnalyticsService;

    @GetMapping("/overview")
    public Result<OverviewResponseDTO> overview() {
        return Result.ok(dataAnalyticsService.getOverview());
    }
}
