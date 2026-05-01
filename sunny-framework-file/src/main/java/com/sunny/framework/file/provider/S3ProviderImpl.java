package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;

@Slf4j
public class S3ProviderImpl extends AbstractFileProvider implements FileProvider {

    S3Client s3Client;

    public S3ProviderImpl(FiletProperties.Target config, FileRepository fileRepository) {
        super(config, fileRepository);
        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .endpointOverride(URI.create(config.getRoot()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.getAccessKey(), config.getAccessKeySecret())));
        if (StringUtils.hasText(config.getRegion())) {
            s3ClientBuilder = s3ClientBuilder.region(Region.of(config.getRegion()));
        }
        s3Client = s3ClientBuilder.build();
    }

    @Override
    public FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception {
        PutObjectRequest.Builder pbr = PutObjectRequest.builder().bucket(target.getPath()).key(fileEntity.getPath());
        s3Client.putObject(pbr.build(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return fileResult;
    }
}
