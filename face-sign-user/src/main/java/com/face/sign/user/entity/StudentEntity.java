package com.face.sign.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;


@Data
@TableName("student")
public class StudentEntity extends BaseEntity {
    private Long userId; // 关联用户表的外键
    private String name; // 学生姓名
    private String studentNumber; // 学号，唯一
    private Long classId; // 班级ID，关联班级表
    private String faceImage; // 人脸图像存储路径
    private String phone; // 联系电话
    private String email; // 电子邮箱
}