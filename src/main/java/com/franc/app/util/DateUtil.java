package com.franc.app.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    /**
     * Now-LocalDateTime => 문자열 (yyyyMMddHHmmss)
     * @return
     */
    public static String nowDateToString() {
        return localDateTimeToString(LocalDateTime.now());
    }

    /**
     * LocalDateTime => 문자열 (yyyyMMddHHmmss)
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {
        if(localDateTime == null)
            localDateTime = LocalDateTime.now();

        return localDateTime.format((DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    /**
     * 현재날짜와 비교 (5일전 -5, 5일후 5)
     * @param targetDateTime
     * @return
     */
    public static int compareNow(LocalDateTime targetDateTime) {
        if(targetDateTime == null)
            throw new NullPointerException();

        return compareNow(targetDateTime.toLocalDate());
    }

    /**
     * 현재날짜와 비교 (5일전 -5, 5일후 5)
     * @param targetDate
     * @return
     */
    public static int compareNow(LocalDate targetDate) {
        if(targetDate == null)
            throw new NullPointerException();

        return targetDate.compareTo(LocalDate.now());
    }

    public static String getAddMonth(int addMonths) {
        LocalDate localDate = LocalDate.now().plusMonths(addMonths);
        return localDate.format((DateTimeFormatter.ofPattern("yyyyMMdd")));
    }


}
