package com.face.sign.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;


@Data
@TableName("user")
public class UserEntity extends BaseEntity {
    private String userName; // 用户名，唯一
    private String password; // 密码，加密存储
    private String role; // 用户角色（学生、教师、管理员）
    private Date lastLoginTime; // 最后登录时间
    private String lastLoginIp; // 最后登录IP
    private int loginCount; // 登录次数
    private int status; // 账号状态（1启用/0禁用）
}