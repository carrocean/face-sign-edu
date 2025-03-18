package com.face.sign.user.entity;

import com.face.sign.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;


@Data
public class UserEntity extends BaseEntity {
    private Integer userId;
    private String username;
    private String password;
    private String role;
//    private Date createdAt;
//    private Date updatedAt;
//    private Boolean isDeleted;
    private Date lastLoginTime;    // 最后登录时间
    private String lastLoginIp;      // 最后登录IP
    private Integer loginCount;      // 登录次数
    private Integer status;          // 账号状态（启用/禁用）
}