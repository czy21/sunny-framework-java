package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.repository.FileRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class OSSMinioFileProviderImpl extends AbstractFileProvider implements FileProvider {

    MinioClient client;


    public OSSMinioFileProviderImpl(FiletProperties.Target config, FileRepository fileRepository) {
        super(config, fileRepository);
        client = MinioClient.builder().endpoint(config.getRoot()).credentials(config.getAccessKey(), config.getAccessKeySecret()).build();
    }

    @Override
    public FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception {
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(config.getPath())
                .stream(file.getInputStream(), file.getSize(), 1024 * 1024 * 10)
                .object(fileEntity.getPath())
                .contentType(file.getContentType())
                .build();
        client.putObject(putObjectArgs);
        return fileResult;
    }
}
