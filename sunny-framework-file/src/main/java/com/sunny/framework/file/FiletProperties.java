package com.sunny.framework.file;

import com.sunny.framework.file.model.FileTargetKind;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = FiletProperties.PREFIX)
public class FiletProperties {

    public static final String PREFIX = "sunny.file";

    private String accessKey;
    private String secretKey;

    private Map<String, Target> target = new LinkedHashMap<>();

    @Data
    public static class Target {
        private String key;
        private String root;
        private String path;
        private FileTargetKind kind;
        private String region = "us-east-1";
        private String accessKey;
        private String secretKey;
        private String publicUrl;
        private Duration signatureDuration = Duration.ofHours(2);
        @Name("default")
        private boolean isDefault;
    }

}
