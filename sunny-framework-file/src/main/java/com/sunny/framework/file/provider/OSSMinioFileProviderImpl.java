package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.repository.FileRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.web.multipart.MultipartFile;


public class OSSMinioFileProviderImpl extends AbstractFileProvider implements FileProvider {

    MinioClient client;


    public OSSMinioFileProviderImpl(FiletProperties.Target target, FileRepository fileRepository) {
        super(target, fileRepository);
        client = MinioClient.builder().endpoint(target.getRoot()).credentials(target.getAccessKey(), target.getAccessKeySecret()).build();
    }

    @Override
    public FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception {
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(target.getPath())
                .stream(file.getInputStream(), file.getSize(), 1024 * 1024 * 10)
                .object(fileEntity.getPath())
                .contentType(file.getContentType())
                .build();
        client.putObject(putObjectArgs);
        return fileResult;
    }
}
