package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;
import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
public class LocalFileProviderImpl extends AbstractFileProvider implements FileProvider {

    public LocalFileProviderImpl(FiletProperties.Target config, FileRepository fileRepository) {
        super(config, fileRepository);
    }

    @Override
    public FileResult upload(MultipartFile file, FileEntity fileEntity, FileResult fileResult) throws Exception {
        File f = FileUtils.getFile(config.getRoot(), fileResult.getPath());
        FileUtils.forceMkdir(f.getParentFile());
        FileUtils.copyInputStreamToFile(file.getInputStream(), f);
        return fileResult;
    }
}
