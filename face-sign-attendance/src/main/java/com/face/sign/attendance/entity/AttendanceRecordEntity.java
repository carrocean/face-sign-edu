package com.face.sign.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考勤记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance_record")
public class AttendanceRecordEntity extends BaseEntity {
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 考勤日期
     */
    private String date;
    
    /**
     * 考勤时间
     */
    private String time;
    
    /**
     * 考勤状态
     */
    private String status;
    
    /**
     * 签到时间
     */
    private LocalDateTime checkInTime;
    
    /**
     * 签到图片
     */
    private String imageUrl;
}