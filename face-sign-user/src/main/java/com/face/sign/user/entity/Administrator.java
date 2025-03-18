package com.face.sign.user.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Administrator {
    private Integer adminId;
    private Integer userId;
    private String name;
    private String phone;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
}