package com.sunny.framework.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CommonResult<T> {

    private Integer code;
    private String message;
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public static <T> CommonResult<T> ok() {
        return ok(null);
    }

    public static <T> CommonResult<T> ok(T data) {
        return new CommonResult<>(CommonCodeEnum.SUCCESS.getCode(), CommonCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResult<T> error() {
        return error(CommonCodeEnum.EXCEPTION.getMessage());
    }

    public static <T> CommonResult<T> error(String msg) {
        return error(CommonCodeEnum.EXCEPTION.getCode(), msg);
    }

    public static <T> CommonResult<T> error(Integer code, String msg) {
        return new CommonResult<>(code, msg, null);
    }

    public static <T> CommonResult<T> create(int code, String message, T data) {
        return new CommonResult<>(code, message, data);
    }
}
