package com.face.sign.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseEntity {
    private Long courseId; // 课程唯一标识
    private String courseName; // 课程名称
    private Long teacherId; // 授课教师ID
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}