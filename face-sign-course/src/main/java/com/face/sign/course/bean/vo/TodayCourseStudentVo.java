package com.face.sign.course.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 学生今日课程信息
 */
@Data
public class TodayCourseStudentVo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long scheduleId;

    // 课程名称
    private String courseName;

    // 授课教师
    private String teacherName;

    // 节次
    private Integer period;

    // 上课地点
    private String classroom;

    // 状态
    private String status;
}