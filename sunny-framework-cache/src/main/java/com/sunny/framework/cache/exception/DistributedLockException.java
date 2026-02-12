package com.sunny.framework.cache.exception;

/**
 * 分布式锁异常
 */
public class DistributedLockException extends RuntimeException {
    
    public DistributedLockException(String message) {
        super(message);
    }
    
    public DistributedLockException(String message, Throwable cause) {
        super(message, cause);
    }
}