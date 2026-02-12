package com.sunny.framework.file.repository;

import com.sunny.framework.file.model.FileEntity;
import com.sunny.framework.file.model.FileResult;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository {
    int deleteByPrimaryKey(String id);

    int insert(FileEntity row);

    int insertSelective(FileEntity row);

    FileEntity selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(FileEntity row);

    int updateByPrimaryKey(FileEntity row);

    List<FileEntity> selectListByIds(@Param("ids") List<String> ids);
}
