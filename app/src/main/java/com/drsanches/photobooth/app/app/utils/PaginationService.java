package com.drsanches.photobooth.app.app.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.stream.Stream;

@Service
public class PaginationService<T> {

    @Value("${application.pagination.default-page-size}")
    private Integer defaultPageSize;

    @Value("${application.pagination.max-page-size}")
    private Integer maxPageSize;

    //TODO: Use pagination on database layer
    public Stream<T> pagination(Stream<T> stream, Integer page, Integer size) {
        page = page(page);
        size = size(size);
        return stream.skip((long) page * size).limit(size);
    }

    public Pageable pageable(Integer page, Integer size) {
        return PageRequest.of(page(page), size(size));
    }

    private int size(Integer size) {
        int result = size == null || size < 1 ? defaultPageSize : size;
        return result > maxPageSize ? maxPageSize : result;
    }

    private int page(Integer page) {
        return page == null || page < 0 ? 0 : page;
    }
}
