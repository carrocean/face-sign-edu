package com.face.sign.attendance.bean.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StudentAttendanceRecordVo {
    private String courseName;

    private String teacherName;

    private Integer theWeek;

    private Integer weekDay;

    private Integer period;

    private Date attendanceDate;

    private String status;

    private String studentName;
}