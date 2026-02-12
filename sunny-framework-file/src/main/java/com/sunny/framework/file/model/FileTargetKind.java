package com.sunny.framework.file.model;

import lombok.Getter;

@Getter
public enum FileTargetKind {
    LOCAL,
    OSS_MINIO,
    OSS_ALI,
    OSS_TENCENT
}
