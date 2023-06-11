package com.drsanches.photobooth.app.app.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class PaginationServiceTest {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private PaginationService<Integer> paginationService;

    @BeforeEach
    void setUp() {
        paginationService = new PaginationService<>();
        ReflectionTestUtils.setField(paginationService, "defaultPageSize", DEFAULT_PAGE_SIZE);
        ReflectionTestUtils.setField(paginationService, "maxPageSize", MAX_PAGE_SIZE);
    }

    @ParameterizedTest
    @MethodSource("paginationData")
    void pagination(Integer page, Integer size, IntStream expected) {
        Stream<Integer> input = IntStream.range(0, 200).boxed();
        Stream<Integer> result = paginationService.pagination(input, page, size);
        Assertions.assertArrayEquals(expected.boxed().toArray(), result.toArray());
    }

    static Arguments[] paginationData() {
        return new Arguments[] {
                Arguments.of(3, 2, IntStream.of(6, 7)),
                Arguments.of(200, 1, IntStream.of()),
                Arguments.of(0, 200, IntStream.range(0, MAX_PAGE_SIZE)),
                Arguments.of(null, 2, IntStream.range(0, 2)),
                Arguments.of(-1, 2, IntStream.range(0, 2)),
                Arguments.of(3, null, IntStream.range(3 * DEFAULT_PAGE_SIZE, 4 * DEFAULT_PAGE_SIZE)),
                Arguments.of(3, 0, IntStream.range(3 * DEFAULT_PAGE_SIZE, 4 * DEFAULT_PAGE_SIZE)),
                Arguments.of(null, null, IntStream.range(0, DEFAULT_PAGE_SIZE)),
                Arguments.of(-1, 0, IntStream.range(0, DEFAULT_PAGE_SIZE))
        };
    }

    @ParameterizedTest
    @MethodSource("pageableData")
    void pageable(Integer page, Integer size, Pageable expected) {
        Pageable result = paginationService.pageable(page, size);
        Assertions.assertEquals(expected, result);
    }

    static Arguments[] pageableData() {
        return new Arguments[] {
                Arguments.of(3, 2, PageRequest.of(3, 2)),
                Arguments.of(3, 200, PageRequest.of(3, MAX_PAGE_SIZE)),
                Arguments.of(null, null, PageRequest.of(0, DEFAULT_PAGE_SIZE)),
                Arguments.of(-1, 0, PageRequest.of(0, DEFAULT_PAGE_SIZE))
        };
    }
}
