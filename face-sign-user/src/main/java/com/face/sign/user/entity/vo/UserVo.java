package com.face.sign.user.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVo {
    private Long id; // 唯一ID号
    private Date addTime; // 添加时间
    private Date deleteTime; // 删除时间
    private Date updateTime; // 更新时间
    private int hasDelete; // 是否删除(1删除|0正常|null正常)
    private String userName;
    private String password;
    private String role;
    private Date lastLoginTime;    // 最后登录时间
    private String lastLoginIp;      // 最后登录IP
    private Integer loginCount;      // 登录次数
    private Integer status;          // 账号状态（启用/禁用）
    private String token;
}
