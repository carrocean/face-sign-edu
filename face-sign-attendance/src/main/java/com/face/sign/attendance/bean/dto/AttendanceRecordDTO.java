package com.face.sign.attendance.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class AttendanceRecordDTO {
    @ExcelProperty("课程名称")
    private String courseName;

    @ExcelProperty("教师")
    private String teacherName;

    @ExcelProperty("学号")
    private String studentNumber;

    @ExcelProperty("学生")
    private String studentName;

    @ExcelProperty("班级")
    private String className;

    @ExcelProperty("考勤日期")
    private Date attendanceDate;

    @ExcelProperty("考勤状态")
    private String status;

    @ExcelProperty("上课地点")
    private String classroom;


    private static final Map<String, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put("PRESENT", "出勤");
        STATUS_MAP.put("LATE", "迟到");
        STATUS_MAP.put("ABSENT", "缺勤");
        STATUS_MAP.put("LEAVE", "请假");
    }

    // 根据状态代码获取中文文本
    public static String getStatusText(String status) {
        return STATUS_MAP.getOrDefault(status, "未知状态");
    }

    // 设置状态时进行转换
    public void setStatus(String status) {
        this.status = getStatusText(status);
    }
}