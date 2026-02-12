package com.sunny.framework.file.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResult {
    String id;
    String name;
    String type;
    String path;
    String fullPath;
}
