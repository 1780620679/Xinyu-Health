package com.example.springbootaiproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 分析任务表
 */
@Data
@TableName("ai_analysis_task")
@Builder
public class AiAnalysisTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("diary_id")
    private Long diaryId;

    @TableField("user_id")
    private Long userId;

    private String status;

    @TableField("task_type")
    private String taskType;

    private Integer priority;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("max_retry_count")
    private Integer maxRetryCount;

    @TableField("error_message")
    private String errorMessage;

    @TableField("started_at")
    private LocalDateTime startedAt;

    @TableField("completed_at")
    private LocalDateTime completedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
