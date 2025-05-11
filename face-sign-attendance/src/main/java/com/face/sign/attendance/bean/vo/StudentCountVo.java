package com.face.sign.attendance.bean.vo;

import lombok.Data;

/**
 * 学生端统计信息
 */
@Data
public class StudentCountVo {
    // 今日课程数量
    private Integer todayCourses;

    // 本周考勤率（百分比）
    private Double attendanceRate;

    // 待签到课程数量
    private Integer pendingSignIn;

    // 总课程数量
    private Integer totalCourses;
}