package com.sunny.framework.file.provider;

import com.sunny.framework.file.FiletProperties;

import java.util.Map;

@FunctionalInterface
public interface FileTargetLoader {
    Map<String, FiletProperties.Target> get();
}
