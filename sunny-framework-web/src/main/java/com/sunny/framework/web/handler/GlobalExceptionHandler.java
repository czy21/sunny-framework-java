package com.sunny.framework.web.handler;


import com.sunny.framework.core.exception.CommonException;
import com.sunny.framework.core.model.CommonCodeEnum;
import com.sunny.framework.core.model.CommonResult;
import com.sunny.framework.web.controller.BaseController;
import com.sunny.framework.web.feign.FeignCommonException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = BaseController.class)
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResult<Void> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        if (e.getCause() instanceof FeignCommonException fcException) {
            return CommonResult.error(fcException.getCode(), fcException.getMessage());
        }
        return CommonResult.error(CommonCodeEnum.EXCEPTION.getMessage());
    }

    @ExceptionHandler(value = {CommonException.class})
    @ResponseBody
    public CommonResult<?> handleCommonException(CommonException e) {
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    @ResponseBody
    public CommonResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return CommonResult.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        String errors = objectErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return CommonResult.error(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public CommonResult<?> handleMethodArgumentInValidException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> objectErrors = e.getConstraintViolations();
        String errors = null;
        if (objectErrors != null) {
            errors = objectErrors.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        }
        return CommonResult.error(errors);
    }
}
