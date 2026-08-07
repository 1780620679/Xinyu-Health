package com.example.springbootaiproject.controller;

import com.example.springbootaiproject.DTO.response.FileUploadResponseDTO;
import com.example.springbootaiproject.common.Result;
import com.example.springbootaiproject.service.SysFileInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Resource
    private SysFileInfoService fileInfoService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result<FileUploadResponseDTO> upload(
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) String businessId,
            @RequestParam(name = "businessFiled", required = false) String businessField) {

        FileUploadResponseDTO result = fileInfoService.upload(file, businessType, businessId, businessField);
        return Result.ok(result);
    }

    /**
     * 删除 OSS 文件
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam String filePath) {
        fileInfoService.delete(filePath);
        return Result.ok();
    }
}
