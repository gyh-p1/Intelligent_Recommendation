package com.travel.travelserver.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public static <T> PageResult<T> of(List<T> source, int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);
        int fromIndex = Math.min((safePage - 1) * safePageSize, source.size());
        int toIndex = Math.min(fromIndex + safePageSize, source.size());
        List<T> pageList = fromIndex >= toIndex ? Collections.emptyList() : source.subList(fromIndex, toIndex);
        return new PageResult<>(pageList, source.size(), safePage, safePageSize);
    }
}
