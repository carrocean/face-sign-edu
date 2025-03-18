package com.face.sign.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("class")
public class ClassEntity extends BaseEntity {
    private String className; // 班级名称
}