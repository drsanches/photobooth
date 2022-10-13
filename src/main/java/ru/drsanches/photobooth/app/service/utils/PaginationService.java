package ru.drsanches.photobooth.app.service.utils;

import org.springframework.beans.factory.annotation.Value;
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
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 0 ? defaultPageSize : size;
        size = size > maxPageSize ? maxPageSize : size;
        return stream.skip((page - 1) * size).limit(size);
    }
}
