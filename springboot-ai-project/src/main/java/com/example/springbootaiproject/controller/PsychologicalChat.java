package com.example.springbootaiproject.controller;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootaiproject.AiService.PsychologicalSupportService;
import com.example.springbootaiproject.AiService.StructOutPut;
import com.example.springbootaiproject.DTO.command.ConsultationSessionCreateDTO;
import com.example.springbootaiproject.DTO.command.ConsultationStreamDTO;
import com.example.springbootaiproject.DTO.response.ConsultationMessageResponseDTO;
import com.example.springbootaiproject.DTO.response.ConsultationSessionVO;
import com.example.springbootaiproject.DTO.response.SessionEmotionDTO;
import com.example.springbootaiproject.common.Result;
import com.example.springbootaiproject.common.ResultCode;
import com.example.springbootaiproject.service.ConsultationMessageService;
import com.example.springbootaiproject.service.ConsultationSessionService;
import com.example.springbootaiproject.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/psychological-chat")
public class PsychologicalChat {

    @Autowired
    private PsychologicalSupportService psychologicalSupportService;

    @Resource
    private ConsultationSessionService sessionService;

    @Resource
    private ConsultationMessageService messageService;

    @RequestMapping("/session/start")
    public Result<StructOutPut.StreamChatSession> startSession(@Valid @RequestBody ConsultationSessionCreateDTO createDTO) {
        // 获取当前用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        // 创建会话
        StructOutPut.StreamChatSession session = psychologicalSupportService.startSession(userId, createDTO);
        return Result.ok(session);
    }
    /**
     * 流式返回聊天结果
     * {
     *   "sessionId": "session_123",
     *   "userMessage": "我最近压力很大"
     * }
     */

    // 流式返回聊天结果 使用 Flux 实现
    //Flux<ServerSentEvent<String>>：这是 Spring WebFlux 的响应式类型。Flux 表示"0 到 N 个元素的异步流"，ServerSentEvent<String> 表示每个元素是一个 SSE 事件，数据体是 String。结合起来就是：这个方法会持续推送多条 SSE 事件给前端，比如 AI 逐字生成回复时，每个字/词作为一个事件推出去。
    @PostMapping(value="/stream",produces = "text/event-stream")
    public Flux<ServerSentEvent<String>> stream(@RequestBody ConsultationStreamDTO streamDTO) {
        // 获取当前用户
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();

        if (userId == null) {
            // 用户未登录，返回错误事件
            //Flux.just(...) 表示创建只包含一个元素的流。因为方法签名要求返回 Flux<ServerSentEvent<String>>，所以不能直接 return 一个对象，必须用 Flux.just() 包一下。
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data(JSONUtil.toJsonStr(Result.error(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMsg(),"用户未登录")))// 将 Result 转换为 JSON 字符串
            .build());
        }

        // 开始流式对话
        return psychologicalSupportService.streamPsychologicalChat(
                        streamDTO.getSessionId(),
                        streamDTO.getUserMessage(),
                        Boolean.TRUE.equals(streamDTO.getRetry()))//此时返回的是一个字符串流
                .map(fragment -> {
                    return ServerSentEvent.<String>builder()
                        .event("message")
                        .data(JSONUtil.toJsonStr(Result.ok(Map.of("content", fragment,"type","normal"))))
                        .build();
                })
                .onErrorResume(error -> Flux.just(ServerSentEvent.<String>builder()
                        .event("error")
                        .data(JSONUtil.toJsonStr(Result.error(
                                ResultCode.ERROR.getCode(),
                                "AI 服务处理失败，请稍后重试",
                                null)))
                        .build()))
                .concatWith(Flux.just(ServerSentEvent.<String>builder() // 对话结束事件
                        .event("done")
                        .data(JSONUtil.toJsonStr(Result.ok(Map.of("content", "对话结束","type","end"))))
                        .build()
                ))
                .delayElements(Duration.ofMillis(50));// 每个事件之间延迟 50 毫秒，模拟真实对话的延迟
    }

    // ==================== 会话列表 ====================

    /**
     * 获取会话列表（前台 + 后台共用）
     * 前台传 pageNum/pageSize，只返回当前用户会话
     * 后台传 currentPage/size，可通过 userId 筛选
     */
    @GetMapping("/sessions")
    public Result<Page<ConsultationSessionVO>> sessions(
            @RequestParam(name = "pageNum", required = false, defaultValue = "1") long pageNum,
            @RequestParam(name = "currentPage", required = false) Long currentPage,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") long pageSize,
            @RequestParam(name = "size", required = false) Long size,
            @RequestParam(name = "userId", required = false) Long filterUserId) {

        long current = currentPage != null ? currentPage : pageNum;
        long pageSizeVal = size != null ? size : pageSize;

        //区分前台和后台模式
        Long queryUserId;
        if (filterUserId != null) {
            queryUserId = filterUserId;   // 后台选了具体用户
        } else if (currentPage != null || size != null) {
            // 后台模式：未传 userId → 查看全部会话(只有后台才会传 currentPage/size，前台传的是 pageNum/pageSize)
            queryUserId = null;
        } else {
            // 前台模式：只看自己的会话（这种模式下，前台不传 userId，只返回当前用户会话）
            queryUserId = getCurrentUserId();
        }

        return Result.ok(sessionService.page(queryUserId, current, pageSizeVal));
    }

    // ==================== 消息列表 ====================

    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<ConsultationMessageResponseDTO>> messages(@PathVariable Long sessionId) {
        List<ConsultationMessageResponseDTO> list = messageService.getMessagesBySessionId(sessionId);
        return Result.ok(list);
    }

    // ==================== 删除会话 ====================

    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return Result.ok();
    }

    // ==================== 情绪分析 ====================

    @GetMapping("/session/{sessionId}/emotion")
    public Result<SessionEmotionDTO> emotion(@PathVariable String sessionId) {
        Long id = parseSessionId(sessionId);
        return Result.ok(sessionService.getEmotionAnalysis(id));
    }

    private Long getCurrentUserId() {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }

    /** 处理前端可能带 "session_" 前缀的 sessionId */
    private Long parseSessionId(String sessionId) {
        if (sessionId.startsWith("session_")) {
            return Long.valueOf(sessionId.substring("session_".length()));
        }
        return Long.valueOf(sessionId);
    }
}
