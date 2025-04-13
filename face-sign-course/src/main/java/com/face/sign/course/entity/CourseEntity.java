package com.face.sign.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("course")
public class CourseEntity extends BaseEntity {
    private String courseName; // 课程名称
    @JsonSerialize(using = ToStringSerializer.class)
    private Long teacherId; // 关联教师表的外键
    @JsonSerialize(using = ToStringSerializer.class)
    private Long classId; // 关联班级表的外键
    private String semester; // 开课学期
    private int startWeek; // 开始周数
    private int endWeek; // 结束周数
}