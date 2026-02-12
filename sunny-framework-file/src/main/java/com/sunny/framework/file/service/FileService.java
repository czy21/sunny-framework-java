package com.sunny.framework.file.service;

import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import com.sunny.framework.file.provider.FileProviderFactory;
import com.sunny.framework.file.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FileService {

    FileRepository fileRepository;
    FileProviderFactory fileProviderFactory;

    public FileService(FileRepository fileRepository, FileProviderFactory fileProviderFactory) {
        this.fileRepository = fileRepository;
        this.fileProviderFactory = fileProviderFactory;
    }

    public FileResult queryById(String id) {
        FileEntity fileEntity = fileRepository.selectByPrimaryKey(id);
        return fileProviderFactory.getProvider(fileEntity.getTargetKey()).generateFileResult(fileEntity);
    }

    public List<FileResult> queryByIds(List<String> ids) {
        List<FileEntity> fileEntities = fileRepository.selectListByIds(ids);
        return fileEntities.stream().map(t -> fileProviderFactory.getProvider(t.getTargetKey()).generateFileResult(t)).toList();
    }

}
