package com.face.sign.user.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TeacherEntity {
    private Integer teacherId;
    private Integer userId;
    private String name;
    private String teacherNumber;
    private String phone;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
}