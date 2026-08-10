package com.example.springbootaiproject.AiService;

import cn.hutool.json.JSONUtil;
import com.example.springbootaiproject.DTO.command.ConsultationSessionCreateDTO;
import com.example.springbootaiproject.DTO.response.ConsultationMessageResponseDTO;
import com.example.springbootaiproject.entity.ConsultationSession;
import com.example.springbootaiproject.mapper.ConsultationSessionMapper;
import com.example.springbootaiproject.service.ConsultationMessageService;
import com.example.springbootaiproject.service.ConsultationSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PsychologicalSupportService {

    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;// 注入open-ai的聊天客户端
    @Autowired
    private ChatMemory chatMemory;// 注入聊天记忆体

    @Autowired
    private ConsultationSessionService consultationSessionService;
    @Autowired
    private ConsultationMessageService consultationMessageService;
    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    private static final Logger log = LoggerFactory.getLogger(PsychologicalSupportService.class);

    // 开始心理支持会话
    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        // 创建数据库会话记录 session表里插入一条记录
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);

        //将初始用户消息插入 message表里插入一条记录
        consultationMessageService.saveUserMessage(consultationSession.getId(), createDTO.getInitialMessage(),null);

        // 创建会话信息
        String sessionId = "session_" + consultationSession.getId();
        return new StructOutPut.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L, // 24小时
                1,
                "ACTIVE"
        );
    }

    // 获取参数中的sessionId （创建时的 sessionId 格式为 session_ + idStr，这里提取 idStr） 实际数据库中的 id 是不带 session_的
    public Long extractSessionId(String sessionId) {
        if (sessionId != null && sessionId.startsWith("session_")) {
            String idStr = sessionId.substring("session_".length());
            return Long.parseLong(idStr);
        }
        return null;
    }

    /**
     * 流式心理支持对话
     * @param sessionId 会话ID
     * @param userMessage 用户消息
     * @return 流式响应
     */
    public Flux<String> streamPsychologicalChat(String sessionId, String userMessage) {
        // 创建响应流
        return Flux.create(sink -> {
            // sink.next("数据1") // 发布数据
            // sink.complete(); // 完成流
            // sink.error(exception); // 发布错误
            Long dbSessionId = extractSessionId(sessionId);// 调用上面方法从 sessionId 中提取实际会话ID
            if (dbSessionId == null) {
                sink.error(new RuntimeException("会话ID格式错误"));
                return;
            }
            // 是否为初始消息
            boolean isInitialMessage = false;
            // 检查是否为初始消息，避免重复保存
            // 核心目的：前端在进入流式对话时，可能会把初始消息再发一遍。
            // 如果 messageCount == 1（表里只有初始消息），且内容又一样，就说明是重复发送，isInitialMessage = true 跳过入库，避免 message 表出现两条相同内容。
            //、表里仅有初始消息的这个时间窗口内，做一次去重比对。如果前端把初始消息又发了一遍，就跳过入库；一旦过了这个窗口（AI 回复入库后 messageCount > 1），就不再检查，后续消息直接正常保存。
            //当 messageCount > 1 时，说明已经有多轮对话了，此时传来的消息一定是新的，没必要再比对。
            //  messageCount == 0	异常情况，理论上不会发生	跳过检查，直接保存
            Integer messageCount = consultationMessageService.getMessageCountBySessionId(dbSessionId);
            if (messageCount == 1) {
                ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessageBySessionId(dbSessionId);
                if (lastMessage != null && lastMessage.getSenderType() == 1 && userMessage.equals(lastMessage.getContent())) {
                    isInitialMessage = true;
                }
            }
            if (!isInitialMessage) {
                // 可能用户只是在session表里插入了一条记录，但是由于一些bug导致message表里还没有初始消息
                // 保存用户消息到数据库
                consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
            }
            //-----------------------------------------------------核心逻辑----------------------------------------------------------------------------//
            // 进行流式对话
            // 生成对话记忆管理
            String conversationId = "conversation_" + sessionId; // 会话记忆体ID
            // 构建系统提示词
            List<Message> userMessages = new ArrayList<>();// 初始化用户消息列表泛型<Message>为springai的Message类
            userMessages.add(new UserMessage(userMessage));//加入用户提示词
            chatMemory.add(conversationId, userMessages);// 加入用户消息到会话记忆体
            // 构建系统提示词
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)//加入系统提示词
            ));

            //用于存储AI完成的响应
            StringBuilder fullResponse = new StringBuilder();

            // 使用chatClient发送消息到Open AI
            Disposable aiSubscription = chatClient.prompt(prompt)
                    .user(userMessage)// 加入用户提示词
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))// 加入会话记忆体ID
                    .stream()               // 设置流式响应
                    .content()              // 获取响应内容 流的格式
                    .doOnNext(Fragment -> { //监听方法1，每次有数据时调用
                        fullResponse.append(Fragment); // 累加数据到 fullResponse
                        sink.next(Fragment); // 发布数据
                    })
                    .doOnComplete(() -> { //监听方法2，当流完成时调用
                        String completeRes = fullResponse.toString();// 所有拼接好的数据转换为字符串
                        // 将AI返回的内容保存到数据库
                        consultationMessageService.saveAimessage(dbSessionId, completeRes, "openai");
                        // 同理添加AI回复到chatMemory记忆体
                        List<Message> aiMessages = new ArrayList<>();// 初始化AI回复消息列表泛型<Message>为springai的Message类
                        aiMessages.add(new AssistantMessage(completeRes));// 加入AI回复词
                        chatMemory.add(conversationId, aiMessages);

                        // 情绪分析（先分析再结束，确保前端拿到最新数据）
                        analyzeEmotion(dbSessionId, userMessage, completeRes);

                        sink.complete();// 完成流
                    })
                    .doOnError(error -> { //监听方法3，当流出错时调用
                        sink.error(error);// 发布错误
                    })
                    .subscribe(); // 订阅并启动流

            // 浏览器通过 AbortController 断开 SSE 时，同时取消底层 AI 流，避免后端继续生成
            sink.onCancel(aiSubscription);
        });
    }

    /**
     * 异步情绪分析：分析用户消息和AI回复，将结果存入 consultation_session.last_emotion_analysis
     */
    private void analyzeEmotion(Long sessionId, String userMessage, String aiResponse) {
        try {
            String analysisPrompt = PromptManage.EMOTION_ANALYSIS_PROMPT
                    + "\n\n用户消息：" + userMessage
                    + "\nAI回复：" + aiResponse;

            String result = chatClient.prompt()
                    .user(analysisPrompt)
                    .call()
                    .content();

            log.info("会话 {} 情绪分析完成: {}", sessionId, result);

            // 更新数据库
            ConsultationSession session = consultationSessionMapper.selectById(sessionId);
            if (session != null) {
                session.setLastEmotionAnalysis(result);
                session.setLastEmotionUpdatedAt(LocalDateTime.now());
                consultationSessionMapper.updateById(session);
            }
        } catch (Exception e) {
            log.error("会话 {} 情绪分析失败", sessionId, e);
        }
    }
}
