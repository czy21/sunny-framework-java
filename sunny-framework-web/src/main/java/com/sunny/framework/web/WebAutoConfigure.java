package com.sunny.framework.web;


import com.sunny.framework.web.feign.FeignConfigure;
import com.sunny.framework.web.handler.GlobalExceptionHandler;
import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

@EnableConfigurationProperties(SunnyWebProperties.class)
@PropertySource("classpath:sunny-common-web-default.properties")
@EnableWebMvc
@Configuration(proxyBeanMethods = false)
@Import({JacksonConfigure.class, FeignConfigure.class, GlobalExceptionHandler.class})
public class WebAutoConfigure implements WebMvcConfigurer {

    SunnyWebProperties sunnyWebProperties;

    public WebAutoConfigure(SunnyWebProperties sunnyWebProperties) {
        this.sunnyWebProperties = sunnyWebProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(sunnyWebProperties.getAllowedOriginPatterns().toArray(new String[0]))
                .allowedHeaders(sunnyWebProperties.getAllowedHeaders().toArray(new String[0]))
                .allowedMethods(sunnyWebProperties.getAllowedMethods().toArray(new String[0]))
                .allowCredentials(true);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        PathPatternParser patternParser = configurer.getPatternParserOrDefault();
        patternParser.setCaseSensitive(false);
        configurer.setPatternParser(patternParser);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
        return (registry) -> {
            new ProcessMemoryMetrics().bindTo(registry);
            new ProcessThreadMetrics().bindTo(registry);
        };
    }
}
