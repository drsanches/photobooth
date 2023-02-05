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
public class ExternalExceptionLogInfoDTO {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String timestamp = GregorianCalendarConvertor.convert(new GregorianCalendar());

    private final String exception;

    public ExternalExceptionLogInfoDTO(Throwable e) {
        this.exception = ExceptionUtils.getStackTrace(e);
    }

    @SneakyThrows
    @Override
    public String toString() {
        return MAPPER.writeValueAsString(this);
    }
}
