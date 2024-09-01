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

@ExtendWith(MockitoExtension.class)
class PagingServiceTest {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private PagingService pagingService;

    @BeforeEach
    void setUp() {
        pagingService = new PagingService();
        ReflectionTestUtils.setField(pagingService, "defaultPageSize", DEFAULT_PAGE_SIZE);
        ReflectionTestUtils.setField(pagingService, "maxPageSize", MAX_PAGE_SIZE);
    }

    @ParameterizedTest
    @MethodSource("pageableData")
    void pageable(Integer page, Integer size, Pageable expected) {
        var result = pagingService.pageable(page, size);
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
