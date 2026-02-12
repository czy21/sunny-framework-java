package com.sunny.framework.test.db;

import com.sunny.framework.test.mapper.UserMapper;
import com.sunny.framework.test.model.po.UserPO;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan(basePackageClasses = UserMapper.class)
public class MybatisJdbcTest {

    @Autowired
    UserMapper userMapper;

    @Test
    public void testInsert() {
        UserPO userPO = new UserPO();
        userPO.setName("你好");
        userMapper.insertSelective(userPO);
        System.out.println();
    }

//    @Test
//    public void testMybatisSelect() {
//        userMapper.find
//    }
}
