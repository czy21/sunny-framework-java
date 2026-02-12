package com.sunny.framework.test.model.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * @TableName user
 */
@Data
public class UserPO {
    /**
     * 
     */
    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private Integer age;

    /**
     * 
     */
    private String address;

    /**
     * 
     */
    private Integer status;

    /**
     * 
     */
    private String createUser;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private String updateUser;

    /**
     * 
     */
    private LocalDateTime updateTime;

    /**
     * 
     */
    private Boolean deleted;
}