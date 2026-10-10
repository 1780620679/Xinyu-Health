package com.example.springbootaiproject.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.springbootaiproject.DTO.response.FileUploadResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class SysFileInfoService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    private OSS ossClient;

    /**
     * 初始化 OSS 客户端
     */
    @PostConstruct
    public void init() {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    public FileUploadResponseDTO upload(MultipartFile file, String businessType,
                                         String businessId, String businessField) {
        String originalName = file.getOriginalFilename();// 原始文件名
        String ext = StrUtil.isNotBlank(originalName) ? originalName.substring(originalName.lastIndexOf(".")) : "";// 文件扩展名
        String storedName = IdUtil.fastSimpleUUID() + ext;// 生成随机文件名

        String subDir = StrUtil.isNotBlank(businessType) ? businessType : "common";// 子目录
        String ossKey = subDir + "/" + storedName;// OSS 键

        try (InputStream inputStream = file.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setObjectAcl(com.aliyun.oss.model.CannedAccessControlList.PublicRead);
            ossClient.putObject(bucketName, ossKey, inputStream, metadata);// 上传文件到 OSS
        } catch (Exception e) {
            throw new RuntimeException("OSS 上传失败", e);
        }

        String url = "https://" + bucketName + "." + endpoint + "/" + ossKey;// 完整 OSS 访问 URL

        return FileUploadResponseDTO.builder()
                .filePath("/" + ossKey)
                .url(url)
                .fileName(originalName)
                .fileSize(file.getSize())
                .build();
    }

    public void delete(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return;
        }
        String ossKey = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        ossClient.deleteObject(bucketName, ossKey);
    }
}
