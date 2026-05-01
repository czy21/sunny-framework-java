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

public class OSSAliFileProviderImpl extends AbstractFileProvider implements FileProvider {

    OSS client;

    public OSSAliFileProviderImpl(FiletProperties.Target target, FileRepository fileRepository) {
        super(target, fileRepository);
        client = OSSClientBuilder.create().endpoint(target.getRoot())
                .credentialsProvider(CredentialsProviderFactory.newDefaultCredentialProvider(target.getAccessKey(), target.getAccessKeySecret()))
                .build();
    }

    @Override
    public FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception {
        PutObjectRequest putObjectRequest = new PutObjectRequest(target.getPath(), fileEntity.getPath(), file.getInputStream());
        client.putObject(putObjectRequest);
        return fileResult;
    }
}
