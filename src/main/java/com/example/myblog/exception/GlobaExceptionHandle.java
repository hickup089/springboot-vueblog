package com.example.myblog.exception;

import com.example.myblog.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobaExceptionHandle {

//    // ShiroException，基本可以捕获大部分异常
//    // @ResponseStatus(HttpStatus.UNAUTHORIZED)返回没有权限
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(value=ShiroException.class)
//    public Result handler(ShiroException e){
//        log.error("没有权限异常--------");
//        return Result.fail("401",e.getMessage(),null);
//    }
//
//
//    // runtimeException运行时异常，基本可以捕获大部分异常
//    // @ResponseStatus(HttpStatus.BAD_REQUEST)返回badrequest
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(value=RuntimeException.class)
//    public Result handler(RuntimeException e){
//
//        return Result.fail(e.getMessage());
//    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = ShiroException.class)
    public Result handler(ShiroException e) {
        log.error("运行时异常：----------------{}", e);
        return Result.fail(401, e.getMessage(), null);
    }


    // MethodArgumentNotValidException方法参数无效异常，一般用于request请求出错或者校验
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        log.error("实体校验异常：----------------{}", e);
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();

        return Result.fail(objectError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        log.error("Assert异常：----------------{}", e);
        return Result.fail(e.getMessage());
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e) {
        log.error("运行时异常：----------------{}", e);
        return Result.fail(e.getMessage());
    }
}
