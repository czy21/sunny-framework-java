package com.sunny.framework.test.mapper;

import com.sunny.framework.test.model.po.UserPO;

/**
* @author chenzhaoyu
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-08-02 14:16:05
* @Entity com.sunny.framework.test.model.po.UserPO
*/
public interface UserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserPO record);

    int insertSelective(UserPO record);

    UserPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPO record);

    int updateByPrimaryKey(UserPO record);

}
