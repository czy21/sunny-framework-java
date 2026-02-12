package com.sunny.framework.job;


import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:sunny-common-job-default.properties")
@ConditionalOnProperty(prefix = XxlJobProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(XxlJobProperties.class)
@Configuration
public class XxlJobAutoConfigure {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobAutoConfigure.class);

    private final XxlJobProperties properties;

    public XxlJobAutoConfigure(XxlJobProperties properties) {
        this.properties = properties;
    }

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(properties.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAccessToken(properties.getAccessToken());
        xxlJobSpringExecutor.setAppname(properties.getExecutor().getAppName());
        xxlJobSpringExecutor.setIp(properties.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(properties.getExecutor().getPort());
        xxlJobSpringExecutor.setLogPath(properties.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(properties.getExecutor().getLogRetentionDays());
        return xxlJobSpringExecutor;
    }
}
