package com.example.springbootaiproject.service;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootaiproject.DTO.command.EmotionDiaryCreateDTO;
import com.example.springbootaiproject.DTO.response.EmotionDiaryAdminDTO;
import com.example.springbootaiproject.DTO.response.EmotionDiaryResponseDTO;
import com.example.springbootaiproject.entity.EmotionDiary;
import com.example.springbootaiproject.entity.User;
import com.example.springbootaiproject.mapper.EmotionDiaryMapper;
import com.example.springbootaiproject.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmotionDiaryService {

    @Resource
    private EmotionDiaryMapper emotionDiaryMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建或更新情绪日记（同一用户每天只能有一条记录）
     */
    public EmotionDiaryResponseDTO createOrUpdate(Long userId, EmotionDiaryCreateDTO dto) {
        LocalDate diaryDate = LocalDate.parse(dto.getDiaryDate());

        // 查询当天是否已有记录
        LambdaQueryWrapper<EmotionDiary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmotionDiary::getUserId, userId)
                .eq(EmotionDiary::getDiaryDate, diaryDate);
        EmotionDiary existing = emotionDiaryMapper.selectOne(wrapper);

        LocalDateTime now = LocalDateTime.now();

        if (existing != null) {
            // 更新现有记录
            existing.setMoodScore(dto.getMoodScore());
            existing.setDominantEmotion(dto.getDominantEmotion());
            existing.setEmotionTriggers(dto.getEmotionTriggers());
            existing.setDiaryContent(dto.getDiaryContent());
            existing.setSleepQuality(dto.getSleepQuality());
            existing.setStressLevel(dto.getStressLevel());
            existing.setUpdatedAt(now);
            emotionDiaryMapper.updateById(existing);

            return EmotionDiaryResponseDTO.builder()
                    .id(existing.getId())
                    .content(existing.getDiaryContent())
                    .emotion(existing.getDominantEmotion())
                    .emotionScore(existing.getMoodScore())
                    .tags(existing.getEmotionTriggers())
                    .createdAt(formatDateTime(existing.getCreatedAt()))
                    .updatedAt(formatDateTime(now))
                    .build();
        } else {
            // 新增记录
            EmotionDiary diary = EmotionDiary.builder()
                    .userId(userId)
                    .diaryDate(diaryDate)
                    .moodScore(dto.getMoodScore())
                    .dominantEmotion(dto.getDominantEmotion())
                    .emotionTriggers(dto.getEmotionTriggers())
                    .diaryContent(dto.getDiaryContent())
                    .sleepQuality(dto.getSleepQuality())
                    .stressLevel(dto.getStressLevel())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            emotionDiaryMapper.insert(diary);

            return EmotionDiaryResponseDTO.builder()
                    .id(diary.getId())
                    .content(diary.getDiaryContent())
                    .emotion(diary.getDominantEmotion())
                    .emotionScore(diary.getMoodScore())
                    .tags(diary.getEmotionTriggers())
                    .createdAt(formatDateTime(now))
                    .updatedAt(formatDateTime(now))
                    .build();
        }
    }

    /**
     * 管理端：分页查询情绪日记（支持多条件筛选）
     */
    public Page<EmotionDiaryAdminDTO> adminPage(long current, long size,
                                                 Long userId, Integer minMoodScore,
                                                 Integer maxMoodScore, String dominantEmotion) {
        LambdaQueryWrapper<EmotionDiary> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(EmotionDiary::getUserId, userId);
        }
        if (minMoodScore != null) {
            wrapper.ge(EmotionDiary::getMoodScore, minMoodScore);
        }
        if (maxMoodScore != null) {
            wrapper.le(EmotionDiary::getMoodScore, maxMoodScore);
        }
        if (CharSequenceUtil.isNotBlank(dominantEmotion)) {
            wrapper.eq(EmotionDiary::getDominantEmotion, dominantEmotion);
        }
        wrapper.orderByDesc(EmotionDiary::getDiaryDate);

        Page<EmotionDiary> page = new Page<>(current, size);
        Page<EmotionDiary> diaryPage = emotionDiaryMapper.selectPage(page, wrapper);

        // 批量查询用户信息
        List<Long> userIds = diaryPage.getRecords().stream()
                .map(EmotionDiary::getUserId)
                .distinct()
                .collect(Collectors.toList());

        final Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = Collections.emptyMap();
        }

        // 转换为 AdminDTO
        Page<EmotionDiaryAdminDTO> resultPage = new Page<>(current, size, diaryPage.getTotal());
        List<EmotionDiaryAdminDTO> dtoList = diaryPage.getRecords().stream().map(diary -> {
            EmotionDiaryAdminDTO dto = new EmotionDiaryAdminDTO();
            dto.setId(diary.getId());
            dto.setUserId(diary.getUserId());
            dto.setDiaryDate(diary.getDiaryDate());
            dto.setDiaryContent(diary.getDiaryContent());
            dto.setMoodScore(diary.getMoodScore());
            dto.setDominantEmotion(diary.getDominantEmotion());
            dto.setEmotionTriggers(diary.getEmotionTriggers());
            dto.setSleepQuality(diary.getSleepQuality());
            dto.setStressLevel(diary.getStressLevel());
            dto.setAiEmotionAnalysis(diary.getAiEmotionAnalysis());
            dto.setAiAnalysisUpdatedAt(diary.getAiAnalysisUpdatedAt());
            dto.setCreatedAt(diary.getCreatedAt());
            dto.setUpdatedAt(diary.getUpdatedAt());

            // 内容预览（截取前100字符）
            if (CharSequenceUtil.isNotBlank(diary.getDiaryContent())) {
                dto.setDiaryContentPreview(diary.getDiaryContent().length() > 100
                        ? diary.getDiaryContent().substring(0, 100) + "..."
                        : diary.getDiaryContent());
                dto.setContentLength(diary.getDiaryContent().length());
            } else {
                dto.setDiaryContentPreview("");
                dto.setContentLength(0);
            }

            // AI 分析状态
            if (CharSequenceUtil.isNotBlank(diary.getAiEmotionAnalysis())) {
                dto.setHasAiEmotionAnalysis(true);
                dto.setAiAnalysisStatus("COMPLETED");
            } else {
                dto.setHasAiEmotionAnalysis(false);
                dto.setAiAnalysisStatus("PENDING");
            }

            // 关联用户信息
            User user = userMap.get(diary.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
                dto.setNickname(user.getNickname());
            }

            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(dtoList);
        return resultPage;
    }

    /**
     * 管理端：删除情绪日记
     */
    public void deleteById(Long id) {
        emotionDiaryMapper.deleteById(id);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
