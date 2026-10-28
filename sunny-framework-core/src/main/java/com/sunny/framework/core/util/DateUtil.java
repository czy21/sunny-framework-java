package com.sunny.framework.core.util;

import java.time.*;
import java.util.Optional;


public class DateUtil {

    public static Long toTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime toLocalDateTimeBySourceTimeZone(Long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
    }

    public static Long toTimeStamp(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDate toLocalDate(Long timeStamp) {
        return LocalDate.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
    }

    public static OffsetDateTime toOffsetDateTime(LocalDateTime value, String sourceTimeZone, String targetTimeZone) {
        return Optional.ofNullable(value).map(t -> t.atZone(ZoneId.of(sourceTimeZone)).withZoneSameInstant(ZoneId.of(targetTimeZone)).toOffsetDateTime()).orElse(null);
    }

    public static LocalDateTime toLocalDateTime(LocalDateTime value, String sourceTimeZone, String targetTimeZone) {
        return Optional.ofNullable(value).map(t -> t.atZone(ZoneId.of(sourceTimeZone)).withZoneSameInstant(ZoneId.of(targetTimeZone)).toLocalDateTime()).orElse(null);
    }
}
