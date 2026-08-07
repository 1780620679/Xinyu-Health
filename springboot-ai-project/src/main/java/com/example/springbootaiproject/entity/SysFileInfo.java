package com.example.springbootaiproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统文件信息表
 */
@Data
@TableName("sys_file_info")
@Builder
public class SysFileInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("original_name")
    private String originalName;

    @TableField("file_path")
    private String filePath;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_type")
    private String fileType;

    @TableField("business_type")
    private String businessType;

    @TableField("business_id")
    private String businessId;

    @TableField("business_field")
    private String businessField;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField("is_temp")
    private Integer isTemp;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("expire_time")
    private LocalDateTime expireTime;
}
