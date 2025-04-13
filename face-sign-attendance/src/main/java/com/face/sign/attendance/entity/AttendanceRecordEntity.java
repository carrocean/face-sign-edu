package com.face.sign.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 考勤记录实体类
 */
@Data
@TableName("attendance_record")
public class AttendanceRecordEntity extends BaseEntity {
    
    /**
     * 课程ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseId;
    
    /**
     * 学生ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long studentId;

    /**
     * 课程安排ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long scheduleId;
    
    /**
     * 考勤时间
     */
    private Date attendanceDate;
    
    /**
     * 考勤状态
     */
    private String status;
}