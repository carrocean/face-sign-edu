package com.face.sign.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("system_log")
public class SystemLogEntity extends BaseEntity {
    private Long userId; // 关联用户表的外键
    private String operation; // 操作描述
    private Date operationTime; // 操作时间
    private String ipAddress; // 操作IP地址
}