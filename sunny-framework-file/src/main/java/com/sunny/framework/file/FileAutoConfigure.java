package com.sunny.framework.file;

import com.sunny.framework.file.provider.FileProviderFactory;
import com.sunny.framework.file.provider.FileTargetLoader;
import com.sunny.framework.file.repository.FileRepository;
import com.sunny.framework.file.service.FileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(FiletProperties.class)
@Configuration
public class FileAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public FileTargetLoader fileTargetLoader(FiletProperties filetProperties) {
        return filetProperties::getTarget;
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
