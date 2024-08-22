package com.drsanches.photobooth.app.common.exception.dto;

import com.drsanches.photobooth.app.common.exception.BaseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ExceptionDto { //TODO: Rename to ErrorDto

    record Detail(String field, String message) {}

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private final String uuid;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Detail> details;

    public ExceptionDto(BaseException e) {
        this.uuid = e.getUuid();
        this.code = e.getMessage();
    }

    public void addDetail(String field, String message) {
        if (details == null) {
            details = new LinkedList<>();
        }
        details.add(new Detail(field, message));
    }

    @SneakyThrows
    @Override
    public String toString() {
        if (details != null && details.isEmpty()) {
            details = null;
        }
        return MAPPER.writeValueAsString(this);
    }
}
