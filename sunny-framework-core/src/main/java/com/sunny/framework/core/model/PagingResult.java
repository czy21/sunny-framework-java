package com.sunny.framework.core.model;

import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

@Data
@NoArgsConstructor
public class PagingResult<E> {

    private int page;
    private int pageSize;
    private int total;
    private List<E> list;

    private boolean isHeadPage;
    private boolean isLastPage;

    public PagingResult(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public PagingResult(int page, int pageSize, int total) {
        this.page = page;
        this.pageSize = pageSize;
        this.setTotal(total);
    }

    public boolean getIsHeadPage() {
        return isHeadPage;
    }

    public void setIsHeadPage(boolean headPage) {
        isHeadPage = headPage;
    }

    public boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public int getTotalPage() {
        return (this.getTotal() + this.getPageSize() - 1) / this.getPageSize();
    }

    public static <T> PagingResult<T> convert(PageInfo<T> pageInfo) {
        return convert(pageInfo, s -> s);
    }

    public static <S, T> PagingResult<T> convert(PageInfo<S> pageInfo, Function<List<S>, List<T>> convertListFunc) {
        if (pageInfo == null) {
            return null;
        }
        PagingResult<T> pagingResult = new PagingResult<>(pageInfo.getPageNum(), pageInfo.getPageSize(), (int) pageInfo.getTotal());
        pagingResult.setList(convertListFunc.apply(pageInfo.getList()));
        pagingResult.setIsHeadPage(pageInfo.isIsFirstPage());
        pagingResult.setIsLastPage(pageInfo.isIsLastPage());
        return pagingResult;
    }
}

