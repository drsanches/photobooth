package com.drsanches.photobooth.app.app.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class PagingService {

    @Value("${application.pagination.default-page-size}")
    private Integer defaultPageSize;

    @Value("${application.pagination.max-page-size}")
    private Integer maxPageSize;

    public Pageable pageable(@Nullable Integer page, @Nullable Integer size) {
        return PageRequest.of(page(page), size(size));
    }

    private int size(@Nullable Integer size) {
        var result = size == null || size < 1 ? defaultPageSize : size;
        return result > maxPageSize ? maxPageSize : result;
    }

    private int page(@Nullable Integer page) {
        return page == null || page < 0 ? 0 : page;
    }
}
