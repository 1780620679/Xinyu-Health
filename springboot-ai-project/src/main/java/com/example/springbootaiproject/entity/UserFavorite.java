package com.example.springbootaiproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户收藏表
 */
@Data
@TableName("user_favorite")
@Builder
public class UserFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("article_id")
    private String articleId;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
