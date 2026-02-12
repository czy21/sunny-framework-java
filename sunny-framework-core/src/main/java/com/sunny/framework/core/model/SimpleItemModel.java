package com.sunny.framework.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleItemModel<T> implements TreeNode<T> {
    private String label;
    private T value;
    private String parentLabel;
    private T parentValue;
    private List<T> parentValues;
    private List<T> pathValues;
    private Integer level;
    private Map<String, Object> extra;
    private List<SimpleItemModel<T>> children;

    @Builder.Default
    private Integer sort = 0;

    public static <T> SimpleItemModel<T> of(String label, T value, String parentLabel, T parentValue) {
        return SimpleItemModel.<T>builder()
                .label(label)
                .value(value)
                .parentLabel(parentLabel)
                .parentValue(parentValue)
                .build();
    }

    public static <T> SimpleItemModel<T> of(String label, T value) {
        return of(label, value, null, null);
    }

    @Override
    public T getKey() {
        return value;
    }

    @Override
    public void setKey(T key) {
        this.value = key;
    }

    @Override
    public T getParentKey() {
        return parentValue;
    }

    @Override
    public void setParentKey(T parentKey) {
        this.parentValue = parentKey;
    }

    @Override
    public List<T> getParentKeys() {
        return this.parentValues;
    }

    @Override
    public void setParentKeys(List<T> parentKeys) {
        this.parentValues = parentKeys;
    }

    @Override
    public List<T> getPathKeys() {
        return pathValues;
    }

    @Override
    public void setPathKeys(List<T> pathKeys) {
        this.pathValues = pathKeys;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setChildren(List<? extends TreeNode<T>> children) {
        this.children = (List<SimpleItemModel<T>>) children;
    }

    public static <T> String translateByValue(List<SimpleItemModel<T>> list, T value) {
        return translateByValue(list, value, null);
    }

    public static <T> String translateByValue(List<SimpleItemModel<T>> list, T value, String defaultValue) {
        return Optional.ofNullable(list).orElse(new ArrayList<>()).stream().filter(t -> t.getValue().equals(value)).map(SimpleItemModel::getLabel).findFirst().orElse(defaultValue);
    }

    public static <T> List<String> translateByValues(List<SimpleItemModel<T>> list, List<T> values) {
        return CollectionUtils.isNotEmpty(values)
                ? Optional.ofNullable(list).orElse(new ArrayList<>()).stream().filter(t -> values.contains(t.getValue())).map(SimpleItemModel::getLabel).collect(Collectors.toList())
                : new ArrayList<>();
    }

    public static String translateTrueFalse(Boolean value, Function<Boolean, String> labelFunc, String defaultLabel) {
        return Optional.ofNullable(value).map(labelFunc).orElse(defaultLabel);
    }

    public static String translateTrueFalse(Boolean value, Function<Boolean, String> labelFunc) {
        return translateTrueFalse(value, labelFunc, null);
    }

    public static String translateYesNo(Boolean value, String defaultLabel) {
        return translateTrueFalse(value, t -> t ? "是" : "否", defaultLabel);
    }

    public static String translateYesNo(Boolean value) {
        return translateYesNo(value, null);
    }
}
