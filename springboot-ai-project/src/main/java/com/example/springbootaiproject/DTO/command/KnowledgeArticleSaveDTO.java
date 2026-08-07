package com.example.springbootaiproject.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建/编辑知识文章请求参数
 */
@Data
public class KnowledgeArticleSaveDTO {

    @NotBlank(message = "文章标题不能为空")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String summary;

    private String coverImage;

    private String tags;

    private Integer status;

    private String id;
}
