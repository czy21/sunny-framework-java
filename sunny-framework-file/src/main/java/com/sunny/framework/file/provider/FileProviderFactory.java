package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.repository.FileRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileProviderFactory implements InitializingBean {

    private static final ConcurrentHashMap<String, FileProvider> FILE_PROVIDERS = new ConcurrentHashMap<>();

    private FileTargetLoader fileTargetLoader;
    private FileRepository fileRepository;

    public FileProviderFactory(FileTargetLoader fileTargetLoader, FileRepository fileRepository) {
        this.fileTargetLoader = fileTargetLoader;
        this.fileRepository = fileRepository;
    }

    public FileProvider getProvider(String key) {
        return FILE_PROVIDERS.get(key);
    }

    @Override
    public void afterPropertiesSet() {
        for (Map.Entry<String, FiletProperties.Target> p : fileTargetLoader.get().entrySet()) {
            if (!StringUtils.hasText(p.getValue().getKey())){
                p.getValue().setKey(p.getKey());
            }
            switch (p.getValue().getKind()) {
                case LOCAL:
                    FILE_PROVIDERS.put(p.getKey(),new LocalFileProviderImpl(p.getValue(), fileRepository));
                    break;
                case OSS_MINIO:
                    FILE_PROVIDERS.put(p.getKey(), new OSSMinioFileProviderImpl(p.getValue(), fileRepository));
                    break;
                case OSS_ALI:
                    FILE_PROVIDERS.put(p.getKey(), new OSSAliFileProviderImpl(p.getValue(), fileRepository));
                    break;
                default:
                    break;
            }
        }
    }
}
