package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

public interface FileProvider {

    FiletProperties.Target getTarget();

    FileResult upload(MultipartFile file);

    FileResult generateFileResult(FileEntity fileEntity);

    default List<FileResult> list(String path) {
        return null;
    }

    default String get(String path, Duration expire) {
        return null;
    }
}
