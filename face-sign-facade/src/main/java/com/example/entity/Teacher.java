package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Teacher {
    private Long teacherId; // 教师唯一标识
    private String name; // 教师姓名
    private String teacherNo; // 教师工号
    private String faceImagePath; // 存储在 MinIO 中的人脸图像路径
    private LocalDateTime createdAt; // 注册时间
    private LocalDateTime updatedAt; // 更新时间
}