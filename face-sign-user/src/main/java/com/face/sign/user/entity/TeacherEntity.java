package com.face.sign.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("teacher")
public class TeacherEntity extends BaseEntity {
    private Long userId; // 关联用户表的外键
    private String name; // 教师姓名
    private String teacherNumber; // 工号，唯一
    private String phone; // 联系电话
    private String email; // 电子邮箱
}