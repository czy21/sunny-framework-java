package com.sunny.framework.test.file;

import com.alibaba.excel.EasyExcel;
import com.sunny.framework.file.excel.EasyExcelProperty;
import com.sunny.framework.file.excel.EasyExcelReader;
import com.sunny.framework.file.excel.EasyExcelWriter;
import com.sunny.framework.file.model.ExcelResult;
import com.sunny.framework.test.model.excel.UserImport;
import jakarta.validation.Validator;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ResourceUtils;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class FileTest {

    @Autowired
    JsonMapper jsonMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    Validator validator;

    static Map<String, EasyExcelProperty> userNameProperty = new LinkedHashMap<>();

    static {
        userNameProperty.put("name", EasyExcelProperty.of(0, List.of("姓名").stream().collect(Collectors.toList())));
        userNameProperty.put("age", EasyExcelProperty.of(1, List.of("年龄").stream().collect(Collectors.toList()), Integer.class));
        userNameProperty.put("birthDay", EasyExcelProperty.of(2, List.of("出生日期").stream().collect(Collectors.toList())));
    }

    @Test
    public void testReader() throws FileNotFoundException {
        File userImportFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "excel/user-import.xlsx");
        EasyExcelReader<UserImport> reader = new EasyExcelReader<>(jsonMapper, stringRedisTemplate, validator);
        reader.process(ctx -> {
            ctx.getRows().forEach(t -> {
                ctx.getSuccessTotal().incrementAndGet();
            });
        });
        reader.read(new FileInputStream(userImportFile)).head(UserImport.class).doReadAll();
        ExcelResult result = new ExcelResult();
        result.setToken(reader.getToken());
        result.setSuccess(reader.getTotal() == reader.getSuccessTotal().get());
        result.setSuccessTotal(reader.getSuccessTotal().get());
        result.setFailureTotal(reader.getFailureTotal());
        System.out.println();
    }

    @Test
    public void testReadMap() throws FileNotFoundException {
        File userImportFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "excel/user-import.xlsx");
        EasyExcelReader<Map<String, Object>> reader = new EasyExcelReader<>(jsonMapper, stringRedisTemplate, validator);
        reader.nameProperty(userNameProperty).process(ctx -> {
            for (Map<String, Object> t : ctx.getRows()) {
                Integer rowIndex = (Integer) t.get("rowIndex");
                List<String> errors = ctx.getError().get(rowIndex);
                if (ObjectUtils.isEmpty(t.get("name"))) {
                    errors.add("姓名不能为空");
                    continue;
                }
                ctx.getSuccessTotal().incrementAndGet();
            }
        });
        reader.read(new FileInputStream(userImportFile)).headRowNumber(userNameProperty.values().stream().map(t -> t.getHead().size()).max(Comparator.comparingInt(t -> t)).orElse(0)).doReadAll();
        ExcelResult result = new ExcelResult();
        result.setToken(reader.getToken());
        result.setSuccess(reader.getTotal() == reader.getSuccessTotal().get());
        result.setSuccessTotal(reader.getSuccessTotal().get());
        result.setFailureTotal(reader.getFailureTotal());
        System.out.println();
    }

    @Test
    public void testWriteMapError() throws Exception {
        String filePath = ResourceUtils.getURL("classpath:excel/").getPath() + "user-import-error.xlsx";
        EasyExcelWriter<Map<String, Object>> writer = new EasyExcelWriter<>(jsonMapper, stringRedisTemplate);
        writer.token("c9601e48754747a4a75d6e6cedc2a315");
        writer.doWrite(EasyExcel.write(filePath), userNameProperty);
    }

    @Test
    public void testWriter() throws Exception {
        List<List<String>> heads = new ArrayList<>();
        List<String> headName = new ArrayList<>();
        headName.add("一级");
        headName.add("姓名");
        List<String> headAge = new ArrayList<>();
        headAge.add("二级");
        headAge.add("年龄");

        List<String> headBirthDay = new ArrayList<>();
        headBirthDay.add("二级");
        headBirthDay.add("生日");

        heads.add(headName);
        heads.add(headAge);
        heads.add(headBirthDay);
        EasyExcel.write(ResourceUtils.getURL("classpath:excel/").getPath() + "user-export.xlsx").head(heads).sheet().doWrite(new ArrayList<>());
    }
}
