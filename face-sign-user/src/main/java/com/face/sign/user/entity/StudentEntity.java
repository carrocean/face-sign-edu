package com.face.sign.user.entity;

import lombok.Data;

import java.util.Date;


@Data
public class StudentEntity {
    private Integer studentId;
    private Integer userId;
    private String name;
    private String studentNumber;
    private Integer classId;
    private String faceImage;
    private String phone;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;

}