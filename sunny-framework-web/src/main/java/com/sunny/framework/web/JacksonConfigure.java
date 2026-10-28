package com.sunny.framework.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
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

    @Bean
    @Order(10)
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer(JacksonProperties jacksonProperties) {
        SimpleModule module = new SimpleModule();

        DateTimeFormatter dateTimeFormatter = jacksonProperties.getDateFormat() != null ? DateTimeFormatter.ofPattern(jacksonProperties.getDateFormat()) : null;
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        module.addDeserializer(String.class, new ValueDeserializer<>() {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
                return StringUtils.trim(p.getValueAsString());
            }
        });
        return (builder) -> {
            builder.defaultDateFormat(jacksonProperties.getDateFormat() != null ? new SimpleDateFormat(jacksonProperties.getDateFormat()) : null);
            builder.addModule(module);
        };
    }
}
