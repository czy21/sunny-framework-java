package com.sunny.framework.core.util;

import org.apache.commons.lang3.time.DateUtils;

import java.time.*;


public class DateUtil {

    public static Long toTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime toLocalDateTime(Long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
    }

    public static Long toTimeStamp(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate toLocalDate(Long timeStamp) {
        return LocalDate.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
    }
}
