package com.sunny.framework.file;

import com.sunny.framework.file.provider.FileProviderFactory;
import com.sunny.framework.file.provider.FileTargetLoader;
import com.sunny.framework.file.repository.FileRepository;
import com.sunny.framework.file.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@EnableConfigurationProperties(FiletProperties.class)
@Configuration(proxyBeanMethods = false)
public class FileAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public FileTargetLoader fileTargetLoader(FiletProperties properties) {
        return () -> {
            Map<String, FiletProperties.Target> targets = properties.getTarget();

            targets.values().forEach(target -> {
                target.setAccessKey(StringUtils.defaultIfEmpty(target.getAccessKey(), properties.getAccessKey()));
                target.setAccessKeySecret(StringUtils.defaultIfEmpty(target.getAccessKeySecret(), properties.getAccessKeySecret()));
            });

            return targets;
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public FileProviderFactory fileProviderFactory(FileTargetLoader fileTargetLoader, FileRepository fileRepository) {
        return new FileProviderFactory(fileTargetLoader, fileRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public FileService fileService(FileRepository fileRepository, FileProviderFactory fileProviderFactory) {
        return new FileService(fileRepository, fileProviderFactory);
    }
}
