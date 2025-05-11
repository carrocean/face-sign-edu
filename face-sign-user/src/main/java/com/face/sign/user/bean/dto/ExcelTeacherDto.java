package com.face.sign.user.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelTeacherDto {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("工号")
    private String teacherNumber;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("电子邮箱")
    private String email;

}
   