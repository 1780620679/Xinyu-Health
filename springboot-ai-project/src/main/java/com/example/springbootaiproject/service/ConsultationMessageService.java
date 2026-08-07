package com.example.springbootaiproject.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootaiproject.DTO.response.ConsultationMessageResponseDTO;
import com.example.springbootaiproject.entity.ConsultationMessage;
import com.example.springbootaiproject.mapper.ConsultationMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationMessageService {
    @Autowired
    private ConsultationMessageMapper consultationMessageMapper;

    public ConsultationMessage saveUserMessage(Long sessionId,String content ,String emotion_tag) {
        // 构建咨询消息实体
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(1)
                .messageType(1)
                .content(content)
                .emotionTag(emotion_tag)
                .createdAt(LocalDateTime.now())
                .build();
        // 插入数据库
        consultationMessageMapper.insert(message);
        return message;
    }

    // 根据会话ID查询有无初始消息，避免重复创建，如果有那就获取最后一个消息，继续在追加消息，没有则创建新的，插入新的会话记录consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
    public Integer getMessageCountBySessionId(Long sessionId) {
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId);

        Long count = consultationMessageMapper.selectCount(queryWrapper);
        return count.intValue();
    }

    // 获取最后一个消息
    public ConsultationMessageResponseDTO getLastMessageBySessionId(Long sessionId) {
        LambdaQueryWrapper<ConsultationMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsultationMessage::getSessionId, sessionId) // 等选出会话ID为sessionId的消息，肯定有多条
                .orderByDesc(ConsultationMessage::getCreatedAt)       //，按创建时间降序排序，
                .last("limit 1");                              // 只取第一条

        ConsultationMessage lastMessage = consultationMessageMapper.selectOne(queryWrapper);
        return lastMessage != null ? convertToResponseDTO(lastMessage) : null;

    }
    // 把获取到的最后一个消息实体转换为响应DTO
    private ConsultationMessageResponseDTO convertToResponseDTO(ConsultationMessage message) {
        if (message == null) {
            return null;
        }
        // 手动逐字段赋值，确保转换的准确性和可控性
        ConsultationMessageResponseDTO responseDTO = new ConsultationMessageResponseDTO();
        responseDTO.setId(message.getId());
        responseDTO.setSessionId(message.getSessionId());
        responseDTO.setSenderType(message.getSenderType());
        responseDTO.setMessageType(message.getMessageType());
        responseDTO.setContent(message.getContent());
        responseDTO.setEmotionTag(message.getEmotionTag());
        responseDTO.setAiModel(message.getAiModel());
        responseDTO.setCreatedAt(message.getCreatedAt());

        // 设置描述字段（通过实体方法获取）
        responseDTO.setSenderTypeDesc(message.getSenderTypeDesc());
        responseDTO.setMessageTypeDesc(message.getMessageTypeDesc());

        // 计算消息长度和预览
        responseDTO.calculateContentLength();

        // 前端期望的 sender 字段（字符串格式）
        if (message.getSenderType() == 1) {
            responseDTO.setSender("user");
        } else if (message.getSenderType() == 2) {
            responseDTO.setSender("ai");
        }

        // 状态默认值
        responseDTO.setStatus("sent");

        return responseDTO;
    }



    /**
     * 获取会话消息列表
     */
    public List<ConsultationMessageResponseDTO> getMessagesBySessionId(Long sessionId) {
        LambdaQueryWrapper<ConsultationMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsultationMessage::getSessionId, sessionId)
                .orderByAsc(ConsultationMessage::getCreatedAt);

        List<ConsultationMessage> messages = consultationMessageMapper.selectList(wrapper);
        return messages.stream().map(this::convertToResponseDTO).collect(java.util.stream.Collectors.toList());
    }

    // 插入AI助手消息
    public ConsultationMessage saveAimessage(Long sessionId,String content ,String aiModel) {
        // 构建咨询消息实体
        ConsultationMessage message = ConsultationMessage.builder()
                .sessionId(sessionId)
                .senderType(2)
                .messageType(1)
                .content(content)
                .aiModel(aiModel)
                .createdAt(LocalDateTime.now())
                .build();
        // 插入数据库
        consultationMessageMapper.insert(message);
        return message;
    }
}
