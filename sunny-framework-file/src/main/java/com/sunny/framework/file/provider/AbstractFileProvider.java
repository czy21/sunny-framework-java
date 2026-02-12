package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.model.FileTargetKind;
import com.sunny.framework.file.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public abstract class AbstractFileProvider implements FileProvider {

    protected FiletProperties.Target config;
    protected FileRepository fileRepository;

    public AbstractFileProvider(FiletProperties.Target config,
                                FileRepository fileRepository) {
        this.config = config;
        this.fileRepository = fileRepository;
    }

    public FileResult upload(MultipartFile file) {
        try {
            FileEntity fileEntity = generateFileEntity(file);
            FileResult fileResult = generateFileResult(fileEntity);
            fileResult = upload(file, fileEntity, fileResult);
            fileRepository.insertSelective(fileEntity);
            return fileResult;
        } catch (Exception e) {
            log.error("upload error", e);
            throw new RuntimeException(e);
        }
    }

    protected abstract FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception;

    public static String getExtension(MultipartFile file) {
        return FilenameUtils.getExtension(file.getOriginalFilename());
    }

    public static String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + (StringUtils.isEmpty(extension) ? "" : "." + extension);
    }

    public FileEntity generateFileEntity(MultipartFile file) {
        String path = FilenameUtils.separatorsToUnix(Paths.get(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), generateFileName(file)).toString());
        String id = UUID.randomUUID().toString();
        return FileEntity.builder()
                .id(id)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .path(path)
                .targetKey(config.getKey())
                .build();
    }

    @Override
    public FileResult generateFileResult(FileEntity fileEntity) {
        FileResult fileResult = FileResult.builder()
                .id(fileEntity.getId())
                .name(fileEntity.getName())
                .type(fileEntity.getType())
                .path(FilenameUtils.separatorsToUnix(Paths.get(config.getPath(), fileEntity.getPath()).toString()))
                .build();
        try {
            if (config.getKind() != FileTargetKind.LOCAL) {
                fileResult.setFullPath(new URI(config.getRoot()).resolve(fileResult.getPath()).toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileResult;
    }
}
