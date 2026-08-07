package com.example.springbootaiproject.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识文章统一响应（前台 + 后台）
 */
@Data
public class KnowledgeArticleVO {

    private String id;

    private String title;

    private String summary;

    private String content;

    private Long categoryId;

    private String categoryName;

    private String coverImage;

    private String tags;

    private Long authorId;

    private String authorName;

    private Integer readCount;

    private Integer status;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
