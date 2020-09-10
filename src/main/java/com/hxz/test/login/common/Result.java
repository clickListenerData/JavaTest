package com.hxz.test.login.common;


public class Result<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> custom(int code,String message,T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200,"",data);
    }

    public static <T> Result<T> error(T data) {
        return new Result<>(100,"",data);
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}