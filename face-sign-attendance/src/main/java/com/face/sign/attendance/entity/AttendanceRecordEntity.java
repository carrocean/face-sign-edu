package com.face.sign.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("attendance_record")
public class AttendanceRecordEntity extends BaseEntity {
    private Long studentId; // 关联学生表的外键
    private Long courseId; // 关联课程表的外键
    private Long scheduleId; // 关联课程安排表的外键
    private Date attendanceDate; // 考勤日期和时间
    private String status; // 考勤状态（出勤、迟到、缺勤、请假）
}