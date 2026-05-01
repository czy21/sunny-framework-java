package com.sunny.framework.file.provider;

import com.sunny.framework.file.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.*;

public class FileProviderFactory implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(FileProviderFactory.class);

    private Map<String, FileProvider> providers;

    private FileProvider defaultProvider;

    private FileTargetLoader fileTargetLoader;
    private FileRepository fileRepository;

    public FileProviderFactory(FileTargetLoader fileTargetLoader, FileRepository fileRepository) {
        this.fileTargetLoader = fileTargetLoader;
        this.fileRepository = fileRepository;
    }

    public Map<String, FileProvider> getProviders() {
        return providers;
    }

    public FileProvider getDefaultProvider() {
        return defaultProvider;
    }

    @Override
    public void afterPropertiesSet() {
        providers = Collections.unmodifiableMap(fileTargetLoader.get().entrySet().stream()
                .sorted(Comparator.comparing(entry -> !entry.getValue().isDefault()))
                .peek(p -> {
                    if (!StringUtils.hasText(p.getValue().getKey())) {
                        p.getValue().setKey(p.getKey());
                    }
                })
                .collect(LinkedHashMap::new, (m, n) -> {
                    switch (n.getValue().getKind()) {
                        case LOCAL -> m.put(n.getKey(), new LocalFileProviderImpl(n.getValue(), fileRepository));
                        case S3 -> m.put(n.getKey(), new S3ProviderImpl(n.getValue(), fileRepository));
                        case OSS_MINIO -> m.put(n.getKey(), new OSSMinioFileProviderImpl(n.getValue(), fileRepository));
                        case OSS_ALI -> m.put(n.getKey(), new OSSAliFileProviderImpl(n.getValue(), fileRepository));
                    }
                }, Map::putAll));
        logger.info("file providers: {}", String.join(" ", providers.keySet()));
        defaultProvider = providers.values().stream().findFirst().orElse(null);
        logger.info("file default provider: {}", Optional.ofNullable(defaultProvider).map(t -> t.getTarget().getKey()).orElse(null));
    }
}
