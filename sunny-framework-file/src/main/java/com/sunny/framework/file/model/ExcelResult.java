package com.sunny.framework.file.model;

import lombok.Data;

/**
 * excel导入后响应体
 */
@Data
public class ExcelResult {
    /**
     * 下载错误文件时入参
     */
    private String token;
    /**
     * 是否导入成功
     */
    private boolean success;
    /**
     * 成功数
     */
    private int successTotal;
    /**
     * 失败数
     */
    private int failureTotal;
}
