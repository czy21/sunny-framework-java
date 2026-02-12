package com.sunny.framework.file.provider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.model.PutObjectRequest;
import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.repository.FileRepository;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

public class OSSAliFileProviderImpl extends AbstractFileProvider implements FileProvider {

    OSS client;

    public OSSAliFileProviderImpl(FiletProperties.Target config, FileRepository fileRepository) {
        super(config, fileRepository);
        client = OSSClientBuilder.create().endpoint(config.getRoot())
                .credentialsProvider(CredentialsProviderFactory.newDefaultCredentialProvider(config.getAccessKey(), config.getAccessKeySecret()))
                .build();
    }

    @Override
    public FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception {
        PutObjectRequest putObjectRequest = new PutObjectRequest(config.getPath(), fileEntity.getPath(), file.getInputStream());
        client.putObject(putObjectRequest);
        return fileResult;
    }
}
