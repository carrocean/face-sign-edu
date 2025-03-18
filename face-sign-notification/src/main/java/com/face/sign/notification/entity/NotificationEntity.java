package com.face.sign.notification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notification")
public class NotificationEntity extends BaseEntity {
    private Long senderId; // 发送者ID，关联用户表
    private Long receiverId; // 接收者ID，关联用户表
    private String title; // 通知标题
    private String content; // 通知内容
    private Date sendTime; // 发送时间
    private boolean isRead; // 是否已读
}