package com.sunny.framework.file;

import com.sunny.framework.file.model.FileTargetKind;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.bind.Name;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = FiletProperties.PREFIX)
public class FiletProperties {

    public static final String PREFIX = "file";

    private Map<String, Target> target = new LinkedHashMap<>();

    @Data
    public static class Target {
        private String key;
        private String root;
        private String path;
        private FileTargetKind kind;
        private String region;
        private String accessKey;
        private String accessKeySecret;
        @Name("default")
        private boolean isDefault;
    }

}
