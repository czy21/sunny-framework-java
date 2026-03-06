package com.sunny.framework.cache;

import org.redisson.config.Config;

@FunctionalInterface
public interface RedissonAutoConfigurationCustomizer {

    /**
     * Customize the RedissonClient configuration.
     *
     * @param configuration the {@link Config} to customize
     */
    void customize(Config configuration);

}