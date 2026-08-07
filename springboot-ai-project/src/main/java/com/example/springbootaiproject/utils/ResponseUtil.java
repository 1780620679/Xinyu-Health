package com.example.springbootaiproject.utils;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import com.example.springbootaiproject.common.Result;
import com.example.springbootaiproject.common.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ResponseUtil {
    // 过滤器中的异常响应
    public static void writeError(HttpServletResponse response, ResultCode resultCode) {
        // 根据不同结果码返回不同的响应
        int status = switch (resultCode) {
            case UNAUTHORIZED, ACCESS_UNAUTHORIZED, TOKEN_INVALID, TOKEN_EXPIRED, TOKEN_BLOCKED -> HttpStatus.UNAUTHORIZED.value();// 未授权401错误
            case TOKEN_ACCESS_FORBIDDEN -> HttpStatus.FORBIDDEN.value();// 禁止访问403错误
            default -> HttpStatus.BAD_REQUEST.value();// 无效请求400错误
        };
        response.setStatus(status);// 设置响应状态码
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);// 设置响应内容类型为JSON  "application/ json"
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());// 设置响应字符编码为UTF-8

        try (PrintWriter writer = response.getWriter()){
            String jsonResponse = JSONUtil.toJsonStr(Result.error(resultCode.getCode(), resultCode.getMsg(), null));// 构建JSON响应字符串
            writer.print(jsonResponse); // 写入JSON响应字符串到响应体
            writer.flush(); // 确保将响应内容写入到输出流
        }catch (IOException e) {
            System.out.println("写入响应失败：" + e.getMessage());
        }
    }
}
