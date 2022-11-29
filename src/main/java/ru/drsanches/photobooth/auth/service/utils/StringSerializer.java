package ru.drsanches.photobooth.auth.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.common.exception.server.ServerError;

@Component
public class StringSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T deserialize(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new ServerError("Registration confirmation deserialization error", e);
        }
    }

    public String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ServerError("Registration serialization error", e);
        }
    }
}
