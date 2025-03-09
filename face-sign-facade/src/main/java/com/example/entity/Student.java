package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Student {
    private Long studentId; // 学生唯一标识
    private String name; // 学生姓名
    private Long classId; // 所属班级ID
    private String studentNo; // 学号
    private String faceImagePath; // 存储在 MinIO 中的人脸图像路径
    private LocalDateTime createdAt; // 注册时间
    private LocalDateTime updatedAt; // 更新时间

}