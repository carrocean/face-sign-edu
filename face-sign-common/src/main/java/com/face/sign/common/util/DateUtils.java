package com.face.sign.common.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {
    public static LocalTime parseTime(String time) {
        return LocalTime.parse(time);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}