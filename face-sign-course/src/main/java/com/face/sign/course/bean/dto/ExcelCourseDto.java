package com.face.sign.course.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ExcelCourseDto {
    @ExcelProperty("课程名称")
    private String courseName;

    @ExcelProperty("教师姓名")
    private String teacherName;

    @ExcelProperty("教师工号")
    private String teacherNumber;

    @ExcelProperty("班级名称")
    private String className;

    @ExcelProperty("学期开始时间")
    private Date semester;

    @ExcelProperty("开始周数")
    private Integer startWeek;

    @ExcelProperty("结束周数")
    private Integer endWeek;

    @ExcelProperty("星期")
    private Integer weekDay;

    @ExcelProperty("节次")
    private Integer period;

    @ExcelProperty("上课地点")
    private String classroom;

}

