package com.example.springbootaiproject.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootaiproject.DTO.response.*;
import com.example.springbootaiproject.entity.ConsultationMessage;
import com.example.springbootaiproject.entity.ConsultationSession;
import com.example.springbootaiproject.entity.EmotionDiary;
import com.example.springbootaiproject.entity.User;
import com.example.springbootaiproject.mapper.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class DataAnalyticsService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private EmotionDiaryMapper emotionDiaryMapper;
    @Resource
    private ConsultationSessionMapper sessionMapper;
    @Resource
    private ConsultationMessageMapper messageMapper;

    public OverviewResponseDTO getOverview() {
        OverviewResponseDTO overview = new OverviewResponseDTO();
        overview.setSystemOverview(buildSystemOverview());
        overview.setConsultationStats(buildConsultationStats());
        overview.setEmotionTrend(buildEmotionTrend());
        overview.setUserActivity(buildUserActivity());
        return overview;
    }

    private SystemOverviewDTO buildSystemOverview() {
        SystemOverviewDTO dto = new SystemOverviewDTO();
        LocalDate today = LocalDate.now();

        dto.setTotalUsers(userMapper.selectCount(null));
        dto.setActiveUsers(userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 1)));

        dto.setTotalDiaries(emotionDiaryMapper.selectCount(null));
        dto.setTodayNewDiaries(emotionDiaryMapper.selectCount(
                new LambdaQueryWrapper<EmotionDiary>().eq(EmotionDiary::getDiaryDate, today)));

        dto.setTotalSessions(sessionMapper.selectCount(null));
        dto.setTodayNewSessions(sessionMapper.selectCount(
                new LambdaQueryWrapper<ConsultationSession>().apply("DATE(started_at) = {0}", today)));

        List<EmotionDiary> allDiaries = emotionDiaryMapper.selectList(null);
        if (allDiaries != null && !allDiaries.isEmpty()) {
            double avg = allDiaries.stream()
                    .filter(d -> d.getMoodScore() != null)
                    .mapToInt(EmotionDiary::getMoodScore)
                    .average().orElse(0);
            dto.setAvgMoodScore(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
        } else {
            dto.setAvgMoodScore(BigDecimal.ZERO);
        }
        return dto;
    }

    private ConsultationStatsDTO buildConsultationStats() {
        ConsultationStatsDTO dto = new ConsultationStatsDTO();
        dto.setTotalSessions(sessionMapper.selectCount(null));

        List<ConsultationMessage> allMessages = messageMapper.selectList(null);
        if (!allMessages.isEmpty()) {
            Map<Long, List<ConsultationMessage>> grouped = new HashMap<>();
            for (ConsultationMessage msg : allMessages) {
                grouped.computeIfAbsent(msg.getSessionId(), k -> new ArrayList<>()).add(msg);
            }
            double totalDuration = 0;
            int sessionCount = 0;
            for (List<ConsultationMessage> msgs : grouped.values()) {
                if (msgs.size() >= 2) {
                    ConsultationMessage first = msgs.stream()
                            .min(Comparator.comparing(ConsultationMessage::getCreatedAt)).orElse(null);
                    ConsultationMessage last = msgs.stream()
                            .max(Comparator.comparing(ConsultationMessage::getCreatedAt)).orElse(null);
                    if (first != null && last != null) {
                        totalDuration += ChronoUnit.MINUTES.between(first.getCreatedAt(), last.getCreatedAt());
                        sessionCount++;
                    }
                }
            }
            dto.setAvgDurationMinutes(sessionCount > 0
                    ? BigDecimal.valueOf(totalDuration / sessionCount).setScale(1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO);
        } else {
            dto.setAvgDurationMinutes(BigDecimal.ZERO);
        }
        dto.setDailyTrend(buildDailyTrend());
        return dto;
    }

    private List<DailyTrendDTO> buildDailyTrend() {
        List<DailyTrendDTO> trends = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            DailyTrendDTO trend = new DailyTrendDTO();
            trend.setDate(date.toString());

            LambdaQueryWrapper<ConsultationSession> w = new LambdaQueryWrapper<>();
            w.apply("DATE(started_at) = {0}", date);
            trend.setSessionCount(sessionMapper.selectCount(w));
            trend.setUserCount(sessionMapper.selectList(w).stream()
                    .map(ConsultationSession::getUserId).distinct().count());

            trends.add(trend);
        }
        return trends;
    }

    private List<EmotionTrendDTO> buildEmotionTrend() {
        List<EmotionTrendDTO> trends = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<EmotionDiary> dayDiaries = emotionDiaryMapper.selectList(
                    new LambdaQueryWrapper<EmotionDiary>().eq(EmotionDiary::getDiaryDate, date));

            EmotionTrendDTO trend = new EmotionTrendDTO();
            trend.setDate(date.toString());
            trend.setRecordCount((long) dayDiaries.size());

            if (!dayDiaries.isEmpty()) {
                double avg = dayDiaries.stream()
                        .filter(d -> d.getMoodScore() != null)
                        .mapToInt(EmotionDiary::getMoodScore).average().orElse(0);
                trend.setAvgMoodScore(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
            } else {
                trend.setAvgMoodScore(BigDecimal.ZERO);
            }
            trends.add(trend);
        }
        return trends;
    }

    private List<UserActivityDTO> buildUserActivity() {
        List<UserActivityDTO> activities = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            UserActivityDTO dto = new UserActivityDTO();
            dto.setDate(date.toString());

            dto.setNewUsers(userMapper.selectCount(
                    new LambdaQueryWrapper<User>().apply("DATE(created_at) = {0}", date)));

            List<EmotionDiary> dayDiaries = emotionDiaryMapper.selectList(
                    new LambdaQueryWrapper<EmotionDiary>().eq(EmotionDiary::getDiaryDate, date));
            dto.setDiaryUsers(dayDiaries.stream().map(EmotionDiary::getUserId).distinct().count());

            List<ConsultationSession> daySessions = sessionMapper.selectList(
                    new LambdaQueryWrapper<ConsultationSession>().apply("DATE(started_at) = {0}", date));
            dto.setConsultationUsers(daySessions.stream().map(ConsultationSession::getUserId).distinct().count());

            Set<Long> activeSet = new HashSet<>();
            dayDiaries.forEach(d -> activeSet.add(d.getUserId()));
            daySessions.forEach(s -> activeSet.add(s.getUserId()));
            dto.setActiveUsers((long) activeSet.size());

            activities.add(dto);
        }
        return activities;
    }
}
