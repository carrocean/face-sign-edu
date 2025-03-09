package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Class {
    private Long classId; // 班级唯一标识
    private String className; // 班级名称
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}