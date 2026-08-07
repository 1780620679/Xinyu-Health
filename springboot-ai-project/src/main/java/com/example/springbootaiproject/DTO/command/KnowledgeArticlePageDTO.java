package com.example.springbootaiproject.DTO.command;

import lombok.Data;

/**
 * 知识文章分页查询参数
 */
@Data
public class KnowledgeArticlePageDTO {

    private Long currentPage;
    private Long size;

    /** 文章标题（模糊搜索） */
    private String title;

    /** 分类ID */
    private Long categoryId;

    /** 状态筛选（管理端使用） */
    private Integer status;

    /** 排序字段 */
    private String sortField;

    /** 排序方向：ASC/DESC */
    private String sortDirection;
}
