package com.sunny.framework.job.client;

import com.sunny.framework.job.XxlJobProperties;
import com.xxl.job.core.constant.Const;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tools.jackson.databind.json.JsonMapper;


public class XxlJobApiConfig {

    @Bean
    public RequestInterceptor xxlJobApiInterceptor(XxlJobProperties xxlJobProperties) {
        return template -> {
            template.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            template.header(Const.XXL_JOB_ACCESS_TOKEN, xxlJobProperties.getAccessToken());
            template.header(Const.XXL_JOB_APPNAME, xxlJobProperties.getExecutor().getAppName());
        };
    }

    @Bean
    public Encoder xxlJobApiEncoder(JsonMapper jsonMapper) {
        return (object, bodyType, template) -> template.body(jsonMapper.writeValueAsString(object));
    }

}
