package ru.drsanches.photobooth.common.utils;

import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

public class GregorianCalendarConvertor {

    public static final String PATTERN = "dd-MM-yyyy HH:mm:ss.SSS z";

    public static String convert(GregorianCalendar gregorianCalendar) {
        return gregorianCalendar
                .toZonedDateTime()
                .format(DateTimeFormatter.ofPattern(PATTERN));
    }
}