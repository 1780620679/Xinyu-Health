package com.example.springbootaiproject.DTO.response;

import lombok.Builder;
import lombok.Data;

/**
 * 文件上传响应
 */
@Data
@Builder
public class FileUploadResponseDTO {

    /** OSS 对象路径（如 ARTICLE/xxx.jpg），前端拼 fileBaseURL 使用 */
    private String filePath;

    /** 完整 OSS 访问 URL */
    private String url;

    private String fileName;

    private Long fileSize;
}
