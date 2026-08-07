package com.example.springbootaiproject.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootaiproject.DTO.command.EmotionDiaryCreateDTO;
import com.example.springbootaiproject.DTO.response.EmotionDiaryAdminDTO;
import com.example.springbootaiproject.DTO.response.EmotionDiaryResponseDTO;
import com.example.springbootaiproject.common.Result;
import com.example.springbootaiproject.service.EmotionDiaryService;
import com.example.springbootaiproject.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 情绪日记控制器
 */
@RestController
@RequestMapping("/api/emotion-diary")
public class EmotionDiaryController {

    @Resource
    private EmotionDiaryService emotionDiaryService;

    /**
     * 前台：创建或更新情绪日记
     */
    @PostMapping
    public Result<EmotionDiaryResponseDTO> createOrUpdate(@Valid @RequestBody EmotionDiaryCreateDTO dto) {
        Long userId = getCurrentUserId();
        EmotionDiaryResponseDTO result = emotionDiaryService.createOrUpdate(userId, dto);
        return Result.ok(result);
    }

    /**
     * 管理端：分页查询情绪日记
     */
    @GetMapping("/admin/page")
    public Result<Page<EmotionDiaryAdminDTO>> adminPage(
            @RequestParam(name = "currentPage", defaultValue = "1") long current,
            @RequestParam(name = "size", defaultValue = "10") long size,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "emotionScoreRange", required = false) String emotionScoreRange,
            @RequestParam(name = "dominantEmotion", required = false) String dominantEmotion) {

        Integer minMoodScore = null;
        Integer maxMoodScore = null;
        if (emotionScoreRange != null && emotionScoreRange.contains("-")) {
            String[] parts = emotionScoreRange.split("-");
            minMoodScore = Integer.valueOf(parts[0]);
            maxMoodScore = Integer.valueOf(parts[1]);
        }

        Page<EmotionDiaryAdminDTO> page = emotionDiaryService.adminPage(
                current, size, userId, minMoodScore, maxMoodScore, dominantEmotion);
        return Result.ok(page);
    }

    /**
     * 管理端：删除情绪日记
     */
    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        emotionDiaryService.deleteById(id);
        return Result.ok();
    }

    private Long getCurrentUserId() {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }
}
