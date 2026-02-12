package com.sunny.framework.webflux.handler;


import com.sunny.framework.core.exception.CommonException;
import com.sunny.framework.core.model.CommonCodeEnum;
import com.sunny.framework.core.model.CommonResult;
import com.sunny.framework.webflux.controller.BaseController;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;
@RestControllerAdvice(assignableTypes = BaseController.class)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Mono<CommonResult<Void>> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return Mono.just(CommonResult.error(CommonCodeEnum.EXCEPTION.getMessage()));
    }

    @ExceptionHandler(CommonException.class)
    public Mono<CommonResult<?>> handleCommonException(CommonException e) {
        return Mono.just(CommonResult.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<CommonResult<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return Mono.just(CommonResult.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<CommonResult<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(";"));
        return Mono.just(CommonResult.error(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<CommonResult<?>> handleConstraintViolationException(ConstraintViolationException e) {
        String errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(";"));
        return Mono.just(CommonResult.error(errors));
    }
}
