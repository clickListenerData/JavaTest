package com.hxz.test.login.common;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice  // 处理其它controller 抛出的异常
public class CustomExceptionHandler {


    /*@ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handlerException(Exception e) {
        return Result.custom(500,e.getMessage(),null);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Result<Object> handlerNotFound(NotFoundException e) {
        return Result.custom(404,"request.getRequestURL().toString()",null);
    }*/

    @ExceptionHandler(value = Exception.class)  // 可以处理 除了 404 以外的异常
    @ResponseBody
    public Result<Object> handlerException(Exception e) {
        return Result.custom(1000,e.getMessage(),e);
    }
}
