package com.sunny.framework.core.exception;

import com.sunny.framework.core.model.CommonCodeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonException extends RuntimeException {

    private Integer code;
    private String message;

    public CommonException() {
        this(CommonCodeEnum.EXCEPTION.getCode(), CommonCodeEnum.EXCEPTION.getMessage());
    }

    public CommonException(String message, Object... arguments) {
        this(CommonCodeEnum.EXCEPTION.getCode(), message, arguments);
    }

    public CommonException(Integer code, String message, Object... arguments) {
        super(String.format(message, arguments));
        this.code = code;
        this.message = String.format(message, arguments);
    }
}
