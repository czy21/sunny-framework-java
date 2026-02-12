package com.sunny.framework.file.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.sunny.framework.file.listener.ExcelGenericDataEventListener;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EasyExcelWriter<T> {
    private String token;
    private int batch = 200;
    private final StringRedisTemplate redisTemplate;
    private final JsonMapper jsonMapper;

    public EasyExcelWriter(JsonMapper jsonMapper, StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
    }

    public EasyExcelWriter<T> batch(int batch) {
        this.batch = batch;
        return this;
    }

    public EasyExcelWriter<T> token(String token) {
        this.token = token;
        return this;
    }

    public void doWriteGeneric(ExcelWriterBuilder writerBuilder, Class<?> rowType, Function<T, Object> rowFunc) {
        try (ExcelWriter writer = writerBuilder.build()) {
            JavaType javaType = jsonMapper.getTypeFactory().constructType(rowType);
            var boundListOperation = redisTemplate.boundListOps(ExcelGenericDataEventListener.DATA_KEY_PREFIX_FUNC.apply(token));
            int start = 0;
            int end = batch;
            List<String> list;
            do {
                list = boundListOperation.range(start, end - 1);
                List<Object> targets = Optional.ofNullable(list).orElse(List.of()).stream()
                        .map(t -> {
                            T row = jsonMapper.readValue(t, javaType);
                            return rowFunc.apply(row);
                        }).collect(Collectors.toList());
                writer.write(targets, EasyExcel.writerSheet().build());
                start = end;
                end = start + batch;
            } while (CollectionUtils.isNotEmpty(list));
            writer.finish();
        }
    }

    public void doWrite(ExcelWriterBuilder writerBuilder, Class<?> head, Function<T, T> rowFunc) {
        doWriteGeneric(writerBuilder, head, row -> {
            String errorListStr = (String) redisTemplate.opsForHash().get(ExcelGenericDataEventListener.ERROR_KEY_PREFIX_FUNC.apply(token), String.valueOf(((BaseExcelModel) row).getRowIndex()));
            if (!StringUtils.isEmpty(errorListStr)) {
                List<String> errors = jsonMapper.readValue(errorListStr, new TypeReference<List<String>>() {
                });
                String message = String.join(";", errors);
                ((BaseExcelModel) row).setMessage(message);
            }
            return rowFunc.apply(row);
        });
    }

    public void doWrite(ExcelWriterBuilder writerBuilder, Class<?> head) {
        doWrite(writerBuilder, head, r -> r);
    }

    public void doWrite(OutputStream outputStream, Class<?> head) {
        doWrite(EasyExcel.write(outputStream).head(head), head);
    }

    @SuppressWarnings("unchecked")
    public void doWrite(ExcelWriterBuilder writerBuilder, Map<String, EasyExcelProperty> nameProperty, Function<T, T> rowFunc) {
        int messageIndex = nameProperty.values().stream().max(Comparator.comparingInt(EasyExcelProperty::getIndex)).map(EasyExcelProperty::getIndex).orElse(0) + 1;
        List<String> message = new ArrayList<>();
        message.add("错误信息");
        nameProperty.put("message", EasyExcelProperty.of(messageIndex, message));
        Map<String, List<String>> nameHead = IntStream.rangeClosed(0, messageIndex).collect(LinkedHashMap::new,
                (m, n) -> nameProperty.entrySet().stream()
                        .filter(p -> p.getValue().getIndex().equals(n))
                        .findFirst()
                        .ifPresentOrElse(p -> m.put(p.getKey(), p.getValue().getHead()), () -> m.put("empty" + n, new ArrayList<>())),
                Map::putAll);
        writerBuilder = writerBuilder.head(new ArrayList<>(nameHead.values()));
        doWriteGeneric(writerBuilder, Map.class, row -> {
            String errorListStr = (String) redisTemplate.opsForHash().get(ExcelGenericDataEventListener.ERROR_KEY_PREFIX_FUNC.apply(token), String.valueOf(((Map<String, Object>) row).get("rowIndex")));
            if (!StringUtils.isEmpty(errorListStr)) {
                List<String> errors = jsonMapper.readValue(errorListStr, new TypeReference<List<String>>() {
                });
                ((Map<String, Object>) row).put("message", String.join(";", errors));
            }
            return nameHead.keySet().stream().map(t -> ((Map<String, Object>) rowFunc.apply(row)).get(t)).collect(Collectors.toList());
        });
    }

    public void doWrite(ExcelWriterBuilder writerBuilder, Map<String, EasyExcelProperty> nameProperty) {
        doWrite(writerBuilder, nameProperty, r -> r);
    }

    public void doWrite(OutputStream outputStream, Map<String, EasyExcelProperty> nameProperty) {
        doWrite(EasyExcel.write(outputStream), nameProperty, r -> r);
    }
}
