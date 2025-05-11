package com.face.sign.common.util;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class WeekCalculator {

    /**
     * 计算当前日期是从学期开始的第几周
     * @param semesterStart 学期开始时间
     * @return 当前周数
     */
    public static Integer getCurrentWeek(Date semesterStart) {
        // 确保学期开始时间是周一
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(semesterStart);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 调整为周一
        Date adjustedSemesterStart = calendar.getTime();

        // 获取当前日期
        Date today = new Date();

        // 计算从学期开始到当前日期的天数差
        long daysBetween = (today.getTime() - adjustedSemesterStart.getTime()) / (24 * 60 * 60 * 1000);

        // 计算周数（天数除以7，向上取整）
        int currentWeek = (int) Math.ceil((double) daysBetween / 7);

        // 如果当前日期在学期开始之前，返回1
        return Math.max(currentWeek, 0);

    }

    /**
     * 获取当前星期
     * @return 当前星期
     */
    public static Integer getCurrentDayOfWeek() {
        return LocalDate.now().getDayOfWeek().getValue();
    }
}