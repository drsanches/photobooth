package ru.drsanches.photobooth.exception.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

@Getter
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
