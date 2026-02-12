package com.sunny.framework.file.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.NumberUtils;
import com.alibaba.excel.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.ParseException;

@Slf4j
public class StringNumberConverter implements Converter<String> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.NUMBER;
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        if (cellData.getNumberValue() != null) {
            return cellData.getNumberValue().toPlainString();
        }
        if (!StringUtils.isEmpty(cellData.getStringValue())) {
            try {
                return NumberUtils.parseBigDecimal(cellData.getStringValue(), contentProperty).toPlainString();
            } catch (ParseException e) {
                log.error("Number parse to BigDecimal error", e);
            }
        }
        return cellData.getStringValue();
    }

    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        try {
            return NumberUtils.formatToCellData(new BigDecimal(value), contentProperty);
        } catch (Exception e) {
            return new WriteCellData<>(value);
        }
    }
}