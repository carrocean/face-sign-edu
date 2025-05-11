package com.face.sign.course.bean.dto;

import lombok.Data;
import java.util.Date;

/**
 * 上个时间段的课程信息
 */
@Data
public class CourseScheduleDto {
    // 课程ID
    private Long courseId;

    private Long scheduleId;

    // 课程名称
    private String courseName;

    // 教师ID
    private Long teacherId;

    // 班级ID
    private Long classId;

    // 学期开始时间
    private Date semester;

    // 开始周数
    private Integer startWeek;

    // 结束周数
    private Integer endWeek;

    // 所在周数
    private Integer theWeek;

    // 星期
    private Integer weekDay;

    // 节次
    private Integer period;

    // 上课地点
    private String classroom;
}