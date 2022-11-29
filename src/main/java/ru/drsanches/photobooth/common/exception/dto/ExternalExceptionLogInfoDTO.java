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
