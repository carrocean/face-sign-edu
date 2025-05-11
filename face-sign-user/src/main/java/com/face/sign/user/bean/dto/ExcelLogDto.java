package com.face.sign.user.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ExcelLogDto {
    @ExcelProperty("用户ID")
    private Long userId;

    @ExcelProperty("用户名")
    private String name;

    @ExcelProperty("操作描述")
    private String operation;

    @ExcelProperty("操作时间")
    private Date operationTime;

    @ExcelProperty("操作IP地址")
    private String ipAddress;

}