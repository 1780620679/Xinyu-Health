package com.example.springbootaiproject.AiService;

public class StructOutPut {

    public record StreamChatSession(
            String sessionId,
            Long userHash,
            String initialMessage,
            Long startTime,
            Long expiryTime,
            Integer messageCount,
            String status
    ){}
}
//StructOutPut 是一个空的容器类，内部用 Java 的 record 关键字定义了一个 StreamChatSession 不可变数据载体，
//用于自动生成构造器、getter、equals、hashCode 和 toString 等样板代码。

//record 会自动生成构造器
//、getter、equals、hashCode、toString，代码简洁且不可变，非常适合作为 DTO 返回