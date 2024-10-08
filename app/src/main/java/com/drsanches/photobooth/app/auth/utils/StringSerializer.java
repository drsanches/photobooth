package com.drsanches.photobooth.app.auth.utils;

import com.drsanches.photobooth.app.common.exception.ServerError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class StringSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T deserialize(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new ServerError("Deserialization error", e);
        }
    }

    public String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ServerError("Serialization error", e);
        }
    }
}
