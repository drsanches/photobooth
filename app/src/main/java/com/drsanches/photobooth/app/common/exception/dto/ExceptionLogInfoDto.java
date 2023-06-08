package com.drsanches.photobooth.app.common.exception.dto;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.GregorianCalendar;

@Data
@AllArgsConstructor
public class ExceptionLogInfoDto {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String uuid;

    private final String timestamp;

    private final String logMessage;

    private final String exception;

    public ExceptionLogInfoDto(String uuid, GregorianCalendar timestamp, String logMessage, Throwable e) {
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
