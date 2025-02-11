package com.azki.reservation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static long dateTimeToSeconds(String dateTimeStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        return sdf.parse(dateTimeStr).getTime() / 1000;
    }
}
