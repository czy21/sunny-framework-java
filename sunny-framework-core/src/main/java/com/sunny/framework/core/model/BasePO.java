package com.sunny.framework.core.model;

import lombok.Data;

import java.time.LocalDateTime;

public class BasePO<TID, U> {
    private TID id;
    private LocalDateTime createTime;
    private U createUser;
    private LocalDateTime updateTime;
    private U updateUser;
    private Boolean deleted;

    public TID getId() {
        return id;
    }

    public void setId(TID id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public U getCreateUser() {
        return createUser;
    }

    public void setCreateUser(U createUser) {
        this.createUser = createUser;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public U getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(U updateUser) {
        this.updateUser = updateUser;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
