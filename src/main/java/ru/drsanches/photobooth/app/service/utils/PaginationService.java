package ru.drsanches.photobooth.app.service.utils;

import org.springframework.stereotype.Service;
import java.util.stream.Stream;

@Service
public class PaginationService<T> {

    //TODO: use?
    public Stream<T> pagination(Stream<T> stream, Integer from, Integer to) {
        Integer _from = from != null && from < 0 ? null : from;
        Integer _to = to != null && to < 0 ? null : to;
        if (_from == null && _to == null) {
            return stream;
        }
        if (_from == null) {
            return stream.limit(_to);
        }
        if (_to == null) {
            return stream.skip(_from);
        }
        if (_from > _to) {
            return Stream.empty();
        }
        return stream.limit(_to).skip(_from);
    }
}
