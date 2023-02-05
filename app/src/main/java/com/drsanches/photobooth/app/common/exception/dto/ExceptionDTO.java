package com.drsanches.photobooth.app.common.exception.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

@Data
@AllArgsConstructor
public class ExceptionDTO {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String uuid;

    private final String message;

    @SneakyThrows
    @Override
    public String toString() {
        return MAPPER.writeValueAsString(this);
    }
}
