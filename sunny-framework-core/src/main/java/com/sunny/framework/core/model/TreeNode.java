package com.sunny.framework.core.model;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public interface TreeNode<T> extends Comparable<TreeNode<T>> {

    T getKey();

    default void setKey(T key) {
    }

    T getParentKey();

    default void setParentKey(T parentKey) {
    }

    default List<T> getParentKeys() {
        return null;
    }

    default void setParentKeys(List<T> parentKeys) {
    }

    default List<T> getPathKeys() {
        return null;
    }

    default void setPathKeys(List<T> pathKeys) {
    }

    default Integer getLevel() {
        return null;
    }

    default void setLevel(Integer level) {
    }

    List<? extends TreeNode<T>> getChildren();

    default void setChildren(List<? extends TreeNode<T>> children) {
    }

    default Integer getSort() {
        return 0;
    }

    default void setSort(Integer sort) {
    }

    default TreeNodeOperation getOperation() {
        return null;
    }

    default void setOperation(TreeNodeOperation operation) {
    }

    @Override
    default int compareTo(TreeNode<T> o) {
        return ObjectUtils.compare(this.getSort(), o.getSort());
    }
}
