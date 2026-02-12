package com.sunny.framework.file.excel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EasyExcelProperty {
    private Integer index;
    private List<String> head;
    private Class<?> javaType;

    public EasyExcelProperty(Integer index, List<String> head, Class<?> javaType) {
        this.index = index;
        this.head = head;
        this.javaType = javaType;
    }

    public static EasyExcelProperty of(Integer index, List<String> head, Class<?> javaType) {
        return new EasyExcelProperty(index, head,javaType);
    }

    public static EasyExcelProperty of(Integer index, List<String> head) {
        return new EasyExcelProperty(index, head,null);
    }
}
