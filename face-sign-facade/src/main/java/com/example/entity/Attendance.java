package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class Attendance {
    private Long attendanceId; // 考勤记录唯一标识
    private Long studentId; // 学生ID
    private Long courseId; // 课程ID
    private LocalDate attendanceDate; // 考勤日期
    private String status; // 考勤状态（Present、Absent、Late）
    private LocalDateTime createdAt; // 创建时间
}