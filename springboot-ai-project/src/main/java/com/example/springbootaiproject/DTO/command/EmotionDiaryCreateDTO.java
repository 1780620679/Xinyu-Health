package com.example.springbootaiproject.DTO.command;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建/更新情绪日记请求参数
 */
@Data
public class EmotionDiaryCreateDTO {

    @NotBlank(message = "记录日期不能为空")
    private String diaryDate;

    @NotNull(message = "情绪评分不能为空")
    @Min(value = 1, message = "情绪评分最低为1")
    @Max(value = 10, message = "情绪评分最高为10")
    private Integer moodScore;

    @NotBlank(message = "主要情绪不能为空")
    private String dominantEmotion;

    @NotBlank(message = "情绪触发因素不能为空")
    private String emotionTriggers;

    @NotBlank(message = "日记内容不能为空")
    private String diaryContent;

    @NotNull(message = "睡眠质量不能为空")
    @Min(value = 1, message = "睡眠质量最低为1")
    @Max(value = 5, message = "睡眠质量最高为5")
    private Integer sleepQuality;

    @NotNull(message = "压力水平不能为空")
    @Min(value = 1, message = "压力水平最低为1")
    @Max(value = 5, message = "压力水平最高为5")
    private Integer stressLevel;
}
