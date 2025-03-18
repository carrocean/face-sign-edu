package com.face.sign.user.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVo {
    private Integer userId;
    private String username;
    private String password;
    private String role;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
    private String token;
}
