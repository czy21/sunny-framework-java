package com.sunny.framework.file.model;

import lombok.Getter;

@Getter
public enum FileTargetKind {
    LOCAL,
    S3,
    OSS_MINIO,
    OSS_ALI,
    OSS_TENCENT
}
