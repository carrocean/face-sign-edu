package com.face.sign.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
@TableName("course_schedule")
public class CourseScheduleEntity extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId; // 关联课程表的外键
    private int weekDay; // 周几
    private Date startTime; // 开始时间
    private Date endTime; // 结束时间
    private String classroom; // 上课地点
}