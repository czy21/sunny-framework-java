package com.sunny.framework.core.model;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class PageIterator<T> implements Iterator<T> {
    private Integer pageIndex;
    private Integer pageSize;

    private int currentIndex;
    private boolean hasMore = true;
    private List<T> list;
    private BiFunction<Integer, Integer, List<T>> loadFunc;

    public PageIterator(Integer pageIndex, Integer pageSize, BiFunction<Integer, Integer, List<T>> loadFunc) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.loadFunc = loadFunc;
    }

    @Override
    public boolean hasNext() {
        if (list != null && list.size() > currentIndex) {
            return true;
        }
        if (!hasMore) {
            return false;
        }
        list = loadFunc.apply(pageIndex, pageSize);
        if (list == null || list.isEmpty()) {
            return false;
        }
        if (list.size() < pageSize) {
            hasMore = false;
        }
        currentIndex = 0;
        pageIndex++;
        return true;
    }

    @Override
    public T next() {
        return list.get(currentIndex++);
    }
}
