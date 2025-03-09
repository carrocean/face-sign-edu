package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long userId; // 用户唯一标识
    private String username; // 用户名
    private String password; // 密码（加密存储）
    private String role; // 用户角色（ADMIN、TEACHER、STUDENT）
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}