package com.sunny.framework.web.feign;


import com.sunny.framework.core.exception.CommonException;

public class FeignCommonException extends CommonException {
    public FeignCommonException() {
    }

    public FeignCommonException(Integer code, String message) {
        super(code, message);
    }

}
