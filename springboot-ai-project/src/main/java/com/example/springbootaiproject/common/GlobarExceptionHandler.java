package com.example.springbootaiproject.common;

import com.example.springbootaiproject.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobarExceptionHandler {
    //处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleException(MethodArgumentNotValidException e) {
        // 从异常中获取校验失败的字段和错误信息
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Result.error(ResultCode.PARAM_ERROR.getCode(),ResultCode.PARAM_ERROR.getMsg(), msg);
    }
    //处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessError(BusinessException e) {
       //如果异常携带数据
        if (e.getData() != null) {
            return Result.error(e.getCode(),e.getMessage(), e.getData());
        } else {
            return Result.error(e.getCode(), e.getMessage(), null);
        }
    }
}
