package com.face.sign.course.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class CourseScheduleVo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId; // 关联课程表的外键
    private int weekDay; // 星期
    private int period; // 节次
    private int startWeek;  // 开始周数
    private int endWeek; // 结束周数
    private String classroom; // 上课地点
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; // 唯一ID号
    private Date addTime; // 添加时间
    private Date updateTime; // 更新时间
}
