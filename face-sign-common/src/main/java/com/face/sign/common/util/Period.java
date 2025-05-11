package com.face.sign.common.util;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Data
public class Period {
    private final Integer periodName; // 节次名称，如 "第1节"
    private final LocalTime startTime; // 节次开始时间
    private final LocalTime endTime; // 节次结束时间

    public Period(Integer periodName, LocalTime startTime, LocalTime endTime) {
        this.periodName = periodName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 静态方法初始化节次数据
    public static List<Period> getPeriods() {
        return List.of(
                new Period(1, LocalTime.of(8, 0), LocalTime.of(9, 40)),
                new Period(2, LocalTime.of(10, 0), LocalTime.of(11, 40)),
                new Period(3, LocalTime.of(14, 30), LocalTime.of(16, 5)),
                new Period(4, LocalTime.of(16, 25), LocalTime.of(18, 0))
        );
    }

    // 获取当前节次，若当前时间不在上课时间内，则返回上一个上课时间段
    public static Period getCurrentPeriod() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime nowTime = now.toLocalTime();

        // 首先尝试找到当前正在上课的节次
        Period currentPeriod = Period.getPeriods().stream()
                .filter(period -> nowTime.isAfter(period.getStartTime()) && nowTime.isBefore(period.getEndTime()))
                .findFirst()
                .orElse(null);

        // 如果当前时间不在任何上课时间内，则返回上一个上课时间段
        if (currentPeriod == null) {
            return Period.getPeriods().stream()
                    .filter(period -> nowTime.isAfter(period.getEndTime().plusMinutes(5))) // 当前时间大于上一节课结束时间+5分钟
                    .max(Comparator.comparing(Period::getEndTime))
                    .orElse(null);
        }

        return currentPeriod;
    }

    // 判断当前时间的考勤状态
    public static String getAttendanceStatus() {
        LocalTime nowTime = LocalTime.now();
        Period currentPeriod = getCurrentPeriod();
        if (currentPeriod != null) {
            // 上课前20分钟或上课时间开始后10分钟内为出勤
            if (nowTime.isAfter(currentPeriod.getStartTime().minusMinutes(20)) && nowTime.isBefore(currentPeriod.getStartTime().plusMinutes(10))) {
                return "PRESENT"; // 出勤
            } else if (nowTime.isAfter(currentPeriod.getStartTime().plusMinutes(10)) && nowTime.isBefore(currentPeriod.getEndTime())) {
                return "LATE"; // 迟到
            }
        }
        return "ABSENT"; // 缺勤
    }
}