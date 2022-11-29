package ru.drsanches.photobooth.common.exception.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

import java.util.GregorianCalendar;

@AllArgsConstructor
@Getter
@Setter
public class ExceptionLogInfoDTO {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String uuid;

    private final String timestamp;

    private final String logMessage;

    private final String exception;

    public ExceptionLogInfoDTO(String uuid, GregorianCalendar timestamp, String logMessage, Throwable e) {
        this.uuid = uuid;
        this.timestamp = GregorianCalendarConvertor.convert(timestamp);
        this.logMessage = logMessage;
        this.exception = ExceptionUtils.getStackTrace(e);
    }

    @SneakyThrows
    @Override
    public String toString() {
        return MAPPER.writeValueAsString(this);
    }
}
