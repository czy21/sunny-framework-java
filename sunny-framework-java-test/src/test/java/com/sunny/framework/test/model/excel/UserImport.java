package com.sunny.framework.test.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sunny.framework.file.excel.BaseExcelModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserImport extends BaseExcelModel {

    @NotBlank(message = "姓名不能为空")
    @ExcelProperty(value = "姓名")
    private String name;

    @NotNull(message = "年龄不能为空")
    @ExcelProperty(value = "年龄")
    private Integer age;
    @ExcelProperty(value = "出生年月")
    private LocalDate birthDay;
    @ExcelProperty(value = "身高")
    private BigDecimal height;

    @ExcelProperty(value = "错误信息")
    private String message;
}
