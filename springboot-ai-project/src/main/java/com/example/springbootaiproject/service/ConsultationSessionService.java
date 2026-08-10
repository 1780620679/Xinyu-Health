package com.example.springbootaiproject.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootaiproject.DTO.command.ConsultationSessionCreateDTO;
import com.example.springbootaiproject.DTO.response.ConsultationSessionVO;
import com.example.springbootaiproject.DTO.response.SessionEmotionDTO;
import com.example.springbootaiproject.entity.ConsultationMessage;
import com.example.springbootaiproject.entity.ConsultationSession;
import com.example.springbootaiproject.entity.User;
import com.example.springbootaiproject.mapper.ConsultationMessageMapper;
import com.example.springbootaiproject.mapper.ConsultationSessionMapper;
import com.example.springbootaiproject.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConsultationSessionService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;
    @Autowired
    private ConsultationMessageMapper consultationMessageMapper;

    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        //判断用户是否存在
        User user = userMapper.selectById(userId);
        if (user != null) {
            //创建会话记录
            ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            //如果没有传入标题
            if(StrUtil.isBlank(createDTO.getSessionTitle())){//如果标题为空
                session.setSessionTitle(String.format("心屿AI助手 - %s", DateUtil.format(LocalDateTime.now(), "YYYY-MM-dd HH:mm")));
            }
            //保存会话记录
            consultationSessionMapper.insert(session);
            return session;
        }
        return null;
    }

    /**
     * 分页查询会话列表（前台 + 后台共用）
     * 传入 userId 则只看该用户的，不传则看全部（后台管理）
     */
    public Page<ConsultationSessionVO> page(Long userId, long current, long size) {
        LambdaQueryWrapper<ConsultationSession> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ConsultationSession::getUserId, userId);
        }
        wrapper.orderByDesc(ConsultationSession::getStartedAt);

        Page<ConsultationSession> page = new Page<>(current, size);
        Page<ConsultationSession> sessionPage = consultationSessionMapper.selectPage(page, wrapper);

        // 收集所有 userId 从查到的会话记录中提取
        // 从查询结果中提取 userId 并去重
        List<Long> userIds = sessionPage.getRecords().stream()
                .map(ConsultationSession::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 一条 SQL 批量查 得到    {1 → {id:1, username:"zhangsan", nickname:"张三"},
        //                      2 → {id:2, username:"lisi",    nickname:"李四"}}
        final Map<Long, User> userMap;
        if (!userIds.isEmpty()) {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
        } else {
            userMap = Collections.emptyMap();
        }

        // 收集所有 sessionId，批量查消息表 consultation_message（关键！）
        List<Long> sessionIds = sessionPage.getRecords().stream()
                .map(ConsultationSession::getId)
                .collect(Collectors.toList());

        final Map<Long, Long> messageCountMap;
        final Map<Long, ConsultationMessage> lastMessageMap;
        if (!sessionIds.isEmpty()) {
            List<ConsultationMessage> allMessages = consultationMessageMapper.selectList(
                    new LambdaQueryWrapper<ConsultationMessage>()
                            .in(ConsultationMessage::getSessionId, sessionIds)
                            .orderByAsc(ConsultationMessage::getCreatedAt));
            // 消息计数
            messageCountMap = allMessages.stream()
                    .collect(Collectors.groupingBy(ConsultationMessage::getSessionId, Collectors.counting()));
            // 最后一条消息
            lastMessageMap = allMessages.stream()
                    .collect(Collectors.toMap(
                            ConsultationMessage::getSessionId,
                            m -> m,
                            (existing, replacement) -> existing.getCreatedAt().isAfter(replacement.getCreatedAt()) ? existing : replacement));
        } else {
            messageCountMap = Collections.emptyMap();
            lastMessageMap = Collections.emptyMap();
        }

        // 组装 VO
        List<ConsultationSessionVO> voList = sessionPage.getRecords().stream().map(session -> {
            ConsultationSessionVO vo = new ConsultationSessionVO();
            vo.setId(session.getId());
            vo.setUserId(session.getUserId());
            vo.setSessionTitle(session.getSessionTitle());
            vo.setStartedAt(session.getStartedAt());
            vo.setStatus("ACTIVE");

            // 一条会话只保存最后一次情绪分析，列表仅返回绘图需要的摘要字段
            if (StrUtil.isNotBlank(session.getLastEmotionAnalysis())) {
                try {
                    JSONObject emotion = JSONUtil.parseObj(session.getLastEmotionAnalysis());
                    vo.setPrimaryEmotion(emotion.getStr("primaryEmotion"));
                    vo.setEmotionScore(emotion.getInt("emotionScore"));
                    vo.setEmotionUpdatedAt(session.getLastEmotionUpdatedAt());
                } catch (RuntimeException ignored) {
                    // 历史脏数据不应导致整个会话列表查询失败
                }
            }

            // 消息数
            Long count = messageCountMap.get(session.getId());
            vo.setMessageCount(count != null ? count.intValue() : 0);

            // 最后一条消息
            ConsultationMessage lastMsg = lastMessageMap.get(session.getId());
            if (lastMsg != null) {
                vo.setLastMessageContent(lastMsg.getContent());
                vo.setLastMessageTime(lastMsg.getCreatedAt());
            }

            // 持续分钟数
            if (lastMsg != null) {
                vo.setDurationMinutes(Duration.between(session.getStartedAt(), lastMsg.getCreatedAt()).toMinutes());
            }

            // 用户昵称
            User user = userMap.get(session.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
            }

            return vo;
        }).collect(Collectors.toList());

        Page<ConsultationSessionVO> result = new Page<>(current, size, sessionPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 删除会话及关联消息
     */
    @Transactional
    public void deleteSession(Long sessionId) {
        consultationMessageMapper.delete(
                new LambdaQueryWrapper<ConsultationMessage>()
                        .eq(ConsultationMessage::getSessionId, sessionId));
        consultationSessionMapper.deleteById(sessionId);
    }

    /**
     * 获取会话情绪分析（解析 JSON 为前端期望的字段格式）
     */
    public SessionEmotionDTO getEmotionAnalysis(Long sessionId) {
        ConsultationSession session = consultationSessionMapper.selectById(sessionId);
        if (session == null) {
            return null;
        }

        String json = session.getLastEmotionAnalysis();
        if (cn.hutool.core.util.StrUtil.isBlank(json)) {
            SessionEmotionDTO empty = new SessionEmotionDTO();
            empty.setPrimaryEmotion("暂无");
            empty.setEmotionScore(50);
            empty.setIsNegative(false);
            empty.setRiskLevel(0);
            empty.setSuggestion("暂无情绪分析数据，继续对话后将自动生成分析报告");
            empty.setRiskDescription("暂无数据");
            empty.setImprovementSuggestions(java.util.Collections.emptyList());
            return empty;
        }

        // 将 DB 中的 JSON 字符串解析为 DTO
        cn.hutool.json.JSONObject obj = cn.hutool.json.JSONUtil.parseObj(json);

        SessionEmotionDTO dto = new SessionEmotionDTO();
        dto.setPrimaryEmotion(obj.getStr("primaryEmotion"));
        dto.setEmotionScore(obj.getInt("emotionScore"));
        dto.setIsNegative(obj.getBool("isNegative"));
        dto.setRiskLevel(obj.getInt("riskLevel"));
        dto.setSuggestion(obj.getStr("suggestion"));
        dto.setRiskDescription(obj.getStr("riskDescription"));
        dto.setIcon(obj.getStr("icon"));
        dto.setLabel(obj.getStr("label"));
        dto.setTimestamp(obj.getLong("timestamp"));

        // 数组字段
        if (obj.containsKey("improvementSuggestions")) {
            dto.setImprovementSuggestions(obj.getJSONArray("improvementSuggestions")
                    .toList(String.class));
        }
        if (obj.containsKey("keywords")) {
            dto.setKeywords(obj.getJSONArray("keywords")
                    .toList(String.class));
        }

        return dto;
    }
}
