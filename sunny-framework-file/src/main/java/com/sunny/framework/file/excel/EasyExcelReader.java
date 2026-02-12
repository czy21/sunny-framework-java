package com.sunny.framework.file.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import tools.jackson.databind.ObjectMapper;
import com.sunny.framework.file.listener.ExcelGenericDataEventListener;
import jakarta.validation.Validator;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EasyExcelReader<T> {

    private String token;
    private int batch = 200;
    private int expireMinutes = 30;
    private JsonMapper jsonMapper;
    private StringRedisTemplate redisTemplate;
    private Validator validator;
    private ExcelGenericDataEventListener<T> excelGenericDataEventListener;
    private Map<String, EasyExcelProperty> nameProperty;

    public EasyExcelReader(JsonMapper jsonMapper, StringRedisTemplate redisTemplate, Validator validator) {
        this.jsonMapper = jsonMapper;
        this.redisTemplate = redisTemplate;
        this.validator = validator;
    }

    public EasyExcelReader<T> batch(int batch) {
        if (batch > 0) {
            this.batch = batch;
        }
        return this;
    }

    public EasyExcelReader<T> expire(int minutes) {
        if (minutes > expireMinutes) {
            this.expireMinutes = minutes;
        }
        return this;
    }

    public EasyExcelReader<T> nameProperty(Map<String, EasyExcelProperty> nameProperty) {
        this.nameProperty = nameProperty;
        return this;
    }

    public EasyExcelReader<T> process(Consumer<ExcelGenericDataEventListener.Context<T>> consumer) {
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.excelGenericDataEventListener = new ExcelGenericDataEventListener<>(consumer, jsonMapper, redisTemplate, validator);
        this.excelGenericDataEventListener.setToken(token);
        this.excelGenericDataEventListener.setBatch(batch);
        this.excelGenericDataEventListener.setExpireMinutes(expireMinutes);
        this.excelGenericDataEventListener.setNameProperty(nameProperty);
        return this;
    }

    public String getToken() {
        return token;
    }

    public int getTotal() {
        return excelGenericDataEventListener.getTotal();
    }

    public AtomicInteger getSuccessTotal() {
        return excelGenericDataEventListener.getSuccessTotal();
    }

    public int getFailureTotal() {
        return getTotal() - getSuccessTotal().get();
    }

    public ExcelReaderBuilder read(InputStream inputStream) {
        return EasyExcel.read(inputStream, excelGenericDataEventListener);
    }
}
