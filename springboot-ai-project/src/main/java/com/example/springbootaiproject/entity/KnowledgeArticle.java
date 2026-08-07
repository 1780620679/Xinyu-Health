package com.example.springbootaiproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识文章表（id 为 UUID，非自增）
 */
@Data
@TableName("knowledge_article")
@Builder
public class KnowledgeArticle {

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField("category_id")
    private Long categoryId;

    private String title;

    private String summary;

    private String content;

    @TableField("cover_image")
    private String coverImage;

    private String tags;

    @TableField("author_id")
    private Long authorId;

    @TableField("read_count")
    private Integer readCount;

    private Integer status;

    @TableField("published_at")
    private LocalDateTime publishedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
