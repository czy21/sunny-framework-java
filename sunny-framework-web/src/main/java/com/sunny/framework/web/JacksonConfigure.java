package com.sunny.framework.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JacksonConfigure {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    @Order(10)
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        module.addDeserializer(String.class, new ValueDeserializer<>() {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
                return StringUtils.trim(p.getValueAsString());
            }
        });
        return (builder) -> {
            builder.defaultDateFormat(new SimpleDateFormat(DATE_FORMAT_PATTERN));
            builder.addModule(module);
        };
    }
}
