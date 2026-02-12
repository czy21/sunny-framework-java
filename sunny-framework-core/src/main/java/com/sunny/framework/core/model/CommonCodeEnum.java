package com.sunny.framework.core.model;

import lombok.Getter;

@Getter
public enum CommonCodeEnum {
    SUCCESS(0, "success"),
    EXCEPTION(-1, "inner error");
    private final Integer code;
    private final String message;

    CommonCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
