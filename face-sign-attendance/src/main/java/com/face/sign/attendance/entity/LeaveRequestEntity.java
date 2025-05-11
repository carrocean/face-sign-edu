package com.face.sign.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.face.sign.common.base.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
@TableName("leave_request")
public class LeaveRequestEntity extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long studentId; // 关联学生表的外键
    private Date startTime; // 请假开始时间
    private Date endTime; // 请假结束时间
    private String reason; // 请假原因
    private String status; // 请假状态（已申请、已批准、已拒绝）
    @JsonSerialize(using = ToStringSerializer.class)
    private Long approverId; // 审批人id
    private Date approveTime; // 审批时间
    private String approveRemark; // 审批备注

}