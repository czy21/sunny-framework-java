package com.sunny.framework.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PagingRollResult<E> {

    private int total;
    private List<E> list;

    private String version;
    private Boolean hasMore;
}
