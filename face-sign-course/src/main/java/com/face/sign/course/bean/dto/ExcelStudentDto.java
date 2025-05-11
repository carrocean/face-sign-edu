package com.face.sign.course.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelStudentDto {
    @ExcelProperty("班级名称")
    private String className;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("学号")
    private String studentNumber;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("电子邮箱")
    private String email;
}