package com.example.springbootaiproject.common;

import lombok.Data;


/**
 * 后端统一返回结果
 */
@Data
public class Result<T> {

    private String code;

    private String msg;

    private T data;

    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        return result;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = ok();  //ok(T data) 的 code/msg 和 ok() 完全一样，所以直接调用 ok() 复用
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMsg(ResultCode.ERROR.getMsg());
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> error(String code, String msg , T data) {
        Result<T> result = new Result<>(); // error(String code, String msg, T data) 的 code/msg 是调用者传入的任意值，无法复用 error()
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
