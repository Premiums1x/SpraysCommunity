package com.lancer.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> records;
    private long total;
    private long page;
    private long size;
    private long pages;

    public static <T> PageResponse<T> from(IPage<T> source) {
        return new PageResponse<>(
                source.getRecords(),
                source.getTotal(),
                source.getCurrent(),
                source.getSize(),
                source.getPages()
        );
    }
}
