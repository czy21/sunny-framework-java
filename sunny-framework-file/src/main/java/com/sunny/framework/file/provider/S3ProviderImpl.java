package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Slf4j
public class S3ProviderImpl extends AbstractFileProvider implements FileProvider {

    S3Client s3Client;

    public S3ProviderImpl(FiletProperties.Target target, FileRepository fileRepository) {
        super(target, fileRepository);
        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .endpointOverride(URI.create(target.getRoot()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(target.getAccessKey(), target.getAccessKeySecret())));
        s3ClientBuilder = s3ClientBuilder.region(Region.of(target.getRegion()));
        s3ClientBuilder = s3ClientBuilder.forcePathStyle(true);
        s3Client = s3ClientBuilder.build();
    }

    @Override
    public FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception {
        PutObjectRequest.Builder pbr = PutObjectRequest.builder().bucket(target.getPath()).key(fileEntity.getPath());
        s3Client.putObject(pbr.build(), RequestBody.fromBytes(file.getBytes()));
        return fileResult;
    }

    @Override
    public List<FileResult> list(String path) {

        ListObjectsV2Request req = ListObjectsV2Request.builder()
                .bucket(target.getPath())
                .prefix(path)
                .build();

        ListObjectsV2Response res = s3Client.listObjectsV2(req);
        return res.contents().stream().map(t -> FileResult.builder()
                .id(t.key())
                .name(t.key())
                .size(t.size())
                .build()).toList();
    }

    @Override
    public String get(String path, Duration expire) {
        S3Presigner.Builder presignerBuilder = S3Presigner.builder()
                .endpointOverride(URI.create(target.getRoot()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(target.getAccessKey(), target.getAccessKeySecret())))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                );
        presignerBuilder = presignerBuilder.region(Region.of(target.getRegion()));
        try (S3Presigner presigner = presignerBuilder.build()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(target.getPath())
                    .key(path)
                    .build();
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(builder -> builder.signatureDuration(expire).getObjectRequest(getObjectRequest));
            return presignedRequest.url().toString();
        }
    }

    @Override
    public void appendText(String path, String text) {
        String bucket = target.getPath();
        StringBuilder sb = new StringBuilder();
        try {
            ResponseInputStream<GetObjectResponse> obj = s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(path).build());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(obj, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
        } catch (NoSuchKeyException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sb.append(text);

        byte[] result = sb.toString().getBytes(StandardCharsets.UTF_8);

        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(path).contentType("text/plain; charset=utf-8").build(), RequestBody.fromBytes(result));
    }
}
