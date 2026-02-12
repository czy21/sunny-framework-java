package com.sunny.framework.file.provider;

import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileProvider {
    FileResult upload(MultipartFile file);

    FileResult generateFileResult(FileEntity fileEntity);
}
